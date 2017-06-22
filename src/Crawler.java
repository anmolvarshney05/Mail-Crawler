/**
 * Created by anmolvarshney on 20/06/17.
 */

import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Vector;
//import java.math.BigInteger;
//import java.security.MessageDigest;

public class Crawler {
    private final String DISALLOW = "DISALLOW"; // Disallowed Pattern in Robots.txt
    private final char ESCAPES[] = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '\\', '.', '<', '>' }; // WildCard Escape Characters
    private Vector<String> blockedIP; // Blocked IP's by Robots.txt
    private Hashtable<URL, Integer> seenURL; // URL's already visited
    private Vector<URL> newURLs; // New URL's
    private String saveRegex = ""; // Regex for the URL's whose content is desired
    private URL url; // Starting URL
    //private Hashtable<String, Integer> seenURLHash; // For Storing Hash of Web Pages

//    // Computes Hash of a Webpage
//    private String MD5Hash(byte[] dataBytes) throws NoSuchAlgorithmException { // Computes Hash for Web Pages
//        if(dataBytes == null){
//            return "";
//        }
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        md.update(dataBytes);
//        byte[] digest = md.digest();
//        BigInteger bi = new BigInteger(digest);
//        String ret = bi.toString(16);
//        if(ret.length() %2 != 0)
//            ret = "0" + ret;
//        return ret;
//    }

    // Initialzing all the Data Structures and Variables
    private void initialize(String URL){
        blockedIP = new Vector<String>();
        seenURL = new Hashtable<URL, Integer>();
        newURLs = new Vector<URL>();
        //seenURLHash = new Hashtable<String, Integer>();
        try {
            url = new URL(URL);
            saveRegex = url.toString() + "[0-9]{6}.mbox/<.*>$";
            seenURL.put(url, new Integer(1));
            newURLs.add(url);
//            try {
//                seenURLHash.put(MD5Hash((getPage(url).toLowerCase()).getBytes("UTF-8")), new Integer(1));
//            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
//                return;
//            }
        } catch (MalformedURLException e) {
            System.out.println("Invalid starting URL");
            return;
        }
    }

    // Convert WildCards to Regex Expressions
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

    // Get Robots.txt of a URL
    private String getRobot() {
        String baseURL = url.getProtocol() + "://" + url.getHost();
        try (InputStream in = new URL(baseURL + "/robots.txt").openStream()) {
            return IOUtils.toString(in, "UTF-8");
        } catch (Exception e) {
            return "No Robots.txt File";
        }
    }

    // Store all the Disallowed URL's by Robots.txt
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

    // Check to see if URL is not disallowed in Robots.txt
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

    // Get URL Page Contents
    private String getPage(URL url){
        try (InputStream in = new URL(url.toString()).openStream()) {
            return IOUtils.toString(in, "UTF-8");
        } catch (Exception e) {
            System.out.println("Couldn't open the URL");
            return "";
        }
    }

    // Add New URL to the Queue
    // Check to see if the URL is already Visited
    // Display the desired URL's
    // Option to avoid visiting the pages with same hash
    private void addNewURL(URL oldURL, String newURL) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            URI uri = oldURL.toURI();
            URI uriRes = uri.resolve(newURL.replaceAll("\"", ""));
            URL url = uriRes.toURL();
            if(robotSafe(url.toString())){
                if(!(seenURL.containsKey(url))) {
                    //if(!(seenURLHash.containsKey(MD5Hash((getPage(url).toLowerCase()).getBytes("UTF-8"))))) {
                    if (url.toString().contains(this.url.toString())) {
                        if(URLDecoder.decode(url.toString(), "UTF-8").matches(saveRegex)){
                            System.out.println("Matched " + url.toString());
                        }
                        else {
                            newURLs.add(url);
                        }
                    }
                    //seenURLHash.put(MD5Hash((getPage(url).toLowerCase()).getBytes("UTF-8"))), new Integer(1));
                    //}
                    seenURL.put(url, new Integer(1));
                }
            }
            else
                System.out.println("Blocked by Robot");
        } catch (URISyntaxException | MalformedURLException e) {
            return;
        }
    }

    // Extract all URL's from the page
    public void processPage(URL url) throws UnsupportedEncodingException, NoSuchAlgorithmException {
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

    // Main Function to run everything
    public void run(String URL) throws UnsupportedEncodingException, NoSuchAlgorithmException {
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

    public static void main(String args[]) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Crawler crawler = new Crawler();
        crawler.run("http://mail-archives.apache.org/mod_mbox/maven-users/");
    }
}
