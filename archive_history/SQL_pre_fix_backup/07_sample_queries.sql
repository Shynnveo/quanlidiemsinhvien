-- ====================================================================================================
-- TỆP 07: CÁC TRUY VẤN MẪU BÁO CÁO VÀ KIỂM THỬ NGHIỆP VỤ (SAMPLE QUERIES)
-- Database: duanjavanangcao
-- 
-- MỤC ĐÍCH:
--   - Cung cấp các câu truy vấn SELECT mẫu để demo vấn đáp với giảng viên.
--   - Báo cáo kết quả học tập sinh viên, bảng điểm theo lớp học phần, thống kê cảnh báo học vụ.
--   - Toàn bộ là lệnh CHỈ ĐỌC (READ-ONLY).
-- ====================================================================================================

USE duanjavanangcao;
GO

SET NOCOUNT ON;
GO

PRINT N'========================================================================';
PRINT N'TRUY VẤN 1: BẢNG ĐIỂM CHI TIẾT CỦA MỘT LỚP HỌC PHẦN (VÍ DỤ: JAVA101)';
PRINT N'========================================================================';

SELECT 
    s.MaSinhVien,
    s.HoTen AS HoTenSinhVien,
    lop.TenLop,
    b.DiemCC,
    b.DiemGK,
    b.DiemCK,
    b.DiemTongKet,
    b.DiemChu,
    b.DiemHe4,
    b.GhiChu
FROM dbo.BangDiem b
JOIN dbo.DangKyHocPhan d ON b.DangKyID = d.DangKyID
JOIN dbo.SinhVien s ON d.SinhVienID = s.SinhVienID
LEFT JOIN dbo.Lop lop ON s.LopID = lop.LopID
JOIN dbo.LopHocPhan lhp ON d.LopHocPhanID = lhp.LopHocPhanID
WHERE lhp.MaLopHocPhan LIKE '%JAVA101%'
ORDER BY s.MaSinhVien;
GO

PRINT N'========================================================================';
PRINT N'TRUY VẤN 2: KẾT QUẢ HỌC TẬP GPA & XẾP LOẠI HỌC LỰC THEO HỌC KỲ';
PRINT N'========================================================================';

SELECT 
    s.MaSinhVien,
    s.HoTen,
    lop.TenLop,
    hk.TenHocKy,
    kq.GPAHocKy,
    kq.CPATichLuy,
    kq.SoTinChiDangKy,
    kq.SoTinChiDat,
    kq.SoTinChiNo,
    kq.XepLoaiHocLuc
FROM dbo.KetQuaHocTap kq
JOIN dbo.SinhVien s ON kq.SinhVienID = s.SinhVienID
LEFT JOIN dbo.Lop lop ON s.LopID = lop.LopID
JOIN dbo.HocKy hk ON kq.HocKyID = hk.HocKyID
ORDER BY hk.MaHocKy, kq.GPAHocKy DESC;
GO

PRINT N'========================================================================';
PRINT N'TRUY VẤN 3: DANH SÁCH SINH VIÊN BỊ CẢNH BÁO HỌC VỤ VÀ BIỆN PHÁP XỬ LÝ';
PRINT N'========================================================================';

SELECT 
    s.MaSinhVien,
    s.HoTen,
    lop.TenLop,
    hk.TenHocKy,
    cc.MucCanhCao,
    cc.LyDo,
    cc.TrangThai,
    qd.SoQuyetDinh,
    qd.NgayBanHanh,
    qd.NguoiKy
FROM dbo.CanhCaoHocTap cc
JOIN dbo.SinhVien s ON cc.SinhVienID = s.SinhVienID
LEFT JOIN dbo.Lop lop ON s.LopID = lop.LopID
JOIN dbo.HocKy hk ON cc.HocKyID = hk.HocKyID
LEFT JOIN dbo.QuyetDinhCanhCao qd ON cc.CanhCaoID = qd.CanhCaoID
ORDER BY cc.MucCanhCao, s.MaSinhVien;
GO

PRINT N'========================================================================';
PRINT N'TRUY VẤN 4: LỊCH SỬ CHỈNH SỬA ĐIỂM (AUDIT TRAIL)';
PRINT N'========================================================================';

SELECT 
    ls.LichSuID,
    s.MaSinhVien,
    s.HoTen AS SinhVien,
    m.TenMonHoc,
    ls.DiemCCCu, ls.DiemCCMoi,
    ls.DiemGKCu, ls.DiemGKMoi,
    ls.DiemCKCu, ls.DiemCKMoi,
    ls.DiemTongKetCu, ls.DiemTongKetMoi,
    ls.LyDo,
    ls.ThoiGianThayDoi,
    tk.HoTen AS NguoiThayDoi
FROM dbo.LichSuDiem ls
JOIN dbo.BangDiem b ON ls.BangDiemID = b.BangDiemID
JOIN dbo.DangKyHocPhan d ON b.DangKyID = d.DangKyID
JOIN dbo.SinhVien s ON d.SinhVienID = s.SinhVienID
JOIN dbo.LopHocPhan lhp ON d.LopHocPhanID = lhp.LopHocPhanID
JOIN dbo.MonHoc m ON lhp.MonHocID = m.MonHocID
LEFT JOIN dbo.TaiKhoan tk ON ls.NguoiThayDoiID = tk.TaiKhoanID
ORDER BY ls.ThoiGianThayDoi DESC;
GO

