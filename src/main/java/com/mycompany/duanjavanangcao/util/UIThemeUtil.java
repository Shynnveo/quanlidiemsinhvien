package com.mycompany.duanjavanangcao.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

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

            // Vẽ nền phẳng bo góc hiện đại 8px
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, width, height, 8, 8);

            // Nếu là nút màu sáng (nút phụ neutral), vẽ thêm viền nhẹ để phân cách
            if (b.isEnabled() && isLightColor(bg)) {
                g2.setColor(new Color(203, 213, 225)); // #CBD5E1
                g2.drawRoundRect(0, 0, width - 1, height - 1, 8, 8);
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
}
