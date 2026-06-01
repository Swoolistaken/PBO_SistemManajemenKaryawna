package view.karyawan;

import controller.ControllerKaryawan;
import model.karyawan.ModelKaryawan;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Form input/edit data karyawan
 * Implementasi: GUI SWING, EXCEPTION HANDLING
 */
public class InputData extends JDialog {

    private final ControllerKaryawan controller;
    private ModelKaryawan karyawan;
    private boolean isEdit = false;
    private boolean saved = false;

    // Form fields
    private JTextField txtNik, txtNama, txtEmail, txtNoTelp, txtAlamat;
    private JTextField txtTanggalLahir, txtTanggalMasuk;
    private JComboBox<String> cboJenisKelamin, cboJabatan, cboDepartemen;
    private JComboBox<String> cboLevelJabatan, cboPendidikan, cboStatus;
    private JTextField txtGajiPokok, txtTunjangTransport, txtTunjangMakan, txtTunjangKesehatan;
    private JTextArea txtKeahlian;
    private JLabel lblTotalGaji;
    private JButton btnSimpan, btnBatal, btnHitung;

    public InputData(Frame parent, ControllerKaryawan controller) {
        super(parent, "Tambah Karyawan Baru", true);
        this.controller = controller;
        this.karyawan = new ModelKaryawan();
        initUI();
    }

    public InputData(Frame parent, ControllerKaryawan controller, ModelKaryawan k) {
        super(parent, "Edit Data Karyawan", true);
        this.controller = controller;
        this.karyawan = k;
        this.isEdit = true;
        initUI();
        isiForm(k);
    }

