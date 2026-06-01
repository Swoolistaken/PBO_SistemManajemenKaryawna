package view.Absensi;

import controller.*;
import model.karyawan.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import model.absensi.ModelAbsensi;

/**
 * View Absensi Karyawan
 */
public class ViewAbsensi extends JPanel {

    private final ControllerKaryawan controllerKaryawan;
    private final ControllerAbsensi controllerAbsensi;
    private List<ModelKaryawan> daftarKaryawan = new ArrayList<>();

    private JComboBox<String> cboKaryawan;
    private JComboBox<String> cboStatus;
    private JTextField txtTanggal, txtJamMasuk, txtJamKeluar, txtKeterangan;
    private JCheckBox chkTerlambat, chkPulangAwal;
    private JSpinner spnMenitTerlambat;
    private JButton btnSimpan, btnHapus, btnRefresh;
    private JTable tabel;
    private DefaultTableModel modelTabel;
    private JLabel lblInfo;

    public ViewAbsensi(ControllerKaryawan controllerKaryawan, ControllerAbsensi controllerAbsensi) {
        this.controllerKaryawan = controllerKaryawan;
        this.controllerAbsensi = controllerAbsensi;
        initUI();
        loadKaryawan();
        loadAbsensi();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 33, 43));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buatFormAbsensi(), buatTabelAbsensi());
        split.setDividerLocation(380);
        split.setBorder(new EmptyBorder(10, 15, 10, 15));
        add(split, BorderLayout.CENTER);
    }

    private JScrollPane buatFormAbsensi() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(42, 46, 60));
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(55, 65, 90), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel judul = new JLabel("📋 Catat Absensi");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 16));
        judul.setForeground(new Color(180, 205, 255));
        judul.setBorder(new MatteBorder(0, 0, 1, 0, new Color(55, 70, 110)));
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        judul.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(judul);
        panel.add(Box.createVerticalStrut(12));

        cboKaryawan = new JComboBox<>();
        cboStatus = new JComboBox<>(new String[]{
            "HADIR", "IZIN", "SAKIT", "ALPHA", "CUTI", "DINAS_LUAR", "WORK_FROM_HOME"
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtTanggal = new JTextField(sdf.format(new Date()));
        txtJamMasuk = new JTextField("08:00");
        txtJamKeluar = new JTextField("17:00");
        txtKeterangan = new JTextField();

        chkTerlambat = new JCheckBox("Terlambat");
        chkTerlambat.setBackground(new Color(42, 46, 60));

        chkPulangAwal = new JCheckBox("Pulang Lebih Awal");
        chkPulangAwal.setBackground(new Color(42, 46, 60));
        chkTerlambat.setForeground(Color.WHITE);
        chkPulangAwal.setForeground(Color.WHITE);
        spnMenitTerlambat = new JSpinner(new SpinnerNumberModel(0, 0, 480, 5));

        panel.add(row("Karyawan *:", cboKaryawan));
        panel.add(Box.createVerticalStrut(6));
        panel.add(row("Tanggal:", txtTanggal));
        panel.add(Box.createVerticalStrut(6));
        panel.add(row("Status:", cboStatus));
        panel.add(Box.createVerticalStrut(6));
        panel.add(row("Jam Masuk:", txtJamMasuk));
        panel.add(Box.createVerticalStrut(6));
        panel.add(row("Jam Keluar:", txtJamKeluar));
        panel.add(Box.createVerticalStrut(6));
        panel.add(row("Keterangan:", txtKeterangan));
        panel.add(Box.createVerticalStrut(8));

        JPanel panelCheck = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelCheck.setBackground(new Color(42, 46, 60));
        panelCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCheck.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panelCheck.add(chkTerlambat);
        panelCheck.add(Box.createHorizontalStrut(15));
        panelCheck.add(chkPulangAwal);
        panel.add(panelCheck);
        panel.add(Box.createVerticalStrut(6));
        panel.add(row("Menit Terlambat:", spnMenitTerlambat));
        panel.add(Box.createVerticalStrut(12));

        lblInfo = new JLabel(" ");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(160, 170, 200));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblInfo);
        panel.add(Box.createVerticalStrut(8));

        JPanel tombolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        tombolPanel.setBackground(new Color(42, 46, 60));
        tombolPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tombolPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnHapus = btn("Hapus", new Color(200, 50, 50));
        btnSimpan = btn("Catat", new Color(34, 139, 34));
        btnHapus.setEnabled(false);
        btnSimpan.addActionListener(e -> simpanAbsensi());
        btnHapus.addActionListener(e -> hapusAbsensi());

        tombolPanel.add(btnHapus);
        tombolPanel.add(btnSimpan);
        panel.add(tombolPanel);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel buatTabelAbsensi() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(42, 46, 60));
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(55, 65, 90), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel judul = new JLabel("📅 Rekap Absensi");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 15));
        judul.setForeground(new Color(180, 205, 255));
        judul.setBorder(new MatteBorder(0, 0, 1, 0, new Color(55, 70, 110)));
        panel.add(judul, BorderLayout.NORTH);

        String[] kolom = {"No", "Nama Karyawan", "Tanggal", "Jam Masuk", "Jam Keluar", "Status", "Terlambat", "Keterangan"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabel = new JTable(modelTabel);
        tabel.setBackground(new Color(42, 46, 60));
        tabel.setForeground(new Color(210, 215, 230));
        tabel.setSelectionBackground(new Color(50, 75, 130));
        tabel.setSelectionForeground(Color.WHITE);
        tabel.setShowGrid(false);
        tabel.setIntercellSpacing(new Dimension(0, 0));

        tabel.setDefaultRenderer(Object.class,
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean selected,
                    boolean focused,
                    int row,
                    int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);

                if (!selected) {
                    lbl.setBackground(
                            row % 2 == 0
                                    ? new Color(42, 46, 60)
                                    : new Color(38, 41, 54)
                    );
                }

                lbl.setForeground(new Color(210, 215, 230));
                lbl.setBorder(new EmptyBorder(0, 8, 0, 8));

                return lbl;
            }
        }
        );
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabel.setRowHeight(30);
        tabel.getTableHeader().setDefaultRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
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
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

                return lbl;
            }
        }
        );

        tabel.getTableHeader().setPreferredSize(new Dimension(0, 35));
        tabel.getSelectionModel().addListSelectionListener(e -> btnHapus.setEnabled(tabel.getSelectedRow() >= 0));

        JScrollPane scroll = new JScrollPane(tabel);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBawah.setBackground(new Color(42, 46, 60));
        btnRefresh = btn("↺ Refresh", new Color(80, 100, 160));
        btnRefresh.addActionListener(e -> loadAbsensi());
        panelBawah.add(btnRefresh);
        panel.add(panelBawah, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel row(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setPreferredSize(new Dimension(140, 20));
        p.setBackground(new Color(42, 46, 60));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setPreferredSize(new Dimension(140, 20));
        if (comp instanceof JTextField) {
            ((JTextField) comp).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        if (comp instanceof JComboBox) {
            ((JComboBox<?>) comp).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        p.add(lbl, BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JButton btn(String teks, Color bg) {
        JButton b = new JButton(teks);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(new EmptyBorder(6, 12, 6, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    private void loadKaryawan() {
        try {
            daftarKaryawan = controllerKaryawan.getAllKaryawan();
            cboKaryawan.removeAllItems();
            cboKaryawan.addItem("-- Pilih Karyawan --");
            for (ModelKaryawan k : daftarKaryawan) {
                cboKaryawan.addItem("[" + k.getNik() + "] " + k.getNama());
            }
        } catch (Exception e) {
            lblInfo.setText("Error: " + e.getMessage());
        }
    }

    private void loadAbsensi() {
        controllerAbsensi.loadAbsensiAsync(new ControllerAbsensi.AbsensiListener() {
            @Override
            public void onSuccess(String p) {
            }

            @Override
            public void onError(String p) {
                lblInfo.setText("Error: " + p);
            }

            @Override
            public void onDataLoaded(List<ModelAbsensi> data) {
                modelTabel.setRowCount(0);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                int no = 1;
                for (ModelAbsensi a : data) {
                    modelTabel.addRow(new Object[]{
                        no++,
                        a.getNamaKaryawan(),
                        a.getTanggal() != null ? sdf.format(a.getTanggal()) : "-",
                        a.getJamMasuk() != null ? a.getJamMasuk() : "-",
                        a.getJamKeluar() != null ? a.getJamKeluar() : "-",
                        a.getStatus().name(),
                        a.isTerlambat() ? a.getMenitTerlambat() + " menit" : "Tidak",
                        a.getKeterangan() != null ? a.getKeterangan() : ""
                    });
                }
                lblInfo.setText("Total: " + data.size() + " catatan absensi");
            }
        });
    }

    private void simpanAbsensi() {
        int idx = cboKaryawan.getSelectedIndex() - 1;
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!");
            return;
        }
        ModelKaryawan k = daftarKaryawan.get(idx);
        ModelAbsensi a = new ModelAbsensi(k.getId(), k.getNik(), k.getNama());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            a.setTanggal(sdf.parse(txtTanggal.getText().trim()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format tanggal: dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        a.setJamMasuk(txtJamMasuk.getText().trim());
        a.setJamKeluar(txtJamKeluar.getText().trim());
        try {
            a.setStatus(ModelAbsensi.StatusAbsensi.valueOf((String) cboStatus.getSelectedItem()));
        } catch (Exception ex) {
            a.setStatus(ModelAbsensi.StatusAbsensi.HADIR);
        }
        a.setKeterangan(txtKeterangan.getText().trim());
        a.setTerlambat(chkTerlambat.isSelected());
        a.setMenitTerlambat((Integer) spnMenitTerlambat.getValue());
        a.setPulangAwal(chkPulangAwal.isSelected());

        controllerAbsensi.simpanAbsensiAsync(a, new ControllerAbsensi.AbsensiListener() {
            @Override
            public void onSuccess(String p) {
                JOptionPane.showMessageDialog(ViewAbsensi.this, p, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                loadAbsensi();
            }

            @Override
            public void onError(String p) {
                JOptionPane.showMessageDialog(ViewAbsensi.this, p, "Error", JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void onDataLoaded(List<ModelAbsensi> d) {
            }
        });
    }

    private void hapusAbsensi() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus catatan absensi ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        lblInfo.setText("Fitur hapus memerlukan ID dari tabel. Segera diimplementasi.");
    }

    public void refreshKaryawan() {
        loadKaryawan();
    }
}
