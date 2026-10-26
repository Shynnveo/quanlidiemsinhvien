package com.mycompany.duanjavanangcao.model;

/**
 * Model ánh xạ bảng GiangVien trong CSDL duanjavanangcao
 */
public class GiangVien {

    private int giangVienId;
    private String maGiangVien;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String khoa;
    private Integer taiKhoanId;

    public GiangVien() {
    }

    public GiangVien(int giangVienId, String maGiangVien, String hoTen, String email, String soDienThoai, String khoa, Integer taiKhoanId) {
        this.giangVienId = giangVienId;
        this.maGiangVien = maGiangVien;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.khoa = khoa;
        this.taiKhoanId = taiKhoanId;
    }

    public int getGiangVienId() {
        return giangVienId;
    }

    public void setGiangVienId(int giangVienId) {
        this.giangVienId = giangVienId;
    }

    public String getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(String maGiangVien) {
        this.maGiangVien = maGiangVien;
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

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public Integer getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(Integer taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }

    @Override
    public String toString() {
        return maGiangVien + " - " + hoTen;
    }
}

