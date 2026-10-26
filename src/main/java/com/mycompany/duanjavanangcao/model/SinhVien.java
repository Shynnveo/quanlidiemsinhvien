package com.mycompany.duanjavanangcao.model;

import java.sql.Date;

/**
 * Model ánh xạ bảng SinhVien trong CSDL duanjavanangcao
 */
public class SinhVien {

    private int sinhVienId;
    private String maSinhVien;
    private String hoTen;
    private Date ngaySinh;
    private String gioiTinh;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private Date ngayNhapHoc;
    private String trangThai; // "Đang học", "Đã tốt nghiệp", "Bảo lưu", "Thôi học"
    private Integer lopId;
    private Integer taiKhoanId;

    public SinhVien() {
    }

    public SinhVien(int sinhVienId, String maSinhVien, String hoTen, Date ngaySinh, String gioiTinh,
                    String email, String soDienThoai, String diaChi, Date ngayNhapHoc,
                    String trangThai, Integer lopId, Integer taiKhoanId) {
        this.sinhVienId = sinhVienId;
        this.maSinhVien = maSinhVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.ngayNhapHoc = ngayNhapHoc;
        this.trangThai = trangThai;
        this.lopId = lopId;
        this.taiKhoanId = taiKhoanId;
    }

    public int getSinhVienId() {
        return sinhVienId;
    }

    public void setSinhVienId(int sinhVienId) {
        this.sinhVienId = sinhVienId;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
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

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public Date getNgayNhapHoc() {
        return ngayNhapHoc;
    }

    public void setNgayNhapHoc(Date ngayNhapHoc) {
        this.ngayNhapHoc = ngayNhapHoc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getLopId() {
        return lopId;
    }

    public void setLopId(Integer lopId) {
        this.lopId = lopId;
    }

    public Integer getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(Integer taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }

    @Override
    public String toString() {
        return maSinhVien + " - " + hoTen;
    }
}

