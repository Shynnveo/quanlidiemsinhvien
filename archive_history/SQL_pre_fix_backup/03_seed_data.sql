-- ====================================================================================================
-- TỆP 03: NẠP DỮ LIỆU MẪU CHUẨN ĐẠI HỌC UTT (SEED DATA)
-- Database: duanjavanangcao
-- 
-- HƯỚNG DẪN QUAN TRỌNG:
--   - Script này nạp dữ liệu mẫu toàn diện cho 17 bảng dữ liệu.
--   - Cơ chế kiểm soát chạy lại an toàn: Từng bản ghi đều được kiểm tra sự tồn tại dựa trên
--     Khóa chính tự nhiên (MaSinhVien, MaGiangVien, MaLop, TenDangNhap, MaMonHoc...) trước khi INSERT.
--   - Không gây trùng lặp bản ghi, không vi phạm Unique Constraints khi chạy nhiều lần.
--   - Giữ nguyên 100% tiếng Việt có dấu chuẩn Unicode (NVARCHAR).
-- ====================================================================================================

USE duanjavanangcao;
GO
SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

PRINT N'========================================================================';
PRINT N'BẮT ĐẦU NẠP VÀ ĐỒNG BỘ BỘ DỮ LIỆU MẪU CHO 17 BẢNG...';
PRINT N'========================================================================';
GO

-- ----------------------------------------------------------------------------------------------------
-- BƯỚC 0: TỐI ƯU RÀNG BUỘC DUY NHẤT CHO CỘT SinhVien.TaiKhoanID
-- Chuyển sang Filtered Unique Index (WHERE TaiKhoanID IS NOT NULL) để cho phép nhiều sinh viên
-- chưa có tài khoản (TaiKhoanID = NULL) nhằm kiểm thử chức năng Đăng ký tài khoản sinh viên từ UI.
-- ----------------------------------------------------------------------------------------------------
DECLARE @ConstraintName NVARCHAR(200);
SELECT @ConstraintName = kc.name
FROM sys.key_constraints kc
JOIN sys.index_columns ic ON kc.parent_object_id = ic.object_id AND kc.unique_index_id = ic.index_id
JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
WHERE kc.parent_object_id = OBJECT_ID('SinhVien') AND c.name = 'TaiKhoanID' AND kc.type = 'UQ';

IF @ConstraintName IS NOT NULL
BEGIN
    PRINT N'-> Tối ưu ràng buộc TaiKhoanID trên bảng SinhVien: Xóa [' + @ConstraintName + N'] và tạo Filtered Index...';
    EXEC('ALTER TABLE SinhVien DROP CONSTRAINT [' + @ConstraintName + ']');
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID('SinhVien') AND name = 'UQ_SinhVien_TaiKhoanID')
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UQ_SinhVien_TaiKhoanID ON SinhVien(TaiKhoanID) WHERE TaiKhoanID IS NOT NULL;
    PRINT N'-> Đã tạo thành công Filtered Unique Index UQ_SinhVien_TaiKhoanID (cho phép nhiều SV chưa có tài khoản).';
END
GO

-- ----------------------------------------------------------------------------------------------------
-- 1. BẢNG Lop (5 Lớp sinh viên hành chính)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 1. Nạp và chuẩn hóa dữ liệu bảng Lop...';
IF NOT EXISTS (SELECT 1 FROM Lop WHERE MaLop = 'HTTT_K20A')
    INSERT INTO Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai)
    VALUES ('HTTT_K20A', N'Hệ thống thông tin K20A', 'K20', N'Hệ thống thông tin', N'Công nghệ thông tin', 1);

IF NOT EXISTS (SELECT 1 FROM Lop WHERE MaLop = 'HTTT_K20B')
    INSERT INTO Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai)
    VALUES ('HTTT_K20B', N'Hệ thống thông tin K20B', 'K20', N'Hệ thống thông tin', N'Công nghệ thông tin', 1);

IF NOT EXISTS (SELECT 1 FROM Lop WHERE MaLop = 'CNTT_K20A')
    INSERT INTO Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai)
    VALUES ('CNTT_K20A', N'Công nghệ thông tin K20A', 'K20', N'Công nghệ thông tin', N'Công nghệ thông tin', 1);

IF NOT EXISTS (SELECT 1 FROM Lop WHERE MaLop = 'CNTT_K20B')
    INSERT INTO Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai)
    VALUES ('CNTT_K20B', N'Công nghệ thông tin K20B', 'K20', N'Công nghệ thông tin', N'Công nghệ thông tin', 1);

IF NOT EXISTS (SELECT 1 FROM Lop WHERE MaLop = 'ATTT_K20A')
    INSERT INTO Lop (MaLop, TenLop, KhoaHoc, NganhHoc, Khoa, TrangThai)
    VALUES ('ATTT_K20A', N'An toàn thông tin K20A', 'K20', N'An toàn thông tin', N'Công nghệ thông tin', 1);

UPDATE Lop SET TenLop = N'Hệ thống thông tin K20A' WHERE MaLop = 'HTTT_K20A';
UPDATE Lop SET TenLop = N'Hệ thống thông tin K20B' WHERE MaLop = 'HTTT_K20B';
UPDATE Lop SET TenLop = N'Công nghệ thông tin K20A' WHERE MaLop = 'CNTT_K20A';
UPDATE Lop SET TenLop = N'Công nghệ thông tin K20B' WHERE MaLop = 'CNTT_K20B';
UPDATE Lop SET TenLop = N'An toàn thông tin K20A' WHERE MaLop = 'ATTT_K20A';
GO

-- ----------------------------------------------------------------------------------------------------
-- 2. BẢNG TaiKhoan (Tài khoản người dùng: Giảng viên và Sinh viên)
-- Mật khẩu: 123456 -> SHA-256: 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 2. Nạp dữ liệu bảng TaiKhoan...';
DECLARE @PassHash VARCHAR(255) = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92';

-- Tài khoản Quản trị viên và Giảng viên gốc
IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'admin')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('admin', @PassHash, N'Quản trị viên', 'admin@demo.local', '0900000000', 'ADMIN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'gv001')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('gv001', @PassHash, N'Nguyễn Minh Anh', 'gv001@demo.local', '0900000001', 'GIANGVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'gv002')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('gv002', @PassHash, N'Trần Quốc Bảo', 'gv002@demo.local', '0900000002', 'GIANGVIEN', 1, SYSDATETIME());

-- Tài khoản Sinh viên gốc (sv001 -> sv005)
IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv001')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv001', @PassHash, N'Phạm Thu Hà', 'sv001@demo.local', '0910000001', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv002')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv002', @PassHash, N'Nguyễn Hoàng Long', 'sv002@demo.local', '0910000002', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv003')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv003', @PassHash, N'Trần Ngọc Mai', 'sv003@demo.local', '0910000003', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv004')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv004', @PassHash, N'Lê Đức Anh', 'sv004@demo.local', '0910000004', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv005')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv005', @PassHash, N'Vũ Khánh Linh', 'sv005@demo.local', '0910000005', 'SINHVIEN', 1, SYSDATETIME());

-- Tài khoản Giảng viên bổ sung (gv003 -> gv006)
IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'gv003')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('gv003', @PassHash, N'Lê Thị Mai Hương', 'huongltm@utt.edu.vn', '0912345603', 'GIANGVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'gv004')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('gv004', @PassHash, N'Hoàng Văn Thắng', 'thanghv@utt.edu.vn', '0912345604', 'GIANGVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'gv005')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('gv005', @PassHash, N'Đặng Thị Bích Ngọc', 'ngocdtb@utt.edu.vn', '0912345605', 'GIANGVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'gv006')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('gv006', @PassHash, N'Vũ Đình Trọng', 'trongvd@utt.edu.vn', '0912345606', 'GIANGVIEN', 1, SYSDATETIME());

-- Tài khoản Sinh viên bổ sung (sv007 -> sv024)
IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv007')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv007', @PassHash, N'Đỗ Gia Huy', 'huydg@utt.edu.vn', '0987111007', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv008')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv008', @PassHash, N'Bùi Phương Thảo', 'thaobp@utt.edu.vn', '0987111008', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv009')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv009', @PassHash, N'Đinh Quang Hải', 'haidq@utt.edu.vn', '0987111009', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv010')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv010', @PassHash, N'Dương Thùy Trang', 'trangdt@utt.edu.vn', '0987111010', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv011')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv011', @PassHash, N'Hồ Minh Quân', 'quanhm@utt.edu.vn', '0987111011', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv012')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv012', @PassHash, N'Lương Thị Yến', 'yenlt@utt.edu.vn', '0987111012', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv013')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv013', @PassHash, N'Mai Tiến Dũng', 'dungmt@utt.edu.vn', '0987111013', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv014')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv014', @PassHash, N'Ngô Bảo Châu', 'chaunb@utt.edu.vn', '0987111014', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv015')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv015', @PassHash, N'Phan Thanh Tùng', 'tungpt@utt.edu.vn', '0987111015', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv016')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv016', @PassHash, N'Tạ Quỳnh Nga', 'ngatq@utt.edu.vn', '0987111016', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv017')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv017', @PassHash, N'Trịnh Quốc Huy', 'huytq@utt.edu.vn', '0987111017', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv018')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv018', @PassHash, N'Đoàn Thu Thảo', 'thaodt@utt.edu.vn', '0987111018', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv019')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv019', @PassHash, N'Lâm Văn Cường', 'cuonglv@utt.edu.vn', '0987111019', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv020')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv020', @PassHash, N'Cao Thị Mỹ Hạnh', 'hanhctm@utt.edu.vn', '0987111020', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv021')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv021', @PassHash, N'Hà Văn Nam', 'namhv@utt.edu.vn', '0987111021', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv022')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv022', @PassHash, N'Đào Quốc Việt', 'vietdq@utt.edu.vn', '0987111022', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv023')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv023', @PassHash, N'Lưu Thị Cẩm Tú', 'tulct@utt.edu.vn', '0987111023', 'SINHVIEN', 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'sv024')
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, HoTen, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES ('sv024', @PassHash, N'Phùng Đức Minh', 'minhpd@utt.edu.vn', '0987111024', 'SINHVIEN', 1, SYSDATETIME());

-- Đồng bộ họ tên tiếng Việt Unicode chuẩn
UPDATE TaiKhoan SET HoTen = N'Lê Thị Mai Hương' WHERE TenDangNhap = 'gv003';
UPDATE TaiKhoan SET HoTen = N'Hoàng Văn Thắng' WHERE TenDangNhap = 'gv004';
UPDATE TaiKhoan SET HoTen = N'Đặng Thị Bích Ngọc' WHERE TenDangNhap = 'gv005';
UPDATE TaiKhoan SET HoTen = N'Vũ Đình Trọng' WHERE TenDangNhap = 'gv006';
UPDATE TaiKhoan SET HoTen = N'Đỗ Gia Huy' WHERE TenDangNhap = 'sv007';
UPDATE TaiKhoan SET HoTen = N'Bùi Phương Thảo' WHERE TenDangNhap = 'sv008';
UPDATE TaiKhoan SET HoTen = N'Đinh Quang Hải' WHERE TenDangNhap = 'sv009';
UPDATE TaiKhoan SET HoTen = N'Dương Thùy Trang' WHERE TenDangNhap = 'sv010';
UPDATE TaiKhoan SET HoTen = N'Hồ Minh Quân' WHERE TenDangNhap = 'sv011';
UPDATE TaiKhoan SET HoTen = N'Lương Thị Yến' WHERE TenDangNhap = 'sv012';
UPDATE TaiKhoan SET HoTen = N'Mai Tiến Dũng' WHERE TenDangNhap = 'sv013';
UPDATE TaiKhoan SET HoTen = N'Ngô Bảo Châu' WHERE TenDangNhap = 'sv014';
UPDATE TaiKhoan SET HoTen = N'Phan Thanh Tùng' WHERE TenDangNhap = 'sv015';
UPDATE TaiKhoan SET HoTen = N'Tạ Quỳnh Nga' WHERE TenDangNhap = 'sv016';
UPDATE TaiKhoan SET HoTen = N'Trịnh Quốc Huy' WHERE TenDangNhap = 'sv017';
UPDATE TaiKhoan SET HoTen = N'Đoàn Thu Thảo' WHERE TenDangNhap = 'sv018';
UPDATE TaiKhoan SET HoTen = N'Lâm Văn Cường' WHERE TenDangNhap = 'sv019';
UPDATE TaiKhoan SET HoTen = N'Cao Thị Mỹ Hạnh' WHERE TenDangNhap = 'sv020';
UPDATE TaiKhoan SET HoTen = N'Hà Văn Nam' WHERE TenDangNhap = 'sv021';
UPDATE TaiKhoan SET HoTen = N'Đào Quốc Việt' WHERE TenDangNhap = 'sv022';
UPDATE TaiKhoan SET HoTen = N'Lưu Thị Cẩm Tú' WHERE TenDangNhap = 'sv023';
UPDATE TaiKhoan SET HoTen = N'Phùng Đức Minh' WHERE TenDangNhap = 'sv024';
GO

-- ----------------------------------------------------------------------------------------------------
-- 3. BẢNG GiangVien (6 Giảng viên giảng dạy)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 3. Nạp dữ liệu bảng GiangVien...';
IF NOT EXISTS (SELECT 1 FROM GiangVien WHERE MaGiangVien = 'GV001')
    INSERT INTO GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID)
    SELECT 'GV001', N'Nguyễn Minh Anh', 'gv001@demo.local', '0900000001', N'Công nghệ thông tin', TaiKhoanID
    FROM TaiKhoan WHERE TenDangNhap = 'gv001';

