-- ====================================================================================================
-- TỆP 01: KIỂM TRA TÌNH TRẠNG MÃ HÓA UNICODE TRONG DATABASE (CHỈ ĐỌC - READ ONLY)
-- Database: duanjavanangcao
-- Mục đích: Kiểm tra các cột, collation và phát hiện chính xác các bản ghi chứa ký tự lỗi Mojibake.
-- Tuyệt đối không thay đổi, thêm, sửa hoặc xóa bất kỳ dữ liệu nào.
-- ====================================================================================================

USE duanjavanangcao;
GO

PRINT N'========================================================================';
PRINT N'1. KIỂM TRA THÔNG TIN COLLATION CỦA DATABASE VÀ CÁC CỘT CHỨA CHUỖI';
PRINT N'========================================================================';

-- 1.1 Collation của Database
SELECT 
    name AS DatabaseName, 
    collation_name AS DatabaseCollation
FROM sys.databases 
WHERE name = 'duanjavanangcao';

-- 1.2 Kiểu dữ liệu của các cột văn bản trong bảng SinhVien, GiangVien, Lop
SELECT 
    TABLE_NAME, 
    COLUMN_NAME, 
    DATA_TYPE, 
    CHARACTER_MAXIMUM_LENGTH, 
    COLLATION_NAME
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('SinhVien', 'GiangVien', 'Lop', 'MonHoc', 'TaiKhoan')
  AND DATA_TYPE IN ('nvarchar', 'varchar', 'nchar', 'char')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
GO

PRINT N'========================================================================';
PRINT N'2. TRUY VẤN CÁC BẢN GHI CÓ DẤU HIỆU LỖI MÃ HÓA MOJIBAKE (UTF-8 AS CP1252)';
PRINT N'========================================================================';

-- 2.1 Các sinh viên có Địa chỉ hoặc Trạng thái bị lỗi hiển thị (ví dụ: ThÃ¡i BÃ¬nh, Â, Ã, ...)
PRINT N'--- Danh sách Sinh viên bị lỗi mã hóa ---';
SELECT 
    SinhVienID, 
    MaSinhVien, 
    HoTen, 
    GioiTinh, 
    DiaChi, 
    TrangThai
FROM SinhVien
WHERE DiaChi LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%' 
   OR TrangThai LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%'
ORDER BY MaSinhVien;

-- 2.2 Các giảng viên có Khoa bị lỗi hiển thị
PRINT N'--- Danh sách Giảng viên bị lỗi mã hóa ---';
SELECT 
    GiangVienID, 
    MaGiangVien, 
    HoTen, 
    Khoa
FROM GiangVien
WHERE Khoa LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%'
ORDER BY MaGiangVien;

-- 2.3 Các lớp có Ngành học hoặc Khoa bị lỗi hiển thị
PRINT N'--- Danh sách Lớp có thông tin ngành/khoa bị lỗi mã hóa ---';
SELECT 
    LopID, 
    MaLop, 
    TenLop, 
    NganhHoc, 
    Khoa
FROM Lop
WHERE NganhHoc LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%'
   OR Khoa LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%'
ORDER BY MaLop;
GO

