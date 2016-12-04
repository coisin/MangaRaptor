package MangaRipper.Core;

import Core.Table;
import MangaRipper.Core.GUI.CancellationToken;
import MangaRipper.Core.GUI.progressBar;
import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Series;
import MangaRipper.Services.Manga3;
import MangaRipper.Services.MangaKakalot;
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

    static Dimension size;
    private int WIDTH = 1000, HEIGHT = WIDTH / 14 * 7;

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
    JButton clearChaptersTable;
    JButton clearDownloadsTable;

    JCheckBox packageAsZipCheckBox;

    CardLayout mainLayout = new CardLayout();

    JPanel searchCard;
    JPanel mainCard;

    // Main Card Tables
    Table<Chapter> chaptersTable = new Table(Chapter.class);
    Table<Chapter> downloadsTable = new Table(Chapter.class);

    // Search Card Tables
    Table<Series> mangaReaderResultsTable = new Table<Series>(Series.class);
    Table<Series> manga3ResultsTable = new Table<Series>(Series.class);
    Table<Series> mangaTownResultsTable = new Table<Series>(Series.class);

    CancellationToken cancelTokenDownload;
    Service service;

    ArrayList<Service> services = new ArrayList();
    HashMap<Service, Table> serviceToTable = new HashMap<Service, Table>();

    public ApplicationPanel() {

        super();

        // Instantiate Cards - Start
        searchCard = new JPanel();
        searchCard.setLayout(new BorderLayout());

        mainCard = new JPanel();
        mainCard.setLayout(new BorderLayout());

        setLayout(mainLayout);

        // Instantiate Cards - End

        // Sizing - Start

        size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);

        // Sizing - End

        // Main Card - Start

        // Top Panel - Start

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        seriesNameField = new JTextField(60);
        seriesNameField.addActionListener((ActionEvent actionEvent) -> {
            searchButton.doClick();
        });

        searchButton = new JButton("Search");
        searchButton.addActionListener((ActionEvent e) -> {
            searchButton.setEnabled(false);
            searchQuery();
        });

        JLabel packageAsZipLabel = new JLabel("Package As Zip? ");
        packageAsZipCheckBox = new JCheckBox();

        topPanel.add(seriesNameField);
        topPanel.add(searchButton);
        topPanel.add(packageAsZipLabel);
        topPanel.add(packageAsZipCheckBox);

        // Top Panel - End

        // Center Panel - Start

        chaptersTable.avoidColumn("size");
        chaptersTable.avoidColumn("progress");
        chaptersTable.setPaneSize(350, 400);

        downloadsTable.avoidColumn("size");
        downloadsTable.getColumn("progress").setCellRenderer(new progressBar());
        downloadsTable.setPaneSize(400, 400);

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

        // Bottom Panel - Start

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

        clearChaptersTable = new JButton("Clear");
        clearChaptersTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chaptersTable.removeAllRows();
            }
        });

        bottomLeftPanel.add(clearChaptersTable);
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

        clearDownloadsTable = new JButton("Clear");
        clearDownloadsTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                downloadsTable.removeAllRows();
            }
        });

        bottomRightPanel.add(downloadButton);
        bottomRightPanel.add(cancelDownloadButton);
        bottomRightPanel.add(clearDownloadsTable);
        bottomRightPanel.add(removeFromDownloadsButton);

        bottomPanel.add(bottomLeftPanel);
        bottomPanel.add(bottomRightPanel);

        // Bottom Panel - End

        mainCard.add(topPanel, BorderLayout.NORTH);
        mainCard.add(chaptersTable.getScrollPane(), BorderLayout.WEST);
        mainCard.add(centerPanel, BorderLayout.CENTER);
        mainCard.add(downloadsTable.getScrollPane(), BorderLayout.EAST);
        mainCard.add(bottomPanel, BorderLayout.SOUTH);

        // Main Card - End

        // Search Card - Start

        // Search Card Center - Start

        JPanel searchCardCenter = new JPanel();
        searchCardCenter.setLayout(new FlowLayout());

        addChaptersButton = new JButton("Add");
        addChaptersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addChaptersButton.setEnabled(false);
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

        // Search Card Center - End

        // Tabbed Pane - Start

        mangaReaderResultsTable.setPaneSize(700, 400);
        manga3ResultsTable.setPaneSize(700, 400);
        mangaTownResultsTable.setPaneSize(700, 400);

        JTabbedPane serviceTabs = new JTabbedPane();
        serviceTabs.add("MangaReader", mangaReaderResultsTable.getScrollPane());
        serviceTabs.add("Manga3", manga3ResultsTable.getScrollPane());
        serviceTabs.add("MangaKakalot", mangaTownResultsTable.getScrollPane());

        // Tabbed Pane - End

        searchCard.add(serviceTabs, BorderLayout.WEST);
        searchCard.add(searchCardCenter, BorderLayout.CENTER);

        // Search Card - End

        // Add Cards To Layout - Start

        add(mainCard, "downloader-card");
        add(searchCard, "searcher-card");

        // Add Cards To Layout - End

        // Add Services to ArrayList - Start

        MangaReader mangaReader = new MangaReader();
        Manga3 manga3 = new Manga3();
        MangaKakalot mangaKakalot = new MangaKakalot();

        services.add(mangaReader);
        services.add(manga3);
        services.add(mangaKakalot);

        // Add Services to ArrayList - End

        // Match Search Tables To Services - Start

        serviceToTable.put(mangaReader, mangaReaderResultsTable);
        serviceToTable.put(manga3, manga3ResultsTable);
        serviceToTable.put(mangaKakalot, mangaTownResultsTable);

        // Match Search Tables To Services - End

        switchCard("downloader-card");

    }

    public void switchCard(String name) {
        mainLayout.show(this, name);
    }

    // For adding components to a GridBagLayout
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

    // Set the service to use based on the series given
    public void setServices(String serviceName) {
        for(Service service:services) {
            if(serviceName.equalsIgnoreCase(service.serviceName)) {
                this.service =  service;
                break;
            }
        }
    }

    public void setServices(Series series) {
        setServices(series.service);
    }

    public void setServices(Chapter chapter) {
        setServices(chapter.service);
    }

    // Return currently used Service
    public Service getService() {
        return service;
    }

    // Return download path
    public String getDestinationPath() {
        return destinationFolderField.getText();
    }

    public void removeAllSearchRows() {
        for(Service service:services) {
            serviceToTable.get(service).removeAllRows();
        }
    }

    // Iterate through each Service Search Result Table, and returns highlighted rows
    public List<Series> getHighlightedSearchRows() {
        List<Series> series = new ArrayList<Series>();
        for(Service service:services) {
            series.addAll(serviceToTable.get(service).getHighlightedRows());
        }
        return series;
    }

    // Search for a series, and switches to "searcher-card"
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

    // Add all selected series to the Chapters Table
    public void addAllSeries() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Series> series = getHighlightedSearchRows();
                for(Series i:series) {
                    addChaptersFromSeries(i);
                }
                switchCard("downloader-card");
                addChaptersButton.setEnabled(true);
            }
        }).start();
    }

    // Add Series to Chapters Table
    public void addChaptersFromSeries(Series i) {
        setServices(i);
        ArrayList<Chapter> chapters = service.getChapters(i);
        chaptersTable.addManyRows(chapters);
        removeAllSearchRows();
    }

    // Add highlighted Chapters to Downloads Table
    public void addDownloads() {
        List<Chapter> chapters = chaptersTable.getHighlightedRows();
        downloadsTable.addManyRows(chapters);
    }

    // Start Downloading
    public void download() {

        if(getDestinationPath().equals("") || getDestinationPath() == null) {
            return;
        }

        new Thread(new Runnable() {
            public void run() {

                cancelDownloadButton.setEnabled(true);
                packageAsZipCheckBox.setEnabled(false);
                removeFromDownloadsButton.setEnabled(false);
                addToDownloadButton.setEnabled(false);
                downloadButton.setEnabled(false);
                clearDownloadsTable.setEnabled(false);

                Downloader downloader = new Downloader();
                List<Chapter> chapters = downloadsTable.getData();

                cancelTokenDownload = new CancellationToken();

                for(Chapter chapter:chapters) {
                    setServices(chapter);
                    downloader.downloadChapter(chapter, chapters.indexOf(chapter), chapter.name, cancelTokenDownload);
                    if(cancelTokenDownload.cancel) {
                        stopDownloading();
                        cancelTokenDownload = null;
                        return;
                    }
                }

                stopDownloading();

            }
        }).start();
    }

    // Finalize Downloading - Enable / Disable certain buttons
    public void stopDownloading() {
        cancelDownloadButton.setEnabled(false);
        removeFromDownloadsButton.setEnabled(true);
        packageAsZipCheckBox.setEnabled(true);
        addToDownloadButton.setEnabled(true);
        downloadButton.setEnabled(true);
        clearDownloadsTable.setEnabled(true);
    }

}
