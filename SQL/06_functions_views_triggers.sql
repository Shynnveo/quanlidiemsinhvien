-- ====================================================================================================
-- TỆP 06: HÀM (FUNCTIONS), GIAO DIỆN XEM (VIEWS) VÀ RÀNG BUỘC KÍCH HOẠT (TRIGGERS)
-- Database: duanjavanangcao
-- 
-- MỤC ĐÍCH:
--   - Cung cấp các View trực quan để xem bảng điểm sinh viên và danh sách lớp học phần.
--   - Cung cấp Function quy đổi điểm.
--   - Cung cấp Trigger tự động ghi nhật ký (Audit Log) vào bảng LichSuDiem khi có người sửa điểm.
-- ====================================================================================================

USE duanjavanangcao;
GO

SET NOCOUNT ON;
GO

/* 1. VIEW TỔNG HỢP BẢNG ĐIỂM CHI TIẾT CỦA SINH VIÊN */
IF OBJECT_ID(N'dbo.v_BangDiemChiTiet', N'V') IS NOT NULL
    DROP VIEW dbo.v_BangDiemChiTiet;
GO

CREATE VIEW dbo.v_BangDiemChiTiet
AS
SELECT 
    b.BangDiemID,
    d.DangKyID,
    s.SinhVienID,
    s.MaSinhVien,
    s.HoTen AS HoTenSinhVien,
    l.MaLop,
    l.TenLop,
    m.MaMonHoc,
    m.TenMonHoc,
    m.SoTinChi,
    hk.MaHocKy,
    hk.TenHocKy,
    lhp.MaLopHocPhan,
    b.DiemCC,
    b.DiemGK,
    b.DiemCK,
    b.DiemTongKet,
    b.DiemChu,
    b.DiemHe4,
    d.TrangThai AS TrangThaiDangKy,
    CASE 
        WHEN b.DiemTongKet >= 4.0 THEN N'Đạt'
        WHEN b.DiemTongKet IS NOT NULL THEN N'Học lại'
        ELSE N'Chưa có điểm'
    END AS KetQuaMonHoc,
    b.NgayCapNhat,
    b.GhiChu
FROM dbo.BangDiem b
JOIN dbo.DangKyHocPhan d ON b.DangKyID = d.DangKyID
JOIN dbo.SinhVien s ON d.SinhVienID = s.SinhVienID
LEFT JOIN dbo.Lop l ON s.LopID = l.LopID
JOIN dbo.LopHocPhan lhp ON d.LopHocPhanID = lhp.LopHocPhanID
JOIN dbo.MonHoc m ON lhp.MonHocID = m.MonHocID
JOIN dbo.HocKy hk ON lhp.HocKyID = hk.HocKyID;
GO
PRINT N'-> Đã tạo View: dbo.v_BangDiemChiTiet';
GO

/* 2. FUNCTION QUY ĐỔI ĐIỂM TỔNG KẾT HỆ 10 SANG ĐIỂM CHỮ */
IF OBJECT_ID(N'dbo.fn_QuyDoiDiemChu', N'FN') IS NOT NULL
    DROP FUNCTION dbo.fn_QuyDoiDiemChu;
GO

CREATE FUNCTION dbo.fn_QuyDoiDiemChu (@DiemTK DECIMAL(4,2))
RETURNS VARCHAR(5)
AS
BEGIN
    DECLARE @DiemChu VARCHAR(5);
    IF @DiemTK IS NULL RETURN NULL;
    IF @DiemTK >= 8.5 SET @DiemChu = 'A';
    ELSE IF @DiemTK >= 8.0 SET @DiemChu = 'B+';
    ELSE IF @DiemTK >= 7.0 SET @DiemChu = 'B';
    ELSE IF @DiemTK >= 6.5 SET @DiemChu = 'C+';
    ELSE IF @DiemTK >= 5.5 SET @DiemChu = 'C';
    ELSE IF @DiemTK >= 5.0 SET @DiemChu = 'D+';
    ELSE IF @DiemTK >= 4.0 SET @DiemChu = 'D';
    ELSE SET @DiemChu = 'F';
    RETURN @DiemChu;
END
GO
PRINT N'-> Đã tạo Function: dbo.fn_QuyDoiDiemChu';
GO

/* 3. TRIGGER TỰ ĐỘNG GHI LỊCH SỬ CHỈNH SỬA ĐIỂM (AUDIT LOG VÀO LichSuDiem) */
IF OBJECT_ID(N'dbo.trg_GhiNhanLichSuDiem', N'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_GhiNhanLichSuDiem;
GO

CREATE TRIGGER dbo.trg_GhiNhanLichSuDiem
ON dbo.BangDiem
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Chỉ ghi nhận lịch sử khi có sự thay đổi thực sự ở một trong các cột điểm
    INSERT INTO dbo.LichSuDiem (
        BangDiemID,
        DiemCCCu, DiemGKCu, DiemCKCu, DiemTongKetCu, DiemChuCu, DiemHe4Cu,
        DiemCCMoi, DiemGKMoi, DiemCKMoi, DiemTongKetMoi, DiemChuMoi, DiemHe4Moi,
        LyDo, NguoiThayDoiID, ThoiGianThayDoi
    )
    SELECT 
        i.BangDiemID,
        d.DiemCC, d.DiemGK, d.DiemCK, d.DiemTongKet, d.DiemChu, d.DiemHe4,
        i.DiemCC, i.DiemGK, i.DiemCK, i.DiemTongKet, i.DiemChu, i.DiemHe4,
        N'Cập nhật điểm từ hệ thống',
        ISNULL((SELECT TOP 1 TaiKhoanID FROM dbo.TaiKhoan WHERE VaiTro = 'ADMIN'), 1),
        SYSDATETIME()
    FROM inserted i
    JOIN deleted d ON i.BangDiemID = d.BangDiemID
    WHERE ISNULL(i.DiemCC, -1) <> ISNULL(d.DiemCC, -1)
       OR ISNULL(i.DiemGK, -1) <> ISNULL(d.DiemGK, -1)
       OR ISNULL(i.DiemCK, -1) <> ISNULL(d.DiemCK, -1);
END
GO
PRINT N'-> Đã tạo Trigger: dbo.trg_GhiNhanLichSuDiem';
GO

