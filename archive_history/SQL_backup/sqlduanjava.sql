/*
    DU AN: QUAN LY DIEM SINH VIEN
    He quan tri CSDL: Microsoft SQL Server
    Cach su dung:
      1. Mo SQL Server Management Studio (SSMS).
      2. Chon New Query.
      3. Dan toan bo noi dung file nay vao va nhan Execute.
    Luu y: Script tao database moi. Neu database duanjavanangcao da ton tai,
    hay doi ten database ben duoi hoac sao luu du lieu truoc khi chay.
*/

IF DB_ID(N'duanjavanangcao') IS NULL
BEGIN
    CREATE DATABASE duanjavanangcao;
END
GO

USE duanjavanangcao;
GO

/* 1. Tai khoan dang nhap */
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
END
GO

/* 2. Lop hanh chinh */
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
END
GO

/* 3. Giang vien */
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
END
GO

/* 4. Sinh vien */
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
        TaiKhoanID INT NULL UNIQUE,
        CONSTRAINT FK_SinhVien_Lop FOREIGN KEY (LopID) REFERENCES dbo.Lop(LopID),
        CONSTRAINT FK_SinhVien_TaiKhoan FOREIGN KEY (TaiKhoanID) REFERENCES dbo.TaiKhoan(TaiKhoanID),
        CONSTRAINT CK_SinhVien_GioiTinh CHECK (GioiTinh IS NULL OR GioiTinh IN (N'Nam', N'Nữ', N'Khác'))
    );
END
GO

