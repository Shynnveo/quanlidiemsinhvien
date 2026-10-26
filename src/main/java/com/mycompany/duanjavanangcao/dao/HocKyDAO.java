package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu bảng HocKy trong CSDL duanjavanangcao
 */
public class HocKyDAO {

    public List<HocKy> findAll() {
        List<HocKy> list = new ArrayList<>();
        String sql = "SELECT HocKyID, MaHocKy, TenHocKy, NamHoc, NgayBatDau, NgayKetThuc, TrangThai FROM HocKy ORDER BY NgayBatDau DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HocKy hk = new HocKy();
                hk.setHocKyId(rs.getInt("HocKyID"));
                hk.setMaHocKy(rs.getString("MaHocKy"));
                hk.setTenHocKy(rs.getString("TenHocKy"));
                hk.setNamHoc(rs.getString("NamHoc"));
                hk.setNgayBatDau(rs.getDate("NgayBatDau"));
                hk.setNgayKetThuc(rs.getDate("NgayKetThuc"));
                hk.setTrangThai(rs.getString("TrangThai"));
                list.add(hk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách học kỳ: " + e.getMessage());
        }
        return list;
    }

    public HocKy findById(int hocKyId) {
        String sql = "SELECT HocKyID, MaHocKy, TenHocKy, NamHoc, NgayBatDau, NgayKetThuc, TrangThai FROM HocKy WHERE HocKyID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hocKyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HocKy hk = new HocKy();
                    hk.setHocKyId(rs.getInt("HocKyID"));
                    hk.setMaHocKy(rs.getString("MaHocKy"));
                    hk.setTenHocKy(rs.getString("TenHocKy"));
                    hk.setNamHoc(rs.getString("NamHoc"));
                    hk.setNgayBatDau(rs.getDate("NgayBatDau"));
                    hk.setNgayKetThuc(rs.getDate("NgayKetThuc"));
                    hk.setTrangThai(rs.getString("TrangThai"));
                    return hk;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm học kỳ theo ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(HocKy hk) {
        String sql = "INSERT INTO HocKy (MaHocKy, TenHocKy, NamHoc, NgayBatDau, NgayKetThuc, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hk.getMaHocKy());
            ps.setString(2, hk.getTenHocKy());
            ps.setString(3, hk.getNamHoc());
            ps.setDate(4, hk.getNgayBatDau());
            ps.setDate(5, hk.getNgayKetThuc());
            ps.setString(6, hk.getTrangThai() != null ? hk.getTrangThai() : "Đang học");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm học kỳ: " + e.getMessage());
            return false;
        }
    }

    public boolean update(HocKy hk) {
        String sql = "UPDATE HocKy SET MaHocKy = ?, TenHocKy = ?, NamHoc = ?, NgayBatDau = ?, NgayKetThuc = ?, TrangThai = ? WHERE HocKyID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hk.getMaHocKy());
            ps.setString(2, hk.getTenHocKy());
            ps.setString(3, hk.getNamHoc());
            ps.setDate(4, hk.getNgayBatDau());
            ps.setDate(5, hk.getNgayKetThuc());
            ps.setString(6, hk.getTrangThai());
            ps.setInt(7, hk.getHocKyId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật học kỳ: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int hocKyId) throws SQLException {
        // Kiểm tra xem học kỳ đã có lớp học phần nào mở chưa
        String checkSql = "SELECT COUNT(*) FROM LopHocPhan WHERE HocKyID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, hocKyId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Không thể xóa học kỳ này vì đã có " + rs.getInt(1) + " lớp học phần mở trong kỳ!");
                }
            }

            String deleteSql = "DELETE FROM HocKy WHERE HocKyID = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, hocKyId);
                return psDelete.executeUpdate() > 0;
            }
        }
    }
}

