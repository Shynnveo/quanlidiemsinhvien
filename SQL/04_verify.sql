-- ====================================================================================================
-- TỆP 04: KIỂM TRA TÍNH TOÀN VẸN CỦA DỮ LIỆU VÀ CẤU TRÚC (VERIFY DATA & SCHEMA)
-- Database: duanjavanangcao
-- 
-- NGUYÊN TẮC:
--   - Script này là CHỈ ĐỌC (READ-ONLY), tuyệt đối KHÔNG làm thay đổi bất kỳ dữ liệu nào.
--   - Dùng để kiểm tra số lượng bản ghi thực tế của 17 bảng sau khi khởi tạo hoặc nạp dữ liệu.
--   - Kiểm tra khóa ngoại mồ côi (orphaned foreign keys) nếu có.
-- ====================================================================================================

USE duanjavanangcao;
GO

SET NOCOUNT ON;
GO

PRINT N'========================================================================';
PRINT N'1. THỐNG KÊ SỐ LƯỢNG BẢN GHI TRÊN TOÀN BỘ 17 BẢNG DỮ LIỆU';
PRINT N'========================================================================';

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
UNION ALL SELECT N'ThongBao', COUNT(*) FROM dbo.ThongBao;
GO

PRINT N'========================================================================';
PRINT N'2. KIỂM TRA RÀNG BUỘC KHÓA NGOẠI (FOREIGN KEYS INTEGRITY)';
PRINT N'========================================================================';

-- 2.1 Sinh viên không thuộc lớp nào hợp lệ
SELECT COUNT(*) AS SoSinhVienLoiLop
FROM dbo.SinhVien s
LEFT JOIN dbo.Lop l ON s.LopID = l.LopID
WHERE s.LopID IS NOT NULL AND l.LopID IS NULL;

-- 2.2 Đăng ký học phần trỏ đến Sinh viên hoặc Lớp học phần không tồn tại
SELECT COUNT(*) AS SoDangKyLoi
FROM dbo.DangKyHocPhan d
LEFT JOIN dbo.SinhVien s ON d.SinhVienID = s.SinhVienID
LEFT JOIN dbo.LopHocPhan l ON d.LopHocPhanID = l.LopHocPhanID
WHERE s.SinhVienID IS NULL OR l.LopHocPhanID IS NULL;

-- 2.3 Bảng điểm trỏ đến Đăng ký học phần không tồn tại
SELECT COUNT(*) AS SoBangDiemLoi
FROM dbo.BangDiem b
LEFT JOIN dbo.DangKyHocPhan d ON b.DangKyID = d.DangKyID
WHERE d.DangKyID IS NULL;
GO

PRINT N'========================================================================';
PRINT N'3. KIỂM TRA DẢI ĐIỂM SỐ VÀ CÔNG THỨC ĐIỂM HỌC PHẦN';
PRINT N'========================================================================';

-- Kiểm tra điểm ngoài dải hợp lệ [0, 10]
SELECT COUNT(*) AS SoDiemNgoaiLe
FROM dbo.BangDiem
WHERE (DiemCC < 0 OR DiemCC > 10)
   OR (DiemGK < 0 OR DiemGK > 10)
   OR (DiemCK < 0 OR DiemCK > 10)
   OR (DiemTongKet < 0 OR DiemTongKet > 10);
GO

