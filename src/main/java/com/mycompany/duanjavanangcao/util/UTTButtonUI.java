package com.mycompany.duanjavanangcao.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * Custom ButtonUI cho toàn bộ hệ thống phần mềm quản lý UTT.
 * Đảm bảo mọi JButton hiển thị chính xác màu nền và màu chữ theo thiết kế,
 * khắc phục hoàn toàn lỗi Windows L&F / Nimbus bỏ qua background của button
 * dẫn đến tình trạng chữ trắng trên nền trắng.
 */
public class UTTButtonUI extends BasicButtonUI {

    private static final UTTButtonUI INSTANCE = new UTTButtonUI();

    public static ComponentUI createUI(JComponent c) {
        return INSTANCE;
    }

    public static UTTButtonUI getSharedInstance() {
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
            bg = UIThemeUtil.BTN_NEUTRAL_BG;
        }

        if (!b.isEnabled()) {
            bg = new Color(226, 232, 240); // #E2E8F0 mờ khi disable
        } else if (model.isPressed()) {
            bg = darken(bg, 0.15f);
        } else if (model.isRollover()) {
            bg = brighten(bg, 0.12f);
        }

        // Vẽ nền phẳng bo góc hiện đại 6px
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
                fg = UIThemeUtil.TEXT_MAIN;
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
        // Công thức tính độ sáng YIQ
        double yiq = ((c.getRed() * 299) + (c.getGreen() * 587) + (c.getBlue() * 114)) / 1000.0;
        return yiq >= 180;
    }
}
