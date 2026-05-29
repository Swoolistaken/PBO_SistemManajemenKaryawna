package model.karyawan;

import model.Connector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOAbsensi implements InterfaceDAO<ModelAbsensi> {

    private Connection getConn() throws SQLException {
        return Connector.getInstance().getConnection();
    }

    @Override
    public boolean simpan(ModelAbsensi a) throws SQLException {
        String sql = "INSERT INTO absensi (karyawan_id, nik_karyawan, nama_karyawan, tanggal, "
                + "jam_masuk, jam_keluar, status, keterangan, terlambat, menit_terlambat, pulang_awal) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, a.getKaryawanId());
            ps.setString(2, a.getNikKaryawan());
            ps.setString(3, a.getNamaKaryawan());
            ps.setDate(4, new java.sql.Date(a.getTanggal().getTime()));
            ps.setString(5, a.getJamMasuk());
            ps.setString(6, a.getJamKeluar());
            ps.setString(7, a.getStatus().name());
            ps.setString(8, a.getKeterangan());
            ps.setBoolean(9, a.isTerlambat());
            ps.setInt(10, a.getMenitTerlambat());
            ps.setBoolean(11, a.isPulangAwal());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(ModelAbsensi a) throws SQLException {
        String sql = "UPDATE absensi SET jam_masuk=?, jam_keluar=?, status=?, keterangan=?, "
                + "terlambat=?, menit_terlambat=?, pulang_awal=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, a.getJamMasuk());
            ps.setString(2, a.getJamKeluar());
            ps.setString(3, a.getStatus().name());
            ps.setString(4, a.getKeterangan());
            ps.setBoolean(5, a.isTerlambat());
            ps.setInt(6, a.getMenitTerlambat());
            ps.setBoolean(7, a.isPulangAwal());
            ps.setInt(8, a.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hapus(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM absensi WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<ModelAbsensi> getAll() throws SQLException {
        List<ModelAbsensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi ORDER BY tanggal DESC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public ModelAbsensi getById(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT * FROM absensi WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<ModelAbsensi> cari(String keyword) throws SQLException {
        List<ModelAbsensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE nama_karyawan LIKE ? OR nik_karyawan LIKE ?";
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
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM absensi")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<ModelAbsensi> getByKaryawanDanBulan(int karyawanId, int bulan, int tahun) throws SQLException {
        List<ModelAbsensi> list = new ArrayList<>();
        String sql = "SELECT * FROM absensi WHERE karyawan_id=? AND MONTH(tanggal)=? AND YEAR(tanggal)=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, karyawanId);
            ps.setInt(2, bulan);
            ps.setInt(3, tahun);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int countHadirBulanIni(int karyawanId, int bulan, int tahun) throws SQLException {
        String sql = "SELECT COUNT(*) FROM absensi WHERE karyawan_id=? AND MONTH(tanggal)=? AND YEAR(tanggal)=? AND status='HADIR'";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, karyawanId);
            ps.setInt(2, bulan);
            ps.setInt(3, tahun);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private ModelAbsensi mapRow(ResultSet rs) throws SQLException {
        ModelAbsensi a = new ModelAbsensi();
        a.setId(rs.getInt("id"));
        a.setKaryawanId(rs.getInt("karyawan_id"));
        a.setNikKaryawan(rs.getString("nik_karyawan"));
        a.setNamaKaryawan(rs.getString("nama_karyawan"));
        a.setTanggal(rs.getDate("tanggal"));
        a.setJamMasuk(rs.getString("jam_masuk"));
        a.setJamKeluar(rs.getString("jam_keluar"));
        try {
            a.setStatus(ModelAbsensi.StatusAbsensi.valueOf(rs.getString("status")));
        } catch (Exception e) {
            a.setStatus(ModelAbsensi.StatusAbsensi.HADIR);
        }
        a.setKeterangan(rs.getString("keterangan"));
        a.setTerlambat(rs.getBoolean("terlambat"));
        a.setMenitTerlambat(rs.getInt("menit_terlambat"));
        a.setPulangAwal(rs.getBoolean("pulang_awal"));
        return a;
    }
}
