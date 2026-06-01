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

    public void simpanKPIAsync(ModelKPI kpi, KPIListener listener) {
        new Thread(() -> {
            try {
                validasi(kpi);
                boolean ok = daoKPI.simpan(kpi);
                if (ok) {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("KPI berhasil disimpan! Grade: " + kpi.getGradeKPI()));
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal menyimpan KPI."));
                }
            } catch (IllegalArgumentException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Validasi: " + e.getMessage()));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-SimpanKPI").start();
    }

    public void loadKPIByKaryawanAsync(int karyawanId, KPIListener listener) {
        new Thread(() -> {
            try {
                List<ModelKPI> list = daoKPI.getByKaryawan(karyawanId);
                javax.swing.SwingUtilities.invokeLater(() -> listener.onDataLoaded(list));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-LoadKPI").start();
    }

    public void loadKPIByPeriodeAsync(int periode, int bulan, KPIListener listener) {
        new Thread(() -> {
            try {
                List<ModelKPI> list = daoKPI.getByPeriode(periode, bulan);
                javax.swing.SwingUtilities.invokeLater(() -> listener.onDataLoaded(list));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-LoadKPIPeriode").start();
    }

    public void hapusKPIAsync(int id, KPIListener listener) {
        new Thread(() -> {
            try {
                boolean ok = daoKPI.hapus(id);
                if (ok) {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("KPI berhasil dihapus!"));
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal menghapus KPI."));
                }
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-HapusKPI").start();
    }

    public int getTotalKPI() throws SQLException {
        return daoKPI.getTotal();
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