IF NOT EXISTS (SELECT 1 FROM GiangVien WHERE MaGiangVien = 'GV002')
    INSERT INTO GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID)
    SELECT 'GV002', N'Trần Quốc Bảo', 'gv002@demo.local', '0900000002', N'Công nghệ thông tin', TaiKhoanID
    FROM TaiKhoan WHERE TenDangNhap = 'gv002';

IF NOT EXISTS (SELECT 1 FROM GiangVien WHERE MaGiangVien = 'GV003')
    INSERT INTO GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID)
    SELECT 'GV003', N'Lê Thị Mai Hương', 'huongltm@utt.edu.vn', '0912345603', N'Công nghệ thông tin', TaiKhoanID
    FROM TaiKhoan WHERE TenDangNhap = 'gv003';

IF NOT EXISTS (SELECT 1 FROM GiangVien WHERE MaGiangVien = 'GV004')
    INSERT INTO GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID)
    SELECT 'GV004', N'Hoàng Văn Thắng', 'thanghv@utt.edu.vn', '0912345604', N'Hệ thống thông tin', TaiKhoanID
    FROM TaiKhoan WHERE TenDangNhap = 'gv004';

IF NOT EXISTS (SELECT 1 FROM GiangVien WHERE MaGiangVien = 'GV005')
    INSERT INTO GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID)
    SELECT 'GV005', N'Đặng Thị Bích Ngọc', 'ngocdtb@utt.edu.vn', '0912345605', N'An toàn thông tin', TaiKhoanID
    FROM TaiKhoan WHERE TenDangNhap = 'gv005';

IF NOT EXISTS (SELECT 1 FROM GiangVien WHERE MaGiangVien = 'GV006')
    INSERT INTO GiangVien (MaGiangVien, HoTen, Email, SoDienThoai, Khoa, TaiKhoanID)
    SELECT 'GV006', N'Vũ Đình Trọng', 'trongvd@utt.edu.vn', '0912345606', N'Khoa học máy tính', TaiKhoanID
    FROM TaiKhoan WHERE TenDangNhap = 'gv006';

UPDATE GiangVien SET HoTen = N'Nguyễn Minh Anh' WHERE MaGiangVien = 'GV001';
UPDATE GiangVien SET HoTen = N'Trần Quốc Bảo' WHERE MaGiangVien = 'GV002';
UPDATE GiangVien SET HoTen = N'Lê Thị Mai Hương' WHERE MaGiangVien = 'GV003';
UPDATE GiangVien SET HoTen = N'Hoàng Văn Thắng' WHERE MaGiangVien = 'GV004';
UPDATE GiangVien SET HoTen = N'Đặng Thị Bích Ngọc' WHERE MaGiangVien = 'GV005';
UPDATE GiangVien SET HoTen = N'Vũ Đình Trọng' WHERE MaGiangVien = 'GV006';
GO

-- ----------------------------------------------------------------------------------------------------
-- 4. BẢNG SinhVien (Đủ 30 sinh viên)
-- Nhóm SV025 - SV030 (6 sinh viên) để TaiKhoanID = NULL để kiểm thử chức năng Đăng ký tài khoản!
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 4. Nạp dữ liệu bảng SinhVien...';
DECLARE @LopCNTT_A INT = (SELECT LopID FROM Lop WHERE MaLop = 'CNTT_K20A');
DECLARE @LopCNTT_B INT = (SELECT LopID FROM Lop WHERE MaLop = 'CNTT_K20B');
DECLARE @LopHTTT_A INT = (SELECT LopID FROM Lop WHERE MaLop = 'HTTT_K20A');
DECLARE @LopHTTT_B INT = (SELECT LopID FROM Lop WHERE MaLop = 'HTTT_K20B');
DECLARE @LopATTT_A INT = (SELECT LopID FROM Lop WHERE MaLop = 'ATTT_K20A');

-- Nhóm sinh viên gốc (SV001 -> SV005)
IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV001')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV001', N'Phạm Thu Hà', '2005-04-12', N'Nữ', 'sv001@demo.local', '0910000001', N'Hà Nội', '2023-09-05', N'Đang học', @LopHTTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv001';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV002')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV002', N'Nguyễn Hoàng Long', '2005-08-20', N'Nam', 'sv002@demo.local', '0910000002', N'Hà Nội', '2023-09-05', N'Đang học', @LopHTTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv002';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV003')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV003', N'Trần Ngọc Mai', '2005-11-03', N'Nữ', 'sv003@demo.local', '0910000003', N'Hà Nội', '2023-09-05', N'Đang học', @LopHTTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv003';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV004')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV004', N'Lê Đức Anh', '2005-02-18', N'Nam', 'sv004@demo.local', '0910000004', N'Hà Nội', '2023-09-05', N'Đang học', @LopHTTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv004';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV005')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV005', N'Vũ Khánh Linh', '2005-06-27', N'Nữ', 'sv005@demo.local', '0910000005', N'Hà Nội', '2023-09-05', N'Đang học', @LopHTTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv005';

-- Nhóm đã có tài khoản (SV007 -> SV024)
IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV007')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV007', N'Đỗ Gia Huy', '2004-03-12', N'Nam', 'huydg@utt.edu.vn', '0987111007', N'Hà Nội', '2022-09-05', N'Đang học', @LopCNTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv007';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV008')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV008', N'Bùi Phương Thảo', '2004-07-21', N'Nữ', 'thaobp@utt.edu.vn', '0987111008', N'Hải Phòng', '2022-09-05', N'Đang học', @LopCNTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv008';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV009')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV009', N'Đinh Quang Hải', '2004-11-05', N'Nam', 'haidq@utt.edu.vn', '0987111009', N'Nam Định', '2022-09-05', N'Đang học', @LopCNTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv009';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV010')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV010', N'Dương Thùy Trang', '2004-09-18', N'Nữ', 'trangdt@utt.edu.vn', '0987111010', N'Thái Nguyên', '2022-09-05', N'Đang học', @LopCNTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv010';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV011')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV011', N'Hồ Minh Quân', '2004-01-25', N'Nam', 'quanhm@utt.edu.vn', '0987111011', N'Hà Nội', '2022-09-05', N'Đang học', @LopCNTT_B, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv011';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV012')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV012', N'Lương Thị Yến', '2004-05-14', N'Nữ', 'yenlt@utt.edu.vn', '0987111012', N'Bắc Ninh', '2022-09-05', N'Đang học', @LopCNTT_B, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv012';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV013')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV013', N'Mai Tiến Dũng', '2004-08-30', N'Nam', 'dungmt@utt.edu.vn', '0987111013', N'Vĩnh Phúc', '2022-09-05', N'Đang học', @LopCNTT_B, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv013';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV014')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV014', N'Ngô Bảo Châu', '2004-12-02', N'Nam', 'chaunb@utt.edu.vn', '0987111014', N'Thanh Hóa', '2022-09-05', N'Đang học', @LopCNTT_B, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv014';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV015')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV015', N'Phan Thanh Tùng', '2004-02-19', N'Nam', 'tungpt@utt.edu.vn', '0987111015', N'Nghệ An', '2022-09-05', N'Đang học', @LopCNTT_B, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv015';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV016')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV016', N'Tạ Quỳnh Nga', '2004-06-08', N'Nữ', 'ngatq@utt.edu.vn', '0987111016', N'Hà Nội', '2022-09-05', N'Đang học', @LopHTTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv016';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV017')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV017', N'Trịnh Quốc Huy', '2004-10-10', N'Nam', 'huytq@utt.edu.vn', '0987111017', N'Hải Dương', '2022-09-05', N'Đang học', @LopHTTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv017';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV018')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV018', N'Đoàn Thu Thảo', '2004-04-17', N'Nữ', 'thaodt@utt.edu.vn', '0987111018', N'Hà Nam', '2022-09-05', N'Đang học', @LopHTTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv018';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV019')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV019', N'Lâm Văn Cường', '2004-03-01', N'Nam', 'cuonglv@utt.edu.vn', '0987111019', N'Hà Nội', '2022-09-05', N'Đang học', @LopHTTT_B, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv019';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV020')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV020', N'Cao Thị Mỹ Hạnh', '2004-08-22', N'Nữ', 'hanhctm@utt.edu.vn', '0987111020', N'Quảng Ninh', '2022-09-05', N'Đang học', @LopHTTT_B, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv020';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV021')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV021', N'Hà Văn Nam', '2004-11-15', N'Nam', 'namhv@utt.edu.vn', '0987111021', N'Hòa Bình', '2022-09-05', N'Đang học', @LopHTTT_B, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv021';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV022')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV022', N'Đào Quốc Việt', '2004-01-09', N'Nam', 'vietdq@utt.edu.vn', '0987111022', N'Thái Bình', '2022-09-05', N'Đang học', @LopATTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv022';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV023')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV023', N'Lưu Thị Cẩm Tú', '2004-06-27', N'Nữ', 'tulct@utt.edu.vn', '0987111023', N'Hà Nội', '2022-09-05', N'Đang học', @LopATTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv023';

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV024')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    SELECT 'SV024', N'Phùng Đức Minh', '2004-09-03', N'Nam', 'minhpd@utt.edu.vn', '0987111024', N'Phú Thọ', '2022-09-05', N'Đang học', @LopATTT_A, TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv024';

-- Nhóm CHƯA CÓ TÀI KHOẢN (TaiKhoanID = NULL)
IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV025')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    VALUES ('SV025', N'Vũ Hải Yến', '2004-12-19', N'Nữ', 'yenvh@utt.edu.vn', '0987111025', N'Hà Nội', '2022-09-05', N'Đang học', @LopHTTT_A, NULL);

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV026')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    VALUES ('SV026', N'Chu Đình Trọng', '2004-04-04', N'Nam', 'trongcd@utt.edu.vn', '0987111026', N'Hưng Yên', '2022-09-05', N'Đang học', @LopCNTT_A, NULL);

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV027')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    VALUES ('SV027', N'Bạch Diệu Linh', '2004-07-16', N'Nữ', 'linhbd@utt.edu.vn', '0987111027', N'Hà Nội', '2022-09-05', N'Đang học', @LopCNTT_B, NULL);

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV028')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    VALUES ('SV028', N'Ninh Khắc Đạt', '2004-10-28', N'Nam', 'datnk@utt.edu.vn', '0987111028', N'Ninh Bình', '2022-09-05', N'Đang học', @LopHTTT_B, NULL);

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV029')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    VALUES ('SV029', N'Đặng Hoàng Quân', '2004-02-11', N'Nam', 'quandh@utt.edu.vn', '0987111029', N'Hà Nội', '2022-09-05', N'Đang học', @LopATTT_A, NULL);

IF NOT EXISTS (SELECT 1 FROM SinhVien WHERE MaSinhVien = 'SV030')
    INSERT INTO SinhVien (MaSinhVien, HoTen, NgaySinh, GioiTinh, Email, SoDienThoai, DiaChi, NgayNhapHoc, TrangThai, LopID, TaiKhoanID)
    VALUES ('SV030', N'Tống Mỹ Kim', '2004-08-08', N'Nữ', 'kimtm@utt.edu.vn', '0987111030', N'Bắc Giang', '2022-09-05', N'Đang học', @LopATTT_A, NULL);

