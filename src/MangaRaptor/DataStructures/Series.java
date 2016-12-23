package MangaRaptor.DataStructures;

/**
 * Created by oduibhir on 26/09/16.
 */
public class Series {
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String link;
    public String service;
    public Series(String name, String link, String service) {
        this.link = link;
        this.name = name;
        this.service = service;
    }
}
