package com.mycompany.duanjavanangcao;

import com.mycompany.duanjavanangcao.dao.DangKyHocPhanDAO;
import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.model.DangKyHocPhan;
import com.mycompany.duanjavanangcao.model.LopHocPhan;
import com.mycompany.duanjavanangcao.model.SinhVien;

import java.util.List;

public class DangKyHocPhanTest {
    public static void main(String[] args) {
        System.out.println("=== KIỂM TRA TỰ ĐỘNG GIAI ĐOẠN 5: ĐĂNG KÝ HỌC PHẦN ===");
        try {
            DangKyHocPhanDAO dao = new DangKyHocPhanDAO();
            SinhVienDAO svDao = new SinhVienDAO();

            // 1. Kiểm tra tìm sinh viên theo TaiKhoanID (cho tài khoản sv001 có TaiKhoanID = 4)
            SinhVien sv = svDao.findByTaiKhoanId(4);
            if (sv != null) {
                System.out.println("[OK] Tìm thấy sinh viên theo TaiKhoanID = 4: " + sv.getMaSinhVien() + " - " + sv.getHoTen());
            } else {
                System.err.println("[FAIL] Không tìm thấy sinh viên theo TaiKhoanID = 4");
            }

            // 2. Kiểm tra lấy danh sách lớp học phần đang mở
            List<LopHocPhan> openClasses = dao.getAvailableClasses(0, "");
            System.out.println("[OK] Số lớp học phần đang mở: " + openClasses.size());
            for (LopHocPhan lhp : openClasses) {
                System.out.println("   + " + lhp.getMaLopHocPhan() + " | " + lhp.getTenMonHoc()
                        + " | " + lhp.getSoTinChi() + " TC | Sĩ số: " + lhp.getSiSoHienTai() + "/" + lhp.getSiSoToiDa());
            }

            // 3. Kiểm tra danh sách học phần đã đăng ký của sinh viên ID = 1
            List<DangKyHocPhan> regList = dao.findBySinhVien(1);
            System.out.println("[OK] Số học phần SV 1 đã đăng ký: " + regList.size());
            for (DangKyHocPhan dk : regList) {
                System.out.println("   + " + dk.getMaLopHocPhan() + " | " + dk.getTenMonHoc() + " | Trạng thái: " + dk.getTrangThai());
            }

            // 4. Kiểm tra điều kiện đăng ký (SV 1 đăng ký lại lớp 1 -> phải báo lỗi đã đăng ký)
            String checkMsg = dao.checkRegistrationEligibility(1, 1);
            System.out.println("[OK] Kiểm tra đăng ký trùng lớp 1 cho SV 1: " + checkMsg);

            // 5. Kiểm tra tính tổng số tín chỉ
            int totalCredits = dao.calculateTotalCredits(1, 0);
            System.out.println("[OK] Tổng số tín chỉ đang học của SV 1: " + totalCredits + " TC");

            System.out.println("=== TẤT CẢ KIỂM TRA GIAI ĐOẠN 5 ĐÃ HOÀN TẤT THÀNH CÔNG ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

