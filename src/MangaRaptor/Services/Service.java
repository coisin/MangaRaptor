package MangaRaptor.Services;

import MangaRaptor.Core.GUI.CancellationToken;
import MangaRaptor.DataStructures.Chapter;
import MangaRaptor.DataStructures.Page;
import MangaRaptor.DataStructures.Series;

import java.util.ArrayList;

/**
 * Created by oduibhir on 25/09/16.
 */
public abstract class Service {
    public String sitePath;
    public String serviceName;
    public abstract ArrayList<Chapter> getChapters(Series series);
    public abstract ArrayList<Page> getPages(Chapter chapter, CancellationToken token);
    public abstract ArrayList<Series> getSeries(String query);
}
