package controller;

import model.kpi.ModelKPI;
import model.kpi.DAOKPI;
import java.sql.SQLException;
import java.util.List;

public class ControllerKPI {

    private final DAOKPI daoKPI = new DAOKPI();

    public interface KPIListener {

        void onSuccess(String pesan);

        void onError(String pesan);

        void onDataLoaded(List<ModelKPI> data);
    }

    public void simpanKPI(ModelKPI kpi, KPIListener listener) {
        try {
            validasi(kpi);
            boolean ok = daoKPI.simpan(kpi);
            if (ok) {
                listener.onSuccess("KPI berhasil disimpan! Grade: " + kpi.getGradeKPI());
            } else {
                listener.onError("Gagal menyimpan KPI.");
            }
        } catch (IllegalArgumentException e) {
            listener.onError("Validasi: " + e.getMessage());
        } catch (SQLException e) {
            listener.onError(e.getMessage());
        }
    }

    public void loadKPIByKaryawan(int karyawanId, KPIListener listener) {
        try {
            List<ModelKPI> list = daoKPI.getByKaryawan(karyawanId);
            listener.onDataLoaded(list);
        } catch (SQLException e) {
            listener.onError(e.getMessage());
        }
    }

    public void loadKPIByPeriode(int periode, int bulan, KPIListener listener) {
        try {
            List<ModelKPI> list = daoKPI.getByPeriode(periode, bulan);
            listener.onDataLoaded(list);
        } catch (SQLException e) {
            listener.onError(e.getMessage());
        }
    }

    public void loadSemuaKPI(KPIListener listener) {
        try {
            List<ModelKPI> list = daoKPI.getAll();
            listener.onDataLoaded(list);
        } catch (SQLException e) {
            listener.onError(e.getMessage());
        }
    }

    public void hapusKPI(int id, KPIListener listener) {
        try {
            boolean ok = daoKPI.hapus(id);
            if (ok) {
                listener.onSuccess("KPI berhasil dihapus!");
            } else {
                listener.onError("Gagal menghapus KPI.");
            }
        } catch (SQLException e) {
            listener.onError(e.getMessage());
        }
    }

    public int getTotalKPI() throws SQLException {
        return daoKPI.getTotal();
    }

    public List<ModelKPI> getAllKPI() throws SQLException {
        return daoKPI.getAll();
    }

    private void validasi(ModelKPI kpi) {
        if (kpi.getKaryawanId() <= 0) {
            throw new IllegalArgumentException("Pilih karyawan terlebih dahulu!");
        }
        if (kpi.getPeriode() < 2000 || kpi.getPeriode() > 2100) {
            throw new IllegalArgumentException("Periode tahun tidak valid!");
        }
        if (kpi.getBulan() < 1 || kpi.getBulan() > 12) {
            throw new IllegalArgumentException("Bulan tidak valid!");
        }
        if (kpi.getPenilai() == null || kpi.getPenilai().isEmpty()) {
            throw new IllegalArgumentException("Nama penilai tidak boleh kosong!");
        }
    }
}
