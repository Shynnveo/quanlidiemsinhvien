package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.BangDiem;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lớp DAO thống kê số liệu tổng quan & phân tích báo cáo kết quả học tập
 */
public class ThongKeDAO {

    /**
     * Lấy thống kê tổng quan số lượng các đối tượng trong CSDL
     */
    public Map<String, Integer> getThongKeTongQuan() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT "
                   + "(SELECT COUNT(*) FROM SinhVien) AS SoSV, "
                   + "(SELECT COUNT(*) FROM GiangVien) AS SoGV, "
                   + "(SELECT COUNT(*) FROM Lop) AS SoLop, "
                   + "(SELECT COUNT(*) FROM MonHoc) AS SoMon, "
                   + "(SELECT COUNT(*) FROM LopHocPhan) AS SoLHP";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.put("SoSV", rs.getInt("SoSV"));
                stats.put("SoGV", rs.getInt("SoGV"));
                stats.put("SoLop", rs.getInt("SoLop"));
                stats.put("SoMon", rs.getInt("SoMon"));
                stats.put("SoLHP", rs.getInt("SoLHP"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê tổng quan: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Thống kê phân loại học lực theo học kỳ và lớp học
     */
    public Map<String, Integer> thongKeXepLoaiHocLuc(int hocKyId, int lopId) {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("Xuất sắc (>= 3.60)", 0);
        result.put("Giỏi (3.20 - 3.59)", 0);
        result.put("Khá (2.50 - 3.19)", 0);
        result.put("Trung bình (2.00 - 2.49)", 0);
        result.put("Yếu / Kém (< 2.00)", 0);
        result.put("Tổng số sinh viên", 0);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT kq.GPAHocKy ")
           .append("FROM KetQuaHocTap kq ")
           .append("INNER JOIN SinhVien sv ON kq.SinhVienID = sv.SinhVienID ")
           .append("WHERE kq.GPAHocKy IS NOT NULL ");

        if (hocKyId > 0) {
            sql.append("AND kq.HocKyID = ? ");
        }
        if (lopId > 0) {
            sql.append("AND sv.LopID = ? ");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hocKyId > 0) ps.setInt(paramIndex++, hocKyId);
            if (lopId > 0) ps.setInt(paramIndex++, lopId);

            try (ResultSet rs = ps.executeQuery()) {
                int tongSV = 0;
                while (rs.next()) {
                    double gpa = rs.getDouble("GPAHocKy");
                    tongSV++;
                    if (gpa >= 3.60) {
                        result.put("Xuất sắc (>= 3.60)", result.get("Xuất sắc (>= 3.60)") + 1);
                    } else if (gpa >= 3.20) {
                        result.put("Giỏi (3.20 - 3.59)", result.get("Giỏi (3.20 - 3.59)") + 1);
                    } else if (gpa >= 2.50) {
                        result.put("Khá (2.50 - 3.19)", result.get("Khá (2.50 - 3.19)") + 1);
                    } else if (gpa >= 2.00) {
                        result.put("Trung bình (2.00 - 2.49)", result.get("Trung bình (2.00 - 2.49)") + 1);
                    } else {
                        result.put("Yếu / Kém (< 2.00)", result.get("Yếu / Kém (< 2.00)") + 1);
                    }
                }
                result.put("Tổng số sinh viên", tongSV);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê xếp loại học lực: " + e.getMessage());
        }
        return result;
    }

    /**
     * Thống kê phổ điểm và tỷ lệ đạt/trượt của một lớp học phần
     */
    public Map<String, Object> thongKeDiemHocPhan(int lopHocPhanId) {
        Map<String, Object> stats = new LinkedHashMap<>();

        String sql = "SELECT bd.DiemTongKet, bd.DiemChu "
                   + "FROM BangDiem bd "
                   + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
                   + "WHERE dk.LopHocPhanID = ? AND dk.TrangThai != N'Đã hủy' AND bd.DiemTongKet IS NOT NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lopHocPhanId);
            try (ResultSet rs = ps.executeQuery()) {
                int tongSV = 0;
                int soDat = 0;
                int soTruot = 0;
                double tongDiem = 0.0;
                double maxDiem = -1.0;
                double minDiem = 11.0;

                Map<String, Integer> phoDiem = new LinkedHashMap<>();
                phoDiem.put("A", 0);
                phoDiem.put("B+", 0);
                phoDiem.put("B", 0);
                phoDiem.put("C+", 0);
                phoDiem.put("C", 0);
                phoDiem.put("D+", 0);
                phoDiem.put("D", 0);
                phoDiem.put("F", 0);

                while (rs.next()) {
                    tongSV++;
                    double diem = rs.getDouble("DiemTongKet");
                    String chu = rs.getString("DiemChu");

                    tongDiem += diem;
                    if (diem > maxDiem) maxDiem = diem;
                    if (diem < minDiem) minDiem = diem;

                    if (diem >= 4.0) {
                        soDat++;
                    } else {
                        soTruot++;
                    }

                    if (chu != null) {
                        phoDiem.put(chu, phoDiem.getOrDefault(chu, 0) + 1);
                    }
                }

                stats.put("TongSV", tongSV);
                stats.put("SoDat", soDat);
                stats.put("SoTruot", soTruot);
                stats.put("TyLeDat", tongSV > 0 ? (double) soDat * 100.0 / tongSV : 0.0);
                stats.put("DiemTB", tongSV > 0 ? tongDiem / tongSV : 0.0);
                stats.put("DiemCaoNhat", maxDiem >= 0 ? maxDiem : 0.0);
                stats.put("DiemThapNhat", minDiem <= 10.0 ? minDiem : 0.0);
                stats.put("PhoDiem", phoDiem);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê điểm học phần: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Thống kê số lượng cảnh báo học tập theo học kỳ
     */
    public Map<String, Integer> thongKeCanhCaoHocTap(int hocKyId) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Cảnh báo mức 1", 0);
        stats.put("Cảnh báo mức 2", 0);
        stats.put("Cảnh báo mức 3 (Buộc thôi học)", 0);
        stats.put("Tổng số cảnh báo", 0);

        StringBuilder sql = new StringBuilder("SELECT MucCanhCao, COUNT(*) AS SoLuong FROM CanhCaoHocTap WHERE TrangThai != N'Đã hủy' ");
        if (hocKyId > 0) {
            sql.append("AND HocKyID = ? ");
        }
        sql.append("GROUP BY MucCanhCao");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (hocKyId > 0) {
                ps.setInt(1, hocKyId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                int tong = 0;
                while (rs.next()) {
                    String muc = rs.getString("MucCanhCao");
                    int sl = rs.getInt("SoLuong");
                    tong += sl;
                    stats.put(muc, sl);
                }
                stats.put("Tổng số cảnh báo", tong);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê cảnh báo học tập: " + e.getMessage());
        }
        return stats;
    }
}
