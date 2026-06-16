package model.karyawan;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModelTable extends AbstractTableModel {

    private final String[] KOLOM = {
        "No", "NIK", "Nama", "Jabatan", "Departemen",
        "Level", "Gaji Pokok", "Total Gaji", "Status"
    };

    private List<ModelKaryawan> data = new ArrayList<>();
    private final NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public void setData(List<ModelKaryawan> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public void addRow(ModelKaryawan k) {
        data.add(k);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void removeRow(int rowIndex) {
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public ModelKaryawan getKaryawan(int rowIndex) {
        return data.get(rowIndex);
    }

    public void clear() {
        data.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return KOLOM.length;
    }

    @Override
    public String getColumnName(int col) {
        return KOLOM[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        ModelKaryawan k = data.get(row);
        switch (col) {
            case 0:
                return row + 1;
            case 1:
                return k.getNik();
            case 2:
                return k.getNama();
            case 3:
                return k.getJabatan();
            case 4:
                return k.getDepartemen();
            case 5:
                return getLevelLabel(k.getLevelJabatan());
            case 6:
                return rupiah.format(k.getGajiPokok());
            case 7:
                return rupiah.format(k.hitungTotalGaji());
            case 8:
                return k.getStatus();
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    private String getLevelLabel(int level) {
        switch (level) {
            case 4:
                return "Director";
            case 3:
                return "Manager";
            case 2:
                return "Supervisor";
            default:
                return "Staff";
        }
    }
}
