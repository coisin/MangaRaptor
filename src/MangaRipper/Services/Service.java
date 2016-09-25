package MangaRipper.Services;

import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Page;

import java.util.ArrayList;

/**
 * Created by oduibhir on 25/09/16.
 */
public abstract class Service {
    public String sitePath;
    public abstract ArrayList<Chapter> getChapters(String seriesName);
    public abstract ArrayList<Page> getPages(Chapter chapter);
}
