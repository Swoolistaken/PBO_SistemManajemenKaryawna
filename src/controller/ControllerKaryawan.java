package controller;

import model.karyawan.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller utama untuk semua operasi bisnis Karyawan
 * Implementasi: EXCEPTION HANDLING, MULTITHREAD (background tasks)
 */
public class ControllerKaryawan {

    // ===== ENCAPSULATION: Private DAO instances =====
    private final DAO daoKaryawan = new DAO();
    private final DAOKPI daoKPI = new DAOKPI();
    private final DAOAbsensi daoAbsensi = new DAOAbsensi();

    // ===== Listener interface untuk update UI dari thread lain =====
    public interface DataListener {
        void onSuccess(String pesan);
        void onError(String pesan);
        void onDataLoaded(List<ModelKaryawan> data);
    }

    public interface KPIListener {
        void onSuccess(String pesan);
        void onError(String pesan);
        void onDataLoaded(List<ModelKPI> data);
    }

    public interface AbsensiListener {
        void onSuccess(String pesan);
        void onError(String pesan);
        void onDataLoaded(List<ModelAbsensi> data);
    }

    // ===================================================================
    // ===== MULTITHREAD: Load data di background thread =====
    // ===================================================================

    public void loadAllKaryawanAsync(DataListener listener) {
        new Thread(() -> {
            try {
                List<ModelKaryawan> list = daoKaryawan.getAll();
                javax.swing.SwingUtilities.invokeLater(() -> listener.onDataLoaded(list));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-LoadKaryawan").start();
    }

    public void cariKaryawanAsync(String keyword, DataListener listener) {
        new Thread(() -> {
            try {
                List<ModelKaryawan> list = keyword.isEmpty()
                        ? daoKaryawan.getAll()
                        : daoKaryawan.cari(keyword);
                javax.swing.SwingUtilities.invokeLater(() -> listener.onDataLoaded(list));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-CariKaryawan").start();
    }

    public void loadKaryawanByDeptAsync(String dept, DataListener listener) {
        new Thread(() -> {
            try {
                List<ModelKaryawan> list = dept.equals("Semua")
                        ? daoKaryawan.getAll()
                        : daoKaryawan.getByDepartemen(dept);
                javax.swing.SwingUtilities.invokeLater(() -> listener.onDataLoaded(list));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-FilterDept").start();
    }

    public void simpanKaryawanAsync(ModelKaryawan k, DataListener listener) {
        new Thread(() -> {
            try {
                // Validasi
                validasiKaryawan(k);
                boolean ok = daoKaryawan.simpan(k);
                if (ok) {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("Karyawan berhasil disimpan!"));
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal menyimpan karyawan."));
                }
            } catch (IllegalArgumentException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Validasi gagal: " + e.getMessage()));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-SimpanKaryawan").start();
    }

    public void updateKaryawanAsync(ModelKaryawan k, DataListener listener) {
        new Thread(() -> {
            try {
                validasiKaryawan(k);
                boolean ok = daoKaryawan.update(k);
                if (ok) {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("Data karyawan berhasil diupdate!"));
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal mengupdate karyawan."));
                }
            } catch (IllegalArgumentException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Validasi: " + e.getMessage()));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-UpdateKaryawan").start();
    }

    public void hapusKaryawanAsync(int id, DataListener listener) {
        new Thread(() -> {
            try {
                boolean ok = daoKaryawan.hapus(id);
                if (ok) {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("Karyawan berhasil dihapus!"));
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal menghapus karyawan."));
                }
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-HapusKaryawan").start();
    }

    // ===================================================================
    // ===== KPI Operations =====
    // ===================================================================

    public void simpanKPIAsync(ModelKPI kpi, KPIListener listener) {
        new Thread(() -> {
            try {
                validasiKPI(kpi);
                boolean ok = daoKPI.simpan(kpi);
                if (ok) {
                    javax.swing.SwingUtilities.invokeLater(() ->
                        listener.onSuccess("KPI berhasil disimpan! Grade: " + kpi.getGradeKPI()));
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
                if (ok) javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("KPI berhasil dihapus!"));
                else javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal menghapus KPI."));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-HapusKPI").start();
    }

    // ===================================================================
    // ===== Absensi Operations =====
    // ===================================================================

    public void simpanAbsensiAsync(ModelAbsensi a, AbsensiListener listener) {
        new Thread(() -> {
            try {
                boolean ok = daoAbsensi.simpan(a);
                if (ok) javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("Absensi berhasil dicatat!"));
                else javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal mencatat absensi."));
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
                if (ok) javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess("Absensi berhasil dihapus!"));
                else javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Gagal menghapus absensi."));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            }
        }, "Thread-HapusAbsensi").start();
    }

    // ===================================================================
    // ===== Sync methods untuk keperluan lain =====
    // ===================================================================

    public List<ModelKaryawan> getAllKaryawan() throws SQLException {
        return daoKaryawan.getAll();
    }

    public ModelKaryawan getKaryawanById(int id) throws SQLException {
        return daoKaryawan.getById(id);
    }

    public int getTotalKaryawan() throws SQLException {
        return daoKaryawan.getTotal();
    }

    public int getTotalKPI() throws SQLException {
        return daoKPI.getTotal();
    }

    public int getTotalAbsensiHariIni() throws SQLException {
        return daoAbsensi.getTotal();
    }

    public List<ModelKaryawan> getKaryawanAktif() throws SQLException {
        return daoKaryawan.getByStatus("AKTIF");
    }

    // ===================================================================
    // ===== EXCEPTION HANDLING: Validasi bisnis =====
    // ===================================================================

    private void validasiKaryawan(ModelKaryawan k) {
        if (k.getNik() == null || k.getNik().isEmpty())
            throw new IllegalArgumentException("NIK tidak boleh kosong!");
        if (k.getNama() == null || k.getNama().isEmpty())
            throw new IllegalArgumentException("Nama tidak boleh kosong!");
        if (k.getJabatan() == null || k.getJabatan().isEmpty())
            throw new IllegalArgumentException("Jabatan tidak boleh kosong!");
        if (k.getDepartemen() == null || k.getDepartemen().isEmpty())
            throw new IllegalArgumentException("Departemen tidak boleh kosong!");
        if (k.getGajiPokok() < 500000)
            throw new IllegalArgumentException("Gaji pokok minimal Rp 500.000!");
        if (k.getEmail() != null && !k.getEmail().isEmpty() && !k.getEmail().contains("@"))
            throw new IllegalArgumentException("Format email tidak valid!");
    }

    private void validasiKPI(ModelKPI kpi) {
        if (kpi.getKaryawanId() <= 0)
            throw new IllegalArgumentException("Pilih karyawan terlebih dahulu!");
        if (kpi.getPeriode() < 2000 || kpi.getPeriode() > 2100)
            throw new IllegalArgumentException("Periode tahun tidak valid!");
        if (kpi.getBulan() < 1 || kpi.getBulan() > 12)
            throw new IllegalArgumentException("Bulan tidak valid!");
        if (kpi.getPenilai() == null || kpi.getPenilai().isEmpty())
            throw new IllegalArgumentException("Nama penilai tidak boleh kosong!");
    }
}
