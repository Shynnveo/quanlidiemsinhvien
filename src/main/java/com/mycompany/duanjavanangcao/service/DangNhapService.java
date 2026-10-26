package com.mycompany.duanjavanangcao.service;

import com.mycompany.duanjavanangcao.dao.TaiKhoanDAO;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.PasswordUtil;
import com.mycompany.duanjavanangcao.util.SessionManager;

/**
 * Lớp xử lý nghiệp vụ xác thực đăng nhập
 */
public class DangNhapService {

    private final TaiKhoanDAO taiKhoanDAO;

    public DangNhapService() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    /**
     * Xác thực đăng nhập
     * @param tenDangNhap Tên đăng nhập người dùng nhập vào
     * @param matKhau Mật khẩu người dùng nhập vào
     * @return TaiKhoan nếu đăng nhập thành công
     * @throws Exception nếu có lỗi nhập liệu, tài khoản không đúng hoặc bị khóa
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) throws Exception {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập tên đăng nhập!");
        }

        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập mật khẩu!");
        }

        String username = tenDangNhap.trim();
        TaiKhoan tk = taiKhoanDAO.findByTenDangNhap(username);

        if (tk == null) {
            throw new Exception("Tên đăng nhập không tồn tại trên hệ thống!");
        }

        if (!tk.isTrangThai()) {
            throw new Exception("Tài khoản đang bị khóa, vui lòng liên hệ quản trị viên!");
        }

        if (!PasswordUtil.verifyPassword(matKhau, tk.getMatKhauHash())) {
            throw new Exception("Mật khẩu không chính xác!");
        }

        // Lưu thông tin người dùng vào phiên đăng nhập hiện tại
        SessionManager.setCurrentUser(tk);

        return tk;
    }
}

