package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.LichSuDiem;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO quản lý lịch sử chỉnh sửa điểm số trong CSDL duanjavanangcao
 */
public class LichSuDiemDAO {

    public boolean insert(LichSuDiem lsd) {
        String sql = "INSERT INTO LichSuDiem (BangDiemID, DiemCCCu, DiemGKCu, DiemCKCu, DiemTongKetCu, DiemChuCu, DiemHe4Cu, "
                   + "                       DiemCCMoi, DiemGKMoi, DiemCKMoi, DiemTongKetMoi, DiemChuMoi, DiemHe4Moi, "
                   + "                       LyDo, NguoiThayDoiID, ThoiGianThayDoi) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lsd.getBangDiemId());

            setDoubleOrNull(ps, 2, lsd.getDiemCCCu());
            setDoubleOrNull(ps, 3, lsd.getDiemGKCu());
            setDoubleOrNull(ps, 4, lsd.getDiemCKCu());
            setDoubleOrNull(ps, 5, lsd.getDiemTongKetCu());
            ps.setString(6, lsd.getDiemChuCu());
            setDoubleOrNull(ps, 7, lsd.getDiemHe4Cu());

            setDoubleOrNull(ps, 8, lsd.getDiemCCMoi());
            setDoubleOrNull(ps, 9, lsd.getDiemGKMoi());
            setDoubleOrNull(ps, 10, lsd.getDiemCKMoi());
            setDoubleOrNull(ps, 11, lsd.getDiemTongKetMoi());
            ps.setString(12, lsd.getDiemChuMoi());
            setDoubleOrNull(ps, 13, lsd.getDiemHe4Moi());

            ps.setString(14, lsd.getLyDo() != null ? lsd.getLyDo() : "Cập nhật điểm");
            ps.setInt(15, lsd.getNguoiThayDoiId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi ghi nhận lịch sử sửa điểm: " + e.getMessage());
            return false;
        }
    }

    public List<LichSuDiem> findByBangDiem(int bangDiemId) {
        List<LichSuDiem> list = new ArrayList<>();
        String sql = "SELECT lsd.LichSuDiemID, lsd.BangDiemID, "
                   + "       lsd.DiemCCCu, lsd.DiemGKCu, lsd.DiemCKCu, lsd.DiemTongKetCu, lsd.DiemChuCu, lsd.DiemHe4Cu, "
                   + "       lsd.DiemCCMoi, lsd.DiemGKMoi, lsd.DiemCKMoi, lsd.DiemTongKetMoi, lsd.DiemChuMoi, lsd.DiemHe4Moi, "
                   + "       lsd.LyDo, lsd.NguoiThayDoiID, lsd.ThoiGianThayDoi, "
                   + "       tk.HoTen AS TenNguoiThayDoi, sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, lhp.MaLopHocPhan "
                   + "FROM LichSuDiem lsd "
                   + "INNER JOIN TaiKhoan tk ON lsd.NguoiThayDoiID = tk.TaiKhoanID "
                   + "INNER JOIN BangDiem bd ON lsd.BangDiemID = bd.BangDiemID "
                   + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
                   + "INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID "
                   + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                   + "WHERE lsd.BangDiemID = ? "
                   + "ORDER BY lsd.ThoiGianThayDoi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bangDiemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử sửa điểm: " + e.getMessage());
        }
        return list;
    }

    public List<LichSuDiem> findByLopHocPhan(int lopHocPhanId) {
        List<LichSuDiem> list = new ArrayList<>();
        String sql = "SELECT lsd.LichSuDiemID, lsd.BangDiemID, "
                   + "       lsd.DiemCCCu, lsd.DiemGKCu, lsd.DiemCKCu, lsd.DiemTongKetCu, lsd.DiemChuCu, lsd.DiemHe4Cu, "
                   + "       lsd.DiemCCMoi, lsd.DiemGKMoi, lsd.DiemCKMoi, lsd.DiemTongKetMoi, lsd.DiemChuMoi, lsd.DiemHe4Moi, "
                   + "       lsd.LyDo, lsd.NguoiThayDoiID, lsd.ThoiGianThayDoi, "
                   + "       tk.HoTen AS TenNguoiThayDoi, sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, lhp.MaLopHocPhan "
                   + "FROM LichSuDiem lsd "
                   + "INNER JOIN TaiKhoan tk ON lsd.NguoiThayDoiID = tk.TaiKhoanID "
                   + "INNER JOIN BangDiem bd ON lsd.BangDiemID = bd.BangDiemID "
                   + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
                   + "INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID "
                   + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                   + "WHERE lhp.LopHocPhanID = ? "
                   + "ORDER BY lsd.ThoiGianThayDoi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lopHocPhanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử sửa điểm của lớp học phần: " + e.getMessage());
        }
        return list;
    }

    private void setDoubleOrNull(PreparedStatement ps, int paramIndex, Double val) throws SQLException {
        if (val != null) {
            ps.setDouble(paramIndex, val);
        } else {
            ps.setNull(paramIndex, Types.DECIMAL);
        }
    }

    private LichSuDiem extractFromResultSet(ResultSet rs) throws SQLException {
        LichSuDiem lsd = new LichSuDiem();
        lsd.setLichSuDiemId(rs.getInt("LichSuDiemID"));
        lsd.setBangDiemId(rs.getInt("BangDiemID"));

        double ccCu = rs.getDouble("DiemCCCu");
        if (!rs.wasNull()) lsd.setDiemCCCu(ccCu);

        double gkCu = rs.getDouble("DiemGKCu");
        if (!rs.wasNull()) lsd.setDiemGKCu(gkCu);

        double ckCu = rs.getDouble("DiemCKCu");
        if (!rs.wasNull()) lsd.setDiemCKCu(ckCu);

        double tkCu = rs.getDouble("DiemTongKetCu");
        if (!rs.wasNull()) lsd.setDiemTongKetCu(tkCu);

        lsd.setDiemChuCu(rs.getString("DiemChuCu"));

        double h4Cu = rs.getDouble("DiemHe4Cu");
        if (!rs.wasNull()) lsd.setDiemHe4Cu(h4Cu);

        double ccMoi = rs.getDouble("DiemCCMoi");
        if (!rs.wasNull()) lsd.setDiemCCMoi(ccMoi);

        double gkMoi = rs.getDouble("DiemGKMoi");
        if (!rs.wasNull()) lsd.setDiemGKMoi(gkMoi);

        double ckMoi = rs.getDouble("DiemCKMoi");
        if (!rs.wasNull()) lsd.setDiemCKMoi(ckMoi);

        double tkMoi = rs.getDouble("DiemTongKetMoi");
        if (!rs.wasNull()) lsd.setDiemTongKetMoi(tkMoi);

        lsd.setDiemChuMoi(rs.getString("DiemChuMoi"));

        double h4Moi = rs.getDouble("DiemHe4Moi");
        if (!rs.wasNull()) lsd.setDiemHe4Moi(h4Moi);

        lsd.setLyDo(rs.getString("LyDo"));
        lsd.setNguoiThayDoiId(rs.getInt("NguoiThayDoiID"));
        lsd.setThoiGianThayDoi(rs.getTimestamp("ThoiGianThayDoi"));

        lsd.setTenNguoiThayDoi(rs.getString("TenNguoiThayDoi"));
        lsd.setMaSinhVien(rs.getString("MaSinhVien"));
        lsd.setHoTenSinhVien(rs.getString("HoTenSinhVien"));
        lsd.setMaLopHocPhan(rs.getString("MaLopHocPhan"));

        return lsd;
    }
}

