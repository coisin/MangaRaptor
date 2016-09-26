package MangaRipper.Core;

import MangaRipper.Core.GUI.CancellationToken;
import MangaRipper.Core.GUI.ChaptersTable;
import MangaRipper.Core.GUI.DownloadsTable;
import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Page;
import MangaRipper.Services.MangaReader;
import MangaRipper.Services.Service;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by oduibhir on 24/09/16.
 */
public class ApplicationPanel extends JPanel {

    Dimension size;
    static int WIDTH = 1000, HEIGHT = WIDTH / 14 * 7;

    ArrayList<Service> services = new ArrayList();

    JFileChooser destinationFolderChooser;

    JTextField seriesNameField;
    JTextField destinationFolderField;

    JButton openFolderChooserButton;
    JButton cancelDownloadButton;
    JButton addChaptersButton;
    JButton addToDownloadButton;
    JButton selectAllButton;
    JButton downloadButton;

    ChaptersTable chaptersTable = new ChaptersTable();
    DownloadsTable downloadsTable = new DownloadsTable();

    CancellationToken cancelTokenDownload;
    Service service;

    public ApplicationPanel() {

        super();
        setLayout(new BorderLayout());

        size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);

        //Top Panel - Start

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        seriesNameField = new JTextField();
        seriesNameField.setPreferredSize(new Dimension(600, 20));

        addChaptersButton = new JButton("Add");
        addChaptersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addChaptersFromSeries();
            }
        });

        topPanel.add(seriesNameField);
        topPanel.add(addChaptersButton);

        //Top Panel - End

        //Center Panel - Start

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        destinationFolderChooser = new JFileChooser();
        destinationFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        destinationFolderField = new JTextField();
        destinationFolderField.setColumns(20);

        openFolderChooserButton = new JButton("Select Destination Folder");
        openFolderChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = destinationFolderChooser.showOpenDialog(null);
                if(returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = destinationFolderChooser.getSelectedFile();
                    destinationFolderField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        addToGrid(openFolderChooserButton, centerPanel, 0, 0, 1, 1, 0, 0, 10, 0);
        addToGrid(destinationFolderField, centerPanel, 0, 1, 3, 1, 0, 0, 0, 0);

        // Center Panel - End

        //Bottom Panel - Start

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel bottomLeftPanel = new JPanel();
        bottomLeftPanel.setLayout(new FlowLayout());

        addToDownloadButton = new JButton("Add To Downloads");
        addToDownloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDownloads();
            }
        });

        selectAllButton = new JButton("Select All");
        selectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chaptersTable.selectAll();
            }
        });

        bottomLeftPanel.add(selectAllButton);
        bottomLeftPanel.add(addToDownloadButton);

        JPanel bottomRightPanel = new JPanel();
        bottomRightPanel.setLayout(new FlowLayout());

        downloadButton = new JButton("Download");
        downloadButton.setEnabled(true);
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                download();
            }
        });

        cancelTokenDownload = new CancellationToken();
        cancelDownloadButton = new JButton("Cancel");
        cancelDownloadButton.setEnabled(false);
        cancelDownloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelTokenDownload.cancel = true;
                cancelDownloadButton.setEnabled(false);
            }
        });

        bottomRightPanel.add(downloadButton);
        bottomRightPanel.add(cancelDownloadButton);

        bottomPanel.add(bottomLeftPanel);
        bottomPanel.add(bottomRightPanel);

        //Bottom Panel - End

        // Add To Application Panel

        add(topPanel, BorderLayout.NORTH);
        add(chaptersTable.getPane(), BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(downloadsTable.getPane(), BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add All Services to ArrayList

        services.add(new MangaReader());

    }

    public void addToGrid(JComponent component, JPanel panel, int x, int y, int width, int height, int top, int right, int bottom, int left) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        Insets insets = new Insets(top, left, bottom, right);
        constraints.insets = insets;
        panel.add(component, constraints);
    }

    public String refactorUrl(String url) {
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    public void setServices(String url) {
        for(Service service:services) {
            if(url.startsWith(service.sitePath)) {
                this.service =  service;
                break;
            }
        }
    }

    public void addChaptersFromSeries() {

        String url = refactorUrl(seriesNameField.getText());
        setServices(url);

        ArrayList<Chapter> chapters = service.getChapters(url);

        chaptersTable.addManyRows(chapters);

    }

    public void addDownloads() {
        ArrayList<Chapter> chapters = chaptersTable.getDownloads();
        downloadsTable.addManyRows(chapters);
    }

    public void download() {
        new Thread(new Runnable() {
            public void run() {
                cancelDownloadButton.setEnabled(true);
                Downloader downloader = new Downloader();
                ArrayList<Chapter> chapters = downloadsTable.getAsChapters();
                for(Chapter chapter:chapters) {
                    double chapterSize = 0;
                    ArrayList<Page> pages = service.getPages(chapter);
                    for (Page page : pages) {
                        chapterSize += page.size;
                    }
                    if(cancelTokenDownload.cancel) {
                        stopDownload();
                        return;
                    }
                    chapter.size = (int) chapterSize;
                    double amountComplete = 0;
                    for (Page page : pages) {
                        downloader.downloadFile(page.imageUrl);
                        amountComplete += page.size;
                        downloadsTable.updateProgress(amountComplete, chapterSize, chapters.indexOf(chapter));

                        if(cancelTokenDownload.cancel) {
                            stopDownload();
                            return;
                        }

                    }
                }
                cancelDownloadButton.setEnabled(false);
                downloadButton.setEnabled(true);
            }
        }).start();
        downloadButton.setEnabled(false);
    }

    public String getDestinationPath() {
        return destinationFolderField.getText();
    }

    public void stopDownload() {
        cancelDownloadButton.setEnabled(false);
        downloadButton.setEnabled(true);
    }

}
