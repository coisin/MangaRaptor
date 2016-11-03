package MangaRipper.Core;

import MangaRipper.Core.GUI.CancellationToken;
import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Page;
import MangaRipper.Services.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

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
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            InputStream input = connection.getInputStream();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
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

    Chapter currentChapterDownloading;
    int currentChapterDownloadingIndex;

    public void downloadChapter(Chapter chapter, int index, String fileName, CancellationToken token) {
        Service service = panel.getService();
        ArrayList<Page> pages = service.getPages(chapter, token);
        if(pages == null) {
            return;
        }
        chapter.size = getPagesSize(pages);
        currentChapterDownloading = chapter;
        currentChapterDownloadingIndex = index;
        downloadPages(pages, fileName, token);
    }

    public void downloadPages(ArrayList<Page> pages, String fileName, CancellationToken token) {
        for(Page page : pages) {
            downloadFile(page.imageUrl, fileName + "/" + page.name + page.extension);
            updateProgress(page.size);
            if(token.cancel) {
                return;
            }
        }
    }

    public void updateProgress(int pageSize) {
        int totalSize = currentChapterDownloading.size;
        double percentagePageCompletes = ((double)pageSize / (double)totalSize) * 100.00;
        currentChapterDownloading.progress += percentagePageCompletes;
        panel.downloadsTable.update(currentChapterDownloading);

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
