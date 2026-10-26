package com.mycompany.duanjavanangcao;

import com.mycompany.duanjavanangcao.util.UIThemeUtil;
import com.mycompany.duanjavanangcao.view.FrmDangNhap;

/**
 * Lớp khởi động chính của phần mềm Quản lý điểm sinh viên
 */
public class Duanjavanangcao {

    public static void main(String[] args) {
        // Thiết lập phong cách giao diện cổng quản lý đại học UTT
        UIThemeUtil.applyGlobalTheme();

        // Khởi chạy màn hình đăng nhập
        java.awt.EventQueue.invokeLater(() -> {
            new FrmDangNhap().setVisible(true);
        });
    }
}
