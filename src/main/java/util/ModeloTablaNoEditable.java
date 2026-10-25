package util;

import javax.swing.table.DefaultTableModel;

public class ModeloTablaNoEditable extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
