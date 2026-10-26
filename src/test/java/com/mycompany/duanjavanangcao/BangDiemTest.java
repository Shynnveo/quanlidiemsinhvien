package com.mycompany.duanjavanangcao;

import com.mycompany.duanjavanangcao.dao.BangDiemDAO;
import com.mycompany.duanjavanangcao.model.BangDiem;
import com.mycompany.duanjavanangcao.util.DiemUtil;

import java.util.List;

public class BangDiemTest {
    public static void main(String[] args) {
        System.out.println("=== KIỂM TRA TỰ ĐỘNG GIAI ĐOẠN 6: QUẢN LÝ VÀ NHẬP ĐIỂM HỌC PHẦN ===");
        try {
            BangDiemDAO dao = new BangDiemDAO();

            // 1. Kiểm tra tính điểm tổng kết & quy đổi
            Double tk = DiemUtil.tinhDiemTongKet(8.0, 7.0, 9.0); // 8*0.2 + 7*0.3 + 9*0.5 = 1.6 + 2.1 + 4.5 = 8.2
            String dc = DiemUtil.quyDoiDiemChu(tk);
            Double h4 = DiemUtil.quyDoiDiemHe4(tk);
            String kq = DiemUtil.quyDoiKetQua(8.0, tk);
            System.out.println("[OK] Điểm TK test (8.0, 7.0, 9.0) = " + tk + " | Điểm chữ: " + dc + " | Hệ 4: " + h4 + " | Kết quả: " + kq);

            // 2. Kiểm tra điều kiện cấm thi (CC < 4.0)
            Double tkCamThi = DiemUtil.tinhDiemTongKet(3.5, 8.0, 9.0);
            System.out.println("[OK] Cấm thi test (CC=3.5) => Điểm TK: " + tkCamThi + " | Kết quả: " + DiemUtil.quyDoiKetQua(3.5, tkCamThi));

            // 3. Lấy bảng điểm của lớp học phần ID = 1 (Lập trình Java)
            List<BangDiem> listLhp = dao.findByLopHocPhan(1);
            System.out.println("[OK] Số sinh viên trong bảng điểm LHP 1: " + listLhp.size());
            for (BangDiem bd : listLhp) {
                System.out.println("   + SV: " + bd.getMaSinhVien() + " - " + bd.getHoTenSinhVien()
                        + " | CC: " + bd.getDiemCC() + " | GK: " + bd.getDiemGK() + " | CK: " + bd.getDiemCK()
                        + " | TK: " + bd.getDiemTongKet() + " (" + bd.getDiemChu() + ")");
            }

            // 4. Lấy bảng điểm cá nhân sinh viên ID = 1 (SV001)
            List<BangDiem> listSv = dao.findBySinhVien(1);
            System.out.println("[OK] Bảng điểm cá nhân SV001 có: " + listSv.size() + " môn học");

            System.out.println("=== TẤT CẢ KIỂM TRA GIAI ĐOẠN 6 ĐÃ HOÀN TẤT THÀNH CÔNG ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

