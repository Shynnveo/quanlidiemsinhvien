package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.CanhCaoHocTap;
import com.mycompany.duanjavanangcao.model.KetQuaHocTap;
import com.mycompany.duanjavanangcao.model.ThongBao;
import com.mycompany.duanjavanangcao.model.TieuChiCanhCao;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO quản lý nghiệp vụ Cảnh báo học tập và Xử lý học vụ
 */
public class CanhCaoHocTapDAO {

    private final TieuChiCanhCaoDAO tieuChiDAO = new TieuChiCanhCaoDAO();
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

    private final String BASE_SELECT =
            "SELECT cc.CanhCaoID, cc.SinhVienID, cc.HocKyID, cc.TieuChiID, cc.NgayXet, "
            + "       cc.LyDo, cc.MucCanhCao, cc.TrangThai, cc.NguoiXetID, cc.GhiChu, "
            + "       sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, sv.TaiKhoanID AS TaiKhoanSinhVienID, l.TenLop, "
            + "       hk.MaHocKy, hk.TenHocKy, "
            + "       tc.MaTieuChi, tc.TenTieuChi, "
            + "       tk.HoTen AS TenNguoiXet, "
            + "       kq.GPAHocKy, kq.GPATichLuy "
            + "FROM CanhCaoHocTap cc "
            + "INNER JOIN SinhVien sv ON cc.SinhVienID = sv.SinhVienID "
            + "LEFT JOIN Lop l ON sv.LopID = l.LopID "
            + "INNER JOIN HocKy hk ON cc.HocKyID = hk.HocKyID "
            + "INNER JOIN TieuChiCanhCao tc ON cc.TieuChiID = tc.TieuChiID "
            + "LEFT JOIN TaiKhoan tk ON cc.NguoiXetID = tk.TaiKhoanID "
            + "LEFT JOIN KetQuaHocTap kq ON (kq.SinhVienID = cc.SinhVienID AND kq.HocKyID = cc.HocKyID) ";

