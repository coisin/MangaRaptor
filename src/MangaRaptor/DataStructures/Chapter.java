package MangaRaptor.DataStructures;

/**
 * Created by oduibhir on 24/09/16.
 */
public class Chapter {
    public String url, name;
    public int size = 0;
    public double progress = 0.0;
    public String service;
    public Chapter(String url, String name, String service) {
        this.url = url;
        this.name = name;
        this.service = service;
    }
}
