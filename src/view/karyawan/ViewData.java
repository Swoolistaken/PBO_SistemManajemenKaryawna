package view.karyawan;

import controller.ControllerKaryawan;
import model.karyawan.ModelKaryawan;
import model.karyawan.ModelTable;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * View utama daftar karyawan
 * Implementasi: GUI SWING, MULTITHREAD (async load)
 */
public class ViewData extends JPanel {

    private final ControllerKaryawan controller;
    private final ModelTable tableModel = new ModelTable();

    private JTable table;
    private JTextField txtCari;
    private JComboBox<String> cboDept;
    private JLabel lblStatus;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh;

    // Callback ke parent frame
    public interface ActionCallback {
        void onTambah();
        void onEdit(ModelKaryawan k);
        void onLihatKPI(ModelKaryawan k);
        void onLihatAbsensi(ModelKaryawan k);
    }

    private ActionCallback callback;

    public ViewData(ControllerKaryawan controller) {
        this.controller = controller;
        initComponents();
        loadData();
    }

    public void setCallback(ActionCallback callback) {
        this.callback = callback;
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(30, 33, 43));

        add(buatPanelHeader(), BorderLayout.NORTH);
        add(buatPanelTabel(), BorderLayout.CENTER);
        add(buatPanelStatus(), BorderLayout.SOUTH);
    }

    private JPanel buatPanelHeader() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 33, 43));
        panel.setBorder(new EmptyBorder(15, 20, 10, 20));

        // Judul
        JLabel lblJudul = new JLabel("Data Karyawan");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setForeground(new Color(230, 235, 255));

        // Panel toolbar kanan
        JPanel panelKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelKanan.setOpaque(false);

        // Filter departemen
        cboDept = new JComboBox<>(new String[]{
            "Semua", "IT", "HRD", "Finance", "Marketing",
            "Operations", "Legal", "Procurement", "R&D"
        });
        cboDept.setBackground(Color.WHITE);
        cboDept.setForeground(Color.BLACK);
        cboDept.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboDept.setPreferredSize(new Dimension(140, 32));
        cboDept.addActionListener(e -> filterByDept());

        // Search
        txtCari = new JTextField(18);
        txtCari.setBackground(Color.WHITE);
        txtCari.setForeground(Color.BLACK);
        txtCari.setCaretColor(Color.BLACK);
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.setPreferredSize(new Dimension(200, 32));
        txtCari.putClientProperty("JTextField.placeholderText", "Cari nama/NIK/jabatan...");
        txtCari.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { cariData(); }
        });

        // Tombol
        btnTambah  = buatTombol("+ Tambah",  new Color(34, 139, 34));
        btnEdit    = buatTombol("✎ Edit",     new Color(30, 100, 200));
        btnHapus   = buatTombol("✕ Hapus",   new Color(200, 50, 50));
        btnRefresh = buatTombol("↺ Refresh", new Color(100, 100, 120));

        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);

        btnTambah.addActionListener(e -> { if (callback != null) callback.onTambah(); });
        btnEdit.addActionListener(e -> editSelected());
        btnHapus.addActionListener(e -> hapusSelected());
        btnRefresh.addActionListener(e -> loadData());

        JLabel lblDept = new JLabel("Dept:");
lblDept.setForeground(Color.WHITE);
panelKanan.add(lblDept);
        panelKanan.add(cboDept);
        panelKanan.add(txtCari);
        panelKanan.add(btnTambah);
        panelKanan.add(btnEdit);
        panelKanan.add(btnHapus);
        panelKanan.add(btnRefresh);

        panel.add(lblJudul, BorderLayout.WEST);
        panel.add(panelKanan, BorderLayout.EAST);
        return panel;
    }

    private JPanel buatPanelTabel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setBorder(new CompoundBorder(
            new EmptyBorder(0, 15, 10, 15),
            new LineBorder(new Color(55, 65, 90), 1, true)
        ));

        table = new JTable(tableModel);
        table.setBackground(new Color(42, 46, 60));
table.setForeground(new Color(210, 215, 230));
table.setSelectionBackground(new Color(50, 75, 130));
table.setSelectionForeground(Color.WHITE);
table.setGridColor(new Color(55, 65, 90));
table.setRowHeight(36);
table.setShowGrid(false);
table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Color.BLACK);
        table.setSelectionForeground(Color.BLACK);
       table.getTableHeader().setDefaultRenderer(
    new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            lbl.setOpaque(true);
            lbl.setBackground(new Color(35, 50, 100));
            lbl.setForeground(Color.WHITE);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

            return lbl;
        }
    }
);

