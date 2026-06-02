package view.karyawan;

import controller.ControllerKaryawan;
import controller.ControllerKPI;
import model.karyawan.ModelKaryawan;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Dashboard extends JPanel {

    private final ControllerKaryawan controllerKaryawan;
    private final ControllerKPI controllerKPI;

    private JLabel lblTotalKaryawan;
    private JLabel lblKaryawanAktif;
    private JLabel lblTotalKPI;
    private JLabel lblJam;
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
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Judul
        JLabel lblJudul = new JLabel("Dashboard");
        lblJudul.setFont(new Font("Dialog", Font.BOLD, 18));
        add(lblJudul, BorderLayout.NORTH);

        // Panel statistik
        JPanel panelStat = new JPanel(new GridLayout(1, 3, 10, 0));

        lblTotalKaryawan = new JLabel("...", SwingConstants.CENTER);
        lblKaryawanAktif = new JLabel("...", SwingConstants.CENTER);
        lblTotalKPI = new JLabel("...", SwingConstants.CENTER);

        panelStat.add(buatKartu("Total Karyawan", lblTotalKaryawan));
        panelStat.add(buatKartu("Karyawan Aktif", lblKaryawanAktif));
        panelStat.add(buatKartu("Total Penilaian KPI", lblTotalKPI));

        // Jam
        JPanel panelJam = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblJam = new JLabel();
        lblJam.setFont(new Font("Dialog", Font.BOLD, 14));
        panelJam.add(new JLabel("Waktu saat ini: "));
        panelJam.add(lblJam);
        new Timer(1000, e
                -> lblJam.setText(new java.text.SimpleDateFormat("HH:mm:ss — dd/MM/yyyy")
                        .format(new java.util.Date()))
        ).start();

        JPanel panelTengah = new JPanel(new BorderLayout(0, 10));
        panelTengah.add(panelStat, BorderLayout.NORTH);
        panelTengah.add(panelJam, BorderLayout.SOUTH);

        add(panelTengah, BorderLayout.CENTER);
    }

    private JPanel buatKartu(String judul, JLabel lblNilai) {
        JPanel kartu = new JPanel(new BorderLayout(5, 5));
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        JLabel lblJudul = new JLabel(judul, SwingConstants.CENTER);
        lblJudul.setFont(new Font("Dialog", Font.PLAIN, 12));

        lblNilai.setFont(new Font("Dialog", Font.BOLD, 28));

        kartu.add(lblJudul, BorderLayout.NORTH);
        kartu.add(lblNilai, BorderLayout.CENTER);
        return kartu;
    }

    public void loadStatistik() {
        new Thread(() -> {
            try {
                int total = controllerKaryawan.getTotalKaryawan();
                int aktif = controllerKaryawan.getKaryawanAktif().size();
                int totalKPI = controllerKPI.getTotalKPI();

                SwingUtilities.invokeLater(() -> {
                    lblTotalKaryawan.setText(String.valueOf(total));
                    lblKaryawanAktif.setText(String.valueOf(aktif));
                    lblTotalKPI.setText(String.valueOf(totalKPI));
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
