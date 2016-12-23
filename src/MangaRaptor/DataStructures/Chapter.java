package MangaRaptor.DataStructures;

/**
 * Created by oduibhir on 24/09/16.
 */
public class Chapter {
    public String url, name;
    public int size = 0;
    public double progress = 0.0;
    public String service;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Chapter(String url, String name, String service) {
        this.url = url;
        this.name = name;
        this.service = service;
    }
}
