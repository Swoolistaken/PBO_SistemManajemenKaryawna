package controller;

import model.absensi.ModelAbsensi;
import model.absensi.DAOAbsensi;
import java.sql.SQLException;
import java.util.List;

public class ControllerAbsensi {

    private final DAOAbsensi daoAbsensi = new DAOAbsensi();

    public interface AbsensiListener {

        void onSuccess(String pesan);

        void onError(String pesan);

        void onDataLoaded(List<ModelAbsensi> data);
    }

    public void simpanAbsensi(ModelAbsensi a, AbsensiListener listener) {
        try {
            boolean ok = daoAbsensi.simpan(a);
            if (ok) {
                listener.onSuccess("Absensi berhasil dicatat!");
            } else {
                listener.onError("Gagal mencatat absensi.");
            }
        } catch (SQLException e) {
            listener.onError(e.getMessage());
        }
    }

    public void loadAbsensi(AbsensiListener listener) {
        try {
            List<ModelAbsensi> list = daoAbsensi.getAll();
            listener.onDataLoaded(list);
        } catch (SQLException e) {
            listener.onError(e.getMessage());
        }
    }

    public void hapusAbsensi(int id, AbsensiListener listener) {
        try {
            boolean ok = daoAbsensi.hapus(id);
            if (ok) {
                listener.onSuccess("Absensi berhasil dihapus!");
            } else {
                listener.onError("Gagal menghapus absensi.");
            }
        } catch (SQLException e) {
            listener.onError(e.getMessage());
        }
    }

    public int getTotalAbsensi() throws SQLException {
        return daoAbsensi.getTotal();
    }

    public int getTotalMenitTerlambat(int karyawanId, int bulan, int tahun) throws SQLException {
        return daoAbsensi.getTotalMenitTerlambat(karyawanId, bulan, tahun);
    }

    public int getTotalAlpha(int karyawanId, int bulan, int tahun) throws SQLException {
        return daoAbsensi.getTotalAlpha(karyawanId, bulan, tahun);
    }
}
