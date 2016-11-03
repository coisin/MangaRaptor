package MangaRipper.Core;

import Core.Table;
import MangaRipper.Core.GUI.CancellationToken;
import MangaRipper.Core.GUI.progressBar;
import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Series;
import MangaRipper.Services.Manga3;
import MangaRipper.Services.MangaReader;
import MangaRipper.Services.Service;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
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
    JButton searchButton;
    JButton addToDownloadButton;
    JButton selectAllButton;
    JButton downloadButton;
    JButton removeFromChaptersButton;
    JButton removeFromDownloadsButton;
    JButton backButton;

    Table<Chapter> chaptersTable = new Table(Chapter.class);
    Table<Chapter> downloadsTable = new Table(Chapter.class);

    Table<Series> mangaReaderResultsTable = new Table<Series>(Series.class);
    Table<Series> manga3ResultsTable = new Table<Series>(Series.class);

    CardLayout mainLayout = new CardLayout();

    CancellationToken cancelTokenDownload;
    Service service;

    JPanel searchCard;
    JPanel mainCard;

    HashMap<Service, Table> serviceToTable = new HashMap<Service, Table>();

    public ApplicationPanel() {

        super();

        chaptersTable.avoidColumn("size");
        chaptersTable.avoidColumn("progress");
        chaptersTable.setPaneSize(350, 400);

        downloadsTable.avoidColumn("size");
        downloadsTable.getColumn("progress").setCellRenderer(new progressBar());
        downloadsTable.setPaneSize(400, 400);

        //Instantiate Cards
        searchCard = new JPanel();
        searchCard.setLayout(new BorderLayout());

        mainCard = new JPanel();
        mainCard.setLayout(new BorderLayout());

        //Instantiation End

        setLayout(mainLayout);

        size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);

        //Top Panel - Start

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        seriesNameField = new JTextField();
        seriesNameField.setPreferredSize(new Dimension(600, 20));

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchButton.setEnabled(false);
                searchQuery();
            }
        });

        topPanel.add(seriesNameField);
        topPanel.add(searchButton);

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

        removeFromChaptersButton = new JButton("Remove");
        removeFromChaptersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chaptersTable.removeAllRows();
            }
        });

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

        bottomLeftPanel.add(removeFromChaptersButton);
        bottomLeftPanel.add(selectAllButton);
        bottomLeftPanel.add(addToDownloadButton);

        JPanel bottomRightPanel = new JPanel();
        bottomRightPanel.setLayout(new FlowLayout());

        removeFromDownloadsButton = new JButton("Remove");
        removeFromDownloadsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadsTable.removeHighlightedRows();
            }
        });

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
        bottomRightPanel.add(removeFromDownloadsButton);

        bottomPanel.add(bottomLeftPanel);
        bottomPanel.add(bottomRightPanel);

        //Bottom Panel - End

        // Search Card - Start

        JPanel searchCardCenter = new JPanel();
        searchCardCenter.setLayout(new FlowLayout());

        addChaptersButton = new JButton("Add");
        addChaptersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addAllSeries();
            }
        });
        
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		removeAllSearchRows();
        		switchCard("downloader-card");
        	}
        });

        searchCardCenter.add(addChaptersButton);
        searchCardCenter.add(backButton);

        mangaReaderResultsTable.setPaneSize(700, 400);
        manga3ResultsTable.setPaneSize(700, 400);

        JTabbedPane serviceTabs = new JTabbedPane();
        serviceTabs.add("MangaReader", mangaReaderResultsTable.getScrollPane());
        serviceTabs.add("Manga3", manga3ResultsTable.getScrollPane());

        searchCard.add(serviceTabs, BorderLayout.WEST);
        searchCard.add(searchCardCenter, BorderLayout.CENTER);

        // Search Card - End

        // Add To Application Panel

        mainCard.add(topPanel, BorderLayout.NORTH);
        mainCard.add(chaptersTable.getScrollPane(), BorderLayout.WEST);
        mainCard.add(centerPanel, BorderLayout.CENTER);
        mainCard.add(downloadsTable.getScrollPane(), BorderLayout.EAST);
        mainCard.add(bottomPanel, BorderLayout.SOUTH);

        //Add Cards To Layout

        add(mainCard, "downloader-card");
        add(searchCard, "searcher-card");

        // Add All Services to ArrayList

        MangaReader mangaReader = new MangaReader();
        Manga3 manga3 = new Manga3();

        services.add(mangaReader);
        services.add(manga3);

        // Match Search Tables To Services

        serviceToTable.put(mangaReader, mangaReaderResultsTable);
        serviceToTable.put(manga3, manga3ResultsTable);

        //Set Card
        switchCard("downloader-card");

    }

    public void removeAllSearchRows() {
        for(Service service:services) {
            serviceToTable.get(service).removeAllRows();
        }
    }

    public List<Series> getHighlightedSearchRows() {
        List<Series> series = new ArrayList<Series>();
        for(Service service:services) {
            series.addAll(serviceToTable.get(service).getHighlightedRows());
        }
        return series;
    }

    public void switchCard(String name) {
        mainLayout.show(this, name);
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

    public void addAllSeries() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Series> series = getHighlightedSearchRows();
                for(Series i:series) {
                    addChaptersFromSeries(i);
                }
                switchCard("downloader-card");
            }
        }).start();
    }

    public void addChaptersFromSeries(Series i) {
        setServices(i.link);
        ArrayList<Chapter> chapters = service.getChapters(i);
        chaptersTable.addManyRows(chapters);
        removeAllSearchRows();
    }

    public void addDownloads() {
        List<Chapter> chapters = chaptersTable.getHighlightedRows();
        downloadsTable.addManyRows(chapters);
    }

    public void download() {

        if(getDestinationPath().equals("") || getDestinationPath() == null) {
            return;
        }

        new Thread(new Runnable() {
            public void run() {

                cancelDownloadButton.setEnabled(true);
                removeFromDownloadsButton.setEnabled(false);
                addToDownloadButton.setEnabled(false);
                downloadButton.setEnabled(false);

                Downloader downloader = new Downloader();
                List<Chapter> chapters = downloadsTable.getData();

                for(Chapter chapter:chapters) {
                    downloader.downloadChapter(chapter, chapters.indexOf(chapter), chapter.name, cancelTokenDownload);
                    if(cancelTokenDownload.cancel) {
                        stopDownloading();
                        return;
                    }
                }

                stopDownloading();

            }
        }).start();
    }

    public Service getService() {
        return service;
    }

    public String getDestinationPath() {
        return destinationFolderField.getText();
    }

    public void stopDownloading() {
        cancelDownloadButton.setEnabled(false);
        removeFromDownloadsButton.setEnabled(true);
        addToDownloadButton.setEnabled(true);
        downloadButton.setEnabled(true);
    }

    public void searchQuery() {
        String seriesName = seriesNameField.getText();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Series> series;
                for(Service service:services) {
                    series = service.getSeries(seriesName);
                    serviceToTable.get(service).addManyRows(series);
                }
                searchButton.setEnabled(true);
                switchCard("searcher-card");
            }
        }).start();
    }

}
