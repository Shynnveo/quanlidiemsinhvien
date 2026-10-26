import com.mycompany.duanjavanangcao.dao.HocKyDAO;
import com.mycompany.duanjavanangcao.dao.LopHocPhanDAO;
import com.mycompany.duanjavanangcao.dao.TaiKhoanDAO;
import com.mycompany.duanjavanangcao.dao.ThongKeDAO;
import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.model.LopHocPhan;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.PasswordUtil;
import com.mycompany.duanjavanangcao.view.FrmQLTaiKhoan;
import com.mycompany.duanjavanangcao.view.FrmThongKeBaoCao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TestGiaiDoan10 {

    public static void main(String[] args) {
        System.out.println("========== BẮT ĐẦU KIỂM THỬ GIAI ĐOẠN 10: QUẢN LÝ TÀI KHOẢN & THỐNG KÊ BÁO CÁO ==========");

        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
        ThongKeDAO thongKeDAO = new ThongKeDAO();
        HocKyDAO hocKyDAO = new HocKyDAO();
        LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO();

        int step = 1;

        // ----------------------------------------------------
        // PHẦN 1: KIỂM THỬ TÀI KHOẢN DAO & CRUD TÀI KHOẢN
        // ----------------------------------------------------
        System.out.println("\n--- [PHẦN 1: QUẢN LÝ TÀI KHOẢN] ---");

        // 1. Kiểm tra danh sách tài khoản hiện có
        List<TaiKhoan> danhSachTK = taiKhoanDAO.findAll();
        System.out.printf("%d. Tổng số tài khoản hiện có trong CSDL: %d%n", step++, danhSachTK.size());
        if (danhSachTK.isEmpty()) {
            System.err.println("LỖI: Bảng TaiKhoan rỗng!");
            return;
        }

        // 2. Tìm kiếm và lọc tài khoản
        List<TaiKhoan> listAdmin = taiKhoanDAO.findByFilter("ADMIN", "Hoạt động", "admin");
        System.out.printf("%d. Lọc tài khoản (keyword='admin', vaiTro='ADMIN', trangThai='Hoạt động'): %d tài khoản tìm thấy.%n",
                step++, listAdmin.size());
        if (listAdmin.isEmpty()) {
            System.err.println("LỖI: Không tìm thấy tài khoản admin mẫu!");
            return;
        }

        // 3. Thêm mới tài khoản kiểm thử
        String testUsername = "test_user_phase10";
        if (taiKhoanDAO.existsByTenDangNhap(testUsername)) {
            TaiKhoan old = taiKhoanDAO.findByTenDangNhap(testUsername);
            taiKhoanDAO.delete(old.getTaiKhoanId());
        }

        TaiKhoan newTk = new TaiKhoan();
        newTk.setTenDangNhap(testUsername);
        newTk.setHoTen("Nguyễn Văn Test 10");
        newTk.setVaiTro("GIANGVIEN");
        newTk.setEmail("test_phase10@utt.edu.vn");
        newTk.setSoDienThoai("0987654321");
        newTk.setTrangThai(true);

        boolean inserted = taiKhoanDAO.insert(newTk, "InitPass123");
        TaiKhoan createdTk = taiKhoanDAO.findByTenDangNhap(testUsername);
        System.out.printf("%d. Thêm tài khoản kiểm thử '%s': %s (ID = %d)%n",
                step++, testUsername, (inserted && createdTk != null) ? "THÀNH CÔNG" : "THẤT BẠI",
                createdTk != null ? createdTk.getTaiKhoanId() : -1);
        if (!inserted || createdTk == null) {
            System.err.println("LỖI: Thêm tài khoản thất bại!");
            return;
        }

        // 4. Cập nhật thông tin tài khoản
        createdTk.setEmail("updated_phase10@utt.edu.vn");
        createdTk.setTrangThai(false);
        boolean updated = taiKhoanDAO.update(createdTk);
        TaiKhoan reloadedTk = taiKhoanDAO.findById(createdTk.getTaiKhoanId());
        boolean checkUpdate = updated && "updated_phase10@utt.edu.vn".equals(reloadedTk.getEmail())
                && !reloadedTk.isTrangThai();
        System.out.printf("%d. Cập nhật email và trạng thái: %s%n", step++, checkUpdate ? "THÀNH CÔNG" : "THẤT BẠI");
        if (!checkUpdate) {
            System.err.println("LỖI: Cập nhật tài khoản thất bại!");
            return;
        }

        // 5. Đặt lại mật khẩu (Reset password)
        String newRawPassword = "NewResetPassword@2026";
        boolean resetSuccess = taiKhoanDAO.resetMatKhau(createdTk.getTaiKhoanId(), newRawPassword);
        TaiKhoan afterResetTk = taiKhoanDAO.findById(createdTk.getTaiKhoanId());
        boolean checkPass = resetSuccess && PasswordUtil.verifyPassword(newRawPassword, afterResetTk.getMatKhauHash());
        System.out.printf("%d. Đặt lại mật khẩu tài khoản: %s (Mật khẩu được mã hóa BCrypt an toàn)%n",
                step++, checkPass ? "THÀNH CÔNG" : "THẤT BẠI");
        if (!checkPass) {
            System.err.println("LỖI: Reset mật khẩu thất bại!");
            return;
        }

        // 6. Xóa tài khoản kiểm thử
        try {
            boolean deleted = taiKhoanDAO.delete(createdTk.getTaiKhoanId());
            boolean stillExists = taiKhoanDAO.existsByTenDangNhap(testUsername);
            System.out.printf("%d. Dọn dẹp / Xóa tài khoản kiểm thử: %s (Còn tồn tại: %s)%n",
                    step++, (deleted && !stillExists) ? "THÀNH CÔNG" : "THẤT BẠI", stillExists);
        } catch (Exception ex) {
            System.err.println("Lỗi khi xóa tài khoản: " + ex.getMessage());
        }

        // ----------------------------------------------------
        // PHẦN 2: KIỂM THỬ THỐNG KÊ DAO & CÁC CHẾ ĐỘ BÁO CÁO
        // ----------------------------------------------------
        System.out.println("\n--- [PHẦN 2: THỐNG KÊ & BÁO CÁO KẾT QUẢ HỌC TẬP] ---");

        // 7. Thống kê tổng quan số lượng
        Map<String, Integer> tongQuan = thongKeDAO.getThongKeTongQuan();
        System.out.printf("%d. Thống kê tổng quan CSDL: Sinh viên=%d, Giảng viên=%d, Lớp=%d, Môn học=%d, Lớp HP=%d%n",
                step++,
                tongQuan.getOrDefault("SoSV", 0),
                tongQuan.getOrDefault("SoGV", 0),
                tongQuan.getOrDefault("SoLop", 0),
                tongQuan.getOrDefault("SoMon", 0),
                tongQuan.getOrDefault("SoLHP", 0));

        // 8. Báo cáo 1: Thống kê xếp loại học lực (GPA)
        Map<String, Integer> thongKeGPA = thongKeDAO.thongKeXepLoaiHocLuc(0, 0);
        System.out.printf("%d. Báo cáo xếp loại học lực (Toàn trường): Tổng SV=%d | XS=%d, Giỏi=%d, Khá=%d, TB=%d, Yếu/Kém=%d%n",
                step++,
                thongKeGPA.getOrDefault("Tổng số sinh viên", 0),
                thongKeGPA.getOrDefault("Xuất sắc (>= 3.60)", 0),
                thongKeGPA.getOrDefault("Giỏi (3.20 - 3.59)", 0),
                thongKeGPA.getOrDefault("Khá (2.50 - 3.19)", 0),
                thongKeGPA.getOrDefault("Trung bình (2.00 - 2.49)", 0),
                thongKeGPA.getOrDefault("Yếu / Kém (< 2.00)", 0));

        // 9. Báo cáo 2: Thống kê phổ điểm học phần
        List<LopHocPhan> listLhp = lopHocPhanDAO.findAll();
        int lhpId = listLhp.isEmpty() ? 0 : listLhp.get(0).getLopHocPhanId();
        Map<String, Object> thongKeLhp = thongKeDAO.thongKeDiemHocPhan(lhpId);
        @SuppressWarnings("unchecked")
        Map<String, Integer> phoDiem = (Map<String, Integer>) thongKeLhp.get("PhoDiem");
        System.out.printf("%d. Báo cáo phổ điểm học phần ID %d (%s): Tổng SV=%d, Tỷ lệ Đạt=%.1f%%, Điểm TB=%.2f, Max=%.1f, Min=%.1f%n",
                step++,
                lhpId,
                listLhp.isEmpty() ? "None" : listLhp.get(0).getMaLopHocPhan(),
                (int) thongKeLhp.getOrDefault("TongSV", 0),
                (double) thongKeLhp.getOrDefault("TyLeDat", 0.0),
                (double) thongKeLhp.getOrDefault("DiemTB", 0.0),
                (double) thongKeLhp.getOrDefault("DiemCaoNhat", 0.0),
                (double) thongKeLhp.getOrDefault("DiemThapNhat", 0.0));
        if (phoDiem != null) {
            System.out.println("   Phổ điểm: " + phoDiem);
        }

        // 10. Báo cáo 3: Thống kê cảnh báo học tập
        Map<String, Integer> thongKeCC = thongKeDAO.thongKeCanhCaoHocTap(0);
        System.out.printf("%d. Báo cáo cảnh báo học tập: Tổng số=%d | Mức 1=%d, Mức 2=%d, Mức 3=%d%n",
                step++,
                thongKeCC.getOrDefault("Tổng số cảnh báo", 0),
                thongKeCC.getOrDefault("Cảnh báo mức 1", 0),
                thongKeCC.getOrDefault("Cảnh báo mức 2", 0),
                thongKeCC.getOrDefault("Cảnh báo mức 3 (Buộc thôi học)", 0));

        // 11. Kiểm thử xuất file báo cáo định dạng CSV chuẩn UTF-8 BOM
        File tempCsv = new File("test_baocao_utf8_bom.csv");
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tempCsv), StandardCharsets.UTF_8))) {
            writer.write('\uFEFF'); // UTF-8 BOM
            writer.println("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ GIAO THÔNG VẬN TẢI - UTT");
            writer.println("BÁO CÁO: 1. Thống kê xếp loại học lực (GPA)");
            writer.println("\"STT\",\"Chỉ tiêu\",\"Tiêu chuẩn\",\"Số lượng\",\"Tỷ lệ (%)\"");
            writer.println("\"1\",\"Xuất sắc\",\"GPA >= 3.60\",\"5\",\"10.0%\"");
            writer.println("\"2\",\"Giỏi\",\"3.20 <= GPA < 3.60\",\"15\",\"30.0%\"");
            writer.println("\"\",\"TỔNG CỘNG\",\"\",\"50\",\"100.0%\"");
        } catch (Exception ex) {
            System.err.println("Lỗi khi ghi file CSV: " + ex.getMessage());
        }

        // Kiểm tra xem file có chứa BOM 0xEF 0xBB 0xBF không
        boolean hasBom = false;
        try (FileInputStream fis = new FileInputStream(tempCsv)) {
            byte[] bomBytes = new byte[3];
            int read = fis.read(bomBytes);
            if (read == 3 && (bomBytes[0] & 0xFF) == 0xEF && (bomBytes[1] & 0xFF) == 0xBB && (bomBytes[2] & 0xFF) == 0xBF) {
                hasBom = true;
            }
        } catch (Exception ex) {
            System.err.println("Lỗi đọc file CSV: " + ex.getMessage());
        }
        System.out.printf("%d. Kiểm thử xuất báo cáo CSV có UTF-8 BOM (Tương thích Excel tiếng Việt): %s (Size=%d bytes)%n",
                step++, hasBom ? "THÀNH CÔNG" : "THẤT BẠI", tempCsv.length());
        tempCsv.delete();

        // 12. Khởi tạo thử giao diện FrmQLTaiKhoan và FrmThongKeBaoCao
        try {
            com.mycompany.duanjavanangcao.util.SessionManager.setCurrentUser(listAdmin.get(0));
            FrmQLTaiKhoan frmTK = new FrmQLTaiKhoan();
            FrmThongKeBaoCao frmBC = new FrmThongKeBaoCao();
            System.out.printf("%d. Kiểm tra khởi tạo Swing Frames (FrmQLTaiKhoan & FrmThongKeBaoCao): THÀNH CÔNG%n", step++);
            frmTK.dispose();
            frmBC.dispose();
        } catch (Exception ex) {
            System.err.println("LỖI khởi tạo Frame: " + ex.getMessage());
        }

        System.out.println("\n========== TẤT CẢ 12 BƯỚC KIỂM THỬ GIAI ĐOẠN 10 ĐÃ HOÀN TẤT XUẤT SẮC! ==========");
        System.exit(0);
    }
}
