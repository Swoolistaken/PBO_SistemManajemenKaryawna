package model.karyawan;

import java.util.Date;

/**
 * Model Absensi Karyawan
 * Implementasi: ENCAPSULATION
 */
public class ModelAbsensi {

    public enum StatusAbsensi {
        HADIR, IZIN, SAKIT, ALPHA, CUTI, DINAS_LUAR, WORK_FROM_HOME
    }

    private int id;
    private int karyawanId;
    private String nikKaryawan;
    private String namaKaryawan;
    private Date tanggal;
    private String jamMasuk;
    private String jamKeluar;
    private StatusAbsensi status;
    private String keterangan;
    private boolean terlambat;
    private int menitTerlambat;
    private boolean pulangAwal;

    public ModelAbsensi() {
        this.tanggal = new Date();
        this.status = StatusAbsensi.HADIR;
    }

    public ModelAbsensi(int karyawanId, String nikKaryawan, String namaKaryawan) {
        this.karyawanId = karyawanId;
        this.nikKaryawan = nikKaryawan;
        this.namaKaryawan = namaKaryawan;
        this.tanggal = new Date();
        this.status = StatusAbsensi.HADIR;
    }

    // Business logic
    public double hitungPotonganKeterlambatan(double gajiHarian) {
        if (!terlambat || menitTerlambat == 0) return 0;
        if (menitTerlambat <= 30) return gajiHarian * 0.05;
        if (menitTerlambat <= 60) return gajiHarian * 0.10;
        return gajiHarian * 0.25;
    }

    public boolean isHadir() {
        return status == StatusAbsensi.HADIR || status == StatusAbsensi.DINAS_LUAR || status == StatusAbsensi.WORK_FROM_HOME;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKaryawanId() { return karyawanId; }
    public void setKaryawanId(int karyawanId) { this.karyawanId = karyawanId; }

    public String getNikKaryawan() { return nikKaryawan; }
    public void setNikKaryawan(String nikKaryawan) { this.nikKaryawan = nikKaryawan; }

    public String getNamaKaryawan() { return namaKaryawan; }
    public void setNamaKaryawan(String namaKaryawan) { this.namaKaryawan = namaKaryawan; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public String getJamMasuk() { return jamMasuk; }
    public void setJamMasuk(String jamMasuk) { this.jamMasuk = jamMasuk; }

    public String getJamKeluar() { return jamKeluar; }
    public void setJamKeluar(String jamKeluar) { this.jamKeluar = jamKeluar; }

    public StatusAbsensi getStatus() { return status; }
    public void setStatus(StatusAbsensi status) { this.status = status; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public boolean isTerlambat() { return terlambat; }
    public void setTerlambat(boolean terlambat) { this.terlambat = terlambat; }

    public int getMenitTerlambat() { return menitTerlambat; }
    public void setMenitTerlambat(int menitTerlambat) { this.menitTerlambat = menitTerlambat; }

    public boolean isPulangAwal() { return pulangAwal; }
    public void setPulangAwal(boolean pulangAwal) { this.pulangAwal = pulangAwal; }
}
