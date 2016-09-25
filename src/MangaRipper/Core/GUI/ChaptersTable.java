package MangaRipper.Core.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * Created by oduibhir on 25/09/16.
 */
public class ChaptersTable extends JTable {

    JScrollPane pane;

    private String[] columnNames = {"Name", "Download"};
    private DefaultTableModel model = new DefaultTableModel() {
        public Class getColumnClass(int column) {
            switch(column) {
                case 0: return String.class;
                default: return Boolean.class;
            }
        }
    };
    public ChaptersTable() {

        pane = new JScrollPane(this);
        pane.setPreferredSize(new Dimension(300, 150));

        setModel(model);
        model.setColumnIdentifiers(columnNames);

        TableColumn column;
        for(int i = 0;i<2;i++) {
            column = getColumnModel().getColumn(i);
            if(i == 0) {
                column.setPreferredWidth(100);
            } else {
                column.setPreferredWidth(50);
            }
        }
    }

    public void addRow(Object[] row) {
        model.addRow(row);
    }

    public JScrollPane getPane() {
        return pane;
    }

}