-- Chuẩn hóa họ tên tiếng Việt chuẩn Unicode cho toàn bộ 30 sinh viên
UPDATE SinhVien SET HoTen = N'Phạm Thu Hà' WHERE MaSinhVien = 'SV001';
UPDATE SinhVien SET HoTen = N'Nguyễn Hoàng Long' WHERE MaSinhVien = 'SV002';
UPDATE SinhVien SET HoTen = N'Trần Ngọc Mai' WHERE MaSinhVien = 'SV003';
UPDATE SinhVien SET HoTen = N'Lê Đức Anh' WHERE MaSinhVien = 'SV004';
UPDATE SinhVien SET HoTen = N'Vũ Khánh Linh' WHERE MaSinhVien = 'SV005';
UPDATE SinhVien SET HoTen = N'Nguyễn Tuấn Kiệt' WHERE MaSinhVien = 'SV06';
UPDATE SinhVien SET HoTen = N'Đỗ Gia Huy' WHERE MaSinhVien = 'SV007';
UPDATE SinhVien SET HoTen = N'Bùi Phương Thảo' WHERE MaSinhVien = 'SV008';
UPDATE SinhVien SET HoTen = N'Đinh Quang Hải' WHERE MaSinhVien = 'SV009';
UPDATE SinhVien SET HoTen = N'Dương Thùy Trang' WHERE MaSinhVien = 'SV010';
UPDATE SinhVien SET HoTen = N'Hồ Minh Quân' WHERE MaSinhVien = 'SV011';
UPDATE SinhVien SET HoTen = N'Lương Thị Yến' WHERE MaSinhVien = 'SV012';
UPDATE SinhVien SET HoTen = N'Mai Tiến Dũng' WHERE MaSinhVien = 'SV013';
UPDATE SinhVien SET HoTen = N'Ngô Bảo Châu' WHERE MaSinhVien = 'SV014';
UPDATE SinhVien SET HoTen = N'Phan Thanh Tùng' WHERE MaSinhVien = 'SV015';
UPDATE SinhVien SET HoTen = N'Tạ Quỳnh Nga' WHERE MaSinhVien = 'SV016';
UPDATE SinhVien SET HoTen = N'Trịnh Quốc Huy' WHERE MaSinhVien = 'SV017';
UPDATE SinhVien SET HoTen = N'Đoàn Thu Thảo' WHERE MaSinhVien = 'SV018';
UPDATE SinhVien SET HoTen = N'Lâm Văn Cường' WHERE MaSinhVien = 'SV019';
UPDATE SinhVien SET HoTen = N'Cao Thị Mỹ Hạnh' WHERE MaSinhVien = 'SV020';
UPDATE SinhVien SET HoTen = N'Hà Văn Nam' WHERE MaSinhVien = 'SV021';
UPDATE SinhVien SET HoTen = N'Đào Quốc Việt' WHERE MaSinhVien = 'SV022';
UPDATE SinhVien SET HoTen = N'Lưu Thị Cẩm Tú' WHERE MaSinhVien = 'SV023';
UPDATE SinhVien SET HoTen = N'Phùng Đức Minh' WHERE MaSinhVien = 'SV024';
UPDATE SinhVien SET HoTen = N'Vũ Hải Yến' WHERE MaSinhVien = 'SV025';
UPDATE SinhVien SET HoTen = N'Chu Đình Trọng' WHERE MaSinhVien = 'SV026';
UPDATE SinhVien SET HoTen = N'Bạch Diệu Linh' WHERE MaSinhVien = 'SV027';
UPDATE SinhVien SET HoTen = N'Ninh Khắc Đạt' WHERE MaSinhVien = 'SV028';
UPDATE SinhVien SET HoTen = N'Đặng Hoàng Quân' WHERE MaSinhVien = 'SV029';
UPDATE SinhVien SET HoTen = N'Tống Mỹ Kim' WHERE MaSinhVien = 'SV030';
GO

-- ----------------------------------------------------------------------------------------------------
-- 5. BẢNG MonHoc (12 Môn học đại học)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 5. Nạp dữ liệu bảng MonHoc...';
-- 5 môn học gốc từ sqlduanjava
IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'JAVA101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('JAVA101', N'Lập trình Java', 3, N'Môn học lập trình Java cơ bản', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'DB101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('DB101', N'Cơ sở dữ liệu', 3, N'Mô hình dữ liệu và SQL Server', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'NET101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('NET101', N'Mạng máy tính', 3, N'Kiến thức mạng máy tính', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'SE101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('SE101', N'Công nghệ phần mềm', 3, N'Phân tích và thiết kế phần mềm', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'IS101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('IS101', N'Hệ thống thông tin quản lý', 2, N'Tổng quan hệ thống thông tin', 1);

-- 7 môn học bổ sung
IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'WEB101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('WEB101', N'Phát triển ứng dụng Web', 3, N'Lập trình Web Frontend, Backend API với Java/Spring Boot', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'OOP101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('OOP101', N'Lập trình hướng đối tượng', 3, N'Nguyên lý OOP, kế thừa, đa hình, trừu tượng, đóng gói', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'AI101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('AI101', N'Trí tuệ nhân tạo căn bản', 3, N'Nhập môn AI, Machine Learning và các thuật toán tìm kiếm', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'OS101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('OS101', N'Hệ điều hành', 3, N'Kiến trúc hệ điều hành, tiến trình, luồng, quản lý bộ nhớ', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'SEC101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('SEC101', N'An toàn thông tin mạng', 3, N'Mã hóa, bảo mật mạng máy tính và phòng thủ an ninh mạng', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'PY101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('PY101', N'Lập trình Python ứng dụng', 2, N'Cú pháp Python, phân tích dữ liệu và tự động hóa', 1);

IF NOT EXISTS (SELECT 1 FROM MonHoc WHERE MaMonHoc = 'CLOUD101')
    INSERT INTO MonHoc (MaMonHoc, TenMonHoc, SoTinChi, MoTa, TrangThai)
    VALUES ('CLOUD101', N'Điện toán đám mây', 2, N'Kiến trúc Cloud Computing, AWS/Azure và Containerization', 1);

UPDATE MonHoc SET TenMonHoc = N'Phát triển ứng dụng Web', MoTa = N'Lập trình Web Frontend, Backend API với Java/Spring Boot' WHERE MaMonHoc = 'WEB101';
UPDATE MonHoc SET TenMonHoc = N'Lập trình hướng đối tượng', MoTa = N'Nguyên lý OOP, kế thừa, đa hình, trừu tượng, đóng gói' WHERE MaMonHoc = 'OOP101';
UPDATE MonHoc SET TenMonHoc = N'Trí tuệ nhân tạo căn bản', MoTa = N'Nhập môn AI, Machine Learning và các thuật toán tìm kiếm' WHERE MaMonHoc = 'AI101';
UPDATE MonHoc SET TenMonHoc = N'Hệ điều hành', MoTa = N'Kiến trúc hệ điều hành, tiến trình, luồng, quản lý bộ nhớ' WHERE MaMonHoc = 'OS101';
UPDATE MonHoc SET TenMonHoc = N'An toàn thông tin mạng', MoTa = N'Mã hóa, bảo mật mạng máy tính và phòng thủ an ninh mạng' WHERE MaMonHoc = 'SEC101';
UPDATE MonHoc SET TenMonHoc = N'Lập trình Python ứng dụng', MoTa = N'Cú pháp Python, phân tích dữ liệu và tự động hóa' WHERE MaMonHoc = 'PY101';
UPDATE MonHoc SET TenMonHoc = N'Điện toán đám mây', MoTa = N'Kiến trúc Cloud Computing, AWS/Azure và Containerization' WHERE MaMonHoc = 'CLOUD101';
GO

-- ----------------------------------------------------------------------------------------------------
-- 6. BẢNG HocKy (3 Học kỳ)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 6. Nạp dữ liệu bảng HocKy...';
IF NOT EXISTS (SELECT 1 FROM HocKy WHERE MaHocKy = '2026-1')
    INSERT INTO HocKy (MaHocKy, TenHocKy, NamHoc, NgayBatDau, NgayKetThuc, TrangThai)
    VALUES ('2026-1', N'Học kỳ 1 năm học 2026-2027', '2026-2027', '2026-09-01', '2027-01-15', N'Đang học');

IF NOT EXISTS (SELECT 1 FROM HocKy WHERE MaHocKy = '2026-2')
    INSERT INTO HocKy (MaHocKy, TenHocKy, NamHoc, NgayBatDau, NgayKetThuc, TrangThai)
    VALUES ('2026-2', N'Học kỳ 2 năm học 2026-2027', '2026-2027', '2027-02-01', '2027-06-15', N'Chưa bắt đầu');

IF NOT EXISTS (SELECT 1 FROM HocKy WHERE MaHocKy = '2025-2')
    INSERT INTO HocKy (MaHocKy, TenHocKy, NamHoc, NgayBatDau, NgayKetThuc, TrangThai)
    VALUES ('2025-2', N'Học kỳ 2 năm học 2025-2026', '2025-2026', '2026-01-15', '2026-06-15', N'Đã kết thúc');

UPDATE HocKy SET TenHocKy = N'Học kỳ 1 năm học 2026-2027' WHERE MaHocKy = '2026-1';
UPDATE HocKy SET TenHocKy = N'Học kỳ 2 năm học 2026-2027' WHERE MaHocKy = '2026-2';
UPDATE HocKy SET TenHocKy = N'Học kỳ 2 năm học 2025-2026' WHERE MaHocKy = '2025-2';
GO

-- ----------------------------------------------------------------------------------------------------
-- 7. BẢNG LopHocPhan (14 Lớp học phần trải qua các học kỳ)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 7. Nạp dữ liệu bảng LopHocPhan...';
DECLARE @Hk2026_1 INT = (SELECT HocKyID FROM HocKy WHERE MaHocKy = '2026-1');
DECLARE @Hk2026_2 INT = (SELECT HocKyID FROM HocKy WHERE MaHocKy = '2026-2');
DECLARE @Hk2025_2 INT = (SELECT HocKyID FROM HocKy WHERE MaHocKy = '2025-2');

DECLARE @MhJava INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'JAVA101');
DECLARE @MhDb   INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'DB101');
DECLARE @MhNet  INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'NET101');
DECLARE @MhSe   INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'SE101');
DECLARE @MhIs   INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'IS101');
DECLARE @MhWeb  INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'WEB101');
DECLARE @MhOop  INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'OOP101');
DECLARE @MhAi   INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'AI101');
DECLARE @MhOs   INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'OS101');
DECLARE @MhSec  INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'SEC101');
DECLARE @MhPy   INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'PY101');
DECLARE @MhCloud INT = (SELECT MonHocID FROM MonHoc WHERE MaMonHoc = 'CLOUD101');

DECLARE @Gv1 INT = (SELECT GiangVienID FROM GiangVien WHERE MaGiangVien = 'GV001');
DECLARE @Gv2 INT = (SELECT GiangVienID FROM GiangVien WHERE MaGiangVien = 'GV002');
DECLARE @Gv3 INT = (SELECT GiangVienID FROM GiangVien WHERE MaGiangVien = 'GV003');
DECLARE @Gv4 INT = (SELECT GiangVienID FROM GiangVien WHERE MaGiangVien = 'GV004');
DECLARE @Gv5 INT = (SELECT GiangVienID FROM GiangVien WHERE MaGiangVien = 'GV005');
DECLARE @Gv6 INT = (SELECT GiangVienID FROM GiangVien WHERE MaGiangVien = 'GV006');