/* 5. Mon hoc */
IF OBJECT_ID(N'dbo.MonHoc', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.MonHoc (
        MonHocID INT IDENTITY(1,1) PRIMARY KEY,
        MaMonHoc VARCHAR(20) NOT NULL UNIQUE,
        TenMonHoc NVARCHAR(150) NOT NULL,
        SoTinChi INT NOT NULL,
        MoTa NVARCHAR(500) NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_MonHoc_TrangThai DEFAULT (1),
        CONSTRAINT CK_MonHoc_SoTinChi CHECK (SoTinChi > 0)
    );
END
GO

/* 6. Hoc ky */
IF OBJECT_ID(N'dbo.HocKy', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HocKy (
        HocKyID INT IDENTITY(1,1) PRIMARY KEY,
        MaHocKy VARCHAR(20) NOT NULL UNIQUE,
        TenHocKy NVARCHAR(100) NOT NULL,
        NamHoc VARCHAR(20) NOT NULL,
        NgayBatDau DATE NULL,
        NgayKetThuc DATE NULL,
        TrangThai NVARCHAR(20) NOT NULL CONSTRAINT DF_HocKy_TrangThai DEFAULT (N'Chưa bắt đầu'),
        CONSTRAINT CK_HocKy_Ngay CHECK (NgayBatDau IS NULL OR NgayKetThuc IS NULL OR NgayBatDau <= NgayKetThuc)
    );
END
GO

/* 7. Lop hoc phan: mot mon hoc duoc mo trong mot hoc ky */
IF OBJECT_ID(N'dbo.LopHocPhan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LopHocPhan (
        LopHocPhanID INT IDENTITY(1,1) PRIMARY KEY,
        MaLopHocPhan VARCHAR(30) NOT NULL UNIQUE,
        MonHocID INT NOT NULL,
        HocKyID INT NOT NULL,
        GiangVienID INT NULL,
        Nhom VARCHAR(20) NULL,
        SiSoToiDa INT NULL,
        LichHoc NVARCHAR(255) NULL,
        PhongHoc NVARCHAR(50) NULL,
        TrangThai NVARCHAR(20) NOT NULL CONSTRAINT DF_LopHocPhan_TrangThai DEFAULT (N'Đang mở'),
        CONSTRAINT FK_LopHocPhan_MonHoc FOREIGN KEY (MonHocID) REFERENCES dbo.MonHoc(MonHocID),
        CONSTRAINT FK_LopHocPhan_HocKy FOREIGN KEY (HocKyID) REFERENCES dbo.HocKy(HocKyID),
        CONSTRAINT FK_LopHocPhan_GiangVien FOREIGN KEY (GiangVienID) REFERENCES dbo.GiangVien(GiangVienID),
        CONSTRAINT CK_LopHocPhan_SiSo CHECK (SiSoToiDa IS NULL OR SiSoToiDa > 0)
    );
END
GO

/* 8. Dang ky hoc phan */
IF OBJECT_ID(N'dbo.DangKyHocPhan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.DangKyHocPhan (
        DangKyID INT IDENTITY(1,1) PRIMARY KEY,
        SinhVienID INT NOT NULL,
        LopHocPhanID INT NOT NULL,
        NgayDangKy DATETIME2 NOT NULL CONSTRAINT DF_DangKy_Ngay DEFAULT (SYSDATETIME()),
        TrangThai NVARCHAR(20) NOT NULL CONSTRAINT DF_DangKy_TrangThai DEFAULT (N'Đang học'),
        CONSTRAINT FK_DangKy_SinhVien FOREIGN KEY (SinhVienID) REFERENCES dbo.SinhVien(SinhVienID),
        CONSTRAINT FK_DangKy_LopHocPhan FOREIGN KEY (LopHocPhanID) REFERENCES dbo.LopHocPhan(LopHocPhanID),
        CONSTRAINT UQ_DangKy_SinhVien_LopHocPhan UNIQUE (SinhVienID, LopHocPhanID)
    );
END
GO

/* 9. Bang diem: mot ban ghi diem cho mot dang ky hoc phan */
IF OBJECT_ID(N'dbo.BangDiem', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BangDiem (
        BangDiemID INT IDENTITY(1,1) PRIMARY KEY,
        DangKyID INT NOT NULL UNIQUE,
        DiemCC DECIMAL(4,2) NULL,
        DiemGK DECIMAL(4,2) NULL,
        DiemCK DECIMAL(4,2) NULL,
        DiemTongKet DECIMAL(3,1) NULL,
        DiemChu VARCHAR(2) NULL,
        DiemHe4 DECIMAL(2,1) NULL,
        NgayCapNhat DATETIME2 NOT NULL CONSTRAINT DF_BangDiem_NgayCapNhat DEFAULT (SYSDATETIME()),
        NguoiCapNhatID INT NULL,
        GhiChu NVARCHAR(500) NULL,
        CONSTRAINT FK_BangDiem_DangKy FOREIGN KEY (DangKyID) REFERENCES dbo.DangKyHocPhan(DangKyID),
        CONSTRAINT FK_BangDiem_NguoiCapNhat FOREIGN KEY (NguoiCapNhatID) REFERENCES dbo.TaiKhoan(TaiKhoanID),
        CONSTRAINT CK_BangDiem_CC CHECK (DiemCC IS NULL OR DiemCC BETWEEN 0 AND 10),
        CONSTRAINT CK_BangDiem_GK CHECK (DiemGK IS NULL OR DiemGK BETWEEN 0 AND 10),
        CONSTRAINT CK_BangDiem_CK CHECK (DiemCK IS NULL OR DiemCK BETWEEN 0 AND 10),
        CONSTRAINT CK_BangDiem_TongKet CHECK (DiemTongKet IS NULL OR DiemTongKet BETWEEN 0 AND 10),
        CONSTRAINT CK_BangDiem_DiemChu CHECK (DiemChu IS NULL OR DiemChu IN ('A','B+','B','C+','C','D+','D','F')),
        CONSTRAINT CK_BangDiem_DiemHe4 CHECK (DiemHe4 IS NULL OR DiemHe4 BETWEEN 0 AND 4)
    );
END
GO

/* 10. Lich su thay doi diem */
IF OBJECT_ID(N'dbo.LichSuDiem', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LichSuDiem (
        LichSuDiemID INT IDENTITY(1,1) PRIMARY KEY,
        BangDiemID INT NOT NULL,
        DiemCCCu DECIMAL(4,2) NULL,
        DiemGKCu DECIMAL(4,2) NULL,
        DiemCKCu DECIMAL(4,2) NULL,
        DiemTongKetCu DECIMAL(3,1) NULL,
        DiemChuCu VARCHAR(2) NULL,
        DiemHe4Cu DECIMAL(2,1) NULL,
        DiemCCMoi DECIMAL(4,2) NULL,
        DiemGKMoi DECIMAL(4,2) NULL,
        DiemCKMoi DECIMAL(4,2) NULL,
        DiemTongKetMoi DECIMAL(3,1) NULL,
        DiemChuMoi VARCHAR(2) NULL,
        DiemHe4Moi DECIMAL(2,1) NULL,
        LyDo NVARCHAR(500) NOT NULL,
        NguoiThayDoiID INT NOT NULL,
        ThoiGianThayDoi DATETIME2 NOT NULL CONSTRAINT DF_LichSuDiem_ThoiGian DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_LichSuDiem_BangDiem FOREIGN KEY (BangDiemID) REFERENCES dbo.BangDiem(BangDiemID),
        CONSTRAINT FK_LichSuDiem_TaiKhoan FOREIGN KEY (NguoiThayDoiID) REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
END
GO

/* 11. Phuc khao diem */
IF OBJECT_ID(N'dbo.PhucKhao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PhucKhao (
        PhucKhaoID INT IDENTITY(1,1) PRIMARY KEY,
        BangDiemID INT NOT NULL,
        SinhVienID INT NOT NULL,
        NgayGui DATETIME2 NOT NULL CONSTRAINT DF_PhucKhao_NgayGui DEFAULT (SYSDATETIME()),
        LyDo NVARCHAR(1000) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_PhucKhao_TrangThai DEFAULT (N'Chờ xử lý'),
        KetQuaXuLy NVARCHAR(1000) NULL,
        NguoiXuLyID INT NULL,
        NgayXuLy DATETIME2 NULL,
        CONSTRAINT FK_PhucKhao_BangDiem FOREIGN KEY (BangDiemID) REFERENCES dbo.BangDiem(BangDiemID),
        CONSTRAINT FK_PhucKhao_SinhVien FOREIGN KEY (SinhVienID) REFERENCES dbo.SinhVien(SinhVienID),
        CONSTRAINT FK_PhucKhao_NguoiXuLy FOREIGN KEY (NguoiXuLyID) REFERENCES dbo.TaiKhoan(TaiKhoanID),
        CONSTRAINT CK_PhucKhao_TrangThai CHECK (TrangThai IN (N'Chờ xử lý', N'Đang xử lý', N'Đã chấp nhận', N'Không chấp nhận'))
    );
END
GO

/* 12. Ket qua hoc tap cua sinh vien theo hoc ky */
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
        NgayCapNhat DATETIME2 NOT NULL CONSTRAINT DF_KQHT_Ngay DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_KQHT_SinhVien FOREIGN KEY (SinhVienID) REFERENCES dbo.SinhVien(SinhVienID),
        CONSTRAINT FK_KQHT_HocKy FOREIGN KEY (HocKyID) REFERENCES dbo.HocKy(HocKyID),
        CONSTRAINT UQ_KQHT_SinhVien_HocKy UNIQUE (SinhVienID, HocKyID),
        CONSTRAINT CK_KQHT_GPAHK CHECK (GPAHocKy IS NULL OR GPAHocKy BETWEEN 0 AND 4),
        CONSTRAINT CK_KQHT_GPATL CHECK (GPATichLuy IS NULL OR GPATichLuy BETWEEN 0 AND 4),
        CONSTRAINT CK_KQHT_TinChi CHECK (SoTinChiDangKy >= 0 AND SoTinChiDat >= 0 AND TongTinChiTichLuy >= 0)
    );
END
GO

/* 13. Tieu chi canh cao hoc tap co the cau hinh */
IF OBJECT_ID(N'dbo.TieuChiCanhCao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TieuChiCanhCao (
        TieuChiID INT IDENTITY(1,1) PRIMARY KEY,
        MaTieuChi VARCHAR(30) NOT NULL UNIQUE,
        TenTieuChi NVARCHAR(150) NOT NULL,
        LoaiCanhCao NVARCHAR(50) NOT NULL,
        MoTa NVARCHAR(1000) NULL,
        GPAHocKyToiDa DECIMAL(3,2) NULL,
        GPATichLuyToiDa DECIMAL(3,2) NULL,
        SoTinChiNoToiThieu INT NULL,
        SoTinChiDatToiDa INT NULL,
        NgayApDung DATE NULL,
        NgayKetThuc DATE NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_TieuChi_TrangThai DEFAULT (1),
        CONSTRAINT CK_TieuChi_GPAHK CHECK (GPAHocKyToiDa IS NULL OR GPAHocKyToiDa BETWEEN 0 AND 4),
        CONSTRAINT CK_TieuChi_GPATL CHECK (GPATichLuyToiDa IS NULL OR GPATichLuyToiDa BETWEEN 0 AND 4),
        CONSTRAINT CK_TieuChi_TinChi CHECK ((SoTinChiNoToiThieu IS NULL OR SoTinChiNoToiThieu >= 0)
            AND (SoTinChiDatToiDa IS NULL OR SoTinChiDatToiDa >= 0)),
        CONSTRAINT CK_TieuChi_Ngay CHECK (NgayApDung IS NULL OR NgayKetThuc IS NULL OR NgayApDung <= NgayKetThuc)
    );
END
GO

/* 14. Ket qua xet canh cao cua sinh vien trong hoc ky */
IF OBJECT_ID(N'dbo.CanhCaoHocTap', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.CanhCaoHocTap (
        CanhCaoID INT IDENTITY(1,1) PRIMARY KEY,
        SinhVienID INT NOT NULL,
        HocKyID INT NOT NULL,
        TieuChiID INT NOT NULL,
        NgayXet DATETIME2 NOT NULL CONSTRAINT DF_CanhCao_NgayXet DEFAULT (SYSDATETIME()),
        LyDo NVARCHAR(1000) NOT NULL,
        MucCanhCao NVARCHAR(50) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_CanhCao_TrangThai DEFAULT (N'Đã ghi nhận'),
        NguoiXetID INT NULL,
        GhiChu NVARCHAR(1000) NULL,
        CONSTRAINT FK_CanhCao_SinhVien FOREIGN KEY (SinhVienID) REFERENCES dbo.SinhVien(SinhVienID),
        CONSTRAINT FK_CanhCao_HocKy FOREIGN KEY (HocKyID) REFERENCES dbo.HocKy(HocKyID),
        CONSTRAINT FK_CanhCao_TieuChi FOREIGN KEY (TieuChiID) REFERENCES dbo.TieuChiCanhCao(TieuChiID),
        CONSTRAINT FK_CanhCao_NguoiXet FOREIGN KEY (NguoiXetID) REFERENCES dbo.TaiKhoan(TaiKhoanID),
        CONSTRAINT UQ_CanhCao_SV_HK_TieuChi UNIQUE (SinhVienID, HocKyID, TieuChiID)
    );
END
GO

/* 15. Quyet dinh chinh thuc ve canh cao */
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
        CONSTRAINT FK_QDCC_CanhCao FOREIGN KEY (CanhCaoID) REFERENCES dbo.CanhCaoHocTap(CanhCaoID),
        CONSTRAINT FK_QDCC_NguoiKy FOREIGN KEY (NguoiKyID) REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
END
GO

/* 16. Theo doi ket qua sau canh cao */
IF OBJECT_ID(N'dbo.TheoDoiSauCanhCao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TheoDoiSauCanhCao (
        TheoDoiID INT IDENTITY(1,1) PRIMARY KEY,
        CanhCaoID INT NOT NULL,
        HocKyTheoDoiID INT NOT NULL,
        NgayTheoDoi DATE NOT NULL CONSTRAINT DF_TheoDoi_Ngay DEFAULT (CONVERT(DATE, SYSDATETIME())),
        KetQuaTheoDoi NVARCHAR(1000) NULL,
        GPAHocKy DECIMAL(3,2) NULL,
        GPATichLuy DECIMAL(3,2) NULL,
        SoTinChiDat INT NULL,
        TinhTrang NVARCHAR(50) NOT NULL CONSTRAINT DF_TheoDoi_TinhTrang DEFAULT (N'Đang theo dõi'),
        NguoiTheoDoiID INT NULL,
        GhiChu NVARCHAR(1000) NULL,
        CONSTRAINT FK_TheoDoi_CanhCao FOREIGN KEY (CanhCaoID) REFERENCES dbo.CanhCaoHocTap(CanhCaoID),
        CONSTRAINT FK_TheoDoi_HocKy FOREIGN KEY (HocKyTheoDoiID) REFERENCES dbo.HocKy(HocKyID),
        CONSTRAINT FK_TheoDoi_NguoiTheoDoi FOREIGN KEY (NguoiTheoDoiID) REFERENCES dbo.TaiKhoan(TaiKhoanID),
        CONSTRAINT UQ_TheoDoi_CanhCao_HocKy UNIQUE (CanhCaoID, HocKyTheoDoiID),
        CONSTRAINT CK_TheoDoi_GPAHK CHECK (GPAHocKy IS NULL OR GPAHocKy BETWEEN 0 AND 4),
        CONSTRAINT CK_TheoDoi_GPATL CHECK (GPATichLuy IS NULL OR GPATichLuy BETWEEN 0 AND 4),
        CONSTRAINT CK_TheoDoi_TinChi CHECK (SoTinChiDat IS NULL OR SoTinChiDat >= 0)
    );
END
GO

/* 17. Thong bao trong he thong */
IF OBJECT_ID(N'dbo.ThongBao', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ThongBao (
        ThongBaoID INT IDENTITY(1,1) PRIMARY KEY,
        TaiKhoanID INT NOT NULL,
        TieuDe NVARCHAR(200) NOT NULL,
        NoiDung NVARCHAR(2000) NOT NULL,
        LoaiThongBao NVARCHAR(50) NULL,
        ThoiGianTao DATETIME2 NOT NULL CONSTRAINT DF_ThongBao_ThoiGian DEFAULT (SYSDATETIME()),
        DaDoc BIT NOT NULL CONSTRAINT DF_ThongBao_DaDoc DEFAULT (0),
        ThoiGianDoc DATETIME2 NULL,
        CONSTRAINT FK_ThongBao_TaiKhoan FOREIGN KEY (TaiKhoanID) REFERENCES dbo.TaiKhoan(TaiKhoanID)
    );
END
GO

/* Index ho tro truy van thuong dung */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_SinhVien_LopID' AND object_id = OBJECT_ID(N'dbo.SinhVien'))
    CREATE INDEX IX_SinhVien_LopID ON dbo.SinhVien(LopID);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LopHocPhan_HocKyID' AND object_id = OBJECT_ID(N'dbo.LopHocPhan'))
    CREATE INDEX IX_LopHocPhan_HocKyID ON dbo.LopHocPhan(HocKyID);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_DangKy_SinhVienID' AND object_id = OBJECT_ID(N'dbo.DangKyHocPhan'))
    CREATE INDEX IX_DangKy_SinhVienID ON dbo.DangKyHocPhan(SinhVienID);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_CanhCao_SinhVien_HocKy' AND object_id = OBJECT_ID(N'dbo.CanhCaoHocTap'))
    CREATE INDEX IX_CanhCao_SinhVien_HocKy ON dbo.CanhCaoHocTap(SinhVienID, HocKyID);
GO

PRINT N'Hoan tat tao database va 17 bang (neu chua ton tai).';
GO



/*
    DU LIEU MAU CHO DU AN QUAN LY DIEM SINH VIEN
    Database: duanjavanangcao
    Dung cho: Java Swing + JDBC + SQL Server

    Luu y:
    - Tat ca thong tin sinh vien/giang vien duoi day la du lieu gia lap.
    - Chay sau khi da chay file tao cau truc database.
    - Script co the chay lai: cac ban ghi mau co ma dinh danh se duoc bo qua neu da ton tai.
    - MatKhauHash la chuoi placeholder phuc vu du lieu mau, KHONG phai hash mat khau an toan.
      Khi lam ung dung that, can bam hash mat khau bang BCrypt/Argon2 trong Java.
*/

USE duanjavanangcao;
GO

/* 1. LOP HANH CHINH */
IF NOT EXISTS (SELECT 1 FROM dbo.Lop WHERE MaLop = 'HTTT_K20A')
    INSERT INTO dbo.Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa)
    VALUES ('HTTT_K20A', N'Hệ thống thông tin K20A', 'K20', N'Hệ thống thông tin', N'Công nghệ thông tin');

IF NOT EXISTS (SELECT 1 FROM dbo.Lop WHERE MaLop = 'HTTT_K20B')
    INSERT INTO dbo.Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa)
    VALUES ('HTTT_K20B', N'Hệ thống thông tin K20B', 'K20', N'Hệ thống thông tin', N'Công nghệ thông tin');

IF NOT EXISTS (SELECT 1 FROM dbo.Lop WHERE MaLop = 'CNTT_K20A')
    INSERT INTO dbo.Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa)
    VALUES ('CNTT_K20A', N'Công nghệ thông tin K20A', 'K20', N'Công nghệ thông tin', N'Công nghệ thông tin');
GO

/* 2. TAI KHOAN MAU */
IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE TenDangNhap = 'admin')
    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, VaiTro)
    VALUES ('admin', 'DEMO_ONLY_NOT_A_REAL_HASH', N'Quản trị viên', 'admin@demo.local', 'ADMIN');

IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE TenDangNhap = 'gv001')
    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, VaiTro)
    VALUES ('gv001', 'DEMO_ONLY_NOT_A_REAL_HASH', N'Nguyễn Minh Anh', 'gv001@demo.local', 'GIANGVIEN');

IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE TenDangNhap = 'gv002')
    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, VaiTro)
    VALUES ('gv002', 'DEMO_ONLY_NOT_A_REAL_HASH', N'Trần Quốc Bảo', 'gv002@demo.local', 'GIANGVIEN');

IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv001')
    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, VaiTro)
    VALUES ('sv001', 'DEMO_ONLY_NOT_A_REAL_HASH', N'Phạm Thu Hà', 'sv001@demo.local', 'SINHVIEN');

IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv002')
    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, VaiTro)
    VALUES ('sv002', 'DEMO_ONLY_NOT_A_REAL_HASH', N'Nguyễn Hoàng Long', 'sv002@demo.local', 'SINHVIEN');

IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv003')
    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, VaiTro)
    VALUES ('sv003', 'DEMO_ONLY_NOT_A_REAL_HASH', N'Trần Ngọc Mai', 'sv003@demo.local', 'SINHVIEN');

IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv004')
    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, VaiTro)
    VALUES ('sv004', 'DEMO_ONLY_NOT_A_REAL_HASH', N'Lê Đức Anh', 'sv004@demo.local', 'SINHVIEN');

IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv005')
    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, VaiTro)
    VALUES ('sv005', 'DEMO_ONLY_NOT_A_REAL_HASH', N'Vũ Khánh Linh', 'sv005@demo.local', 'SINHVIEN');
