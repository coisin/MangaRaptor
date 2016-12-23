package MangaRaptor.Services;

import MangaRaptor.Core.Downloader;
import MangaRaptor.Core.GUI.CancellationToken;
import MangaRaptor.Core.Parser;
import MangaRaptor.DataStructures.Chapter;
import MangaRaptor.DataStructures.Page;
import MangaRaptor.DataStructures.Series;
import MangaRaptor.DataStructures.StringPair;

import java.util.ArrayList;

/**
 * Created by oduibhir on 25/09/16.
 */
public class MangaReader extends Service {

    public MangaReader() {
        sitePath = "http://www.mangareader.net";
        serviceName = "MangaReader";
    }



    public ArrayList<Series> getSeries(String query) {
        ArrayList<Series> series = new ArrayList();
        Downloader downloader = new Downloader();
        String link = sitePath + "/search/?w=" + query.replaceAll(" ", "+");

        String searchPage = downloader.getWebpageAsString(link);
        String expression = "<div class=\"manga_name\">(.*?)<a href=\"(.*?)\">(.*?)</a>";
        ArrayList<StringPair> pairs = Parser.parse(searchPage, expression, 2, 3);
        for(StringPair pair:pairs) {
            series.add(new Series(pair.getTwo(), sitePath + pair.getOne(), serviceName));
        }
        return series;
    }

    public ArrayList<Chapter> getChapters(Series series) {
        String path = series.getLink();
        Downloader downloader = new Downloader();
        ArrayList<Chapter> chapters = new ArrayList();

        String seriesPage = Parser.parse(downloader.getWebpageAsString(path), "<table id=\"listing\">(.*?)</table>(.*)", 1, 2).get(0).getOne();
        String expression = "<a href=(\"|')(.*?)(\"|')>(.*?)</a>";
        ArrayList<StringPair> chapterPair = Parser.parse(seriesPage, expression, 2, 4);
        for(StringPair pair:chapterPair) {
            String chapterPath = sitePath + pair.getOne();
            if(!Parser.matches(pair.getOne(), "(.*?)/([0-9]+)"))continue;
            Chapter chapter = new Chapter(chapterPath, pair.getTwo(), serviceName);
            chapters.add(chapter);
        }
        return chapters;
    }

    public ArrayList<Page> getPages(Chapter chapter, CancellationToken token) {
        Downloader downloader = new Downloader();
        ArrayList<Page> pages = new ArrayList();
        String expression = "<option value=\"(.*?)\"(.*?)>(.*?)</option>";

        String chapterPage = downloader.getWebpageAsString(chapter.getUrl());
        ArrayList<StringPair> pagePair = Parser.parse(chapterPage, expression, 1, 3);

        for(StringPair pair:pagePair) {

            if(token.cancel) {
                return null;
            }

            String pagePath = sitePath + pair.getOne();
            String imagePath = getImagePath(pagePath);
            System.out.println(imagePath);
            Page page = new Page(pagePath, pair.getTwo(), imagePath, imagePath.substring(imagePath.lastIndexOf(".")));
            page.setSize(downloader.getFileSize(pagePath));
            pages.add(page);
        }

        return pages;
    }

    public String getImagePath(String pagePath) {
        Downloader downloader = new Downloader();

        String pagePage = downloader.getWebpageAsString(pagePath);
        String expression = "<img id=\"img\"(.*?)src=\"(.*?)\"";

        StringPair pair = Parser.parse(pagePage, expression, 2, -1).get(0);
        return pair.getOne();
    }

}
