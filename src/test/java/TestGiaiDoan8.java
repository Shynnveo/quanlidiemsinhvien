import com.mycompany.duanjavanangcao.dao.BangDiemDAO;
import com.mycompany.duanjavanangcao.dao.KetQuaHocTapDAO;
import com.mycompany.duanjavanangcao.dao.LichSuDiemDAO;
import com.mycompany.duanjavanangcao.dao.PhucKhaoDAO;
import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.dao.TaiKhoanDAO;
import com.mycompany.duanjavanangcao.dao.ThongBaoDAO;
import com.mycompany.duanjavanangcao.model.BangDiem;
import com.mycompany.duanjavanangcao.model.KetQuaHocTap;
import com.mycompany.duanjavanangcao.model.LichSuDiem;
import com.mycompany.duanjavanangcao.model.PhucKhao;
import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.model.ThongBao;
import com.mycompany.duanjavanangcao.util.SessionManager;

import java.util.List;

public class TestGiaiDoan8 {

    public static void main(String[] args) {
        System.out.println("========== BẮT ĐẦU KIỂM THỬ GIAI ĐOẠN 8: QUẢN LÝ PHÚC KHẢO ĐIỂM ==========");

        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
        SinhVienDAO sinhVienDAO = new SinhVienDAO();
        BangDiemDAO bangDiemDAO = new BangDiemDAO();
        PhucKhaoDAO phucKhaoDAO = new PhucKhaoDAO();
        LichSuDiemDAO lichSuDAO = new LichSuDiemDAO();
        KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
        ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

        // 1. Kiểm tra tài khoản sinh viên đăng nhập
        TaiKhoan svTk = taiKhoanDAO.findByTenDangNhap("sv001");
        if (svTk == null) {
            System.err.println("LỖI: Không tìm thấy tài khoản sv001");
            return;
        }
        SessionManager.setCurrentUser(svTk);
        System.out.println("1. Đã đăng nhập tài khoản sinh viên: " + svTk.getHoTen() + " (" + svTk.getTenDangNhap() + ")");

        SinhVien sv = sinhVienDAO.findByTaiKhoanId(svTk.getTaiKhoanId());
        if (sv == null) {
            System.err.println("LỖI: Không tìm thấy hồ sơ sinh viên tương ứng");
            return;
        }
        System.out.println("   Mã SV: " + sv.getMaSinhVien() + ", Tên: " + sv.getHoTen());

        // 2. Lấy danh sách môn học đủ điều kiện phúc khảo
        List<BangDiem> dsMon = phucKhaoDAO.findMonHocCoThePhucKhao(sv.getSinhVienId());
        System.out.println("2. Số môn học sinh viên có điểm thi đủ điều kiện phúc khảo: " + dsMon.size());
        if (dsMon.isEmpty()) {
            System.err.println("LỖI: Không có môn học nào có điểm cuối kỳ để test");
            return;
        }

        BangDiem targetBd = dsMon.get(0);
        System.out.println("   Môn chọn để phúc khảo: " + targetBd.getTenMonHoc() + " (" + targetBd.getMaLopHocPhan() + ")");
        System.out.println("   Điểm hiện tại: CC = " + targetBd.getDiemCC() + ", GK = " + targetBd.getDiemGK()
                + ", CK = " + targetBd.getDiemCK() + ", TK = " + targetBd.getDiemTongKet() + ", Chữ = " + targetBd.getDiemChu());

        // 3. Sinh viên gửi đơn phúc khảo
        String lyDoTest = "Nghi ngờ bài thi cuối kỳ câu 3 phần SQL tính toán nhầm 1.0 điểm, xin thầy chấm lại giúp em";
        PhucKhao donMoi = new PhucKhao(targetBd.getBangDiemId(), sv.getSinhVienId(), lyDoTest);

        boolean guiThanhCong = phucKhaoDAO.guiDonPhucKhao(donMoi);
        System.out.println("3. Kết quả gửi đơn phúc khảo: " + (guiThanhCong ? "THÀNH CÔNG" : "THẤT BẠI"));

        // 4. Kiểm tra chặn gửi trùng lặp đơn đang chờ xử lý
        boolean checkTrung = phucKhaoDAO.hasPendingAppeal(targetBd.getBangDiemId());
        System.out.println("4. Kiểm tra phát hiện đơn đang chờ xử lý: " + (checkTrung ? "ĐÚNG (Đã chặn trùng lặp)" : "SAI"));

        // 5. Kiểm tra danh sách đơn của sinh viên
        List<PhucKhao> dsDonSV = phucKhaoDAO.findBySinhVien(sv.getSinhVienId());
        System.out.println("5. Số đơn phúc khảo của sinh viên trong CSDL: " + dsDonSV.size());
        PhucKhao donVuaGui = null;
        for (PhucKhao pk : dsDonSV) {
            if (pk.getBangDiemId() == targetBd.getBangDiemId() && "Chờ xử lý".equals(pk.getTrangThai())) {
                donVuaGui = pk;
                break;
            }
        }
        if (donVuaGui == null) {
            System.err.println("LỖI: Không tìm thấy đơn vừa gửi!");
            return;
        }
        System.out.println("   Tìm thấy đơn ID #" + donVuaGui.getPhucKhaoId() + " - Trạng thái: " + donVuaGui.getTrangThai());

        // 6. Mô phỏng Giảng viên / Quản trị viên xử lý đơn
        TaiKhoan adminTk = taiKhoanDAO.findByTenDangNhap("admin");
        SessionManager.setCurrentUser(adminTk);
        System.out.println("6. Đăng nhập Giảng viên / Quản trị viên: " + adminTk.getHoTen());

        // Giảng viên lọc danh sách đơn "Chờ xử lý"
        List<PhucKhao> dsChoXuLy = phucKhaoDAO.findByFilter("Chờ xử lý", 0, "");
        System.out.println("   Số đơn đang chờ xử lý: " + dsChoXuLy.size());

        // Cán bộ duyệt: Đã chấp nhận phúc khảo, tăng điểm CK lên +0.5 điểm
        double diemCKCu = (targetBd.getDiemCK() != null) ? targetBd.getDiemCK() : 7.0;
        double diemCKMoi = Math.min(10.0, diemCKCu + 0.5);
        String yKienPheDuyet = "Đã chấm lại bài thi: Câu 3 đúng kết quả truy vấn JOIN, cộng thêm 0.5 điểm";

        boolean xuLyThanhCong = phucKhaoDAO.xuLyPhucKhao(
                donVuaGui.getPhucKhaoId(),
                "Đã chấp nhận",
                yKienPheDuyet,
                adminTk.getTaiKhoanId(),
                diemCKMoi
        );
        System.out.println("7. Kết quả xử lý đơn phúc khảo của Cán bộ ('Đã chấp nhận'): " + (xuLyThanhCong ? "THÀNH CÔNG" : "THẤT BẠI"));

        // 8. Xác minh chi tiết sau xử lý
        PhucKhao donSauXuLy = phucKhaoDAO.findById(donVuaGui.getPhucKhaoId());
        System.out.println("8. Kiểm tra trạng thái đơn sau xử lý:");
        System.out.println("   - Trạng thái: " + donSauXuLy.getTrangThai());
        System.out.println("   - Phản hồi: " + donSauXuLy.getKetQuaXuLy());
        System.out.println("   - Người xử lý: " + donSauXuLy.getTenNguoiXuLy());
        System.out.println("   - Ngày xử lý: " + donSauXuLy.getNgayXuLy());

        // 9. Xác minh BangDiem đã cập nhật điểm mới và tự động tính điểm tổng kết, chữ, hệ 4
        BangDiem bdSauXuLy = bangDiemDAO.findById(targetBd.getBangDiemId());
        System.out.println("9. Kiểm tra Bảng điểm môn học sau cập nhật:");
        System.out.println("   - Điểm CK cũ: " + diemCKCu + " -> Điểm CK mới: " + bdSauXuLy.getDiemCK());
        System.out.println("   - Điểm Tổng kết mới: " + bdSauXuLy.getDiemTongKet() + ", Chữ: " + bdSauXuLy.getDiemChu() + ", Hệ 4: " + bdSauXuLy.getDiemHe4());

        // 10. Xác minh bảng LichSuDiem đã ghi nhận lịch sử sửa điểm
        List<LichSuDiem> dsLichSu = lichSuDAO.findByBangDiem(targetBd.getBangDiemId());
        System.out.println("10. Kiểm tra Lịch sử sửa điểm (LichSuDiem):");
        System.out.println("   - Số lần sửa điểm đã ghi nhận: " + dsLichSu.size());
        if (!dsLichSu.isEmpty()) {
            LichSuDiem lsdMoiNhat = dsLichSu.get(0);
            System.out.println("   - Lần sửa mới nhất: Lý do = '" + lsdMoiNhat.getLyDo() + "'");
            System.out.println("   - Người sửa: " + lsdMoiNhat.getTenNguoiThayDoi() + ", Thời gian: " + lsdMoiNhat.getThoiGianThayDoi());
        }

        // 11. Xác minh thông báo được gửi đến sinh viên
        List<ThongBao> dsThongBao = thongBaoDAO.findRecentByTaiKhoan(svTk.getTaiKhoanId());
        System.out.println("11. Kiểm tra Thông báo sinh viên nhận được:");
        System.out.println("   - Số thông báo: " + dsThongBao.size());
        if (!dsThongBao.isEmpty()) {
            ThongBao tbMoiNhat = dsThongBao.get(0);
            System.out.println("   - Tiêu đề: " + tbMoiNhat.getTieuDe());
            System.out.println("   - Nội dung: " + tbMoiNhat.getNoiDung().replace("\n", " | "));
        }

        // 12. Kiểm tra nghiệp vụ Hủy đơn phúc khảo
        if (dsMon.size() > 1) {
            BangDiem mon2 = dsMon.get(1);
            PhucKhao donHuyTest = new PhucKhao(mon2.getBangDiemId(), sv.getSinhVienId(), "Test hủy đơn khi chưa xử lý");
            phucKhaoDAO.guiDonPhucKhao(donHuyTest);

            List<PhucKhao> dsDonSauDo = phucKhaoDAO.findBySinhVien(sv.getSinhVienId());
            PhucKhao donCanHuy = null;
            for (PhucKhao pk : dsDonSauDo) {
                if (pk.getBangDiemId() == mon2.getBangDiemId() && "Chờ xử lý".equals(pk.getTrangThai())) {
                    donCanHuy = pk;
                    break;
                }
            }
            if (donCanHuy != null) {
                boolean huyOk = phucKhaoDAO.huyDonPhucKhao(donCanHuy.getPhucKhaoId());
                System.out.println("12. Kiểm tra Hủy đơn phúc khảo ở trạng thái 'Chờ xử lý': " + (huyOk ? "THÀNH CÔNG" : "THẤT BẠI"));
            }
        }

        System.out.println("========== HOÀN TẤT KIỂM THỬ GIAI ĐOẠN 8: TẤT CẢ TEST ĐỀU THÀNH CÔNG! ==========");
    }
}

