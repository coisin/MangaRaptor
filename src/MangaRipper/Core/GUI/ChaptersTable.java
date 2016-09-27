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

        columnNames = new String[]{"Name"};

        model = new DefaultTableModel() {
            public Class getColumnClass(int column) {
                switch(column) {
                    default: return String.class;
                }
            }
        };
        model.setColumnIdentifiers(columnNames);
        setModel(model);

        pane.setPreferredSize(new Dimension(300, 150));

    }

    public void addRow(Chapter row) {
        Object[] rowData = {row.name};
        model.addRow(rowData);
        data.add(row);
    }

    public void addManyRows(ArrayList<Chapter> rows) {
        Object[] rowData;
        for(Chapter row:rows) {
            addRow(row);
        }
    }

    public int getIndexInData(String value) {
        for(int i = 0;i<data.size();i++) {
            if(data.get(i).name.equals(value))return i;
        }
        return -1;
    }

    public ArrayList<Chapter> getDownloads() {
        ArrayList<Chapter> chapters = new ArrayList();
        int[] selectedRows = getSelectedRows();
        Object[][] downloads = new Object[selectedRows.length][model.getColumnCount() + 1];
        for(int i = 0;i<downloads.length;i++) {
            for(int j = 0;j<model.getColumnCount();j++) {
                downloads[i][j] = model.getValueAt(selectedRows[i], j);
            }
            downloads[i][model.getColumnCount()] = getIndexInData((String)downloads[i][0]);
        }

        for(Object[] row:downloads) {
            chapters.add(data.get((Integer)row[model.getColumnCount()]));
        }

        return chapters;
    }

}
