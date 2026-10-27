-- ====================================================================================================
-- TỆP 02: TẠO CẤU TRÚC BẢNG, RÀNG BUỘC VÀ CHỈ MỤC (TABLES & CONSTRAINTS DEFINITION)
-- Database: duanjavanangcao
-- 
-- HƯỚNG DẪN QUAN TRỌNG:
--   - Script này chỉ chạy khi cấu trúc các bảng chưa được tạo.
--   - Không chạy script này để ghi đè hoặc tạo lại trên database đã có sẵn dữ liệu hoạt động.
--   - Toàn bộ 17 bảng được kiểm tra an toàn bằng IF OBJECT_ID(...) IS NULL trước khi tạo.
--   - Giữ nguyên 100% tên bảng, tên cột, kiểu dữ liệu NVARCHAR, khóa chính và khóa ngoại hiện có.
-- ====================================================================================================

USE duanjavanangcao;
GO

SET NOCOUNT ON;
GO

PRINT N'========================================================================';
PRINT N'BẮT ĐẦU KIỂM TRA VÀ TẠO CẤU TRÚC 17 BẢNG...';
PRINT N'========================================================================';
GO

/* 1. Tài khoản đăng nhập hệ thống */
IF OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TaiKhoan (
        TaiKhoanID INT IDENTITY(1,1) PRIMARY KEY,
        TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
        MatKhauHash VARCHAR(255) NOT NULL,
        HoTen NVARCHAR(100) NOT NULL,
        Email VARCHAR(100) NULL,
        SoDienThoai VARCHAR(20) NULL,
        VaiTro VARCHAR(20) NOT NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_TaiKhoan_TrangThai DEFAULT (1),
        NgayTao DATETIME2 NOT NULL CONSTRAINT DF_TaiKhoan_NgayTao DEFAULT (SYSDATETIME()),
        CONSTRAINT CK_TaiKhoan_VaiTro CHECK (VaiTro IN ('ADMIN', 'GIANGVIEN', 'SINHVIEN'))
    );
    PRINT N'-> Đã tạo bảng dbo.TaiKhoan';
END
GO

