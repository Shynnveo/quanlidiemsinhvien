-- ====================================================================================================
-- TỆP 02: SCRIPT PHỤC HỒI DỮ LIỆU UNICODE TIẾNG VIỆT
-- Database: duanjavanangcao
-- Nguồn dữ liệu đối chiếu: BoDuLieuMau_duanjavanangcao.sql (Đọc ở chuẩn UTF-8 gốc)
-- 
-- NGUYÊN TẮC AN TOÀN TUYỆT ĐỐI:
--   1. Sử dụng TRANSACTION (BEGIN TRANSACTION ... COMMIT).
--   2. Không thay đổi ID, Mã SV, Mã GV, Mã Lớp, Điểm số, Đăng ký học phần hay Khóa ngoại.
--   3. Chỉ UPDATE chính xác từng dòng theo Khóa chính (MaSinhVien, MaGiangVien, MaLop).
-- ====================================================================================================

USE duanjavanangcao;
GO

BEGIN TRANSACTION;

PRINT N'========================================================================';
PRINT N'BƯỚC 1: CẬP NHẬT PHỤC HỒI CHUẨN UNICODE CHO BẢNG [GiangVien]';
PRINT N'========================================================================';

UPDATE GiangVien SET Khoa = N'Công nghệ thông tin' WHERE MaGiangVien = 'GV003';
UPDATE GiangVien SET Khoa = N'Hệ thống thông tin' WHERE MaGiangVien = 'GV004';
UPDATE GiangVien SET Khoa = N'An toàn thông tin' WHERE MaGiangVien = 'GV005';
UPDATE GiangVien SET Khoa = N'Khoa học máy tính' WHERE MaGiangVien = 'GV006';

PRINT N'-> Đã cập nhật 4 giảng viên.';
GO

PRINT N'========================================================================';
PRINT N'BƯỚC 2: CẬP NHẬT PHỤC HỒI CHUẨN UNICODE CHO BẢNG [Lop]';
PRINT N'========================================================================';

UPDATE Lop SET NganhHoc = N'An toàn thông tin', Khoa = N'Công nghệ thông tin' WHERE MaLop = 'ATTT_K20A';
UPDATE Lop SET NganhHoc = N'Công nghệ thông tin', Khoa = N'Công nghệ thông tin' WHERE MaLop = 'CNTT_K20B';

PRINT N'-> Đã cập nhật 2 lớp hành chính.';
GO

PRINT N'========================================================================';
PRINT N'BƯỚC 3: CẬP NHẬT PHỤC HỒI CHUẨN UNICODE CHO BẢNG [SinhVien] (14 BẢN GHI)';
PRINT N'========================================================================';

UPDATE SinhVien SET DiaChi = N'Hà Nội', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV007';
UPDATE SinhVien SET DiaChi = N'Nam Định', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV009';
UPDATE SinhVien SET DiaChi = N'Hà Nội', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV011';
UPDATE SinhVien SET DiaChi = N'Vĩnh Phúc', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV013';
UPDATE SinhVien SET DiaChi = N'Thanh Hóa', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV014';
UPDATE SinhVien SET DiaChi = N'Nghệ An', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV015';
UPDATE SinhVien SET DiaChi = N'Hải Dương', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV017';
UPDATE SinhVien SET DiaChi = N'Hà Nội', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV019';
UPDATE SinhVien SET DiaChi = N'Hòa Bình', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV021';
UPDATE SinhVien SET DiaChi = N'Thái Bình', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV022';
UPDATE SinhVien SET DiaChi = N'Phú Thọ', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV024';
UPDATE SinhVien SET DiaChi = N'Hưng Yên', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV026';
UPDATE SinhVien SET DiaChi = N'Ninh Bình', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV028';
UPDATE SinhVien SET DiaChi = N'Hà Nội', TrangThai = N'Đang học' WHERE MaSinhVien = 'SV029';

PRINT N'-> Đã cập nhật 14 sinh viên.';
GO

COMMIT TRANSACTION;
PRINT N'-> GIAO DỊCH ĐÃ ĐƯỢC COMMIT THÀNH CÔNG!';
GO
