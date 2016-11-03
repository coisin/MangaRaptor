package MangaRipper.DataStructures;

/**
 * Created by oduibhir on 26/09/16.
 */
public class Series {
    public String name, link;
    public String service;
    public Series() {

    }
    public Series(String name, String link, String service) {
        this.link = link;
        this.name = name;
        this.service = service;
    }
}
