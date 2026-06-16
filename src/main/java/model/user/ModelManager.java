package model.user;

public class ModelManager extends User {

    private String departemen;

    public ModelManager() {
        super();
        setRole("MANAGER");
    }

    public ModelManager(String username, String password, String namaLengkap, String departemen) {
        super(username, password, namaLengkap);
        setRole("MANAGER");
        this.departemen = departemen;
    }

    @Override
    public boolean bisaEdit() {
        return false;
    }

    @Override
    public boolean bisaHapus() {
        return false;
    }

    @Override
    public boolean bisaLihatGaji() {
        return true;
    }

    @Override
    public boolean bisaTambahKaryawan() {
        return false;
    }

    @Override
    public String getMenuAkses() {
        return "DASHBOARD,KARYAWAN,KPI,GAJI";
    }

    @Override
    public String getRoleLabel() {
        return "Manager";
    }

    public String getDepartemen() {
        return departemen;
    }

    public void setDepartemen(String departemen) {
        this.departemen = departemen;
    }
}