GO

/* 3. GIANG VIEN */
IF NOT EXISTS (SELECT 1 FROM dbo.GiangVien WHERE MaGiangVien = 'GV001')
    INSERT INTO dbo.GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID)
    SELECT 'GV001', N'Nguyễn Minh Anh', 'gv001@demo.local', '0900000001',
           N'Công nghệ thông tin', TaiKhoanID
    FROM dbo.TaiKhoan WHERE TenDangNhap = 'gv001';

IF NOT EXISTS (SELECT 1 FROM dbo.GiangVien WHERE MaGiangVien = 'GV002')
    INSERT INTO dbo.GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID)
    SELECT 'GV002', N'Trần Quốc Bảo', 'gv002@demo.local', '0900000002',
           N'Công nghệ thông tin', TaiKhoanID
    FROM dbo.TaiKhoan WHERE TenDangNhap = 'gv002';
GO

/* 4. SINH VIEN */
IF NOT EXISTS (SELECT 1 FROM dbo.SinhVien WHERE MaSinhVien = 'SV001')
    INSERT INTO dbo.SinhVien
        (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV001', N'Phạm Thu Hà', '2005-04-12', N'Nữ', 'sv001@demo.local', '0910000001',
           N'Hà Nội', '2023-09-05', N'Đang học', LopID,
           (SELECT TaiKhoanID FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv001')
    FROM dbo.Lop WHERE MaLop = 'HTTT_K20A';

IF NOT EXISTS (SELECT 1 FROM dbo.SinhVien WHERE MaSinhVien = 'SV002')
    INSERT INTO dbo.SinhVien
        (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV002', N'Nguyễn Hoàng Long', '2005-08-20', N'Nam', 'sv002@demo.local', '0910000002',
           N'Hà Nội', '2023-09-05', N'Đang học', LopID,
           (SELECT TaiKhoanID FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv002')
    FROM dbo.Lop WHERE MaLop = 'HTTT_K20A';

IF NOT EXISTS (SELECT 1 FROM dbo.SinhVien WHERE MaSinhVien = 'SV003')
    INSERT INTO dbo.SinhVien
        (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV003', N'Trần Ngọc Mai', '2005-11-03', N'Nữ', 'sv003@demo.local', '0910000003',
           N'Hà Nội', '2023-09-05', N'Đang học', LopID,
           (SELECT TaiKhoanID FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv003')
    FROM dbo.Lop WHERE MaLop = 'HTTT_K20A';

IF NOT EXISTS (SELECT 1 FROM dbo.SinhVien WHERE MaSinhVien = 'SV004')
    INSERT INTO dbo.SinhVien
        (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV004', N'Lê Đức Anh', '2005-02-18', N'Nam', 'sv004@demo.local', '0910000004',
           N'Hà Nội', '2023-09-05', N'Đang học', LopID,
           (SELECT TaiKhoanID FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv004')
    FROM dbo.Lop WHERE MaLop = 'HTTT_K20A';

IF NOT EXISTS (SELECT 1 FROM dbo.SinhVien WHERE MaSinhVien = 'SV005')
    INSERT INTO dbo.SinhVien
        (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV005', N'Vũ Khánh Linh', '2005-06-27', N'Nữ', 'sv005@demo.local', '0910000005',
           N'Hà Nội', '2023-09-05', N'Đang học', LopID,
           (SELECT TaiKhoanID FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv005')
    FROM dbo.Lop WHERE MaLop = 'HTTT_K20A';
GO

/* 5. MON HOC */
IF NOT EXISTS (SELECT 1 FROM dbo.MonHoc WHERE MaMonHoc = 'JAVA101')
    INSERT INTO dbo.MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa)
    VALUES ('JAVA101', N'Lập trình Java', 3, N'Môn học lập trình Java cơ bản');

IF NOT EXISTS (SELECT 1 FROM dbo.MonHoc WHERE MaMonHoc = 'DB101')
    INSERT INTO dbo.MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa)
    VALUES ('DB101', N'Cơ sở dữ liệu', 3, N'Mô hình dữ liệu và SQL Server');

IF NOT EXISTS (SELECT 1 FROM dbo.MonHoc WHERE MaMonHoc = 'NET101')
    INSERT INTO dbo.MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa)
    VALUES ('NET101', N'Mạng máy tính', 3, N'Kiến thức mạng máy tính');

IF NOT EXISTS (SELECT 1 FROM dbo.MonHoc WHERE MaMonHoc = 'SE101')
    INSERT INTO dbo.MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa)
    VALUES ('SE101', N'Công nghệ phần mềm', 3, N'Phân tích và thiết kế phần mềm');

IF NOT EXISTS (SELECT 1 FROM dbo.MonHoc WHERE MaMonHoc = 'IS101')
    INSERT INTO dbo.MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa)
    VALUES ('IS101', N'Hệ thống thông tin quản lý', 2, N'Tổng quan hệ thống thông tin');
GO

/* 6. HOC KY */
IF NOT EXISTS (SELECT 1 FROM dbo.HocKy WHERE MaHocKy = '2026-1')
    INSERT INTO dbo.HocKy (MaHocKy, TenHocKy, NamHoc, NgayBatDau, NgayKetThuc, TrangThai)
    VALUES ('2026-1', N'Học kỳ 1 năm học 2026-2027', '2026-2027', '2026-09-01', '2027-01-15', N'Đang học');

IF NOT EXISTS (SELECT 1 FROM dbo.HocKy WHERE MaHocKy = '2026-2')
    INSERT INTO dbo.HocKy (MaHocKy, TenHocKy, NamHoc, NgayBatDau, NgayKetThuc, TrangThai)
    VALUES ('2026-2', N'Học kỳ 2 năm học 2026-2027', '2026-2027', '2027-02-01', '2027-06-15', N'Chưa bắt đầu');
GO

/* 7. LOP HOC PHAN */
IF NOT EXISTS (SELECT 1 FROM dbo.LopHocPhan WHERE MaLopHocPhan = 'JAVA101-2026-1-01')
    INSERT INTO dbo.LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc)
    SELECT 'JAVA101-2026-1-01', m.MonHocID, h.HocKyID, g.GiangVienID, '01', 40, N'Thứ 2, tiết 1-3', N'P.301'
    FROM dbo.MonHoc m CROSS JOIN dbo.HocKy h CROSS JOIN dbo.GiangVien g
    WHERE m.MaMonHoc = 'JAVA101' AND h.MaHocKy = '2026-1' AND g.MaGiangVien = 'GV001';

IF NOT EXISTS (SELECT 1 FROM dbo.LopHocPhan WHERE MaLopHocPhan = 'DB101-2026-1-01')
    INSERT INTO dbo.LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc)
    SELECT 'DB101-2026-1-01', m.MonHocID, h.HocKyID, g.GiangVienID, '01', 40, N'Thứ 3, tiết 4-6', N'P.302'
    FROM dbo.MonHoc m CROSS JOIN dbo.HocKy h CROSS JOIN dbo.GiangVien g
    WHERE m.MaMonHoc = 'DB101' AND h.MaHocKy = '2026-1' AND g.MaGiangVien = 'GV001';

IF NOT EXISTS (SELECT 1 FROM dbo.LopHocPhan WHERE MaLopHocPhan = 'NET101-2026-1-01')
    INSERT INTO dbo.LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc)
    SELECT 'NET101-2026-1-01', m.MonHocID, h.HocKyID, g.GiangVienID, '01', 40, N'Thứ 4, tiết 1-3', N'P.303'
    FROM dbo.MonHoc m CROSS JOIN dbo.HocKy h CROSS JOIN dbo.GiangVien g
    WHERE m.MaMonHoc = 'NET101' AND h.MaHocKy = '2026-1' AND g.MaGiangVien = 'GV002';

IF NOT EXISTS (SELECT 1 FROM dbo.LopHocPhan WHERE MaLopHocPhan = 'SE101-2026-1-01')
    INSERT INTO dbo.LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc)
    SELECT 'SE101-2026-1-01', m.MonHocID, h.HocKyID, g.GiangVienID, '01', 40, N'Thứ 5, tiết 4-6', N'P.304'
    FROM dbo.MonHoc m CROSS JOIN dbo.HocKy h CROSS JOIN dbo.GiangVien g
    WHERE m.MaMonHoc = 'SE101' AND h.MaHocKy = '2026-1' AND g.MaGiangVien = 'GV002';

IF NOT EXISTS (SELECT 1 FROM dbo.LopHocPhan WHERE MaLopHocPhan = 'IS101-2026-1-01')
    INSERT INTO dbo.LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc)
    SELECT 'IS101-2026-1-01', m.MonHocID, h.HocKyID, g.GiangVienID, '01', 40, N'Thứ 6, tiết 1-2', N'P.305'
    FROM dbo.MonHoc m CROSS JOIN dbo.HocKy h CROSS JOIN dbo.GiangVien g
    WHERE m.MaMonHoc = 'IS101' AND h.MaHocKy = '2026-1' AND g.MaGiangVien = 'GV001';
