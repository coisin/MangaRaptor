package MangaRipper.Core.GUI;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by oduibhir on 25/09/16.
 */
public class progressBar extends JProgressBar implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        double progress = 0;
        if (value instanceof Float) {
            progress = Math.round(((Float) value) * 100f);
        } else if (value instanceof Double) {
            progress = (double)value;
        }
        System.out.println(value);
        setValue((int)Math.ceil(progress));
        return this;

    }

}
