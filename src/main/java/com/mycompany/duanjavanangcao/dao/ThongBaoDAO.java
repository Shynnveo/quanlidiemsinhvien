package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.ThongBao;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO truy vấn dữ liệu bảng ThongBao
 */
public class ThongBaoDAO {

    /**
     * Lấy danh sách các thông báo mới nhất cho tài khoản hoặc thông báo chung
     */
    public List<ThongBao> findRecentByTaiKhoan(int taiKhoanId) {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT TOP 10 ThongBaoID, TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc, ThoiGianDoc "
                   + "FROM ThongBao "
                   + "WHERE TaiKhoanID = ? OR TaiKhoanID IS NULL "
                   + "ORDER BY ThoiGianTao DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taiKhoanId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongBao tb = new ThongBao();
                    tb.setThongBaoId(rs.getInt("ThongBaoID"));
                    tb.setTaiKhoanId(rs.getInt("TaiKhoanID"));
                    tb.setTieuDe(rs.getString("TieuDe"));
                    tb.setNoiDung(rs.getString("NoiDung"));
                    tb.setLoaiThongBao(rs.getString("LoaiThongBao"));
                    tb.setThoiGianTao(rs.getTimestamp("ThoiGianTao"));
                    tb.setDaDoc(rs.getBoolean("DaDoc"));
                    tb.setThoiGianDoc(rs.getTimestamp("ThoiGianDoc"));
                    list.add(tb);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách thông báo: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy toàn bộ danh sách thông báo
     */
    public List<ThongBao> findAll() {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT TOP 20 ThongBaoID, TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc, ThoiGianDoc "
                   + "FROM ThongBao ORDER BY ThoiGianTao DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ThongBao tb = new ThongBao();
                tb.setThongBaoId(rs.getInt("ThongBaoID"));
                tb.setTaiKhoanId(rs.getInt("TaiKhoanID"));
                tb.setTieuDe(rs.getString("TieuDe"));
                tb.setNoiDung(rs.getString("NoiDung"));
                tb.setLoaiThongBao(rs.getString("LoaiThongBao"));
                tb.setThoiGianTao(rs.getTimestamp("ThoiGianTao"));
                tb.setDaDoc(rs.getBoolean("DaDoc"));
                tb.setThoiGianDoc(rs.getTimestamp("ThoiGianDoc"));
                list.add(tb);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả thông báo: " + e.getMessage());
        }
        return list;
    }

    /**
     * Thêm thông báo mới vào CSDL
     */
    public boolean insert(ThongBao tb) {
        String sql = "INSERT INTO ThongBao (TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc) "
                   + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (tb.getTaiKhoanId() > 0) {
                ps.setInt(1, tb.getTaiKhoanId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, tb.getTieuDe());
            ps.setString(3, tb.getNoiDung());
            ps.setString(4, tb.getLoaiThongBao() != null ? tb.getLoaiThongBao() : "Phúc khảo");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm thông báo: " + e.getMessage());
            return false;
        }
    }
}

