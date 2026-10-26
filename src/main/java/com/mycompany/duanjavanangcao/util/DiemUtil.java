package com.mycompany.duanjavanangcao.util;

/**
 * Tiện ích tính toán, làm tròn và quy đổi điểm số theo quy chế tín chỉ đại học
 */
public class DiemUtil {

    /**
     * Tính điểm tổng kết học phần theo thang điểm 10:
     * - Trọng số: Chuyên cần 20%, Giữa kỳ 30%, Cuối kỳ 50%
     * - Điều kiện: Nếu điểm Chuyên cần < 4.0 thì coi như cấm thi (Điểm tổng kết = 0.0)
     */
    public static Double tinhDiemTongKet(Double diemCC, Double diemGK, Double diemCK) {
        if (diemCC == null || diemGK == null || diemCK == null) {
            return null;
        }

        if (diemCC < 4.0) {
            return 0.0;
        }

        double rawScore = diemCC * 0.2 + diemGK * 0.3 + diemCK * 0.5;
        // Làm tròn đến 1 chữ số thập phân theo quy chế
        return Math.round(rawScore * 10.0) / 10.0;
    }

    /**
     * Quy đổi điểm hệ 10 sang Điểm chữ (A, B+, B, C+, C, D+, D, F)
     */
    public static String quyDoiDiemChu(Double diemTongKet) {
        if (diemTongKet == null) {
            return "";
        }
        if (diemTongKet >= 8.5) return "A";
        if (diemTongKet >= 8.0) return "B+";
        if (diemTongKet >= 7.0) return "B";
        if (diemTongKet >= 6.5) return "C+";
        if (diemTongKet >= 5.5) return "C";
        if (diemTongKet >= 5.0) return "D+";
        if (diemTongKet >= 4.0) return "D";
        return "F";
    }

    /**
     * Quy đổi điểm hệ 10 sang thang điểm 4
     */
    public static Double quyDoiDiemHe4(Double diemTongKet) {
        if (diemTongKet == null) {
            return null;
        }
        if (diemTongKet >= 8.5) return 4.0;
        if (diemTongKet >= 8.0) return 3.5;
        if (diemTongKet >= 7.0) return 3.0;
        if (diemTongKet >= 6.5) return 2.5;
        if (diemTongKet >= 5.5) return 2.0;
        if (diemTongKet >= 5.0) return 1.5;
        if (diemTongKet >= 4.0) return 1.0;
        return 0.0;
    }

    /**
     * Quy đổi kết quả đạt / học lại
     */
    public static String quyDoiKetQua(Double diemCC, Double diemTongKet) {
        if (diemCC == null || diemTongKet == null) {
            return "Chưa đủ điểm";
        }
        if (diemCC < 4.0) {
            return "Học lại (Cấm thi)";
        }
        return (diemTongKet >= 4.0) ? "Đạt" : "Học lại";
    }

    /**
     * Quy đổi xếp loại học phần
     */
    public static String quyDoiXepLoai(Double diemTongKet) {
        if (diemTongKet == null) {
            return "";
        }
        if (diemTongKet >= 8.5) return "Xuất sắc";
        if (diemTongKet >= 8.0) return "Giỏi";
        if (diemTongKet >= 7.0) return "Khá";
        if (diemTongKet >= 5.5) return "Trung bình";
        if (diemTongKet >= 4.0) return "Trung bình yếu";
        return "Kém";
    }

    /**
     * Quy đổi xếp loại học lực theo thang điểm 4 (GPA)
     */
    public static String xepLoaiGPA4(Double gpa4) {
        if (gpa4 == null) return "--";
        if (gpa4 >= 3.6) return "Xuất sắc";
        if (gpa4 >= 3.2) return "Giỏi";
        if (gpa4 >= 2.5) return "Khá";
        if (gpa4 >= 2.0) return "Trung bình";
        if (gpa4 >= 1.0) return "Yếu";
        return "Kém";
    }

    /**
     * Kiểm tra điểm số hợp lệ từ 0.0 đến 10.0
     */
    public static boolean isValidDiem(Double diem) {
        return diem != null && diem >= 0.0 && diem <= 10.0;
    }
}

