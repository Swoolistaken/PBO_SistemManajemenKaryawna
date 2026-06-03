package model.user;

import java.util.Date;

public abstract class User {

    private int id;
    private String username;
    private String password;
    private String namaLengkap;
    private String role;
    private boolean aktif;
    private Date lastLogin;

    public User() {
        this.aktif = true;
    }

    public User(String username, String password, String namaLengkap) {
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.aktif = true;
    }

    // Abstract method
    public abstract boolean bisaEdit();

    public abstract boolean bisaHapus();

    public abstract boolean bisaLihatGaji();

    public abstract boolean bisaTambahKaryawan();

    public abstract String getMenuAkses();

    public abstract String getRoleLabel();

    // Getters & Setters 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong!");
        }
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return namaLengkap + " (" + getRoleLabel() + ")";
    }
}
