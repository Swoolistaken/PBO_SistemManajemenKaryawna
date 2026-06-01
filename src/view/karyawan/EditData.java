package view.karyawan;

import controller.ControllerKaryawan;
import model.karyawan.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.NumberFormat;

/**
 * View untuk Penilaian KPI Karyawan
 * Implementasi: GUI SWING, MULTITHREAD
 */
public class EditData extends JPanel {

    private final ControllerKaryawan controller;

    // Komponen form KPI
    private JComboBox<String> cboKaryawan;
    private JComboBox<Integer> cboPeriode;
    private JComboBox<String> cboBulan;
    private JSlider sliderProduktivitas, sliderKualitas, sliderKehadiran, sliderTeamwork, sliderInovasi;
    private JLabel lblNilaiProd, lblNilaiKual, lblNilaiHadir, lblNilaiTeam, lblNilaiInovasi;
    private JLabel lblNilaiAkhir, lblGrade, lblBonus;
    private JTextArea txtCatatan, txtTarget;
    private JTextField txtPenilai;
    private JButton btnSimpan, btnHapus, btnReset;

    // Tabel riwayat KPI
    private JTable tabelKPI;
    private DefaultTableModel modelTabel;

    private List<ModelKaryawan> daftarKaryawan = new ArrayList<>();
    private int selectedKaryawanId = -1;
    private int selectedKPIId = -1;
    private double gajiPokokKaryawan = 0;

    private final String[] NAMA_BULAN = {
        "Januari","Februari","Maret","April","Mei","Juni",
        "Juli","Agustus","September","Oktober","November","Desember"
    };

    public EditData(ControllerKaryawan controller) {
        this.controller = controller;
        initUI();
        loadKaryawan();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 33, 43));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buatPanelForm(), buatPanelRiwayat());
        split.setDividerLocation(450);
        split.setBorder(new EmptyBorder(10, 15, 10, 15));
        split.setResizeWeight(0.45);
        add(split, BorderLayout.CENTER);
    }

    // ===== Panel Form KPI =====
    private JScrollPane buatPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(42, 46, 60));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(55, 65, 90), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Judul
        JLabel judul = new JLabel("📊 Penilaian KPI Karyawan");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 16));
        judul.setForeground(new Color(180, 205, 255));
        judul.setBorder(new MatteBorder(0, 0, 1, 0, new Color(55, 70, 110)));
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        judul.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(judul);
        panel.add(Box.createVerticalStrut(10));

        // Pilih karyawan
        panel.add(buatRowForm("Karyawan *:", cboKaryawan = new JComboBox<>()));
        cboKaryawan.addActionListener(e -> onKaryawanDipilih());
        panel.add(Box.createVerticalStrut(5));

        // Periode
        JPanel panelPeriode = new JPanel(new GridLayout(1, 2, 8, 0));
        panelPeriode.setBackground(new Color(42, 46, 60));
        panelPeriode.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelPeriode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        int tahunIni = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] tahunList = new Integer[10];
        for (int i = 0; i < 10; i++) tahunList[i] = tahunIni - i;
        cboPeriode = new JComboBox<>(tahunList);

        cboBulan = new JComboBox<>(NAMA_BULAN);
        cboBulan.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));

        panelPeriode.add(cboPeriode);
        panelPeriode.add(cboBulan);
        panel.add(buatRowForm("Periode (Tahun / Bulan) *:", panelPeriode));
        panel.add(Box.createVerticalStrut(10));

        // ===== Slider Nilai KPI =====
        JLabel lblPenilaian = new JLabel("━━ Penilaian (0 - 100) ━━");
        lblPenilaian.setForeground(Color.WHITE);
        lblPenilaian.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPenilaian.setForeground(new Color(150, 175, 240));
        lblPenilaian.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblPenilaian);
        panel.add(Box.createVerticalStrut(6));

        sliderProduktivitas = buatSlider();
        sliderKualitas      = buatSlider();
        sliderKehadiran     = buatSlider();
        sliderTeamwork      = buatSlider();
        sliderInovasi       = buatSlider();

        lblNilaiProd   = new JLabel("0");
        lblNilaiKual   = new JLabel("0");
        lblNilaiHadir  = new JLabel("0");
        lblNilaiTeam   = new JLabel("0");
        lblNilaiInovasi = new JLabel("0");

        panel.add(buatRowSlider("Produktivitas (30%)", sliderProduktivitas, lblNilaiProd));
        panel.add(buatRowSlider("Kualitas Kerja (25%)", sliderKualitas, lblNilaiKual));
        panel.add(buatRowSlider("Kehadiran (20%)", sliderKehadiran, lblNilaiHadir));
        panel.add(buatRowSlider("Kerjasama Tim (15%)", sliderTeamwork, lblNilaiTeam));
        panel.add(buatRowSlider("Inovasi (10%)", sliderInovasi, lblNilaiInovasi));
        panel.add(Box.createVerticalStrut(10));

        // Hasil KPI
        JPanel panelHasil = new JPanel(new GridLayout(3, 1, 0, 3));
        panelHasil.setBackground(new Color(28, 45, 75));
        panelHasil.setBorder(new CompoundBorder(
            new LineBorder(new Color(50, 90, 160)), new EmptyBorder(8, 12, 8, 12)
        ));
        panelHasil.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHasil.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblNilaiAkhir = new JLabel("Nilai Akhir: 0.00");
        lblNilaiAkhir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGrade = new JLabel("Grade: -");
        lblGrade.setFont(new Font("Segoe UI", Font.BOLD, 13));txtCatatan = new JTextArea(3, 20);
