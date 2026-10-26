package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.MonHoc;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu bảng MonHoc trong CSDL duanjavanangcao
 */
public class MonHocDAO {

    public List<MonHoc> findAll() {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT MonHocID, MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai FROM MonHoc ORDER BY MaMonHoc";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MonHoc mh = new MonHoc();
                mh.setMonHocId(rs.getInt("MonHocID"));
                mh.setMaMonHoc(rs.getString("MaMonHoc"));
                mh.setTenMonHoc(rs.getString("TenMonHoc"));
                mh.setSoTinChi(rs.getInt("SoTinChi"));
                mh.setMoTa(rs.getString("MoTa"));
                mh.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(mh);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách môn học: " + e.getMessage());
        }
        return list;
    }

    public MonHoc findById(int monHocId) {
        String sql = "SELECT MonHocID, MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai FROM MonHoc WHERE MonHocID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, monHocId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MonHoc mh = new MonHoc();
                    mh.setMonHocId(rs.getInt("MonHocID"));
                    mh.setMaMonHoc(rs.getString("MaMonHoc"));
                    mh.setTenMonHoc(rs.getString("TenMonHoc"));
                    mh.setSoTinChi(rs.getInt("SoTinChi"));
                    mh.setMoTa(rs.getString("MoTa"));
                    mh.setTrangThai(rs.getBoolean("TrangThai"));
                    return mh;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm môn học theo ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(MonHoc mh) {
        String sql = "INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mh.getMaMonHoc());
            ps.setString(2, mh.getTenMonHoc());
            ps.setInt(3, mh.getSoTinChi());
            ps.setString(4, mh.getMoTa());
            ps.setBoolean(5, mh.isTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm môn học: " + e.getMessage());
            return false;
        }
    }

    public boolean update(MonHoc mh) {
        String sql = "UPDATE MonHoc SET MaMonHoc = ?, TenMonHoc = ?, SoTinChi = ?, MoTa = ?, TrangThai = ? WHERE MonHocID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mh.getMaMonHoc());
            ps.setString(2, mh.getTenMonHoc());
            ps.setInt(3, mh.getSoTinChi());
            ps.setString(4, mh.getMoTa());
            ps.setBoolean(5, mh.isTrangThai());
            ps.setInt(6, mh.getMonHocId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật môn học: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int monHocId) throws SQLException {
        // Kiểm tra xem môn học đã có lớp học phần nào mở chưa
        String checkSql = "SELECT COUNT(*) FROM LopHocPhan WHERE MonHocID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, monHocId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Không thể xóa môn học này vì đã có " + rs.getInt(1) + " lớp học phần liên kết!");
                }
            }

            String deleteSql = "DELETE FROM MonHoc WHERE MonHocID = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, monHocId);
                return psDelete.executeUpdate() > 0;
            }
        }
    }

    public List<MonHoc> search(String keyword) {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT MonHocID, MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai FROM MonHoc "
                   + "WHERE MaMonHoc LIKE ? OR TenMonHoc LIKE ? ORDER BY MaMonHoc";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String param = "%" + keyword + "%";
            ps.setString(1, param);
            ps.setString(2, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonHoc mh = new MonHoc();
                    mh.setMonHocId(rs.getInt("MonHocID"));
                    mh.setMaMonHoc(rs.getString("MaMonHoc"));
                    mh.setTenMonHoc(rs.getString("TenMonHoc"));
                    mh.setSoTinChi(rs.getInt("SoTinChi"));
                    mh.setMoTa(rs.getString("MoTa"));
                    mh.setTrangThai(rs.getBoolean("TrangThai"));
                    list.add(mh);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm môn học: " + e.getMessage());
        }
        return list;
    }
}

