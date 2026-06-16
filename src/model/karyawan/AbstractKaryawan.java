package model.karyawan;

import java.util.Date;

/**
 * Abstract Base Class untuk semua entitas Person
 * Implementasi: ABSTRACTION, ENCAPSULATION, INHERITANCE (base class)
 */
public abstract class AbstractKaryawan {

    // ===== ENCAPSULATION: Private fields =====
    private int id;
    private String nik;
    private String nama;
    private String email;
    private String noTelp;
    private String alamat;
    private Date tanggalLahir;
    private String jenisKelamin;
    private Date tanggalMasuk;
    private String status; // AKTIF, NONAKTIF, CUTI

    // ===== ABSTRACTION: Abstract methods yang harus diimplementasi subclass =====
    public abstract double hitungGajiPokok();
    public abstract double hitungTunjangan();
    public abstract String getJabatan();
    public abstract String getDepartemen();

    // ===== Concrete method di abstract class =====
    public double hitungTotalGaji() {
        return hitungGajiPokok() + hitungTunjangan();
    }

    public String getStatusKaryawan() {
        return "NIK: " + nik + " | Nama: " + nama + " | Jabatan: " + getJabatan();
    }

    // ===== ENCAPSULATION: Getters & Setters =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNik() { return nik; }
    public void setNik(String nik) {
        if (nik == null || nik.trim().isEmpty()) {
            throw new IllegalArgumentException("NIK tidak boleh kosong!");
        }
        this.nik = nik.trim();
    }

    public String getNama() { return nama; }
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong!");
        }
        this.nama = nama.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email != null && !email.isEmpty() && !email.contains("@")) {
            throw new IllegalArgumentException("Format email tidak valid!");
        }
        this.email = email;
    }

    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public Date getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(Date tanggalLahir) { this.tanggalLahir = tanggalLahir; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public Date getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(Date tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "[" + getNik() + "] " + getNama() + " - " + getJabatan();
    }
}