GO

/* 8. DANG KY HOC PHAN: moi sinh vien dang ky 5 lop hoc phan mau */
IF NOT EXISTS (
    SELECT 1 FROM dbo.DangKyHocPhan d
    JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'JAVA101-2026-1-01'
)
    INSERT INTO dbo.DangKyHocPhan (SinhVienID, LopHocPhanID)
    SELECT s.SinhVienID, l.LopHocPhanID FROM dbo.SinhVien s CROSS JOIN dbo.LopHocPhan l
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'JAVA101-2026-1-01';

IF NOT EXISTS (
    SELECT 1 FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'DB101-2026-1-01'
)
    INSERT INTO dbo.DangKyHocPhan (SinhVienID, LopHocPhanID)
    SELECT s.SinhVienID, l.LopHocPhanID FROM dbo.SinhVien s CROSS JOIN dbo.LopHocPhan l
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'DB101-2026-1-01';

IF NOT EXISTS (
    SELECT 1 FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'NET101-2026-1-01'
)
    INSERT INTO dbo.DangKyHocPhan (SinhVienID, LopHocPhanID)
    SELECT s.SinhVienID, l.LopHocPhanID FROM dbo.SinhVien s CROSS JOIN dbo.LopHocPhan l
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'NET101-2026-1-01';

