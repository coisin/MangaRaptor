package MangaRipper.Core.GUI;

import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Series;
import javafx.scene.control.Tab;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Created by oduibhir on 27/09/16.
 */
public class SearchResultsTable extends Table<Series> {

    public SearchResultsTable() {
        super();

        columnNames = new String[] {"Series Name"};

        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        setModel(model);
    }

    public void addRow(Series series) {
        model.addRow(new Object[] {series.name});
        data.add(series);
    }
    public void addManyRows(ArrayList<Series> series) {
        for(Series i:series) {
            addRow(i);
        }
    }
    public ArrayList<Series> getCheckedSeries() {
        ArrayList<Series> series = new ArrayList();
        int[] selectedRows = getSelectedRows();
        Object[][] downloads = new Object[selectedRows.length][model.getColumnCount() + 1];
        for(int i = 0;i<downloads.length;i++) {
            for(int j = 0;j<model.getColumnCount();j++) {
                downloads[i][j] = model.getValueAt(selectedRows[i], j);
            }
            downloads[i][model.getColumnCount()] = getIndexInData((String)downloads[i][0]);
        }

        for(Object[] row:downloads) {
            series.add(data.get((Integer)row[model.getColumnCount()]));
        }

        return series;
    }

    public int getIndexInData(String value) {
        for(int i = 0;i<data.size();i++) {
            if(data.get(i).name.equals(value))return i;
        }
        return -1;
    }

}