-- Lớp học phần kỳ 2026-1 gốc từ sqlduanjava
IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'JAVA101-2026-1-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('JAVA101-2026-1-01', @MhJava, @Hk2026_1, @Gv1, '01', 40, N'Thứ 2 (Tiết 1-3)', N'P.301', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'DB101-2026-1-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('DB101-2026-1-01', @MhDb, @Hk2026_1, @Gv1, '01', 40, N'Thứ 3 (Tiết 4-6)', N'P.302', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'NET101-2026-1-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('NET101-2026-1-01', @MhNet, @Hk2026_1, @Gv2, '01', 40, N'Thứ 4 (Tiết 1-3)', N'P.303', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'SE101-2026-1-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('SE101-2026-1-01', @MhSe, @Hk2026_1, @Gv2, '01', 40, N'Thứ 5 (Tiết 4-6)', N'P.304', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'IS101-2026-1-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('IS101-2026-1-01', @MhIs, @Hk2026_1, @Gv1, '01', 40, N'Thứ 6 (Tiết 1-2)', N'P.305', N'Đang mở');

-- Lớp học phần kỳ 2026-1 bổ sung
IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'JAVA101-2026-1-02')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('JAVA101-2026-1-02', @MhJava, @Hk2026_1, @Gv1, '02', 40, N'Thứ 3 (Tiết 7-9)', N'A2-302', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'WEB101-2026-1-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('WEB101-2026-1-01', @MhWeb, @Hk2026_1, @Gv3, '01', 45, N'Thứ 4 (Tiết 1-3)', N'Lab 201', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'OOP101-2026-1-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('OOP101-2026-1-01', @MhOop, @Hk2026_1, @Gv4, '01', 50, N'Thứ 5 (Tiết 4-6)', N'A1-405', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'SEC101-2026-1-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('SEC101-2026-1-01', @MhSec, @Hk2026_1, @Gv5, '01', 40, N'Thứ 6 (Tiết 1-3)', N'Lab 305', N'Đang mở');

-- Lớp học phần kỳ 2026-2
IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'AI101-2026-2-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('AI101-2026-2-01', @MhAi, @Hk2026_2, @Gv6, '01', 45, N'Thứ 2 (Tiết 1-3)', N'A2-205', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'OS101-2026-2-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('OS101-2026-2-01', @MhOs, @Hk2026_2, @Gv2, '01', 50, N'Thứ 4 (Tiết 7-9)', N'A1-203', N'Đang mở');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'PY101-2026-2-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('PY101-2026-2-01', @MhPy, @Hk2026_2, @Gv4, '01', 40, N'Thứ 6 (Tiết 4-6)', N'Lab 102', N'Đang mở');

-- Lớp học phần kỳ 2025-2
IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'JAVA101-2025-2-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('JAVA101-2025-2-01', @MhJava, @Hk2025_2, @Gv1, '01', 45, N'Thứ 2 (Tiết 7-9)', N'A2-101', N'Đã kết thúc');

IF NOT EXISTS (SELECT 1 FROM LopHocPhan WHERE MaLopHocPhan = 'DB101-2025-2-01')
    INSERT INTO LopHocPhan (MaLopHocPhan, MonHocID, HocKyID, GiangVienID, Nhom, SiSoToiDa, LichHoc, PhongHoc, TrangThai)
    VALUES ('DB101-2025-2-01', @MhDb, @Hk2025_2, @Gv1, '01', 45, N'Thứ 5 (Tiết 1-3)', N'Lab 101', N'Đã kết thúc');
GO

-- ----------------------------------------------------------------------------------------------------
-- 8 & 9. BẢNG DangKyHocPhan VÀ BangDiem
-- Quy mô: 120 lượt đăng ký học phần kèm bảng điểm chuẩn quy chế tín chỉ
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 8 & 9. Nạp dữ liệu bảng DangKyHocPhan và BangDiem...';

IF OBJECT_ID('tempdb..#usp_ThemDangKyVaDiem') IS NOT NULL DROP PROCEDURE #usp_ThemDangKyVaDiem;
GO

CREATE PROCEDURE #usp_ThemDangKyVaDiem
    @MaSV VARCHAR(20),
    @MaLHP VARCHAR(30),
    @TrangThaiDK NVARCHAR(20),
    @DCC DECIMAL(4,2),
    @DGK DECIMAL(4,2),
    @DCK DECIMAL(4,2)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @SVID INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = @MaSV);
    DECLARE @LHPID INT = (SELECT LopHocPhanID FROM LopHocPhan WHERE MaLopHocPhan = @MaLHP);
    
    IF @SVID IS NULL OR @LHPID IS NULL RETURN;

    DECLARE @DKID INT;
    SELECT @DKID = DangKyID FROM DangKyHocPhan WHERE SinhVienID = @SVID AND LopHocPhanID = @LHPID;

    IF @DKID IS NULL
    BEGIN
        INSERT INTO DangKyHocPhan (SinhVienID, LopHocPhanID, NgayDangKy, TrangThai)
        VALUES (@SVID, @LHPID, DATEADD(DAY, -15, SYSDATETIME()), @TrangThaiDK);
        SET @DKID = SCOPE_IDENTITY();
    END

    IF @DKID IS NOT NULL AND NOT EXISTS (SELECT 1 FROM BangDiem WHERE DangKyID = @DKID)
    BEGIN
        DECLARE @DTK DECIMAL(3,1) = NULL;
        DECLARE @DChu VARCHAR(2) = NULL;
        DECLARE @DHe4 DECIMAL(2,1) = NULL;

        IF @DCC IS NOT NULL AND @DGK IS NOT NULL AND @DCK IS NOT NULL
        BEGIN
            IF @DCC < 4.0
            BEGIN
                SET @DTK = 0.0;
            END
            ELSE
            BEGIN
                SET @DTK = ROUND(@DCC * 0.2 + @DGK * 0.3 + @DCK * 0.5, 1);
            END

            IF @DTK >= 8.5 BEGIN SET @DChu = 'A'; SET @DHe4 = 4.0; END
            ELSE IF @DTK >= 8.0 BEGIN SET @DChu = 'B+'; SET @DHe4 = 3.5; END
            ELSE IF @DTK >= 7.0 BEGIN SET @DChu = 'B'; SET @DHe4 = 3.0; END
            ELSE IF @DTK >= 6.5 BEGIN SET @DChu = 'C+'; SET @DHe4 = 2.5; END
            ELSE IF @DTK >= 5.5 BEGIN SET @DChu = 'C'; SET @DHe4 = 2.0; END
            ELSE IF @DTK >= 5.0 BEGIN SET @DChu = 'D+'; SET @DHe4 = 1.5; END
            ELSE IF @DTK >= 4.0 BEGIN SET @DChu = 'D'; SET @DHe4 = 1.0; END
            ELSE BEGIN SET @DChu = 'F'; SET @DHe4 = 0.0; END
        END

        INSERT INTO BangDiem (DangKyID, DiemCC, DiemGK, DiemCK, DiemTongKet, DiemChu, DiemHe4, NgayCapNhat, NguoiCapNhatID, GhiChu)
        VALUES (@DKID, @DCC, @DGK, @DCK, @DTK, @DChu, @DHe4, SYSDATETIME(), 1, N'Dữ liệu mẫu');
    END
END;
GO

-- 1. LỚP JAVA101-2026-1-01 (Nhóm 1)
EXEC #usp_ThemDangKyVaDiem 'SV002', 'JAVA101-2026-1-01', N'Đang học', 8.0, 7.0, 7.0; -- 7.2 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV003', 'JAVA101-2026-1-01', N'Đang học', 9.0, 8.5, 9.0; -- 8.9 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV004', 'JAVA101-2026-1-01', N'Đang học', 7.5, 7.0, 6.5; -- 6.9 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV005', 'JAVA101-2026-1-01', N'Đang học', 6.0, 4.5, 3.5; -- 4.3 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV06',  'JAVA101-2026-1-01', N'Đang học', 8.5, 8.0, 8.5; -- 8.4 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV007', 'JAVA101-2026-1-01', N'Đang học', 9.0, 8.0, 8.5; -- 8.5 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV008', 'JAVA101-2026-1-01', N'Đang học', 8.0, 8.0, 7.5; -- 7.8 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV009', 'JAVA101-2026-1-01', N'Đang học', 6.0, 4.0, 3.0; -- 3.9 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV010', 'JAVA101-2026-1-01', N'Đang học', 9.5, 9.0, 9.5; -- 9.4 (A / 4.0)

-- 2. LỚP JAVA101-2026-1-02 (Nhóm 2)
EXEC #usp_ThemDangKyVaDiem 'SV011', 'JAVA101-2026-1-02', N'Đang học', 8.5, 8.0, 8.0; -- 8.1 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV012', 'JAVA101-2026-1-02', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV013', 'JAVA101-2026-1-02', N'Đang học', 7.5, 6.5, 7.0; -- 7.0 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV014', 'JAVA101-2026-1-02', N'Đang học', 5.0, 4.0, 3.5; -- 4.0 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV015', 'JAVA101-2026-1-02', N'Đang học', 9.0, 8.5, 8.5; -- 8.6 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV016', 'JAVA101-2026-1-02', N'Đang học', 8.5, 8.0, 7.5; -- 7.9 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV017', 'JAVA101-2026-1-02', N'Đang học', 7.0, 6.5, 6.0; -- 6.4 (C / 2.0)
EXEC #usp_ThemDangKyVaDiem 'SV018', 'JAVA101-2026-1-02', N'Đang học', 8.0, 7.5, 7.0; -- 7.4 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV019', 'JAVA101-2026-1-02', N'Đang học', 7.5, 7.0, 6.5; -- 6.9 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV020', 'JAVA101-2026-1-02', N'Đang học', 7.0, 5.5, 5.0; -- 5.6 (C / 2.0)

-- 3. LỚP DB101-2026-1-01
EXEC #usp_ThemDangKyVaDiem 'SV002', 'DB101-2026-1-01', N'Đang học', 8.0, 7.5, 6.5; -- 7.1 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV003', 'DB101-2026-1-01', N'Đang học', 9.0, 9.0, 8.5; -- 8.8 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV004', 'DB101-2026-1-01', N'Đang học', 7.0, 6.5, 6.0; -- 6.4 (C / 2.0)
EXEC #usp_ThemDangKyVaDiem 'SV005', 'DB101-2026-1-01', N'Đang học', 5.0, 4.0, 3.0; -- 3.7 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV007', 'DB101-2026-1-01', N'Đang học', 8.5, 8.5, 8.0; -- 8.3 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV008', 'DB101-2026-1-01', N'Đang học', 8.0, 7.0, 7.0; -- 7.2 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV011', 'DB101-2026-1-01', N'Đang học', 8.5, 8.0, 8.0; -- 8.1 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV012', 'DB101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV016', 'DB101-2026-1-01', N'Đang học', 9.0, 8.5, 8.5; -- 8.6 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV017', 'DB101-2026-1-01', N'Đang học', 7.5, 7.0, 6.5; -- 6.9 (C+ / 2.5)

-- 4. LỚP NET101-2026-1-01
EXEC #usp_ThemDangKyVaDiem 'SV002', 'NET101-2026-1-01', N'Đang học', 7.5, 6.5, 6.5; -- 6.7 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV003', 'NET101-2026-1-01', N'Đang học', 8.5, 8.0, 8.5; -- 8.4 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV06',  'NET101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV009', 'NET101-2026-1-01', N'Đang học', 5.5, 4.5, 3.0; -- 4.0 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV010', 'NET101-2026-1-01', N'Đang học', 9.0, 8.5, 8.5; -- 8.6 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV013', 'NET101-2026-1-01', N'Đang học', 8.0, 7.0, 6.5; -- 7.0 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV014', 'NET101-2026-1-01', N'Đang học', 5.0, 3.5, 3.0; -- 3.6 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV018', 'NET101-2026-1-01', N'Đang học', 8.5, 8.0, 7.5; -- 7.9 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV022', 'NET101-2026-1-01', N'Đang học', 5.0, 4.0, 3.0; -- 3.7 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV023', 'NET101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)

-- 5. LỚP SE101-2026-1-01
EXEC #usp_ThemDangKyVaDiem 'SV004', 'SE101-2026-1-01', N'Đang học', 8.0, 7.0, 7.0; -- 7.2 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV005', 'SE101-2026-1-01', N'Đang học', 5.5, 4.0, 3.5; -- 4.1 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV007', 'SE101-2026-1-01', N'Đang học', 9.0, 8.5, 8.5; -- 8.6 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV008', 'SE101-2026-1-01', N'Đang học', 8.5, 8.0, 8.0; -- 8.1 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV011', 'SE101-2026-1-01', N'Đang học', 8.0, 7.5, 7.0; -- 7.4 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV012', 'SE101-2026-1-01', N'Đang học', 8.5, 8.0, 8.0; -- 8.1 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV015', 'SE101-2026-1-01', N'Đang học', 9.0, 9.0, 8.5; -- 8.8 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV019', 'SE101-2026-1-01', N'Đang học', 7.5, 6.5, 6.0; -- 6.5 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV020', 'SE101-2026-1-01', N'Đang học', 6.0, 4.0, 3.0; -- 3.9 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV024', 'SE101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)

-- 6. LỚP IS101-2026-1-01
EXEC #usp_ThemDangKyVaDiem 'SV002', 'IS101-2026-1-01', N'Đang học', 8.0, 7.0, 8.0; -- 7.7 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV003', 'IS101-2026-1-01', N'Đang học', 9.5, 9.0, 9.0; -- 9.1 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV06',  'IS101-2026-1-01', N'Đang học', 8.5, 8.0, 8.0; -- 8.1 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV009', 'IS101-2026-1-01', N'Đang học', 5.0, 4.0, 3.0; -- 3.7 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV010', 'IS101-2026-1-01', N'Đang học', 9.0, 8.5, 8.5; -- 8.6 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV013', 'IS101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV016', 'IS101-2026-1-01', N'Đang học', 8.5, 8.0, 8.0; -- 8.1 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV017', 'IS101-2026-1-01', N'Đang học', 7.5, 7.0, 6.5; -- 6.9 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV018', 'IS101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV021', 'IS101-2026-1-01', N'Đang học', 7.0, 6.5, 6.0; -- 6.4 (C / 2.0)

-- 7. LỚP WEB101-2026-1-01
EXEC #usp_ThemDangKyVaDiem 'SV007', 'WEB101-2026-1-01', N'Đang học', 9.5, 9.0, 9.0; -- 9.1 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV008', 'WEB101-2026-1-01', N'Đang học', 8.5, 8.0, 8.5; -- 8.4 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV009', 'WEB101-2026-1-01', N'Đang học', 6.0, 3.5, 3.0; -- 3.8 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV010', 'WEB101-2026-1-01', N'Đang học', 9.0, 9.0, 8.5; -- 8.8 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV011', 'WEB101-2026-1-01', N'Đang học', 8.0, 8.0, 7.5; -- 7.8 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV012', 'WEB101-2026-1-01', N'Đang học', 8.5, 8.5, 8.0; -- 8.3 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV013', 'WEB101-2026-1-01', N'Đang học', 7.5, 6.5, 6.5; -- 6.7 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV014', 'WEB101-2026-1-01', N'Đang học', 5.5, 3.0, 3.5; -- 3.8 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV015', 'WEB101-2026-1-01', N'Đang học', 9.0, 8.5, 9.0; -- 8.9 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV022', 'WEB101-2026-1-01', N'Đang học', 5.0, 3.5, 3.0; -- 3.6 (F / 0.0) -> Học lại

-- 8. LỚP OOP101-2026-1-01
EXEC #usp_ThemDangKyVaDiem 'SV002', 'OOP101-2026-1-01', N'Đang học', 7.5, 7.0, 6.0; -- 6.6 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV004', 'OOP101-2026-1-01', N'Đang học', 7.5, 6.5, 6.0; -- 6.5 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV007', 'OOP101-2026-1-01', N'Đang học', 8.5, 8.0, 8.0; -- 8.1 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV011', 'OOP101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV016', 'OOP101-2026-1-01', N'Đang học', 8.5, 8.5, 8.0; -- 8.3 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV017', 'OOP101-2026-1-01', N'Đang học', 7.0, 6.5, 6.0; -- 6.4 (C / 2.0)
EXEC #usp_ThemDangKyVaDiem 'SV019', 'OOP101-2026-1-01', N'Đang học', 7.5, 7.0, 7.0; -- 7.1 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV021', 'OOP101-2026-1-01', N'Đang học', 8.0, 7.0, 6.5; -- 7.0 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV023', 'OOP101-2026-1-01', N'Đang học', 8.0, 8.0, 7.5; -- 7.8 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV024', 'OOP101-2026-1-01', N'Đang học', 7.5, 7.0, 6.5; -- 6.9 (C+ / 2.5)

-- 9. LỚP SEC101-2026-1-01
EXEC #usp_ThemDangKyVaDiem 'SV06',  'SEC101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV010', 'SEC101-2026-1-01', N'Đang học', 9.0, 8.5, 9.0; -- 8.9 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV013', 'SEC101-2026-1-01', N'Đang học', 7.5, 7.0, 6.5; -- 6.9 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV014', 'SEC101-2026-1-01', N'Đang học', 5.0, 4.0, 3.5; -- 4.0 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV015', 'SEC101-2026-1-01', N'Đang học', 8.5, 8.5, 8.0; -- 8.3 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV018', 'SEC101-2026-1-01', N'Đang học', 8.0, 7.5, 7.0; -- 7.4 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV020', 'SEC101-2026-1-01', N'Đang học', 5.5, 4.0, 3.5; -- 4.1 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV022', 'SEC101-2026-1-01', N'Đang học', 4.5, 3.5, 3.0; -- 3.5 (F / 0.0) -> Học lại
EXEC #usp_ThemDangKyVaDiem 'SV023', 'SEC101-2026-1-01', N'Đang học', 8.5, 8.0, 8.5; -- 8.4 (B+ / 3.5)
EXEC #usp_ThemDangKyVaDiem 'SV024', 'SEC101-2026-1-01', N'Đang học', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)

-- 10. LỚP KỲ TRƯỚC: JAVA101-2025-2-01 (Kỳ 2025-2)
EXEC #usp_ThemDangKyVaDiem 'SV001', 'JAVA101-2025-2-01', N'Hoàn thành', 8.5, 8.5, 8.5; -- 8.5 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV002', 'JAVA101-2025-2-01', N'Hoàn thành', 7.5, 7.0, 7.0; -- 7.1 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV003', 'JAVA101-2025-2-01', N'Hoàn thành', 9.0, 9.0, 9.0; -- 9.0 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV005', 'JAVA101-2025-2-01', N'Hoàn thành', 5.0, 3.5, 3.0; -- 3.6 (F / 0.0)
EXEC #usp_ThemDangKyVaDiem 'SV009', 'JAVA101-2025-2-01', N'Hoàn thành', 6.0, 5.0, 4.0; -- 4.7 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV014', 'JAVA101-2025-2-01', N'Hoàn thành', 5.0, 3.5, 3.0; -- 3.6 (F / 0.0)
EXEC #usp_ThemDangKyVaDiem 'SV020', 'JAVA101-2025-2-01', N'Hoàn thành', 7.0, 6.0, 6.0; -- 6.2 (C+ / 2.5)
EXEC #usp_ThemDangKyVaDiem 'SV022', 'JAVA101-2025-2-01', N'Hoàn thành', 4.5, 3.0, 3.0; -- 3.3 (F / 0.0)

-- 11. LỚP KỲ TRƯỚC: DB101-2025-2-01 (Kỳ 2025-2)
EXEC #usp_ThemDangKyVaDiem 'SV001', 'DB101-2025-2-01', N'Hoàn thành', 9.0, 9.0, 9.0; -- 9.0 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV002', 'DB101-2025-2-01', N'Hoàn thành', 8.0, 7.5, 7.5; -- 7.6 (B / 3.0)
EXEC #usp_ThemDangKyVaDiem 'SV003', 'DB101-2025-2-01', N'Hoàn thành', 8.5, 8.5, 8.5; -- 8.5 (A / 4.0)
EXEC #usp_ThemDangKyVaDiem 'SV005', 'DB101-2025-2-01', N'Hoàn thành', 5.5, 4.0, 3.5; -- 4.1 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV009', 'DB101-2025-2-01', N'Hoàn thành', 6.5, 5.5, 5.0; -- 5.5 (C / 2.0)
EXEC #usp_ThemDangKyVaDiem 'SV014', 'DB101-2025-2-01', N'Hoàn thành', 5.0, 4.0, 3.5; -- 4.0 (D / 1.0)
EXEC #usp_ThemDangKyVaDiem 'SV020', 'DB101-2025-2-01', N'Hoàn thành', 6.5, 6.0, 6.0; -- 6.1 (C / 2.0)
EXEC #usp_ThemDangKyVaDiem 'SV022', 'DB101-2025-2-01', N'Hoàn thành', 4.0, 3.0, 3.0; -- 3.2 (F / 0.0)

-- 12. LỚP KỲ ĐANG HỌC: AI101-2026-2-01
EXEC #usp_ThemDangKyVaDiem 'SV001', 'AI101-2026-2-01', N'Đang học', 9.0, 8.5, NULL;
EXEC #usp_ThemDangKyVaDiem 'SV003', 'AI101-2026-2-01', N'Đang học', 9.5, 9.0, NULL;
EXEC #usp_ThemDangKyVaDiem 'SV007', 'AI101-2026-2-01', N'Đang học', 9.0, 8.5, NULL;
EXEC #usp_ThemDangKyVaDiem 'SV008', 'AI101-2026-2-01', N'Đang học', 8.0, 8.0, NULL;
EXEC #usp_ThemDangKyVaDiem 'SV010', 'AI101-2026-2-01', N'Đang học', 9.5, 9.0, NULL;

-- 13. LỚP KỲ ĐANG HỌC: OS101-2026-2-01
EXEC #usp_ThemDangKyVaDiem 'SV002', 'OS101-2026-2-01', N'Đang học', 8.0, 7.5, NULL;
EXEC #usp_ThemDangKyVaDiem 'SV004', 'OS101-2026-2-01', N'Đang học', 7.5, 7.0, NULL;
EXEC #usp_ThemDangKyVaDiem 'SV011', 'OS101-2026-2-01', N'Đang học', 8.5, 8.0, NULL;
EXEC #usp_ThemDangKyVaDiem 'SV012', 'OS101-2026-2-01', N'Đang học', 8.0, 7.5, NULL;
EXEC #usp_ThemDangKyVaDiem 'SV013', 'OS101-2026-2-01', N'Đang học', 7.5, 6.5, NULL;

DROP PROCEDURE #usp_ThemDangKyVaDiem;
GO

-- ----------------------------------------------------------------------------------------------------
-- 10. BẢNG LichSuDiem (8 bản ghi lịch sử thay đổi điểm rõ ràng, minh bạch)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 10. Nạp dữ liệu bảng LichSuDiem...';
DECLARE @BdOopSV2 INT, @BdNetSV10 INT, @BdJavaSV3 INT, @BdWebSV13 INT, @BdDbSV11 INT, @BdSecSV22 INT;

SELECT @BdOopSV2 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV002' AND lhp.MaLopHocPhan = 'OOP101-2026-1-01';
SELECT @BdNetSV10 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV010' AND lhp.MaLopHocPhan = 'NET101-2026-1-01';
SELECT @BdJavaSV3 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV003' AND lhp.MaLopHocPhan = 'JAVA101-2026-1-01';
SELECT @BdWebSV13 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV013' AND lhp.MaLopHocPhan = 'WEB101-2026-1-01';
SELECT @BdDbSV11  = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV011' AND lhp.MaLopHocPhan = 'DB101-2026-1-01';
SELECT @BdSecSV22 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV022' AND lhp.MaLopHocPhan = 'SEC101-2026-1-01';

IF @BdJavaSV3 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM LichSuDiem WHERE BangDiemID = @BdJavaSV3)
    INSERT INTO LichSuDiem (BangDiemID, DiemCCCu, DiemGKCu, DiemCKCu, DiemTongKetCu, DiemChuCu, DiemHe4Cu,
                           DiemCCMoi, DiemGKMoi, DiemCKMoi, DiemTongKetMoi, DiemChuMoi, DiemHe4Moi,
                           LyDo, NguoiThayDoiID, ThoiGianThayDoi)
    VALUES (@BdJavaSV3, 9.0, 8.5, 8.0, 8.4, 'B+', 3.5, 9.0, 8.5, 9.0, 8.9, 'A', 4.0,
            N'Cập nhật sau phúc khảo: Chấm lại câu 4 phần đa luồng, cộng thêm 1.0 điểm CK', 2, DATEADD(HOUR, -24, SYSDATETIME()));

IF @BdDbSV11 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM LichSuDiem WHERE BangDiemID = @BdDbSV11)
    INSERT INTO LichSuDiem (BangDiemID, DiemCCCu, DiemGKCu, DiemCKCu, DiemTongKetCu, DiemChuCu, DiemHe4Cu,
                           DiemCCMoi, DiemGKMoi, DiemCKMoi, DiemTongKetMoi, DiemChuMoi, DiemHe4Moi,
                           LyDo, NguoiThayDoiID, ThoiGianThayDoi)
    VALUES (@BdDbSV11, 8.5, 7.5, 8.0, 7.9, 'B', 3.0, 8.5, 8.0, 8.0, 8.1, 'B+', 3.5,
            N'Chấm phúc khảo bài thực hành SQL Trigger, cộng thêm 0.5 điểm GK', 2, DATEADD(HOUR, -48, SYSDATETIME()));

