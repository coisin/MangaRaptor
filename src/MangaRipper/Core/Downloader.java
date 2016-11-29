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
import java.util.concurrent.TimeUnit;

/**
 * Created by oduibhir on 24/09/16.
 */
public class Downloader {

    ApplicationPanel panel = MangaRipper.mangaRipper.applicationPanel;
    FileManager fileManager;

    public Downloader() {
    	fileManager = new FileManager();
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
        System.out.println("DOWNLOADING " + fileName);
        try {
            URL url = new URL(filePath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            InputStream inputStream = connection.getInputStream();

            String name = fileName;

            if(panel.packageAsZipCheckBox.isSelected()) {
                fileManager.writeZip(inputStream, fileName);
            } else {
                fileManager.write(inputStream, fileName);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    Chapter currentChapterDownloading;
    int currentChapterDownloadingIndex;

    public void downloadChapter(Chapter chapter, int index, String fileName, CancellationToken token) {
        if(chapter.progress >= 99.99) {
            return;
        }

        Service service = panel.getService();
        ArrayList<Page> pages = service.getPages(chapter, token);
        if(pages == null) {
            return;
        }
        chapter.size = getPagesSize(pages);
        currentChapterDownloading = chapter;
        currentChapterDownloadingIndex = index;

        String folderName = panel.getDestinationPath() + "/" + fileName;

        if(panel.packageAsZipCheckBox.isSelected()) {
            fileManager.newZip(folderName);
        } else {
            fileManager.newFolder(folderName);
        }
        downloadPages(pages, fileName, token);
        fileManager.close();
    }

    public void downloadPages(ArrayList<Page> pages, String fileName, CancellationToken token) {
        for(Page page : pages) {
            downloadFile(page.imageUrl, page.name + page.extension);
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
