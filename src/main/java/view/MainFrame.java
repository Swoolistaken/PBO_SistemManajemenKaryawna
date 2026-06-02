package view;

import controller.ControllerAbsensi;
import controller.ControllerAuth;
import controller.ControllerKPI;
import controller.ControllerKaryawan;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.karyawan.ModelKaryawan;
import model.user.ModelUser;
import view.Absensi.ViewAbsensi;
import view.karyawan.*;

public class MainFrame extends JFrame {

    private final ModelUser currentUser;

    private final ControllerKaryawan controllerKaryawan = new ControllerKaryawan();
    private final ControllerKPI controllerKPI = new ControllerKPI();
    private final ControllerAbsensi controllerAbsensi = new ControllerAbsensi();

    private JPanel panelKonten;
    private CardLayout cardLayout;

    private Dashboard dashboard;
    private ViewData viewData;
    private EditData editData;
    private ViewAbsensi viewAbsensi;

    public MainFrame(ModelUser user) {
        this.currentUser = user;
        initFrame();
        initPanels();
        initNav();
        tampilkan("DASHBOARD");
    }

    private void initFrame() {
        setTitle("Sistem Manajemen Karyawan — "
                + currentUser.getRoleLabel()
                + " | " + currentUser.getNamaLengkap());
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 500));
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.Connector.getInstance().closeConnection();
                if (dashboard != null) {
                    dashboard.stopTimer();
                }
                dispose();
            }
        });
    }

    private void initPanels() {
        cardLayout = new CardLayout();
        panelKonten = new JPanel(cardLayout);

        dashboard = new Dashboard(controllerKaryawan, controllerKPI);
        viewData = new ViewData(controllerKaryawan);
        editData = new EditData(controllerKaryawan, controllerKPI);
        viewAbsensi = new ViewAbsensi(controllerKaryawan, controllerAbsensi);

        viewData.setCallback(new ViewData.ActionCallback() {
            @Override
            public void onTambah() {
                if (!currentUser.bisaTambahKaryawan()) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Anda tidak memiliki akses untuk menambah karyawan!",
                            "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                InputData form = new InputData(MainFrame.this, controllerKaryawan);
                form.setVisible(true);
                if (form.isSaved()) {
                    refresh();
                }
            }

            @Override
            public void onEdit(ModelKaryawan k) {
                if (!currentUser.bisaEdit()) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Anda tidak memiliki akses untuk mengedit karyawan!",
                            "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                InputData form = new InputData(MainFrame.this, controllerKaryawan, k);
                form.setVisible(true);
                if (form.isSaved()) {
                    refresh();
                }
            }

            @Override
            public void onLihatKPI(ModelKaryawan k) {
                tampilkan("KPI");
                editData.refreshKaryawan();
            }

            @Override
            public void onLihatAbsensi(ModelKaryawan k) {
                tampilkan("ABSENSI");
            }
        });

        viewData.setAksesHapus(currentUser.bisaHapus());
        viewData.setAksesTambah(currentUser.bisaTambahKaryawan());

        panelKonten.add(dashboard, "DASHBOARD");
        panelKonten.add(viewData, "KARYAWAN");
        panelKonten.add(editData, "KPI");
        panelKonten.add(viewAbsensi, "ABSENSI");

        if (currentUser.bisaLihatGaji()) {
            panelKonten.add(buatPanelGaji(), "GAJI");
        }

        add(panelKonten, BorderLayout.CENTER);
    }

    private void initNav() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Info user
        JLabel lblUser = new JLabel(currentUser.getNamaLengkap());
        lblUser.setFont(new Font("Dialog", Font.BOLD, 12));
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRole = new JLabel(currentUser.getRoleLabel());
        lblRole.setFont(new Font("Dialog", Font.PLAIN, 11));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(lblUser);
        navPanel.add(lblRole);
        navPanel.add(new JSeparator());
        navPanel.add(Box.createVerticalStrut(5));

        // Menu sesuai akses
        String akses = currentUser.getMenuAkses();

        navPanel.add(buatNavBtn("Dashboard", "DASHBOARD"));

        if (akses.contains("KARYAWAN")) {
            navPanel.add(buatNavBtn("Data Karyawan", "KARYAWAN"));
        }
        if (akses.contains("KPI")) {
            navPanel.add(buatNavBtn("Penilaian KPI", "KPI"));
        }
        if (akses.contains("ABSENSI")) {
            navPanel.add(buatNavBtn("Absensi", "ABSENSI"));
        }
        if (akses.contains("GAJI")) {
            navPanel.add(buatNavBtn("Penggajian", "GAJI"));
        }

        navPanel.add(Box.createVerticalGlue());
        navPanel.add(new JSeparator());

        JButton btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnLogout.addActionListener(e -> logout());
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(btnLogout);
        navPanel.add(Box.createVerticalStrut(5));

        JScrollPane scrollNav = new JScrollPane(navPanel);
        scrollNav.setPreferredSize(new Dimension(160, 0));
        scrollNav.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        add(scrollNav, BorderLayout.WEST);
    }

    private JButton buatNavBtn(String teks, String panel) {
        JButton btn = new JButton(teks);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            tampilkan(panel);
            if ("KARYAWAN".equals(panel)) {
                viewData.loadData();
            }
            if ("KPI".equals(panel)) {
                editData.refreshKaryawan();
            }
            if ("ABSENSI".equals(panel)) {
                viewAbsensi.refreshKaryawan();
            }
        });
        return btn;
    }

    private void tampilkan(String nama) {
        cardLayout.show(panelKonten, nama);
    }

    private void refresh() {
        viewData.loadData();
        dashboard.loadStatistik();
        editData.refreshKaryawan();
        viewAbsensi.refreshKaryawan();
    }

    private void logout() {
        int ok = JOptionPane.showConfirmDialog(this,
                "Yakin ingin logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        ControllerAuth.logout();
        model.Connector.getInstance().closeConnection();
        if (dashboard != null) {
            dashboard.stopTimer();
        }
        dispose();
        new view.auth.LoginForm().setVisible(true);
    }

    private JPanel buatPanelGaji() {
        JPanel p = new JPanel(new BorderLayout());

        JLabel lbl = new JLabel("Modul Penggajian", SwingConstants.CENTER);
        lbl.setFont(new Font("Dialog", Font.BOLD, 16));

        JButton btnKalkulasi = new JButton("Kalkulasi Gaji Bulan Ini");
        btnKalkulasi.addActionListener(e -> kalkulasiGaji());

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        center.add(lbl, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 0, 0);
        center.add(btnKalkulasi, gbc);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private void kalkulasiGaji() {
        new Thread(() -> {
            try {
                java.util.List<ModelKaryawan> list = controllerKaryawan.getAllKaryawan();
                double total = 0;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%-25s %-15s %20s%n", "Nama", "Jabatan", "Total Gaji"));
                sb.append("=".repeat(62)).append("\n");
                for (ModelKaryawan k : list) {
                    if ("AKTIF".equals(k.getStatus())) {
                        total += k.hitungTotalGaji();
                        sb.append(String.format("%-25s %-15s %,20.0f%n",
                                k.getNama(), k.getJabatan(), k.hitungTotalGaji()));
                    }
                }
                sb.append("=".repeat(62)).append("\n");
                sb.append(String.format("%-40s %,20.0f%n", "TOTAL PENGELUARAN GAJI:", total));
                final String hasil = sb.toString();
                SwingUtilities.invokeLater(() -> {
                    JTextArea ta = new JTextArea(hasil);
                    ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    ta.setEditable(false);
                    JOptionPane.showMessageDialog(this,
                            new JScrollPane(ta), "Rekap Penggajian",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(this,
                                "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }, "Thread-Penggajian").start();
    }
}
