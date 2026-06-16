package model.user;

public class ModelHRD extends User {

    public ModelHRD() {
        super();
        setRole("HRD");
    }

    public ModelHRD(String username, String password, String namaLengkap) {
        super(username, password, namaLengkap);
        setRole("HRD");
    }

    @Override
    public boolean bisaEdit() {
        return true;
    }

    @Override
    public boolean bisaHapus() {
        return false;
    }

    @Override
    public boolean bisaLihatGaji() {
        return false;
    }

    @Override
    public boolean bisaTambahKaryawan() {
        return true;
    }

    @Override
    public String getMenuAkses() {
        return "DASHBOARD,KARYAWAN,KPI,ABSENSI";
    }

    @Override
    public String getRoleLabel() {
        return "HRD";
    }
}
