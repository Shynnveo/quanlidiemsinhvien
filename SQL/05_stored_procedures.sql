-- ====================================================================================================
-- TỆP 05: THỦ TỤC LƯU TRỮ HỖ TRỢ NGHIỆP VỤ (STORED PROCEDURES)
-- Database: duanjavanangcao
-- 
-- NGUYÊN TẮC:
--   - Đảm bảo logic tính toán thống nhất 100% với mã Java (DiemUtil.java và BangDiemDAO.java).
--   - Công thức tính điểm tổng kết: TK = CC * 0.2 + GK * 0.3 + CK * 0.5
--   - Quy đổi điểm chữ và hệ 4 theo đúng chuẩn tín chỉ UTT.
-- ====================================================================================================

USE duanjavanangcao;
GO

SET NOCOUNT ON;
GO

/* 1. Thủ tục cập nhật điểm học phần và tự động tính điểm tổng kết */
IF OBJECT_ID(N'dbo.sp_CapNhatDiemHocPhan', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_CapNhatDiemHocPhan;
GO

CREATE PROCEDURE dbo.sp_CapNhatDiemHocPhan
    @DangKyID INT,
    @DiemCC DECIMAL(4,2),
    @DiemGK DECIMAL(4,2),
    @DiemCK DECIMAL(4,2),
    @GhiChu NVARCHAR(255) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra bản ghi đăng ký tồn tại
    IF NOT EXISTS (SELECT 1 FROM dbo.DangKyHocPhan WHERE DangKyID = @DangKyID)
    BEGIN
        RAISERROR(N'Bản ghi đăng ký học phần không tồn tại!', 16, 1);
        RETURN;
    END

    -- Tính điểm tổng kết theo công thức chuẩn: CC*0.2 + GK*0.3 + CK*0.5
    DECLARE @DiemTK DECIMAL(4,2) = NULL;
    DECLARE @DiemChu VARCHAR(5) = NULL;
    DECLARE @DiemHe4 DECIMAL(3,2) = NULL;

    IF @DiemCC IS NOT NULL AND @DiemGK IS NOT NULL AND @DiemCK IS NOT NULL
    BEGIN
        SET @DiemTK = ROUND((@DiemCC * 0.20) + (@DiemGK * 0.30) + (@DiemCK * 0.50), 1);

        -- Quy đổi theo quy chế đào tạo tín chỉ UTT
        IF @DiemTK >= 8.5 BEGIN SET @DiemChu = 'A';  SET @DiemHe4 = 4.0; END
        ELSE IF @DiemTK >= 8.0 BEGIN SET @DiemChu = 'B+'; SET @DiemHe4 = 3.5; END
        ELSE IF @DiemTK >= 7.0 BEGIN SET @DiemChu = 'B';  SET @DiemHe4 = 3.0; END
        ELSE IF @DiemTK >= 6.5 BEGIN SET @DiemChu = 'C+'; SET @DiemHe4 = 2.5; END
        ELSE IF @DiemTK >= 5.5 BEGIN SET @DiemChu = 'C';  SET @DiemHe4 = 2.0; END
        ELSE IF @DiemTK >= 5.0 BEGIN SET @DiemChu = 'D+'; SET @DiemHe4 = 1.5; END
        ELSE IF @DiemTK >= 4.0 BEGIN SET @DiemChu = 'D';  SET @DiemHe4 = 1.0; END
        ELSE BEGIN SET @DiemChu = 'F'; SET @DiemHe4 = 0.0; END
    END

    -- Cập nhật hoặc thêm mới bảng điểm
    IF EXISTS (SELECT 1 FROM dbo.BangDiem WHERE DangKyID = @DangKyID)
    BEGIN
        UPDATE dbo.BangDiem
        SET DiemCC = @DiemCC,
            DiemGK = @DiemGK,
            DiemCK = @DiemCK,
            DiemTongKet = @DiemTK,
            DiemChu = @DiemChu,
            DiemHe4 = @DiemHe4,
            GhiChu = @GhiChu,
            NgayCapNhat = SYSDATETIME()
        WHERE DangKyID = @DangKyID;
    END
    ELSE
    BEGIN
        INSERT INTO dbo.BangDiem (DangKyID, DiemCC, DiemGK, DiemCK, DiemTongKet, DiemChu, DiemHe4, GhiChu)
        VALUES (@DangKyID, @DiemCC, @DiemGK, @DiemCK, @DiemTK, @DiemChu, @DiemHe4, @GhiChu);
    END
END
GO
PRINT N'-> Đã tạo Stored Procedure: dbo.sp_CapNhatDiemHocPhan';
GO

/* 2. Thủ tục tổng hợp điểm GPA và CPA cho một sinh viên trong một học kỳ */
IF OBJECT_ID(N'dbo.sp_TongHopKetQuaHocTapSinhVien', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_TongHopKetQuaHocTapSinhVien;
GO

CREATE PROCEDURE dbo.sp_TongHopKetQuaHocTapSinhVien
    @SinhVienID INT,
    @HocKyID INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Tính số tín chỉ đăng ký, đạt, nợ và điểm GPA học kỳ
    DECLARE @SoTinChiDK INT = 0;
    DECLARE @SoTinChiDat INT = 0;
    DECLARE @SoTinChiNo INT = 0;
    DECLARE @GPA DECIMAL(3,2) = 0.0;
    DECLARE @TongDiemNhanTinChi DECIMAL(8,2) = 0.0;

    SELECT 
        @SoTinChiDK = ISNULL(SUM(m.SoTinChi), 0),
        @SoTinChiDat = ISNULL(SUM(CASE WHEN b.DiemHe4 > 0 THEN m.SoTinChi ELSE 0 END), 0),
        @SoTinChiNo = ISNULL(SUM(CASE WHEN b.DiemHe4 = 0 OR b.DiemHe4 IS NULL THEN m.SoTinChi ELSE 0 END), 0),
        @TongDiemNhanTinChi = ISNULL(SUM(b.DiemHe4 * m.SoTinChi), 0.0)
    FROM dbo.DangKyHocPhan d
    JOIN dbo.LopHocPhan l ON d.LopHocPhanID = l.LopHocPhanID
    JOIN dbo.MonHoc m ON l.MonHocID = m.MonHocID
    LEFT JOIN dbo.BangDiem b ON d.DangKyID = b.DangKyID
    WHERE d.SinhVienID = @SinhVienID AND l.HocKyID = @HocKyID;

    IF @SoTinChiDK > 0
        SET @GPA = ROUND(@TongDiemNhanTinChi / @SoTinChiDK, 2);

    -- Xếp loại học lực
    DECLARE @XepLoai NVARCHAR(30);
    IF @GPA >= 3.60 SET @XepLoai = N'Xuất sắc';
    ELSE IF @GPA >= 3.20 SET @XepLoai = N'Giỏi';
    ELSE IF @GPA >= 2.50 SET @XepLoai = N'Khá';
    ELSE IF @GPA >= 2.00 SET @XepLoai = N'Trung bình';
    ELSE SET @XepLoai = N'Yếu / Kém';

    -- Lưu hoặc cập nhật bảng KetQuaHocTap (chuẩn theo cấu trúc bảng thực tế)
    IF EXISTS (SELECT 1 FROM dbo.KetQuaHocTap WHERE SinhVienID = @SinhVienID AND HocKyID = @HocKyID)
    BEGIN
        UPDATE dbo.KetQuaHocTap
        SET GPAHocKy = @GPA,
            GPATichLuy = @GPA,
            SoTinChiDangKy = @SoTinChiDK,
            SoTinChiDat = @SoTinChiDat,
            TongTinChiTichLuy = @SoTinChiDat,
            NgayCapNhat = SYSDATETIME()
        WHERE SinhVienID = @SinhVienID AND HocKyID = @HocKyID;
    END
    ELSE
    BEGIN
        INSERT INTO dbo.KetQuaHocTap (SinhVienID, HocKyID, GPAHocKy, GPATichLuy, SoTinChiDangKy, SoTinChiDat, TongTinChiTichLuy, NgayCapNhat)
        VALUES (@SinhVienID, @HocKyID, @GPA, @GPA, @SoTinChiDK, @SoTinChiDat, @SoTinChiDat, SYSDATETIME());
    END
END
GO
PRINT N'-> Đã tạo Stored Procedure: dbo.sp_TongHopKetQuaHocTapSinhVien';
GO

