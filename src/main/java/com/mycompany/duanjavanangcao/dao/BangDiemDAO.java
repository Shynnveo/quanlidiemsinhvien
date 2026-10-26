package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.BangDiem;
import com.mycompany.duanjavanangcao.model.LichSuDiem;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;
import com.mycompany.duanjavanangcao.util.DiemUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO thao tác dữ liệu bảng BangDiem trong CSDL duanjavanangcao
 */
public class BangDiemDAO {

    private final LichSuDiemDAO lichSuDAO = new LichSuDiemDAO();

    /**
     * Lấy danh sách bảng điểm của tất cả sinh viên trong một lớp học phần
     */
    public List<BangDiem> findByLopHocPhan(int lopHocPhanId) {
        List<BangDiem> list = new ArrayList<>();
        String sql = "SELECT bd.BangDiemID, bd.DangKyID, bd.DiemCC, bd.DiemGK, bd.DiemCK, "
                   + "       bd.DiemTongKet, bd.DiemChu, bd.DiemHe4, bd.NgayCapNhat, bd.NguoiCapNhatID, bd.GhiChu, "
                   + "       sv.SinhVienID, sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, l.TenLop, "
                   + "       lhp.LopHocPhanID, lhp.MaLopHocPhan, "
                   + "       mh.TenMonHoc, mh.SoTinChi, "
                   + "       gv.HoTen AS TenGiangVien, hk.TenHocKy "
                   + "FROM BangDiem bd "
                   + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
                   + "INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID "
                   + "LEFT JOIN Lop l ON sv.LopID = l.LopID "
                   + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                   + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                   + "INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID "
                   + "LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID "
                   + "WHERE lhp.LopHocPhanID = ? AND dk.TrangThai != N'Đã hủy' "
                   + "ORDER BY sv.MaSinhVien";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lopHocPhanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy bảng điểm theo lớp học phần: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy toàn bộ bảng điểm cá nhân của một sinh viên
     */
    public List<BangDiem> findBySinhVien(int sinhVienId) {
        return findBySinhVienAndHocKy(sinhVienId, 0);
    }

    /**
     * Lấy bảng điểm cá nhân của sinh viên theo từng học kỳ
     */
    public List<BangDiem> findBySinhVienAndHocKy(int sinhVienId, int hocKyId) {
        List<BangDiem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT bd.BangDiemID, bd.DangKyID, bd.DiemCC, bd.DiemGK, bd.DiemCK, ")
           .append("       bd.DiemTongKet, bd.DiemChu, bd.DiemHe4, bd.NgayCapNhat, bd.NguoiCapNhatID, bd.GhiChu, ")
           .append("       sv.SinhVienID, sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, l.TenLop, ")
           .append("       lhp.LopHocPhanID, lhp.MaLopHocPhan, ")
           .append("       mh.TenMonHoc, mh.SoTinChi, ")
           .append("       gv.HoTen AS TenGiangVien, hk.TenHocKy ")
           .append("FROM BangDiem bd ")
           .append("INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID ")
           .append("INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID ")
           .append("LEFT JOIN Lop l ON sv.LopID = l.LopID ")
           .append("INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID ")
           .append("INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID ")
           .append("INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID ")
           .append("LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID ")
           .append("WHERE sv.SinhVienID = ? AND dk.TrangThai != N'Đã hủy' ");

        if (hocKyId > 0) {
            sql.append("AND lhp.HocKyID = ? ");
        }
        sql.append("ORDER BY hk.MaHocKy, mh.MaMonHoc");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, sinhVienId);
            if (hocKyId > 0) {
                ps.setInt(2, hocKyId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy bảng điểm sinh viên theo học kỳ: " + e.getMessage());
        }
        return list;
    }

    /**
     * Tìm bản ghi điểm theo ID
     */
    public BangDiem findById(int bangDiemId) {
        String sql = "SELECT bd.BangDiemID, bd.DangKyID, bd.DiemCC, bd.DiemGK, bd.DiemCK, "
                   + "       bd.DiemTongKet, bd.DiemChu, bd.DiemHe4, bd.NgayCapNhat, bd.NguoiCapNhatID, bd.GhiChu, "
                   + "       sv.SinhVienID, sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, l.TenLop, "
                   + "       lhp.LopHocPhanID, lhp.MaLopHocPhan, "
                   + "       mh.TenMonHoc, mh.SoTinChi, "
                   + "       gv.HoTen AS TenGiangVien, hk.TenHocKy "
                   + "FROM BangDiem bd "
                   + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
                   + "INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID "
                   + "LEFT JOIN Lop l ON sv.LopID = l.LopID "
                   + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                   + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                   + "INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID "
                   + "LEFT JOIN GiangVien gv ON lhp.GiangVienID = gv.GiangVienID "
                   + "WHERE bd.BangDiemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bangDiemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm bảng điểm theo ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cập nhật điểm số cho sinh viên:
     * - Tự động tính toán Điểm tổng kết hệ 10, Điểm chữ, Điểm hệ 4.
     * - Tự động ghi lại lịch sử chỉnh sửa vào bảng LichSuDiem.
     */
    public boolean updateDiem(BangDiem bd, int nguoiCapNhatId, String lyDo) {
        BangDiem oldBd = findById(bd.getBangDiemId());
        if (oldBd == null) {
            System.err.println("Không tìm thấy bản ghi điểm ID: " + bd.getBangDiemId());
            return false;
        }

        // Tự động tính toán điểm tổng kết và quy đổi
        Double diemTK = DiemUtil.tinhDiemTongKet(bd.getDiemCC(), bd.getDiemGK(), bd.getDiemCK());
        String diemChu = DiemUtil.quyDoiDiemChu(diemTK);
        Double diemHe4 = DiemUtil.quyDoiDiemHe4(diemTK);

        bd.setDiemTongKet(diemTK);
        bd.setDiemChu(diemChu);
        bd.setDiemHe4(diemHe4);

        String updateSql = "UPDATE BangDiem SET DiemCC = ?, DiemGK = ?, DiemCK = ?, "
                         + "                   DiemTongKet = ?, DiemChu = ?, DiemHe4 = ?, "
                         + "                   NgayCapNhat = CURRENT_TIMESTAMP, NguoiCapNhatID = ?, GhiChu = ? "
                         + "WHERE BangDiemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            setDoubleOrNull(ps, 1, bd.getDiemCC());
            setDoubleOrNull(ps, 2, bd.getDiemGK());
            setDoubleOrNull(ps, 3, bd.getDiemCK());
            setDoubleOrNull(ps, 4, bd.getDiemTongKet());
            ps.setString(5, bd.getDiemChu());
            setDoubleOrNull(ps, 6, bd.getDiemHe4());
            ps.setInt(7, nguoiCapNhatId);
            ps.setString(8, bd.getGhiChu());
            ps.setInt(9, bd.getBangDiemId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // Kiểm tra xem có sự thay đổi điểm thực tế không để ghi lịch sử
                boolean isChanged = isScoreChanged(oldBd.getDiemCC(), bd.getDiemCC())
                                 || isScoreChanged(oldBd.getDiemGK(), bd.getDiemGK())
                                 || isScoreChanged(oldBd.getDiemCK(), bd.getDiemCK());

                if (isChanged) {
                    LichSuDiem lsd = new LichSuDiem();
                    lsd.setBangDiemId(bd.getBangDiemId());
                    lsd.setDiemCCCu(oldBd.getDiemCC());
                    lsd.setDiemGKCu(oldBd.getDiemGK());
                    lsd.setDiemCKCu(oldBd.getDiemCK());
                    lsd.setDiemTongKetCu(oldBd.getDiemTongKet());
                    lsd.setDiemChuCu(oldBd.getDiemChu());
                    lsd.setDiemHe4Cu(oldBd.getDiemHe4());

                    lsd.setDiemCCMoi(bd.getDiemCC());
                    lsd.setDiemGKMoi(bd.getDiemGK());
                    lsd.setDiemCKMoi(bd.getDiemCK());
                    lsd.setDiemTongKetMoi(bd.getDiemTongKet());
                    lsd.setDiemChuMoi(bd.getDiemChu());
                    lsd.setDiemHe4Moi(bd.getDiemHe4());

                    lsd.setLyDo(lyDo != null && !lyDo.trim().isEmpty() ? lyDo : "Cập nhật điểm định kỳ");
                    lsd.setNguoiThayDoiId(nguoiCapNhatId);

                    lichSuDAO.insert(lsd);
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật điểm: " + e.getMessage());
        }
        return false;
    }

    private boolean isScoreChanged(Double oldVal, Double newVal) {
        if (oldVal == null && newVal == null) return false;
        if (oldVal == null || newVal == null) return true;
        return Math.abs(oldVal - newVal) > 0.001;
    }

    private void setDoubleOrNull(PreparedStatement ps, int paramIndex, Double val) throws SQLException {
        if (val != null) {
            ps.setDouble(paramIndex, val);
        } else {
            ps.setNull(paramIndex, Types.DECIMAL);
        }
    }

    private BangDiem extractFromResultSet(ResultSet rs) throws SQLException {
        BangDiem bd = new BangDiem();
        bd.setBangDiemId(rs.getInt("BangDiemID"));
        bd.setDangKyId(rs.getInt("DangKyID"));

        double cc = rs.getDouble("DiemCC");
        if (!rs.wasNull()) bd.setDiemCC(cc);

        double gk = rs.getDouble("DiemGK");
        if (!rs.wasNull()) bd.setDiemGK(gk);

        double ck = rs.getDouble("DiemCK");
        if (!rs.wasNull()) bd.setDiemCK(ck);

        double tk = rs.getDouble("DiemTongKet");
        if (!rs.wasNull()) bd.setDiemTongKet(tk);

        bd.setDiemChu(rs.getString("DiemChu"));

        double h4 = rs.getDouble("DiemHe4");
        if (!rs.wasNull()) bd.setDiemHe4(h4);

        bd.setNgayCapNhat(rs.getTimestamp("NgayCapNhat"));

        int nguoiCn = rs.getInt("NguoiCapNhatID");
        if (!rs.wasNull()) bd.setNguoiCapNhatId(nguoiCn);

        bd.setGhiChu(rs.getString("GhiChu"));

        // Các trường liên kết
        bd.setSinhVienId(rs.getInt("SinhVienID"));
        bd.setMaSinhVien(rs.getString("MaSinhVien"));
        bd.setHoTenSinhVien(rs.getString("HoTenSinhVien"));
        bd.setTenLop(rs.getString("TenLop"));

        bd.setLopHocPhanId(rs.getInt("LopHocPhanID"));
        bd.setMaLopHocPhan(rs.getString("MaLopHocPhan"));
        bd.setTenMonHoc(rs.getString("TenMonHoc"));
        bd.setSoTinChi(rs.getInt("SoTinChi"));
        bd.setTenGiangVien(rs.getString("TenGiangVien"));
        bd.setTenHocKy(rs.getString("TenHocKy"));

        bd.setKetQua(DiemUtil.quyDoiKetQua(bd.getDiemCC(), bd.getDiemTongKet()));

        return bd;
    }
}

