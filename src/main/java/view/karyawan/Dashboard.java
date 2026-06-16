package view.karyawan;

import controller.ControllerAbsensi;
import controller.ControllerKaryawan;
import controller.ControllerKPI;
import model.absensi.ModelAbsensi;
import model.karyawan.ModelKaryawan;
import model.kpi.ModelKPI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class Dashboard extends JPanel {

    private final ControllerKaryawan controllerKaryawan;
    private final ControllerKPI controllerKPI;

    private JLabel lblTotalKaryawan, lblKaryawanAktif, lblTotalKPI, lblTotalNonAktif;
    private JLabel lblJam;
    private DefaultTableModel modelDept, modelKPITerbaru;
    private JLabel lblInfoAbsensi;
    private Timer refreshTimer;

    public Dashboard(ControllerKaryawan controllerKaryawan, ControllerKPI controllerKPI) {
        this.controllerKaryawan = controllerKaryawan;
        this.controllerKPI = controllerKPI;
        initUI();
        loadStatistik();
        refreshTimer = new Timer(30000, e -> loadStatistik());
        refreshTimer.start();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buatPanelAtas(), BorderLayout.NORTH);
        add(buatPanelTengah(), BorderLayout.CENTER);
    }

    // ===== Panel atas: kartu statistik + jam =====
    private JPanel buatPanelAtas() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        // Judul + jam
        JPanel panelJudul = new JPanel(new BorderLayout());
        JLabel lblJudul = new JLabel("Dashboard");
        lblJudul.setFont(new Font("Dialog", Font.BOLD, 18));
        lblJam = new JLabel();
        lblJam.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblJam.setHorizontalAlignment(SwingConstants.RIGHT);
        new Timer(1000, e
                -> lblJam.setText(new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy  |  HH:mm:ss",
                        new java.util.Locale("id", "ID")).format(new java.util.Date()))
        ).start();
        panelJudul.add(lblJudul, BorderLayout.WEST);
        panelJudul.add(lblJam, BorderLayout.EAST);
        panel.add(panelJudul, BorderLayout.NORTH);

        // 4 kartu statistik
        JPanel panelKartu = new JPanel(new GridLayout(1, 4, 8, 0));
        lblTotalKaryawan = new JLabel("...", SwingConstants.CENTER);
        lblKaryawanAktif = new JLabel("...", SwingConstants.CENTER);
        lblTotalNonAktif = new JLabel("...", SwingConstants.CENTER);
        lblTotalKPI = new JLabel("...", SwingConstants.CENTER);

        panelKartu.add(buatKartu("Total Karyawan", lblTotalKaryawan));
        panelKartu.add(buatKartu("Karyawan Aktif", lblKaryawanAktif));
        panelKartu.add(buatKartu("Karyawan Nonaktif", lblTotalNonAktif));
        panelKartu.add(buatKartu("Total Penilaian KPI", lblTotalKPI));
        panel.add(panelKartu, BorderLayout.CENTER);

        return panel;
    }

    // ===== Panel tengah: tabel dept + KPI terbaru =====
    private JPanel buatPanelTengah() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Tabel karyawan per departemen
        JPanel panelDept = new JPanel(new BorderLayout(0, 5));
        panelDept.setBorder(BorderFactory.createTitledBorder("Jumlah Karyawan per Departemen"));
        modelDept = new DefaultTableModel(new String[]{"Departemen", "Jumlah"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabelDept = new JTable(modelDept);
        tabelDept.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelDept.add(new JScrollPane(tabelDept), BorderLayout.CENTER);
        panel.add(panelDept);

        // Tabel KPI terbaru
        JPanel panelKPI = new JPanel(new BorderLayout(0, 5));
        panelKPI.setBorder(BorderFactory.createTitledBorder("Penilaian KPI Terbaru"));
        modelKPITerbaru = new DefaultTableModel(
                new String[]{"Nama", "Periode", "Nilai", "Grade"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabelKPI = new JTable(modelKPITerbaru);
        tabelKPI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelKPI.add(new JScrollPane(tabelKPI), BorderLayout.CENTER);
        panel.add(panelKPI);

        return panel;
    }

    private JPanel buatKartu(String judul, JLabel lblNilai) {
        JPanel kartu = new JPanel(new BorderLayout(5, 5));
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(12, 8, 12, 8)
        ));
        JLabel lblJudul = new JLabel(judul, SwingConstants.CENTER);
        lblJudul.setFont(new Font("Dialog", Font.PLAIN, 11));
        lblNilai.setFont(new Font("Dialog", Font.BOLD, 26));
        kartu.add(lblJudul, BorderLayout.NORTH);
        kartu.add(lblNilai, BorderLayout.CENTER);
        return kartu;
    }

    public void loadStatistik() {
        new Thread(() -> {
            try {
                List<ModelKaryawan> semua = controllerKaryawan.getAllKaryawan();
                List<ModelKaryawan> aktif = controllerKaryawan.getKaryawanAktif();
                int totalKPI = controllerKPI.getTotalKPI();

                int nonAktif = (int) semua.stream()
                        .filter(k -> !"AKTIF".equals(k.getStatus())).count();

                // Hitung per departemen
                Map<String, Integer> countDept = new LinkedHashMap<>();
                for (ModelKaryawan k : semua) {
                    countDept.merge(k.getDepartemen(), 1, Integer::sum);
                }

                // KPI terbaru — ambil semua lalu ambil 10 teratas
                List<ModelKPI> semuaKPI = controllerKPI.getAllKPI();

                final int total = semua.size();
                final int jmlAktif = aktif.size();
                final int jmlNon = nonAktif;
                final int jmlKPI = totalKPI;

                SwingUtilities.invokeLater(() -> {
                    lblTotalKaryawan.setText(String.valueOf(total));
                    lblKaryawanAktif.setText(String.valueOf(jmlAktif));
                    lblTotalNonAktif.setText(String.valueOf(jmlNon));
                    lblTotalKPI.setText(String.valueOf(jmlKPI));

                    // Isi tabel dept
                    modelDept.setRowCount(0);
                    countDept.forEach((dept, jml)
                            -> modelDept.addRow(new Object[]{dept, jml})
                    );

                    // Isi tabel KPI terbaru (max 10)
                    modelKPITerbaru.setRowCount(0);
                    int batas = Math.min(semuaKPI.size(), 10);
                    for (int i = 0; i < batas; i++) {
                        ModelKPI kpi = semuaKPI.get(i);
                        modelKPITerbaru.addRow(new Object[]{
                            kpi.getNamaKaryawan(),
                            kpi.getBulan() + "/" + kpi.getPeriode(),
                            String.format("%.1f", kpi.hitungNilaiAkhir()),
                            kpi.getGradeKPI()
                        });
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> lblTotalKaryawan.setText("Error"));
            }
        }, "Thread-Dashboard").start();
    }

    public void stopTimer() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
}