IF @BdOopSV2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM LichSuDiem WHERE BangDiemID = @BdOopSV2)
    INSERT INTO LichSuDiem (BangDiemID, DiemCCCu, DiemGKCu, DiemCKCu, DiemTongKetCu, DiemChuCu, DiemHe4Cu,
                           DiemCCMoi, DiemGKMoi, DiemCKMoi, DiemTongKetMoi, DiemChuMoi, DiemHe4Moi,
                           LyDo, NguoiThayDoiID, ThoiGianThayDoi)
    VALUES (@BdOopSV2, 7.5, 6.0, 6.0, 6.3, 'C', 2.0, 7.5, 7.0, 6.0, 6.6, 'C+', 2.5,
            N'Đính chính điểm giữa kỳ: Cộng bổ sung điểm bài tập lớn trên lớp', 1, DATEADD(DAY, -5, SYSDATETIME()));

IF @BdNetSV10 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM LichSuDiem WHERE BangDiemID = @BdNetSV10)
    INSERT INTO LichSuDiem (BangDiemID, DiemCCCu, DiemGKCu, DiemCKCu, DiemTongKetCu, DiemChuCu, DiemHe4Cu,
                           DiemCCMoi, DiemGKMoi, DiemCKMoi, DiemTongKetMoi, DiemChuMoi, DiemHe4Moi,
                           LyDo, NguoiThayDoiID, ThoiGianThayDoi)
    VALUES (@BdNetSV10, 8.0, 8.5, 8.5, 8.4, 'B+', 3.5, 9.0, 8.5, 8.5, 8.6, 'A', 4.0,
            N'Bổ sung điểm chuyên cần do đi học bù có phép được xác nhận', 3, DATEADD(DAY, -6, SYSDATETIME()));

IF @BdWebSV13 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM LichSuDiem WHERE BangDiemID = @BdWebSV13)
    INSERT INTO LichSuDiem (BangDiemID, DiemCCCu, DiemGKCu, DiemCKCu, DiemTongKetCu, DiemChuCu, DiemHe4Cu,
                           DiemCCMoi, DiemGKMoi, DiemCKMoi, DiemTongKetMoi, DiemChuMoi, DiemHe4Moi,
                           LyDo, NguoiThayDoiID, ThoiGianThayDoi)
    VALUES (@BdWebSV13, 7.5, 5.5, 6.5, 6.4, 'C', 2.0, 7.5, 6.5, 6.5, 6.7, 'C+', 2.5,
            N'Giảng viên chấm lại bài tập lớn nhóm phần thiết kế Database REST API', 1, DATEADD(DAY, -7, SYSDATETIME()));

IF @BdSecSV22 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM LichSuDiem WHERE BangDiemID = @BdSecSV22)
    INSERT INTO LichSuDiem (BangDiemID, DiemCCCu, DiemGKCu, DiemCKCu, DiemTongKetCu, DiemChuCu, DiemHe4Cu,
                           DiemCCMoi, DiemGKMoi, DiemCKMoi, DiemTongKetMoi, DiemChuMoi, DiemHe4Moi,
                           LyDo, NguoiThayDoiID, ThoiGianThayDoi)
    VALUES (@BdSecSV22, 4.0, 3.5, 3.0, 3.4, 'F', 0.0, 4.5, 3.5, 3.0, 3.5, 'F', 0.0,
            N'Cập nhật chuyên cần sau rà soát sổ điểm lớp học phần', 2, DATEADD(DAY, -8, SYSDATETIME()));
GO

-- ----------------------------------------------------------------------------------------------------
-- 11. BẢNG PhucKhao (8 yêu cầu phúc khảo bao quát 4 trạng thái theo nghiệp vụ)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 11. Nạp dữ liệu bảng PhucKhao...';
DECLARE @BdOopSV2 INT, @BdNetSV10 INT, @BdJavaSV3 INT, @BdWebSV13 INT;
DECLARE @BdDbSV11 INT, @BdJavaSV9 INT, @BdSeSV4 INT;

SELECT @BdOopSV2 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV002' AND lhp.MaLopHocPhan = 'OOP101-2026-1-01';
SELECT @BdNetSV10 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV010' AND lhp.MaLopHocPhan = 'NET101-2026-1-01';
SELECT @BdJavaSV3 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV003' AND lhp.MaLopHocPhan = 'JAVA101-2026-1-01';
SELECT @BdWebSV13 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV013' AND lhp.MaLopHocPhan = 'WEB101-2026-1-01';
SELECT @BdDbSV11  = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV011' AND lhp.MaLopHocPhan = 'DB101-2026-1-01';
SELECT @BdJavaSV9 = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV009' AND lhp.MaLopHocPhan = 'JAVA101-2026-1-01';
SELECT @BdSeSV4   = bd.BangDiemID FROM BangDiem bd INNER JOIN DangKyHocPhan dk ON bd.DangKyID = dk.DangKyID INNER JOIN SinhVien sv ON dk.SinhVienID = sv.SinhVienID INNER JOIN LopHocPhan lhp ON dk.LopHocPhanID = lhp.LopHocPhanID WHERE sv.MaSinhVien = 'SV004' AND lhp.MaLopHocPhan = 'SE101-2026-1-01';

