package MangaRipper.DataStructures;

/**
 * Created by oduibhir on 24/09/16.
 */
public class Page {
    public String url, name, imageUrl;
    public int size = 0;
    public Page(String url, String name, String imageUrl) {
        this.url = url;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
