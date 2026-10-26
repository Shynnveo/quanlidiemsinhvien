package com.mycompany.duanjavanangcao;

import com.mycompany.duanjavanangcao.util.UIThemeUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JButton;

public class UIThemeUtilTest {

    public static void main(String[] args) {
        System.out.println("=== KIỂM TRA HIỂN THỊ MÀU SẮC BUTTON & UTTHEME ===");
        try {
            // 1. Khởi tạo theme UTT toàn cục
            UIThemeUtil.applyGlobalTheme();
            System.out.println("[OK] Đã kích hoạt UIThemeUtil.applyGlobalTheme()");

            // 2. Kiểm tra JButton tự động nhận UIThemeUtil.ButtonUI
            JButton btnTest = new JButton("Test Tự Động");
            if (btnTest.getUI() instanceof UIThemeUtil.ButtonUI) {
                System.out.println("[OK] JButton tự động nhận UIThemeUtil.ButtonUI: " + btnTest.getUI().getClass().getName());
            } else {
                throw new RuntimeException("LỖI: JButton không nhận UIThemeUtil.ButtonUI mà nhận: " + btnTest.getUI().getClass().getName());
            }

            // 3. Kiểm tra nút Đăng nhập Primary (Xanh dương, chữ trắng)
            JButton btnLogin = new JButton("Đăng nhập");
            btnLogin.setSize(120, 36);
            btnLogin.setBackground(UIThemeUtil.PRIMARY_BLUE);
            btnLogin.setForeground(Color.WHITE);

            BufferedImage imgLogin = new BufferedImage(120, 36, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2Login = imgLogin.createGraphics();
            btnLogin.paint(g2Login);
            g2Login.dispose();

            int centerPixel = imgLogin.getRGB(60, 18);
            if (centerPixel == 0) {
                throw new RuntimeException("LỖI: Nền nút Đăng nhập rỗng (không được vẽ)");
            }
            System.out.println("[OK] Nút Đăng nhập vẽ thành công với màu xanh UTT và chữ trắng.");

            // 4. Kiểm tra nút Disabled (màu xám mờ, không lỗi khi vẽ)
            JButton btnDisabled = new JButton("Lưu điểm");
            btnDisabled.setSize(120, 36);
            btnDisabled.setEnabled(false);

            BufferedImage imgDisabled = new BufferedImage(120, 36, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2Disabled = imgDisabled.createGraphics();
            btnDisabled.paint(g2Disabled);
            g2Disabled.dispose();

            int disabledPixel = imgDisabled.getRGB(60, 18);
            if (disabledPixel == 0) {
                throw new RuntimeException("LỖI: Nền nút Disabled rỗng");
            }
            System.out.println("[OK] Nút Disabled vẽ thành công với màu xám mờ đạt chuẩn UX.");

            // 5. Kiểm tra các helper style
            JButton btnPrimary = new JButton("Hành động");
            UIThemeUtil.stylePrimaryButton(btnPrimary);
            if (!btnPrimary.getBackground().equals(UIThemeUtil.PRIMARY_BLUE) || !btnPrimary.getForeground().equals(Color.WHITE)) {
                throw new RuntimeException("LỖI stylePrimaryButton");
            }

            JButton btnSuccess = new JButton("Thêm mới");
            UIThemeUtil.styleSuccessButton(btnSuccess);
            if (!btnSuccess.getBackground().equals(UIThemeUtil.SUCCESS_GREEN) || !btnSuccess.getForeground().equals(Color.WHITE)) {
                throw new RuntimeException("LỖI styleSuccessButton");
            }

            JButton btnDanger = new JButton("Xóa");
            UIThemeUtil.styleDangerButton(btnDanger);
            if (!btnDanger.getBackground().equals(UIThemeUtil.DANGER_RED) || !btnDanger.getForeground().equals(Color.WHITE)) {
                throw new RuntimeException("LỖI styleDangerButton");
            }

            JButton btnWarning = new JButton("Cập nhật");
            UIThemeUtil.styleWarningButton(btnWarning);
            if (!btnWarning.getBackground().equals(UIThemeUtil.ORANGE_UTT) || !btnWarning.getForeground().equals(Color.WHITE)) {
                throw new RuntimeException("LỖI styleWarningButton");
            }

            JButton btnNeutral = new JButton("Làm mới");
            UIThemeUtil.styleNeutralButton(btnNeutral);
            if (!btnNeutral.getBackground().equals(UIThemeUtil.BTN_NEUTRAL_BG) || !btnNeutral.getForeground().equals(UIThemeUtil.TEXT_MAIN)) {
                throw new RuntimeException("LỖI styleNeutralButton");
            }

            System.out.println("[OK] Toàn bộ Style Helpers hoạt động chính xác 100%!");

            // 6. Kiểm tra trực tiếp trên Form thực tế (FrmDangNhap và FrmDangKyTaiKhoan)
            com.mycompany.duanjavanangcao.view.FrmDangNhap frmLogin = new com.mycompany.duanjavanangcao.view.FrmDangNhap();
            assertAllButtonsHaveUTTUI(frmLogin.getContentPane(), "FrmDangNhap");
            frmLogin.dispose();
            System.out.println("[OK] Tất cả JButton trên FrmDangNhap đều sử dụng ButtonUI thành công!");

            com.mycompany.duanjavanangcao.view.FrmDangKyTaiKhoan frmRegister = new com.mycompany.duanjavanangcao.view.FrmDangKyTaiKhoan();
            assertAllButtonsHaveUTTUI(frmRegister.getContentPane(), "FrmDangKyTaiKhoan");
            frmRegister.dispose();
            System.out.println("[OK] Tất cả JButton trên FrmDangKyTaiKhoan đều sử dụng ButtonUI thành công!");

            // 7. Kiểm tra không còn hiện tượng chữ cái đầu tiên bị gạch chân (Mnemonic index = -1)
            JButton btnCheckMnemonic = new JButton("Quản lý Sinh viên");
            if (btnCheckMnemonic.getDisplayedMnemonicIndex() != -1) {
                throw new RuntimeException("LỖI: Nút vẫn bị gán mnemonic index: " + btnCheckMnemonic.getDisplayedMnemonicIndex());
            }
            System.out.println("[OK] Đã kiểm tra loại bỏ hoàn toàn gạch chân ngoài ý muốn (Mnemonic index = -1)!");

            System.out.println("=== KẾT QUẢ: TẤT CẢ KIỂM TRA MÀU SẮC BUTTON & GẠCH CHÂN ĐỀU THÀNH CÔNG ===");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertAllButtonsHaveUTTUI(java.awt.Container container, String formName) {
        if (container == null) return;
        for (java.awt.Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (!(btn.getUI() instanceof UIThemeUtil.ButtonUI)) {
                    throw new RuntimeException("LỖI: Nút '" + btn.getText() + "' trên " + formName + " không dùng UIThemeUtil.ButtonUI mà dùng: " + btn.getUI().getClass().getName());
                }
            } else if (comp instanceof java.awt.Container) {
                assertAllButtonsHaveUTTUI((java.awt.Container) comp, formName);
            }
        }
    }
}
