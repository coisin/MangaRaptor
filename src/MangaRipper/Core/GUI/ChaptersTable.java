package MangaRipper.Core.GUI;

import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.StringPair;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by oduibhir on 25/09/16.
 */
public class ChaptersTable extends Table<Chapter> {

    public ChaptersTable() {

        super();

        columnNames = new String[]{"Name", "Download"};

        model = new DefaultTableModel() {
            public Class getColumnClass(int column) {
                switch(column) {
                    case 0: return String.class;
                    default: return Boolean.class;
                }
            }
        };
        model.setColumnIdentifiers(columnNames);
        setModel(model);

        pane = new JScrollPane(this);
        pane.setPreferredSize(new Dimension(300, 150));

    }

    public void selectAll() {
        int numRows = model.getRowCount();

        for(int i = 0;i<numRows;i++) {
            model.setValueAt(true, i, 1);
        }
    }

    public void addRow(Chapter row) {
        Object[] rowData = {row.name, false};
        model.addRow(rowData);
        data.add(row);
    }

    public void addManyRows(ArrayList<Chapter> rows) {
        Object[] rowData;
        for(Chapter row:rows) {
            addRow(row);
        }
    }

    public ArrayList<Chapter> getDownloads() {
        ArrayList<Chapter> chapters = new ArrayList();
        ArrayList<Object[]> downloads = getResultsWhereColumnEquals(1, true);

        for(Object[] row:downloads) {
            chapters.add(data.get((Integer)row[model.getColumnCount()]));
        }

        return chapters;
    }

}
