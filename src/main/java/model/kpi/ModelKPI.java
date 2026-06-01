package model.kpi;

import java.util.Date;

public class ModelKPI {

    public enum KategoriKPI {
        PRODUKTIVITAS("Produktivitas", 30),
        KUALITAS("Kualitas Kerja", 25),
        KEHADIRAN("Kehadiran & Disiplin", 20),
        TEAMWORK("Kerjasama Tim", 15),
        INOVASI("Inovasi & Inisiatif", 10);

        private final String label;
        private final int bobot; // persentase bobot

        KategoriKPI(String label, int bobot) {
            this.label = label;
            this.bobot = bobot;
        }

        public String getLabel() {
            return label;
        }

        public int getBobot() {
            return bobot;
        }
    }

    private int id;
    private int karyawanId;
    private String nikKaryawan;
    private String namaKaryawan;
    private int periode; // Tahun
    private int bulan;   // 1-12
    private double nilaiProduktivitas;  // 0-100
    private double nilaiKualitas;
    private double nilaiKehadiran;
    private double nilaiTeamwork;
    private double nilaiInovasi;
    private String catatanAtasan;
    private String targetPeriodeBerikutnya;
    private Date tanggalPenilaian;
    private String penilai;

    // ===== Constructor =====
    public ModelKPI() {
        this.tanggalPenilaian = new Date();
    }

    public ModelKPI(int karyawanId, String nikKaryawan, String namaKaryawan, int periode, int bulan) {
        this.karyawanId = karyawanId;
        this.nikKaryawan = nikKaryawan;
        this.namaKaryawan = namaKaryawan;
        this.periode = periode;
        this.bulan = bulan;
        this.tanggalPenilaian = new Date();
    }

    public double hitungNilaiAkhir() {
        return (nilaiProduktivitas * KategoriKPI.PRODUKTIVITAS.getBobot() / 100.0)
                + (nilaiKualitas * KategoriKPI.KUALITAS.getBobot() / 100.0)
                + (nilaiKehadiran * KategoriKPI.KEHADIRAN.getBobot() / 100.0)
                + (nilaiTeamwork * KategoriKPI.TEAMWORK.getBobot() / 100.0)
                + (nilaiInovasi * KategoriKPI.INOVASI.getBobot() / 100.0);
    }

    public String getGradeKPI() {
        double nilai = hitungNilaiAkhir();
        if (nilai >= 90) {
            return "A+ (Luar Biasa)";
        }
        if (nilai >= 80) {
            return "A (Sangat Baik)";
        }
        if (nilai >= 70) {
            return "B (Baik)";
        }
        if (nilai >= 60) {
            return "C (Cukup)";
        }
        if (nilai >= 50) {
            return "D (Perlu Perbaikan)";
        }
        return "E (Tidak Memenuhi Standar)";
    }

    public double getBonusKPI(double gajiPokok) {
        double nilai = hitungNilaiAkhir();
        if (nilai >= 90) {
            return gajiPokok * 0.20;
        }
        if (nilai >= 80) {
            return gajiPokok * 0.15;
        }
        if (nilai >= 70) {
            return gajiPokok * 0.10;
        }
        if (nilai >= 60) {
            return gajiPokok * 0.05;
        }
        return 0;
    }

    public String getStatusKPI() {
        double nilai = hitungNilaiAkhir();
        if (nilai >= 70) {
            return "LULUS";
        }
        return "TIDAK LULUS";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKaryawanId() {
        return karyawanId;
    }

    public void setKaryawanId(int karyawanId) {
        this.karyawanId = karyawanId;
    }

    public String getNikKaryawan() {
        return nikKaryawan;
    }

    public void setNikKaryawan(String nikKaryawan) {
        this.nikKaryawan = nikKaryawan;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public int getPeriode() {
        return periode;
    }

    public void setPeriode(int periode) {
        this.periode = periode;
    }

    public int getBulan() {
        return bulan;
    }

    public void setBulan(int bulan) {
        if (bulan < 1 || bulan > 12) {
            throw new IllegalArgumentException("Bulan harus antara 1-12!");
        }
        this.bulan = bulan;
    }

    public double getNilaiProduktivitas() {
        return nilaiProduktivitas;
    }

    public void setNilaiProduktivitas(double nilaiProduktivitas) {
        if (nilaiProduktivitas < 0 || nilaiProduktivitas > 100) {
            throw new IllegalArgumentException("Nilai harus antara 0-100!");
        }
        this.nilaiProduktivitas = nilaiProduktivitas;
    }

    public double getNilaiKualitas() {
        return nilaiKualitas;
    }

    public void setNilaiKualitas(double nilaiKualitas) {
        if (nilaiKualitas < 0 || nilaiKualitas > 100) {
            throw new IllegalArgumentException("Nilai harus antara 0-100!");
        }
        this.nilaiKualitas = nilaiKualitas;
    }

    public double getNilaiKehadiran() {
        return nilaiKehadiran;
    }

    public void setNilaiKehadiran(double nilaiKehadiran) {
        if (nilaiKehadiran < 0 || nilaiKehadiran > 100) {
            throw new IllegalArgumentException("Nilai harus antara 0-100!");
        }
        this.nilaiKehadiran = nilaiKehadiran;
    }

    public double getNilaiTeamwork() {
        return nilaiTeamwork;
    }

    public void setNilaiTeamwork(double nilaiTeamwork) {
        if (nilaiTeamwork < 0 || nilaiTeamwork > 100) {
            throw new IllegalArgumentException("Nilai harus antara 0-100!");
        }
        this.nilaiTeamwork = nilaiTeamwork;
    }

    public double getNilaiInovasi() {
        return nilaiInovasi;
    }

    public void setNilaiInovasi(double nilaiInovasi) {
        if (nilaiInovasi < 0 || nilaiInovasi > 100) {
            throw new IllegalArgumentException("Nilai harus antara 0-100!");
        }
        this.nilaiInovasi = nilaiInovasi;
    }

    public String getCatatanAtasan() {
        return catatanAtasan;
    }

    public void setCatatanAtasan(String catatanAtasan) {
        this.catatanAtasan = catatanAtasan;
    }

    public String getTargetPeriodeBerikutnya() {
        return targetPeriodeBerikutnya;
    }

    public void setTargetPeriodeBerikutnya(String targetPeriodeBerikutnya) {
        this.targetPeriodeBerikutnya = targetPeriodeBerikutnya;
    }

    public Date getTanggalPenilaian() {
        return tanggalPenilaian;
    }

    public void setTanggalPenilaian(Date tanggalPenilaian) {
        this.tanggalPenilaian = tanggalPenilaian;
    }

    public String getPenilai() {
        return penilai;
    }

    public void setPenilai(String penilai) {
        this.penilai = penilai;
    }
}
