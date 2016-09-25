package MangaRipper.Core;

import MangaRipper.Core.GUI.ChaptersTable;
import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Page;
import MangaRipper.Services.MangaReader;
import MangaRipper.Services.Service;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by oduibhir on 24/09/16.
 */
public class ApplicationPanel extends JPanel {

    Dimension size;
    static int WIDTH = 700, HEIGHT = WIDTH / 14 * 7;

    ArrayList<Service> services = new ArrayList();

    JTextField seriesNameField;
    JButton addChaptersButton;

    ChaptersTable chaptersTable = new ChaptersTable();

    public ApplicationPanel() {

        super();
        setLayout(new BorderLayout());

        size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);

        JPanel topPanel = new JPanel();
        seriesNameField = new JTextField();
        seriesNameField.setPreferredSize(new Dimension(600, 20));

        addChaptersButton = new JButton("Add");
        addChaptersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addChaptersFromSeries();
            }
        });

        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(seriesNameField);
        topPanel.add(addChaptersButton);

        addComponent(topPanel, BorderLayout.NORTH);
        addComponent(chaptersTable.getPane(), BorderLayout.WEST);

        services.add(new MangaReader());

    }

    public void addComponent(JComponent component, String position) {
        add(component, position);
    }

    public Service getServices(String url) {
        for(Service service:services) {
            if(url.startsWith(service.sitePath)) {
                return service;
            }
        }
        return null;
    }

    public String refactorUrl(String url) {
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    public void addChaptersFromSeries() {

        String url = refactorUrl(seriesNameField.getText());
        Service service = getServices(url);

        ArrayList<Chapter> chapters = service.getChapters(url);

        for(Chapter chapter:chapters) {
            Object[] row = {chapter.name, false};
            chaptersTable.addRow(row);
        }

    }

}
