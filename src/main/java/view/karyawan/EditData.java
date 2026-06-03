package view.karyawan;

import controller.ControllerKaryawan;
import controller.ControllerKPI;
import model.karyawan.ModelKaryawan;
import model.kpi.ModelKPI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import javax.swing.event.ChangeListener;

public class EditData extends JPanel {

    private final ControllerKaryawan controllerKaryawan;
    private final ControllerKPI controllerKPI;

    private JComboBox<String> cboKaryawan;
    private JComboBox<Integer> cboPeriode;
    private JComboBox<String> cboBulan;
    private JSpinner spnProduktivitas, spnKualitas, spnKehadiran, spnTeamwork, spnInovasi;
    private JLabel lblNilaiAkhir, lblGrade, lblBonus;
    private JTextArea txtCatatan, txtTarget;
    private JTextField txtPenilai;
    private JButton btnSimpan, btnReset;

    private JTable tabel;
    private DefaultTableModel modelTabel;
    private JLabel lblInfo;

    private List<ModelKaryawan> daftarKaryawan = new ArrayList<>();
    private int selectedKaryawanId = -1;
    private double gajiPokokKaryawan = 0;

    private final String[] NAMA_BULAN = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    public EditData(ControllerKaryawan controllerKaryawan, ControllerKPI controllerKPI) {
        this.controllerKaryawan = controllerKaryawan;
        this.controllerKPI = controllerKPI;
        initUI();
        loadKaryawan();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buatPanelForm(), buatPanelRiwayat());
        split.setDividerLocation(400);
        add(split, BorderLayout.CENTER);
    }

    private JScrollPane buatPanelForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Penilaian KPI"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Karyawan
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel("Karyawan *:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboKaryawan = new JComboBox<>();
        cboKaryawan.addActionListener(e -> onKaryawanDipilih());
        panel.add(cboKaryawan, gbc);

        // Periode
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.weightx = 0;
        panel.add(new JLabel("Tahun:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        int tahunIni = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] tahunList = new Integer[10];
        for (int i = 0; i < 10; i++) {
            tahunList[i] = tahunIni - i;
        }
        cboPeriode = new JComboBox<>(tahunList);
        panel.add(cboPeriode, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.weightx = 0;
        panel.add(new JLabel("Bulan:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboBulan = new JComboBox<>(NAMA_BULAN);
        cboBulan.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        panel.add(cboBulan, gbc);

        // Separator
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Spinner nilai
        spnProduktivitas = buatSpinner();
        spnKualitas = buatSpinner();
        spnKehadiran = buatSpinner();
        spnTeamwork = buatSpinner();
        spnInovasi = buatSpinner();

        ChangeListener updateListener = e -> updateHasil();
        spnProduktivitas.addChangeListener(updateListener);
        spnKualitas.addChangeListener(updateListener);
        spnKehadiran.addChangeListener(updateListener);
        spnTeamwork.addChangeListener(updateListener);
        spnInovasi.addChangeListener(updateListener);

        row = addRow(panel, gbc, ++row, "Produktivitas (30%)", spnProduktivitas);
        row = addRow(panel, gbc, ++row, "Kualitas Kerja (25%)", spnKualitas);
        row = addRow(panel, gbc, ++row, "Kehadiran (20%)", spnKehadiran);
        row = addRow(panel, gbc, ++row, "Kerjasama Tim (15%)", spnTeamwork);
        row = addRow(panel, gbc, ++row, "Inovasi (10%)", spnInovasi);

        // Separator
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Hasil
        lblNilaiAkhir = new JLabel("Nilai Akhir: 0.00");
        lblNilaiAkhir.setFont(new Font("Dialog", Font.BOLD, 13));
        lblGrade = new JLabel("Grade: -");
        lblBonus = new JLabel("Estimasi Bonus: Rp 0");

        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        panel.add(lblNilaiAkhir, gbc);
        gbc.gridy = ++row;
        panel.add(lblGrade, gbc);
        gbc.gridy = ++row;
        panel.add(lblBonus, gbc);
        gbc.gridwidth = 1;

        // Separator
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Penilai & catatan
        txtPenilai = new JTextField();
        row = addRow(panel, gbc, ++row, "Penilai *", txtPenilai);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.weightx = 0;
        panel.add(new JLabel("Catatan:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCatatan = new JTextArea(3, 15);
        txtCatatan.setLineWrap(true);
        panel.add(new JScrollPane(txtCatatan), gbc);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.weightx = 0;
        panel.add(new JLabel("Target Berikutnya:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtTarget = new JTextArea(2, 15);
        txtTarget.setLineWrap(true);
        panel.add(new JScrollPane(txtTarget), gbc);

        // Tombol
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        JPanel tombolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnReset = new JButton("Reset");
        btnSimpan = new JButton("Simpan KPI");
        btnReset.addActionListener(e -> resetForm());
        btnSimpan.addActionListener(e -> simpanKPI());
        tombolPanel.add(btnReset);
        tombolPanel.add(btnSimpan);
        panel.add(tombolPanel, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private JPanel buatPanelRiwayat() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Riwayat KPI"));

        String[] kolom = {"No", "Karyawan", "Tahun", "Bulan", "Nilai", "Grade", "Penilai"};
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
        lblInfo = new JLabel("Total: 0 penilaian");
        JButton btnMuat = new JButton("Muat Semua");
        btnMuat.addActionListener(e -> loadSemuaKPI());
        bawah.add(lblInfo);
        bawah.add(btnMuat);
        panel.add(bawah, BorderLayout.SOUTH);

        return panel;
    }

    // ===== Helpers =====
    private JSpinner buatSpinner() {
        return new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    }

    private javax.swing.event.ChangeListener updateListener(Runnable r) {
        return e -> r.run();
    }

    private int addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(comp, gbc);
        return row;
    }

    //Data
    private void loadKaryawan() {
        try {
            daftarKaryawan = controllerKaryawan.getAllKaryawan();
            SwingUtilities.invokeLater(() -> {
                cboKaryawan.removeAllItems();
                cboKaryawan.addItem("-- Pilih Karyawan --");
                for (ModelKaryawan k : daftarKaryawan) {
                    cboKaryawan.addItem("[" + k.getNik() + "] " + k.getNama());
                }
            });
        } catch (Exception e) {
            SwingUtilities.invokeLater(()
                    -> JOptionPane.showMessageDialog(this, "Gagal memuat karyawan: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE));
        }
    }

    private void onKaryawanDipilih() {
        int idx = cboKaryawan.getSelectedIndex() - 1;
        if (idx < 0) {
            selectedKaryawanId = -1;
            return;
        }
        ModelKaryawan k = daftarKaryawan.get(idx);
        selectedKaryawanId = k.getId();
        gajiPokokKaryawan = k.getGajiPokok();
        loadRiwayatKPI(k.getId());
        updateHasil();
    }

    private void loadRiwayatKPI(int karyawanId) {
        new Thread(()
                -> controllerKPI.loadKPIByKaryawan(karyawanId, new ControllerKPI.KPIListener() {
                    @Override
                    public void onSuccess(String p) {
                    }

                    @Override
                    public void onError(String p) {
                        SwingUtilities.invokeLater(()
                                -> JOptionPane.showMessageDialog(EditData.this, p, "Error", JOptionPane.ERROR_MESSAGE));
                    }

                    @Override
                    public void onDataLoaded(List<ModelKPI> data) {
                        SwingUtilities.invokeLater(() -> isiTabel(data));
                    }
                }),
                "Thread-LoadKPI").start();
    }

    private void loadSemuaKPI() {
        new Thread(()
                -> controllerKPI.loadSemuaKPI(new ControllerKPI.KPIListener() {
                    @Override
                    public void onSuccess(String p) {
                    }

                    @Override
                    public void onError(String p) {
                        SwingUtilities.invokeLater(()
                                -> JOptionPane.showMessageDialog(EditData.this, p, "Error", JOptionPane.ERROR_MESSAGE));
                    }

                    @Override
                    public void onDataLoaded(List<ModelKPI> data) {
                        SwingUtilities.invokeLater(() -> {
                            isiTabel(data);
                            lblInfo.setText("Total: " + data.size() + " penilaian (semua periode)");
                        });
                    }
                }),
                "Thread-LoadSemuaKPI").start();
    }

    private void isiTabel(List<ModelKPI> data) {
        modelTabel.setRowCount(0);
        int no = 1;
        for (ModelKPI kpi : data) {
            modelTabel.addRow(new Object[]{
                no++,
                kpi.getNamaKaryawan(),
                kpi.getPeriode(),
                NAMA_BULAN[kpi.getBulan() - 1],
                String.format("%.2f", kpi.hitungNilaiAkhir()),
                kpi.getGradeKPI(),
                kpi.getPenilai()
            });
        }
        lblInfo.setText("Total: " + data.size() + " penilaian");
    }

    private void updateHasil() {
        ModelKPI kpi = buatKPIDariForm();
        double nilai = kpi.hitungNilaiAkhir();
        lblNilaiAkhir.setText(String.format("Nilai Akhir: %.2f / 100", nilai));
        lblGrade.setText("Grade: " + kpi.getGradeKPI());
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        lblBonus.setText("Estimasi Bonus: " + rupiah.format(kpi.getBonusKPI(gajiPokokKaryawan)));
    }

    private ModelKPI buatKPIDariForm() {
        ModelKPI kpi = new ModelKPI();
        kpi.setNilaiProduktivitas((Integer) spnProduktivitas.getValue());
        kpi.setNilaiKualitas((Integer) spnKualitas.getValue());
        kpi.setNilaiKehadiran((Integer) spnKehadiran.getValue());
        kpi.setNilaiTeamwork((Integer) spnTeamwork.getValue());
        kpi.setNilaiInovasi((Integer) spnInovasi.getValue());
        return kpi;
    }

    private void simpanKPI() {
        if (selectedKaryawanId < 0) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!");
            return;
        }
        int idx = cboKaryawan.getSelectedIndex() - 1;
        ModelKaryawan k = daftarKaryawan.get(idx);

        ModelKPI kpi = buatKPIDariForm();
        kpi.setKaryawanId(selectedKaryawanId);
        kpi.setNikKaryawan(k.getNik());
        kpi.setNamaKaryawan(k.getNama());
        kpi.setPeriode((Integer) cboPeriode.getSelectedItem());
        kpi.setBulan(cboBulan.getSelectedIndex() + 1);
        kpi.setCatatanAtasan(txtCatatan.getText().trim());
        kpi.setTargetPeriodeBerikutnya(txtTarget.getText().trim());
        kpi.setPenilai(txtPenilai.getText().trim());
        kpi.setTanggalPenilaian(new Date());

        new Thread(()
                -> controllerKPI.simpanKPI(kpi, new ControllerKPI.KPIListener() {
                    @Override
                    public void onSuccess(String p) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(EditData.this, p);
                            loadRiwayatKPI(selectedKaryawanId);
                            resetForm();
                        });
                    }

                    @Override
                    public void onError(String p) {
                        SwingUtilities.invokeLater(()
                                -> JOptionPane.showMessageDialog(EditData.this, p, "Error", JOptionPane.ERROR_MESSAGE));
                    }

                    @Override
                    public void onDataLoaded(List<ModelKPI> d) {
                    }
                }),
                 "Thread-SimpanKPI").start();
    }

    private void resetForm() {
        spnProduktivitas.setValue(0);
        spnKualitas.setValue(0);
        spnKehadiran.setValue(0);
        spnTeamwork.setValue(0);
        spnInovasi.setValue(0);
        txtCatatan.setText("");
        txtTarget.setText("");
        txtPenilai.setText("");
        updateHasil();
    }

    public void refreshKaryawan() {
        loadKaryawan();
    }
}
