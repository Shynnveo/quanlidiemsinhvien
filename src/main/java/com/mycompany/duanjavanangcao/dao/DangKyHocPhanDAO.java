package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.DangKyHocPhan;
import com.mycompany.duanjavanangcao.model.LopHocPhan;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu đăng ký học phần trong CSDL duanjavanangcao
 */
public class DangKyHocPhanDAO {

    /**
     * Lấy toàn bộ danh sách đăng ký học phần của một sinh viên
     */
    public List<DangKyHocPhan> findBySinhVien(int sinhVienId) {
        return findBySinhVienAndHocKy(sinhVienId, 0);
    }

    /**
     * Lấy danh sách đăng ký học phần của sinh viên theo học kỳ (hocKyId <= 0: tất cả học kỳ)
     */
    public List<DangKyHocPhan> findBySinhVienAndHocKy(int sinhVienId, int hocKyId) {
        List<DangKyHocPhan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dk.DangKyID, dk.SinhVienID, dk.LopHocPhanID, dk.NgayDangKy, dk.TrangThai, ")
           .append("       sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, ")
           .append("       lhp.MaLopHocPhan, lhp.LichHoc, lhp.PhongHoc, ")
           .append("       mh.TenMonHoc, mh.SoTinChi, ")
           .append("       gv.HoTen AS TenGiangVien, ")
           .append("       hk.TenHocKy ")
           .append("FROM DangKyHocPhan dk ")
           .append("INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID ")
           .append("INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID ")
           .append("INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID ")
           .append("INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID ")
           .append("LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID ")
           .append("WHERE dk.SinhVienID = ? ");

        if (hocKyId > 0) {
            sql.append("AND lhp.HocKyID = ? ");
        }
        sql.append("ORDER BY dk.NgayDangKy DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, sinhVienId);
            if (hocKyId > 0) {
                ps.setInt(2, hocKyId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DangKyHocPhan dk = new DangKyHocPhan();
                    dk.setDangKyId(rs.getInt("DangKyID"));
                    dk.setSinhVienId(rs.getInt("SinhVienID"));
                    dk.setLopHocPhanId(rs.getInt("LopHocPhanID"));
                    dk.setNgayDangKy(rs.getTimestamp("NgayDangKy"));
                    dk.setTrangThai(rs.getString("TrangThai"));

                    dk.setMaSinhVien(rs.getString("MaSinhVien"));
                    dk.setHoTenSinhVien(rs.getString("HoTenSinhVien"));
                    dk.setMaLopHocPhan(rs.getString("MaLopHocPhan"));
                    dk.setTenMonHoc(rs.getString("TenMonHoc"));
                    dk.setSoTinChi(rs.getInt("SoTinChi"));
                    dk.setTenGiangVien(rs.getString("TenGiangVien"));
                    dk.setLichHoc(rs.getString("LichHoc"));
                    dk.setPhongHoc(rs.getString("PhongHoc"));
                    dk.setTenHocKy(rs.getString("TenHocKy"));

                    list.add(dk);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách ĐKHP của sinh viên: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy danh sách các lớp học phần đang mở kèm sĩ số hiện tại và số tín chỉ
     */
    public List<LopHocPhan> getAvailableClasses(int hocKyId, String keyword) {
        List<LopHocPhan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT lhp.LopHocPhanID, lhp.MaLopHocPhan, lhp.MonHocID, lhp.HocKyID, lhp.GiangVienID, ")
           .append("       lhp.Nhom, lhp.SiSoToiDa, lhp.LichHoc, lhp.PhongHoc, lhp.TrangThai, ")
           .append("       mh.TenMonHoc, mh.SoTinChi, hk.TenHocKy, gv.HoTen AS TenGiangVien, ")
           .append("       (SELECT COUNT(*) FROM DangKyHocPhan dk WHERE dk.LopHocPhanID = lhp.LopHocPhanID AND dk.TrangThai != N'Đã hủy') AS SiSoHienTai ")
           .append("FROM LopHocPhan lhp ")
           .append("INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID ")
           .append("INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID ")
           .append("LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID ")
           .append("WHERE lhp.TrangThai = N'Đang mở' ");

        if (hocKyId > 0) {
            sql.append("AND lhp.HocKyID = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (lhp.MaLopHocPhan LIKE ? OR mh.TenMonHoc LIKE ? OR gv.HoTen LIKE ?) ");
        }
        sql.append("ORDER BY lhp.MaLopHocPhan");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hocKyId > 0) {
                ps.setInt(paramIndex++, hocKyId);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    int siSoToiDa = rs.getInt("SiSoToiDa");
                    if (!rs.wasNull()) {
                        lhp.setSiSoToiDa(siSoToiDa);
                    }

                    lhp.setLichHoc(rs.getString("LichHoc"));
                    lhp.setPhongHoc(rs.getString("PhongHoc"));
                    lhp.setTrangThai(rs.getString("TrangThai"));
                    lhp.setTenMonHoc(rs.getString("TenMonHoc"));
                    lhp.setTenHocKy(rs.getString("TenHocKy"));
                    lhp.setTenGiangVien(rs.getString("TenGiangVien"));
                    lhp.setSoTinChi(rs.getInt("SoTinChi"));
                    lhp.setSiSoHienTai(rs.getInt("SiSoHienTai"));

                    list.add(lhp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách lớp học phần đang mở: " + e.getMessage());
        }
        return list;
    }

    /**
     * Đếm số sinh viên đang học thực tế trong lớp học phần
     */
    public int countEnrolled(int lopHocPhanId) {
        String sql = "SELECT COUNT(*) FROM DangKyHocPhan WHERE LopHocPhanID = ? AND TrangThai != N'Đã hủy'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lopHocPhanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm sĩ số lớp: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Kiểm tra tính hợp lệ trước khi cho sinh viên đăng ký học phần:
     * 1. Kiểm tra lớp có đang mở không và còn chỗ không.
     * 2. Kiểm tra sinh viên đã đăng ký chính lớp này chưa.
     * 3. Kiểm tra sinh viên đã đăng ký lớp khác cùng môn trong học kỳ chưa.
     * @return null nếu hợp lệ; trả về câu thông báo lỗi nếu không đủ điều kiện.
     */
    public String checkRegistrationEligibility(int sinhVienId, int lopHocPhanId) {
        String lhpSql = "SELECT lhp.MonHocID, lhp.HocKyID, lhp.SiSoToiDa, lhp.TrangThai, "
                      + "       (SELECT COUNT(*) FROM DangKyHocPhan dk WHERE dk.LopHocPhanID = lhp.LopHocPhanID AND dk.TrangThai != N'Đã hủy') AS SiSoHienTai "
                      + "FROM LopHocPhan lhp WHERE lhp.LopHocPhanID = ?";

        String checkExactSql = "SELECT TrangThai FROM DangKyHocPhan WHERE SinhVienID = ? AND LopHocPhanID = ?";

        String checkDuplicateSubjectSql = "SELECT otherLhp.MaLopHocPhan "
                + "FROM DangKyHocPhan dk "
                + "INNER JOIN LopHocPhan otherLhp ON dk.LopHocPhanID = otherLhp.LopHocPhanID "
                + "WHERE dk.SinhVienID = ? AND dk.TrangThai != N'Đã hủy' "
                + "  AND otherLhp.MonHocID = ? AND otherLhp.HocKyID = ? AND otherLhp.LopHocPhanID <> ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Kiểm tra trạng thái lớp học phần và sĩ số
            int monHocId = 0;
            int hocKyId = 0;
            try (PreparedStatement psLhp = conn.prepareStatement(lhpSql)) {
                psLhp.setInt(1, lopHocPhanId);
                try (ResultSet rs = psLhp.executeQuery()) {
                    if (!rs.next()) {
                        return "Không tìm thấy thông tin lớp học phần!";
                    }
                    String trangThai = rs.getString("TrangThai");
                    if (!"Đang mở".equalsIgnoreCase(trangThai)) {
                        return "Lớp học phần hiện không ở trạng thái mở đăng ký!";
                    }
                    int siSoToiDa = rs.getInt("SiSoToiDa");
                    int siSoHienTai = rs.getInt("SiSoHienTai");
                    if (siSoToiDa > 0 && siSoHienTai >= siSoToiDa) {
                        return "Lớp học phần đã đủ sĩ số tối đa (" + siSoHienTai + "/" + siSoToiDa + ")!";
                    }
                    monHocId = rs.getInt("MonHocID");
                    hocKyId = rs.getInt("HocKyID");
                }
            }

            // 2. Kiểm tra xem sinh viên đã đăng ký chính lớp này chưa
            try (PreparedStatement psExact = conn.prepareStatement(checkExactSql)) {
                psExact.setInt(1, sinhVienId);
                psExact.setInt(2, lopHocPhanId);
                try (ResultSet rs = psExact.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("TrangThai");
                        if (!"Đã hủy".equalsIgnoreCase(status)) {
                            return "Sinh viên đã đăng ký lớp học phần này rồi (Trạng thái: " + status + ")!";
                        }
                    }
                }
            }

            // 3. Kiểm tra xem sinh viên đã đăng ký môn học này ở lớp khác trong cùng học kỳ chưa
            try (PreparedStatement psDup = conn.prepareStatement(checkDuplicateSubjectSql)) {
                psDup.setInt(1, sinhVienId);
                psDup.setInt(2, monHocId);
                psDup.setInt(3, hocKyId);
                psDup.setInt(4, lopHocPhanId);
                try (ResultSet rs = psDup.executeQuery()) {
                    if (rs.next()) {
                        String dupClass = rs.getString("MaLopHocPhan");
                        return "Sinh viên đã đăng ký môn học này ở lớp " + dupClass + " trong cùng học kỳ!";
                    }
                }
            }

        } catch (SQLException e) {
            return "Lỗi kiểm tra điều kiện đăng ký: " + e.getMessage();
        }

        return null; // Đủ điều kiện đăng ký
    }

    /**
     * Thực hiện đăng ký học phần:
     * - Nếu đã từng đăng ký và bị hủy: cập nhật lại trạng thái thành 'Đang học'.
     * - Nếu chưa từng đăng ký: tạo bản ghi mới trong DangKyHocPhan và bản ghi trống trong BangDiem.
     */
    public boolean register(int sinhVienId, int lopHocPhanId) throws SQLException {
        // Kiểm tra xem đã có bản ghi trong DangKyHocPhan hay chưa
        String selectSql = "SELECT DangKyID, TrangThai FROM DangKyHocPhan WHERE SinhVienID = ? AND LopHocPhanID = ?";
        Integer existingDangKyId = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, sinhVienId);
                psSelect.setInt(2, lopHocPhanId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        existingDangKyId = rs.getInt("DangKyID");
                    }
                }
            }

            if (existingDangKyId != null) {
                // Tái kích hoạt bản ghi cũ (đã hủy)
                String updateSql = "UPDATE DangKyHocPhan SET TrangThai = N'Đang học', NgayDangKy = CURRENT_TIMESTAMP WHERE DangKyID = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, existingDangKyId);
                    int affected = psUpdate.executeUpdate();
                    if (affected > 0) {
                        // Đảm bảo có bản ghi trong BangDiem
                        ensureBangDiemExists(conn, existingDangKyId);
                        return true;
                    }
                }
            } else {
                // Tạo mới bản ghi DangKyHocPhan
                String insertSql = "INSERT INTO DangKyHocPhan (SinhVienID, LopHocPhanID, NgayDangKy, TrangThai) "
                                 + "VALUES (?, ?, CURRENT_TIMESTAMP, N'Đang học')";
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setInt(1, sinhVienId);
                    psInsert.setInt(2, lopHocPhanId);
                    int affected = psInsert.executeUpdate();
                    if (affected > 0) {
                        try (ResultSet gk = psInsert.getGeneratedKeys()) {
                            if (gk.next()) {
                                int newDangKyId = gk.getInt(1);
                                ensureBangDiemExists(conn, newDangKyId);
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Hủy đăng ký học phần:
     * Kiểm tra nếu đã có điểm số nhập thì không cho hủy.
     * Cập nhật trạng thái thành 'Đã hủy'.
     */
    public boolean cancelRegistration(int dangKyId) throws SQLException {
        // Kiểm tra xem đã có điểm trên bảng điểm chưa
        String checkDiemSql = "SELECT DiemCC, DiemGK, DiemCK, DiemTongKet FROM BangDiem WHERE DangKyID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkDiemSql)) {
                psCheck.setInt(1, dangKyId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        Object cc = rs.getObject("DiemCC");
                        Object gk = rs.getObject("DiemGK");
                        Object ck = rs.getObject("DiemCK");
                        Object tk = rs.getObject("DiemTongKet");
                        if (cc != null || gk != null || ck != null || tk != null) {
                            throw new SQLException("Học phần này đã có điểm số trong hệ thống, không thể hủy đăng ký!");
                        }
                    }
                }
            }

            // Tiến hành cập nhật trạng thái đã hủy
            String updateSql = "UPDATE DangKyHocPhan SET TrangThai = N'Đã hủy' WHERE DangKyID = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, dangKyId);
                return psUpdate.executeUpdate() > 0;
            }
        }
    }

