package com.mycompany.duanjavanangcao.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Tiện ích chuẩn hóa phong cách giao diện UTT (UTT Design System)
 * Áp dụng bảng màu, phông chữ và tối ưu hiển thị JTable
 */
public class UIThemeUtil {

    // Bảng màu chuẩn UTT Management System
    public static final Color NAVY_UTT = new Color(23, 54, 93);       // #17365D - Xanh navy UTT
    public static final Color ORANGE_UTT = new Color(242, 140, 40);   // #F28C28 - Màu cam điểm nhấn UTT
    public static final Color PRIMARY_BLUE = new Color(37, 99, 235);  // #2563EB - Hành động chính
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
            // Đăng ký UTTButtonUI toàn cục để vẽ chuẩn màu nền và màu chữ
            javax.swing.UIManager.getDefaults().put("ClassLoader", UIThemeUtil.class.getClassLoader());
            javax.swing.UIManager.put("ButtonUI", UTTButtonUI.class.getName());
            javax.swing.UIManager.getDefaults().put("ButtonUI", UTTButtonUI.class.getName());
        } catch (Exception ignored) {
        }
    }

    /**
     * Áp dụng UTTButtonUI thủ công cho một hoặc nhiều JButton
     */
    public static void formatButton(JButton... buttons) {
        if (buttons == null) return;
        for (JButton btn : buttons) {
            if (btn != null) {
                btn.setUI(UTTButtonUI.getSharedInstance());
            }
        }
    }

    /**
     * Duyệt đệ quy toàn bộ container/JFrame để đảm bảo 100% mọi JButton bên trong
     * đều sử dụng UTTButtonUI, bảo toàn màu nền và màu chữ UTT sắc nét
     */
    public static void applyThemeToContainer(java.awt.Container container) {
        if (container == null) return;
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (!(btn.getUI() instanceof UTTButtonUI)) {
                    btn.setUI(UTTButtonUI.getSharedInstance());
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
        btn.setUI(UTTButtonUI.getSharedInstance());
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút thành công / thêm mới (Xanh lá)
     */
    public static void styleSuccessButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(UTTButtonUI.getSharedInstance());
        btn.setBackground(SUCCESS_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút cảnh báo / xóa / đăng xuất (Đỏ)
     */
    public static void styleDangerButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(UTTButtonUI.getSharedInstance());
        btn.setBackground(DANGER_RED);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút nhấn mạnh / cam (Cam UTT)
     */
    public static void styleWarningButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(UTTButtonUI.getSharedInstance());
        btn.setBackground(ORANGE_UTT);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
    }

    /**
     * Định kiểu nút phụ / neutral (Xám nhạt chữ đậm)
     */
    public static void styleNeutralButton(JButton btn) {
        if (btn == null) return;
        btn.setUI(UTTButtonUI.getSharedInstance());
        btn.setBackground(BTN_NEUTRAL_BG);
        btn.setForeground(TEXT_MAIN);
        btn.setFont(FONT_BUTTON);
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
}
