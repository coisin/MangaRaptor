package MangaRipper.Core;

import MangaRipper.Core.GUI.CancellationToken;
import MangaRipper.Core.GUI.ChaptersTable;
import MangaRipper.Core.GUI.DownloadsTable;
import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Page;
import MangaRipper.Services.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by oduibhir on 24/09/16.
 */
public class Downloader {

    ApplicationPanel panel = MangaRipper.mangaRipper.applicationPanel;

    public Downloader() {

    }

    public String getWebpageAsString(String link) {
        String page = "";
        byte[] buff = new byte[150];
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream input = connection.getInputStream();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));
            String line;
            while((line = inputReader.readLine()) != null) {
                page += line;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    public void downloadFile(String filePath, String fileName) {
        try {
            URL url = new URL(filePath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            InputStream inputStream = connection.getInputStream();

            File outputFile = new File(panel.getDestinationPath() + "/" + fileName);
            if(!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buff = new byte[150];
            int len = 0;
            while((len = inputStream.read(buff, 0, buff.length)) != -1) {
                outputStream.write(buff, 0, len);
            }

            inputStream.close();
            outputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadChapter(Chapter chapter, int index, String fileName, CancellationToken token) {
        Service service = panel.getService();
        ArrayList<Page> pages = service.getPages(chapter);
        chapter.size = getPagesSize(pages);
        panel.downloadsTable.setCurrentChapter(chapter, index);
        downloadPages(pages, fileName, token);
    }

    public void downloadPages(ArrayList<Page> pages, String fileName, CancellationToken token) {
        for(Page page : pages) {
            downloadFile(page.imageUrl, fileName + "/" + page.name);
            panel.downloadsTable.updateProgress(page.size);
            if(token.cancel) {
                panel.stopDownloading();
                return;
            }
        }
    }

    public int getPagesSize(ArrayList<Page> pages) {
        int size = 0;
        for(Page page:pages) {
            size += page.size;
        }
        return size;
    }

    public int getFileSize(String path) {
        try {
            URL url = new URL(path);
            URLConnection connection = url.openConnection();
            connection.connect();
            return connection.getContentLength();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