txtCatatan.setBackground(new Color(55, 60, 80));
txtCatatan.setForeground(Color.WHITE);
txtCatatan.setCaretColor(Color.WHITE);
        lblBonus = new JLabel("Estimasi Bonus: Rp 0");
        lblBonus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBonus.setForeground(new Color(0, 130, 0));
        lblNilaiAkhir.setForeground(Color.WHITE);
lblGrade.setForeground(Color.WHITE);
lblBonus.setForeground(Color.WHITE);
        panelHasil.add(lblNilaiAkhir);
        panelHasil.add(lblGrade);
        panelHasil.add(lblBonus);
        panel.add(panelHasil);
        panel.add(Box.createVerticalStrut(8));

        // Catatan & penilai
        panel.add(buatRowForm("Penilai *:", txtPenilai = new JTextField()));
        panel.add(Box.createVerticalStrut(5));
        panel.add(buatLabelKiri("Catatan Atasan:"));
        txtCatatan = new JTextArea(3, 20);
txtCatatan.setBackground(new Color(55, 60, 80));
txtCatatan.setForeground(Color.WHITE);
txtCatatan.setCaretColor(Color.WHITE);
        txtCatatan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        JScrollPane scCatatan = new JScrollPane(txtCatatan);
        scCatatan.setAlignmentX(Component.LEFT_ALIGNMENT);
        scCatatan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panel.add(scCatatan);
        panel.add(Box.createVerticalStrut(5));
        panel.add(buatLabelKiri("Target Periode Berikutnya:"));
        txtTarget = new JTextArea(2, 20);
txtTarget.setBackground(new Color(55, 60, 80));
txtTarget.setForeground(Color.WHITE);
txtTarget.setCaretColor(Color.WHITE);
        txtTarget.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTarget.setLineWrap(true);
        JScrollPane scTarget = new JScrollPane(txtTarget);
        scTarget.setAlignmentX(Component.LEFT_ALIGNMENT);
        scTarget.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        panel.add(scTarget);
        panel.add(Box.createVerticalStrut(10));

        // Tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelTombol.setBackground(new Color(42, 46, 60));
        panelTombol.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelTombol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnReset  = buatBtn("Reset",  new Color(130, 130, 160));
        btnHapus  = buatBtn("Hapus",  new Color(200, 50,  50));
        btnSimpan = buatBtn("Simpan", new Color(34,  139, 34));
        btnHapus.setEnabled(false);

        btnReset.addActionListener(e -> resetForm());
        btnHapus.addActionListener(e -> hapusKPI());
        btnSimpan.addActionListener(e -> simpanKPI());

        panelTombol.add(btnReset);
        panelTombol.add(btnHapus);
        panelTombol.add(btnSimpan);
        panel.add(panelTombol);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    // ===== Panel Riwayat KPI =====
    private JPanel buatPanelRiwayat() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(42, 46, 60));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(55, 65, 90), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel judul = new JLabel("📋 Riwayat Penilaian KPI");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 15));
        judul.setForeground(new Color(180, 205, 255));
        judul.setBorder(new MatteBorder(0, 0, 1, 0, new Color(55, 70, 110)));
        panel.add(judul, BorderLayout.NORTH);

        String[] kolom = {"No", "Karyawan", "Tahun", "Bulan", "Nilai", "Grade", "Bonus", "Penilai"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelKPI = new JTable(modelTabel);
        tabelKPI.setBackground(new Color(42, 46, 60));
tabelKPI.setForeground(new Color(210, 215, 230));
tabelKPI.setSelectionBackground(new Color(50, 75, 130));
tabelKPI.setSelectionForeground(Color.WHITE);

tabelKPI.setGridColor(new Color(55, 65, 90));
tabelKPI.setShowGrid(false);
tabelKPI.setIntercellSpacing(new Dimension(0, 0));

tabelKPI.setFont(new Font("Segoe UI", Font.PLAIN, 12));
tabelKPI.setRowHeight(30);

tabelKPI.getTableHeader().setDefaultRenderer(
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
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

            return lbl;
        }
    }
);

