package com.mycompany.duanjavanangcao.service;

import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.dao.TaiKhoanDAO;
import com.mycompany.duanjavanangcao.model.SinhVien;

/**
 * Lớp xử lý nghiệp vụ Đăng ký tài khoản sinh viên
 */
public class DangKyService {

    private final TaiKhoanDAO taiKhoanDAO;
    private final SinhVienDAO sinhVienDAO;

    public DangKyService() {
        this.taiKhoanDAO = new TaiKhoanDAO();
        this.sinhVienDAO = new SinhVienDAO();
    }

    /**
     * Xác thực dữ liệu và thực hiện đăng ký tài khoản cho sinh viên
     *
     * @param maSinhVien Mã sinh viên trong hồ sơ sinh viên UTT
     * @param tenDangNhap Tên đăng nhập người dùng muốn tạo
     * @param matKhau Mật khẩu
     * @param xacNhanMatKhau Mật khẩu xác nhận
     * @return SinhVien nếu đăng ký thành công
     * @throws Exception nếu thông tin không hợp lệ hoặc sinh viên không đủ điều kiện đăng ký
     */
    public SinhVien dangKyTaiKhoan(String maSinhVien, String tenDangNhap, String matKhau, String xacNhanMatKhau) throws Exception {
        // 1. Kiểm tra các trường bắt buộc không được để trống
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập Mã sinh viên!");
        }
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập Tên đăng nhập!");
        }
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập Mật khẩu!");
        }
        if (xacNhanMatKhau == null || xacNhanMatKhau.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập Xác nhận mật khẩu!");
        }

        String maSV = maSinhVien.trim();
        String username = tenDangNhap.trim();

        // 2. Kiểm tra mật khẩu xác nhận phải trùng khớp
        if (!matKhau.equals(xacNhanMatKhau)) {
            throw new Exception("Mật khẩu và Xác nhận mật khẩu không khớp!");
        }

        // 3. Kiểm tra độ dài mật khẩu tối thiểu (từ 6 ký tự trở lên)
        if (matKhau.length() < 6) {
            throw new Exception("Mật khẩu phải có độ dài tối thiểu từ 6 ký tự!");
        }

        // 4. Kiểm tra mã sinh viên phải tồn tại trong CSDL hồ sơ đào tạo
        SinhVien sv = sinhVienDAO.findByMaSinhVien(maSV);
        if (sv == null) {
            throw new Exception("Mã sinh viên '" + maSV + "' không tồn tại trong hệ thống đào tạo!\nVui lòng kiểm tra lại hoặc liên hệ Phòng Đào tạo UTT.");
        }

        // 5. Kiểm tra ràng buộc duy nhất: Mỗi sinh viên chỉ được sở hữu 1 tài khoản
        if (sv.getTaiKhoanId() != null && sv.getTaiKhoanId() > 0) {
            throw new Exception("Sinh viên '" + sv.getHoTen() + "' (Mã: " + maSV + ") đã có tài khoản trên hệ thống!\nMỗi sinh viên chỉ được phép đăng ký duy nhất 1 tài khoản.");
        }

        // 6. Kiểm tra tên đăng nhập không được trùng với tài khoản đã có
        if (taiKhoanDAO.existsByTenDangNhap(username)) {
            throw new Exception("Tên đăng nhập '" + username + "' đã được sử dụng!\nVui lòng chọn một tên đăng nhập khác.");
        }

        // 7. Thực hiện lưu tài khoản và liên kết với sinh viên trong giao dịch SQL an toàn
        boolean ketQua = taiKhoanDAO.dangKyTaiKhoanSinhVien(
                sv.getSinhVienId(),
                username,
                matKhau,
                sv.getHoTen(),
                sv.getEmail(),
                sv.getSoDienThoai()
        );

        if (!ketQua) {
            throw new Exception("Đăng ký tài khoản không thành công do lỗi hệ thống! Vui lòng thử lại sau.");
        }

        return sv;
    }
}