DECLARE @Sv2 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV002');
DECLARE @Sv3 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV003');
DECLARE @Sv4 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV004');
DECLARE @Sv9 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV009');
DECLARE @Sv10 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV010');
DECLARE @Sv11 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV011');
DECLARE @Sv13 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV013');

-- 1. Chờ xử lý (SV002 - OOP101)
IF @BdOopSV2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM PhucKhao WHERE BangDiemID = @BdOopSV2)
    INSERT INTO PhucKhao (BangDiemID, SinhVienID, NgayGui, LyDo, TrangThai, KetQuaXuLy, NguoiXuLyID, NgayXuLy)
    VALUES (@BdOopSV2, @Sv2, DATEADD(DAY, -1, SYSDATETIME()), N'Em làm đủ các câu và chạy thử chương trình đều đúng, đề nghị thầy chấm lại bài thi cuối kỳ.', N'Chờ xử lý', NULL, NULL, NULL);

-- 2. Chờ xử lý (SV010 - NET101)
IF @BdNetSV10 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM PhucKhao WHERE BangDiemID = @BdNetSV10)
    INSERT INTO PhucKhao (BangDiemID, SinhVienID, NgayGui, LyDo, TrangThai, KetQuaXuLy, NguoiXuLyID, NgayXuLy)
    VALUES (@BdNetSV10, @Sv10, DATEADD(DAY, -2, SYSDATETIME()), N'Điểm thi cuối kỳ trắc nghiệm thấp hơn đối chiếu đáp án, xin xem lại phiếu trả lời trắc nghiệm.', N'Chờ xử lý', NULL, NULL, NULL);

-- 3. Đang xử lý (SV004 - SE101)
IF @BdSeSV4 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM PhucKhao WHERE BangDiemID = @BdSeSV4)
    INSERT INTO PhucKhao (BangDiemID, SinhVienID, NgayGui, LyDo, TrangThai, KetQuaXuLy, NguoiXuLyID, NgayXuLy)
    VALUES (@BdSeSV4, @Sv4, DATEADD(DAY, -4, SYSDATETIME()), N'Em xin xem lại bài thi tự luận phần thiết kế sơ đồ UML Câu 2.', N'Đang xử lý', N'Khoa đã tiếp nhận bài thi và đang giao 2 cán bộ chấm thi đối chiếu barem điểm.', 2, DATEADD(DAY, -2, SYSDATETIME()));

-- 4. Đang xử lý (SV013 - WEB101)
IF @BdWebSV13 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM PhucKhao WHERE BangDiemID = @BdWebSV13)
    INSERT INTO PhucKhao (BangDiemID, SinhVienID, NgayGui, LyDo, TrangThai, KetQuaXuLy, NguoiXuLyID, NgayXuLy)
    VALUES (@BdWebSV13, @Sv13, DATEADD(DAY, -5, SYSDATETIME()), N'Xin phúc khảo điểm bài tập lớn phần Backend REST API.', N'Đang xử lý', N'Giảng viên phụ trách học phần đang rà soát lại repository GitHub của nhóm.', 1, DATEADD(DAY, -3, SYSDATETIME()));

-- 5. Đã chấp nhận (SV003 - JAVA101)
IF @BdJavaSV3 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM PhucKhao WHERE BangDiemID = @BdJavaSV3)
    INSERT INTO PhucKhao (BangDiemID, SinhVienID, NgayGui, LyDo, TrangThai, KetQuaXuLy, NguoiXuLyID, NgayXuLy)
    VALUES (@BdJavaSV3, @Sv3, DATEADD(DAY, -7, SYSDATETIME()), N'Em nghi ngờ sót điểm câu 4 phần đa luồng (Multi-threading).', N'Đã chấp nhận', N'Đã chấm lại: Câu 4 đa luồng đúng logic đồng bộ, cộng 1.0 điểm CK.', 2, DATEADD(DAY, -1, SYSDATETIME()));

-- 6. Đã chấp nhận (SV011 - DB101)
IF @BdDbSV11 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM PhucKhao WHERE BangDiemID = @BdDbSV11)
    INSERT INTO PhucKhao (BangDiemID, SinhVienID, NgayGui, LyDo, TrangThai, KetQuaXuLy, NguoiXuLyID, NgayXuLy)
    VALUES (@BdDbSV11, @Sv11, DATEADD(DAY, -8, SYSDATETIME()), N'Xin chấm lại câu lệnh Trigger cập nhật số lượng tồn kho.', N'Đã chấp nhận', N'Đã chấm lại bài: Trigger xử lý đúng tình huống bảng inserted, cộng 0.5 điểm GK.', 2, DATEADD(DAY, -2, SYSDATETIME()));

-- 7. Không chấp nhận (SV009 - JAVA101)
IF @BdJavaSV9 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM PhucKhao WHERE BangDiemID = @BdJavaSV9)
    INSERT INTO PhucKhao (BangDiemID, SinhVienID, NgayGui, LyDo, TrangThai, KetQuaXuLy, NguoiXuLyID, NgayXuLy)
    VALUES (@BdJavaSV9, @Sv9, DATEADD(DAY, -9, SYSDATETIME()), N'Xin phúc khảo bài thi cuối kỳ môn Lập trình Java.', N'Không chấp nhận', N'Hội đồng chấm thi đã kiểm tra lại toàn bộ bài thi. Bài làm sai cú pháp lambda và ngoại lệ runtime, giữ nguyên điểm số 3.0.', 1, DATEADD(DAY, -4, SYSDATETIME()));
GO

-- ----------------------------------------------------------------------------------------------------
-- 12. BẢNG KetQuaHocTap (Điểm GPA học kỳ và CPA tích lũy chuẩn quy chế tín chỉ)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 12. Nạp dữ liệu bảng KetQuaHocTap...';

IF OBJECT_ID('tempdb..#usp_LuuKetQuaGPA') IS NOT NULL DROP PROCEDURE #usp_LuuKetQuaGPA;
GO

CREATE PROCEDURE #usp_LuuKetQuaGPA
    @MaSV VARCHAR(20),
    @HocKyID INT,
    @GPA_HK DECIMAL(3,2),
    @GPA_TL DECIMAL(3,2),
    @TC_DK INT,
    @TC_Dat INT,
    @TongTC_TL INT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @SVID INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = @MaSV);
    IF @SVID IS NULL RETURN;

    IF NOT EXISTS (SELECT 1 FROM KetQuaHocTap WHERE SinhVienID = @SVID AND HocKyID = @HocKyID)
    BEGIN
        INSERT INTO KetQuaHocTap (SinhVienID, HocKyID, GPAHocKy, GPATichLuy, SoTinChiDangKy, SoTinChiDat, TongTinChiTichLuy, NgayCapNhat)
        VALUES (@SVID, @HocKyID, @GPA_HK, @GPA_TL, @TC_DK, @TC_Dat, @TongTC_TL, SYSDATETIME());
    END
END;
GO

-- GPA Kỳ 2026-1 (Kỳ chính)
EXEC #usp_LuuKetQuaGPA 'SV002', 1, 2.79, 2.85, 14, 14, 20; -- Khá
EXEC #usp_LuuKetQuaGPA 'SV003', 1, 3.86, 3.90, 11, 11, 17; -- Xuất sắc
EXEC #usp_LuuKetQuaGPA 'SV004', 1, 2.50, 2.55, 12, 12, 18; -- Khá
EXEC #usp_LuuKetQuaGPA 'SV005', 1, 0.67, 0.85, 9, 6, 12;   -- Kém (Vi phạm tiêu chí GPA < 1.0)
EXEC #usp_LuuKetQuaGPA 'SV06',  1, 3.38, 3.38, 12, 12, 12; -- Giỏi
EXEC #usp_LuuKetQuaGPA 'SV007', 1, 3.80, 3.80, 15, 15, 15; -- Xuất sắc
EXEC #usp_LuuKetQuaGPA 'SV008', 1, 3.20, 3.20, 12, 12, 12; -- Giỏi
EXEC #usp_LuuKetQuaGPA 'SV009', 1, 0.27, 0.75, 11, 3, 9;   -- Kém (Nợ 8 tín chỉ, GPA < 1.0)
EXEC #usp_LuuKetQuaGPA 'SV010', 1, 3.88, 3.88, 12, 12, 12; -- Xuất sắc
EXEC #usp_LuuKetQuaGPA 'SV011', 1, 3.38, 3.38, 12, 12, 12; -- Giỏi
EXEC #usp_LuuKetQuaGPA 'SV012', 1, 3.17, 3.17, 9, 9, 9;    -- Khá
EXEC #usp_LuuKetQuaGPA 'SV013', 1, 2.75, 2.75, 12, 12, 12; -- Khá
EXEC #usp_LuuKetQuaGPA 'SV014', 1, 0.50, 0.80, 12, 3, 9;   -- Kém (Nợ 9 tín chỉ)
EXEC #usp_LuuKetQuaGPA 'SV015', 1, 3.88, 3.88, 12, 12, 12; -- Xuất sắc
EXEC #usp_LuuKetQuaGPA 'SV016', 1, 3.13, 3.13, 11, 11, 11; -- Khá
EXEC #usp_LuuKetQuaGPA 'SV017', 1, 2.38, 2.38, 11, 11, 11; -- Trung bình
EXEC #usp_LuuKetQuaGPA 'SV018', 1, 2.91, 2.91, 11, 11, 11; -- Khá
EXEC #usp_LuuKetQuaGPA 'SV019', 1, 2.67, 2.67, 9, 9, 9;    -- Khá
EXEC #usp_LuuKetQuaGPA 'SV020', 1, 1.33, 1.80, 9, 6, 12;   -- Yếu (Nợ 3 tín chỉ)
EXEC #usp_LuuKetQuaGPA 'SV021', 1, 2.40, 2.40, 5, 5, 5;    -- Trung bình
EXEC #usp_LuuKetQuaGPA 'SV022', 1, 0.00, 0.65, 9, 0, 6;    -- Kém (Nợ toàn bộ 9 tín chỉ)
EXEC #usp_LuuKetQuaGPA 'SV023', 1, 3.17, 3.17, 9, 9, 9;    -- Khá
EXEC #usp_LuuKetQuaGPA 'SV024', 1, 2.67, 2.67, 9, 9, 9;    -- Khá

-- GPA Kỳ 2025-2 (Kỳ quá khứ)
DECLARE @HkPastID INT = (SELECT HocKyID FROM HocKy WHERE MaHocKy = '2025-2');
IF @HkPastID IS NOT NULL
BEGIN
    EXEC #usp_LuuKetQuaGPA 'SV001', @HkPastID, 4.00, 4.00, 6, 6, 6;
    EXEC #usp_LuuKetQuaGPA 'SV002', @HkPastID, 3.00, 3.00, 6, 6, 6;
    EXEC #usp_LuuKetQuaGPA 'SV003', @HkPastID, 4.00, 4.00, 6, 6, 6;
    EXEC #usp_LuuKetQuaGPA 'SV005', @HkPastID, 0.50, 0.50, 6, 3, 3;
    EXEC #usp_LuuKetQuaGPA 'SV009', @HkPastID, 1.50, 1.50, 6, 6, 6;
    EXEC #usp_LuuKetQuaGPA 'SV014', @HkPastID, 0.50, 0.50, 6, 3, 3;
    EXEC #usp_LuuKetQuaGPA 'SV020', @HkPastID, 2.25, 2.25, 6, 6, 6;
    EXEC #usp_LuuKetQuaGPA 'SV022', @HkPastID, 0.00, 0.00, 6, 0, 0;
END

DROP PROCEDURE #usp_LuuKetQuaGPA;
GO

-- ----------------------------------------------------------------------------------------------------
-- 13. BẢNG TieuChiCanhCao (Tiêu chí cảnh báo học vụ chính thức theo quy chế UTT)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 13. Nạp dữ liệu bảng TieuChiCanhCao...';
IF NOT EXISTS (SELECT 1 FROM TieuChiCanhCao WHERE MaTieuChi = 'CPA_TICH_LUY_THAP')
    INSERT INTO TieuChiCanhCao (MaTieuChi, TenTieuChi, LoaiCanhCao, MoTa, GPAHocKyToiDa, GPATichLuyToiDa, SoTinChiNoToiThieu, SoTinChiDatToiDa, NgayApDung, NgayKetThuc, TrangThai)
    VALUES ('CPA_TICH_LUY_THAP', N'CPA tích lũy dưới ngưỡng quy định', N'Cảnh báo theo CPA', N'Điểm trung bình chung tích lũy CPA dưới 1.20 (năm thứ nhất) hoặc dưới 1.40 (năm thứ hai)', NULL, 1.20, NULL, NULL, '2025-09-01', NULL, 1);

