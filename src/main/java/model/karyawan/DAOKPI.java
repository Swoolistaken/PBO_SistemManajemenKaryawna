package model.karyawan;

import model.Connector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOKPI implements InterfaceDAO<ModelKPI> {

    private Connection getConn() throws SQLException {
        return Connector.getInstance().getConnection();
    }

    @Override
    public boolean simpan(ModelKPI kpi) throws SQLException {
        String sql = "INSERT INTO kpi (karyawan_id, nik_karyawan, nama_karyawan, periode, bulan, "
                + "nilai_produktivitas, nilai_kualitas, nilai_kehadiran, nilai_teamwork, "
                + "nilai_inovasi, catatan_atasan, target_berikutnya, tanggal_penilaian, penilai) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, kpi.getKaryawanId());
            ps.setString(2, kpi.getNikKaryawan());
            ps.setString(3, kpi.getNamaKaryawan());
            ps.setInt(4, kpi.getPeriode());
            ps.setInt(5, kpi.getBulan());
            ps.setDouble(6, kpi.getNilaiProduktivitas());
            ps.setDouble(7, kpi.getNilaiKualitas());
            ps.setDouble(8, kpi.getNilaiKehadiran());
            ps.setDouble(9, kpi.getNilaiTeamwork());
            ps.setDouble(10, kpi.getNilaiInovasi());
            ps.setString(11, kpi.getCatatanAtasan());
            ps.setString(12, kpi.getTargetPeriodeBerikutnya());
            ps.setDate(13, new java.sql.Date(kpi.getTanggalPenilaian().getTime()));
            ps.setString(14, kpi.getPenilai());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("KPI untuk karyawan ini di periode/bulan tersebut sudah ada!");
        }
    }

    @Override
    public boolean update(ModelKPI kpi) throws SQLException {
        String sql = "UPDATE kpi SET nilai_produktivitas=?, nilai_kualitas=?, nilai_kehadiran=?, "
                + "nilai_teamwork=?, nilai_inovasi=?, catatan_atasan=?, target_berikutnya=?, "
                + "tanggal_penilaian=?, penilai=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, kpi.getNilaiProduktivitas());
            ps.setDouble(2, kpi.getNilaiKualitas());
            ps.setDouble(3, kpi.getNilaiKehadiran());
            ps.setDouble(4, kpi.getNilaiTeamwork());
            ps.setDouble(5, kpi.getNilaiInovasi());
            ps.setString(6, kpi.getCatatanAtasan());
            ps.setString(7, kpi.getTargetPeriodeBerikutnya());
            ps.setDate(8, new java.sql.Date(kpi.getTanggalPenilaian().getTime()));
            ps.setString(9, kpi.getPenilai());
            ps.setInt(10, kpi.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hapus(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM kpi WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<ModelKPI> getAll() throws SQLException {
        List<ModelKPI> list = new ArrayList<>();
        String sql = "SELECT * FROM kpi ORDER BY tanggal_penilaian DESC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public ModelKPI getById(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT * FROM kpi WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<ModelKPI> cari(String keyword) throws SQLException {
        List<ModelKPI> list = new ArrayList<>();
        String sql = "SELECT * FROM kpi WHERE nama_karyawan LIKE ? OR nik_karyawan LIKE ?";
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
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM kpi")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<ModelKPI> getByKaryawan(int karyawanId) throws SQLException {
        List<ModelKPI> list = new ArrayList<>();
        String sql = "SELECT * FROM kpi WHERE karyawan_id=? ORDER BY periode DESC, bulan DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, karyawanId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<ModelKPI> getByPeriode(int periode, int bulan) throws SQLException {
        List<ModelKPI> list = new ArrayList<>();
        String sql = "SELECT * FROM kpi WHERE periode=? AND bulan=? ORDER BY nama_karyawan";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, periode);
            ps.setInt(2, bulan);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private ModelKPI mapRow(ResultSet rs) throws SQLException {
        ModelKPI kpi = new ModelKPI();
        kpi.setId(rs.getInt("id"));
        kpi.setKaryawanId(rs.getInt("karyawan_id"));
        kpi.setNikKaryawan(rs.getString("nik_karyawan"));
        kpi.setNamaKaryawan(rs.getString("nama_karyawan"));
        kpi.setPeriode(rs.getInt("periode"));
        kpi.setBulan(rs.getInt("bulan"));
        kpi.setNilaiProduktivitas(rs.getDouble("nilai_produktivitas"));
        kpi.setNilaiKualitas(rs.getDouble("nilai_kualitas"));
        kpi.setNilaiKehadiran(rs.getDouble("nilai_kehadiran"));
        kpi.setNilaiTeamwork(rs.getDouble("nilai_teamwork"));
        kpi.setNilaiInovasi(rs.getDouble("nilai_inovasi"));
        kpi.setCatatanAtasan(rs.getString("catatan_atasan"));
        kpi.setTargetPeriodeBerikutnya(rs.getString("target_berikutnya"));
        kpi.setTanggalPenilaian(rs.getDate("tanggal_penilaian"));
        kpi.setPenilai(rs.getString("penilai"));
        return kpi;
    }
}
