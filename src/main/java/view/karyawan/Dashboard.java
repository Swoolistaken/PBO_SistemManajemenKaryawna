package view.karyawan;

import controller.ControllerKaryawan;
import model.karyawan.ModelKaryawan;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Panel Dashboard - ringkasan statistik
 */
public class Dashboard extends JPanel {

    private final ControllerKaryawan controller;
    private JLabel lblTotalKaryawan, lblTotalKPI, lblKaryawanAktif, lblDeptTerbanyak;
    private JPanel panelGrafikDept;
    private Timer refreshTimer;

    public Dashboard(ControllerKaryawan controller) {
        this.controller = controller;
        initUI();
        loadStatistik();
        // Auto refresh setiap 30 detik (MultiThread via Timer)
        refreshTimer = new Timer(30000, e -> loadStatistik());
        refreshTimer.start();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 244, 252));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buatHeaderWelcome(), BorderLayout.NORTH);

        JPanel panelTengah = new JPanel(new BorderLayout(15, 15));
        panelTengah.setOpaque(false);
        panelTengah.add(buatPanelStatKartu(), BorderLayout.NORTH);
        panelTengah.add(buatPanelGrafik(), BorderLayout.CENTER);
        add(panelTengah, BorderLayout.CENTER);

        add(buatPanelFitur(), BorderLayout.SOUTH);
    }

    private JPanel buatHeaderWelcome() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 55, 130));
        p.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblWelcome = new JLabel("🏢 Sistem Manajemen Karyawan");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Employee Management System — Kelola karyawan dengan mudah & efisien");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(180, 200, 240));

        JPanel kiri = new JPanel(new GridLayout(2, 1, 0, 4));
        kiri.setOpaque(false);
        kiri.add(lblWelcome);
        kiri.add(lblSub);
        p.add(kiri, BorderLayout.WEST);

        // Jam real-time
        JLabel lblJam = new JLabel();
        lblJam.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        lblJam.setForeground(new Color(150, 210, 255));
        lblJam.setHorizontalAlignment(SwingConstants.RIGHT);
        new Timer(1000, e -> {
            lblJam.setText(new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
        }).start();
        p.add(lblJam, BorderLayout.EAST);

        return p;
    }

    private JPanel buatPanelStatKartu() {
        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);

        lblTotalKaryawan = buatKartu(p, "👥 Total Karyawan", "...", new Color(30, 100, 200));
        lblKaryawanAktif = buatKartu(p, "✅ Karyawan Aktif", "...", new Color(34, 139, 34));
        lblTotalKPI      = buatKartu(p, "📊 Total Penilaian KPI", "...", new Color(180, 80, 0));
        lblDeptTerbanyak = buatKartu(p, "🏆 Dept. Terbesar", "...", new Color(120, 30, 160));

        return p;
    }

    private JLabel buatKartu(JPanel parent, String judul, String nilai, Color warna) {
        JPanel kartu = new JPanel(new BorderLayout(0, 8));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(new CompoundBorder(
            new LineBorder(warna, 2, true),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblJudul.setForeground(new Color(80, 90, 110));

        JLabel lblNilai = new JLabel(nilai);
        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblNilai.setForeground(warna);

        JPanel garis = new JPanel();
        garis.setBackground(warna);
        garis.setPreferredSize(new Dimension(0, 4));

        kartu.add(garis, BorderLayout.NORTH);
        kartu.add(lblJudul, BorderLayout.CENTER);
        kartu.add(lblNilai, BorderLayout.SOUTH);

        parent.add(kartu);
        return lblNilai;
    }

    private JPanel buatPanelGrafik() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(210, 220, 240), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel judul = new JLabel("📈 Distribusi Karyawan per Departemen");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 14));
        judul.setForeground(new Color(30, 50, 110));
        judul.setBorder(new MatteBorder(0, 0, 1, 0, new Color(200, 215, 240)));
        p.add(judul, BorderLayout.NORTH);

        panelGrafikDept = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                gambarGrafik(g);
            }
        };
        panelGrafikDept.setBackground(Color.WHITE);
        panelGrafikDept.setPreferredSize(new Dimension(0, 200));
        p.add(panelGrafikDept, BorderLayout.CENTER);

        return p;
    }

    private int[] dataDept = new int[8];
    private final String[] DEPT = {"IT", "HRD", "Finance", "Marketing", "Operations", "Legal", "Procurement", "R&D"};
    private final Color[] WARNA_DEPT = {
        new Color(30, 100, 210), new Color(34, 139, 34), new Color(200, 100, 0),
        new Color(150, 30, 160), new Color(0, 150, 150), new Color(180, 50, 50),
        new Color(100, 130, 30), new Color(50, 50, 160)
    };

    private void gambarGrafik(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int maxVal = 1;
        for (int v : dataDept) if (v > maxVal) maxVal = v;

        int w = panelGrafikDept.getWidth();
        int h = panelGrafikDept.getHeight();
        int marginLeft = 40, marginBottom = 50, marginTop = 20;
        int graphW = w - marginLeft - 20;
        int graphH = h - marginBottom - marginTop;
        int barW = graphW / (DEPT.length * 2);

        g2.setColor(new Color(240, 243, 250));
        g2.fillRect(marginLeft, marginTop, graphW, graphH);

        // Grid lines
        g2.setColor(new Color(210, 215, 230));
        for (int i = 0; i <= 5; i++) {
            int y = marginTop + graphH - (graphH * i / 5);
            g2.drawLine(marginLeft, y, marginLeft + graphW, y);
            g2.setColor(new Color(130, 140, 160));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(String.valueOf(maxVal * i / 5), 2, y + 4);
            g2.setColor(new Color(210, 215, 230));
        }

        for (int i = 0; i < DEPT.length; i++) {
            int x = marginLeft + i * (graphW / DEPT.length) + (graphW / DEPT.length - barW) / 2;
            int barH = dataDept[i] == 0 ? 0 : Math.max(5, (int)(((double) dataDept[i] / maxVal) * graphH));
            int y = marginTop + graphH - barH;

            g2.setColor(WARNA_DEPT[i]);
            g2.fillRoundRect(x, y, barW, barH, 6, 6);
            g2.setColor(WARNA_DEPT[i].darker());
            g2.drawRoundRect(x, y, barW, barH, 6, 6);

            g2.setColor(new Color(50, 60, 90));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.drawString(String.valueOf(dataDept[i]), x + barW/2 - 5, y - 3);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + barW/2 - fm.stringWidth(DEPT[i])/2;
            g2.drawString(DEPT[i], tx, h - 10);
        }
    }

    private JPanel buatPanelFitur() {
        JPanel p = new JPanel(new GridLayout(1, 5, 10, 0));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, 70));

        String[][] fitur = {
            {"👤", "Data Karyawan", "Kelola data karyawan"},
            {"📊", "Penilaian KPI", "Evaluasi performa"},
            {"📋", "Absensi", "Rekap kehadiran"},
            {"💰", "Penggajian", "Kalkulasi gaji"},
            {"📈", "Laporan", "Ringkasan data"}
        };

        for (String[] f : fitur) {
            JPanel kartu = new JPanel(new BorderLayout(5, 2));
            kartu.setBackground(Color.WHITE);
            kartu.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 220, 240), 1, true),
                new EmptyBorder(8, 12, 8, 12)
            ));
            kartu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(f[0] + " " + f[1]);
            icon.setFont(new Font("Segoe UI", Font.BOLD, 13));
            icon.setForeground(new Color(30, 60, 130));

            JLabel sub = new JLabel(f[2]);
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            sub.setForeground(new Color(100, 110, 130));

            kartu.add(icon, BorderLayout.CENTER);
            kartu.add(sub, BorderLayout.SOUTH);

            kartu.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { kartu.setBackground(new Color(235, 243, 255)); }
                @Override public void mouseExited(MouseEvent e)  { kartu.setBackground(Color.WHITE); }
            });

            p.add(kartu);
        }
        return p;
    }

    public void loadStatistik() {
        new Thread(() -> {
            try {
                int total = controller.getTotalKaryawan();
                int totalKPI = controller.getTotalKPI();
                List<ModelKaryawan> aktif = controller.getKaryawanAktif();
                List<ModelKaryawan> semua = controller.getAllKaryawan();

                // Hitung per dept
                java.util.Map<String, Integer> countDept = new java.util.LinkedHashMap<>();
                for (String d : DEPT) countDept.put(d, 0);
                for (ModelKaryawan k : semua) {
                    if (countDept.containsKey(k.getDepartemen()))
                        countDept.put(k.getDepartemen(), countDept.get(k.getDepartemen()) + 1);
                }

                // Dept terbesar
                String deptMax = "-";
                int maxCount = 0;
                for (java.util.Map.Entry<String, Integer> e : countDept.entrySet()) {
                    if (e.getValue() > maxCount) { maxCount = e.getValue(); deptMax = e.getKey(); }
                }
                final String deptFinal = deptMax + " (" + maxCount + ")";
                for (int i = 0; i < DEPT.length; i++) dataDept[i] = countDept.getOrDefault(DEPT[i], 0);

                final int t = total, a = aktif.size(), kpi = totalKPI;
                SwingUtilities.invokeLater(() -> {
                    lblTotalKaryawan.setText(String.valueOf(t));
                    lblKaryawanAktif.setText(String.valueOf(a));
                    lblTotalKPI.setText(String.valueOf(kpi));
                    lblDeptTerbanyak.setText(deptFinal);
                    panelGrafikDept.repaint();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    lblTotalKaryawan.setText("DB Error");
                });
            }
        }, "Thread-Dashboard").start();
    }

    public void stopTimer() {
        if (refreshTimer != null) refreshTimer.stop();
    }
}
