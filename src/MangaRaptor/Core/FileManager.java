package MangaRaptor.Core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by oisin on 11/29/16.
 */
public class FileManager {

    OutputStream output;
    ZipEntry currentEntry;

    String relativePath;

    public FileManager() {

    }

    public void newZip(String name) {
        if(!name.endsWith(".zip")) name += ".zip";
        File file = new File(name);

        if(!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            output = new ZipOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newFolder(String name) {
        if(!name.endsWith("/"))name += "/";
        relativePath = name;
    }

    public void writeZip(InputStream input, String fileName) {
        currentEntry = new ZipEntry(fileName);
        try {

            ((ZipOutputStream)output).putNextEntry(currentEntry);

            byte[] buff = new byte[150];
            int len = 0;
            while((len = input.read(buff, 0, buff.length)) != -1) {
                output.write(buff, 0, len);
            }

            ((ZipOutputStream)output).closeEntry();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(InputStream input, String fileName) {
        fileName = relativePath + fileName;
        File file = new File(fileName);

        if(!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            output = new FileOutputStream(file);
            byte[] buff = new byte[150];
            int len = 0;
            while((len = input.read(buff, 0, buff.length)) != -1) {
                output.write(buff, 0, len);
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Close Streams
    public void close() {
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
