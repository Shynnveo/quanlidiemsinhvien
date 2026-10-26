package com.mycompany.duanjavanangcao.dao;

import com.mycompany.duanjavanangcao.model.KetQuaHocTap;
import com.mycompany.duanjavanangcao.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO quản lý và tính toán kết quả học tập & điểm trung bình tích lũy (GPA)
 */
public class KetQuaHocTapDAO {

    /**
     * Lấy kết quả học tập theo lớp và học kỳ (lopId <= 0: tất cả lớp, hocKyId <= 0: tất cả kỳ)
     */
    public List<KetQuaHocTap> findByLopAndHocKy(int lopId, int hocKyId) {
        List<KetQuaHocTap> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT kq.KetQuaHocTapID, kq.SinhVienID, kq.HocKyID, ")
           .append("       kq.GPAHocKy, kq.GPATichLuy, kq.SoTinChiDangKy, kq.SoTinChiDat, kq.TongTinChiTichLuy, kq.NgayCapNhat, ")
           .append("       sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, l.TenLop, ")
           .append("       hk.MaHocKy, hk.TenHocKy, hk.NamHoc ")
           .append("FROM KetQuaHocTap kq ")
           .append("INNER JOIN SinhVien sv ON kq.SinhVienID = sv.SinhVienID ")
           .append("LEFT JOIN Lop l ON sv.LopID = l.LopID ")
           .append("INNER JOIN HocKy hk ON kq.HocKyID = hk.HocKyID ")
           .append("WHERE (1 = 1) ");

        if (lopId > 0) {
            sql.append("AND sv.LopID = ? ");
        }
        if (hocKyId > 0) {
            sql.append("AND kq.HocKyID = ? ");
        }
        sql.append("ORDER BY hk.MaHocKy, sv.MaSinhVien");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (lopId > 0) {
                ps.setInt(paramIndex++, lopId);
            }
            if (hocKyId > 0) {
                ps.setInt(paramIndex++, hocKyId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy kết quả học tập: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy toàn bộ lịch sử GPA qua các học kỳ của một sinh viên
     */
    public List<KetQuaHocTap> findBySinhVien(int sinhVienId) {
        List<KetQuaHocTap> list = new ArrayList<>();
        String sql = "SELECT kq.KetQuaHocTapID, kq.SinhVienID, kq.HocKyID, "
                   + "       kq.GPAHocKy, kq.GPATichLuy, kq.SoTinChiDangKy, kq.SoTinChiDat, kq.TongTinChiTichLuy, kq.NgayCapNhat, "
                   + "       sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, l.TenLop, "
                   + "       hk.MaHocKy, hk.TenHocKy, hk.NamHoc "
                   + "FROM KetQuaHocTap kq "
                   + "INNER JOIN SinhVien sv ON kq.SinhVienID = sv.SinhVienID "
                   + "LEFT JOIN Lop l ON sv.LopID = l.LopID "
                   + "INNER JOIN HocKy hk ON kq.HocKyID = hk.HocKyID "
                   + "WHERE kq.SinhVienID = ? "
                   + "ORDER BY hk.NgayBatDau, hk.MaHocKy";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sinhVienId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy kết quả GPA của sinh viên: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy kết quả học tập của một sinh viên trong một học kỳ cụ thể
     */
    public KetQuaHocTap findBySinhVienAndHocKy(int sinhVienId, int hocKyId) {
        String sql = "SELECT kq.KetQuaHocTapID, kq.SinhVienID, kq.HocKyID, "
                   + "       kq.GPAHocKy, kq.GPATichLuy, kq.SoTinChiDangKy, kq.SoTinChiDat, kq.TongTinChiTichLuy, kq.NgayCapNhat, "
                   + "       sv.MaSinhVien, sv.HoTen AS HoTenSinhVien, l.TenLop, "
                   + "       hk.MaHocKy, hk.TenHocKy, hk.NamHoc "
                   + "FROM KetQuaHocTap kq "
                   + "INNER JOIN SinhVien sv ON kq.SinhVienID = sv.SinhVienID "
                   + "LEFT JOIN Lop l ON sv.LopID = l.LopID "
                   + "INNER JOIN HocKy hk ON kq.HocKyID = hk.HocKyID "
                   + "WHERE kq.SinhVienID = ? AND kq.HocKyID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sinhVienId);
            ps.setInt(2, hocKyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kết quả học tập SV theo kỳ: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lưu hoặc cập nhật kết quả học tập vào CSDL
     */
    public boolean saveOrUpdate(KetQuaHocTap kq) {
        String checkSql = "SELECT KetQuaHocTapID FROM KetQuaHocTap WHERE SinhVienID = ? AND HocKyID = ?";
        Integer existingId = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, kq.getSinhVienId());
                psCheck.setInt(2, kq.getHocKyId());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getInt("KetQuaHocTapID");
                    }
                }
            }

            if (existingId != null) {
                String updateSql = "UPDATE KetQuaHocTap SET GPAHocKy = ?, GPATichLuy = ?, "
                                 + "                       SoTinChiDangKy = ?, SoTinChiDat = ?, "
                                 + "                       TongTinChiTichLuy = ?, NgayCapNhat = CURRENT_TIMESTAMP "
                                 + "WHERE KetQuaHocTapID = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    setDoubleOrNull(psUpdate, 1, kq.getGpaHocKy());
                    setDoubleOrNull(psUpdate, 2, kq.getGpaTichLuy());
                    psUpdate.setInt(3, kq.getSoTinChiDangKy());
                    psUpdate.setInt(4, kq.getSoTinChiDat());
                    psUpdate.setInt(5, kq.getTongTinChiTichLuy());
                    psUpdate.setInt(6, existingId);
                    return psUpdate.executeUpdate() > 0;
                }
            } else {
                String insertSql = "INSERT INTO KetQuaHocTap (SinhVienID, HocKyID, GPAHocKy, GPATichLuy, "
                                 + "                          SoTinChiDangKy, SoTinChiDat, TongTinChiTichLuy, NgayCapNhat) "
                                 + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setInt(1, kq.getSinhVienId());
                    psInsert.setInt(2, kq.getHocKyId());
                    setDoubleOrNull(psInsert, 3, kq.getGpaHocKy());
                    setDoubleOrNull(psInsert, 4, kq.getGpaTichLuy());
                    psInsert.setInt(5, kq.getSoTinChiDangKy());
                    psInsert.setInt(6, kq.getSoTinChiDat());
                    psInsert.setInt(7, kq.getTongTinChiTichLuy());
                    return psInsert.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu/cập nhật kết quả học tập: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tự động tính toán GPA học kỳ & GPA tích lũy từ dữ liệu điểm số BangDiem và lưu vào CSDL
     */
    public boolean calculateAndSave(int sinhVienId, int hocKyId) {
        // 1. Tính toán điểm trong học kỳ này
        String semesterScoreSql = "SELECT mh.SoTinChi, bd.DiemHe4, bd.DiemTongKet "
                                + "FROM BangDiem bd "
                                + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
                                + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                                + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                                + "WHERE dk.SinhVienID = ? AND lhp.HocKyID = ? AND dk.TrangThai != N'Đã hủy'";

        // 2. Tính toán điểm tích lũy toàn khóa đến hết kỳ này
        String cumulativeScoreSql = "SELECT mh.SoTinChi, bd.DiemHe4, bd.DiemTongKet "
                                  + "FROM BangDiem bd "
                                  + "INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID "
                                  + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                                  + "INNER JOIN MonHoc mh ON lhp.MonHocID = mh.MonHocID "
                                  + "INNER JOIN HocKy hkTarget ON hkTarget.HocKyID = ? "
                                  + "INNER JOIN HocKy hkCurrent ON lhp.HocKyID = hkCurrent.HocKyID "
                                  + "WHERE dk.SinhVienID = ? AND dk.TrangThai != N'Đã hủy' "
                                  + "  AND (hkCurrent.NgayBatDau <= hkTarget.NgayBatDau OR hkCurrent.HocKyID <= hkTarget.HocKyID)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            int tcDangKy = 0;
            int tcDat = 0;
            double tongDiemHe4NhanTCHocKy = 0;
            int tcTinhDiemHocKy = 0;

            try (PreparedStatement psSem = conn.prepareStatement(semesterScoreSql)) {
                psSem.setInt(1, sinhVienId);
                psSem.setInt(2, hocKyId);
                try (ResultSet rs = psSem.executeQuery()) {
                    while (rs.next()) {
                        int tc = rs.getInt("SoTinChi");
                        tcDangKy += tc;

                        Object objDiemTK = rs.getObject("DiemTongKet");
                        if (objDiemTK != null) {
                            double tk = rs.getDouble("DiemTongKet");
                            if (tk >= 4.0) {
                                tcDat += tc;
                            }
                            Object objH4 = rs.getObject("DiemHe4");
                            if (objH4 != null) {
                                double h4 = rs.getDouble("DiemHe4");
                                tongDiemHe4NhanTCHocKy += h4 * tc;
                                tcTinhDiemHocKy += tc;
                            }
                        }
                    }
                }
            }

            Double gpaHocKy = (tcTinhDiemHocKy > 0)
                    ? Math.round((tongDiemHe4NhanTCHocKy / tcTinhDiemHocKy) * 100.0) / 100.0
                    : null;

            // Tính tích lũy
            int tongTCTichLuy = 0;
            double tongDiemHe4NhanTCTichLuy = 0;
            int tcTinhDiemTichLuy = 0;

            try (PreparedStatement psCum = conn.prepareStatement(cumulativeScoreSql)) {
                psCum.setInt(1, hocKyId);
                psCum.setInt(2, sinhVienId);
                try (ResultSet rs = psCum.executeQuery()) {
                    while (rs.next()) {
                        int tc = rs.getInt("SoTinChi");
                        Object objDiemTK = rs.getObject("DiemTongKet");
                        if (objDiemTK != null) {
                            double tk = rs.getDouble("DiemTongKet");
                            if (tk >= 4.0) {
                                tongTCTichLuy += tc;
                            }
                            Object objH4 = rs.getObject("DiemHe4");
                            if (objH4 != null) {
                                double h4 = rs.getDouble("DiemHe4");
                                tongDiemHe4NhanTCTichLuy += h4 * tc;
                                tcTinhDiemTichLuy += tc;
                            }
                        }
                    }
                }
            }

            Double gpaTichLuy = (tcTinhDiemTichLuy > 0)
                    ? Math.round((tongDiemHe4NhanTCTichLuy / tcTinhDiemTichLuy) * 100.0) / 100.0
                    : null;

            KetQuaHocTap kq = new KetQuaHocTap();
            kq.setSinhVienId(sinhVienId);
            kq.setHocKyId(hocKyId);
            kq.setSoTinChiDangKy(tcDangKy);
            kq.setSoTinChiDat(tcDat);
            kq.setGpaHocKy(gpaHocKy);
            kq.setTongTinChiTichLuy(tongTCTichLuy);
            kq.setGpaTichLuy(gpaTichLuy);

            return saveOrUpdate(kq);

        } catch (SQLException e) {
            System.err.println("Lỗi khi tính toán GPA tự động: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tính toán GPA hàng loạt cho toàn bộ sinh viên có đăng ký học phần trong một học kỳ
     */
    public int calculateAndSaveForSemester(int hocKyId) {
        String sql = "SELECT DISTINCT dk.SinhVienID "
                   + "FROM DangKyHocPhan dk "
                   + "INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID "
                   + "WHERE lhp.HocKyID = ? AND dk.TrangThai != N'Đã hủy'";

        List<Integer> listSvId = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hocKyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listSvId.add(rs.getInt("SinhVienID"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách sinh viên theo kỳ: " + e.getMessage());
        }

        int countSuccess = 0;
        for (int svId : listSvId) {
            if (calculateAndSave(svId, hocKyId)) {
                countSuccess++;
            }
        }
        return countSuccess;
    }

    private void setDoubleOrNull(PreparedStatement ps, int paramIndex, Double val) throws SQLException {
        if (val != null) {
            ps.setDouble(paramIndex, val);
        } else {
            ps.setNull(paramIndex, Types.DECIMAL);
        }
    }

    private KetQuaHocTap extractFromResultSet(ResultSet rs) throws SQLException {
        KetQuaHocTap kq = new KetQuaHocTap();
        kq.setKetQuaHocTapId(rs.getInt("KetQuaHocTapID"));
        kq.setSinhVienId(rs.getInt("SinhVienID"));
        kq.setHocKyId(rs.getInt("HocKyID"));

        double gpaHk = rs.getDouble("GPAHocKy");
        if (!rs.wasNull()) kq.setGpaHocKy(gpaHk);

        double gpaTl = rs.getDouble("GPATichLuy");
        if (!rs.wasNull()) kq.setGpaTichLuy(gpaTl);

        kq.setSoTinChiDangKy(rs.getInt("SoTinChiDangKy"));
        kq.setSoTinChiDat(rs.getInt("SoTinChiDat"));
        kq.setTongTinChiTichLuy(rs.getInt("TongTinChiTichLuy"));
        kq.setNgayCapNhat(rs.getTimestamp("NgayCapNhat"));

        kq.setMaSinhVien(rs.getString("MaSinhVien"));
        kq.setHoTenSinhVien(rs.getString("HoTenSinhVien"));
        kq.setTenLop(rs.getString("TenLop"));
        kq.setMaHocKy(rs.getString("MaHocKy"));
        kq.setTenHocKy(rs.getString("TenHocKy"));
        kq.setNamHoc(rs.getString("NamHoc"));

        return kq;
    }
}

