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

    public void simpanAbsensiAsync(ModelAbsensi a, AbsensiListener listener) {
        new Thread(() -> {
            try {
                boolean ok = daoAbsensi.simpan(a);
                if (ok) {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("Absensi berhasil dicatat!"));
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal mencatat absensi."));
                }
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-SimpanAbsensi").start();
    }

    public void loadAbsensiAsync(AbsensiListener listener) {
        new Thread(() -> {
            try {
                List<ModelAbsensi> list = daoAbsensi.getAll();
                javax.swing.SwingUtilities.invokeLater(() -> listener.onDataLoaded(list));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-LoadAbsensi").start();
    }

    public void hapusAbsensiAsync(int id, AbsensiListener listener) {
        new Thread(() -> {
            try {
                boolean ok = daoAbsensi.hapus(id);
                if (ok) {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("Absensi berhasil dihapus!"));
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal menghapus absensi."));
                }
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-HapusAbsensi").start();
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
