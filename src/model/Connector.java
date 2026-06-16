package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Database Connector
 * Implementasi: Encapsulation, Exception Handling
 */
public class Connector {

    // ===== ENCAPSULATION =====
    private static Connector instance;
    private Connection connection;

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "db_karyawan";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false&serverTimezone=UTC";

    // ===== ENCAPSULATION: Private constructor (Singleton Pattern) =====
    private Connector() {
    }

    public static Connector getInstance() {
        if (instance == null) {
            instance = new Connector();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DB] Koneksi ke database berhasil!");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            throw new SQLException("Gagal koneksi ke database: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error menutup koneksi: " + e.getMessage());
        }
    }
}