/* 2. Lớp sinh viên hành chính */
IF OBJECT_ID(N'dbo.Lop', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Lop (
        LopID INT IDENTITY(1,1) PRIMARY KEY,
        MaLop VARCHAR(20) NOT NULL UNIQUE,
        TenLop NVARCHAR(100) NOT NULL,
        KhoaHoc VARCHAR(20) NULL,
        NganhHoc NVARCHAR(100) NULL,
        Khoa NVARCHAR(100) NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_Lop_TrangThai DEFAULT (1)
    );
    PRINT N'-> Đã tạo bảng dbo.Lop';
END
GO

/* 3. Giảng viên giảng dạy */
IF OBJECT_ID(N'dbo.GiangVien', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.GiangVien (
        GiangVienID INT IDENTITY(1,1) PRIMARY KEY,
        MaGiangVien VARCHAR(20) NOT NULL UNIQUE,
        HoTen NVARCHAR(100) NOT NULL,
        Email VARCHAR(100) NULL,
        SoDienThoai VARCHAR(20) NULL,
        Khoa NVARCHAR(100) NULL,
        TaiKhoanID INT NULL UNIQUE,
        CONSTRAINT FK_GiangVien_TaiKhoan FOREIGN KEY (TaiKhoanID)
            REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
    PRINT N'-> Đã tạo bảng dbo.GiangVien';
END
GO

/* 4. Sinh viên */
IF OBJECT_ID(N'dbo.SinhVien', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.SinhVien (
        SinhVienID INT IDENTITY(1,1) PRIMARY KEY,
        MaSinhVien VARCHAR(20) NOT NULL UNIQUE,
        HoTen NVARCHAR(100) NOT NULL,
        NgaySinh DATE NULL,
        GioiTinh NVARCHAR(10) NULL,
        Email VARCHAR(100) NULL,
        SoDienThoai VARCHAR(20) NULL,
        DiaChi NVARCHAR(255) NULL,
        NgayNhapHoc DATE NULL,
        TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_SinhVien_TrangThai DEFAULT (N'Đang học'),
        LopID INT NULL,
        TaiKhoanID INT NULL,
        CONSTRAINT FK_SinhVien_Lop FOREIGN KEY (LopID)
            REFERENCES dbo.Lop(LopID),
        CONSTRAINT FK_SinhVien_TaiKhoan FOREIGN KEY (TaiKhoanID)
            REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
    PRINT N'-> Đã tạo bảng dbo.SinhVien';
END
GO

-- Chỉ mục lọc duy nhất cho SinhVien.TaiKhoanID (cho phép nhiều SV chưa có tài khoản TaiKhoanID = NULL)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_SinhVien_TaiKhoanID_NotNull' AND object_id = OBJECT_ID(N'dbo.SinhVien'))
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UX_SinhVien_TaiKhoanID_NotNull
    ON dbo.SinhVien(TaiKhoanID)
    WHERE TaiKhoanID IS NOT NULL;
    PRINT N'-> Đã tạo chỉ mục duy nhất lọc UX_SinhVien_TaiKhoanID_NotNull';
END
GO

/* 5. Môn học đào tạo */
IF OBJECT_ID(N'dbo.MonHoc', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.MonHoc (
        MonHocID INT IDENTITY(1,1) PRIMARY KEY,
        MaMonHoc VARCHAR(20) NOT NULL UNIQUE,
        TenMonHoc NVARCHAR(100) NOT NULL,
        SoTinChi INT NOT NULL,
        MoTa NVARCHAR(255) NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_MonHoc_TrangThai DEFAULT (1),
        CONSTRAINT CK_MonHoc_SoTinChi CHECK (SoTinChi > 0)
    );
    PRINT N'-> Đã tạo bảng dbo.MonHoc';
END
GO

/* 6. Học kỳ đào tạo */
IF OBJECT_ID(N'dbo.HocKy', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HocKy (
        HocKyID INT IDENTITY(1,1) PRIMARY KEY,
        MaHocKy VARCHAR(20) NOT NULL UNIQUE,
        TenHocKy NVARCHAR(50) NOT NULL,
        NamHoc VARCHAR(20) NOT NULL,
        NgayBatDau DATE NULL,
        NgayKetThuc DATE NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_HocKy_TrangThai DEFAULT (1)
    );
    PRINT N'-> Đã tạo bảng dbo.HocKy';
END
GO

/* 7. Lớp học phần mở trong kỳ */
IF OBJECT_ID(N'dbo.LopHocPhan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LopHocPhan (
        LopHocPhanID INT IDENTITY(1,1) PRIMARY KEY,
        MaLopHocPhan VARCHAR(30) NOT NULL UNIQUE,
        MonHocID INT NOT NULL,
        HocKyID INT NOT NULL,
        GiangVienID INT NULL,
        Nhom VARCHAR(10) NULL,
        SiSoToiDa INT NOT NULL CONSTRAINT DF_LHP_SiSo DEFAULT (40),
        LichHoc NVARCHAR(100) NULL,
        PhongHoc NVARCHAR(50) NULL,
        TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_LHP_TrangThai DEFAULT (N'Đang mở'),
        CONSTRAINT FK_LHP_MonHoc FOREIGN KEY (MonHocID)
            REFERENCES dbo.MonHoc(MonHocID),
        CONSTRAINT FK_LHP_HocKy FOREIGN KEY (HocKyID)
            REFERENCES dbo.HocKy(HocKyID),
        CONSTRAINT FK_LHP_GiangVien FOREIGN KEY (GiangVienID)
            REFERENCES dbo.GiangVien(GiangVienID)
    );
    PRINT N'-> Đã tạo bảng dbo.LopHocPhan';
END
GO

/* 8. Đăng ký học phần của sinh viên */
IF OBJECT_ID(N'dbo.DangKyHocPhan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.DangKyHocPhan (
        DangKyID INT IDENTITY(1,1) PRIMARY KEY,
        SinhVienID INT NOT NULL,
        LopHocPhanID INT NOT NULL,
        NgayDangKy DATETIME2 NOT NULL CONSTRAINT DF_DangKy_Ngay DEFAULT (SYSDATETIME()),
        TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_DangKy_TrangThai DEFAULT (N'Đã đăng ký'),
        CONSTRAINT FK_DangKy_SinhVien FOREIGN KEY (SinhVienID)
            REFERENCES dbo.SinhVien(SinhVienID),
        CONSTRAINT FK_DangKy_LHP FOREIGN KEY (LopHocPhanID)
            REFERENCES dbo.LopHocPhan(LopHocPhanID),
        CONSTRAINT UQ_DangKy_SinhVien_LHP UNIQUE (SinhVienID, LopHocPhanID)
    );
    PRINT N'-> Đã tạo bảng dbo.DangKyHocPhan';
END
GO

/* 9. Bảng điểm học phần chi tiết */
IF OBJECT_ID(N'dbo.BangDiem', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BangDiem (
        BangDiemID INT IDENTITY(1,1) PRIMARY KEY,
        DangKyID INT NOT NULL UNIQUE,
        DiemCC DECIMAL(4,2) NULL,
        DiemGK DECIMAL(4,2) NULL,
        DiemCK DECIMAL(4,2) NULL,
        DiemTongKet DECIMAL(4,2) NULL,
        DiemChu VARCHAR(5) NULL,
        DiemHe4 DECIMAL(3,2) NULL,
        NgayCapNhat DATETIME2 NOT NULL CONSTRAINT DF_BangDiem_NgayCapNhat DEFAULT (SYSDATETIME()),
        NguoiCapNhatID INT NULL,
        GhiChu NVARCHAR(255) NULL,
        CONSTRAINT FK_BangDiem_DangKy FOREIGN KEY (DangKyID)
            REFERENCES dbo.DangKyHocPhan(DangKyID),
        CONSTRAINT FK_BangDiem_NguoiCapNhat FOREIGN KEY (NguoiCapNhatID)
            REFERENCES dbo.TaiKhoan(TaiKhoanID),
        CONSTRAINT CK_BangDiem_CC CHECK (DiemCC IS NULL OR (DiemCC >= 0.00 AND DiemCC <= 10.00)),
        CONSTRAINT CK_BangDiem_GK CHECK (DiemGK IS NULL OR (DiemGK >= 0.00 AND DiemGK <= 10.00)),
        CONSTRAINT CK_BangDiem_CK CHECK (DiemCK IS NULL OR (DiemCK >= 0.00 AND DiemCK <= 10.00)),
        CONSTRAINT CK_BangDiem_TK CHECK (DiemTongKet IS NULL OR (DiemTongKet >= 0.00 AND DiemTongKet <= 10.00)),
        CONSTRAINT CK_BangDiem_He4 CHECK (DiemHe4 IS NULL OR (DiemHe4 >= 0.00 AND DiemHe4 <= 4.00))
    );
    PRINT N'-> Đã tạo bảng dbo.BangDiem';
END
GO

/* 10. Lịch sử chỉnh sửa điểm (Kiểm toán điểm số) */
IF OBJECT_ID(N'dbo.LichSuDiem', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LichSuDiem (
        LichSuDiemID INT IDENTITY(1,1) PRIMARY KEY,
        BangDiemID INT NOT NULL,
        DiemCCCu DECIMAL(4,2) NULL,
        DiemGKCu DECIMAL(4,2) NULL,
        DiemCKCu DECIMAL(4,2) NULL,
        DiemTongKetCu DECIMAL(4,2) NULL,
        DiemChuCu VARCHAR(5) NULL,
        DiemHe4Cu DECIMAL(3,2) NULL,
        DiemCCMoi DECIMAL(4,2) NULL,
        DiemGKMoi DECIMAL(4,2) NULL,
        DiemCKMoi DECIMAL(4,2) NULL,
        DiemTongKetMoi DECIMAL(4,2) NULL,
        DiemChuMoi VARCHAR(5) NULL,
        DiemHe4Moi DECIMAL(3,2) NULL,
        LyDo NVARCHAR(500) NOT NULL,
        NguoiThayDoiID INT NOT NULL,
        ThoiGianThayDoi DATETIME2 NOT NULL CONSTRAINT DF_LichSuDiem_Time DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_LichSu_BangDiem FOREIGN KEY (BangDiemID)
            REFERENCES dbo.BangDiem(BangDiemID),
        CONSTRAINT FK_LichSu_NguoiDung FOREIGN KEY (NguoiThayDoiID)
            REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
    PRINT N'-> Đã tạo bảng dbo.LichSuDiem';
END
GO

/* 11. Đơn đề nghị phúc khảo điểm */
IF OBJECT_ID(N'dbo.PhucKhao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PhucKhao (
        PhucKhaoID INT IDENTITY(1,1) PRIMARY KEY,
        BangDiemID INT NOT NULL,
        SinhVienID INT NOT NULL,
        LyDo NVARCHAR(500) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_PhucKhao_TrangThai DEFAULT (N'Chờ xử lý'),
        PhanHoi NVARCHAR(500) NULL,
        NguoiXuLyID INT NULL,
        NgayGui DATETIME2 NOT NULL CONSTRAINT DF_PhucKhao_NgayGui DEFAULT (SYSDATETIME()),
        NgayXuLy DATETIME2 NULL,
        CONSTRAINT FK_PhucKhao_BangDiem FOREIGN KEY (BangDiemID)
            REFERENCES dbo.BangDiem(BangDiemID),
        CONSTRAINT FK_PhucKhao_SinhVien FOREIGN KEY (SinhVienID)
            REFERENCES dbo.SinhVien(SinhVienID),
        CONSTRAINT FK_PhucKhao_NguoiXuLy FOREIGN KEY (NguoiXuLyID)
            REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
    PRINT N'-> Đã tạo bảng dbo.PhucKhao';
END
GO

/* 12. Kết quả học tập tổng hợp theo kỳ (GPA & CPA) */
IF OBJECT_ID(N'dbo.KetQuaHocTap', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.KetQuaHocTap (
        KetQuaHocTapID INT IDENTITY(1,1) PRIMARY KEY,
        SinhVienID INT NOT NULL,
        HocKyID INT NOT NULL,
        GPAHocKy DECIMAL(3,2) NULL,
        GPATichLuy DECIMAL(3,2) NULL,
        SoTinChiDangKy INT NOT NULL CONSTRAINT DF_KQHT_TCDK DEFAULT (0),
        SoTinChiDat INT NOT NULL CONSTRAINT DF_KQHT_TCDat DEFAULT (0),
        TongTinChiTichLuy INT NOT NULL CONSTRAINT DF_KQHT_TCTL DEFAULT (0),
        NgayCapNhat DATETIME2 NOT NULL CONSTRAINT DF_KetQua_NgayCapNhat DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_KetQua_SinhVien FOREIGN KEY (SinhVienID)
            REFERENCES dbo.SinhVien(SinhVienID),
        CONSTRAINT FK_KetQua_HocKy FOREIGN KEY (HocKyID)
            REFERENCES dbo.HocKy(HocKyID),
        CONSTRAINT UQ_KetQua_SinhVien_HocKy UNIQUE (SinhVienID, HocKyID),
        CONSTRAINT CK_KQHT_GPAHK CHECK (GPAHocKy IS NULL OR (GPAHocKy >= 0.00 AND GPAHocKy <= 4.00)),
        CONSTRAINT CK_KQHT_GPATL CHECK (GPATichLuy IS NULL OR (GPATichLuy >= 0.00 AND GPATichLuy <= 4.00))
    );
    PRINT N'-> Đã tạo bảng dbo.KetQuaHocTap';
END
GO

/* 13. Danh mục tiêu chí cảnh báo học tập */
IF OBJECT_ID(N'dbo.TieuChiCanhCao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TieuChiCanhCao (
        TieuChiID INT IDENTITY(1,1) PRIMARY KEY,
        MaTieuChi VARCHAR(30) NOT NULL UNIQUE,
        TenTieuChi NVARCHAR(100) NOT NULL,
        LoaiCanhCao NVARCHAR(50) NOT NULL,
        NguongGPA DECIMAL(3,2) NULL,
        NguongTinChiNo INT NULL,
        MoTa NVARCHAR(255) NULL
    );
    PRINT N'-> Đã tạo bảng dbo.TieuChiCanhCao';
END
GO

/* 14. Danh sách sinh viên bị cảnh báo học tập */
IF OBJECT_ID(N'dbo.CanhCaoHocTap', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.CanhCaoHocTap (
        CanhCaoID INT IDENTITY(1,1) PRIMARY KEY,
        SinhVienID INT NOT NULL,
        HocKyID INT NOT NULL,
        TieuChiID INT NULL,
        MucCanhCao NVARCHAR(30) NOT NULL,
        LyDo NVARCHAR(255) NULL,
        TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_CanhCao_TrangThai DEFAULT (N'Đang áp dụng'),
        NgayTao DATETIME2 NOT NULL CONSTRAINT DF_CanhCao_NgayTao DEFAULT (SYSDATETIME()),
        GhiChu NVARCHAR(255) NULL,
        CONSTRAINT FK_CanhCao_SinhVien FOREIGN KEY (SinhVienID)
            REFERENCES dbo.SinhVien(SinhVienID),
        CONSTRAINT FK_CanhCao_HocKy FOREIGN KEY (HocKyID)
            REFERENCES dbo.HocKy(HocKyID),
        CONSTRAINT FK_CanhCao_TieuChi FOREIGN KEY (TieuChiID)
            REFERENCES dbo.TieuChiCanhCao(TieuChiID)
    );
    PRINT N'-> Đã tạo bảng dbo.CanhCaoHocTap';
END
GO

/* 15. Quyết định xử lý cảnh báo học vụ chính thức */
IF OBJECT_ID(N'dbo.QuyetDinhCanhCao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.QuyetDinhCanhCao (
        QuyetDinhID INT IDENTITY(1,1) PRIMARY KEY,
        CanhCaoID INT NOT NULL UNIQUE,
        SoQuyetDinh VARCHAR(50) NOT NULL UNIQUE,
        NgayQuyetDinh DATE NOT NULL,
        NoiDung NVARCHAR(2000) NOT NULL,
        NguoiKyID INT NULL,
        TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_QDCC_TrangThai DEFAULT (N'Có hiệu lực'),
        TepDinhKem NVARCHAR(500) NULL,
        CONSTRAINT FK_QuyetDinh_CanhCao FOREIGN KEY (CanhCaoID)
            REFERENCES dbo.CanhCaoHocTap(CanhCaoID),
        CONSTRAINT FK_QuyetDinh_NguoiKy FOREIGN KEY (NguoiKyID)
            REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
    PRINT N'-> Đã tạo bảng dbo.QuyetDinhCanhCao';
END
GO

/* 16. Theo dõi sinh viên sau cảnh báo học tập */
IF OBJECT_ID(N'dbo.TheoDoiSauCanhCao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TheoDoiSauCanhCao (
        TheoDoiID INT IDENTITY(1,1) PRIMARY KEY,
        CanhCaoID INT NOT NULL,
        HocKyTheoDoiID INT NOT NULL,
        NhanXet NVARCHAR(500) NOT NULL,
        KetQua NVARCHAR(50) NOT NULL,
        NguoiTheoDoiID INT NULL,
        NgayGhiNhan DATETIME2 NOT NULL CONSTRAINT DF_TheoDoi_Ngay DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_TheoDoi_CanhCao FOREIGN KEY (CanhCaoID)
            REFERENCES dbo.CanhCaoHocTap(CanhCaoID),
        CONSTRAINT FK_TheoDoi_HocKy FOREIGN KEY (HocKyTheoDoiID)
            REFERENCES dbo.HocKy(HocKyID),
        CONSTRAINT FK_TheoDoi_NguoiDung FOREIGN KEY (NguoiTheoDoiID)
            REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
    PRINT N'-> Đã tạo bảng dbo.TheoDoiSauCanhCao';
END
GO

/* 17. Thông báo hệ thống và cá nhân */
IF OBJECT_ID(N'dbo.ThongBao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ThongBao (
        ThongBaoID INT IDENTITY(1,1) PRIMARY KEY,
        TaiKhoanID INT NULL,
        TieuDe NVARCHAR(200) NOT NULL,
        NoiDung NVARCHAR(MAX) NOT NULL,
        LoaiThongBao NVARCHAR(50) NOT NULL CONSTRAINT DF_ThongBao_Loai DEFAULT (N'Hệ thống'),
        NgayTao DATETIME2 NOT NULL CONSTRAINT DF_ThongBao_NgayTao DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_ThongBao_TaiKhoan FOREIGN KEY (TaiKhoanID)
            REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
    PRINT N'-> Đã tạo bảng dbo.ThongBao';
END
GO

PRINT N'========================================================================';
PRINT N'TẠO CÁC CHỈ MỤC TỐI ƯU HÓA TRUY VẤN (INDEXES)...';
PRINT N'========================================================================';
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_SinhVien_LopID' AND object_id = OBJECT_ID(N'dbo.SinhVien'))
BEGIN
    CREATE INDEX IX_SinhVien_LopID ON dbo.SinhVien(LopID);
    PRINT N'-> Đã tạo chỉ mục IX_SinhVien_LopID';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LopHocPhan_HocKyID' AND object_id = OBJECT_ID(N'dbo.LopHocPhan'))
BEGIN
    CREATE INDEX IX_LopHocPhan_HocKyID ON dbo.LopHocPhan(HocKyID);
    PRINT N'-> Đã tạo chỉ mục IX_LopHocPhan_HocKyID';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_DangKy_SinhVienID' AND object_id = OBJECT_ID(N'dbo.DangKyHocPhan'))
BEGIN
    CREATE INDEX IX_DangKy_SinhVienID ON dbo.DangKyHocPhan(SinhVienID);
    PRINT N'-> Đã tạo chỉ mục IX_DangKy_SinhVienID';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_CanhCao_SinhVien_HocKy' AND object_id = OBJECT_ID(N'dbo.CanhCaoHocTap'))
BEGIN
    CREATE INDEX IX_CanhCao_SinhVien_HocKy ON dbo.CanhCaoHocTap(SinhVienID, HocKyID);
    PRINT N'-> Đã tạo chỉ mục IX_CanhCao_SinhVien_HocKy';
END
GO

PRINT N'========================================================================';
PRINT N'HOÀN TẤT CẤU TRÚC BẢNG CHO DATABASE duanjavanangcao!';
PRINT N'========================================================================';
GO