    /**
     * Lấy toàn bộ danh sách cảnh báo học tập
     */
    public List<CanhCaoHocTap> findAll() {
        List<CanhCaoHocTap> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY cc.NgayXet DESC, sv.MaSinhVien ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách cảnh báo học tập: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy danh sách cảnh báo học tập của một sinh viên
     */
    public List<CanhCaoHocTap> findBySinhVien(int sinhVienId) {
        List<CanhCaoHocTap> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE cc.SinhVienID = ? ORDER BY hk.MaHocKy DESC, cc.NgayXet DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sinhVienId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy cảnh báo của sinh viên: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lọc và tìm kiếm danh sách cảnh báo học tập
     */
    public List<CanhCaoHocTap> findByFilter(Integer hocKyId, Integer lopId, String mucCanhCao, String trangThai, String keyword) {
        List<CanhCaoHocTap> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE (1 = 1) ");

        if (hocKyId != null && hocKyId > 0) {
            sql.append("AND cc.HocKyID = ? ");
        }
        if (lopId != null && lopId > 0) {
            sql.append("AND sv.LopID = ? ");
        }
        if (mucCanhCao != null && !mucCanhCao.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(mucCanhCao)) {
            sql.append("AND cc.MucCanhCao = ? ");
        }
        if (trangThai != null && !trangThai.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(trangThai)) {
            sql.append("AND cc.TrangThai = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (sv.MaSinhVien LIKE ? OR sv.HoTen LIKE ? OR l.TenLop LIKE ?) ");
        }

        sql.append("ORDER BY hk.MaHocKy DESC, cc.NgayXet DESC, sv.MaSinhVien ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hocKyId != null && hocKyId > 0) {
                ps.setInt(paramIndex++, hocKyId);
            }
            if (lopId != null && lopId > 0) {
                ps.setInt(paramIndex++, lopId);
            }
            if (mucCanhCao != null && !mucCanhCao.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(mucCanhCao)) {
                ps.setString(paramIndex++, mucCanhCao.trim());
            }
            if (trangThai != null && !trangThai.trim().isEmpty() && !"Tất cả".equalsIgnoreCase(trangThai)) {
                ps.setString(paramIndex++, trangThai.trim());
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim() + "%";
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
            System.err.println("Lỗi khi lọc danh sách cảnh báo học tập: " + e.getMessage());
        }
        return list;
    }

    /**
     * Tìm chi tiết cảnh báo học tập theo ID
     */
    public CanhCaoHocTap findById(int canhCaoId) {
        String sql = BASE_SELECT + "WHERE cc.CanhCaoID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, canhCaoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm cảnh báo học tập theo ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Đếm số lần sinh viên đã bị cảnh báo học tập trước học kỳ xét
     */
    public int countPreviousWarnings(int sinhVienId, int currentHocKyId) {
        String sql = "SELECT COUNT(DISTINCT HocKyID) FROM CanhCaoHocTap "
                   + "WHERE SinhVienID = ? AND HocKyID < ? AND TrangThai != N'Đã hủy'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sinhVienId);
            ps.setInt(2, currentHocKyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm số lần cảnh báo trước đó: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Kiểm tra xem sinh viên đã có cảnh báo với tiêu chí này trong học kỳ chưa (tránh trùng lặp)
     */
    public boolean exists(int sinhVienId, int hocKyId, int tieuChiId) {
        String sql = "SELECT COUNT(*) FROM CanhCaoHocTap "
                   + "WHERE SinhVienID = ? AND HocKyID = ? AND TieuChiID = ? AND TrangThai != N'Đã hủy'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sinhVienId);
            ps.setInt(2, hocKyId);
            ps.setInt(3, tieuChiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra trùng lặp cảnh báo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Thêm cảnh báo học tập mới vào CSDL và gửi thông báo cho sinh viên
     */
    public boolean insert(CanhCaoHocTap cc) {
        String sql = "INSERT INTO CanhCaoHocTap (SinhVienID, HocKyID, TieuChiID, NgayXet, LyDo, MucCanhCao, TrangThai, NguoiXetID, GhiChu) "
                   + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cc.getSinhVienId());
            ps.setInt(2, cc.getHocKyId());
            ps.setInt(3, cc.getTieuChiId());
            ps.setString(4, cc.getLyDo());
            ps.setString(5, cc.getMucCanhCao() != null ? cc.getMucCanhCao() : "Cảnh báo mức 1");
            ps.setString(6, cc.getTrangThai() != null ? cc.getTrangThai() : "Đang áp dụng");

            if (cc.getNguoiXetId() != null && cc.getNguoiXetId() > 0) {
                ps.setInt(7, cc.getNguoiXetId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, cc.getGhiChu());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // Tự động gửi thông báo cho sinh viên nếu có TaiKhoanID
                if (cc.getTaiKhoanSinhVienId() != null && cc.getTaiKhoanSinhVienId() > 0) {
                    ThongBao tb = new ThongBao();
                    tb.setTaiKhoanId(cc.getTaiKhoanSinhVienId());
                    tb.setTieuDe("CẢNH BÁO HỌC TẬP: " + cc.getMucCanhCao());
                    String noiDung = "Nhà trường thông báo: Bạn bị " + cc.getMucCanhCao() + "\n"
                                   + "Lý do: " + cc.getLyDo() + "\n"
                                   + "Vui lòng liên hệ Cố vấn học tập để xây dựng kế hoạch học tập cải thiện điểm.";
                    tb.setNoiDung(noiDung);
                    tb.setLoaiThongBao("Cảnh báo học tập");
                    thongBaoDAO.insert(tb);
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm cảnh báo học tập: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật thông tin cảnh báo học tập (Mức cảnh báo, Trạng thái, Lý do, Ghi chú)
     */
    public boolean update(CanhCaoHocTap cc) {
        String sql = "UPDATE CanhCaoHocTap SET LyDo = ?, MucCanhCao = ?, TrangThai = ?, "
                   + "                       NguoiXetID = ?, GhiChu = ?, NgayXet = CURRENT_TIMESTAMP "
                   + "WHERE CanhCaoID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cc.getLyDo());
            ps.setString(2, cc.getMucCanhCao());
            ps.setString(3, cc.getTrangThai());

            if (cc.getNguoiXetId() != null && cc.getNguoiXetId() > 0) {
                ps.setInt(4, cc.getNguoiXetId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, cc.getGhiChu());
            ps.setInt(6, cc.getCanhCaoId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật cảnh báo học tập: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa cảnh báo học tập
     */
    public boolean delete(int canhCaoId) {
        String sql = "DELETE FROM CanhCaoHocTap WHERE CanhCaoID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, canhCaoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa cảnh báo học tập: " + e.getMessage());
            return false;
        }
    }

    /**
     * Quét xét cảnh báo học tập tự động cho toàn bộ sinh viên trong một học kỳ:
     * - Tự động tính toán điểm GPA và nợ tín chỉ.
     * - Đối chiếu với các tiêu chí cảnh báo đang hiệu lực (TieuChiCanhCao).
     * - Tự động tính mức cảnh báo (Mức 1, Mức 2, Mức 3).
     * - Ghi nhận vào CanhCaoHocTap và gửi thông báo đến tài khoản sinh viên.
     * @return Số lượng quyết định cảnh báo mới được phát hành
     */
    public int quetCanhCaoTuDong(int hocKyId, int nguoiXetId) {
        int countIssued = 0;
        List<TieuChiCanhCao> activeTieuChi = tieuChiDAO.findActive();
        if (activeTieuChi.isEmpty()) {
            System.out.println("Không có tiêu chí cảnh báo nào đang kích hoạt.");
            return 0;
        }

        // Lấy danh sách tất cả sinh viên có học tập trong kỳ này
        String sqlSinhVien = "SELECT DISTINCT sv.SinhVienID, sv.MaSinhVien, sv.HoTen, sv.TaiKhoanID, sv.LopID "
                           + "FROM SinhVien sv "
                           + "INNER JOIN DangKyHocPhan dk ON sv.SinhVienID = dk.SinhVienID "
                           + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                           + "WHERE lhp.HocKyID = ? AND dk.TrangThai != N'Đã hủy'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psSV = conn.prepareStatement(sqlSinhVien)) {

            psSV.setInt(1, hocKyId);
            try (ResultSet rsSV = psSV.executeQuery()) {
                while (rsSV.next()) {
                    int svId = rsSV.getInt("SinhVienID");
                    String maSV = rsSV.getString("MaSinhVien");
                    String hoTen = rsSV.getString("HoTen");
                    int tkId = rsSV.getInt("TaiKhoanID");

                    // 1. Tự động tính toán hoặc cập nhật GPA học kỳ
                    ketQuaDAO.calculateAndSave(svId, hocKyId);
                    KetQuaHocTap kq = ketQuaDAO.findBySinhVienAndHocKy(svId, hocKyId);

                    // 2. Tính số tín chỉ nợ (môn có điểm tổng kết < 4.0)
                    int soTinChiNo = tinhSoTinChiNoHocKy(conn, svId, hocKyId);

                    // 3. Đếm số lần đã bị cảnh báo trước đó để xác định mức cảnh báo
                    int prevWarnings = countPreviousWarnings(svId, hocKyId);
                    String mucCanhCao = (prevWarnings == 0) ? "Cảnh báo mức 1"
                                      : (prevWarnings == 1 ? "Cảnh báo mức 2" : "Cảnh báo mức 3 (Buộc thôi học)");

                    // 4. Đối chiếu từng tiêu chí
                    for (TieuChiCanhCao tc : activeTieuChi) {
                        boolean viPham = false;
                        String lyDoViPham = "";

                        // Tiêu chí 1: GPA học kỳ thấp
                        if (tc.getGpaHocKyToiDa() != null && kq != null && kq.getGpaHocKy() != null) {
                            if (kq.getGpaHocKy() <= tc.getGpaHocKyToiDa()) {
                                viPham = true;
                                lyDoViPham = String.format("GPA học kỳ đạt %.2f (dưới ngưỡng tối thiểu %.2f theo quy chế)",
                                        kq.getGpaHocKy(), tc.getGpaHocKyToiDa());
                            }
                        }

                        // Tiêu chí 2: Nợ tín chỉ vượt ngưỡng
                        if (!viPham && tc.getSoTinChiNoToiThieu() != null) {
                            if (soTinChiNo >= tc.getSoTinChiNoToiThieu()) {
                                viPham = true;
                                lyDoViPham = String.format("Nợ %d tín chỉ không đạt trong kỳ (ngưỡng cảnh báo từ %d tín chỉ)",
                                        soTinChiNo, tc.getSoTinChiNoToiThieu());
                            }
                        }

                        // Tiêu chí 3: GPA tích lũy thấp
                        if (!viPham && tc.getGpaTichLuyToiDa() != null && kq != null && kq.getGpaTichLuy() != null) {
                            if (kq.getGpaTichLuy() <= tc.getGpaTichLuyToiDa()) {
                                viPham = true;
                                lyDoViPham = String.format("Điểm trung bình tích lũy CPA đạt %.2f (dưới ngưỡng %.2f)",
                                        kq.getGpaTichLuy(), tc.getGpaTichLuyToiDa());
                            }
                        }

                        // Nếu vi phạm và chưa được tạo cảnh báo cho tiêu chí này trong kỳ
                        if (viPham && !exists(svId, hocKyId, tc.getTieuChiId())) {
                            CanhCaoHocTap cc = new CanhCaoHocTap();
                            cc.setSinhVienId(svId);
                            cc.setHocKyId(hocKyId);
                            cc.setTieuChiId(tc.getTieuChiId());
                            cc.setLyDo(lyDoViPham);
                            cc.setMucCanhCao(mucCanhCao);
                            cc.setTrangThai("Đang áp dụng");
                            cc.setNguoiXetId(nguoiXetId);
                            cc.setGhiChu("Hệ thống tự động quét cảnh báo học kỳ");
                            cc.setTaiKhoanSinhVienId(tkId);

                            if (insert(cc)) {
                                countIssued++;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi trong quá trình quét xét cảnh báo tự động: " + e.getMessage());
        }

        return countIssued;
    }

    /**
     * Tính tổng số tín chỉ nợ (điểm tổng kết < 4.0) của sinh viên trong học kỳ
     */
    private int tinhSoTinChiNoHocKy(Connection conn, int sinhVienId, int hocKyId) {
        String sql = "SELECT SUM(mh.SoTinChi) AS TongTinChiNo "
                   + "FROM BangDiem bd "
                   + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
                   + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                   + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                   + "WHERE dk.SinhVienID = ? AND lhp.HocKyID = ? AND dk.TrangThai != N'Đã hủy' "
                   + "  AND (bd.DiemTongKet IS NOT NULL AND bd.DiemTongKet < 4.0)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sinhVienId);
            ps.setInt(2, hocKyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TongTinChiNo");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính số tín chỉ nợ học kỳ: " + e.getMessage());
        }
        return 0;
    }

    private CanhCaoHocTap extractFromResultSet(ResultSet rs) throws SQLException {
        CanhCaoHocTap cc = new CanhCaoHocTap();
        cc.setCanhCaoId(rs.getInt("CanhCaoID"));
        cc.setSinhVienId(rs.getInt("SinhVienID"));
        cc.setHocKyId(rs.getInt("HocKyID"));
        cc.setTieuChiId(rs.getInt("TieuChiID"));
        cc.setNgayXet(rs.getTimestamp("NgayXet"));
        cc.setLyDo(rs.getString("LyDo"));
        cc.setMucCanhCao(rs.getString("MucCanhCao"));
        cc.setTrangThai(rs.getString("TrangThai"));

        int nxl = rs.getInt("NguoiXetID");
        if (!rs.wasNull()) cc.setNguoiXetId(nxl);

        cc.setGhiChu(rs.getString("GhiChu"));

        cc.setMaSinhVien(rs.getString("MaSinhVien"));
        cc.setHoTenSinhVien(rs.getString("HoTenSinhVien"));
        cc.setTenLop(rs.getString("TenLop"));

        int tkId = rs.getInt("TaiKhoanSinhVienID");
        if (!rs.wasNull()) cc.setTaiKhoanSinhVienId(tkId);

        cc.setMaHocKy(rs.getString("MaHocKy"));
        cc.setTenHocKy(rs.getString("TenHocKy"));
        cc.setMaTieuChi(rs.getString("MaTieuChi"));
        cc.setTenTieuChi(rs.getString("TenTieuChi"));
        cc.setTenNguoiXet(rs.getString("TenNguoiXet"));

        double gpaHK = rs.getDouble("GPAHocKy");
        if (!rs.wasNull()) cc.setGpaHocKy(gpaHK);

        double gpaTL = rs.getDouble("GPATichLuy");
        if (!rs.wasNull()) cc.setGpaTichLuy(gpaTL);

        return cc;
    }
}