    private void initUI() {
        setSize(720, 700);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 251));

        add(buatPanelHeader(), BorderLayout.NORTH);
        add(buatPanelForm(), BorderLayout.CENTER);
        add(buatPanelTombol(), BorderLayout.SOUTH);
    }

    private JPanel buatPanelHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(40, 60, 120));
        p.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lbl = new JLabel(isEdit ? "✎ Edit Data Karyawan" : "➕ Tambah Karyawan Baru");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    private JScrollPane buatPanelForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(new Color(245, 247, 251));
        form.setBorder(new EmptyBorder(15, 25, 10, 25));

        // === Seksi Data Pribadi ===
        form.add(buatSeksi("👤 Data Pribadi"));
        JPanel gridPribadi = buatGrid();
        txtNik = addField(gridPribadi, "NIK *", new JTextField());
        txtNama = addField(gridPribadi, "Nama Lengkap *", new JTextField());
        txtEmail = addField(gridPribadi, "Email", new JTextField());
        txtNoTelp = addField(gridPribadi, "No. Telepon", new JTextField());
        cboJenisKelamin = addCombo(gridPribadi, "Jenis Kelamin", new String[]{"Laki-laki", "Perempuan"});
        txtTanggalLahir = addField(gridPribadi, "Tanggal Lahir (dd/MM/yyyy)", new JTextField());
        form.add(gridPribadi);

        form.add(Box.createVerticalStrut(8));
        form.add(buatSeksi("📋 Data Pekerjaan"));
        JPanel gridKerja = buatGrid();
        cboJabatan = addCombo(gridKerja, "Jabatan *", new String[]{
            "Staff IT", "Programmer", "System Analyst", "Network Engineer", "DBA",
            "Staff HRD", "Recruitment Officer", "Training Officer",
            "Staff Finance", "Akuntan", "Kasir",
            "Staff Marketing", "Sales Executive", "Brand Manager",
            "Staff Operasional", "Supervisor", "Manager", "Senior Manager", "Director"
        });
        cboDepartemen = addCombo(gridKerja, "Departemen *", new String[]{
            "IT", "HRD", "Finance", "Marketing", "Operations", "Legal", "Procurement", "R&D"
        });
        cboLevelJabatan = addCombo(gridKerja, "Level *", new String[]{
            "1 - Staff", "2 - Supervisor", "3 - Manager", "4 - Director"
        });
        cboLevelJabatan.addActionListener(e -> hitungTunjangan());
        cboPendidikan = addCombo(gridKerja, "Pendidikan", new String[]{
            "SMA/SMK", "D3", "S1", "S2", "S3"
        });
        txtTanggalMasuk = addField(gridKerja, "Tanggal Masuk", new JTextField());
        cboStatus = addCombo(gridKerja, "Status", new String[]{"AKTIF", "NONAKTIF", "CUTI"});
        form.add(gridKerja);

        form.add(Box.createVerticalStrut(8));
        form.add(buatSeksi("💰 Data Penggajian"));
        JPanel gridGaji = buatGrid();
        txtGajiPokok = addField(gridGaji, "Gaji Pokok (Rp) *", new JTextField("0"));
        txtTunjangTransport = addField(gridGaji, "Tunjangan Transport", new JTextField("0"));
        txtTunjangMakan = addField(gridGaji, "Tunjangan Makan", new JTextField("0"));
        txtTunjangKesehatan = addField(gridGaji, "Tunjangan Kesehatan", new JTextField("0"));
        form.add(gridGaji);

        // Total gaji
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTotal.setBackground(new Color(230, 240, 255));
        panelTotal.setBorder(new LineBorder(new Color(180, 200, 240)));
        btnHitung = new JButton("Hitung Total");
        btnHitung.setBackground(new Color(70, 130, 200));
        btnHitung.setForeground(Color.WHITE);
        btnHitung.setFocusPainted(false);
        btnHitung.addActionListener(e -> hitungTotal());
        lblTotalGaji = new JLabel("Total Gaji: Rp 0");
        lblTotalGaji.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalGaji.setForeground(new Color(30, 120, 30));
        panelTotal.add(btnHitung);
        panelTotal.add(lblTotalGaji);
        form.add(panelTotal);

        form.add(Box.createVerticalStrut(8));
        form.add(buatSeksi("📝 Informasi Tambahan"));

        JPanel panelAlamat = new JPanel(new BorderLayout(8, 0));
        panelAlamat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panelAlamat.setBackground(new Color(245, 247, 251));
        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setPreferredSize(new Dimension(160, 20));
        txtAlamat = new JTextField();
        panelAlamat.add(lblAlamat, BorderLayout.WEST);
        panelAlamat.add(txtAlamat, BorderLayout.CENTER);
        form.add(panelAlamat);
        form.add(Box.createVerticalStrut(6));

        JPanel panelKeahlian = new JPanel(new BorderLayout(8, 0));
        panelKeahlian.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panelKeahlian.setBackground(new Color(245, 247, 251));
        JLabel lblKeahlian = new JLabel("Keahlian:");
        lblKeahlian.setPreferredSize(new Dimension(160, 20));
        txtKeahlian = new JTextArea(3, 20);
        txtKeahlian.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtKeahlian.setLineWrap(true);
        panelKeahlian.add(lblKeahlian, BorderLayout.WEST);
        panelKeahlian.add(new JScrollPane(txtKeahlian), BorderLayout.CENTER);
        form.add(panelKeahlian);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buatPanelTombol() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        p.setBackground(new Color(235, 238, 248));
        p.setBorder(new MatteBorder(1, 0, 0, 0, new Color(200, 205, 225)));

        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBatal.setPreferredSize(new Dimension(100, 34));
        btnBatal.addActionListener(e -> dispose());

        btnSimpan = new JButton(isEdit ? "Update" : "Simpan");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setBackground(new Color(34, 139, 34));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setPreferredSize(new Dimension(120, 34));
        btnSimpan.addActionListener(e -> simpanData());

        p.add(btnBatal);
        p.add(btnSimpan);
        return p;
    }

    // ===== Helper UI =====
    private JPanel buatGrid() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 8));
        p.setBackground(new Color(245, 247, 251));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return p;
    }

    private JLabel buatSeksi(String judul) {
        JLabel lbl = new JLabel(judul);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(40, 60, 120));
        lbl.setBorder(new MatteBorder(0, 0, 1, 0, new Color(180, 200, 240)));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return lbl;
    }

    private <T extends JTextField> T addField(JPanel panel, String label, T field) {
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 30));
        panel.add(lbl);
        panel.add(field);
        return field;
    }

    private JComboBox<String> addCombo(JPanel panel, String label, String[] items) {
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JComboBox<String> cbo = new JComboBox<>(items);
        cbo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbo.setPreferredSize(new Dimension(0, 30));
        panel.add(lbl);
        panel.add(cbo);
        return cbo;
    }

    private void hitungTunjangan() {
        int level = cboLevelJabatan.getSelectedIndex() + 1;
        switch (level) {
            case 4: txtTunjangTransport.setText("2000000"); txtTunjangMakan.setText("1500000"); txtTunjangKesehatan.setText("3000000"); break;
            case 3: txtTunjangTransport.setText("1500000"); txtTunjangMakan.setText("1000000"); txtTunjangKesehatan.setText("2000000"); break;
            case 2: txtTunjangTransport.setText("1000000"); txtTunjangMakan.setText("750000");  txtTunjangKesehatan.setText("1500000"); break;
            default: txtTunjangTransport.setText("500000");  txtTunjangMakan.setText("500000");  txtTunjangKesehatan.setText("1000000");
        }
        hitungTotal();
    }

    private void hitungTotal() {
        try {
            double gp = parseDouble(txtGajiPokok.getText());
            double tt = parseDouble(txtTunjangTransport.getText());
            double tm = parseDouble(txtTunjangMakan.getText());
            double tk = parseDouble(txtTunjangKesehatan.getText());
            double total = gp + tt + tm + tk;
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("id","ID"));
            lblTotalGaji.setText("Total Gaji: Rp " + nf.format(total));
        } catch (NumberFormatException e) {
            lblTotalGaji.setText("Total Gaji: (format angka salah)");
        }
    }

    private double parseDouble(String s) {
        return Double.parseDouble(s.trim().replace(",", "").replace(".", "").isEmpty() ? "0" : s.trim().replace(",", ""));
    }

    // ===== Isi form saat edit =====
    private void isiForm(ModelKaryawan k) {
        txtNik.setText(k.getNik());
        txtNama.setText(k.getNama());
        txtEmail.setText(k.getEmail() != null ? k.getEmail() : "");
        txtNoTelp.setText(k.getNoTelp() != null ? k.getNoTelp() : "");
        txtAlamat.setText(k.getAlamat() != null ? k.getAlamat() : "");
        cboJenisKelamin.setSelectedItem(k.getJenisKelamin());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (k.getTanggalLahir() != null) txtTanggalLahir.setText(sdf.format(k.getTanggalLahir()));
        if (k.getTanggalMasuk() != null) txtTanggalMasuk.setText(sdf.format(k.getTanggalMasuk()));

        cboJabatan.setSelectedItem(k.getJabatan());
        cboDepartemen.setSelectedItem(k.getDepartemen());
        cboLevelJabatan.setSelectedIndex(Math.max(0, k.getLevelJabatan() - 1));
        cboPendidikan.setSelectedItem(k.getPendidikanTerakhir());
        cboStatus.setSelectedItem(k.getStatus());
        txtGajiPokok.setText(String.valueOf((long) k.getGajiPokok()));
        txtTunjangTransport.setText(String.valueOf((long) k.getTunjanganTransport()));
        txtTunjangMakan.setText(String.valueOf((long) k.getTunjanganMakan()));
        txtTunjangKesehatan.setText(String.valueOf((long) k.getTunjanganKesehatan()));
        if (k.getKeahlian() != null) txtKeahlian.setText(k.getKeahlian());
        hitungTotal();
    }

    // ===== Simpan data =====
    private void simpanData() {
        try {
            // Kumpulkan data dari form
            karyawan.setNik(txtNik.getText().trim());
            karyawan.setNama(txtNama.getText().trim());
            karyawan.setEmail(txtEmail.getText().trim());
            karyawan.setNoTelp(txtNoTelp.getText().trim());
            karyawan.setAlamat(txtAlamat.getText().trim());
            karyawan.setJenisKelamin((String) cboJenisKelamin.getSelectedItem());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                if (!txtTanggalLahir.getText().isEmpty())
                    karyawan.setTanggalLahir(sdf.parse(txtTanggalLahir.getText()));
                if (!txtTanggalMasuk.getText().isEmpty())
                    karyawan.setTanggalMasuk(sdf.parse(txtTanggalMasuk.getText()));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Format tanggal harus dd/MM/yyyy. Contoh: 01/01/1990");
            }

            karyawan.setJabatan((String) cboJabatan.getSelectedItem());
            karyawan.setDepartemen((String) cboDepartemen.getSelectedItem());
            karyawan.setLevelJabatan(cboLevelJabatan.getSelectedIndex() + 1);
            karyawan.setPendidikanTerakhir((String) cboPendidikan.getSelectedItem());
            karyawan.setStatus((String) cboStatus.getSelectedItem());
            karyawan.setKeahlian(txtKeahlian.getText().trim());

            try {
                karyawan.setGajiPokok(Double.parseDouble(txtGajiPokok.getText().trim()));
                karyawan.setTunjanganTransport(Double.parseDouble(txtTunjangTransport.getText().trim()));
                karyawan.setTunjanganMakan(Double.parseDouble(txtTunjangMakan.getText().trim()));
                karyawan.setTunjanganKesehatan(Double.parseDouble(txtTunjangKesehatan.getText().trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Nominal gaji/tunjangan harus berupa angka!");
            }

            // Disable tombol saat proses
            btnSimpan.setEnabled(false);
            btnSimpan.setText("Menyimpan...");

            ControllerKaryawan.DataListener listener = new ControllerKaryawan.DataListener() {
                @Override public void onSuccess(String p) {
                    saved = true;
                    JOptionPane.showMessageDialog(InputData.this, p, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
                @Override public void onError(String p) {
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText(isEdit ? "Update" : "Simpan");
                    JOptionPane.showMessageDialog(InputData.this, p, "Error", JOptionPane.ERROR_MESSAGE);
                }
                @Override public void onDataLoaded(List<ModelKaryawan> d) {}
            };

            if (isEdit) controller.updateKaryawanAsync(karyawan, listener);
            else controller.simpanKaryawanAsync(karyawan, listener);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validasi", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
