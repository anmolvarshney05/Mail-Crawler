import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Vector;

public class Crawler {
    private Hashtable<URL, Integer> seenURL; // URL's already visited
    private Vector<URL> newURLs; // New URL's
    private String saveRegex; // Regex for the URL's whose content is desired
    private String addRegex; // Regex for URL's to be added to Queue
    private URL url; // Starting URL
    private File baseDir; // Base Directory for saving Mails
    private RobotTxt robot; // For Robots.txt
    private MailInformation mailInformation; // Mail Information
    private UrlOperations urlOperations; // URL Functions
    private File file; // Mail(s) and Attachment(s) File(s)

    // Initialising all the Data Structures and Variables
    public Crawler(String URL, String directory){
        if(URL.equals(""))
            URL = "http://mail-archives.apache.org/mod_mbox/maven-users/";
        if(directory.equals("")){
            directory = FilenameUtils.normalize(System.getProperty("user.home") + File.separator + "Desktop");
        }
        seenURL = new Hashtable<URL, Integer>();
        newURLs = new Vector<URL>();
        mailInformation = new MailInformation();
        urlOperations = new UrlOperations();
        baseDir = new File(directory + File.separator + "Mail Archive");
        try {
            url = new URL(URL);
            saveRegex = "http://mail-archives.apache.org/mod_mbox/maven-users/[0-9]{6}.mbox/<.*>$";
            addRegex = "http://mail-archives.apache.org/mod_mbox/maven-users/[0-9]{6}.mbox/date.*$";
            seenURL.put(url, new Integer(1));
            newURLs.add(url);
            robot = new RobotTxt(url);
        } catch (MalformedURLException e) {
            System.out.println("Invalid starting URL");
            return;
        }
    }

    // Save Mail Content
    private void saveMailFile(String mail, int year, int month, String author, String dateStamp) throws IOException {
        file = new File(baseDir.toString() + File.separator + String.valueOf(year) + File.separator + String.valueOf(month) + File.separator + author + File.separator + author + " " + dateStamp + ".txt");
        FileUtils.writeStringToFile(file, mail, "UTF-8", true);
        System.out.println("Saved Mail in " + file.toString());
    }

    // Save Attachment(s)
    private void saveAttachments(Vector<URL> attachmentsURL, int year, int month, String author, String dateStamp) throws IOException {
        for(int i = 0; i < attachmentsURL.size(); i++) {
            file = new File(baseDir.toString() + File.separator + String.valueOf(year) + File.separator + String.valueOf(month) + File.separator + author + File.separator + author + " " + dateStamp + " Attachments" + File.separator + "Attachment " + String.valueOf(i + 1) + ".txt");
            String attachment = urlOperations.getPage(attachmentsURL.elementAt(i));
            FileUtils.writeStringToFile(file, attachment, "UTF-8", true);
            System.out.println("Saved Attachment " + String.valueOf(i + 1) + " in " + file.toString());
        }
    }

    // Add New URL to the Queue
    // Save Mail(s) and Attachment(s)
    private void addNewURL(URL oldURL, String newURL) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            URL url = urlOperations.resolveURL(oldURL, newURL);
            if(robot.robotSafe(url.toString())){
                if(!(seenURL.containsKey(url))) {
                    if(URLDecoder.decode(url.toString(), "UTF-8").matches(saveRegex)){
                        int year = mailInformation.getYear(url);
                        int month = mailInformation.getMonth(url);
                        String pageContent = urlOperations.getPage(url);
                        String author = mailInformation.getAuthor(pageContent);
                        String dateStamp = mailInformation.getDateStamp(pageContent);
                        String mail = mailInformation.getMessage(pageContent);
                        Vector<URL> attachmentsURL = mailInformation.getAttachmentsURL(url, pageContent);
                        saveMailFile(mail, year, month, author, dateStamp);
                        saveAttachments(attachmentsURL, year, month, author, dateStamp);
                    }
                    else if (url.toString().matches(addRegex)) {
                        newURLs.add(url);
                    }
                    seenURL.put(url, new Integer(1));
                }
            }
            else
                System.out.println("Blocked by Robot");
        } catch (java.io.IOException | URISyntaxException e) {
            return;
        }
    }

    // Extract all URL's from the page
    public void processPage(URL url) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String page = urlOperations.getPage(url);
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
    public void run() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        while(true) {
            URL url = newURLs.elementAt(0);
            newURLs.removeElementAt(0);
            if (robot.robotSafe(url.toString()))
                processPage(url);
            if (newURLs.isEmpty())
                break;
        }
    }

    public static void main(String args[]) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String URL = "";
        String Directory = "";
        if(args.length == 1)
            URL = args[0];
        else if(args.length == 2) {
            URL = args[0];
            Directory = args[1];
        }
        Crawler crawler = new Crawler(URL, Directory);
        crawler.run();
    }
}