IF NOT EXISTS (SELECT 1 FROM TieuChiCanhCao WHERE MaTieuChi = 'NO_TIN_CHI_TICH_LUY')
    INSERT INTO TieuChiCanhCao (MaTieuChi, TenTieuChi, LoaiCanhCao, MoTa, GPAHocKyToiDa, GPATichLuyToiDa, SoTinChiNoToiThieu, SoTinChiDatToiDa, NgayApDung, NgayKetThuc, TrangThai)
    VALUES ('NO_TIN_CHI_TICH_LUY', N'Nợ tín chỉ tích lũy quá nhiều', N'Cảnh báo theo số tín chỉ nợ', N'Tổng số tín chỉ nợ tích lũy vượt quá 16 tín chỉ theo quy định của nhà trường', NULL, NULL, 16, NULL, '2025-09-01', NULL, 1);

UPDATE TieuChiCanhCao SET TenTieuChi = N'GPA học kỳ thấp', MoTa = N'GPA học kỳ dưới 1.00 theo quy chế đào tạo' WHERE MaTieuChi = 'GPA_HK_THAP';
UPDATE TieuChiCanhCao SET TenTieuChi = N'Nợ tín chỉ trong kỳ', MoTa = N'Nợ từ 5 tín chỉ không đạt trở lên trong học kỳ' WHERE MaTieuChi = 'TIN_CHI_NO';
UPDATE TieuChiCanhCao SET TenTieuChi = N'CPA tích lũy dưới ngưỡng quy định', MoTa = N'Điểm trung bình chung tích lũy CPA dưới 1.20 (năm thứ nhất) hoặc dưới 1.40 (năm thứ hai)' WHERE MaTieuChi = 'CPA_TICH_LUY_THAP';
UPDATE TieuChiCanhCao SET TenTieuChi = N'Nợ tín chỉ tích lũy quá nhiều', MoTa = N'Tổng số tín chỉ nợ tích lũy vượt quá 16 tín chỉ theo quy định của nhà trường' WHERE MaTieuChi = 'NO_TIN_CHI_TICH_LUY';
GO

-- ----------------------------------------------------------------------------------------------------
-- 14. BẢNG CanhCaoHocTap (Cảnh báo học tập Mức 1, Mức 2, Mức 3)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 14. Nạp dữ liệu bảng CanhCaoHocTap...';
DECLARE @HkCurr INT = (SELECT HocKyID FROM HocKy WHERE MaHocKy = '2026-1');
DECLARE @HkPrev INT = (SELECT HocKyID FROM HocKy WHERE MaHocKy = '2025-2');

DECLARE @TcGpaHK INT = (SELECT TieuChiID FROM TieuChiCanhCao WHERE MaTieuChi = 'GPA_HK_THAP');
DECLARE @TcTinChi INT = (SELECT TieuChiID FROM TieuChiCanhCao WHERE MaTieuChi = 'TIN_CHI_NO');

DECLARE @Sv5 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV005');
DECLARE @Sv9 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV009');
DECLARE @Sv14 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV014');
DECLARE @Sv20 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV020');
DECLARE @Sv22 INT = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV022');

-- SV005: Cảnh báo Mức 1 kỳ 2026-1 (GPA đạt 0.67 < 1.0)
IF @Sv5 IS NOT NULL AND @HkCurr IS NOT NULL AND @TcGpaHK IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM CanhCaoHocTap WHERE SinhVienID = @Sv5 AND HocKyID = @HkCurr AND TieuChiID = @TcGpaHK)
    INSERT INTO CanhCaoHocTap (SinhVienID, HocKyID, TieuChiID, NgayXet, LyDo, MucCanhCao, TrangThai, NguoiXetID, GhiChu)
    VALUES (@Sv5, @HkCurr, @TcGpaHK, DATEADD(DAY, -10, SYSDATETIME()), N'GPA học kỳ đạt 0.67 (dưới ngưỡng tối thiểu 1.00 theo quy chế)', N'Cảnh báo mức 1', N'Đang áp dụng', 1, N'Cố vấn học tập cần liên hệ gia đình để phối hợp động viên');

-- SV009: Cảnh báo Mức 1 kỳ 2026-1 (Nợ 8 tín chỉ trong kỳ)
IF @Sv9 IS NOT NULL AND @HkCurr IS NOT NULL AND @TcTinChi IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM CanhCaoHocTap WHERE SinhVienID = @Sv9 AND HocKyID = @HkCurr AND TieuChiID = @TcTinChi)
    INSERT INTO CanhCaoHocTap (SinhVienID, HocKyID, TieuChiID, NgayXet, LyDo, MucCanhCao, TrangThai, NguoiXetID, GhiChu)
    VALUES (@Sv9, @HkCurr, @TcTinChi, DATEADD(DAY, -10, SYSDATETIME()), N'Nợ 8 tín chỉ không đạt trong kỳ (ngưỡng cảnh báo từ 5 tín chỉ)', N'Cảnh báo mức 1', N'Đang áp dụng', 1, N'Yêu cầu đăng ký học lại các học phần JAVA101, IS101, WEB101');

-- SV014: Cảnh báo Mức 2 kỳ 2026-1 (Lần 2 liên tiếp bị cảnh báo)
IF @Sv14 IS NOT NULL AND @HkCurr IS NOT NULL AND @TcGpaHK IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM CanhCaoHocTap WHERE SinhVienID = @Sv14 AND HocKyID = @HkCurr AND TieuChiID = @TcGpaHK)
    INSERT INTO CanhCaoHocTap (SinhVienID, HocKyID, TieuChiID, NgayXet, LyDo, MucCanhCao, TrangThai, NguoiXetID, GhiChu)
    VALUES (@Sv14, @HkCurr, @TcGpaHK, DATEADD(DAY, -10, SYSDATETIME()), N'GPA học kỳ đạt 0.50 (lần thứ 2 liên tiếp bị cảnh báo học tập)', N'Cảnh báo mức 2', N'Đang áp dụng', 1, N'Giới hạn khối lượng học tập tối đa không quá 14 tín chỉ trong kỳ tiếp theo');

-- SV020: Cảnh báo Mức 1 kỳ 2026-1 (Đã khắc phục)
IF @Sv20 IS NOT NULL AND @HkCurr IS NOT NULL AND @TcTinChi IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM CanhCaoHocTap WHERE SinhVienID = @Sv20 AND HocKyID = @HkCurr AND TieuChiID = @TcTinChi)
    INSERT INTO CanhCaoHocTap (SinhVienID, HocKyID, TieuChiID, NgayXet, LyDo, MucCanhCao, TrangThai, NguoiXetID, GhiChu)
    VALUES (@Sv20, @HkCurr, @TcTinChi, DATEADD(DAY, -10, SYSDATETIME()), N'Nợ 3 tín chỉ học phần SE101 và GPA cải thiện', N'Cảnh báo mức 1', N'Đã khắc phục', 1, N'Sinh viên đã nỗ lực cải thiện điểm và tham gia đầy đủ các giờ phụ đạo');

-- SV022: Cảnh báo Mức 3 (Buộc thôi học) kỳ 2026-1 (3 kỳ liên tiếp vi phạm)
IF @Sv22 IS NOT NULL AND @HkCurr IS NOT NULL AND @TcGpaHK IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM CanhCaoHocTap WHERE SinhVienID = @Sv22 AND HocKyID = @HkCurr AND TieuChiID = @TcGpaHK)
    INSERT INTO CanhCaoHocTap (SinhVienID, HocKyID, TieuChiID, NgayXet, LyDo, MucCanhCao, TrangThai, NguoiXetID, GhiChu)
    VALUES (@Sv22, @HkCurr, @TcGpaHK, DATEADD(DAY, -10, SYSDATETIME()), N'GPA học kỳ đạt 0.00, nợ toàn bộ học phần trong 2 kỳ liên tiếp', N'Cảnh báo mức 3 (Buộc thôi học)', N'Đang áp dụng', 1, N'Chuyển hồ sơ sang Hội đồng khen thưởng & kỷ luật nhà trường xem xét quyết định');

-- Dữ liệu kỳ quá khứ 2025-2
IF @Sv5 IS NOT NULL AND @HkPrev IS NOT NULL AND @TcGpaHK IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM CanhCaoHocTap WHERE SinhVienID = @Sv5 AND HocKyID = @HkPrev AND TieuChiID = @TcGpaHK)
    INSERT INTO CanhCaoHocTap (SinhVienID, HocKyID, TieuChiID, NgayXet, LyDo, MucCanhCao, TrangThai, NguoiXetID, GhiChu)
    VALUES (@Sv5, @HkPrev, @TcGpaHK, '2026-06-20', N'GPA học kỳ 2 năm học 2025-2026 đạt 0.50', N'Cảnh báo mức 1', N'Đã khắc phục', 1, N'Đã thông báo đến sinh viên');

IF @Sv14 IS NOT NULL AND @HkPrev IS NOT NULL AND @TcGpaHK IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM CanhCaoHocTap WHERE SinhVienID = @Sv14 AND HocKyID = @HkPrev AND TieuChiID = @TcGpaHK)
    INSERT INTO CanhCaoHocTap (SinhVienID, HocKyID, TieuChiID, NgayXet, LyDo, MucCanhCao, TrangThai, NguoiXetID, GhiChu)
    VALUES (@Sv14, @HkPrev, @TcGpaHK, '2026-06-20', N'GPA học kỳ 2 năm học 2025-2026 đạt 0.50', N'Cảnh báo mức 1', N'Đang áp dụng', 1, N'Đã nhắc nhở lần 1');
GO

-- ----------------------------------------------------------------------------------------------------
-- 15. BẢNG QuyetDinhCanhCao (Quyết định xử lý học vụ chính thức)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 15. Nạp dữ liệu bảng QuyetDinhCanhCao...';
DECLARE @Cc5 INT, @Cc9 INT, @Cc14 INT, @Cc22 INT;
SELECT TOP 1 @Cc5 = CanhCaoID FROM CanhCaoHocTap WHERE SinhVienID = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV005') AND MucCanhCao = N'Cảnh báo mức 1' ORDER BY CanhCaoID DESC;
SELECT TOP 1 @Cc9 = CanhCaoID FROM CanhCaoHocTap WHERE SinhVienID = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV009') ORDER BY CanhCaoID DESC;
SELECT TOP 1 @Cc14 = CanhCaoID FROM CanhCaoHocTap WHERE SinhVienID = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV014') AND MucCanhCao = N'Cảnh báo mức 2' ORDER BY CanhCaoID DESC;
SELECT TOP 1 @Cc22 = CanhCaoID FROM CanhCaoHocTap WHERE SinhVienID = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV022') ORDER BY CanhCaoID DESC;

IF @Cc5 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM QuyetDinhCanhCao WHERE SoQuyetDinh = 'QD-CBHT-2026/01/UTT')
    INSERT INTO QuyetDinhCanhCao (CanhCaoID, SoQuyetDinh, NgayQuyetDinh, NoiDung, NguoiKyID, TrangThai, TepDinhKem)
    VALUES (@Cc5, 'QD-CBHT-2026/01/UTT', CAST(GETDATE() AS DATE), N'Quyết định cảnh báo học tập Mức 1 học kỳ 1 năm học 2026-2027 đối với sinh viên Vũ Khánh Linh (SV005)', 1, N'Đã ban hành', 'QD_CBHT_SV005_2026.pdf');

IF @Cc9 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM QuyetDinhCanhCao WHERE SoQuyetDinh = 'QD-CBHT-2026/02/UTT')
    INSERT INTO QuyetDinhCanhCao (CanhCaoID, SoQuyetDinh, NgayQuyetDinh, NoiDung, NguoiKyID, TrangThai, TepDinhKem)
    VALUES (@Cc9, 'QD-CBHT-2026/02/UTT', CAST(GETDATE() AS DATE), N'Quyết định cảnh báo học tập Mức 1 học kỳ 1 năm học 2026-2027 đối với sinh viên Đinh Quang Hải (SV009) do nợ 8 tín chỉ', 1, N'Đã ban hành', 'QD_CBHT_SV009_2026.pdf');

IF @Cc14 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM QuyetDinhCanhCao WHERE SoQuyetDinh = 'QD-CBHT-2026/03/UTT')
    INSERT INTO QuyetDinhCanhCao (CanhCaoID, SoQuyetDinh, NgayQuyetDinh, NoiDung, NguoiKyID, TrangThai, TepDinhKem)
    VALUES (@Cc14, 'QD-CBHT-2026/03/UTT', CAST(GETDATE() AS DATE), N'Quyết định cảnh báo học tập Mức 2 học kỳ 1 năm học 2026-2027 đối với sinh viên Ngô Bảo Châu (SV014)', 1, N'Đã ban hành', 'QD_CBHT_SV014_2026.pdf');

IF @Cc22 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM QuyetDinhCanhCao WHERE SoQuyetDinh = 'QD-BTH-2026/01/UTT')
    INSERT INTO QuyetDinhCanhCao (CanhCaoID, SoQuyetDinh, NgayQuyetDinh, NoiDung, NguoiKyID, TrangThai, TepDinhKem)
    VALUES (@Cc22, 'QD-BTH-2026/01/UTT', CAST(GETDATE() AS DATE), N'Quyết định xử lý học vụ: Buộc thôi học đối với sinh viên Đào Quốc Việt (SV022) do vi phạm cảnh báo học tập liên tiếp 3 kỳ', 1, N'Đã ban hành', 'QD_BTH_SV022_2026.pdf');
GO

