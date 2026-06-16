package model.user;

public class ModelAdmin extends User {

    public ModelAdmin() {
        super();
        setRole("ADMIN");
    }

    public ModelAdmin(String username, String password, String namaLengkap) {
        super(username, password, namaLengkap);
        setRole("ADMIN");
    }

    @Override
    public boolean bisaEdit() {
        return true;
    }

    @Override
    public boolean bisaHapus() {
        return true;
    }

    @Override
    public boolean bisaLihatGaji() {
        return true;
    }

    @Override
    public boolean bisaTambahKaryawan() {
        return true;
    }

    @Override
    public String getMenuAkses() {
        return "DASHBOARD,KARYAWAN,KPI,ABSENSI,GAJI";
    }

    @Override
    public String getRoleLabel() {
        return "Administrator";
    }
}
