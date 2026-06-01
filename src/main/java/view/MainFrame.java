package view;

import controller.ControllerAbsensi;
import controller.ControllerAuth;
import controller.ControllerKPI;
import controller.ControllerKaryawan;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.border.EmptyBorder;
import model.karyawan.ModelKaryawan;
import model.user.ModelUser;
import view.Absensi.ViewAbsensi;
import view.karyawan.Dashboard;
import view.karyawan.EditData;
import view.karyawan.InputData;
import view.karyawan.ViewData;
import javax.swing.*;

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

    private JButton btnAktif;
    private final Color BG_NAV = new Color(25, 45, 110);
    private final Color BG_NAV_HOVER = new Color(45, 75, 160);
    private final Color BG_NAV_AKTIF = new Color(70, 120, 220);

    public MainFrame(ModelUser user) {
        this.currentUser = user;
        initLookAndFeel();
        initFrame();
        initPanels();
        initNav();
        tampilkan("DASHBOARD");
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Table.alternateRowColor", new Color(248, 250, 255));
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 13));
        } catch (Exception ignored) {
        }
    }

    private void initFrame() {
        setTitle("Sistem Manajemen Karyawan — "
                + currentUser.getRoleLabel()
                + " | " + currentUser.getNamaLengkap());
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
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

        // Kontrol akses tombol di ViewData sesuai role
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
        navPanel.setBackground(BG_NAV);
        navPanel.setPreferredSize(new Dimension(210, 0));

        // Logo + info user
        JPanel panelLogo = new JPanel(new BorderLayout());
        panelLogo.setBackground(new Color(15, 30, 80));
        panelLogo.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        JLabel logo = new JLabel("<html><b style='font-size:14px'>🏢 EMS</b><br>"
                + "<small style='color:#aabbdd'>" + currentUser.getNamaLengkap() + "</small><br>"
                + "<small style='color:#8899cc'>" + currentUser.getRoleLabel() + "</small></html>");
        logo.setForeground(Color.WHITE);
        panelLogo.add(logo, BorderLayout.WEST);
        navPanel.add(panelLogo);
        navPanel.add(Box.createVerticalStrut(10));

        // Menu — tampilkan sesuai akses role
        String akses = currentUser.getMenuAkses();

        JButton btnDash = buatNavBtn("🏠  Dashboard", "DASHBOARD");
        navPanel.add(btnDash);
        btnAktif = btnDash;
        setAktif(btnDash);

        if (akses.contains("KARYAWAN")) {
            navPanel.add(buatNavBtn("👤  Data Karyawan", "KARYAWAN"));
        }
        if (akses.contains("KPI")) {
            navPanel.add(buatNavBtn("📊  Penilaian KPI", "KPI"));
        }
        if (akses.contains("ABSENSI")) {
            navPanel.add(buatNavBtn("📋  Absensi", "ABSENSI"));
        }
        if (akses.contains("GAJI")) {
            navPanel.add(buatNavBtn("💰  Penggajian", "GAJI"));
        }

        navPanel.add(Box.createVerticalGlue());

        // Info versi
        JLabel lblVersi = new JLabel("  v1.0 | PBO Project 2025");
        lblVersi.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblVersi.setForeground(new Color(120, 140, 180));
        lblVersi.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(lblVersi);
        navPanel.add(Box.createVerticalStrut(4));

        // Tombol logout
        JButton btnLogout = new JButton("🚪  Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLogout.setForeground(new Color(255, 180, 180));
        btnLogout.setBackground(new Color(100, 30, 30));
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogout.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(new Color(140, 40, 40));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(new Color(100, 30, 30));
            }
        });
        btnLogout.addActionListener(e -> logout());
        navPanel.add(btnLogout);
        navPanel.add(Box.createVerticalStrut(6));

        add(navPanel, BorderLayout.WEST);
    }

    private JButton buatNavBtn(String teks, String panel) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(200, 215, 245));
        btn.setBackground(BG_NAV);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(14, 20, 14, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != btnAktif) {
                    btn.setBackground(BG_NAV_HOVER);
                    btn.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != btnAktif) {
                    btn.setBackground(BG_NAV);
                    btn.setForeground(new Color(200, 215, 245));
                }
            }
        });

        btn.addActionListener(e -> {
            tampilkan(panel);
            setAktif(btn);
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

    private void setAktif(JButton btn) {
        if (btnAktif != null) {
            btnAktif.setBackground(BG_NAV);
            btnAktif.setForeground(new Color(200, 215, 245));
        }
        btnAktif = btn;
        btn.setBackground(BG_NAV_AKTIF);
        btn.setForeground(Color.WHITE);
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
                "Yakin ingin logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
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
        p.setBackground(new Color(245, 247, 251));

        JLabel lbl = new JLabel("<html><center>💰 Modul Penggajian<br><small>Fitur kalkulasi slip gaji & export laporan<br>— Coming Soon —</small></center></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(new Color(100, 110, 140));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnKalkulasi = new JButton("🔢 Kalkulasi Gaji Bulan Ini");
        btnKalkulasi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKalkulasi.setBackground(new Color(34, 100, 180));
        btnKalkulasi.setForeground(Color.WHITE);
        btnKalkulasi.setOpaque(true);
        btnKalkulasi.setBorderPainted(false);
        btnKalkulasi.setFocusPainted(false);
        btnKalkulasi.setPreferredSize(new Dimension(250, 40));
        btnKalkulasi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKalkulasi.addActionListener(e -> kalkulasiGaji());

        JPanel center = new JPanel(new java.awt.GridBagLayout());
        center.setBackground(new Color(245, 247, 251));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridy = 0;
        center.add(lbl, gbc);
        gbc.gridy = 1;
        gbc.insets = new java.awt.Insets(20, 0, 0, 0);
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
