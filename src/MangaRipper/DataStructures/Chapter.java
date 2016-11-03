package MangaRipper.DataStructures;

import MangaRipper.Core.GUI.progressBar;

/**
 * Created by oduibhir on 24/09/16.
 */
public class Chapter {
    public String url, name;
    public int size = 0;
    public double progress = 0.0;
    public Chapter(String url, String name) {
        this.url = url;
        this.name = name;
    }
}
