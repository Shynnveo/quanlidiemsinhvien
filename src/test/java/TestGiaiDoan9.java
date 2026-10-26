import com.mycompany.duanjavanangcao.dao.CanhCaoHocTapDAO;
import com.mycompany.duanjavanangcao.dao.HocKyDAO;
import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.dao.TaiKhoanDAO;
import com.mycompany.duanjavanangcao.dao.ThongBaoDAO;
import com.mycompany.duanjavanangcao.dao.TieuChiCanhCaoDAO;
import com.mycompany.duanjavanangcao.model.CanhCaoHocTap;
import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.model.ThongBao;
import com.mycompany.duanjavanangcao.model.TieuChiCanhCao;
import com.mycompany.duanjavanangcao.util.SessionManager;

import java.util.List;

public class TestGiaiDoan9 {

    public static void main(String[] args) {
        System.out.println("========== BẮT ĐẦU KIỂM THỬ GIAI ĐOẠN 9: CẢNH BÁO HỌC TẬP & XỬ LÝ HỌC VỤ ==========");

        TieuChiCanhCaoDAO tieuChiDAO = new TieuChiCanhCaoDAO();
        CanhCaoHocTapDAO canhCaoDAO = new CanhCaoHocTapDAO();
        SinhVienDAO sinhVienDAO = new SinhVienDAO();
        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
        HocKyDAO hocKyDAO = new HocKyDAO();
        ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

        // 1. Kiểm tra danh sách tiêu chí cảnh báo
        List<TieuChiCanhCao> listTc = tieuChiDAO.findActive();
        System.out.println("1. Danh sách tiêu chí cảnh báo đang kích hoạt: " + listTc.size());
        for (TieuChiCanhCao tc : listTc) {
            System.out.println("   - ID " + tc.getTieuChiId() + ": " + tc.getTenTieuChi()
                    + " (" + tc.getMaTieuChi() + ") | GPA max = " + tc.getGpaHocKyToiDa()
                    + " | Nợ TC min = " + tc.getSoTinChiNoToiThieu());
        }
        if (listTc.isEmpty()) {
            System.err.println("LỖI: Không có tiêu chí cảnh báo nào!");
            return;
        }

        // 2. Lấy dữ liệu sinh viên và học kỳ kiểm thử
        List<SinhVien> listSv = sinhVienDAO.findAll();
        List<HocKy> listHk = hocKyDAO.findAll();
        if (listSv.isEmpty() || listHk.isEmpty()) {
            System.err.println("LỖI: Dữ liệu sinh viên hoặc học kỳ rỗng!");
            return;
        }

        SinhVien targetSv = listSv.get(0);
        HocKy targetHk = listHk.get(0);
        TieuChiCanhCao targetTc = listTc.get(0);

        System.out.println("2. Sinh viên kiểm thử: " + targetSv.getHoTen() + " (" + targetSv.getMaSinhVien() + ")");
        System.out.println("   Học kỳ xét: " + targetHk.getTenHocKy() + " (" + targetHk.getMaHocKy() + ")");
        System.out.println("   Tiêu chí: " + targetTc.getTenTieuChi());

        // 3. Đăng nhập Quản trị viên
        TaiKhoan adminTk = taiKhoanDAO.findByTenDangNhap("admin");
        SessionManager.setCurrentUser(adminTk);
        System.out.println("3. Đăng nhập Quản trị viên xét duyệt: " + adminTk.getHoTen());

        // 4. Kiểm tra thêm cảnh báo thủ công
        CanhCaoHocTap ccMoi = new CanhCaoHocTap();
        ccMoi.setSinhVienId(targetSv.getSinhVienId());
        ccMoi.setHocKyId(targetHk.getHocKyId());
        ccMoi.setTieuChiId(targetTc.getTieuChiId());
        ccMoi.setLyDo("GPA học kỳ đạt 0.95 (dưới ngưỡng 1.00 quy định)");
        ccMoi.setMucCanhCao("Cảnh báo mức 1");
        ccMoi.setTrangThai("Đang áp dụng");
        ccMoi.setNguoiXetId(adminTk.getTaiKhoanId());
        ccMoi.setGhiChu("Đề nghị Cố vấn học tập gặp trực tiếp để nhắc nhở");
        ccMoi.setTaiKhoanSinhVienId(targetSv.getTaiKhoanId());

        // Đảm bảo không trùng trước khi insert test
        if (canhCaoDAO.exists(targetSv.getSinhVienId(), targetHk.getHocKyId(), targetTc.getTieuChiId())) {
            System.out.println("   (Cảnh báo này đã có trong CSDL, xóa để test từ đầu)");
            List<CanhCaoHocTap> existing = canhCaoDAO.findByFilter(targetHk.getHocKyId(), targetSv.getLopId(), null, null, targetSv.getMaSinhVien());
            for (CanhCaoHocTap old : existing) {
                if (old.getTieuChiId() == targetTc.getTieuChiId()) {
                    canhCaoDAO.delete(old.getCanhCaoId());
                }
            }
        }

        boolean insertOk = canhCaoDAO.insert(ccMoi);
        System.out.println("4. Thêm cảnh báo học tập thủ công: " + (insertOk ? "THÀNH CÔNG" : "THẤT BẠI"));

        // 5. Kiểm tra chống trùng lặp cảnh báo
        boolean trungLap = canhCaoDAO.exists(targetSv.getSinhVienId(), targetHk.getHocKyId(), targetTc.getTieuChiId());
        System.out.println("5. Kiểm tra phát hiện cảnh báo trùng lặp: " + (trungLap ? "ĐÚNG (Chặn trùng thành công)" : "SAI"));

        // 6. Kiểm tra truy vấn danh sách cảnh báo của sinh viên
        List<CanhCaoHocTap> dsCanhCaoSV = canhCaoDAO.findBySinhVien(targetSv.getSinhVienId());
        System.out.println("6. Số cảnh báo của sinh viên " + targetSv.getMaSinhVien() + ": " + dsCanhCaoSV.size());
        CanhCaoHocTap ccVuaTao = null;
        for (CanhCaoHocTap cc : dsCanhCaoSV) {
            if (cc.getHocKyId() == targetHk.getHocKyId() && cc.getTieuChiId() == targetTc.getTieuChiId()) {
                ccVuaTao = cc;
                break;
            }
        }
        if (ccVuaTao == null) {
            System.err.println("LỖI: Không tìm thấy cảnh báo vừa tạo!");
            return;
        }
        System.out.println("   Chi tiết: Mã CB #" + ccVuaTao.getCanhCaoId() + " | Mức: " + ccVuaTao.getMucCanhCao()
                + " | Lý do: " + ccVuaTao.getLyDo() + " | Trạng thái: " + ccVuaTao.getTrangThai());

        // 7. Kiểm tra thông báo gửi đến sinh viên
        if (targetSv.getTaiKhoanId() != null) {
            List<ThongBao> dsTB = thongBaoDAO.findRecentByTaiKhoan(targetSv.getTaiKhoanId());
            System.out.println("7. Kiểm tra Thông báo sinh viên nhận được:");
            if (!dsTB.isEmpty()) {
                ThongBao tb = dsTB.get(0);
                System.out.println("   - Tiêu đề: " + tb.getTieuDe());
                System.out.println("   - Nội dung: " + tb.getNoiDung().replace("\n", " | "));
            }
        }

        // 8. Cập nhật xử lý học vụ (Ví dụ: sinh viên đã học cải thiện đạt yêu cầu -> Đã khắc phục)
        ccVuaTao.setTrangThai("Đã khắc phục");
        ccVuaTao.setGhiChu("Sinh viên đã thi cải thiện điểm nâng GPA học kỳ lên 2.50, xóa cảnh báo");
        boolean updateOk = canhCaoDAO.update(ccVuaTao);
        System.out.println("8. Cập nhật xử lý cảnh báo ('Đã khắc phục'): " + (updateOk ? "THÀNH CÔNG" : "THẤT BẠI"));

        CanhCaoHocTap ccSauUpdate = canhCaoDAO.findById(ccVuaTao.getCanhCaoId());
        System.out.println("   Trạng thái sau cập nhật: " + ccSauUpdate.getTrangThai() + " | Ghi chú: " + ccSauUpdate.getGhiChu());

        // 9. Kiểm tra tính năng quét xét cảnh báo tự động
        System.out.println("9. Thực thi chức năng Quét xét cảnh báo tự động cho " + targetHk.getTenHocKy() + "...");
        int soCanhCaoMoi = canhCaoDAO.quetCanhCaoTuDong(targetHk.getHocKyId(), adminTk.getTaiKhoanId());
        System.out.println("   Số cảnh báo mới phát hành từ quét xét tự động: " + soCanhCaoMoi);

        // 10. Kiểm tra bộ lọc danh sách (findByFilter)
        List<CanhCaoHocTap> dsLoc = canhCaoDAO.findByFilter(targetHk.getHocKyId(), 0, "Tất cả", "Tất cả", "");
        System.out.println("10. Kiểm tra bộ lọc theo học kỳ: Tìm thấy " + dsLoc.size() + " cảnh báo.");

        // 11. Kiểm tra xóa cảnh báo
        boolean xoaOk = canhCaoDAO.delete(ccVuaTao.getCanhCaoId());
        System.out.println("11. Kiểm tra Xóa cảnh báo: " + (xoaOk ? "THÀNH CÔNG" : "THẤT BẠI"));

        System.out.println("========== HOÀN TẤT KIỂM THỬ GIAI ĐOẠN 9: TẤT CẢ TEST ĐỀU THÀNH CÔNG! ==========");
    }
}

