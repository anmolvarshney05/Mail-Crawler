import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlOperations {

    // Get URL Page Contents
    public String getPage(URL url) throws Exception{
        InputStream in = new URL(url.toString()).openStream();
        return IOUtils.toString(in, "UTF-8");
    }

    // Resolve base URL and relative URL
    public URL resolveURL(URL oldURL, String newURL) throws MalformedURLException, URISyntaxException {
        URI uri = oldURL.toURI();
        URI uriRes = uri.resolve(newURL.replaceAll("\"", ""));
        URL url = uriRes.toURL();
        return url;
    }
}