table.getTableHeader().setPreferredSize(new Dimension(0, 40));
table.getTableHeader().setReorderingAllowed(false);
    

        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? new Color(42, 46, 60) : new Color(38, 41, 54));
                }
                // Status coloring
                if (col == 8 && val != null) {
                    String status = val.toString();
                    if (status.equals("AKTIF")) c.setForeground(new Color(0, 140, 0));
                    else if (status.equals("NONAKTIF")) c.setForeground(new Color(200, 0, 0));
                    else c.setForeground(new Color(180, 120, 0));
                } else {
                    c.setForeground(new Color(210, 215, 230));
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        // Column widths
        int[] widths = {40, 100, 160, 150, 130, 100, 150, 160, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Row selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = table.getSelectedRow() >= 0;
            btnEdit.setEnabled(selected);
            btnHapus.setEnabled(selected);
        });

        // Double click untuk edit
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        // Right-click context menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem miEdit    = new JMenuItem("✎ Edit Data");
        JMenuItem miKPI     = new JMenuItem("📊 Lihat KPI");
        JMenuItem miAbsensi = new JMenuItem("📋 Lihat Absensi");
        JMenuItem miHapus   = new JMenuItem("✕ Hapus");
        miHapus.setForeground(Color.RED);

        miEdit.addActionListener(e -> editSelected());
        miKPI.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && callback != null) callback.onLihatKPI(tableModel.getKaryawan(row));
        });
        miAbsensi.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && callback != null) callback.onLihatAbsensi(tableModel.getKaryawan(row));
        });
        miHapus.addActionListener(e -> hapusSelected());

        popup.add(miEdit);
        popup.add(miKPI);
        popup.add(miAbsensi);
        popup.addSeparator();
        popup.add(miHapus);

        table.setComponentPopupMenu(popup);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelStatus() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(30, 33, 43));
        panel.setBorder(new EmptyBorder(0, 20, 10, 20));

        lblStatus = new JLabel("Memuat data...");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(160, 170, 200));
        panel.add(lblStatus);
        return panel;
    }

    private JButton buatTombol(String teks, Color bg) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    // ===================================================================
    // ===== Data operations =====
    // ===================================================================

    public void loadData() {
        lblStatus.setText("Memuat data...");
        controller.loadAllKaryawanAsync(new ControllerKaryawan.DataListener() {
            @Override public void onSuccess(String pesan) {}
            @Override public void onError(String pesan) {
                lblStatus.setText("Error: " + pesan);
                JOptionPane.showMessageDialog(ViewData.this, pesan, "Error DB", JOptionPane.ERROR_MESSAGE);
            }
            @Override public void onDataLoaded(List<ModelKaryawan> data) {
                tableModel.setData(data);
                lblStatus.setText("Total: " + data.size() + " karyawan ditemukan");
            }
        });
    }

    private void cariData() {
        String keyword = txtCari.getText().trim();
        controller.cariKaryawanAsync(keyword, new ControllerKaryawan.DataListener() {
            @Override public void onSuccess(String pesan) {}
            @Override public void onError(String pesan) { lblStatus.setText("Error: " + pesan); }
            @Override public void onDataLoaded(List<ModelKaryawan> data) {
                tableModel.setData(data);
                lblStatus.setText(data.size() + " hasil ditemukan untuk: \"" + keyword + "\"");
            }
        });
    }

    private void filterByDept() {
        String dept = (String) cboDept.getSelectedItem();
        controller.loadKaryawanByDeptAsync(dept, new ControllerKaryawan.DataListener() {
            @Override public void onSuccess(String p) {}
            @Override public void onError(String p) { lblStatus.setText("Error: " + p); }
            @Override public void onDataLoaded(List<ModelKaryawan> data) {
                tableModel.setData(data);
                lblStatus.setText(data.size() + " karyawan di departemen: " + dept);
            }
        });
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!"); return; }
        if (callback != null) callback.onEdit(tableModel.getKaryawan(row));
    }

    private void hapusSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!"); return; }
        ModelKaryawan k = tableModel.getKaryawan(row);
        int konfirm = JOptionPane.showConfirmDialog(this,
            "Yakin hapus karyawan \"" + k.getNama() + "\"?\nData KPI dan absensi terkait juga akan terhapus!",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (konfirm != JOptionPane.YES_OPTION) return;

        controller.hapusKaryawanAsync(k.getId(), new ControllerKaryawan.DataListener() {
            @Override public void onSuccess(String p) {
                JOptionPane.showMessageDialog(ViewData.this, p, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
            @Override public void onError(String p) {
                JOptionPane.showMessageDialog(ViewData.this, p, "Error", JOptionPane.ERROR_MESSAGE);
            }
            @Override public void onDataLoaded(List<ModelKaryawan> d) {}
        });
    }
}
