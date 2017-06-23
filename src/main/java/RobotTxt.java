import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

public class RobotTxt {
    private final String DISALLOW; // Disallowed Pattern in Robots.txt
    private final char[] ESCAPES; // WildCard Escape Characters
    private Vector<String> blockedIP; // Blocked IP's by Robots.txt
    private URL url;

    // Constructor
    public RobotTxt(URL url){
        this.url = url;
        DISALLOW = "DISALLOW";
        ESCAPES = new char[]{ '$', '^', '[', ']', '(', ')', '{', '|', '+', '\\', '.', '<', '>' };
        blockedIP = new Vector<String>();
        fillRobots();
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
    public boolean robotSafe(String link){
        boolean ret = true;
        for(String IP: blockedIP){
            if(link.matches(IP)){
                ret = false;
                break;
            }
        }
        return ret;
    }
}
