package view.karyawan;

import controller.ControllerKaryawan;
import model.karyawan.ModelKaryawan;
import model.karyawan.ModelTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ViewData extends JPanel {

    private final ControllerKaryawan controller;
    private final ModelTable tableModel = new ModelTable();

    private JTable table;
    private JTextField txtCari;
    private JComboBox<String> cboDept;
    private JLabel lblStatus;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh;

    public interface ActionCallback {

        void onTambah();

        void onEdit(ModelKaryawan k);

        void onLihatKPI(ModelKaryawan k);

        void onLihatAbsensi(ModelKaryawan k);
    }

    private ActionCallback callback;

    public ViewData(ControllerKaryawan controller) {
        this.controller = controller;
        initUI();
        loadData();
    }

    public void setCallback(ActionCallback callback) {
        this.callback = callback;
    }

    public void setAksesHapus(boolean boleh) {
        btnHapus.setVisible(boleh);
    }

    public void setAksesTambah(boolean boleh) {
        btnTambah.setVisible(boleh);
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buatToolbar(), BorderLayout.NORTH);
        add(buatTabel(), BorderLayout.CENTER);
        add(buatStatus(), BorderLayout.SOUTH);
    }

    private JPanel buatToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        cboDept = new JComboBox<>(new String[]{
            "Semua", "IT", "HRD", "Finance", "Marketing",
            "Operations", "Legal", "Procurement", "R&D"
        });
        cboDept.addActionListener(e -> filterByDept());

        txtCari = new JTextField(20);
        txtCari.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                cariData();
            }
        });

        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");

        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);

        btnTambah.addActionListener(e -> {
            if (callback != null) {
                callback.onTambah();
            }
        });
        btnEdit.addActionListener(e -> editSelected());
        btnHapus.addActionListener(e -> hapusSelected());
        btnRefresh.addActionListener(e -> loadData());

        p.add(new JLabel("Dept:"));
        p.add(cboDept);
        p.add(new JLabel("Cari:"));
        p.add(txtCari);
        p.add(btnTambah);
        p.add(btnEdit);
        p.add(btnHapus);
        p.add(btnRefresh);
        return p;
    }

    private JScrollPane buatTabel() {
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean ada = table.getSelectedRow() >= 0;
            btnEdit.setEnabled(ada);
            btnHapus.setEnabled(ada);
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelected();
                }
            }
        });

        // Right-click menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem miEdit = new JMenuItem("Edit");
        JMenuItem miKPI = new JMenuItem("Lihat KPI");
        JMenuItem miAbsensi = new JMenuItem("Lihat Absensi");
        JMenuItem miHapus = new JMenuItem("Hapus");

        miEdit.addActionListener(e -> editSelected());
        miKPI.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && callback != null) {
                callback.onLihatKPI(tableModel.getKaryawan(row));
            }
        });
        miAbsensi.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && callback != null) {
                callback.onLihatAbsensi(tableModel.getKaryawan(row));
            }
        });
        miHapus.addActionListener(e -> hapusSelected());

        popup.add(miEdit);
        popup.add(miKPI);
        popup.add(miAbsensi);
        popup.addSeparator();
        popup.add(miHapus);
        table.setComponentPopupMenu(popup);

        return new JScrollPane(table);
    }

    private JPanel buatStatus() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblStatus = new JLabel("Memuat data...");
        p.add(lblStatus);
        return p;
    }

    public void loadData() {
        lblStatus.setText("Memuat data...");
        new Thread(()
                -> controller.loadAllKaryawan(new ControllerKaryawan.DataListener() {
                    @Override
                    public void onSuccess(String p) {
                    }

                    @Override
                    public void onError(String p) {
                        SwingUtilities.invokeLater(() -> {
                            lblStatus.setText("Error: " + p);
                            JOptionPane.showMessageDialog(ViewData.this, p, "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }

                    @Override
                    public void onDataLoaded(List<ModelKaryawan> data) {
                        SwingUtilities.invokeLater(() -> {
                            tableModel.setData(data);
                            lblStatus.setText("Total: " + data.size() + " karyawan");
                        });
                    }
                }),
                 "Thread-LoadKaryawan").start();
    }

    private void cariData() {
        String keyword = txtCari.getText().trim();
        new Thread(()
                -> controller.cariKaryawan(keyword, new ControllerKaryawan.DataListener() {
                    @Override
                    public void onSuccess(String p) {
                    }

                    @Override
                    public void onError(String p) {
                        SwingUtilities.invokeLater(() -> lblStatus.setText("Error: " + p));
                    }

                    @Override
                    public void onDataLoaded(List<ModelKaryawan> data) {
                        SwingUtilities.invokeLater(() -> {
                            tableModel.setData(data);
                            lblStatus.setText(data.size() + " hasil untuk: \"" + keyword + "\"");
                        });
                    }
                }),
                 "Thread-CariKaryawan").start();
    }

    private void filterByDept() {
        String dept = (String) cboDept.getSelectedItem();
        new Thread(()
                -> controller.loadKaryawanByDept(dept, new ControllerKaryawan.DataListener() {
                    @Override
                    public void onSuccess(String p) {
                    }

                    @Override
                    public void onError(String p) {
                        SwingUtilities.invokeLater(() -> lblStatus.setText("Error: " + p));
                    }

                    @Override
                    public void onDataLoaded(List<ModelKaryawan> data) {
                        SwingUtilities.invokeLater(() -> {
                            tableModel.setData(data);
                            lblStatus.setText(data.size() + " karyawan di: " + dept);
                        });
                    }
                }),
                 "Thread-FilterDept").start();
    }

    private void hapusSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!");
            return;
        }
        ModelKaryawan k = tableModel.getKaryawan(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Yakin hapus karyawan \"" + k.getNama() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }

        new Thread(()
                -> controller.hapusKaryawan(k.getId(), new ControllerKaryawan.DataListener() {
                    @Override
                    public void onSuccess(String p) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(ViewData.this, p);
                            loadData();
                        });
                    }

                    @Override
                    public void onError(String p) {
                        SwingUtilities.invokeLater(()
                                -> JOptionPane.showMessageDialog(ViewData.this, p, "Error", JOptionPane.ERROR_MESSAGE));
                    }

                    @Override
                    public void onDataLoaded(List<ModelKaryawan> d) {
                    }
                }),
                 "Thread-HapusKaryawan").start();
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!");
            return;
        }
        if (callback != null) {
            callback.onEdit(tableModel.getKaryawan(row));
        }
    }
}
