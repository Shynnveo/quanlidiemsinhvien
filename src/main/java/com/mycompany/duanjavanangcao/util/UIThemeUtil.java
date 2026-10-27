package com.mycompany.duanjavanangcao.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicPanelUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Tiện ích quản lý giao diện toàn cục UTT Design System.
 * Tích hợp toàn bộ bảng màu, phông chữ Segoe UI, chuẩn hóa JTable
 * và bộ UI delegate xử lý màu sắc và tương tác cho JButton.
 */
public class UIThemeUtil {

    // Bảng màu chuẩn UTT Management System
    public static final Color NAVY_UTT = new Color(23, 54, 93);       // #17365D - Xanh navy UTT
    public static final Color ORANGE_UTT = new Color(242, 140, 40);   // #F28C28 - Màu cam điểm nhấn UTT
    public static final Color PRIMARY_BLUE = new Color(37, 99, 235);  // #2563EB - Hành động chính
    public static final Color ACCENT_BLUE = new Color(29, 104, 254);   // #1D68FE - Nút Đăng nhập nổi bật
    public static final Color LINK_AMBER = new Color(217, 119, 6);     // #D97706 - Link đăng ký màu hổ phách
    public static final Color TEXT_DARK = new Color(15, 23, 42);       // #0F172A - Tiêu đề đậm
    public static final Color BRAND_NAVY_TOP = new Color(15, 29, 56);     // #0F1D38
    public static final Color BRAND_NAVY_BOTTOM = new Color(6, 12, 24);    // #060C18
    public static final Color BRAND_CYAN = new Color(56, 189, 248);       // #38BDF8
    public static final Color BRAND_BLUE_LIGHT = new Color(147, 197, 253); // #93C5FD
    public static final Color SUCCESS_GREEN = new Color(21, 128, 61); // #15803D - Thêm mới / Thành công
    public static final Color DANGER_RED = new Color(220, 38, 38);    // #DC2626 - Xóa / Đăng xuất / Cảnh báo
    public static final Color BG_LIGHT = new Color(244, 247, 251);    // #F4F7FB - Nền form tổng thể
    public static final Color PANEL_WHITE = new Color(255, 255, 255); // #FFFFFF - Panel nội dung
    public static final Color TEXT_MAIN = new Color(31, 41, 55);      // #1F2937 - Chữ chính
    public static final Color TEXT_MUTED = new Color(100, 116, 139);  // #64748B - Chữ phụ
    public static final Color BORDER_LIGHT = new Color(216, 224, 234);// #D8E0EA - Viền nhẹ nhàng
    public static final Color BTN_NEUTRAL_BG = new Color(241, 245, 249);// #F1F5F9 - Nút phụ
    public static final Color ROW_SELECTED_BG = new Color(219, 234, 254);// #DBEAFE - Dòng được chọn
    public static final Color ROW_SELECTED_FG = new Color(23, 54, 93);   // #17365D - Chữ dòng được chọn

    // Phông chữ Segoe UI chuẩn
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Thiết lập Look and Feel và bảng mã font Segoe UI toàn cục cho hệ thống
     */
    public static void applyGlobalTheme() {
        try {
            // Ưu tiên giao diện Windows hiện đại nếu có, sau đó là Nimbus
            boolean lafFound = false;
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    lafFound = true;
                    break;
                }
            }
            if (!lafFound) {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }

            // Chuẩn hóa font chữ Segoe UI cho toàn bộ các thành phần Swing
            javax.swing.UIManager.put("Label.font", FONT_NORMAL);
            javax.swing.UIManager.put("Button.font", FONT_BUTTON);
            javax.swing.UIManager.put("TextField.font", FONT_NORMAL);
            javax.swing.UIManager.put("PasswordField.font", FONT_NORMAL);
            javax.swing.UIManager.put("ComboBox.font", FONT_NORMAL);
            javax.swing.UIManager.put("Table.font", FONT_NORMAL);
            javax.swing.UIManager.put("TableHeader.font", FONT_BUTTON);
            javax.swing.UIManager.put("TitledBorder.font", FONT_SUBTITLE);
            javax.swing.UIManager.put("TitledBorder.titleColor", NAVY_UTT);
            javax.swing.UIManager.put("Panel.background", BG_LIGHT);
            javax.swing.UIManager.put("OptionPane.messageFont", FONT_NORMAL);
            javax.swing.UIManager.put("OptionPane.buttonFont", FONT_BUTTON);

