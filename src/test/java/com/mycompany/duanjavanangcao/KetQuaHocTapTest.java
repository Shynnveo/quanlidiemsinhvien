package com.mycompany.duanjavanangcao;

import com.mycompany.duanjavanangcao.dao.KetQuaHocTapDAO;
import com.mycompany.duanjavanangcao.model.KetQuaHocTap;

import java.util.List;

public class KetQuaHocTapTest {
    public static void main(String[] args) {
        System.out.println("=== KIỂM TRA TỰ ĐỘNG GIAI ĐOẠN 7: KẾT QUẢ HỌC TẬP & TÍNH TOÁN GPA ===");
        try {
            KetQuaHocTapDAO dao = new KetQuaHocTapDAO();

            // 1. Tính toán GPA cho sinh viên ID = 1 (SV001) trong học kỳ ID = 1
            boolean calcSuccess = dao.calculateAndSave(1, 1);
            System.out.println("[OK] Tính toán GPA tự động cho SV 1 - Kỳ 1: " + (calcSuccess ? "THÀNH CÔNG" : "THẤT BẠI"));

            // 2. Tra cứu lại kết quả học tập vừa tính
            List<KetQuaHocTap> list = dao.findBySinhVien(1);
            System.out.println("[OK] Số bản ghi GPA của SV 1: " + list.size());
            for (KetQuaHocTap kq : list) {
                System.out.println("   + " + kq.getMaSinhVien() + " - " + kq.getHoTenSinhVien()
                        + " | Kỳ: " + kq.getTenHocKy()
                        + " | TC ĐK: " + kq.getSoTinChiDangKy()
                        + " | TC Đạt: " + kq.getSoTinChiDat()
                        + " | GPA Kỳ: " + kq.getGpaHocKy() + " (" + kq.getXepLoaiHocKy() + ")"
                        + " | Tổng TC TL: " + kq.getTongTinChiTichLuy()
                        + " | GPA Tích Lũy: " + kq.getGpaTichLuy() + " (" + kq.getXepLoaiTichLuy() + ")");
            }

            // 3. Kiểm tra tính toán hàng loạt cho cả học kỳ 1
            int countSemester = dao.calculateAndSaveForSemester(1);
            System.out.println("[OK] Tính toán GPA hàng loạt cho Học kỳ 1 hoàn tất cho: " + countSemester + " sinh viên.");

            System.out.println("=== TẤT CẢ KIỂM TRA GIAI ĐOẠN 7 ĐÃ HOÀN TẤT THÀNH CÔNG ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

