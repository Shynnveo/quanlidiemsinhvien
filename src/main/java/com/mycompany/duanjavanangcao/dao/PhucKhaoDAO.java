package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.BangDiem;
import com.mycompany.duanjavanangcao.model.PhucKhao;
import com.mycompany.duanjavanangcao.model.ThongBao;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO quản lý nghiệp vụ phúc khảo điểm trong CSDL duanjavanangcao
 */
public class PhucKhaoDAO {

    private final BangDiemDAO bangDiemDAO = new BangDiemDAO();
    private final KetQuaHocTapDAO ketQuaHocTapDAO = new KetQuaHocTapDAO();
    private final ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

    private final String BASE_SELECT =
            "SELECT pk.PhucKhaoID, pk.BangDiemID, pk.SinhVienID, pk.NgayGui, pk.LyDo, "
            + "       pk.TrangThai, pk.KetQuaXuLy, pk.NguoiXuLyID, pk.NgayXuLy, "
            + "       sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, sv.TaiKhoanID AS SinhVienTaiKhoanID, l.TenLop, "
            + "       lhp.MaLopHocPhan, lhp.HocKyID, mh.TenMonHoc, mh.SoTinChi, hk.TenHocKy, "
            + "       bd.DiemCC, bd.DiemGK, bd.DiemCK, bd.DiemTongKet, bd.DiemChu, "
            + "       tk.HoTen AS TenNguoiXuLy "
            + "FROM PhucKhao pk "
            + "INNER JOIN SinhVien sv ON pk.SinhVienID = sv.SinhVienID "
            + "LEFT JOIN Lop l ON sv.LopID = l.LopID "
            + "INNER JOIN BangDiem bd ON pk.BangDiemID = bd.BangDiemID "
            + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
            + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
            + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
            + "INNER JOIN HocKy hk ON lhp.HocKyID = hk.HocKyID "
            + "LEFT JOIN TaiKhoan tk ON pk.NguoiXuLyID = tk.TaiKhoanID ";

