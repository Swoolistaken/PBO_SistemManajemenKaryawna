package view.Absensi;

import controller.ControllerAbsensi;
import controller.ControllerKaryawan;
import model.absensi.ModelAbsensi;
import model.karyawan.ModelKaryawan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ViewAbsensi extends JPanel {

    private final ControllerKaryawan controllerKaryawan;
    private final ControllerAbsensi controllerAbsensi;
    private List<ModelKaryawan> daftarKaryawan = new ArrayList<>();

    private JComboBox<String> cboKaryawan, cboStatus;
    private JTextField txtTanggal, txtJamMasuk, txtJamKeluar, txtKeterangan;
    private JCheckBox chkTerlambat, chkPulangAwal;
    private JSpinner spnMenit;
    private JButton btnSimpan, btnRefresh;

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
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buatForm(), buatTabel());
        split.setDividerLocation(320);
        add(split, BorderLayout.CENTER);
    }

    private JScrollPane buatForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Catat Absensi"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        // ===== FIX POINT 4: anchor NORTH agar semua naik ke atas =====
        gbc.anchor = GridBagConstraints.NORTH;

        int row = 0;

        cboKaryawan = new JComboBox<>();
        cboStatus = new JComboBox<>(new String[]{
            "HADIR", "IZIN", "SAKIT", "ALPHA", "CUTI", "DINAS_LUAR", "WORK_FROM_HOME"
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtTanggal = new JTextField(sdf.format(new Date()));
        txtJamMasuk = new JTextField("08:00");
        txtJamKeluar = new JTextField("17:00");
        txtKeterangan = new JTextField();

        // ===== FIX POINT 2: mutual exclusive checkbox =====
        chkTerlambat = new JCheckBox("Terlambat");
        chkPulangAwal = new JCheckBox("Pulang Lebih Awal");

        chkTerlambat.addItemListener(e -> {
            if (chkTerlambat.isSelected()) {
                chkPulangAwal.setSelected(false);
                spnMenit.setEnabled(true);
            } else {
                spnMenit.setEnabled(false);
            }
        });

        chkPulangAwal.addItemListener(e -> {
            if (chkPulangAwal.isSelected()) {
                chkTerlambat.setSelected(false);
                spnMenit.setEnabled(false);
            }
        });

        // ===== FIX POINT 1: Spinner angka only, tidak bisa ketik teks =====
        spnMenit = new JSpinner(new SpinnerNumberModel(0, 0, 480, 5));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnMenit, "##0");
        spnMenit.setEditor(editor);
        // Block input non-angka
        JFormattedTextField spnTextField = editor.getTextField();
        spnTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // buang karakter bukan angka
                }
            }
        });
        spnMenit.setEnabled(false); // default disabled sampai chkTerlambat dicentang

        row = addRow(panel, gbc, row, "Karyawan *", cboKaryawan);
        row = addRow(panel, gbc, ++row, "Tanggal (dd/MM/yyyy)", txtTanggal);
        row = addRow(panel, gbc, ++row, "Status", cboStatus);
        row = addRow(panel, gbc, ++row, "Jam Masuk (HH:mm)", txtJamMasuk);
        row = addRow(panel, gbc, ++row, "Jam Keluar (HH:mm)", txtJamKeluar);
        row = addRow(panel, gbc, ++row, "Keterangan", txtKeterangan);
        row = addRow(panel, gbc, ++row, "Menit Terlambat", spnMenit);

        // Checkbox row
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        JPanel panelCheck = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelCheck.add(chkTerlambat);
        panelCheck.add(Box.createHorizontalStrut(15));
        panelCheck.add(chkPulangAwal);
        panel.add(panelCheck, gbc);
        gbc.gridwidth = 1;

        // Tombol
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        btnSimpan = new JButton("Catat Absensi");
        btnSimpan.addActionListener(e -> simpanAbsensi());
        panel.add(btnSimpan, gbc);

        // ===== FIX POINT 4: panel filler agar semua naik ke atas =====
        gbc.gridy = ++row;
        gbc.weighty = 1.0; // dorong semua komponen ke atas
        panel.add(new JLabel(), gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private JPanel buatTabel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Rekap Absensi"));

        String[] kolom = {"No", "Nama", "Tanggal", "Jam Masuk", "Jam Keluar", "Status", "Terlambat", "Keterangan"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabel = new JTable(modelTabel);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblInfo = new JLabel("Total: 0 catatan");
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadAbsensi());
        bawah.add(lblInfo);
        bawah.add(btnRefresh);
        panel.add(bawah, BorderLayout.SOUTH);

        return panel;
    }

    private int addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(comp, gbc);
        return row;
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
            JOptionPane.showMessageDialog(this, "Gagal memuat karyawan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
                lblInfo.setText("Total: " + data.size() + " catatan");
            }
        });
    }

    private void simpanAbsensi() {
        int idx = cboKaryawan.getSelectedIndex() - 1;
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!");
            return;
        }

        // ===== FIX POINT 3: validasi format jam HH:mm =====
        String jamMasuk = txtJamMasuk.getText().trim();
        String jamKeluar = txtJamKeluar.getText().trim();

        if (!jamMasuk.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            JOptionPane.showMessageDialog(this,
                    "Format jam masuk tidak valid!\nGunakan format HH:mm, contoh: 08:00",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            txtJamMasuk.requestFocus();
            return;
        }
        if (!jamKeluar.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            JOptionPane.showMessageDialog(this,
                    "Format jam keluar tidak valid!\nGunakan format HH:mm, contoh: 17:00",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            txtJamKeluar.requestFocus();
            return;
        }

        ModelKaryawan k = daftarKaryawan.get(idx);
        ModelAbsensi a = new ModelAbsensi(k.getId(), k.getNik(), k.getNama());

        try {
            a.setTanggal(new SimpleDateFormat("dd/MM/yyyy").parse(txtTanggal.getText().trim()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Format tanggal tidak valid!\nGunakan format dd/MM/yyyy, contoh: 01/01/2025",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            txtTanggal.requestFocus();
            return;
        }

        a.setJamMasuk(jamMasuk);
        a.setJamKeluar(jamKeluar);
        a.setKeterangan(txtKeterangan.getText().trim());
        a.setTerlambat(chkTerlambat.isSelected());
        a.setMenitTerlambat((Integer) spnMenit.getValue());
        a.setPulangAwal(chkPulangAwal.isSelected());

        try {
            a.setStatus(ModelAbsensi.StatusAbsensi.valueOf((String) cboStatus.getSelectedItem()));
        } catch (Exception ex) {
            a.setStatus(ModelAbsensi.StatusAbsensi.HADIR);
        }

        controllerAbsensi.simpanAbsensiAsync(a, new ControllerAbsensi.AbsensiListener() {
            @Override
            public void onSuccess(String p) {
                JOptionPane.showMessageDialog(ViewAbsensi.this, p);
                loadAbsensi();
                resetForm();
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

    private void resetForm() {
        cboKaryawan.setSelectedIndex(0);
        txtTanggal.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtJamMasuk.setText("08:00");
        txtJamKeluar.setText("17:00");
        txtKeterangan.setText("");
        chkTerlambat.setSelected(false);
        chkPulangAwal.setSelected(false);
        spnMenit.setValue(0);
        spnMenit.setEnabled(false);
        cboStatus.setSelectedIndex(0);
    }

    public void refreshKaryawan() {
        loadKaryawan();
    }
}
