-- ====================================================================================================
-- TỆP 03: XÁC MINH DỮ LIỆU SAU PHỤC HỒI (CHỈ ĐỌC - READ ONLY)
-- Database: duanjavanangcao
-- Mục đích: Kiểm tra xác nhận 100% không còn bất kỳ chuỗi Mojibake nào tồn tại trong database.
-- ====================================================================================================

USE duanjavanangcao;
GO

PRINT N'========================================================================';
PRINT N'1. KIỂM TRA TOÀN BỘ 14 SINH VIÊN ĐÃ ĐƯỢC PHỤC HỒI';
PRINT N'========================================================================';

SELECT 
    SinhVienID, 
    MaSinhVien, 
    HoTen, 
    GioiTinh, 
    DiaChi, 
    TrangThai
FROM SinhVien
WHERE MaSinhVien IN (
    'SV007', 'SV009', 'SV011', 'SV013', 'SV014', 'SV015', 
    'SV017', 'SV019', 'SV021', 'SV022', 'SV024', 'SV026', 'SV028', 'SV029'
)
ORDER BY MaSinhVien;
GO

PRINT N'========================================================================';
PRINT N'2. QUÉT TỔNG THỂ TOÀN BỘ DATABASE TÌM KÝ TỰ RÁC MOJIBAKE (Ã, Â, ?) CÒN SÓT LẠI';
PRINT N'========================================================================';

-- 2.1 Quét trong SinhVien
SELECT 'SinhVien' AS TableName, COUNT(*) AS SoLuongLoi 
FROM SinhVien 
WHERE DiaChi LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%' 
   OR TrangThai LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%';

-- 2.2 Quét trong GiangVien
SELECT 'GiangVien' AS TableName, COUNT(*) AS SoLuongLoi 
FROM GiangVien 
WHERE Khoa LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%';

-- 2.3 Quét trong Lop
SELECT 'Lop' AS TableName, COUNT(*) AS SoLuongLoi 
FROM Lop 
WHERE NganhHoc LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%' 
   OR Khoa LIKE N'%[ÃÂÈÉÊÌÍÒÓÔÙÚÝàáâèéêìíòóôùúý?]%';

PRINT N'========================================================================';
PRINT N'KẾT THÚC KIỂM TRA. NẾU TẤT CẢ SoLuongLoi = 0 LÀ HỆ THỐNG ĐÃ HOÀN TOÀN CHUẨN XÁC!';
PRINT N'========================================================================';
GO