tabelKPI.getTableHeader().setPreferredSize(new Dimension(0, 35));

tabelKPI.setDefaultRenderer(
    Object.class,
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
return panel;
    }

    // ===== Helpers =====
    private JSlider buatSlider() {
        JSlider s = new JSlider(0, 100, 0);
        s.setBackground(new Color(42, 46, 60));
        s.setPaintTicks(true);
        s.setMajorTickSpacing(25);
        s.setMinorTickSpacing(5);
        return s;
    }

    private JPanel buatRowSlider(String label, JSlider slider, JLabel lblNilai) {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.setBackground(new Color(42, 46, 60));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lbl = new JLabel(label);
lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
lbl.setForeground(Color.WHITE);
lbl.setPreferredSize(new Dimension(160, 20));
        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNilai.setForeground(new Color(40, 100, 200));
        lblNilai.setPreferredSize(new Dimension(35, 20));

        slider.addChangeListener(e -> {
            lblNilai.setText(String.valueOf(slider.getValue()));
            updateHasilKPI();
        });

        p.add(lbl, BorderLayout.WEST);
        p.add(slider, BorderLayout.CENTER);
        p.add(lblNilai, BorderLayout.EAST);
        return p;
    }

    private JPanel buatRowForm(String label, JComponent comp) {
    JPanel p = new JPanel(new BorderLayout(8, 0));
    p.setBackground(new Color(42, 46, 60));
    p.setAlignmentX(Component.LEFT_ALIGNMENT);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

    JLabel lbl = new JLabel(label);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lbl.setForeground(Color.WHITE); // <-- TAMBAH INI
    lbl.setPreferredSize(new Dimension(170, 20));

    if (comp instanceof JTextField) {
        ((JTextField) comp).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ((JTextField) comp).setBackground(new Color(55, 60, 80));
        ((JTextField) comp).setForeground(Color.WHITE);
        ((JTextField) comp).setCaretColor(Color.WHITE);
    }

    if (comp instanceof JComboBox) {
        ((JComboBox<?>) comp).setBackground(new Color(55, 60, 80));
        ((JComboBox<?>) comp).setForeground(Color.WHITE);
        ((JComboBox<?>) comp).setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    p.add(lbl, BorderLayout.WEST);
    p.add(comp, BorderLayout.CENTER);

    return p;
}

    private JLabel buatLabelKiri(String teks) {
    JLabel lbl = new JLabel(teks);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lbl.setForeground(Color.WHITE); // <-- TAMBAH
    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
    lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
    return lbl;
}

    private JButton buatBtn(String teks, Color bg) {
        JButton btn = new JButton(teks);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    // ===== Data operations =====

    private void loadKaryawan() {
        try {
            daftarKaryawan = controller.getAllKaryawan();
            cboKaryawan.removeAllItems();
            cboKaryawan.addItem("-- Pilih Karyawan --");
            for (ModelKaryawan k : daftarKaryawan) {
                cboKaryawan.addItem("[" + k.getNik() + "] " + k.getNama());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat karyawan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onKaryawanDipilih() {
        int idx = cboKaryawan.getSelectedIndex() - 1;
        if (idx < 0) { selectedKaryawanId = -1; return; }
        ModelKaryawan k = daftarKaryawan.get(idx);
        selectedKaryawanId = k.getId();
        gajiPokokKaryawan = k.getGajiPokok();
        loadRiwayatKPI(k.getId());
        updateHasilKPI();
    }

    private void loadRiwayatKPI(int karyawanId) {
        controller.loadKPIByKaryawanAsync(karyawanId, new ControllerKaryawan.KPIListener() {
            @Override public void onSuccess(String p) {}
            @Override public void onError(String p) {
                JOptionPane.showMessageDialog(EditData.this, p, "Error", JOptionPane.ERROR_MESSAGE);
            }
            @Override public void onDataLoaded(List<ModelKPI> data) {
                isiTabel(data);
            }
        });
    }

    private void loadSemuaKPI() {
        controller.loadKPIByPeriodeAsync(
            (Integer) cboPeriode.getSelectedItem(),
            cboBulan.getSelectedIndex() + 1,
            new ControllerKaryawan.KPIListener() {
                @Override public void onSuccess(String p) {}
                @Override public void onError(String p) {
                    JOptionPane.showMessageDialog(EditData.this, p, "Error", JOptionPane.ERROR_MESSAGE);
                }
                @Override public void onDataLoaded(List<ModelKPI> data) {
                    isiTabel(data);
                }
            }
        );
    }

    private void isiTabel(List<ModelKPI> data) {
        modelTabel.setRowCount(0);
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        int no = 1;
        for (ModelKPI kpi : data) {
            modelTabel.addRow(new Object[]{
                no++,
                kpi.getNamaKaryawan(),
                kpi.getPeriode(),
                NAMA_BULAN[kpi.getBulan() - 1],
                String.format("%.2f", kpi.hitungNilaiAkhir()),
                kpi.getGradeKPI(),
                rupiah.format(kpi.getBonusKPI(gajiPokokKaryawan)),
                kpi.getPenilai()
            });
        }
    }

    private void loadKPIKeForm() {
        int row = tabelKPI.getSelectedRow();
        if (row < 0) return;
        JOptionPane.showMessageDialog(this,
            "Klik kanan tabel → Edit, atau gunakan tombol Hapus untuk menghapus baris ini.",
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateHasilKPI() {
        ModelKPI kpi = buatKPIDariForm();
        double nilai = kpi.hitungNilaiAkhir();
        lblNilaiAkhir.setText(String.format("Nilai Akhir: %.2f / 100", nilai));
        lblGrade.setText("Grade: " + kpi.getGradeKPI());
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        lblBonus.setText("Estimasi Bonus: " + rupiah.format(kpi.getBonusKPI(gajiPokokKaryawan)));

        // Warna grade
        double n = nilai;
        Color c = n >= 80 ? new Color(0, 140, 0) : n >= 60 ? new Color(180, 120, 0) : new Color(200, 0, 0);
        lblGrade.setForeground(c);
        lblNilaiAkhir.setForeground(c);
    }

    private ModelKPI buatKPIDariForm() {
        ModelKPI kpi = new ModelKPI();
        kpi.setNilaiProduktivitas(sliderProduktivitas.getValue());
        kpi.setNilaiKualitas(sliderKualitas.getValue());
        kpi.setNilaiKehadiran(sliderKehadiran.getValue());
        kpi.setNilaiTeamwork(sliderTeamwork.getValue());
        kpi.setNilaiInovasi(sliderInovasi.getValue());
        return kpi;
    }

    private void simpanKPI() {
        if (selectedKaryawanId < 0) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
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

        controller.simpanKPIAsync(kpi, new ControllerKaryawan.KPIListener() {
            @Override public void onSuccess(String p) {
                JOptionPane.showMessageDialog(EditData.this, p, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                loadRiwayatKPI(selectedKaryawanId);
                resetForm();
            }
            @Override public void onError(String p) {
                JOptionPane.showMessageDialog(EditData.this, p, "Error", JOptionPane.ERROR_MESSAGE);
            }
            @Override public void onDataLoaded(List<ModelKPI> d) {}
        });
    }

    private void hapusKPI() {
        int row = tabelKPI.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih KPI di tabel untuk dihapus!"); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Yakin hapus penilaian KPI ini?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        // Note: ideally pass real ID; for now notify user
        JOptionPane.showMessageDialog(this, "Hapus: Double-klik baris untuk melihat detail, lalu konfirmasi hapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetForm() {
        sliderProduktivitas.setValue(0); sliderKualitas.setValue(0);
        sliderKehadiran.setValue(0); sliderTeamwork.setValue(0); sliderInovasi.setValue(0);
        txtCatatan.setText(""); txtTarget.setText(""); txtPenilai.setText("");
        selectedKPIId = -1;
        btnHapus.setEnabled(false);
        updateHasilKPI();
    }

    public void refreshKaryawan() { loadKaryawan(); }
}