    /**
     * Lấy danh sách tất cả các đơn phúc khảo
     */
    public List<PhucKhao> findAll() {
        List<PhucKhao> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY pk.NgayGui DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách đơn phúc khảo: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy danh sách đơn phúc khảo của một sinh viên
     */
    public List<PhucKhao> findBySinhVien(int sinhVienId) {
        List<PhucKhao> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE pk.SinhVienID = ? ORDER BY pk.NgayGui DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sinhVienId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách đơn phúc khảo của SV: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lọc và tìm kiếm đơn phúc khảo theo trạng thái, học kỳ và từ khóa
     */
    public List<PhucKhao> findByFilter(String trangThai, Integer hocKyId, String keyword) {
        List<PhucKhao> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE (1 = 1) ");

        if (trangThai != null && !trangThai.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(trangThai)) {
            sql.append("AND pk.TrangThai = ? ");
        }
        if (hocKyId != null && hocKyId > 0) {
            sql.append("AND lhp.HocKyID = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (sv.MaSinhVien LIKE ? OR sv.HoTen LIKE ? OR mh.TenMonHoc LIKE ? OR lhp.MaLopHocPhan LIKE ?) ");
        }

        sql.append("ORDER BY pk.NgayGui DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (trangThai != null && !trangThai.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(trangThai)) {
                ps.setString(paramIndex++, trangThai.trim());
            }
            if (hocKyId != null && hocKyId > 0) {
                ps.setInt(paramIndex++, hocKyId);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, likePattern);
                ps.setString(paramIndex++, likePattern);
                ps.setString(paramIndex++, likePattern);
                ps.setString(paramIndex++, likePattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc danh sách đơn phúc khảo: " + e.getMessage());
        }
        return list;
    }

    /**
     * Tìm chi tiết đơn phúc khảo theo ID
     */
    public PhucKhao findById(int phucKhaoId) {
        String sql = BASE_SELECT + "WHERE pk.PhucKhaoID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, phucKhaoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm đơn phúc khảo theo ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Kiểm tra xem sinh viên đã có đơn phúc khảo đang chờ duyệt hoặc đang xử lý cho môn này chưa
     */
    public boolean hasPendingAppeal(int bangDiemId) {
        String sql = "SELECT COUNT(*) FROM PhucKhao WHERE BangDiemID = ? AND TrangThai IN (N'Chờ xử lý', N'Đang xử lý')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bangDiemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra đơn phúc khảo trùng lặp: " + e.getMessage());
        }
        return false;
    }

    /**
     * Sinh viên gửi đơn phúc khảo mới
     */
    public boolean guiDonPhucKhao(PhucKhao pk) {
        String sql = "INSERT INTO PhucKhao (BangDiemID, SinhVienID, NgayGui, LyDo, TrangThai) "
                   + "VALUES (?, ?, CURRENT_TIMESTAMP, ?, N'Chờ xử lý')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pk.getBangDiemId());
            ps.setInt(2, pk.getSinhVienId());
            ps.setString(3, pk.getLyDo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi gửi đơn phúc khảo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xử lý duyệt hoặc cập nhật trạng thái đơn phúc khảo:
     * - Cập nhật Trạng thái, Ý kiến phản hồi, Người xử lý, Ngày xử lý
     * - Nếu Đã chấp nhận và có điểm cuối kỳ mới: tự động cập nhật bảng điểm, ghi lịch sử sửa điểm và cập nhật GPA
     * - Gửi thông báo đến tài khoản sinh viên
     */
    public boolean xuLyPhucKhao(int phucKhaoId, String trangThai, String ketQuaXuLy, int nguoiXuLyId, Double diemCKMoi) {
        PhucKhao pk = findById(phucKhaoId);
        if (pk == null) {
            System.err.println("Không tìm thấy đơn phúc khảo ID: " + phucKhaoId);
            return false;
        }

        String sql = "UPDATE PhucKhao SET TrangThai = ?, KetQuaXuLy = ?, NguoiXuLyID = ?, NgayXuLy = CURRENT_TIMESTAMP "
                   + "WHERE PhucKhaoID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ps.setString(2, ketQuaXuLy != null ? ketQuaXuLy.trim() : "");
            ps.setInt(3, nguoiXuLyId);
            ps.setInt(4, phucKhaoId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // Nếu được chấp thuận và có điểm mới, cập nhật lại BangDiem
                if ("Đã chấp nhận".equalsIgnoreCase(trangThai) && diemCKMoi != null) {
                    BangDiem bd = bangDiemDAO.findById(pk.getBangDiemId());
                    if (bd != null) {
                        bd.setDiemCK(diemCKMoi);
                        String lyDoCapNhat = "Cập nhật sau phúc khảo (Đơn #" + phucKhaoId + ")";
                        if (ketQuaXuLy != null && !ketQuaXuLy.trim().isEmpty()) {
                            lyDoCapNhat += ": " + ketQuaXuLy.trim();
                        }
                        bangDiemDAO.updateDiem(bd, nguoiXuLyId, lyDoCapNhat);

                        // Tự động tính toán lại GPA của sinh viên trong học kỳ tương ứng
                        if (pk.getHocKyId() > 0) {
                            ketQuaHocTapDAO.calculateAndSave(pk.getSinhVienId(), pk.getHocKyId());
                        }
                    }
                }

                // Gửi thông báo cho sinh viên
                if (pk.getSinhVienTaiKhoanId() != null && pk.getSinhVienTaiKhoanId() > 0) {
                    ThongBao tb = new ThongBao();
                    tb.setTaiKhoanId(pk.getSinhVienTaiKhoanId());
                    tb.setTieuDe("Kết quả phúc khảo môn: " + pk.getTenMonHoc());
                    String noiDungTB = "Đơn phúc khảo môn " + pk.getTenMonHoc() + " (Đơn #" + phucKhaoId + ") đã được xử lý.\n"
                                     + "Trạng thái: " + trangThai + "\n"
                                     + "Nội dung phản hồi: " + (ketQuaXuLy != null ? ketQuaXuLy : "Không có ghi chú");
                    if ("Đã chấp nhận".equalsIgnoreCase(trangThai) && diemCKMoi != null) {
                        noiDungTB += "\nĐiểm cuối kỳ mới: " + diemCKMoi;
                    }
                    tb.setNoiDung(noiDungTB);
                    tb.setLoaiThongBao("Phúc khảo");
                    thongBaoDAO.insert(tb);
                }

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xử lý đơn phúc khảo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Hủy đơn phúc khảo (Chỉ cho phép hủy khi đơn còn ở trạng thái 'Chờ xử lý')
     */
    public boolean huyDonPhucKhao(int phucKhaoId) {
        String sql = "DELETE FROM PhucKhao WHERE PhucKhaoID = ? AND TrangThai = N'Chờ xử lý'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, phucKhaoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy đơn phúc khảo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách các học phần của sinh viên có thể phúc khảo (đã có điểm cuối kỳ)
     */
    public List<BangDiem> findMonHocCoThePhucKhao(int sinhVienId) {
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
                   + "WHERE sv.SinhVienID = ? AND bd.DiemCK IS NOT NULL AND dk.TrangThai != N'Đã hủy' "
                   + "ORDER BY hk.MaHocKy DESC, mh.TenMonHoc ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sinhVienId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    list.add(bd);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách môn học có thể phúc khảo: " + e.getMessage());
        }
        return list;
    }

    private PhucKhao extractFromResultSet(ResultSet rs) throws SQLException {
        PhucKhao pk = new PhucKhao();
        pk.setPhucKhaoId(rs.getInt("PhucKhaoID"));
        pk.setBangDiemId(rs.getInt("BangDiemID"));
        pk.setSinhVienId(rs.getInt("SinhVienID"));
        pk.setNgayGui(rs.getTimestamp("NgayGui"));
        pk.setLyDo(rs.getString("LyDo"));
        pk.setTrangThai(rs.getString("TrangThai"));
        pk.setKetQuaXuLy(rs.getString("KetQuaXuLy"));

        int nxl = rs.getInt("NguoiXuLyID");
        if (!rs.wasNull()) {
            pk.setNguoiXuLyId(nxl);
        }
        pk.setNgayXuLy(rs.getTimestamp("NgayXuLy"));

        pk.setMaSinhVien(rs.getString("MaSinhVien"));
        pk.setHoTenSinhVien(rs.getString("HoTenSinhVien"));

        int tkId = rs.getInt("SinhVienTaiKhoanID");
        if (!rs.wasNull()) {
            pk.setSinhVienTaiKhoanId(tkId);
        }

        pk.setTenLop(rs.getString("TenLop"));
        pk.setMaLopHocPhan(rs.getString("MaLopHocPhan"));
        pk.setHocKyId(rs.getInt("HocKyID"));
        pk.setTenMonHoc(rs.getString("TenMonHoc"));
        pk.setSoTinChi(rs.getInt("SoTinChi"));
        pk.setTenHocKy(rs.getString("TenHocKy"));

        double cc = rs.getDouble("DiemCC");
        if (!rs.wasNull()) pk.setDiemCC(cc);

        double gk = rs.getDouble("DiemGK");
        if (!rs.wasNull()) pk.setDiemGK(gk);

        double ck = rs.getDouble("DiemCK");
        if (!rs.wasNull()) pk.setDiemCK(ck);

        double tk = rs.getDouble("DiemTongKet");
        if (!rs.wasNull()) pk.setDiemTongKet(tk);

        pk.setDiemChu(rs.getString("DiemChu"));
        pk.setTenNguoiXuLy(rs.getString("TenNguoiXuLy"));

        return pk;
    }
}
