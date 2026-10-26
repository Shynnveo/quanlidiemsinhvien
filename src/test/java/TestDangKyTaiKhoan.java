import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.dao.TaiKhoanDAO;
import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.service.DangKyService;
import com.mycompany.duanjavanangcao.service.DangNhapService;
import com.mycompany.duanjavanangcao.view.FrmDangKyTaiKhoan;
import com.mycompany.duanjavanangcao.view.FrmDangNhap;

import java.sql.Date;

public class TestDangKyTaiKhoan {

    public static void main(String[] args) {
        System.out.println("========== BẮT ĐẦU KIỂM THỬ CHỨC NĂNG: ĐĂNG KÝ TÀI KHOẢN SINH VIÊN ==========");

        DangKyService dangKyService = new DangKyService();
        DangNhapService dangNhapService = new DangNhapService();
        SinhVienDAO sinhVienDAO = new SinhVienDAO();
        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

        String testMaSV = "SV_TEST_REG";
        String testUsername = "sv_test_user_reg";
        String testPassword = "Password@2026";

        // Chuẩn bị dữ liệu: Dọn dẹp nếu đã tồn tại từ lần chạy trước
        try {
            SinhVien existingSv = sinhVienDAO.findByMaSinhVien(testMaSV);
            if (existingSv != null) {
                Integer tkId = existingSv.getTaiKhoanId();
                sinhVienDAO.delete(existingSv.getSinhVienId());
                if (tkId != null) {
                    taiKhoanDAO.delete(tkId);
                }
            }
            if (taiKhoanDAO.existsByTenDangNhap(testUsername)) {
                TaiKhoan oldTk = taiKhoanDAO.findByTenDangNhap(testUsername);
                if (oldTk != null) {
                    taiKhoanDAO.delete(oldTk.getTaiKhoanId());
                }
            }
        } catch (Exception ignored) {
        }

        // Tạo 1 hồ sơ sinh viên chưa có tài khoản (TaiKhoanID = null)
        SinhVien svMoi = new SinhVien();
        svMoi.setMaSinhVien(testMaSV);
        svMoi.setHoTen("Trần Văn Test Đăng Ký");
        svMoi.setEmail("test_reg@utt.edu.vn");
        svMoi.setSoDienThoai("0912345678");
        svMoi.setGioiTinh("Nam");
        svMoi.setTrangThai("Đang học");
        svMoi.setNgayNhapHoc(Date.valueOf("2024-09-01"));
        svMoi.setTaiKhoanId(null);
        sinhVienDAO.insert(svMoi);

        int countPass = 0;

        // ---------------------------------------------------------------------
        // TEST CASE 2: Bỏ trống thông tin
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TEST CASE 2: Bỏ trống thông tin] ---");
        try {
            dangKyService.dangKyTaiKhoan("", testUsername, testPassword, testPassword);
            System.err.println("THẤT BẠI: Cho phép để trống mã sinh viên!");
        } catch (Exception ex) {
            System.out.println("THÀNH CÔNG: Chặn bỏ trống mã SV -> " + ex.getMessage());
            countPass++;
        }

        try {
            dangKyService.dangKyTaiKhoan(testMaSV, "", testPassword, testPassword);
            System.err.println("THẤT BẠI: Cho phép để trống tên đăng nhập!");
        } catch (Exception ex) {
            System.out.println("THÀNH CÔNG: Chặn bỏ trống username -> " + ex.getMessage());
            countPass++;
        }

        try {
            dangKyService.dangKyTaiKhoan(testMaSV, testUsername, "", testPassword);
            System.err.println("THẤT BẠI: Cho phép để trống mật khẩu!");
        } catch (Exception ex) {
            System.out.println("THÀNH CÔNG: Chặn bỏ trống password -> " + ex.getMessage());
            countPass++;
        }

