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
 * Created by oisin on 11/26/16.
 */
public class MangaKakalot extends Service {
    public MangaKakalot() {
        sitePath = "http://mangakakalot.com";
        serviceName = "MangaKakalot";
    }

    @Override
    public ArrayList<Series> getSeries(String query) {

        Downloader downloader = new Downloader();

        query = query.replaceAll(" ", "_").toLowerCase();
        ArrayList<Series> series = new ArrayList<>();

        String searchUrl = sitePath + "/search/" + query;
        String searchPageHtml = downloader.getWebpageAsString(searchUrl);

        String exp = "<span class=\"item-name\">(.*?)<a(.*?)href=\"(.*?)\"(.*?)>(.*?)</a>";
        ArrayList<StringPair> htmlSeries = Parser.parse(searchPageHtml, exp, 3, 5);

        for(StringPair pair:htmlSeries) {
            series.add(new Series(pair.getTwo(), pair.getOne(), serviceName));
        }

        return series;
    }

    @Override
    public ArrayList<Chapter> getChapters(Series series) {
        Downloader downloader = new Downloader();
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();

        String chapterSite = downloader.getWebpageAsString(series.getLink());
        String exp = "<span><a href=\"(.*?)\"(.*?)>(.*?)</a>";
        ArrayList<StringPair> chaptersHtml = Parser.parse(chapterSite, exp, 1, 3);
        for(StringPair pair:chaptersHtml) {
            String link = pair.getOne();
            String name = pair.getTwo();
            Chapter chapter = new Chapter(link, name, serviceName);
            chapters.add(chapter);
        }

        return chapters;
    }

    @Override
    public ArrayList<Page> getPages(Chapter chapter, CancellationToken token) {
        Downloader downloader = new Downloader();
        ArrayList<Page> pages = new ArrayList<>();

        String pageHtml = downloader.getWebpageAsString(chapter.getUrl());
        String exp = "data = '(.*?)'";
        String pageDataStr = Parser.parse(pageHtml, exp, 1, -1).get(0).getOne();

        String[] pageData = pageDataStr.split("http");

        for(int i = 0;i<pageData.length;i++) {

            if(token.cancel) {
                return null;
            }

            if(i == 0) {
                continue;
            }
            String link = "http" + pageData[i];
            if(i != pageData.length - 1)
                link = link.substring(0, link.length() - 1);
            String ext = link.substring(link.lastIndexOf("."));
            String name = Integer.toString(i);
            Page page = new Page(link, name, link, ext);
            page.setSize(downloader.getFileSize(link));

            pages.add(page);
        }

        return pages;
    }
}
