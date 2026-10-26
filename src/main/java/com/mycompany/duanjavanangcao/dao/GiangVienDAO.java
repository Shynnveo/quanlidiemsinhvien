package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.GiangVien;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu bảng GiangVien trong CSDL duanjavanangcao
 */
public class GiangVienDAO {

    public List<GiangVien> findAll() {
        List<GiangVien> list = new ArrayList<>();
        String sql = "SELECT GiangVienID, MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID FROM GiangVien ORDER BY MaGiangVien";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách giảng viên: " + e.getMessage());
        }
        return list;
    }

    public GiangVien findById(int giangVienId) {
        String sql = "SELECT GiangVienID, MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID FROM GiangVien WHERE GiangVienID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, giangVienId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm giảng viên theo ID: " + e.getMessage());
        }
        return null;
    }

    public GiangVien findByTaiKhoanId(int taiKhoanId) {
        String sql = "SELECT GiangVienID, MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID FROM GiangVien WHERE TaiKhoanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taiKhoanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm giảng viên theo TaiKhoanID: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(GiangVien gv) {
        String sql = "INSERT INTO GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gv.getMaGiangVien());
            ps.setString(2, gv.getHoTen());
            ps.setString(3, gv.getEmail());
            ps.setString(4, gv.getSoDienThoai());
            ps.setString(5, gv.getKhoa());

            if (gv.getTaiKhoanId() != null) {
                ps.setInt(6, gv.getTaiKhoanId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm giảng viên: " + e.getMessage());
            return false;
        }
    }

    public boolean update(GiangVien gv) {
        String sql = "UPDATE GiangVien SET MaGiangVien = ?, HoTen = ?, Email = ?, SoDienThoai = ?, Khoa = ?, TaiKhoanID = ? "
                   + "WHERE GiangVienID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gv.getMaGiangVien());
            ps.setString(2, gv.getHoTen());
            ps.setString(3, gv.getEmail());
            ps.setString(4, gv.getSoDienThoai());
            ps.setString(5, gv.getKhoa());

            if (gv.getTaiKhoanId() != null) {
                ps.setInt(6, gv.getTaiKhoanId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setInt(7, gv.getGiangVienId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật giảng viên: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int giangVienId) throws SQLException {
        // Kiểm tra xem giảng viên có đang được phân công lớp học phần nào không
        String checkSql = "SELECT COUNT(*) FROM LopHocPhan WHERE GiangVienID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, giangVienId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Không thể xóa giảng viên này vì đang được phân công giảng dạy " + rs.getInt(1) + " lớp học phần!");
                }
            }

            String deleteSql = "DELETE FROM GiangVien WHERE GiangVienID = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, giangVienId);
                return psDelete.executeUpdate() > 0;
            }
        }
    }

    public List<GiangVien> search(String keyword) {
        List<GiangVien> list = new ArrayList<>();
        String sql = "SELECT GiangVienID, MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID FROM GiangVien "
                   + "WHERE MaGiangVien LIKE ? OR HoTen LIKE ? OR Email LIKE ? OR Khoa LIKE ? "
                   + "ORDER BY MaGiangVien";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String param = "%" + keyword + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);
            ps.setString(4, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm giảng viên: " + e.getMessage());
        }
        return list;
    }

    private GiangVien extractFromResultSet(ResultSet rs) throws SQLException {
        GiangVien gv = new GiangVien();
        gv.setGiangVienId(rs.getInt("GiangVienID"));
        gv.setMaGiangVien(rs.getString("MaGiangVien"));
        gv.setHoTen(rs.getString("HoTen"));
        gv.setEmail(rs.getString("Email"));
        gv.setSoDienThoai(rs.getString("SoDienThoai"));
        gv.setKhoa(rs.getString("Khoa"));

        int taiKhoanId = rs.getInt("TaiKhoanID");
        if (!rs.wasNull()) {
            gv.setTaiKhoanId(taiKhoanId);
        }

        return gv;
    }
}

