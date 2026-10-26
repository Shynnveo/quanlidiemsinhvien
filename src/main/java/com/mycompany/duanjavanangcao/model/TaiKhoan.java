package com.mycompany.duanjavanangcao.model;

import java.sql.Timestamp;

/**
 * Model ánh xạ bảng TaiKhoan trong CSDL duanjavanangcao
 */
public class TaiKhoan {

    private int taiKhoanId;
    private String tenDangNhap;
    private String matKhauHash;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String vaiTro; // ADMIN, GIANGVIEN, SINHVIEN
    private boolean trangThai; // true: Hoạt động, false: Bị khóa
    private Timestamp ngayTao;

    public TaiKhoan() {
    }

    public TaiKhoan(int taiKhoanId, String tenDangNhap, String matKhauHash, String hoTen,
                    String email, String soDienThoai, String vaiTro, boolean trangThai, Timestamp ngayTao) {
        this.taiKhoanId = taiKhoanId;
        this.tenDangNhap = tenDangNhap;
        this.matKhauHash = matKhauHash;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public int getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(int taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhauHash() {
        return matKhauHash;
    }

    public void setMatKhauHash(String matKhauHash) {
        this.matKhauHash = matKhauHash;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public String toString() {
        return hoTen + " (" + tenDangNhap + " - " + vaiTro + ")";
    }
}

