package MangaRaptor.DataStructures;

/**
 * Created by oduibhir on 24/09/16.
 */
public class Page {
    public String url, name, imageUrl;
    public int size = 0;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String extension;
    public Page(String url, String name, String imageUrl, String ext) {
    	extension = ext;
        this.url = url;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
