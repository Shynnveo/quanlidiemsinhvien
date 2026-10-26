package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu bảng SinhVien trong CSDL duanjavanangcao
 */
public class SinhVienDAO {

    public List<SinhVien> findAll() {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT SinhVienID, MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID "
                   + "FROM SinhVien ORDER BY MaSinhVien";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sinh viên: " + e.getMessage());
        }
        return list;
    }

    public SinhVien findById(int sinhVienId) {
        String sql = "SELECT SinhVienID, MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID "
                   + "FROM SinhVien WHERE SinhVienID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sinhVienId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm sinh viên theo ID: " + e.getMessage());
        }
        return null;
    }

    public SinhVien findByTaiKhoanId(int taiKhoanId) {
        String sql = "SELECT SinhVienID, MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID "
                   + "FROM SinhVien WHERE TaiKhoanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taiKhoanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm sinh viên theo TaiKhoanID: " + e.getMessage());
        }
        return null;
    }

    public SinhVien findByMaSinhVien(String maSinhVien) {
        String sql = "SELECT SinhVienID, MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID "
                   + "FROM SinhVien WHERE MaSinhVien = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSinhVien.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm sinh viên theo mã sinh viên: " + e.getMessage());
        }
        return null;
    }

    public List<SinhVien> findByLop(int lopId) {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT SinhVienID, MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID "
                   + "FROM SinhVien WHERE LopID = ? ORDER BY MaSinhVien";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sinh viên theo lớp: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(SinhVien sv) {
        String sql = "INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sv.getMaSinhVien());
            ps.setString(2, sv.getHoTen());
            ps.setDate(3, sv.getNgaySinh());
            ps.setString(4, sv.getGioiTinh());
            ps.setString(5, sv.getEmail());
            ps.setString(6, sv.getSoDienThoai());
            ps.setString(7, sv.getDiaChi());
            ps.setDate(8, sv.getNgayNhapHoc());
            ps.setString(9, sv.getTrangThai() != null ? sv.getTrangThai() : "Đang học");

            if (sv.getLopId() != null) {
                ps.setInt(10, sv.getLopId());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            if (sv.getTaiKhoanId() != null) {
                ps.setInt(11, sv.getTaiKhoanId());
            } else {
                ps.setNull(11, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm sinh viên: " + e.getMessage());
            return false;
        }
    }

    public boolean update(SinhVien sv) {
        String sql = "UPDATE SinhVien SET MaSinhVien = ?, HoTen = ?, NgaySinh = ?, GioiTinh = ?, "
                   + "Email = ?, SoDienThoai = ?, DiaChi = ?, NgayNhapHoc = ?, TrangThai = ?, LopID = ?, TaiKhoanID = ? "
                   + "WHERE SinhVienID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sv.getMaSinhVien());
            ps.setString(2, sv.getHoTen());
            ps.setDate(3, sv.getNgaySinh());
            ps.setString(4, sv.getGioiTinh());
            ps.setString(5, sv.getEmail());
            ps.setString(6, sv.getSoDienThoai());
            ps.setString(7, sv.getDiaChi());
            ps.setDate(8, sv.getNgayNhapHoc());
            ps.setString(9, sv.getTrangThai());

            if (sv.getLopId() != null) {
                ps.setInt(10, sv.getLopId());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            if (sv.getTaiKhoanId() != null) {
                ps.setInt(11, sv.getTaiKhoanId());
            } else {
                ps.setNull(11, Types.INTEGER);
            }

            ps.setInt(12, sv.getSinhVienId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật sinh viên: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int sinhVienId) throws SQLException {
        // Kiểm tra xem sinh viên có điểm hoặc đăng ký học phần không
        String checkSql = "SELECT COUNT(*) FROM DangKyHocPhan WHERE SinhVienID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, sinhVienId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Không thể xóa sinh viên này vì đã có dữ liệu đăng ký học phần hoặc điểm số trong hệ thống!");
                }
            }

            String deleteSql = "DELETE FROM SinhVien WHERE SinhVienID = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, sinhVienId);
                return psDelete.executeUpdate() > 0;
            }
        }
    }

    public List<SinhVien> search(String keyword) {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT SinhVienID, MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID "
                   + "FROM SinhVien "
                   + "WHERE MaSinhVien LIKE ? OR HoTen LIKE ? OR Email LIKE ? OR SoDienThoai LIKE ? "
                   + "ORDER BY MaSinhVien";

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
            System.err.println("Lỗi khi tìm kiếm sinh viên: " + e.getMessage());
        }
        return list;
    }

    private SinhVien extractFromResultSet(ResultSet rs) throws SQLException {
        SinhVien sv = new SinhVien();
        sv.setSinhVienId(rs.getInt("SinhVienID"));
        sv.setMaSinhVien(rs.getString("MaSinhVien"));
        sv.setHoTen(rs.getString("HoTen"));
        sv.setNgaySinh(rs.getDate("NgaySinh"));
        sv.setGioiTinh(rs.getString("GioiTinh"));
        sv.setEmail(rs.getString("Email"));
        sv.setSoDienThoai(rs.getString("SoDienThoai"));
        sv.setDiaChi(rs.getString("DiaChi"));
        sv.setNgayNhapHoc(rs.getDate("NgayNhapHoc"));
        sv.setTrangThai(rs.getString("TrangThai"));

        int lopId = rs.getInt("LopID");
        if (!rs.wasNull()) {
            sv.setLopId(lopId);
        }

        int taiKhoanId = rs.getInt("TaiKhoanID");
        if (!rs.wasNull()) {
            sv.setTaiKhoanId(taiKhoanId);
        }

        return sv;
    }
}

