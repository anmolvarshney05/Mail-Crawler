import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class TrackProgress {
    private File seenURL;
    private File newURL;
    private File url;
    private File index;

    // Initialise File Paths
    public TrackProgress(String directory){
        seenURL = new File(directory + File.separator + "SeenURL.txt");
        newURL = new File(directory + File.separator + "NewURL.txt");
        url = new File(directory + File.separator + "URL.txt");
        index = new File(directory + File.separator + "Index.txt");
    }

    // Check if files exist in the directory
    public boolean fileExists(String URL) throws IOException {
        boolean seenURLTest = seenURL.exists();
        boolean newURLTest = newURL.exists();
        boolean urlTest = url.exists();
        boolean indexTest = index.exists();
        if(seenURLTest && newURLTest && urlTest && indexTest)
            return urlCheck(URL);
        return false;
    }

    // check if URL is the same
    private boolean urlCheck(String URL) throws IOException {
        return ((FileUtils.readFileToString(url, "UTF-8")).equals(URL));
    }

    // Initialise Files
    public void initialise(URL url) throws IOException {
        FileUtils.writeStringToFile(this.url, url.toString(), "UTF-8");
        FileUtils.writeStringToFile(index, String.valueOf(0), "UTF-8");
        FileUtils.writeStringToFile(newURL, url.toString(), "UTF-8", true);
        FileUtils.writeStringToFile(seenURL, url.toString(), "UTF-8", true);
    }

    // Add to Seen URL's
    public void updateSeenURL(String URL) throws IOException {
        FileUtils.writeStringToFile(seenURL, "\n" + URL, "UTF-8", true);
    }

    // Add to new URL's
    public void addNewURL(String URL) throws IOException {
        FileUtils.writeStringToFile(newURL, "\n" + URL, "UTF-8", true);
    }

    // Remove URL after processing
    public void removeURL() throws IOException {
        String indexStr = FileUtils.readFileToString(index, "UTF-8");
        FileUtils.writeStringToFile(index, String.valueOf(Integer.parseInt(indexStr) + 1), "UTF-8");
    }

    // Reload seen URL table
    public Hashtable<URL, Integer> reloadSeenURL() throws IOException {
        List<String> seen = FileUtils.readLines(seenURL, "UTF-8");
        Hashtable<URL, Integer> hashSeen = new Hashtable<URL, Integer>();
        for(String seenUrl : seen){
            hashSeen.put(new URL(seenUrl), new Integer(1));
        }
        return hashSeen;
    }

    // Reload new URL's to be processed
    public Vector<URL> reloadNewURL() throws IOException {
        List<String> newUrl = FileUtils.readLines(newURL, "UTF-8");
        Vector<URL> newURLVec = new Vector<URL>();
        String indexStr = FileUtils.readFileToString(index, "UTF-8");
        for(int i = Integer.parseInt(indexStr); i < newUrl.size(); i++){
            newURLVec.add(new URL(newUrl.get(i)));
        }
        return newURLVec;
    }
}
