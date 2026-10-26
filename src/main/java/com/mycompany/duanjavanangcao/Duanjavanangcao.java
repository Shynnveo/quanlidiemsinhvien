package com.mycompany.duanjavanangcao;

import com.mycompany.duanjavanangcao.view.FrmDangNhap;

/**
 * Lớp khởi động chính của phần mềm Quản lý điểm sinh viên
 */
public class Duanjavanangcao {

    public static void main(String[] args) {
        // Thiết lập giao diện hệ thống Nimbus / System L&F
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        // Khởi chạy màn hình đăng nhập
        java.awt.EventQueue.invokeLater(() -> {
            new FrmDangNhap().setVisible(true);
        });
    }
}
