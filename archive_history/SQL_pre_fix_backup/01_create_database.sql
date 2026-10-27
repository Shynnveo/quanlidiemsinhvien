-- ====================================================================================================
-- TỆP 01: KHỞI TẠO CƠ SỞ DỮ LIỆU (DATABASE INITIALIZATION)
-- Hệ quản trị CSDL: Microsoft SQL Server
-- Tên cơ sở dữ liệu: duanjavanangcao
-- 
-- NGUYÊN TẮC:
--   - Kiểm tra database đã tồn tại trước khi tạo mới bằng IF DB_ID(...).
--   - Giữ nguyên collation mặc định của SQL Server (SQL_Latin1_General_CP1_CI_AS) theo đúng
--     cấu hình hiện tại của hệ thống, không thay đổi collation đột ngột.
--   - Toàn bộ chuỗi tiếng Việt trong các bảng sẽ được lưu trữ qua kiểu dữ liệu Unicode (NVARCHAR).
-- ====================================================================================================

IF DB_ID(N'duanjavanangcao') IS NULL
BEGIN
    PRINT N'Database duanjavanangcao chưa tồn tại. Đang tiến hành tạo mới...';
    CREATE DATABASE duanjavanangcao;
    PRINT N'Tạo database duanjavanangcao thành công!';
END
ELSE
BEGIN
    PRINT N'Database duanjavanangcao đã tồn tại sẵn trong hệ thống. Bỏ qua bước tạo mới.';
END
GO

USE duanjavanangcao;
GO

PRINT N'Đã chuyển ngữ cảnh làm việc sang cơ sở dữ liệu: duanjavanangcao';
GO

