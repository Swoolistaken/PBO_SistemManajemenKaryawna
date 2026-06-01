package model.karyawan;

import model.Connector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOKaryawan implements InterfaceDAO<ModelKaryawan> {

    private Connection getConn() throws SQLException {
        return Connector.getInstance().getConnection();
    }

    @Override
    public boolean simpan(ModelKaryawan k) throws SQLException {
        String sql = "INSERT INTO karyawan (nik, nama, email, no_telp, alamat, tanggal_lahir, "
                + "jenis_kelamin, tanggal_masuk, jabatan, departemen, gaji_pokok, "
                + "tunjangan_transport, tunjangan_makan, tunjangan_kesehatan, "
                + "level_jabatan, pendidikan_terakhir, keahlian, status) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, k.getNik());
            ps.setString(2, k.getNama());
            ps.setString(3, k.getEmail());
            ps.setString(4, k.getNoTelp());
            ps.setString(5, k.getAlamat());
            ps.setDate(6, k.getTanggalLahir() != null ? new java.sql.Date(k.getTanggalLahir().getTime()) : null);
            ps.setString(7, k.getJenisKelamin());
            ps.setDate(8, k.getTanggalMasuk() != null ? new java.sql.Date(k.getTanggalMasuk().getTime()) : null);
            ps.setString(9, k.getJabatan());
            ps.setString(10, k.getDepartemen());
            ps.setDouble(11, k.getGajiPokok());
            ps.setDouble(12, k.getTunjanganTransport());
            ps.setDouble(13, k.getTunjanganMakan());
            ps.setDouble(14, k.getTunjanganKesehatan());
            ps.setInt(15, k.getLevelJabatan());
            ps.setString(16, k.getPendidikanTerakhir());
            ps.setString(17, k.getKeahlian());
            ps.setString(18, k.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("NIK '" + k.getNik() + "' sudah terdaftar di sistem!");
        } catch (SQLException e) {
            throw new SQLException("Gagal menyimpan karyawan: " + e.getMessage());
        }
    }

    @Override
    public boolean update(ModelKaryawan k) throws SQLException {
        String sql = "UPDATE karyawan SET nik=?, nama=?, email=?, no_telp=?, alamat=?, "
                + "tanggal_lahir=?, jenis_kelamin=?, tanggal_masuk=?, jabatan=?, departemen=?, "
                + "gaji_pokok=?, tunjangan_transport=?, tunjangan_makan=?, tunjangan_kesehatan=?, "
                + "level_jabatan=?, pendidikan_terakhir=?, keahlian=?, status=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, k.getNik());
            ps.setString(2, k.getNama());
            ps.setString(3, k.getEmail());
            ps.setString(4, k.getNoTelp());
            ps.setString(5, k.getAlamat());
            ps.setDate(6, k.getTanggalLahir() != null ? new java.sql.Date(k.getTanggalLahir().getTime()) : null);
            ps.setString(7, k.getJenisKelamin());
            ps.setDate(8, k.getTanggalMasuk() != null ? new java.sql.Date(k.getTanggalMasuk().getTime()) : null);
            ps.setString(9, k.getJabatan());
            ps.setString(10, k.getDepartemen());
            ps.setDouble(11, k.getGajiPokok());
            ps.setDouble(12, k.getTunjanganTransport());
            ps.setDouble(13, k.getTunjanganMakan());
            ps.setDouble(14, k.getTunjanganKesehatan());
            ps.setInt(15, k.getLevelJabatan());
            ps.setString(16, k.getPendidikanTerakhir());
            ps.setString(17, k.getKeahlian());
            ps.setString(18, k.getStatus());
            ps.setInt(19, k.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Gagal mengupdate karyawan: " + e.getMessage());
        }
    }

    @Override
    public boolean hapus(int id) throws SQLException {
        String sql = "DELETE FROM karyawan WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Gagal menghapus karyawan: " + e.getMessage());
        }
    }

    @Override
    public List<ModelKaryawan> getAll() throws SQLException {
        List<ModelKaryawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan ORDER BY nama ASC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Gagal mengambil data karyawan: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ModelKaryawan getById(int id) throws SQLException {
        String sql = "SELECT * FROM karyawan WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Gagal mengambil data karyawan: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ModelKaryawan> cari(String keyword) throws SQLException {
        List<ModelKaryawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan WHERE nama LIKE ? OR nik LIKE ? OR jabatan LIKE ? OR departemen LIKE ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ps.setString(4, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Gagal mencari karyawan: " + e.getMessage());
        }
        return list;
    }

    @Override
    public int getTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM karyawan";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<ModelKaryawan> getByDepartemen(String departemen) throws SQLException {
        List<ModelKaryawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan WHERE departemen=? ORDER BY nama";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, departemen);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<ModelKaryawan> getByStatus(String status) throws SQLException {
        List<ModelKaryawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan WHERE status=? ORDER BY nama";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private ModelKaryawan mapRow(ResultSet rs) throws SQLException {
        ModelKaryawan k = new ModelKaryawan();
        k.setId(rs.getInt("id"));
        k.setNik(rs.getString("nik"));
        k.setNama(rs.getString("nama"));
        k.setEmail(rs.getString("email"));
        k.setNoTelp(rs.getString("no_telp"));
        k.setAlamat(rs.getString("alamat"));
        k.setTanggalLahir(rs.getDate("tanggal_lahir"));
        k.setJenisKelamin(rs.getString("jenis_kelamin"));
        k.setTanggalMasuk(rs.getDate("tanggal_masuk"));
        k.setJabatan(rs.getString("jabatan"));
        k.setDepartemen(rs.getString("departemen"));
        k.setGajiPokok(rs.getDouble("gaji_pokok"));
        k.setTunjanganTransport(rs.getDouble("tunjangan_transport"));
        k.setTunjanganMakan(rs.getDouble("tunjangan_makan"));
        k.setTunjanganKesehatan(rs.getDouble("tunjangan_kesehatan"));
        k.setLevelJabatan(rs.getInt("level_jabatan"));
        k.setPendidikanTerakhir(rs.getString("pendidikan_terakhir"));
        k.setKeahlian(rs.getString("keahlian"));
        k.setStatus(rs.getString("status"));
        return k;
    }
}
