package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.Lop;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu bảng Lop trong CSDL duanjavanangcao
 */
public class LopDAO {

    public List<Lop> findAll() {
        List<Lop> list = new ArrayList<>();
        String sql = "SELECT LopID, MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai FROM Lop ORDER BY MaLop";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Lop lop = new Lop();
                lop.setLopId(rs.getInt("LopID"));
                lop.setMaLop(rs.getString("MaLop"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setKhoaHoc(rs.getString("KhoaHoc"));
                lop.setNganhHoc(rs.getString("NganhHoc"));
                lop.setKhoa(rs.getString("Khoa"));
                lop.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(lop);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách lớp: " + e.getMessage());
        }
        return list;
    }

    public Lop findById(int lopId) {
        String sql = "SELECT LopID, MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai FROM Lop WHERE LopID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lopId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Lop lop = new Lop();
                    lop.setLopId(rs.getInt("LopID"));
                    lop.setMaLop(rs.getString("MaLop"));
                    lop.setTenLop(rs.getString("TenLop"));
                    lop.setKhoaHoc(rs.getString("KhoaHoc"));
                    lop.setNganhHoc(rs.getString("NganhHoc"));
                    lop.setKhoa(rs.getString("Khoa"));
                    lop.setTrangThai(rs.getBoolean("TrangThai"));
                    return lop;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm lớp theo ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Lop lop) {
        String sql = "INSERT INTO Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lop.getMaLop());
            ps.setString(2, lop.getTenLop());
            ps.setString(3, lop.getKhoaHoc());
            ps.setString(4, lop.getNganhHoc());
            ps.setString(5, lop.getKhoa());
            ps.setBoolean(6, lop.isTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm lớp: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Lop lop) {
        String sql = "UPDATE Lop SET MaLop = ?, TenLop = ?, KhoaHoc = ?, NganhHoc = ?, Khoa = ?, TrangThai = ? WHERE LopID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lop.getMaLop());
            ps.setString(2, lop.getTenLop());
            ps.setString(3, lop.getKhoaHoc());
            ps.setString(4, lop.getNganhHoc());
            ps.setString(5, lop.getKhoa());
            ps.setBoolean(6, lop.isTrangThai());
            ps.setInt(7, lop.getLopId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật lớp: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int lopId) throws SQLException {
        // Kiểm tra xem có sinh viên nào thuộc lớp này không trước khi xóa
        String checkSql = "SELECT COUNT(*) FROM SinhVien WHERE LopID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, lopId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Không thể xóa lớp học này vì đang có " + rs.getInt(1) + " sinh viên thuộc lớp!");
                }
            }

            String deleteSql = "DELETE FROM Lop WHERE LopID = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, lopId);
                return psDelete.executeUpdate() > 0;
            }
        }
    }

    public List<Lop> search(String keyword) {
        List<Lop> list = new ArrayList<>();
        String sql = "SELECT LopID, MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai FROM Lop "
                   + "WHERE MaLop LIKE ? OR TenLop LIKE ? OR Khoa LIKE ? ORDER BY MaLop";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String param = "%" + keyword + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lop lop = new Lop();
                    lop.setLopId(rs.getInt("LopID"));
                    lop.setMaLop(rs.getString("MaLop"));
                    lop.setTenLop(rs.getString("TenLop"));
                    lop.setKhoaHoc(rs.getString("KhoaHoc"));
                    lop.setNganhHoc(rs.getString("NganhHoc"));
                    lop.setKhoa(rs.getString("Khoa"));
                    lop.setTrangThai(rs.getBoolean("TrangThai"));
                    list.add(lop);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm lớp: " + e.getMessage());
        }
        return list;
    }
}

