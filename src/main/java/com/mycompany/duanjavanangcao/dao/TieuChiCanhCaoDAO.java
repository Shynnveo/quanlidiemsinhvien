package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.TieuChiCanhCao;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO truy vấn bảng TieuChiCanhCao trong CSDL duanjavanangcao
 */
public class TieuChiCanhCaoDAO {

    public List<TieuChiCanhCao> findAll() {
        List<TieuChiCanhCao> list = new ArrayList<>();
        String sql = "SELECT * FROM TieuChiCanhCao ORDER BY TieuChiID ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách tiêu chí cảnh cáo: " + e.getMessage());
        }
        return list;
    }

    public List<TieuChiCanhCao> findActive() {
        List<TieuChiCanhCao> list = new ArrayList<>();
        String sql = "SELECT * FROM TieuChiCanhCao WHERE TrangThai = 1 ORDER BY TieuChiID ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tiêu chí cảnh cáo đang hiệu lực: " + e.getMessage());
        }
        return list;
    }

    public TieuChiCanhCao findById(int id) {
        String sql = "SELECT * FROM TieuChiCanhCao WHERE TieuChiID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm tiêu chí cảnh cáo theo ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(TieuChiCanhCao tc) {
        String sql = "INSERT INTO TieuChiCanhCao (MaTieuChi, TenTieuChi, LoaiCanhCao, MoTa, "
                   + "                          GPAHocKyToiDa, GPATichLuyToiDa, SoTinChiNoToiThieu, SoTinChiDatToiDa, "
                   + "                          NgayApDung, NgayKetThuc, TrangThai) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tc.getMaTieuChi());
            ps.setString(2, tc.getTenTieuChi());
            ps.setString(3, tc.getLoaiCanhCao());
            ps.setString(4, tc.getMoTa());

            setDoubleOrNull(ps, 5, tc.getGpaHocKyToiDa());
            setDoubleOrNull(ps, 6, tc.getGpaTichLuyToiDa());
            setIntegerOrNull(ps, 7, tc.getSoTinChiNoToiThieu());
            setIntegerOrNull(ps, 8, tc.getSoTinChiDatToiDa());

            ps.setDate(9, tc.getNgayApDung());
            ps.setDate(10, tc.getNgayKetThuc());
            ps.setBoolean(11, tc.isTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm tiêu chí cảnh cáo: " + e.getMessage());
            return false;
        }
    }

    private void setDoubleOrNull(PreparedStatement ps, int paramIndex, Double val) throws SQLException {
        if (val != null) {
            ps.setDouble(paramIndex, val);
        } else {
            ps.setNull(paramIndex, Types.DECIMAL);
        }
    }

    private void setIntegerOrNull(PreparedStatement ps, int paramIndex, Integer val) throws SQLException {
        if (val != null) {
            ps.setInt(paramIndex, val);
        } else {
            ps.setNull(paramIndex, Types.INTEGER);
        }
    }

    private TieuChiCanhCao extractFromResultSet(ResultSet rs) throws SQLException {
        TieuChiCanhCao tc = new TieuChiCanhCao();
        tc.setTieuChiId(rs.getInt("TieuChiID"));
        tc.setMaTieuChi(rs.getString("MaTieuChi"));
        tc.setTenTieuChi(rs.getString("TenTieuChi"));
        tc.setLoaiCanhCao(rs.getString("LoaiCanhCao"));
        tc.setMoTa(rs.getString("MoTa"));

        double gpaHK = rs.getDouble("GPAHocKyToiDa");
        if (!rs.wasNull()) tc.setGpaHocKyToiDa(gpaHK);

        double gpaTL = rs.getDouble("GPATichLuyToiDa");
        if (!rs.wasNull()) tc.setGpaTichLuyToiDa(gpaTL);

        int tcNo = rs.getInt("SoTinChiNoToiThieu");
        if (!rs.wasNull()) tc.setSoTinChiNoToiThieu(tcNo);

        int tcDat = rs.getInt("SoTinChiDatToiDa");
        if (!rs.wasNull()) tc.setSoTinChiDatToiDa(tcDat);

        tc.setNgayApDung(rs.getDate("NgayApDung"));
        tc.setNgayKetThuc(rs.getDate("NgayKetThuc"));
        tc.setTrangThai(rs.getBoolean("TrangThai"));

        return tc;
    }
}