IF NOT EXISTS (
    SELECT 1 FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'SE101-2026-1-01'
)
    INSERT INTO dbo.DangKyHocPhan (SinhVienID, LopHocPhanID)
    SELECT s.SinhVienID, l.LopHocPhanID FROM dbo.SinhVien s CROSS JOIN dbo.LopHocPhan l
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'SE101-2026-1-01';

IF NOT EXISTS (
    SELECT 1 FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'IS101-2026-1-01'
)
    INSERT INTO dbo.DangKyHocPhan (SinhVienID, LopHocPhanID)
    SELECT s.SinhVienID, l.LopHocPhanID FROM dbo.SinhVien s CROSS JOIN dbo.LopHocPhan l
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'IS101-2026-1-01';
GO

/* 9. BANG DIEM: diem mau da duoc lam tron 1 chu so truoc khi quy doi */
IF NOT EXISTS (
    SELECT 1 FROM dbo.BangDiem bd
    JOIN dbo.DangKyHocPhan d ON d.DangKyID = bd.DangKyID
    JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'JAVA101-2026-1-01'
)
    INSERT INTO dbo.BangDiem (DangKyID, DiemCC, DiemGK, DiemCK, DiemTongKet, DiemChu, DiemHe4, GhiChu)
    SELECT d.DangKyID, 8.00, 7.00, 9.00, 8.0, 'B+', 3.5, N'Dữ liệu mẫu'
    FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'JAVA101-2026-1-01';