            // Khắc phục triệt để lỗi màu sắc JButton trên Windows/Nimbus L&F:
            // Đăng ký ButtonUI toàn cục để vẽ chuẩn màu nền và màu chữ
            javax.swing.UIManager.getDefaults().put("ClassLoader", UIThemeUtil.class.getClassLoader());
            javax.swing.UIManager.put("ButtonUI", ButtonUI.class.getName());
            javax.swing.UIManager.getDefaults().put("ButtonUI", ButtonUI.class.getName());
        } catch (Exception ignored) {
        }
    }

    /**
     * Trả về UI Delegate dùng chung cho JButton
     */
    public static ButtonUI getButtonUI() {
        return ButtonUI.INSTANCE;
    }

    /**
     * Áp dụng ButtonUI thủ công cho một hoặc nhiều JButton
     */
    public static void formatButton(JButton... buttons) {
        if (buttons == null) return;
        for (JButton btn : buttons) {
            if (btn != null) {
                btn.setUI(getButtonUI());
            }
        }
    }

    /**
     * Duyệt đệ quy toàn bộ container/JFrame để đảm bảo 100% mọi JButton bên trong
     * đều sử dụng ButtonUI chuẩn, bảo toàn màu nền và màu chữ sắc nét
     */
    public static void applyThemeToContainer(java.awt.Container container) {
        if (container == null) return;
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (!(btn.getUI() instanceof ButtonUI)) {
                    btn.setUI(getButtonUI());
                }
            } else if (comp instanceof java.awt.Container) {
                applyThemeToContainer((java.awt.Container) comp);
            }
        }
    }

    /**
     * Định kiểu nút hành động chính (Xanh dương)
     */
    public static void stylePrimaryButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(getButtonUI());
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút thành công / thêm mới (Xanh lá)
     */
    public static void styleSuccessButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(getButtonUI());
        btn.setBackground(SUCCESS_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút cảnh báo / xóa / đăng xuất (Đỏ)
     */
    public static void styleDangerButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(getButtonUI());
        btn.setBackground(DANGER_RED);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút nhấn mạnh / cam (Cam UTT)
     */
    public static void styleWarningButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(getButtonUI());
        btn.setBackground(ORANGE_UTT);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút phụ / neutral (Xám nhạt chữ đậm)
     */
    public static void styleNeutralButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(getButtonUI());
        btn.setBackground(BTN_NEUTRAL_BG);
        btn.setForeground(TEXT_MAIN);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút phẳng dạng liên kết văn bản (Text Link Button)
     */
    public static void styleLinkButton(JButton btn, Color textColor) {
        if (btn == null) return;
        btn.setUI(getButtonUI());
        btn.putClientProperty("isLink", Boolean.TRUE);
        btn.setForeground(textColor != null ? textColor : LINK_AMBER);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Định kiểu JLabel thành liên kết văn bản có hiệu ứng hover và click
     */
    public static void styleLinkLabel(JLabel lbl, Color normalColor, Color hoverColor, Runnable onClick) {
        if (lbl == null) return;
        Color normal = normalColor != null ? normalColor : TEXT_MUTED;
        Color hover = hoverColor != null ? hoverColor : TEXT_MAIN;
        lbl.setForeground(normal);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lbl.setForeground(hover);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lbl.setForeground(normal);
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    /**
     * Phân loại biểu tượng trang trí trong ô nhập liệu
     */
    public enum IconType { USER, LOCK, NONE }

    /**
     * Bo góc hiện đại và thêm biểu tượng tinh tế cho ô nhập liệu (JTextField / JPasswordField)
     */
    public static void styleInputField(JTextField field, IconType iconType) {
        if (field == null) return;
        RoundedIconBorder border = new RoundedIconBorder(iconType);
        field.setOpaque(false);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(border);
        if (field instanceof JPasswordField) {
            field.setUI(new RoundedPasswordFieldUI());
        } else {
            field.setUI(new RoundedTextFieldUI());
        }
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                border.setFocused(true);
                field.repaint();
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                border.setFocused(false);
                field.repaint();
            }
        });
    }

    /**
     * Áp dụng thẻ bo góc thương hiệu UTT với nền gradient và đồ họa sóng số
     */
    public static void applyBrandCard(JPanel panel) {
        if (panel == null) return;
        panel.setOpaque(false);
        panel.setUI(new BrandCardPanelUI(18));
    }

    /**
     * Áp dụng thẻ trắng bo góc hiện đại kèm bóng đổ nhẹ
     */
    public static void applyWhiteCard(JPanel panel) {
        if (panel == null) return;
        panel.setOpaque(false);
        panel.setUI(new CardPanelUI(18, Color.WHITE, new Color(226, 232, 240)));
    }

    /**
     * Áp dụng toàn bộ thiết kế giao diện đăng nhập hiện đại theo chuẩn UTT Design System
     * Hỗ trợ tách biệt riêng biệt hành động "Quên mật khẩu?" và "Hỗ trợ"
     */
    public static void applyLoginTheme(
            JFrame frame,
            JPanel pnlBrand,
            JPanel pnlLoginForm,
            JTextField txtTenDangNhap,
            JPasswordField txtMatKhau,
            JButton btnDangNhap,
            JButton btnThoat,
            JButton btnDangKy,
            JLabel lblQuenMatKhau,
            JLabel lblHoTro,
            Runnable quenMatKhauAction,
            Runnable hoTroAction
    ) {
        applyGlobalTheme();
        if (frame != null && frame.getContentPane() != null) {
            frame.getContentPane().setBackground(BG_LIGHT);
        }

        applyBrandCard(pnlBrand);
        applyWhiteCard(pnlLoginForm);

        styleInputField(txtTenDangNhap, IconType.USER);
        styleInputField(txtMatKhau, IconType.LOCK);

        if (btnDangNhap != null) {
            btnDangNhap.setUI(getButtonUI());
            btnDangNhap.setBackground(ACCENT_BLUE);
            btnDangNhap.setForeground(Color.WHITE);
            btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
        if (btnThoat != null) {
            styleNeutralButton(btnThoat);
            btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
        if (btnDangKy != null) {
            styleLinkButton(btnDangKy, LINK_AMBER);
        }

        if (lblQuenMatKhau != null) {
            styleLinkLabel(lblQuenMatKhau, TEXT_MUTED, TEXT_DARK, quenMatKhauAction);
        }

        if (lblHoTro != null) {
            styleLinkLabel(lblHoTro, TEXT_MUTED, TEXT_DARK, hoTroAction);
        }

        applyThemeToContainer(frame);
    }

    /**
     * Áp dụng toàn bộ thiết kế giao diện đăng nhập (Tương thích ngược)
     */
    public static void applyLoginTheme(
            JFrame frame,
            JPanel pnlBrand,
            JPanel pnlLoginForm,
            JTextField txtTenDangNhap,
            JPasswordField txtMatKhau,
            JButton btnDangNhap,
            JButton btnThoat,
            JButton btnDangKy,
            JLabel lblQuenMatKhau
    ) {
        applyLoginTheme(frame, pnlBrand, pnlLoginForm, txtTenDangNhap, txtMatKhau, btnDangNhap, btnThoat, btnDangKy, lblQuenMatKhau, null, null, null);
    }

    /**
     * Định nghĩa các kiểu thẻ thống kê tổng quan (Stat Cards)
     */
    public enum StatCardType {
        SINH_VIEN("SINH VIÊN", new Color(235, 245, 255), new Color(191, 219, 254), new Color(37, 99, 235), "GRAD_CAP"),
        GIANG_VIEN("GIẢNG VIÊN", new Color(236, 253, 245), new Color(187, 247, 208), new Color(22, 163, 74), "TEACHER"),
        LOP_HOC("LỚP HỌC", new Color(241, 245, 249), new Color(226, 232, 240), new Color(30, 41, 59), "GROUP"),
        MON_HOC("MÔN HỌC", new Color(255, 247, 237), new Color(254, 215, 170), new Color(234, 88, 12), "BOOK"),
        LOP_HOC_PHAN("LỚP HỌC PHẦN", new Color(254, 243, 199), new Color(253, 230, 138), new Color(194, 65, 12), "DOCUMENT");

        private final String title;
        private final Color bgColor;
        private final Color borderColor;
        private final Color valueColor;
        private final String iconType;

        StatCardType(String title, Color bgColor, Color borderColor, Color valueColor, String iconType) {
            this.title = title;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            this.valueColor = valueColor;
            this.iconType = iconType;
        }

        public String getTitle() { return title; }
        public Color getBgColor() { return bgColor; }
        public Color getBorderColor() { return borderColor; }
        public Color getValueColor() { return valueColor; }
        public String getIconType() { return iconType; }
    }

    /**
     * Khởi tạo định dạng thẻ thống kê tổng quan với nền bo góc và icon vector
     */
    public static void formatStatCard(JLabel lbl, StatCardType type, int count) {
        if (lbl == null || type == null) return;
        lbl.setOpaque(false);
        lbl.setUI(new StatCardUI(type));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        updateStatCard(lbl, type, count);
    }

    /**
     * Cập nhật số liệu trên thẻ thống kê tổng quan
     */
    public static void updateStatCard(JLabel lbl, StatCardType type, int count) {
        if (lbl == null || type == null) return;
        lbl.putClientProperty("statCount", Integer.valueOf(count));
        lbl.putClientProperty("statType", type);
        lbl.setText(type.getTitle() + ": " + count);
        lbl.repaint();
    }

    /**
     * Định kiểu nút Thao tác nhanh (Quick Action Button) với màu pastel hiện đại và icon vector sắc nét
     */
    public static void styleQuickActionButton(JButton btn, Color bgColor, Color fgColor, QuickActionIcon.ActionType actionType, String titleText) {
        if (btn == null) return;
        btn.setUI(getButtonUI());
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setIcon(new QuickActionIcon(actionType, fgColor));
        btn.setIconTextGap(8);
        btn.setText(titleText != null ? titleText.trim() : "");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }

    /**
     * Định kiểu nút Thao tác nhanh (Overload hỗ trợ tự động chuyển đổi sang vector icon)
     */
    public static void styleQuickActionButton(JButton btn, Color bgColor, Color fgColor, String iconText, String titleText) {
        QuickActionIcon.ActionType type = QuickActionIcon.ActionType.NHAP_DIEM;
        if (titleText != null) {
            String lower = titleText.toLowerCase();
            if (lower.contains("tra cứu")) {
                type = QuickActionIcon.ActionType.TRA_CUU;
            } else if (lower.contains("gpa") || lower.contains("kết quả")) {
                type = QuickActionIcon.ActionType.GPA;
            } else if (lower.contains("báo cáo") || lower.contains("thống kê")) {
                type = QuickActionIcon.ActionType.BAO_CAO;
            }
        }
        styleQuickActionButton(btn, bgColor, fgColor, type, titleText);
    }

    /**
     * Định kiểu huy hiệu tròn Avatar người dùng trên thanh tiêu đề Header
     */
    public static void styleAvatarBadge(JLabel lbl) {
        if (lbl == null) return;
        lbl.setText("");
        lbl.setOpaque(false);
        lbl.setBorder(new AvatarCircleBorder());
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Định dạng JTree chức năng theo phong cách hiện đại với các icon cây thư mục rõ ràng
     */
    public static void formatModernTree(JTree tree) {
        if (tree == null) return;
        tree.setRowHeight(34);
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tree.setBackground(Color.WHITE);
        tree.setOpaque(true);
        tree.setCellRenderer(new ModernTreeRenderer());
    }

    /**
     * Áp dụng toàn bộ thiết kế giao diện Trang chủ (Dashboard) theo chuẩn UTT Design System
     */
    public static void applyTrangChuTheme(
            JFrame frame,
            JPanel pnlHeader,
            JPanel pnlMenuTree,
            JPanel pnlThongKe,
            JPanel pnlThongBao,
            JPanel pnlThaoTacNhanh,
            JLabel lblAvatar,
            JButton btnDangXuat,
            JButton btnQuickNhapDiem,
            JButton btnQuickTraCuu,
            JButton btnQuickGPA,
            JButton btnQuickBaoCao,
            JTree treeMenu,
            JTable tblThongBao,
            JButton btnXemThongBao,
            JButton btnLamMoiThongBao
    ) {
        applyGlobalTheme();
        if (frame != null && frame.getContentPane() != null) {
            frame.getContentPane().setBackground(BG_LIGHT);
        }

        if (pnlHeader != null) {
            pnlHeader.setBackground(NAVY_UTT);
        }
        if (pnlThongKe != null && pnlThongKe.getBorder() != null) {
            javax.swing.border.Border outer = pnlThongKe.getBorder();
            if (!(outer instanceof javax.swing.border.CompoundBorder)) {
                pnlThongKe.setBorder(BorderFactory.createCompoundBorder(outer, BorderFactory.createEmptyBorder(6, 4, 4, 4)));
            }
        }
        if (lblAvatar != null) {
            styleAvatarBadge(lblAvatar);
        }
        if (btnDangXuat != null) {
            btnDangXuat.setUI(getButtonUI());
            btnDangXuat.setBackground(new Color(239, 68, 68)); // #EF4444
            btnDangXuat.setForeground(Color.WHITE);
            btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        // Định dạng 4 nút thao tác nhanh với màu sắc pastel chuẩn UX và icon vector sắc nét (không dùng emoji chuỗi tránh lỗi tofu)
        styleQuickActionButton(btnQuickNhapDiem, new Color(224, 242, 254), new Color(2, 132, 199), QuickActionIcon.ActionType.NHAP_DIEM, "Nhập điểm");
        styleQuickActionButton(btnQuickTraCuu, new Color(220, 252, 231), new Color(22, 163, 74), QuickActionIcon.ActionType.TRA_CUU, "Tra cứu điểm");
        styleQuickActionButton(btnQuickGPA, new Color(224, 231, 255), new Color(79, 70, 229), QuickActionIcon.ActionType.GPA, "Kết quả GPA");
        styleQuickActionButton(btnQuickBaoCao, new Color(255, 237, 213), new Color(234, 88, 12), QuickActionIcon.ActionType.BAO_CAO, "Thống kê & Báo cáo");

        // Định dạng cây menu và bảng dữ liệu
        formatModernTree(treeMenu);
        formatTable(tblThongBao);

        if (btnXemThongBao != null) {
            stylePrimaryButton(btnXemThongBao);
        }
        if (btnLamMoiThongBao != null) {
            styleNeutralButton(btnLamMoiThongBao);
        }

        applyThemeToContainer(frame);
    }

    /**
     * Chuẩn hóa bảng dữ liệu JTable theo phong cách cổng đại học
     * @param table JTable cần định dạng
     */
    public static void formatTable(JTable table) {
        if (table == null) return;

        table.setFont(FONT_NORMAL);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(ROW_SELECTED_BG);
        table.setSelectionForeground(ROW_SELECTED_FG);
        table.setFillsViewportHeight(true);

        // Header bảng
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(new Font("Segoe UI", Font.BOLD, 13));
            header.setBackground(NAVY_UTT);
            header.setForeground(Color.WHITE);
            header.setPreferredSize(new Dimension(header.getWidth(), 34));
            header.setOpaque(true);
            header.setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable tbl, Object value,
                        boolean isSelected, boolean hasFocus, int row, int col) {
                    JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                    lbl.setBackground(NAVY_UTT);
                    lbl.setForeground(Color.WHITE);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    lbl.setHorizontalAlignment(JLabel.CENTER);
                    lbl.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(30, 68, 115)),
                            BorderFactory.createEmptyBorder(6, 4, 6, 4)));
                    return lbl;
                }
            });
        }
    }

    /**
     * Custom ButtonUI tích hợp trực tiếp trong UIThemeUtil.
     * Đảm bảo mọi JButton hiển thị chính xác màu nền và màu chữ theo thiết kế UTT,
     * khắc phục lỗi Windows L&F bỏ qua background của button,
     * hỗ trợ hiệu ứng hover/pressed và vô hiệu hóa đúng chuẩn UX.
     */
    public static class ButtonUI extends BasicButtonUI {

        private static final ButtonUI INSTANCE = new ButtonUI();

        public static ComponentUI createUI(JComponent c) {
            return INSTANCE;
        }

        public static ButtonUI getSharedInstance() {
            return INSTANCE;
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            if (c instanceof AbstractButton) {
                AbstractButton b = (AbstractButton) c;
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setRolloverEnabled(true);
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            if (Boolean.TRUE.equals(b.getClientProperty("isLink"))) {
                super.paint(g, c);
                return;
            }

            ButtonModel model = b.getModel();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = b.getWidth();
            int height = b.getHeight();

            Color bg = b.getBackground();
            if (bg == null) {
                bg = BTN_NEUTRAL_BG;
            }

            if (!b.isEnabled()) {
                bg = new Color(226, 232, 240); // #E2E8F0 mờ khi disable
            } else if (model.isPressed()) {
                bg = darken(bg, 0.15f);
            } else if (model.isRollover()) {
                bg = brighten(bg, 0.12f);
            }

            // Vẽ nền phẳng bo góc hiện đại 10px
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, width, height, 10, 10);

            // Nếu là nút màu sáng (nút phụ neutral), vẽ thêm viền nhẹ để phân cách
            if (b.isEnabled() && isLightColor(bg)) {
                g2.setColor(new Color(203, 213, 225)); // #CBD5E1
                g2.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);
            }

            g2.dispose();
            super.paint(g, c);
        }

        @Override
        protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(b.getFont());

            FontMetrics fm = g2.getFontMetrics();

            if (Boolean.TRUE.equals(b.getClientProperty("isLink"))) {
                Color fg = b.getForeground();
                if (fg == null) fg = LINK_AMBER;
                if (b.getModel().isRollover()) {
                    fg = brighten(fg, 0.2f);
                }
                g2.setColor(fg);
                int textY = textRect.y + fm.getAscent();
                g2.drawString(text, textRect.x, textY);
                if (b.getModel().isRollover()) {
                    g2.drawLine(textRect.x, textY + 2, textRect.x + fm.stringWidth(text), textY + 2);
                }
                g2.dispose();
                return;
            }

            if (!b.isEnabled()) {
                g2.setColor(new Color(148, 163, 184)); // #94A3B8 chữ mờ khi disable
            } else {
                Color fg = b.getForeground();
                if (fg == null) {
                    fg = TEXT_MAIN;
                }
                g2.setColor(fg);
            }

            int mnemonicIndex = b.getDisplayedMnemonicIndex();
            BasicGraphicsUtils.drawStringUnderlineCharAt(g2, text, mnemonicIndex,
                    textRect.x, textRect.y + fm.getAscent());
            g2.dispose();
        }

        private Color darken(Color color, float fraction) {
            int red = Math.max(0, Math.round(color.getRed() * (1f - fraction)));
            int green = Math.max(0, Math.round(color.getGreen() * (1f - fraction)));
            int blue = Math.max(0, Math.round(color.getBlue() * (1f - fraction)));
            return new Color(red, green, blue, color.getAlpha());
        }

        private Color brighten(Color color, float fraction) {
            int red = Math.min(255, Math.round(color.getRed() * (1f + fraction) + (fraction * 30)));
            int green = Math.min(255, Math.round(color.getGreen() * (1f + fraction) + (fraction * 30)));
            int blue = Math.min(255, Math.round(color.getBlue() * (1f + fraction) + (fraction * 30)));
            return new Color(red, green, blue, color.getAlpha());
        }

        private boolean isLightColor(Color c) {
            double yiq = ((c.getRed() * 299) + (c.getGreen() * 587) + (c.getBlue() * 114)) / 1000.0;
            return yiq >= 180;
        }
    }

    /**
     * UI Delegate vẽ nền thẻ Panel bo góc hiện đại với bóng mờ êm dịu
     */
    public static class CardPanelUI extends BasicPanelUI {
        private final int radius;
        private final Color bgColor;
        private final Color borderColor;

        public CardPanelUI(int radius, Color bgColor, Color borderColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
        }

        @Override
        public void update(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = c.getWidth();
            int h = c.getHeight();

            // Bóng đổ nhẹ xung quanh
            g2.setColor(new Color(15, 23, 42, 8));
            g2.fillRoundRect(2, 2, w - 4, h - 4, radius, radius);

            // Nền thẻ
            g2.setColor(bgColor != null ? bgColor : Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

            // Viền phân cách
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
            }
            g2.dispose();
        }
    }

    /**
     * UI Delegate vẽ nền thẻ thương hiệu UTT bo góc kèm gradient sâu thẳm
     * và đồ họa sóng số (Digital Tech Wave) phát sáng hiện đại.
     */
    public static class BrandCardPanelUI extends BasicPanelUI {
        private final int radius;

        public BrandCardPanelUI(int radius) {
            this.radius = radius;
        }

        @Override
        public void update(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = c.getWidth();
            int h = c.getHeight();

            // Cắt góc bo tròn
            RoundRectangle2D cardShape = new RoundRectangle2D.Float(0, 0, w, h, radius, radius);
            g2.setClip(cardShape);

            // Vẽ gradient nền xanh Navy UTT chuyên nghiệp
            GradientPaint bgGradient = new GradientPaint(
                    0, 0, BRAND_NAVY_TOP,
                    0, h, BRAND_NAVY_BOTTOM
            );
            g2.setPaint(bgGradient);
            g2.fillRect(0, 0, w, h);

            // Vẽ hiệu ứng sóng số lượng tử công nghệ (Digital Wave)
            drawDigitalWave(g2, w, h);

            // Viền bo mờ bảo vệ thẻ
            g2.setClip(null);
            g2.setColor(new Color(255, 255, 255, 20));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);

            g2.dispose();
        }

        private void drawDigitalWave(Graphics2D g2, int w, int h) {
            int waveCenterY = (int) (h * 0.65);

            // Vẽ lưới dọc dữ liệu số (digital mesh grid)
            g2.setStroke(new BasicStroke(0.8f));
            for (int x = 20; x < w - 20; x += 16) {
                double norm = (x - 20.0) / (w - 40);
                double sin1 = Math.sin(norm * Math.PI * 2.4) * 26.0;
                double sin2 = Math.cos(norm * Math.PI * 3.0) * 18.0;
                int y1 = (int) (waveCenterY + sin1);
                int y2 = (int) (waveCenterY + sin2);

                g2.setColor(new Color(56, 189, 248, 35));
                g2.drawLine(x, Math.min(y1, y2), x, Math.max(y1, y2));

                if (x % 32 == 0) {
                    g2.setColor(new Color(56, 189, 248, 150));
                    g2.fillOval(x - 2, y1 - 2, 4, 4);
                }
            }

            // Vẽ các dải sóng sin lượng tử phát sáng uốn lượn
            drawSineWave(g2, w, waveCenterY, 30, 2.3, 0.0, new Color(56, 189, 248, 190), 1.6f);
            drawSineWave(g2, w, waveCenterY + 7, 24, 2.9, 1.3, new Color(37, 99, 235, 170), 1.4f);
            drawSineWave(g2, w, waveCenterY - 8, 20, 1.9, 2.6, new Color(129, 140, 248, 140), 1.2f);
            drawSineWave(g2, w, waveCenterY + 14, 16, 3.4, 0.7, new Color(30, 64, 175, 120), 1.0f);
        }

        private void drawSineWave(Graphics2D g2, int w, int centerY, double amplitude, double frequency, double phase, Color color, float strokeWidth) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D path = new Path2D.Double();
            boolean first = true;
            for (int x = 10; x <= w - 10; x += 2) {
                double norm = (double) (x - 10) / (w - 20);
                double damping = Math.sin(norm * Math.PI);
                double y = centerY + Math.sin(norm * Math.PI * frequency + phase) * amplitude * damping;
                if (first) {
                    path.moveTo(x, y);
                    first = false;
                } else {
                    path.lineTo(x, y);
                }
            }
            g2.draw(path);
        }
    }

    /**
     * Viền bo góc tích hợp vẽ icon biểu tượng (Người dùng / Ổ khóa) trên ô nhập liệu
     */
    public static class RoundedIconBorder implements Border {
        private final IconType iconType;
        private final int radius = 10;
        private boolean focused = false;

        public RoundedIconBorder(IconType iconType) {
            this.iconType = iconType != null ? iconType : IconType.NONE;
        }

        public void setFocused(boolean focused) {
            this.focused = focused;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // Viền bo góc hiện đại
            if (focused) {
                g2.setColor(ACCENT_BLUE);
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);

            // Vẽ Icon bên trái ô nhập liệu
            if (iconType != IconType.NONE) {
                int iconX = x + 12;
                int centerY = y + height / 2;
                Color iconColor = focused ? ACCENT_BLUE : new Color(148, 163, 184); // #94A3B8
                g2.setColor(iconColor);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                if (iconType == IconType.USER) {
                    // Đầu người dùng (hình tròn)
                    g2.drawOval(iconX + 3, centerY - 8, 8, 8);
                    // Vai người dùng (cung tròn)
                    g2.drawArc(iconX, centerY, 14, 12, 0, 180);
                } else if (iconType == IconType.LOCK) {
                    // Càng khóa
                    g2.drawRoundRect(iconX + 3, centerY - 8, 8, 8, 4, 4);
                    // Thân khóa
                    g2.drawRoundRect(iconX + 1, centerY - 2, 12, 10, 3, 3);
                    // Lỗ khóa
                    g2.drawLine(iconX + 7, centerY + 1, iconX + 7, centerY + 4);
                }
            }

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, iconType != IconType.NONE ? 38 : 12, 8, 12);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /**
     * UI Delegate vẽ nền bo góc trắng cho JTextField
     */
    public static class RoundedTextFieldUI extends BasicTextFieldUI {
        private final int radius = 10;
        @Override
        protected void paintBackground(Graphics g) {
            JComponent c = getComponent();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground() != null ? c.getBackground() : Color.WHITE);
            g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }

    /**
     * UI Delegate vẽ nền bo góc trắng cho JPasswordField
     */
    public static class RoundedPasswordFieldUI extends BasicPasswordFieldUI {
        private final int radius = 10;
        @Override
        protected void paintBackground(Graphics g) {
            JComponent c = getComponent();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground() != null ? c.getBackground() : Color.WHITE);
            g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }

    /**
     * UI tùy chỉnh cho thẻ thống kê (StatCard):
     * Vẽ nền pastel bo góc, viền bo nhẹ và icon vector sắc nét trước,
     * sau đó vẽ chữ JLabel HTML lên trên để không bao giờ bị che khuất.
     */
    public static class StatCardUI extends BasicLabelUI {
        private final StatCardType type;
        private final int radius = 12;

        public StatCardUI(StatCardType type) {
            this.type = type;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = c.getWidth();
            int h = c.getHeight();

            // 1. Nền pastel bo góc
            g2.setColor(type.getBgColor());
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

            // 2. Viền phân cách nhẹ
            g2.setColor(type.getBorderColor());
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);

            // 3. Vẽ icon vector sắc nét bên trái
            int iconCenterX = 20;
            int iconCenterY = h / 2;
            g2.setColor(type.getValueColor());

            String icon = type.getIconType();
            if ("GRAD_CAP".equals(icon)) {
                Path2D cap = new Path2D.Double();
                cap.moveTo(iconCenterX, iconCenterY - 9);
                cap.lineTo(iconCenterX + 13, iconCenterY - 4);
                cap.lineTo(iconCenterX, iconCenterY + 1);
                cap.lineTo(iconCenterX - 13, iconCenterY - 4);
                cap.closePath();
                g2.fill(cap);

                Path2D skull = new Path2D.Double();
                skull.moveTo(iconCenterX - 8, iconCenterY - 1);
                skull.quadTo(iconCenterX, iconCenterY + 8, iconCenterX + 8, iconCenterY - 1);
                g2.fill(skull);

                g2.setStroke(new BasicStroke(1.4f));
                g2.drawLine(iconCenterX + 13, iconCenterY - 4, iconCenterX + 13, iconCenterY + 6);
                g2.fillOval(iconCenterX + 11, iconCenterY + 5, 4, 4);

            } else if ("TEACHER".equals(icon)) {
                g2.fillOval(iconCenterX - 10, iconCenterY - 8, 7, 7);
                g2.fillArc(iconCenterX - 14, iconCenterY, 15, 14, 0, 180);
                g2.fillRoundRect(iconCenterX, iconCenterY - 9, 15, 11, 2, 2);
                g2.setColor(Color.WHITE);
                g2.drawLine(iconCenterX + 3, iconCenterY - 6, iconCenterX + 12, iconCenterY - 6);
                g2.drawLine(iconCenterX + 3, iconCenterY - 3, iconCenterX + 9, iconCenterY - 3);
                g2.setColor(type.getValueColor());
                g2.drawLine(iconCenterX + 7, iconCenterY + 2, iconCenterX + 7, iconCenterY + 8);

            } else if ("GROUP".equals(icon)) {
                g2.fillOval(iconCenterX - 8, iconCenterY - 8, 7, 7);
                g2.fillArc(iconCenterX - 12, iconCenterY, 15, 13, 0, 180);
                g2.fillOval(iconCenterX + 3, iconCenterY - 6, 6, 6);
                g2.fillArc(iconCenterX - 1, iconCenterY + 1, 13, 11, 0, 180);

            } else if ("BOOK".equals(icon)) {
                g2.fillRoundRect(iconCenterX - 9, iconCenterY - 9, 18, 18, 3, 3);
                g2.setColor(type.getBorderColor());
                g2.fillRect(iconCenterX - 9, iconCenterY - 9, 4, 18);
                g2.setColor(Color.WHITE);
                g2.drawLine(iconCenterX - 2, iconCenterY - 5, iconCenterX + 6, iconCenterY - 5);
                g2.drawLine(iconCenterX - 2, iconCenterY - 1, iconCenterX + 6, iconCenterY - 1);
                g2.drawLine(iconCenterX - 2, iconCenterY + 3, iconCenterX + 3, iconCenterY + 3);

            } else if ("DOCUMENT".equals(icon)) {
                g2.fillRoundRect(iconCenterX - 8, iconCenterY - 9, 16, 18, 3, 3);
                g2.setColor(type.getBorderColor());
                g2.fillRoundRect(iconCenterX - 4, iconCenterY - 11, 8, 3, 1, 1);
                g2.setColor(Color.WHITE);
                g2.drawLine(iconCenterX - 4, iconCenterY - 4, iconCenterX + 4, iconCenterY - 4);
                g2.drawLine(iconCenterX - 4, iconCenterY, iconCenterX + 4, iconCenterY);
                g2.drawLine(iconCenterX - 4, iconCenterY + 4, iconCenterX + 1, iconCenterY + 4);
            }

            // 4. Lấy dữ liệu count và cardType
            Object countObj = c.getClientProperty("statCount");
            int count = (countObj instanceof Number) ? ((Number) countObj).intValue() : 0;
            StatCardType cardType = (c.getClientProperty("statType") instanceof StatCardType)
                    ? (StatCardType) c.getClientProperty("statType")
                    : this.type;

            String title = cardType.getTitle();
            String valStr = String.valueOf(count);

            // 5. Căn chỉnh khối văn bản (tiêu đề và con số) ở vùng bên phải icon
            int textStartX = 38;
            int textWidth = Math.max(10, w - textStartX - 4);

            // Font tiêu đề: Segoe UI BOLD, tự động co giãn nếu chuỗi dài để luôn nằm trên 1 dòng
            Font titleFont = new Font("Segoe UI", Font.BOLD, 11);
            FontMetrics fmTitle = g2.getFontMetrics(titleFont);
            while (fmTitle.stringWidth(title) > textWidth && titleFont.getSize() > 8) {
                titleFont = titleFont.deriveFont((float) (titleFont.getSize() - 1));
                fmTitle = g2.getFontMetrics(titleFont);
            }

            // Font con số: Segoe UI BOLD 22pt sắc nét
            Font valFont = new Font("Segoe UI", Font.BOLD, 22);
            FontMetrics fmVal = g2.getFontMetrics(valFont);

            // Căn giữa toàn bộ khối văn bản (tiêu đề + số) theo chiều dọc của thẻ
            int gapY = 2;
            int blockHeight = fmTitle.getAscent() + gapY + fmVal.getAscent();
            int startY = (h - blockHeight) / 2;

            int titleY = startY + fmTitle.getAscent();
            int titleX = textStartX + (textWidth - fmTitle.stringWidth(title)) / 2;

            int valY = titleY + gapY + fmVal.getAscent();
            int valX = textStartX + (textWidth - fmVal.stringWidth(valStr)) / 2;

            // 6. Vẽ tiêu đề (màu #475569)
            g2.setColor(new Color(71, 85, 105));
            g2.setFont(titleFont);
            g2.drawString(title, titleX, titleY);

            // 7. Vẽ số lượng (màu đặc trưng của thẻ)
            g2.setColor(cardType.getValueColor());
            g2.setFont(valFont);
            g2.drawString(valStr, valX, valY);

            g2.dispose();
            // Không gọi super.paint(g, c) để tránh cơ chế HTML của BasicHTML bị lệch lề hoặc rớt dòng
        }
    }

    /**
     * Viền tương thích ngược cho StatCard
     */
    public static class StatCardBorder implements Border {
        private final StatCardType type;

        public StatCardBorder(StatCardType type) {
            this.type = type;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            // Đã được vẽ đầy đủ trong StatCardUI để đảm bảo chữ không bao giờ bị đè lấp
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 46, 4, 8);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /**
     * Icon vector cho 4 nút Thao tác nhanh (Quick Action Buttons)
     */
    public static class QuickActionIcon implements Icon {
        public enum ActionType { NHAP_DIEM, TRA_CUU, GPA, BAO_CAO }

        private final ActionType type;
        private final Color color;
        private final int size;

        public QuickActionIcon(ActionType type, Color color) {
            this(type, color, 18);
        }

        public QuickActionIcon(ActionType type, Color color, int size) {
            this.type = type != null ? type : ActionType.NHAP_DIEM;
            this.color = color != null ? color : new Color(37, 99, 235);
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            switch (type) {
                case NHAP_DIEM: // Bút chì (Nhập điểm)
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 12, y + 3, x + 15, y + 6);
                    g2.drawLine(x + 5, y + 10, x + 12, y + 3);
                    g2.drawLine(x + 8, y + 13, x + 15, y + 6);
                    g2.drawLine(x + 3, y + 15, x + 5, y + 10);
                    g2.drawLine(x + 3, y + 15, x + 8, y + 13);
                    break;

                case TRA_CUU: // Kính lúp (Tra cứu điểm)
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(x + 2, y + 2, 9, 9);
                    g2.drawLine(x + 10, y + 10, x + 15, y + 15);
                    break;

                case GPA: // Biểu đồ cột (Kết quả GPA)
                    g2.fillRect(x + 2, y + 10, 3, 6);
                    g2.fillRect(x + 7, y + 6, 3, 10);
                    g2.fillRect(x + 12, y + 2, 3, 14);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawLine(x + 1, y + 16, x + 16, y + 16);
                    break;

                case BAO_CAO: // Báo cáo / Tài liệu (Thống kê & Báo cáo)
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(x + 2, y + 1, 13, 15, 2, 2);
                    g2.drawLine(x + 5, y + 5, x + 11, y + 5);
                    g2.drawLine(x + 5, y + 8, x + 11, y + 8);
                    g2.drawLine(x + 5, y + 11, x + 9, y + 11);
                    break;
            }
            g2.dispose();
        }
    }

    /**
     * Viền tròn huy hiệu Avatar người dùng
     */
    public static class AvatarCircleBorder implements Border {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(width, height) - 4;
            int cx = x + (width - size) / 2;
            int cy = y + (height - size) / 2;

            // Hình tròn trắng
            g2.setColor(Color.WHITE);
            g2.fillOval(cx, cy, size, size);

            // Icon bóng người UTT Navy
            g2.setColor(NAVY_UTT);
            int headSize = size * 30 / 100;
            int headX = cx + (size - headSize) / 2;
            int headY = cy + size * 20 / 100;
            g2.fillOval(headX, headY, headSize, headSize);

            int shoulderW = size * 62 / 100;
            int shoulderH = size * 50 / 100;
            int shoulderX = cx + (size - shoulderW) / 2;
            int shoulderY = cy + size * 52 / 100;
            g2.fillArc(shoulderX, shoulderY, shoulderW, shoulderH, 0, 180);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /**
     * Icon vector cho các mục trong JTree chức năng
     */
    public static class TreeVectorIcon implements Icon {
        public enum Type { FOLDER, USER, CARD, DIAMOND, GEAR }
        private final Type type;

        public TreeVectorIcon(Type type) {
            this.type = type != null ? type : Type.DIAMOND;
        }

        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = x + 9;
            int cy = y + 9;

            switch (type) {
                case FOLDER:
                    g2.setColor(new Color(245, 158, 11)); // Amber
                    g2.fillRoundRect(x + 1, y + 4, 6, 4, 2, 2);
                    g2.fillRoundRect(x + 1, y + 6, 15, 10, 2, 2);
                    g2.setColor(new Color(217, 119, 6));
                    g2.drawRoundRect(x + 1, y + 6, 15, 10, 2, 2);
                    break;
                case USER:
                    g2.setColor(new Color(71, 85, 105));
                    g2.fillOval(cx - 3, cy - 6, 6, 6);
                    g2.fillArc(cx - 6, cy + 1, 12, 10, 0, 180);
                    break;
                case CARD:
                    g2.setColor(new Color(100, 116, 139));
                    g2.fillRoundRect(x + 2, y + 3, 14, 12, 2, 2);
                    g2.setColor(Color.WHITE);
                    g2.fillRect(x + 4, y + 7, 10, 6);
                    break;
                case GEAR:
                    g2.setColor(new Color(71, 85, 105));
                    g2.fillOval(cx - 5, cy - 5, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(cx - 2, cy - 2, 4, 4);
                    break;
                case DIAMOND:
                default:
                    g2.setColor(new Color(100, 116, 139));
                    Path2D diamond = new Path2D.Double();
                    diamond.moveTo(cx, cy - 4);
                    diamond.lineTo(cx + 4, cy);
                    diamond.lineTo(cx, cy + 4);
                    diamond.lineTo(cx - 4, cy);
                    diamond.closePath();
                    g2.fill(diamond);
                    break;
            }
            g2.dispose();
        }
    }

    /**
     * Renderer hiện đại cho JTree chức năng
     */
    public static class ModernTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            String text = (value != null) ? value.toString() : "";
            setBackgroundSelectionColor(new Color(239, 246, 255));
            setTextSelectionColor(new Color(37, 99, 235));
            setTextNonSelectionColor(new Color(31, 41, 55));
            setBorderSelectionColor(new Color(191, 219, 254));

            if (!leaf) {
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setIcon(new TreeVectorIcon(TreeVectorIcon.Type.FOLDER));
            } else {
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                if (text.contains("Sinh viên") || text.contains("Giảng viên")) {
                    setIcon(new TreeVectorIcon(TreeVectorIcon.Type.USER));
                } else if (text.contains("Lớp học") || text.contains("Môn học")) {
                    setIcon(new TreeVectorIcon(TreeVectorIcon.Type.CARD));
                } else if (text.contains("Tài khoản")) {
                    setIcon(new TreeVectorIcon(TreeVectorIcon.Type.GEAR));
                } else {
                    setIcon(new TreeVectorIcon(TreeVectorIcon.Type.DIAMOND));
                }
            }
            return this;
        }
    }
}
