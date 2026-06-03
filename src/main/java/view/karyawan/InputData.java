package view.karyawan;

import controller.ControllerKaryawan;
import model.karyawan.ModelKaryawan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class InputData extends JDialog {

    private final ControllerKaryawan controller;
    private ModelKaryawan karyawan;
    private boolean isEdit = false;
    private boolean saved = false;

    private JTextField txtNik, txtNama, txtEmail, txtNoTelp;
    private JTextField txtAlamat, txtTanggalLahir, txtTanggalMasuk;
    private JComboBox<String> cboJenisKelamin, cboJabatan, cboDepartemen;
    private JComboBox<String> cboLevelJabatan, cboPendidikan, cboStatus;
    private JTextField txtGajiPokok, txtTunjangTransport, txtTunjangMakan, txtTunjangKesehatan;
    private JTextArea txtKeahlian;
    private JLabel lblTotalGaji;
    private JButton btnSimpan, btnBatal;

    public InputData(Frame parent, ControllerKaryawan controller) {
        super(parent, "Tambah Karyawan", true);
        this.controller = controller;
        this.karyawan = new ModelKaryawan();
        initUI();
    }

    public InputData(Frame parent, ControllerKaryawan controller, ModelKaryawan k) {
        super(parent, "Edit Karyawan", true);
        this.controller = controller;
        this.karyawan = k;
        this.isEdit = true;
        initUI();
        isiForm(k);
    }

    private void initUI() {
        setSize(600, 620);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(5, 5));

        add(buatForm(), BorderLayout.CENTER);
        add(buatTombol(), BorderLayout.SOUTH);
    }

    private JScrollPane buatForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // === Data Pribadi ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(buatSeparator("Data Pribadi"), gbc);
        gbc.gridwidth = 1;

        txtNik = new JTextField();
        txtNama = new JTextField();
        txtEmail = new JTextField();
        txtNoTelp = new JTextField();
        txtAlamat = new JTextField();
        txtTanggalLahir = new JTextField();
        cboJenisKelamin = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});

        row = addRow(form, gbc, ++row, "NIK *", txtNik);
        row = addRow(form, gbc, ++row, "Nama Lengkap *", txtNama);
        row = addRow(form, gbc, ++row, "Email", txtEmail);
        row = addRow(form, gbc, ++row, "No. Telepon", txtNoTelp);
        row = addRow(form, gbc, ++row, "Alamat", txtAlamat);
        row = addRow(form, gbc, ++row, "Tgl. Lahir (dd/MM/yyyy)", txtTanggalLahir);
        row = addRow(form, gbc, ++row, "Jenis Kelamin", cboJenisKelamin);

        // === Data Pekerjaan ===
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        form.add(buatSeparator("Data Pekerjaan"), gbc);
        gbc.gridwidth = 1;

        cboJabatan = new JComboBox<>(new String[]{
            "Staff IT", "Programmer", "System Analyst", "Network Engineer", "DBA",
            "Staff HRD", "Recruitment Officer", "Training Officer",
            "Staff Finance", "Akuntan", "Kasir",
            "Staff Marketing", "Sales Executive", "Brand Manager",
            "Staff Operasional", "Supervisor", "Manager", "Senior Manager", "Director"
        });
        cboDepartemen = new JComboBox<>(new String[]{
            "IT", "HRD", "Finance", "Marketing", "Operations", "Legal", "Procurement", "R&D"
        });
        cboLevelJabatan = new JComboBox<>(new String[]{
            "1 - Staff", "2 - Supervisor", "3 - Manager", "4 - Director"
        });
        cboPendidikan = new JComboBox<>(new String[]{"SMA/SMK", "D3", "S1", "S2", "S3"});
        cboStatus = new JComboBox<>(new String[]{"AKTIF", "NONAKTIF", "CUTI"});
        txtTanggalMasuk = new JTextField();

        cboLevelJabatan.addActionListener(e -> hitungTunjangan());

        row = addRow(form, gbc, ++row, "Jabatan *", cboJabatan);
        row = addRow(form, gbc, ++row, "Departemen *", cboDepartemen);
        row = addRow(form, gbc, ++row, "Level *", cboLevelJabatan);
        row = addRow(form, gbc, ++row, "Pendidikan", cboPendidikan);
        row = addRow(form, gbc, ++row, "Tgl. Masuk (dd/MM/yyyy)", txtTanggalMasuk);
        row = addRow(form, gbc, ++row, "Status", cboStatus);

        // === Penggajian ===
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        form.add(buatSeparator("Penggajian"), gbc);
        gbc.gridwidth = 1;

        txtGajiPokok = new JTextField("0");
        txtTunjangTransport = new JTextField("0");
        txtTunjangMakan = new JTextField("0");
        txtTunjangKesehatan = new JTextField("0");

        row = addRow(form, gbc, ++row, "Gaji Pokok (Rp) *", txtGajiPokok);
        row = addRow(form, gbc, ++row, "Tunjangan Transport", txtTunjangTransport);
        row = addRow(form, gbc, ++row, "Tunjangan Makan", txtTunjangMakan);
        row = addRow(form, gbc, ++row, "Tunjangan Kesehatan", txtTunjangKesehatan);

        // Total gaji
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnHitung = new JButton("Hitung Total");
        btnHitung.addActionListener(e -> hitungTotal());
        lblTotalGaji = new JLabel("Total: Rp 0");
        lblTotalGaji.setFont(new Font("Dialog", Font.BOLD, 13));
        panelTotal.add(btnHitung);
        panelTotal.add(lblTotalGaji);
        form.add(panelTotal, gbc);
        gbc.gridwidth = 1;

        // Keahlian
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 2;
        form.add(buatSeparator("Informasi Tambahan"), gbc);
        gbc.gridwidth = 1;

        txtKeahlian = new JTextArea(3, 20);
        txtKeahlian.setLineWrap(true);
        row = addRow(form, gbc, ++row, "Keahlian", new JScrollPane(txtKeahlian));

        JScrollPane scroll = new JScrollPane(form);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private JPanel buatTombol() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBatal = new JButton("Batal");
        btnSimpan = new JButton(isEdit ? "Update" : "Simpan");

        btnBatal.addActionListener(e -> dispose());
        btnSimpan.addActionListener(e -> simpanData());

        p.add(btnBatal);
        p.add(btnSimpan);
        return p;
    }

    // ===== Helper =====
    private int addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        form.add(comp, gbc);
        return row;
    }

    private JLabel buatSeparator(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Dialog", Font.BOLD, 12));
        return lbl;
    }

    private void hitungTunjangan() {
        int level = cboLevelJabatan.getSelectedIndex() + 1;
        switch (level) {
            case 4:
                txtTunjangTransport.setText("2000000");
                txtTunjangMakan.setText("1500000");
                txtTunjangKesehatan.setText("3000000");
                break;
            case 3:
                txtTunjangTransport.setText("1500000");
                txtTunjangMakan.setText("1000000");
                txtTunjangKesehatan.setText("2000000");
                break;
            case 2:
                txtTunjangTransport.setText("1000000");
                txtTunjangMakan.setText("750000");
                txtTunjangKesehatan.setText("1500000");
                break;
            default:
                txtTunjangTransport.setText("500000");
                txtTunjangMakan.setText("500000");
                txtTunjangKesehatan.setText("1000000");
        }
        hitungTotal();
    }

    private void hitungTotal() {
        try {
            double total = Double.parseDouble(txtGajiPokok.getText().trim())
                    + Double.parseDouble(txtTunjangTransport.getText().trim())
                    + Double.parseDouble(txtTunjangMakan.getText().trim())
                    + Double.parseDouble(txtTunjangKesehatan.getText().trim());
            lblTotalGaji.setText(String.format("Total: Rp %,.0f", total));
        } catch (NumberFormatException e) {
            lblTotalGaji.setText("Total: (format angka salah)");
        }
    }

    private void isiForm(ModelKaryawan k) {
        txtNik.setText(k.getNik());
        txtNama.setText(k.getNama());
        txtEmail.setText(k.getEmail() != null ? k.getEmail() : "");
        txtNoTelp.setText(k.getNoTelp() != null ? k.getNoTelp() : "");
        txtAlamat.setText(k.getAlamat() != null ? k.getAlamat() : "");
        cboJenisKelamin.setSelectedItem(k.getJenisKelamin());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (k.getTanggalLahir() != null) {
            txtTanggalLahir.setText(sdf.format(k.getTanggalLahir()));
        }
        if (k.getTanggalMasuk() != null) {
            txtTanggalMasuk.setText(sdf.format(k.getTanggalMasuk()));
        }
        cboJabatan.setSelectedItem(k.getJabatan());
        cboDepartemen.setSelectedItem(k.getDepartemen());
        cboLevelJabatan.setSelectedIndex(Math.max(0, k.getLevelJabatan() - 1));
        cboPendidikan.setSelectedItem(k.getPendidikanTerakhir());
        cboStatus.setSelectedItem(k.getStatus());
        txtGajiPokok.setText(String.valueOf((long) k.getGajiPokok()));
        txtTunjangTransport.setText(String.valueOf((long) k.getTunjanganTransport()));
        txtTunjangMakan.setText(String.valueOf((long) k.getTunjanganMakan()));
        txtTunjangKesehatan.setText(String.valueOf((long) k.getTunjanganKesehatan()));
        if (k.getKeahlian() != null) {
            txtKeahlian.setText(k.getKeahlian());
        }
        hitungTotal();
    }

    private void simpanData() {
        try {
            karyawan.setNik(txtNik.getText().trim());
            karyawan.setNama(txtNama.getText().trim());
            karyawan.setEmail(txtEmail.getText().trim());
            karyawan.setNoTelp(txtNoTelp.getText().trim());
            karyawan.setAlamat(txtAlamat.getText().trim());
            karyawan.setJenisKelamin((String) cboJenisKelamin.getSelectedItem());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                if (!txtTanggalLahir.getText().trim().isEmpty()) {
                    karyawan.setTanggalLahir(sdf.parse(txtTanggalLahir.getText().trim()));
                }
                if (!txtTanggalMasuk.getText().trim().isEmpty()) {
                    karyawan.setTanggalMasuk(sdf.parse(txtTanggalMasuk.getText().trim()));
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("Format tanggal harus dd/MM/yyyy");
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

            btnSimpan.setEnabled(false);
            btnSimpan.setText("Menyimpan...");

            ControllerKaryawan.DataListener listener = new ControllerKaryawan.DataListener() {
                @Override
                public void onSuccess(String p) {
                    SwingUtilities.invokeLater(() -> {
                        saved = true;
                        JOptionPane.showMessageDialog(InputData.this, p);
                        dispose();
                    });
                }

                @Override
                public void onError(String p) {
                    SwingUtilities.invokeLater(() -> {
                        btnSimpan.setEnabled(true);
                        btnSimpan.setText(isEdit ? "Update" : "Simpan");
                        JOptionPane.showMessageDialog(InputData.this, p, "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }

                @Override
                public void onDataLoaded(List<ModelKaryawan> d) {
                }
            };

            if (isEdit) {
                new Thread(() -> controller.updateKaryawan(karyawan, listener), "Thread-UpdateKaryawan").start();
            } else {
                new Thread(() -> controller.simpanKaryawan(karyawan, listener), "Thread-SimpanKaryawan").start();
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validasi", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