IF NOT EXISTS (
    SELECT 1 FROM dbo.BangDiem bd JOIN dbo.DangKyHocPhan d ON d.DangKyID = bd.DangKyID
    JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'DB101-2026-1-01'
)
    INSERT INTO dbo.BangDiem (DangKyID, DiemCC, DiemGK, DiemCK, DiemTongKet, DiemChu, DiemHe4, GhiChu)
    SELECT d.DangKyID, 9.00, 8.00, 8.00, 8.2, 'B+', 3.5, N'Dữ liệu mẫu'
    FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'DB101-2026-1-01';

IF NOT EXISTS (
    SELECT 1 FROM dbo.BangDiem bd JOIN dbo.DangKyHocPhan d ON d.DangKyID = bd.DangKyID
    JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'NET101-2026-1-01'
)
    INSERT INTO dbo.BangDiem (DangKyID, DiemCC, DiemGK, DiemCK, DiemTongKet, DiemChu, DiemHe4, GhiChu)
    SELECT d.DangKyID, 6.00, 5.00, 4.00, 4.7, 'D', 1.0, N'Dữ liệu mẫu'
    FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'NET101-2026-1-01';

IF NOT EXISTS (
    SELECT 1 FROM dbo.BangDiem bd JOIN dbo.DangKyHocPhan d ON d.DangKyID = bd.DangKyID
    JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'SE101-2026-1-01'
)
    INSERT INTO dbo.BangDiem (DangKyID, DiemCC, DiemGK, DiemCK, DiemTongKet, DiemChu, DiemHe4, GhiChu)
    SELECT d.DangKyID, 7.00, 6.00, 6.00, 6.2, 'C+', 2.5, N'Dữ liệu mẫu'
    FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'SE101-2026-1-01';

IF NOT EXISTS (
    SELECT 1 FROM dbo.BangDiem bd JOIN dbo.DangKyHocPhan d ON d.DangKyID = bd.DangKyID
    JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'IS101-2026-1-01'
)
    INSERT INTO dbo.BangDiem (DangKyID, DiemCC, DiemGK, DiemCK, DiemTongKet, DiemChu, DiemHe4, GhiChu)
    SELECT d.DangKyID, 8.00, 8.00, 8.00, 8.0, 'B+', 3.5, N'Dữ liệu mẫu'
    FROM dbo.DangKyHocPhan d JOIN dbo.SinhVien s ON s.SinhVienID = d.SinhVienID
    JOIN dbo.LopHocPhan l ON l.LopHocPhanID = d.LopHocPhanID
    WHERE s.MaSinhVien = 'SV001' AND l.MaLopHocPhan = 'IS101-2026-1-01';
GO

