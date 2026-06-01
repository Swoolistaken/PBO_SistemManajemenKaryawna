package model.karyawan;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO - Generic CRUD Operations
 * Implementasi: ABSTRACTION, POLYMORPHISM
 * @param <T> Tipe entity
 */
public interface InterfaceDAO<T> {

    /**
     * Menyimpan data baru ke database
     */
    boolean simpan(T entity) throws SQLException;

    /**
     * Mengupdate data yang sudah ada
     */
    boolean update(T entity) throws SQLException;

    /**
     * Menghapus data berdasarkan ID
     */
    boolean hapus(int id) throws SQLException;

    /**
     * Mengambil semua data
     */
    List<T> getAll() throws SQLException;

    /**
     * Mengambil data berdasarkan ID
     */
    T getById(int id) throws SQLException;

    /**
     * Mencari data berdasarkan keyword
     */
    List<T> cari(String keyword) throws SQLException;

    /**
     * Mendapatkan jumlah total data
     */
    int getTotal() throws SQLException;
}
