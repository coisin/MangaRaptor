package MangaRipper.Services;

import MangaRipper.Core.Downloader;
import MangaRipper.Core.Parser;
import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Page;
import MangaRipper.DataStructures.Series;
import MangaRipper.DataStructures.StringPair;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by oduibhir on 25/09/16.
 */
public class MangaReader extends Service {

    public MangaReader() {
        sitePath = "http://www.mangareader.net";
    }

    public ArrayList<Chapter> getChapters(Series series) {
        String path = series.link;
        Downloader downloader = new Downloader();
        ArrayList<Chapter> chapters = new ArrayList();

        String seriesPage = Parser.parse(downloader.getWebpageAsString(path), "<table id=\"listing\">(.*?)</table>(.*)", 1, 2).get(0).one;
        String expression = "<a href=(\"|')(.*?)(\"|')>(.*?)</a>";
        ArrayList<StringPair> chapterPair = Parser.parse(seriesPage, expression, 2, 4);
        for(StringPair pair:chapterPair) {
            String chapterPath = sitePath + pair.one;
            if(!Parser.matches(pair.one, "(.*?)/([0-9]+)"))continue;
            Chapter chapter = new Chapter(chapterPath, pair.two, "MangaReader");
            chapters.add(chapter);
        }
        return chapters;
    }

    public ArrayList<Page> getPages(Chapter chapter) {
        Downloader downloader = new Downloader();
        ArrayList<Page> pages = new ArrayList();
        String expression = "<option value=\"(.*?)\"(.*?)>(.*?)</option>";

        String chapterPage = downloader.getWebpageAsString(chapter.url);
        ArrayList<StringPair> pagePair = Parser.parse(chapterPage, expression, 1, 3);

        for(StringPair pair:pagePair) {
            String pagePath = sitePath + pair.one;
            String imagePath = getImagePath(pagePath);
            Page page = new Page(pagePath, pair.two, imagePath, imagePath.substring(imagePath.lastIndexOf(".")));
            page.size = downloader.getFileSize(pagePath);
            pages.add(page);
        }

        return pages;
    }

    public String getImagePath(String pagePath) {
        Downloader downloader = new Downloader();
        String path="";

        String pagePage = downloader.getWebpageAsString(pagePath);
        String expression = "<img id=\"img\"(.*?)src=\"(.*?)\"";

        StringPair pair = Parser.parse(pagePage, expression, 1, 2).get(0);
        path = pair.two;

        return path;
    }

    public ArrayList<Series> getSeries(String query) {
        ArrayList<Series> series = new ArrayList();
        Downloader downloader = new Downloader();
        String link = sitePath + "/search/?w=" + query.replaceAll(" ", "+");

        String searchPage = downloader.getWebpageAsString(link);
        String expression = "<div class=\"manga_name\">(.*?)<a href=\"(.*?)\">(.*?)</a>";
        ArrayList<StringPair> pairs = Parser.parse(searchPage, expression, 2, 3);
        for(StringPair pair:pairs) {
            series.add(new Series(pair.two, sitePath + pair.one, "MangaReader"));
        }
        return series;
    }

}
