package com.mycompany.employeemanagement;

import view.karyawan.ViewAbsensi;
import view.karyawan.EditData;
import view.karyawan.ViewData;
import view.karyawan.Dashboard;
import view.karyawan.InputData;
import controller.ControllerKaryawan;
import model.karyawan.ModelKaryawan;
import view.karyawan.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main Frame Aplikasi - Entry point GUI Implementasi: GUI SWING, MULTITHREAD
 * (SwingUtilities.invokeLater)
 */
public class Main extends JFrame {

    private final ControllerKaryawan controller = new ControllerKaryawan();

    private JPanel panelKonten;
    private CardLayout cardLayout;

    // Panel-panel utama
    private Dashboard dashboard;
    private ViewData viewData;
    private EditData editData;
    private ViewAbsensi viewAbsensi;

    // Tombol navigasi aktif
    private JButton btnAktif;
    private final Color BG_NAV = new Color(25, 45, 110);
    private final Color BG_NAV_HOVER = new Color(45, 75, 160);
    private final Color BG_NAV_AKTIF = new Color(70, 120, 220);

    public Main() {
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
        setTitle("Sistem Manajemen Karyawan — PBO Project");
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

        dashboard = new Dashboard(controller);
        viewData = new ViewData(controller);
        editData = new EditData(controller);
        viewAbsensi = new ViewAbsensi(controller);

        // Callback untuk navigasi antar panel
        viewData.setCallback(new ViewData.ActionCallback() {
            @Override
            public void onTambah() {
                InputData form = new InputData(Main.this, controller);
                form.setVisible(true);
                if (form.isSaved()) {
                    viewData.loadData();
                    dashboard.loadStatistik();
                    editData.refreshKaryawan();
                    viewAbsensi.refreshKaryawan();
                }
            }

            @Override
            public void onEdit(ModelKaryawan k) {
                InputData form = new InputData(Main.this, controller, k);
                form.setVisible(true);
                if (form.isSaved()) {
                    viewData.loadData();
                    dashboard.loadStatistik();
                    editData.refreshKaryawan();
                    viewAbsensi.refreshKaryawan();
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

        panelKonten.add(dashboard, "DASHBOARD");
        panelKonten.add(viewData, "KARYAWAN");
        panelKonten.add(editData, "KPI");
        panelKonten.add(viewAbsensi, "ABSENSI");
//        panelKonten.add(buatPanelGaji(), "GAJI");

        add(panelKonten, BorderLayout.CENTER);
    }

    private void initNav() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(BG_NAV);
        navPanel.setPreferredSize(new Dimension(210, 0));

        // Logo
        JPanel panelLogo = new JPanel(new BorderLayout());
        panelLogo.setBackground(new Color(15, 30, 80));
        panelLogo.setBorder(new EmptyBorder(20, 15, 20, 15));
        panelLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JLabel logo = new JLabel("<html><b style='font-size:14px'>🏢 EMS</b><br><small>Employee Management</small></html>");
        logo.setForeground(Color.WHITE);
        panelLogo.add(logo, BorderLayout.WEST);
        navPanel.add(panelLogo);
        navPanel.add(Box.createVerticalStrut(10));

        // Menu items
        JButton btnDash = buatNavBtn("🏠  Dashboard", "DASHBOARD");
        JButton btnKaryawan = buatNavBtn("👤  Data Karyawan", "KARYAWAN");
        JButton btnKPI = buatNavBtn("📊  Penilaian KPI", "KPI");
        JButton btnAbsensi = buatNavBtn("📋  Absensi", "ABSENSI");
        JButton btnGaji = buatNavBtn("💰  Penggajian", "GAJI");

        navPanel.add(btnDash);
        navPanel.add(btnKaryawan);
        navPanel.add(btnKPI);
        navPanel.add(btnAbsensi);
        navPanel.add(btnGaji);
        navPanel.add(Box.createVerticalGlue());

        // Info versi
        JLabel lblVersi = new JLabel("  v1.0 | PBO Project 2025");
        lblVersi.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblVersi.setForeground(new Color(120, 140, 180));
        lblVersi.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(lblVersi);
        navPanel.add(Box.createVerticalStrut(10));

        add(navPanel, BorderLayout.WEST);
        btnAktif = btnDash;
        setAktif(btnDash);
    }

    private JButton buatNavBtn(String teks, String panel) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // ===== FIX: set eksplisit agar tidak ngeblend =====
        btn.setForeground(new Color(200, 215, 245));  // teks terang
        btn.setBackground(BG_NAV);
        btn.setOpaque(true);        
        btn.setBorderPainted(false); 
        btn.setContentAreaFilled(true);
        // ===================================================

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
            if ("KONTRAK".equals(panel)) {
//                viewKontrak.refreshKaryawan();
            }
        });

        return btn;
    }

// ===== GANTI method setAktif dengan ini =====
    private void setAktif(JButton btn) {
        if (btnAktif != null) {
            btnAktif.setBackground(BG_NAV);
            btnAktif.setForeground(new Color(200, 215, 245));
        }
        btnAktif = btn;
        btn.setBackground(BG_NAV_AKTIF);
        btn.setForeground(Color.WHITE);   // teks putih penuh saat aktif
    }

    private void tampilkan(String nama) {
        cardLayout.show(panelKonten, nama);
    }

//    private JPanel buatPanelGaji() {
//        JPanel p = new JPanel(new BorderLayout());
//        p.setBackground(new Color(245, 247, 251));
//
//        JLabel lbl = new JLabel("<html><center>💰 Modul Penggajian<br><small>Fitur kalkulasi slip gaji & export laporan<br>— Coming Soon —</small></center></html>");
//        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        lbl.setForeground(new Color(100, 110, 140));
//        lbl.setHorizontalAlignment(SwingConstants.CENTER);
//
//        // Simulasi kalkulasi gaji
//        JButton btnKalkulasi = new JButton("🔢 Kalkulasi Gaji Bulan Ini");
//        btnKalkulasi.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btnKalkulasi.setBackground(new Color(34, 100, 180));
//        btnKalkulasi.setForeground(Color.WHITE);
//        btnKalkulasi.setFocusPainted(false);
//        btnKalkulasi.setPreferredSize(new Dimension(250, 40));
//        btnKalkulasi.addActionListener(e -> kalkulasiGaji());
//
//        JPanel center = new JPanel(new GridBagLayout());
//        center.setBackground(new Color(245, 247, 251));
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridy = 0;
//        center.add(lbl, gbc);
//        gbc.gridy = 1;
//        gbc.insets = new Insets(20, 0, 0, 0);
//        center.add(btnKalkulasi, gbc);
//
//        p.add(center, BorderLayout.CENTER);
//        return p;
//    }
    private void kalkulasiGaji() {
        new Thread(() -> {
            try {
                java.util.List<ModelKaryawan> list = controller.getAllKaryawan();
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
                        -> JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }, "Thread-Penggajian").start();
    }

    // ===== Entry Point =====
    public static void main(String[] args) {
        // MULTITHREAD: Jalankan GUI di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
