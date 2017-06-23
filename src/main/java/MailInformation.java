/**
 * Created by anmolvarshney on 23/06/17.
 */

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;

public class MailInformation {
    // Get Mail Year
    public int getYear(URL url){
        return Integer.parseInt(url.toString().substring(url.toString().indexOf(".mbox") - 6, url.toString().indexOf(".mbox") - 2));
    }

    //Get Mail Month
    public int getMonth(URL url){
        return Integer.parseInt(url.toString().substring(url.toString().indexOf(".mbox") - 2, url.toString().indexOf(".mbox")));
    }

    // Get Mail Author
    public String getAuthor(String pageContent){
        String fromMarker = "<td class=\"left\">From</td>\n    <td class=\"right\">";
        String author = pageContent.substring(pageContent.indexOf(fromMarker) + fromMarker.length(), pageContent.indexOf("</td>", pageContent.indexOf(fromMarker) + fromMarker.length()));
        author = author.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"").replaceAll("&apos;", "'");
        return author;
    }

    // Get Mail DateStamp
    public String getDateStamp(String pageContent){
        String dateMarker = "<td class=\"left\">Date</td>\n    <td class=\"right\">";
        String dateStamp = pageContent.substring(pageContent.indexOf(dateMarker) + dateMarker.length(), pageContent.indexOf("</td>", pageContent.indexOf(dateMarker) + dateMarker.length()));
        dateStamp = dateStamp.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"").replaceAll("&apos;", "'");
        dateStamp = dateStamp.replaceAll(":", "-");
        return dateStamp;
    }

    //Get Mail Content
    public String getMessage(String pageContent){
        int startMarker = pageContent.indexOf("<pre>");
        int endMarker = pageContent.indexOf("</pre>");
        String message = pageContent.substring(startMarker + "<pre>".length(), endMarker);
        message = message.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"").replaceAll("&apos;", "'");
        return message;
    }

    // Get Attachment(s) URL
    public Vector<URL> getAttachmentsURL(URL url, String pageContent) throws MalformedURLException, URISyntaxException {
        Vector<URL> attachments = new Vector<URL>();
        UrlOperations urlOperations = new UrlOperations();
        String startPattern = "<a rel=\"nofollow\" href=\"";
        String endPattern = "\"";
        int index = 0;
        while((index = pageContent.indexOf(startPattern, index)) != -1){
            int start = index + startPattern.length();
            int end = pageContent.indexOf(endPattern, start);
            attachments.add(urlOperations.resolveURL(url, pageContent.substring(start, end)));
            index = end;
        }
        return attachments;
    }
}