    /**
     * Đảm bảo bản ghi BangDiem tồn tại để sẵn sàng cho giảng viên vào điểm ở Giai đoạn 6
     */
    private void ensureBangDiemExists(Connection conn, int dangKyId) {
        String checkSql = "SELECT COUNT(*) FROM BangDiem WHERE DangKyID = ?";
        try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, dangKyId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertSql = "INSERT INTO BangDiem (DangKyID, NgayCapNhat, GhiChu) "
                                     + "VALUES (?, CURRENT_TIMESTAMP, N'Đăng ký mới')";
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        psInsert.setInt(1, dangKyId);
                        psInsert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra/tạo bản ghi bảng điểm: " + e.getMessage());
        }
    }

    /**
     * Tính tổng số tín chỉ đang học của sinh viên theo học kỳ
     */
    public int calculateTotalCredits(int sinhVienId, int hocKyId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ISNULL(SUM(mh.SoTinChi), 0) ")
           .append("FROM DangKyHocPhan dk ")
           .append("INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID ")
           .append("INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID ")
           .append("WHERE dk.SinhVienID = ? AND dk.TrangThai != N'Đã hủy' ");

        if (hocKyId > 0) {
            sql.append("AND lhp.HocKyID = ? ");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, sinhVienId);
            if (hocKyId > 0) {
                ps.setInt(2, hocKyId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính tổng số tín chỉ: " + e.getMessage());
        }
        return 0;
    }
}
