package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;
import com.mycompany.duanjavanangcao.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu bảng TaiKhoan trong SQL Server
 */
public class TaiKhoanDAO {

    /**
     * Tìm tài khoản theo tên đăng nhập
     */
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT TaiKhoanID, TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao "
                   + "FROM TaiKhoan WHERE TenDangNhap = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm tài khoản theo tên đăng nhập: " + e.getMessage());
        }
        return null;
    }

    /**
     * Tìm tài khoản theo ID
     */
    public TaiKhoan findById(int taiKhoanId) {
        String sql = "SELECT TaiKhoanID, TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao "
                   + "FROM TaiKhoan WHERE TaiKhoanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taiKhoanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm tài khoản theo ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa
     */
    public boolean existsByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra tên đăng nhập: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật mật khẩu băm cho tài khoản
     */
    public boolean updateMatKhau(int taiKhoanId, String matKhauHashMoi) {
        String sql = "UPDATE TaiKhoan SET MatKhauHash = ? WHERE TaiKhoanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matKhauHashMoi);
            ps.setInt(2, taiKhoanId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật mật khẩu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đặt lại mật khẩu (Reset password) dạng văn bản thuần sang băm SHA-256
     */
    public boolean resetMatKhau(int taiKhoanId, String matKhauMoi) {
        String hash = PasswordUtil.hashPassword(matKhauMoi);
        return updateMatKhau(taiKhoanId, hash);
    }

    /**
     * Thêm mới tài khoản
     */
    public boolean insert(TaiKhoan tk, String rawPassword) {
        String sql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String hash = (rawPassword != null && !rawPassword.isEmpty())
                    ? PasswordUtil.hashPassword(rawPassword)
                    : PasswordUtil.hashPassword("123456");

            ps.setString(1, tk.getTenDangNhap().trim());
            ps.setString(2, hash);
            ps.setString(3, tk.getHoTen());
            ps.setString(4, tk.getEmail());
            ps.setString(5, tk.getSoDienThoai());
            ps.setString(6, tk.getVaiTro());
            ps.setBoolean(7, tk.isTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm mới tài khoản: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đăng ký tài khoản mới cho sinh viên và tự động liên kết với hồ sơ sinh viên trong giao dịch SQL
     */
    public boolean dangKyTaiKhoanSinhVien(int sinhVienId, String tenDangNhap, String rawPassword, String hoTen, String email, String soDienThoai) throws SQLException {
        String insertSql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao) "
                         + "VALUES (?, ?, ?, ?, ?, 'SINHVIEN', 1, CURRENT_TIMESTAMP)";
        String updateSvSql = "UPDATE SinhVien SET TaiKhoanID = ? WHERE SinhVienID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int newTaiKhoanId = -1;
                String hash = PasswordUtil.hashPassword(rawPassword);

                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, tenDangNhap.trim());
                    ps.setString(2, hash);
                    ps.setString(3, hoTen);
                    ps.setString(4, email);
                    ps.setString(5, soDienThoai);

                    int rows = ps.executeUpdate();
                    if (rows <= 0) {
                        conn.rollback();
                        return false;
                    }

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            newTaiKhoanId = rs.getInt(1);
                        }
                    }
                }

                if (newTaiKhoanId <= 0) {
                    conn.rollback();
                    return false;
                }

                // Cập nhật trường TaiKhoanID cho sinh viên
                try (PreparedStatement psSv = conn.prepareStatement(updateSvSql)) {
                    psSv.setInt(1, newTaiKhoanId);
                    psSv.setInt(2, sinhVienId);
                    int rowsSv = psSv.executeUpdate();
                    if (rowsSv <= 0) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Cập nhật thông tin tài khoản (Họ tên, Email, SĐT, Vai trò, Trạng thái)
     */
    public boolean update(TaiKhoan tk) {
        String sql = "UPDATE TaiKhoan SET HoTen = ?, Email = ?, SoDienThoai = ?, VaiTro = ?, TrangThai = ? "
                   + "WHERE TaiKhoanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getHoTen());
            ps.setString(2, tk.getEmail());
            ps.setString(3, tk.getSoDienThoai());
            ps.setString(4, tk.getVaiTro());
            ps.setBoolean(5, tk.isTrangThai());
            ps.setInt(6, tk.getTaiKhoanId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tài khoản: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa tài khoản theo ID
     */
    public boolean delete(int taiKhoanId) {
        String sql = "DELETE FROM TaiKhoan WHERE TaiKhoanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taiKhoanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tài khoản: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách tất cả tài khoản
     */
    public List<TaiKhoan> findAll() {
        return findByFilter(null, null, null);
    }

    /**
     * Lọc và tìm kiếm danh sách tài khoản theo vai trò, trạng thái và từ khóa
     */
    public List<TaiKhoan> findByFilter(String vaiTro, String trangThai, String keyword) {
        List<TaiKhoan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT TaiKhoanID, TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao FROM TaiKhoan WHERE (1=1) ");

        if (vaiTro != null && !vaiTro.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(vaiTro)) {
            sql.append("AND VaiTro = ? ");
        }
        if (trangThai != null && !trangThai.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(trangThai)) {
            if ("Hoạt động".equalsIgnoreCase(trangThai) || "Kích hoạt".equalsIgnoreCase(trangThai)) {
                sql.append("AND TrangThai = 1 ");
            } else if ("Đã khóa".equalsIgnoreCase(trangThai) || "Khóa".equalsIgnoreCase(trangThai)) {
                sql.append("AND TrangThai = 0 ");
            }
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (TenDangNhap LIKE ? OR HoTen LIKE ? OR Email LIKE ? OR SoDienThoai LIKE ?) ");
        }

        sql.append("ORDER BY TaiKhoanID ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (vaiTro != null && !vaiTro.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(vaiTro)) {
                ps.setString(paramIndex++, vaiTro.trim());
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc tài khoản: " + e.getMessage());
        }
        return list;
    }

    private TaiKhoan extractFromResultSet(ResultSet rs) throws SQLException {
        TaiKhoan tk = new TaiKhoan();
        tk.setTaiKhoanId(rs.getInt("TaiKhoanID"));
        tk.setTenDangNhap(rs.getString("TenDangNhap"));
        tk.setMatKhauHash(rs.getString("MatKhauHash"));
        tk.setHoTen(rs.getString("HoTen"));
        tk.setEmail(rs.getString("Email"));
        tk.setSoDienThoai(rs.getString("SoDienThoai"));
        tk.setVaiTro(rs.getString("VaiTro"));
        tk.setTrangThai(rs.getBoolean("TrangThai"));
        tk.setNgayTao(rs.getTimestamp("NgayTao"));
        return tk;
    }
}
