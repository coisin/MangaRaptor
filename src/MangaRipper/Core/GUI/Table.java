package MangaRipper.Core.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Created by oduibhir on 25/09/16.
 */
public abstract class Table<E> extends JTable {

    public String[] columnNames;
    public JScrollPane pane;
    public DefaultTableModel model;

    public ArrayList<E> data;

    public Table() {
        data = new ArrayList<E>();
    }

    public JScrollPane getPane() {
        return pane;
    }

    public void addRow(Object[] row) {
        model.addRow(row);
    }

    public ArrayList<Object[]> getRowsWhereColumnEquals(ArrayList<E> rows, int colIndex, Object targetValue) {
        int numRows = model.getRowCount();
        int numCols = model.getColumnCount();

        ArrayList<Object[]> result = new ArrayList();

        for(int i = 0;i<numRows;i++) {
            if(model.getValueAt(i, colIndex).equals(targetValue)) {
                Object[] row = new Object[numCols + 1];
                for(int j = 0;j<numCols;j++) {
                    row[j] = model.getValueAt(i, j);
                }
                row[numCols] = i;
                result.add(row);
            }
        }
        return result;
    }

    public ArrayList<Object[]> getResultsWhereColumnEquals(int colIndex, Object targetValue) {
        return getRowsWhereColumnEquals(data, colIndex, targetValue);
    }

    abstract void addRow(E row);
    abstract void addManyRows(ArrayList<E> rows);

}
