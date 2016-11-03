package MangaRipper.Services;

import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Page;
import MangaRipper.DataStructures.Series;

import java.util.ArrayList;

/**
 * Created by oduibhir on 25/09/16.
 */
public abstract class Service {
    public String sitePath;
    public abstract ArrayList<Chapter> getChapters(Series series);
    public abstract ArrayList<Page> getPages(Chapter chapter);
    public abstract ArrayList<Series> getSeries(String query);
}
