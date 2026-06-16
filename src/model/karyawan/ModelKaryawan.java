package model.karyawan;

import java.util.Date;

/**
 * Model Karyawan - Concrete class
 * Implementasi: INHERITANCE (extends AbstractKaryawan), ENCAPSULATION, POLYMORPHISM
 */
public class ModelKaryawan extends AbstractKaryawan {

    // ===== ENCAPSULATION: Additional private fields =====
    private String jabatan;
    private String departemen;
    private double gajiPokok;
    private double tunjanganTransport;
    private double tunjanganMakan;
    private double tunjanganKesehatan;
    private int levelJabatan; // 1=Staff, 2=Supervisor, 3=Manager, 4=Director
    private String pendidikanTerakhir;
    private String keahlian;

    // ===== Constructor =====
    public ModelKaryawan() {
        setStatus("AKTIF");
        setTanggalMasuk(new Date());
    }

    public ModelKaryawan(String nik, String nama, String jabatan, String departemen, double gajiPokok) {
        setNik(nik);
        setNama(nama);
        this.jabatan = jabatan;
        this.departemen = departemen;
        this.gajiPokok = gajiPokok;
        setStatus("AKTIF");
        setTanggalMasuk(new Date());
        hitungTunjanganOtomatis();
    }

    // ===== ABSTRACTION: Implementasi abstract methods =====
    @Override
    public double hitungGajiPokok() {
        return gajiPokok;
    }

    @Override
    public double hitungTunjangan() {
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

    // ===== POLYMORPHISM: Override method dari AbstractKaryawan =====
    @Override
    public double hitungTotalGaji() {
        double total = hitungGajiPokok() + hitungTunjangan();
        // Bonus berdasarkan level jabatan
        if (levelJabatan >= 3) {
            total += gajiPokok * 0.1; // 10% bonus untuk Manager ke atas
        }
        return total;
    }

    @Override
    public String getStatusKaryawan() {
        return super.getStatusKaryawan() + " | Departemen: " + departemen + " | Total Gaji: Rp " + String.format("%,.0f", hitungTotalGaji());
    }

    // ===== Business Logic =====
    public void hitungTunjanganOtomatis() {
        switch (levelJabatan) {
            case 4: // Director
                tunjanganTransport = 2000000;
                tunjanganMakan = 1500000;
                tunjanganKesehatan = 3000000;
                break;
            case 3: // Manager
                tunjanganTransport = 1500000;
                tunjanganMakan = 1000000;
                tunjanganKesehatan = 2000000;
                break;
            case 2: // Supervisor
                tunjanganTransport = 1000000;
                tunjanganMakan = 750000;
                tunjanganKesehatan = 1500000;
                break;
            default: // Staff
                tunjanganTransport = 500000;
                tunjanganMakan = 500000;
                tunjanganKesehatan = 1000000;
        }
    }

    // ===== Getters & Setters =====
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }
    public void setDepartemen(String departemen) { this.departemen = departemen; }

    public double getGajiPokok() { return gajiPokok; }
    public void setGajiPokok(double gajiPokok) {
        if (gajiPokok < 0) throw new IllegalArgumentException("Gaji pokok tidak boleh negatif!");
        this.gajiPokok = gajiPokok;
    }

    public double getTunjanganTransport() { return tunjanganTransport; }
    public void setTunjanganTransport(double tunjanganTransport) { this.tunjanganTransport = tunjanganTransport; }

    public double getTunjanganMakan() { return tunjanganMakan; }
    public void setTunjanganMakan(double tunjanganMakan) { this.tunjanganMakan = tunjanganMakan; }

    public double getTunjanganKesehatan() { return tunjanganKesehatan; }
    public void setTunjanganKesehatan(double tunjanganKesehatan) { this.tunjanganKesehatan = tunjanganKesehatan; }

    public int getLevelJabatan() { return levelJabatan; }
    public void setLevelJabatan(int levelJabatan) {
        this.levelJabatan = levelJabatan;
        hitungTunjanganOtomatis();
    }

    public String getPendidikanTerakhir() { return pendidikanTerakhir; }
    public void setPendidikanTerakhir(String pendidikanTerakhir) { this.pendidikanTerakhir = pendidikanTerakhir; }

    public String getKeahlian() { return keahlian; }
    public void setKeahlian(String keahlian) { this.keahlian = keahlian; }
}
