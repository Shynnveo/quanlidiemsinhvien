package com.mycompany.duanjavanangcao.util;

import com.mycompany.duanjavanangcao.model.TaiKhoan;

/**
 * Quản lý phiên đăng nhập hiện tại của người dùng
 */
public class SessionManager {

    private static TaiKhoan currentUser = null;

    public static TaiKhoan getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(TaiKhoan user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}

