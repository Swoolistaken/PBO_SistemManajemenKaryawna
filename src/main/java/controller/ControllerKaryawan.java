package controller;

import model.karyawan.ModelKaryawan;
import model.karyawan.DAOKaryawan;
import java.sql.SQLException;
import java.util.List;

public class ControllerKaryawan {

    private final DAOKaryawan daoKaryawan = new DAOKaryawan();

    public interface DataListener {

        void onSuccess(String pesan);

        void onError(String pesan);

        void onDataLoaded(List<ModelKaryawan> data);
    }

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
                validasi(k);
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
                validasi(k);
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

    public List<ModelKaryawan> getAllKaryawan() throws SQLException {
        return daoKaryawan.getAll();
    }

    public ModelKaryawan getKaryawanById(int id) throws SQLException {
        return daoKaryawan.getById(id);
    }

    public int getTotalKaryawan() throws SQLException {
        return daoKaryawan.getTotal();
    }

    public List<ModelKaryawan> getKaryawanAktif() throws SQLException {
        return daoKaryawan.getByStatus("AKTIF");
    }

    private void validasi(ModelKaryawan k) {
        if (k.getNik() == null || k.getNik().isEmpty()) {
            throw new IllegalArgumentException("NIK tidak boleh kosong!");
        }
        if (k.getNama() == null || k.getNama().isEmpty()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong!");
        }
        if (k.getJabatan() == null || k.getJabatan().isEmpty()) {
            throw new IllegalArgumentException("Jabatan tidak boleh kosong!");
        }
        if (k.getDepartemen() == null || k.getDepartemen().isEmpty()) {
            throw new IllegalArgumentException("Departemen tidak boleh kosong!");
        }
        if (k.getGajiPokok() < 500000) {
            throw new IllegalArgumentException("Gaji pokok minimal Rp 500.000!");
        }
        if (k.getEmail() != null && !k.getEmail().isEmpty() && !k.getEmail().contains("@")) {
            throw new IllegalArgumentException("Format email tidak valid!");
        }
    }
}