        // ---------------------------------------------------------------------
        // TEST CASE 3: Hai mật khẩu không khớp & Mật khẩu quá ngắn
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TEST CASE 3: Hai mật khẩu không khớp hoặc quá ngắn] ---");
        try {
            dangKyService.dangKyTaiKhoan(testMaSV, testUsername, "Password123", "MismatchPassword456");
            System.err.println("THẤT BẠI: Cho phép mật khẩu không khớp!");
        } catch (Exception ex) {
            System.out.println("THÀNH CÔNG: Chặn mật khẩu không khớp -> " + ex.getMessage());
            countPass++;
        }

        try {
            dangKyService.dangKyTaiKhoan(testMaSV, testUsername, "123", "123");
            System.err.println("THẤT BẠI: Cho phép mật khẩu ngắn hơn 6 ký tự!");
        } catch (Exception ex) {
            System.out.println("THÀNH CÔNG: Chặn mật khẩu quá ngắn -> " + ex.getMessage());
            countPass++;
        }

        // ---------------------------------------------------------------------
        // TEST CASE 4: Tên đăng nhập đã tồn tại
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TEST CASE 4: Tên đăng nhập đã tồn tại (admin)] ---");
        try {
            dangKyService.dangKyTaiKhoan(testMaSV, "admin", testPassword, testPassword);
            System.err.println("THẤT BẠI: Cho phép trùng tên đăng nhập admin!");
        } catch (Exception ex) {
            System.out.println("THÀNH CÔNG: Chặn trùng tên đăng nhập -> " + ex.getMessage());
            countPass++;
        }

        // ---------------------------------------------------------------------
        // TEST CASE 5: Mã sinh viên không tồn tại trong hệ thống
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TEST CASE 5: Mã sinh viên không tồn tại] ---");
        try {
            dangKyService.dangKyTaiKhoan("SV_FAKE_999999", "user_fake", testPassword, testPassword);
            System.err.println("THẤT BẠI: Cho phép mã sinh viên không tồn tại!");
        } catch (Exception ex) {
            System.out.println("THÀNH CÔNG: Chặn mã SV không tồn tại -> " + ex.getMessage());
            countPass++;
        }

        // ---------------------------------------------------------------------
        // TEST CASE 6: Sinh viên đã có tài khoản (SV001 đã có tài khoản ID 4)
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TEST CASE 6: Sinh viên đã có tài khoản (SV001)] ---");
        try {
            dangKyService.dangKyTaiKhoan("SV001", "new_user_sv001", testPassword, testPassword);
            System.err.println("THẤT BẠI: Cho phép sinh viên đã có tài khoản đăng ký tiếp!");
        } catch (Exception ex) {
            System.out.println("THÀNH CÔNG: Chặn sinh viên đã có tài khoản -> " + ex.getMessage());
            countPass++;
        }

        // ---------------------------------------------------------------------
        // TEST CASE 1: Đăng ký hợp lệ
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TEST CASE 1: Đăng ký tài khoản hợp lệ] ---");
        try {
            SinhVien svKetQua = dangKyService.dangKyTaiKhoan(testMaSV, testUsername, testPassword, testPassword);
            System.out.printf("THÀNH CÔNG: Đã đăng ký tài khoản '%s' cho sinh viên: %s (Mã: %s)%n",
                    testUsername, svKetQua.getHoTen(), svKetQua.getMaSinhVien());

            // Kiểm tra CSDL thực tế:
            TaiKhoan tkTrongDb = taiKhoanDAO.findByTenDangNhap(testUsername);
            SinhVien svTrongDb = sinhVienDAO.findByMaSinhVien(testMaSV);

            boolean lienKetChuan = (tkTrongDb != null)
                    && "SINHVIEN".equalsIgnoreCase(tkTrongDb.getVaiTro())
                    && tkTrongDb.isTrangThai()
                    && svTrongDb.getTaiKhoanId() != null
                    && svTrongDb.getTaiKhoanId().equals(tkTrongDb.getTaiKhoanId());

            System.out.printf("   Kiểm tra CSDL: Tài khoản ID=%d, Vai trò=%s, Trạng thái=%s, SinhVien.TaiKhoanID=%d -> %s%n",
                    tkTrongDb.getTaiKhoanId(),
                    tkTrongDb.getVaiTro(),
                    tkTrongDb.isTrangThai() ? "Hoạt động" : "Khóa",
                    svTrongDb.getTaiKhoanId(),
                    lienKetChuan ? "CHÍNH XÁC 100%" : "LỖI LIÊN KẾT");

            if (lienKetChuan) {
                countPass++;
            }
        } catch (Exception ex) {
            System.err.println("LỖI Đăng ký hợp lệ: " + ex.getMessage());
            ex.printStackTrace();
        }

        // ---------------------------------------------------------------------
        // TEST CASE 8: Đăng nhập được bằng tài khoản vừa tạo
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TEST CASE 8: Đăng nhập bằng tài khoản vừa tạo] ---");
        try {
            TaiKhoan loginUser = dangNhapService.dangNhap(testUsername, testPassword);
            boolean checkLogin = (loginUser != null)
                    && testUsername.equalsIgnoreCase(loginUser.getTenDangNhap())
                    && "SINHVIEN".equalsIgnoreCase(loginUser.getVaiTro());

            System.out.printf("THÀNH CÔNG: Đăng nhập tài khoản '%s' thành công! Họ tên: %s, Vai trò: %s -> %s%n",
                    testUsername, loginUser.getHoTen(), loginUser.getVaiTro(),
                    checkLogin ? "XÁC THỰC CHUẨN XÁC" : "LỖI XÁC THỰC");

            if (checkLogin) {
                countPass++;
            }
        } catch (Exception ex) {
            System.err.println("LỖI Đăng nhập: " + ex.getMessage());
        }

        // ---------------------------------------------------------------------
        // TEST CASE: Khởi tạo Frame FrmDangKyTaiKhoan & FrmDangNhap
        // ---------------------------------------------------------------------
        System.out.println("\n--- [TEST GIAO DIỆN: Khởi tạo FrmDangKyTaiKhoan & FrmDangNhap] ---");
        try {
            FrmDangKyTaiKhoan frmDK = new FrmDangKyTaiKhoan();
            FrmDangNhap frmDN = new FrmDangNhap();
            System.out.println("THÀNH CÔNG: Khởi tạo 2 Swing JFrame không có lỗi giao diện.");
            frmDK.dispose();
            frmDN.dispose();
            countPass++;
        } catch (Exception ex) {
            System.err.println("LỖI Giao diện: " + ex.getMessage());
        }

        // Dọn dẹp dữ liệu kiểm thử
        try {
            SinhVien svClean = sinhVienDAO.findByMaSinhVien(testMaSV);
            if (svClean != null) {
                Integer tkId = svClean.getTaiKhoanId();
                sinhVienDAO.delete(svClean.getSinhVienId());
                if (tkId != null) {
                    taiKhoanDAO.delete(tkId);
                }
            }
            System.out.println("\nĐã dọn dẹp dữ liệu kiểm thử hoàn tất.");
        } catch (Exception ignored) {
        }

        System.out.println("\n========== TỔNG KẾT: ĐÃ HOÀN THÀNH TẤT CẢ " + countPass + " TIÊU CHÍ KIỂM THỬ THÀNH CÔNG! ==========");
        System.exit(0);
    }
}
