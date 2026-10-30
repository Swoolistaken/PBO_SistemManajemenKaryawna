package model.karyawan;

import java.util.Date;

/**
 * Karyawan dengan tipe kontrak PKWT Implementasi: INHERITANCE (extends
 * Karyawan), POLYMORPHISM
 */
public class ModelKaryawanKontrak extends Karyawan {

    private String jabatan;
    private String departemen;
    private double gajiPokok;
    private double tunjanganTransport;
    private double tunjanganMakan;
    private double tunjanganKesehatan;
    private Date tanggalMulaiKontrak;
    private Date tanggalBerakhirKontrak;
    private int durasiKontrakBulan;
    private String nomorKontrak;
    private boolean bisaDiperpanjang;
    private int perpanjanganKe;

    public ModelKaryawanKontrak() {
        setStatus("AKTIF");
        this.perpanjanganKe = 1;
        this.bisaDiperpanjang = true;
        this.tunjanganTransport = 300000;
        this.tunjanganMakan = 300000;
        this.tunjanganKesehatan = 500000;
    }

    public ModelKaryawanKontrak(String nik, String nama, String jabatan,
            String departemen, double gajiPokok,
            Date mulai, Date berakhir) {
        setNik(nik);
        setNama(nama);
        this.jabatan = jabatan;
        this.departemen = departemen;
        this.gajiPokok = gajiPokok;
        this.tanggalMulaiKontrak = mulai;
        this.tanggalBerakhirKontrak = berakhir;
        setStatus("AKTIF");
        this.perpanjanganKe = 1;
        this.bisaDiperpanjang = true;
        this.tunjanganTransport = 300000;
        this.tunjanganMakan = 300000;
        this.tunjanganKesehatan = 500000;
        hitungDurasi();
    }

    // ===== POLYMORPHISM: implementasi abstract methods =====
    @Override
    public double hitungGajiPokok() {
        return gajiPokok;
    }

    @Override
    public double hitungTunjangan() {
        // Karyawan kontrak dapat tunjangan lebih kecil dari karyawan tetap
        return tunjanganTransport + tunjanganMakan + tunjanganKesehatan;
    }

    @Override
    public String getJabatan() {
        return jabatan;
    }

    @Override
    public String getDepartemen() {
        return departemen;
    }

    // ===== POLYMORPHISM: override hitungTotalGaji =====
    @Override
    public double hitungTotalGaji() {
        // Karyawan kontrak tidak dapat bonus level
        return hitungGajiPokok() + hitungTunjangan();
    }

    @Override
    public String getStatusKaryawan() {
        long sisaHari = hitungSisaHariKontrak();
        return super.getStatusKaryawan()
                + " | Kontrak ke-" + perpanjanganKe
                + " | Sisa: " + (sisaHari >= 0 ? sisaHari + " hari" : "Habis");
    }

    // ===== Business Logic =====
    public long hitungSisaHariKontrak() {
        if (tanggalBerakhirKontrak == null) {
            return -1;
        }
        long diff = tanggalBerakhirKontrak.getTime() - new Date().getTime();
        return diff / (1000 * 60 * 60 * 24);
    }

    public boolean isKontrakHabis() {
        if (tanggalBerakhirKontrak == null) {
            return false;
        }
        return new Date().after(tanggalBerakhirKontrak);
    }

    public boolean isAkanHabis(int hariThreshold) {
        long sisa = hitungSisaHariKontrak();
        return sisa >= 0 && sisa <= hariThreshold;
    }

    private void hitungDurasi() {
        if (tanggalMulaiKontrak == null || tanggalBerakhirKontrak == null) {
            return;
        }
        long diff = tanggalBerakhirKontrak.getTime() - tanggalMulaiKontrak.getTime();
        this.durasiKontrakBulan = (int) (diff / (1000L * 60 * 60 * 24 * 30));
    }

    public String getTipeKaryawan() {
        return "KONTRAK";
    }

    // ===== Getters & Setters =====
    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public void setDepartemen(String departemen) {
        this.departemen = departemen;
    }

    public double getGajiPokok() {
        return gajiPokok;
    }

    public void setGajiPokok(double gajiPokok) {
        if (gajiPokok < 0) {
            throw new IllegalArgumentException("Gaji tidak boleh negatif!");
        }
        this.gajiPokok = gajiPokok;
    }

    public double getTunjanganTransport() {
        return tunjanganTransport;
    }

    public void setTunjanganTransport(double t) {
        this.tunjanganTransport = t;
    }

    public double getTunjanganMakan() {
        return tunjanganMakan;
    }

    public void setTunjanganMakan(double t) {
        this.tunjanganMakan = t;
    }

    public double getTunjanganKesehatan() {
        return tunjanganKesehatan;
    }

    public void setTunjanganKesehatan(double t) {
        this.tunjanganKesehatan = t;
    }

    public Date getTanggalMulaiKontrak() {
        return tanggalMulaiKontrak;
    }

    public void setTanggalMulaiKontrak(Date d) {
        this.tanggalMulaiKontrak = d;
        hitungDurasi();
    }

    public Date getTanggalBerakhirKontrak() {
        return tanggalBerakhirKontrak;
    }

    public void setTanggalBerakhirKontrak(Date d) {
        this.tanggalBerakhirKontrak = d;
        hitungDurasi();
    }

    public int getDurasiKontrakBulan() {
        return durasiKontrakBulan;
    }

    public String getNomorKontrak() {
        return nomorKontrak;
    }

    public void setNomorKontrak(String n) {
        this.nomorKontrak = n;
    }

    public boolean isBisaDiperpanjang() {
        return bisaDiperpanjang;
    }

    public void setBisaDiperpanjang(boolean b) {
        this.bisaDiperpanjang = b;
    }

    public int getPerpanjanganKe() {
        return perpanjanganKe;
    }

    public void setPerpanjanganKe(int p) {
        this.perpanjanganKe = p;
    }
}
