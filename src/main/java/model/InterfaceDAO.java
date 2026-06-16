package model;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceDAO<T> {

    boolean simpan(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean hapus(int id) throws SQLException;

    List<T> getAll() throws SQLException; // GetAll

    T getById(int id) throws SQLException; // Cari berdasarkan Id

    List<T> cari(String keyword) throws SQLException; // Cari Karyawan

    int getTotal() throws SQLException; //Jumlah Total Data
}
