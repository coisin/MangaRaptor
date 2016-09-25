package MangaRipper.Core.GUI;

import MangaRipper.DataStructures.Chapter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;

/**
 * Created by oduibhir on 25/09/16.
 */
public class DownloadsTable extends Table<Chapter> {

    public DownloadsTable() {

        super();

        columnNames = new String[] {"Name", "Link", "Progress"};

        model = new DefaultTableModel() {
            public Class getColumnClass(int column) {
                switch(column) {
                    case 0:
                    case 1: return String.class;
                    default: return JProgressBar.class;
                }
            }
        };
        model.setColumnIdentifiers(columnNames);
        setModel(model);

        pane = new JScrollPane(this);

        getColumn("Progress").setCellRenderer(new progressBar());
    }

    public void addRow(Chapter row) {
        Object[] rowData = {row.name, row.url, 0};
        model.addRow(rowData);
        data.add(row);
    }

    public void addManyRows(ArrayList<Chapter> rows) {
        for(Chapter row:rows) {
            addRow(row);
        }
    }

    public void updateProgress(double complete, double total, int row) {
        double percentageComplete = (complete / total) * 100D;
        setValueAt((int)percentageComplete, row, 2);
    }

    public ArrayList<Chapter> getAsChapters() {
        return data;
    }

}
