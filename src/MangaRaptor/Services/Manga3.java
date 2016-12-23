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
 * Created by oisin on 11/3/16.
 */
public class Manga3 extends Service {

    public Manga3() {
        sitePath = "http://www.manga3.net";
        serviceName = "Manga3";
    }

    public ArrayList<Series> getSeries(String query) {
        query = query.toLowerCase().replace(" ", "-");
        ArrayList<Series> series = new ArrayList<Series>();
        Downloader downloader = new Downloader();

        String searchPath = sitePath + "/Search/" + query;
        String siteSearch = downloader.getWebpageAsString(searchPath);

        String exp = "<div class=\"semi_title\">(.*?)<a href=\"(.*?)\">(.*?)</a>(.*?)</div>";
        ArrayList<StringPair> rowInfos = Parser.parse(siteSearch, exp, 2, 3);

        for(StringPair rowInfo:rowInfos) {
            String link = sitePath + rowInfo.getOne();
            String name = rowInfo.getTwo();

            series.add(new Series(name, link, serviceName));
        }

        return series;
    }

    public ArrayList<Chapter> getChapters(Series series) {
        Downloader downloader = new Downloader();
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();

        String seriesSite = downloader.getWebpageAsString(series.getLink());
        String exp = "Manga Chapters(.*)";
        seriesSite = Parser.parse(seriesSite, exp, 1, -1).get(0).getOne();
        exp = "<a href=\"(.*?)\">(.*?)</a>";
        ArrayList<StringPair> rowInfos = Parser.parse(seriesSite, exp, 1, 2);

        for(StringPair rowInfo:rowInfos) {
            String link = sitePath + rowInfo.getOne();
            String name = rowInfo.getTwo();
            if(name.contains("<span>")){
                name = name.substring(0, name.indexOf("<span>"));
            }

            if(!chapters.contains(new Chapter(link, name, serviceName)))
                chapters.add(new Chapter(link, name, serviceName));
        }

        return chapters;
    }

    public ArrayList<Page> getPages(Chapter chapter, CancellationToken token) {
        ArrayList<Page> pages = new ArrayList<Page>();
        Downloader downloader = new Downloader();

        String exp = "<select class=\"manga_select_page\"(.*?)>(.*?)</select>";
        System.out.println(chapter.getUrl());
        String chapterSite = Parser.parse(downloader.getWebpageAsString(chapter.getUrl()), exp, 2, -1).get(0).getOne();

        exp = "<option value=\"(.*?)\"(.*?)>(.*?)</option>";
        ArrayList<StringPair> rowInfos = Parser.parse(chapterSite, exp, 1, 3);
        for(StringPair rowInfo:rowInfos) {

            if(token.cancel) {
                return null;
            }

            String link = sitePath + rowInfo.getOne();
            String name = rowInfo.getTwo();

            String pageSite = downloader.getWebpageAsString(link);
            exp = "img id=\"gohere\"(.*?)src=\"(.*?)\"";
            String image = Parser.parse(pageSite, exp, 2, -1).get(0).getOne();
            String ext = image.substring(image.lastIndexOf("."));

            int imageSize = downloader.getFileSize(image);
            Page page = new Page(link, name, image, ext);
            page.setSize(imageSize);
            pages.add(page);

        }

        return pages;
    }
}
