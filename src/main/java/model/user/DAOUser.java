package model.user;

import model.Connector;
import model.InterfaceDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOUser implements InterfaceDAO<User> {

    private Connection getConn() throws SQLException {
        return Connector.getInstance().getConnection();
    }

    @Override
    public boolean simpan(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password, nama_lengkap, role, aktif) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNamaLengkap());
            ps.setString(4, u.getRole());
            ps.setBoolean(5, u.isAktif());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("Username '" + u.getUsername() + "' sudah digunakan!");
        }
    }

    @Override
    public boolean update(User u) throws SQLException {
        String sql = "UPDATE users SET username=?, nama_lengkap=?, role=?, aktif=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNamaLengkap());
            ps.setString(3, u.getRole());
            ps.setBoolean(4, u.isAktif());
            ps.setInt(5, u.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hapus(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY nama_lengkap")) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public User getById(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<User> cari(String keyword) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR nama_lengkap LIKE ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public int getTotal() throws SQLException {
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public User login(String username, String hashedPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=? AND aktif=true";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = mapRow(rs);
                // Update last login
                updateLastLogin(user.getId());
                return user;
            }
        }
        return null;
    }

    private void updateLastLogin(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement(
                "UPDATE users SET last_login=NOW() WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;
        switch (role) {
            case "ADMIN":
                user = new ModelAdmin();
                break;
            case "HRD":
                user = new ModelHRD();
                break;
            case "MANAGER":
                user = new ModelManager();
                break;
            default:
                user = new ModelHRD();
                break;
        }
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setNamaLengkap(rs.getString("nama_lengkap"));
        user.setRole(role);
        user.setAktif(rs.getBoolean("aktif"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}
