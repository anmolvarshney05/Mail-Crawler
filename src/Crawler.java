/**
 * Created by anmolvarshney on 20/06/17.
 */

import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;

public class Crawler {
    private final String DISALLOW = "DISALLOW";
    private final char ESCAPES[] = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '\\', '.', '<', '>' };
    private Vector<String> blockedIP;
    private Hashtable<URL, Integer> seenURL;
    private Vector<URL> newURLs;
    private URL url;

    private void initialize(String URL){
        blockedIP = new Vector<String>();
        seenURL = new Hashtable<URL, Integer>();
        newURLs = new Vector<URL>();
        try {
            url = new URL(URL);
            seenURL.put(url, new Integer(1));
            newURLs.add(url);
        } catch (MalformedURLException e) {
            System.out.println("Invalid starting URL");
            return;
        }
    }

    private String wildcardToReg(String pattern) {
        String result = "^";
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            boolean escaped = false;
            for (int j = 0; j < ESCAPES.length; j++) {
                if (ch == ESCAPES[j]) {
                    result += "\\" + ch;
                    escaped = true;
                    break;
                }
            }
            if (!escaped) {
                if (ch == '*') {
                    result += ".*";
                } else if (ch == '?') {
                    result += ".";
                } else {
                    result += ch;
                }
            }
        }
        result += "$";
        return result;
    }

    private String getRobot() {
        String baseURL = url.getProtocol() + "://" + url.getHost();
        try (InputStream in = new URL(baseURL + "/robots.txt").openStream()) {
                return IOUtils.toString(in, "UTF-8");
        } catch (Exception e) {
                return "No Robots.txt File";
        }
    }

    private void fillRobots(){
        String ret = getRobot();
        if(ret.equals("No Robots.txt File")){
            System.out.println("This site doesn't have a Robots.txt File");
        }
        else{
            String[] str = ret.split("\n");
            for(int i = 0; i < str.length; i++){
                if(str[i].toUpperCase().contains(DISALLOW)) {
                    blockedIP.add(wildcardToReg(url + str[i].substring(str[i].indexOf(":") + 2)));
                }
            }
        }
    }

    private boolean robotSafe(String link){
        boolean ret = true;
        for(String IP: blockedIP){
            if(link.matches(IP)){
                ret = false;
                break;
            }
        }
        return ret;
    }

    private String getPage(URL url){
        try (InputStream in = new URL(url.toString()).openStream()) {
            return IOUtils.toString(in, "UTF-8");
        } catch (Exception e) {
            System.out.println("Couldn't open the URL");
            return "";
        }
    }

    private void addNewURL(URL oldURL, String newURL){
        try {
            URI uri = oldURL.toURI();
            URI uriRes = uri.resolve(newURL);
            URL url = uriRes.toURL();
            if(robotSafe(url.toString())){
                if(!(seenURL.containsKey(url))) {
                    System.out.println("URL Discovered " + url.toString());
                    seenURL.put(url, new Integer(1));
                    newURLs.add(url);
                }
            }
            else
                System.out.println("Blocked by Robot");
        } catch (URISyntaxException | MalformedURLException e) {
            return;
        }
    }

    public void processPage(URL url) {
        String page = getPage(url);
        String lcPage = page.toLowerCase();
        if(lcPage != "") {
            int index = 0;
            int iEndAngle, ihref, iURL, iCloseQuote, iHatchMark, iEnd;
            while ((index = lcPage.indexOf("<a ", index)) != -1) {
                iEndAngle = lcPage.indexOf(">", index);
                ihref = lcPage.indexOf("href", index);
                if ((ihref != -1) && (ihref < iEndAngle)) {
                    iURL = -1;
                    if(lcPage.indexOf("\"", ihref) != -1)
                        iURL = lcPage.indexOf("\"", ihref) + 1;
                    if((iURL == -1) || (iURL > iEndAngle))
                        if(lcPage.indexOf("\'", ihref) != -1)
                            iURL = lcPage.indexOf("\'", ihref) + 1;
                    if ((iURL != -1) && (iEndAngle != -1) && (iURL < iEndAngle)) {
                        iCloseQuote = lcPage.indexOf("\"", iURL);
                        if((iCloseQuote == -1) || (iCloseQuote > iEndAngle))
                            iCloseQuote = lcPage.indexOf("\'", iURL);
                        iHatchMark = lcPage.indexOf("#", iURL);
                        if ((iCloseQuote != -1) && (iCloseQuote < iEndAngle)) {
                            iEnd = iCloseQuote;
                            if ((iHatchMark != -1) && (iHatchMark < iCloseQuote))
                                iEnd = iHatchMark;
                            String newUrlString = page.substring(iURL, iEnd);
                            addNewURL(url, newUrlString);
                        }
                    }
                }
                index = iEndAngle;
            }
        }
    }

    public void run(String URL){
        initialize(URL);
        fillRobots();
        while(true) {
            URL url = newURLs.elementAt(0);
            newURLs.removeElementAt(0);
            if (robotSafe(url.toString()))
                processPage(url);
            if (newURLs.isEmpty())
                break;
        }
    }

    public static void main(String args[]){
        Crawler crawler = new Crawler();
        crawler.run("http://mail-archives.apache.org/mod_mbox/maven-users/");
    }
}
