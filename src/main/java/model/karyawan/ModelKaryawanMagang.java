package model.karyawan;

import java.util.Date;

/**
 * Karyawan dengan tipe magang / internship Implementasi: INHERITANCE (extends
 * Karyawan), POLYMORPHISM
 */
public class ModelKaryawanMagang extends Karyawan {

    private String jabatan;
    private String departemen;
    private double uangSaku;
    private double tunjanganTransport;
    private Date tanggalMulaiMagang;
    private Date tanggalSelesaiMagang;
    private String instansiAsal;       // universitas/sekolah asal
    private String pembimbing;         // nama pembimbing di perusahaan
    private String programStudi;
    private int durasiMagangBulan;
    private boolean sudahSelesai;
    private String nilaiAkhirMagang;   // A, B, C, D

    private static final double UANG_SAKU_DEFAULT = 1500000;
    private static final double TUNJANGAN_TRANSPORT = 300000;

    public ModelKaryawanMagang() {
        setStatus("AKTIF");
        this.uangSaku = UANG_SAKU_DEFAULT;
        this.tunjanganTransport = TUNJANGAN_TRANSPORT;
        this.sudahSelesai = false;
    }

    public ModelKaryawanMagang(String nik, String nama, String jabatan,
            String departemen, String instansiAsal,
            Date mulai, Date selesai) {
        setNik(nik);
        setNama(nama);
        this.jabatan = jabatan;
        this.departemen = departemen;
        this.instansiAsal = instansiAsal;
        this.tanggalMulaiMagang = mulai;
        this.tanggalSelesaiMagang = selesai;
        setStatus("AKTIF");
        this.uangSaku = UANG_SAKU_DEFAULT;
        this.tunjanganTransport = TUNJANGAN_TRANSPORT;
        this.sudahSelesai = false;
        hitungDurasi();
    }

    // ===== POLYMORPHISM: implementasi abstract methods =====
    @Override
    public double hitungGajiPokok() {
        // Magang tidak punya gaji pokok, hanya uang saku
        return uangSaku;
    }

    @Override
    public double hitungTunjangan() {
        // Magang hanya dapat tunjangan transport
        return tunjanganTransport;
    }

    @Override
    public String getJabatan() {
        return jabatan + " (Magang)";
    }

    @Override
    public String getDepartemen() {
        return departemen;
    }

    // ===== POLYMORPHISM: override hitungTotalGaji =====
    @Override
    public double hitungTotalGaji() {
        // Magang hanya dapat uang saku + transport, tidak ada bonus apapun
        return uangSaku + tunjanganTransport;
    }

    @Override
    public String getStatusKaryawan() {
        long sisaHari = hitungSisaMagangHari();
        return super.getStatusKaryawan()
                + " | Magang dari: " + instansiAsal
                + " | Sisa: " + (sisaHari >= 0 ? sisaHari + " hari" : "Selesai");
    }

    // ===== Business Logic =====
    public long hitungSisaMagangHari() {
        if (tanggalSelesaiMagang == null) {
            return -1;
        }
        long diff = tanggalSelesaiMagang.getTime() - new Date().getTime();
        return diff / (1000 * 60 * 60 * 24);
    }

    public boolean isMagangSelesai() {
        if (tanggalSelesaiMagang == null) {
            return false;
        }
        return new Date().after(tanggalSelesaiMagang) || sudahSelesai;
    }

    private void hitungDurasi() {
        if (tanggalMulaiMagang == null || tanggalSelesaiMagang == null) {
            return;
        }
        long diff = tanggalSelesaiMagang.getTime() - tanggalMulaiMagang.getTime();
        this.durasiMagangBulan = (int) (diff / (1000L * 60 * 60 * 24 * 30));
    }

    public String getTipeKaryawan() {
        return "MAGANG";
    }

    // ===== Getters & Setters =====
    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public void setDepartemen(String departemen) {
        this.departemen = departemen;
    }

    public double getUangSaku() {
        return uangSaku;
    }

    public void setUangSaku(double uangSaku) {
        if (uangSaku < 0) {
            throw new IllegalArgumentException("Uang saku tidak boleh negatif!");
        }
        this.uangSaku = uangSaku;
    }

    public double getTunjanganTransport() {
        return tunjanganTransport;
    }

    public void setTunjanganTransport(double t) {
        this.tunjanganTransport = t;
    }

    public Date getTanggalMulaiMagang() {
        return tanggalMulaiMagang;
    }

    public void setTanggalMulaiMagang(Date d) {
        this.tanggalMulaiMagang = d;
        hitungDurasi();
    }

    public Date getTanggalSelesaiMagang() {
        return tanggalSelesaiMagang;
    }

    public void setTanggalSelesaiMagang(Date d) {
        this.tanggalSelesaiMagang = d;
        hitungDurasi();
    }

    public String getInstansiAsal() {
        return instansiAsal;
    }

    public void setInstansiAsal(String instansiAsal) {
        this.instansiAsal = instansiAsal;
    }

    public String getPembimbing() {
        return pembimbing;
    }

    public void setPembimbing(String pembimbing) {
        this.pembimbing = pembimbing;
    }

    public String getProgramStudi() {
        return programStudi;
    }

    public void setProgramStudi(String programStudi) {
        this.programStudi = programStudi;
    }

    public int getDurasiMagangBulan() {
        return durasiMagangBulan;
    }

    public boolean isSudahSelesai() {
        return sudahSelesai;
    }

    public void setSudahSelesai(boolean sudahSelesai) {
        this.sudahSelesai = sudahSelesai;
    }

    public String getNilaiAkhirMagang() {
        return nilaiAkhirMagang;
    }

    public void setNilaiAkhirMagang(String nilai) {
        if (nilai != null && !nilai.matches("[ABCD]")) {
            throw new IllegalArgumentException("Nilai akhir hanya boleh A, B, C, atau D!");
        }
        this.nilaiAkhirMagang = nilai;
    }
}