/* 10. TIEU CHI CANH CAO: vi du de demo, can doi chieu quy che truong */
IF NOT EXISTS (SELECT 1 FROM dbo.TieuChiCanhCao WHERE MaTieuChi = 'GPA_HK_THAP')
    INSERT INTO dbo.TieuChiCanhCao
        (MaTieuChi, TenTieuChi, LoaiCanhCao, MoTa, GPAHocKyToiDa, NgayApDung, TrangThai)
    VALUES
        ('GPA_HK_THAP', N'GPA học kỳ thấp', N'Cảnh cáo học tập',
         N'Tiêu chí minh họa cho đồ án; cần thay bằng ngưỡng chính thức của trường.',
         1.00, '2026-09-01', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.TieuChiCanhCao WHERE MaTieuChi = 'TIN_CHI_NO')
    INSERT INTO dbo.TieuChiCanhCao
        (MaTieuChi, TenTieuChi, LoaiCanhCao, MoTa, SoTinChiNoToiThieu, NgayApDung, TrangThai)
    VALUES
        ('TIN_CHI_NO', N'Nợ tín chỉ', N'Cảnh báo theo tín chỉ',
         N'Tiêu chí minh họa cho đồ án; cần thay bằng ngưỡng chính thức của trường.',
         5, '2026-09-01', 1);
GO

/* 11. KET QUA HOC TAP: du lieu minh hoa cho SV001 */
IF NOT EXISTS (
    SELECT 1 FROM dbo.KetQuaHocTap k
    JOIN dbo.SinhVien s ON s.SinhVienID = k.SinhVienID
    JOIN dbo.HocKy h ON h.HocKyID = k.HocKyID
    WHERE s.MaSinhVien = 'SV001' AND h.MaHocKy = '2026-1'
)
    INSERT INTO dbo.KetQuaHocTap
        (SinhVienID, HocKyID, GPAHocKy, GPATichLuy, SoTinChiDangKy, SoTinChiDat, TongTinChiTichLuy)
    SELECT s.SinhVienID, h.HocKyID, 2.75, 2.75, 14, 14, 14
    FROM dbo.SinhVien s CROSS JOIN dbo.HocKy h
    WHERE s.MaSinhVien = 'SV001' AND h.MaHocKy = '2026-1';
GO

/* 12. THONG BAO MAU */
IF NOT EXISTS (
    SELECT 1 FROM dbo.ThongBao t
    JOIN dbo.TaiKhoan a ON a.TaiKhoanID = t.TaiKhoanID
    WHERE a.TenDangNhap = 'sv001' AND t.TieuDe = N'Chào mừng đến hệ thống'
)
    INSERT INTO dbo.ThongBao (TaiKhoanID, TieuDe, NoiDung, LoaiThongBao)
    SELECT TaiKhoanID, N'Chào mừng đến hệ thống',
           N'Đây là thông báo mẫu của phần mềm quản lý điểm sinh viên.',
           N'Hệ thống'
    FROM dbo.TaiKhoan WHERE TenDangNhap = 'sv001';
GO

/* 13. KIEM TRA SO LUONG BAN GHI */
SELECT N'TaiKhoan' AS TenBang, COUNT(*) AS SoBanGhi FROM dbo.TaiKhoan
UNION ALL SELECT N'Lop', COUNT(*) FROM dbo.Lop
UNION ALL SELECT N'GiangVien', COUNT(*) FROM dbo.GiangVien
UNION ALL SELECT N'SinhVien', COUNT(*) FROM dbo.SinhVien
UNION ALL SELECT N'MonHoc', COUNT(*) FROM dbo.MonHoc
UNION ALL SELECT N'HocKy', COUNT(*) FROM dbo.HocKy
UNION ALL SELECT N'LopHocPhan', COUNT(*) FROM dbo.LopHocPhan
UNION ALL SELECT N'DangKyHocPhan', COUNT(*) FROM dbo.DangKyHocPhan
UNION ALL SELECT N'BangDiem', COUNT(*) FROM dbo.BangDiem
UNION ALL SELECT N'LichSuDiem', COUNT(*) FROM dbo.LichSuDiem
UNION ALL SELECT N'PhucKhao', COUNT(*) FROM dbo.PhucKhao
UNION ALL SELECT N'KetQuaHocTap', COUNT(*) FROM dbo.KetQuaHocTap
UNION ALL SELECT N'TieuChiCanhCao', COUNT(*) FROM dbo.TieuChiCanhCao
UNION ALL SELECT N'CanhCaoHocTap', COUNT(*) FROM dbo.CanhCaoHocTap
UNION ALL SELECT N'QuyetDinhCanhCao', COUNT(*) FROM dbo.QuyetDinhCanhCao
UNION ALL SELECT N'TheoDoiSauCanhCao', COUNT(*) FROM dbo.TheoDoiSauCanhCao
UNION ALL SELECT N'ThongBao', COUNT(*) FROM dbo.ThongBao
ORDER BY TenBang;
GO


USE duanjavanangcao;
GO

SELECT * FROM dbo.TaiKhoan;
SELECT * FROM dbo.Lop;
SELECT * FROM dbo.GiangVien;
SELECT * FROM dbo.SinhVien;
SELECT * FROM dbo.MonHoc;
SELECT * FROM dbo.HocKy;
SELECT * FROM dbo.LopHocPhan;
SELECT * FROM dbo.DangKyHocPhan;
SELECT * FROM dbo.BangDiem;
SELECT * FROM dbo.LichSuDiem;
SELECT * FROM dbo.PhucKhao;
SELECT * FROM dbo.KetQuaHocTap;
SELECT * FROM dbo.TieuChiCanhCao;
SELECT * FROM dbo.CanhCaoHocTap;
SELECT * FROM dbo.QuyetDinhCanhCao;
SELECT * FROM dbo.TheoDoiSauCanhCao;
SELECT * FROM dbo.ThongBao;