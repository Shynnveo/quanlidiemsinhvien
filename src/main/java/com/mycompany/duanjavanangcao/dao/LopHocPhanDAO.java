package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.LopHocPhan;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu bảng LopHocPhan trong CSDL duanjavanangcao
 */
public class LopHocPhanDAO {

    public List<LopHocPhan> findAll() {
        List<LopHocPhan> list = new ArrayList<>();
        String sql = "SELECT lhp.LopHocPhanID, lhp.MaLopHocPhan, lhp.MonHocID, lhp.HocKyID, lhp.GiangVienID, "
                   + "lhp.Nhom, lhp.SiSoToiDa, lhp.LichHoc, lhp.PhongHoc, lhp.TrangThai, "
                   + "mh.TenMonHoc, hk.TenHocKy, gv.HoTen AS TenGiangVien "
                   + "FROM LopHocPhan lhp "
                   + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                   + "INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID "
                   + "LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID "
                   + "ORDER BY lhp.MaLopHocPhan";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách lớp học phần: " + e.getMessage());
        }
        return list;
    }

    public LopHocPhan findById(int lhpId) {
        String sql = "SELECT lhp.LopHocPhanID, lhp.MaLopHocPhan, lhp.MonHocID, lhp.HocKyID, lhp.GiangVienID, "
                   + "lhp.Nhom, lhp.SiSoToiDa, lhp.LichHoc, lhp.PhongHoc, lhp.TrangThai, "
                   + "mh.TenMonHoc, hk.TenHocKy, gv.HoTen AS TenGiangVien "
                   + "FROM LopHocPhan lhp "
                   + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                   + "INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID "
                   + "LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID "
                   + "WHERE lhp.LopHocPhanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lhpId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm lớp học phần theo ID: " + e.getMessage());
        }
        return null;
    }

    public List<LopHocPhan> findByGiangVien(int giangVienId) {
        List<LopHocPhan> list = new ArrayList<>();
        String sql = "SELECT lhp.LopHocPhanID, lhp.MaLopHocPhan, lhp.MonHocID, lhp.HocKyID, lhp.GiangVienID, "
                   + "lhp.Nhom, lhp.SiSoToiDa, lhp.LichHoc, lhp.PhongHoc, lhp.TrangThai, "
                   + "mh.TenMonHoc, hk.TenHocKy, gv.HoTen AS TenGiangVien "
                   + "FROM LopHocPhan lhp "
                   + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                   + "INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID "
                   + "LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID "
                   + "WHERE lhp.GiangVienID = ? "
                   + "ORDER BY lhp.MaLopHocPhan";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, giangVienId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách lớp HP theo giảng viên: " + e.getMessage());
        }
        return list;
    }

    public List<LopHocPhan> findByHocKy(int hocKyId) {
        List<LopHocPhan> list = new ArrayList<>();
        String sql = "SELECT lhp.LopHocPhanID, lhp.MaLopHocPhan, lhp.MonHocID, lhp.HocKyID, lhp.GiangVienID, "
                   + "lhp.Nhom, lhp.SiSoToiDa, lhp.LichHoc, lhp.PhongHoc, lhp.TrangThai, "
                   + "mh.TenMonHoc, hk.TenHocKy, gv.HoTen AS TenGiangVien "
                   + "FROM LopHocPhan lhp "
                   + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                   + "INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID "
                   + "LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID "
                   + "WHERE lhp.HocKyID = ? "
                   + "ORDER BY lhp.MaLopHocPhan";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hocKyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách lớp HP theo học kỳ: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(LopHocPhan lhp) {
        String sql = "INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lhp.getMaLopHocPhan());
            ps.setInt(2, lhp.getMonHocId());
            ps.setInt(3, lhp.getHocKyId());

            if (lhp.getGiangVienId() != null) {
                ps.setInt(4, lhp.getGiangVienId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, lhp.getNhom());

            if (lhp.getSiSoToiDa() != null) {
                ps.setInt(6, lhp.getSiSoToiDa());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setString(7, lhp.getLichHoc());
            ps.setString(8, lhp.getPhongHoc());
            ps.setString(9, lhp.getTrangThai() != null ? lhp.getTrangThai() : "Đang mở");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm lớp học phần: " + e.getMessage());
            return false;
        }
    }

    public boolean update(LopHocPhan lhp) {
        String sql = "UPDATE LopHocPhan SET MaLopHocPhan = ?, MonHocID = ?, HocKyID = ?, GiangVienID = ?, "
                   + "Nhom = ?, SiSoToiDa = ?, LichHoc = ?, PhongHoc = ?, TrangThai = ? "
                   + "WHERE LopHocPhanID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lhp.getMaLopHocPhan());
            ps.setInt(2, lhp.getMonHocId());
            ps.setInt(3, lhp.getHocKyId());

            if (lhp.getGiangVienId() != null) {
                ps.setInt(4, lhp.getGiangVienId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, lhp.getNhom());

            if (lhp.getSiSoToiDa() != null) {
                ps.setInt(6, lhp.getSiSoToiDa());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setString(7, lhp.getLichHoc());
            ps.setString(8, lhp.getPhongHoc());
            ps.setString(9, lhp.getTrangThai());
            ps.setInt(10, lhp.getLopHocPhanId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật lớp học phần: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int lhpId) throws SQLException {
        // Kiểm tra xem lớp học phần đã có sinh viên đăng ký chưa
        String checkSql = "SELECT COUNT(*) FROM DangKyHocPhan WHERE LopHocPhanID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, lhpId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Không thể xóa lớp học phần này vì đã có " + rs.getInt(1) + " sinh viên đăng ký!");
                }
            }

            String deleteSql = "DELETE FROM LopHocPhan WHERE LopHocPhanID = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, lhpId);
                return psDelete.executeUpdate() > 0;
            }
        }
    }

    public List<LopHocPhan> search(String keyword) {
        List<LopHocPhan> list = new ArrayList<>();
        String sql = "SELECT lhp.LopHocPhanID, lhp.MaLopHocPhan, lhp.MonHocID, lhp.HocKyID, lhp.GiangVienID, "
                   + "lhp.Nhom, lhp.SiSoToiDa, lhp.LichHoc, lhp.PhongHoc, lhp.TrangThai, "
                   + "mh.TenMonHoc, hk.TenHocKy, gv.HoTen AS TenGiangVien "
                   + "FROM LopHocPhan lhp "
                   + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                   + "INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID "
                   + "LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID "
                   + "WHERE lhp.MaLopHocPhan LIKE ? OR mh.TenMonHoc LIKE ? OR gv.HoTen LIKE ? "
                   + "ORDER BY lhp.MaLopHocPhan";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String param = "%" + keyword + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm lớp học phần: " + e.getMessage());
        }
        return list;
    }

    private LopHocPhan extractFromResultSet(ResultSet rs) throws SQLException {
        LopHocPhan lhp = new LopHocPhan();
        lhp.setLopHocPhanId(rs.getInt("LopHocPhanID"));
        lhp.setMaLopHocPhan(rs.getString("MaLopHocPhan"));
        lhp.setMonHocId(rs.getInt("MonHocID"));
        lhp.setHocKyId(rs.getInt("HocKyID"));

        int gvId = rs.getInt("GiangVienID");
        if (!rs.wasNull()) {
            lhp.setGiangVienId(gvId);
        }

        lhp.setNhom(rs.getString("Nhom"));

        int siSo = rs.getInt("SiSoToiDa");
        if (!rs.wasNull()) {
            lhp.setSiSoToiDa(siSo);
        }

        lhp.setLichHoc(rs.getString("LichHoc"));
        lhp.setPhongHoc(rs.getString("PhongHoc"));
        lhp.setTrangThai(rs.getString("TrangThai"));

        lhp.setTenMonHoc(rs.getString("TenMonHoc"));
        lhp.setTenHocKy(rs.getString("TenHocKy"));
        lhp.setTenGiangVien(rs.getString("TenGiangVien"));

        return lhp;
    }
}

