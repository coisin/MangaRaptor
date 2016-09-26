package MangaRipper.Core;

import MangaRipper.DataStructures.Page;

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

    public void downloadFile(String filePath) {
        try {
            URL fileUrl = new URL(filePath);
            URLConnection connection = fileUrl.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            File outputFile = new File(MangaRipper.mangaRipper.applicationPanel.getDestinationPath() + "/" + fileUrl.getFile());
            if(!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
            }
            InputStream input = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(outputFile);
            int len = 0;
            byte[] buff = new byte[150];
            while((len = input.read(buff, 0, buff.length)) != -1) {
                output.write(buff, 0, len);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadPages(ArrayList<Page> pages) {
        for(Page page : pages) {
            downloadFile(page.imageUrl);
        }
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
