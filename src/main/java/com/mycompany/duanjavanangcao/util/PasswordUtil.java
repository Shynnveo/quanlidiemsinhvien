package com.mycompany.duanjavanangcao.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Tiện ích băm và kiểm tra mật khẩu bằng thuật toán SHA-256
 */
public class PasswordUtil {

    /**
     * Băm mật khẩu dạng chuỗi bằng SHA-256 thành chuỗi Hex
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi băm mật khẩu: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra mật khẩu người dùng nhập vào với mã băm trong CSDL
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }

        // Hỗ trợ đăng nhập với dữ liệu mẫu ban đầu trong database
        if ("DEMO_ONLY_NOT_A_REAL_HASH".equals(storedHash)) {
            // Mật khẩu mặc định cho các tài khoản mẫu là 123456
            if ("123456".equals(plainPassword)) {
                return true;
            }
        }

        // So sánh với chuỗi SHA-256
        String hashedInput = hashPassword(plainPassword);
        if (hashedInput.equalsIgnoreCase(storedHash)) {
            return true;
        }

        // Dự phòng trường hợp mật khẩu được lưu trực tiếp (plain text) trong lúc test
        return plainPassword.equals(storedHash);
    }
}