-- ----------------------------------------------------------------------------------------------------
-- 16. BẢNG TheoDoiSauCanhCao (Theo dõi chuyển biến học tập của sinh viên sau cảnh báo)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 16. Nạp dữ liệu bảng TheoDoiSauCanhCao...';
DECLARE @Cc5 INT, @Cc9 INT, @Cc14 INT, @Cc20 INT;
SELECT TOP 1 @Cc5 = CanhCaoID FROM CanhCaoHocTap WHERE SinhVienID = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV005') AND MucCanhCao = N'Cảnh báo mức 1' ORDER BY CanhCaoID DESC;
SELECT TOP 1 @Cc9 = CanhCaoID FROM CanhCaoHocTap WHERE SinhVienID = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV009') ORDER BY CanhCaoID DESC;
SELECT TOP 1 @Cc14 = CanhCaoID FROM CanhCaoHocTap WHERE SinhVienID = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV014') AND MucCanhCao = N'Cảnh báo mức 2' ORDER BY CanhCaoID DESC;
SELECT TOP 1 @Cc20 = CanhCaoID FROM CanhCaoHocTap WHERE SinhVienID = (SELECT SinhVienID FROM SinhVien WHERE MaSinhVien = 'SV020') ORDER BY CanhCaoID DESC;

DECLARE @HkTheoDoi INT = (SELECT HocKyID FROM HocKy WHERE MaHocKy = '2026-2');

IF @Cc5 IS NOT NULL AND @HkTheoDoi IS NOT NULL AND NOT EXISTS (SELECT 1 FROM TheoDoiSauCanhCao WHERE CanhCaoID = @Cc5 AND HocKyTheoDoiID = @HkTheoDoi)
    INSERT INTO TheoDoiSauCanhCao (CanhCaoID, HocKyTheoDoiID, NgayTheoDoi, KetQuaTheoDoi, GPAHocKy, GPATichLuy, SoTinChiDat, TinhTrang, NguoiTheoDoiID, GhiChu)
    VALUES (@Cc5, @HkTheoDoi, CAST(GETDATE() AS DATE), N'Sinh viên đã đăng ký học lại môn Cơ sở dữ liệu và đi học đầy đủ các buổi phụ đạo', 2.10, 1.35, 12, N'Có tiến bộ', 2, N'Cố vấn học tập GV001 tiếp tục giám sát chuyên cần');

IF @Cc9 IS NOT NULL AND @HkTheoDoi IS NOT NULL AND NOT EXISTS (SELECT 1 FROM TheoDoiSauCanhCao WHERE CanhCaoID = @Cc9 AND HocKyTheoDoiID = @HkTheoDoi)
    INSERT INTO TheoDoiSauCanhCao (CanhCaoID, HocKyTheoDoiID, NgayTheoDoi, KetQuaTheoDoi, GPAHocKy, GPATichLuy, SoTinChiDat, TinhTrang, NguoiTheoDoiID, GhiChu)
    VALUES (@Cc9, @HkTheoDoi, CAST(GETDATE() AS DATE), N'Sinh viên đã giảm bớt khối lượng làm thêm, tham gia nhóm học tập trên thư viện', 1.85, 1.10, 9, N'Có tiến bộ', 2, N'Đã liên hệ phụ huynh sinh viên trao đổi định kỳ');

IF @Cc14 IS NOT NULL AND @HkTheoDoi IS NOT NULL AND NOT EXISTS (SELECT 1 FROM TheoDoiSauCanhCao WHERE CanhCaoID = @Cc14 AND HocKyTheoDoiID = @HkTheoDoi)
    INSERT INTO TheoDoiSauCanhCao (CanhCaoID, HocKyTheoDoiID, NgayTheoDoi, KetQuaTheoDoi, GPAHocKy, GPATichLuy, SoTinChiDat, TinhTrang, NguoiTheoDoiID, GhiChu)
    VALUES (@Cc14, @HkTheoDoi, CAST(GETDATE() AS DATE), N'Sinh viên còn vắng một số buổi lý thuyết, tinh thần học tập chưa thật sự ổn định', 1.20, 0.95, 6, N'Cần nhắc nhở', 3, N'Đề nghị khoa hẹn gặp trực tiếp sinh viên và phụ huynh');

IF @Cc20 IS NOT NULL AND @HkTheoDoi IS NOT NULL AND NOT EXISTS (SELECT 1 FROM TheoDoiSauCanhCao WHERE CanhCaoID = @Cc20 AND HocKyTheoDoiID = @HkTheoDoi)
    INSERT INTO TheoDoiSauCanhCao (CanhCaoID, HocKyTheoDoiID, NgayTheoDoi, KetQuaTheoDoi, GPAHocKy, GPATichLuy, SoTinChiDat, TinhTrang, NguoiTheoDoiID, GhiChu)
    VALUES (@Cc20, @HkTheoDoi, CAST(GETDATE() AS DATE), N'Sinh viên hoàn thành xuất sắc các môn trả nợ, nâng điểm CPA đạt yêu cầu', 2.80, 2.15, 15, N'Đã khắc phục hoàn toàn', 1, N'Đủ điều kiện xóa tên khỏi danh sách cảnh báo học vụ');
GO

-- ----------------------------------------------------------------------------------------------------
-- 17. BẢNG ThongBao (Thông báo học vụ, thông báo kết quả phúc khảo và cảnh báo sinh viên)
-- ----------------------------------------------------------------------------------------------------
PRINT N'-> 17. Nạp dữ liệu bảng ThongBao...';
DECLARE @TkAdmin INT = (SELECT TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'admin');
DECLARE @TkSv2 INT   = (SELECT TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv002');
DECLARE @TkSv3 INT   = (SELECT TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv003');
DECLARE @TkSv5 INT   = (SELECT TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv005');
DECLARE @TkSv9 INT   = (SELECT TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv009');
DECLARE @TkSv14 INT  = (SELECT TaiKhoanID FROM TaiKhoan WHERE TenDangNhap = 'sv014');

-- Thông báo chung cho Admin
IF @TkAdmin IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ThongBao WHERE TaiKhoanID = @TkAdmin AND TieuDe LIKE N'%Kế hoạch rà soát%')
    INSERT INTO ThongBao (TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc, ThoiGianDoc)
    VALUES (@TkAdmin, N'Kế hoạch rà soát kết quả học tập & Cảnh báo học kỳ 1', N'Phòng Đào tạo thông báo: Đã hoàn tất việc tổng hợp điểm thi kết thúc học phần và xét duyệt danh sách cảnh báo học vụ đợt 1 năm học 2026-2027.', N'Hệ thống', DATEADD(DAY, -3, SYSDATETIME()), 1, DATEADD(DAY, -2, SYSDATETIME()));

-- Thông báo phúc khảo cho SV002
IF @TkSv2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ThongBao WHERE TaiKhoanID = @TkSv2 AND TieuDe LIKE N'%Tiếp nhận đơn phúc khảo%')
    INSERT INTO ThongBao (TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc, ThoiGianDoc)
    VALUES (@TkSv2, N'Tiếp nhận đơn phúc khảo môn Lập trình hướng đối tượng', N'Nhà trường đã tiếp nhận đơn phúc khảo môn OOP101 của bạn. Dự kiến kết quả sẽ được công bố trong vòng 7 ngày làm việc.', N'Phúc khảo', DATEADD(DAY, -1, SYSDATETIME()), 0, NULL);

-- Thông báo kết quả phúc khảo thành công cho SV003
IF @TkSv3 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ThongBao WHERE TaiKhoanID = @TkSv3 AND TieuDe LIKE N'%Kết quả phúc khảo môn: Lập trình Java%')
    INSERT INTO ThongBao (TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc, ThoiGianDoc)
    VALUES (@TkSv3, N'Kết quả phúc khảo môn: Lập trình Java', N'Đơn phúc khảo môn Lập trình Java của bạn đã được chấp nhận.\nĐiểm cuối kỳ mới: 9.0 (Cộng 1.0 điểm câu hỏi đa luồng). Bảng điểm của bạn đã được cập nhật.', N'Phúc khảo', DATEADD(DAY, -1, SYSDATETIME()), 1, DATEADD(HOUR, -12, SYSDATETIME()));

-- Thông báo cảnh báo cho SV005
IF @TkSv5 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ThongBao WHERE TaiKhoanID = @TkSv5 AND TieuDe LIKE N'%CẢNH BÁO HỌC TẬP: Cảnh báo mức 1%')
    INSERT INTO ThongBao (TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc, ThoiGianDoc)
    VALUES (@TkSv5, N'CẢNH BÁO HỌC TẬP: Cảnh báo mức 1', N'Nhà trường thông báo: Bạn bị Cảnh báo học tập Mức 1 do GPA học kỳ 1 đạt 0.67 (dưới ngưỡng tối thiểu 1.00).\nVui lòng liên hệ Cố vấn học tập để xây dựng kế hoạch cải thiện điểm.', N'Cảnh báo học tập', DATEADD(DAY, -10, SYSDATETIME()), 0, NULL);

-- Thông báo cảnh báo cho SV009
IF @TkSv9 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ThongBao WHERE TaiKhoanID = @TkSv9 AND TieuDe LIKE N'%CẢNH BÁO HỌC TẬP: Cảnh báo mức 1 (Nợ tín chỉ)%')
    INSERT INTO ThongBao (TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc, ThoiGianDoc)
    VALUES (@TkSv9, N'CẢNH BÁO HỌC TẬP: Cảnh báo mức 1 (Nợ tín chỉ)', N'Nhà trường thông báo: Bạn bị Cảnh báo học tập Mức 1 do nợ 8 tín chỉ không đạt trong kỳ.\nVui lòng chú ý đăng ký học lại sớm nhất trong kỳ 2.', N'Cảnh báo học tập', DATEADD(DAY, -10, SYSDATETIME()), 0, NULL);

-- Thông báo cảnh báo Mức 2 cho SV014
IF @TkSv14 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ThongBao WHERE TaiKhoanID = @TkSv14 AND TieuDe LIKE N'%CẢNH BÁO HỌC TẬP: Cảnh báo mức 2%')
    INSERT INTO ThongBao (TaiKhoanID, TieuDe, NoiDung, LoaiThongBao, ThoiGianTao, DaDoc, ThoiGianDoc)
    VALUES (@TkSv14, N'CẢNH BÁO HỌC TẬP: Cảnh báo mức 2 (Nghiêm trọng)', N'Nhà trường thông báo: Bạn bị Cảnh báo học tập Mức 2 do 2 kỳ liên tiếp có kết quả học tập không đạt yêu cầu.\nKhối lượng học tập kỳ tiếp theo của bạn bị giới hạn tối đa 14 tín chỉ.', N'Cảnh báo học tập', DATEADD(DAY, -10, SYSDATETIME()), 0, NULL);
GO

-- ----------------------------------------------------------------------------------------------------
-- KIỂM TRA TỔNG QUAN VÀ BÁO CÁO THỐNG KÊ SỐ LƯỢNG BẢN GHI TRÊN TOÀN BỘ 17 BẢNG
-- ----------------------------------------------------------------------------------------------------
PRINT N'========================================================================';
PRINT N'HOÀN TẤT NẠP DỮ LIỆU! THỐNG KÊ SỐ LƯỢNG BẢN GHI TRÊN CẢ 17 BẢNG:';
PRINT N'========================================================================';

SELECT '1. TaiKhoan' AS TenBang, COUNT(*) AS SoLuongBanGhi FROM TaiKhoan
UNION ALL
SELECT '2. Lop', COUNT(*) FROM Lop
UNION ALL
SELECT '3. GiangVien', COUNT(*) FROM GiangVien
UNION ALL
SELECT '4. SinhVien (Tong cong)', COUNT(*) FROM SinhVien
UNION ALL
SELECT '   - SinhVien (Co TaiKhoan)', COUNT(*) FROM SinhVien WHERE TaiKhoanID IS NOT NULL
UNION ALL
SELECT '   - SinhVien (Chua co TK - Cho test dang ky)', COUNT(*) FROM SinhVien WHERE TaiKhoanID IS NULL
UNION ALL
SELECT '5. MonHoc', COUNT(*) FROM MonHoc
UNION ALL
SELECT '6. HocKy', COUNT(*) FROM HocKy
UNION ALL
SELECT '7. LopHocPhan', COUNT(*) FROM LopHocPhan
UNION ALL
SELECT '8. DangKyHocPhan', COUNT(*) FROM DangKyHocPhan
UNION ALL
SELECT '9. BangDiem', COUNT(*) FROM BangDiem
UNION ALL
SELECT '10. LichSuDiem', COUNT(*) FROM LichSuDiem
UNION ALL
SELECT '11. PhucKhao', COUNT(*) FROM PhucKhao
UNION ALL
SELECT '12. KetQuaHocTap', COUNT(*) FROM KetQuaHocTap
UNION ALL
SELECT '13. TieuChiCanhCao', COUNT(*) FROM TieuChiCanhCao
UNION ALL
SELECT '14. CanhCaoHocTap', COUNT(*) FROM CanhCaoHocTap
UNION ALL
SELECT '15. QuyetDinhCanhCao', COUNT(*) FROM QuyetDinhCanhCao
UNION ALL
SELECT '16. TheoDoiSauCanhCao', COUNT(*) FROM TheoDoiSauCanhCao
UNION ALL
SELECT '17. ThongBao', COUNT(*) FROM ThongBao;
GO

