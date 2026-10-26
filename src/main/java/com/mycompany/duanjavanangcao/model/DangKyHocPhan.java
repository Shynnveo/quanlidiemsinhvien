package com.mycompany.duanjavanangcao.model;

import java.sql.Timestamp;

/**
 * Model đại diện cho bảng DangKyHocPhan trong CSDL duanjavanangcao
 */
public class DangKyHocPhan {

    private Integer dangKyId;
    private int sinhVienId;
    private int lopHocPhanId;
    private Timestamp ngayDangKy;
    private String trangThai; // "Đang học", "Đã hủy", ...

    // Các trường phụ phục vụ hiển thị trên giao diện (JOIN từ bảng khác)
    private String maSinhVien;
    private String hoTenSinhVien;
    private String maLopHocPhan;
    private String tenMonHoc;
    private int soTinChi;
    private String tenGiangVien;
    private String lichHoc;
    private String phongHoc;
    private String tenHocKy;

    public DangKyHocPhan() {
    }

    public DangKyHocPhan(Integer dangKyId, int sinhVienId, int lopHocPhanId, Timestamp ngayDangKy, String trangThai) {
        this.dangKyId = dangKyId;
        this.sinhVienId = sinhVienId;
        this.lopHocPhanId = lopHocPhanId;
        this.ngayDangKy = ngayDangKy;
        this.trangThai = trangThai;
    }

    public Integer getDangKyId() {
        return dangKyId;
    }

    public void setDangKyId(Integer dangKyId) {
        this.dangKyId = dangKyId;
    }

    public int getSinhVienId() {
        return sinhVienId;
    }

    public void setSinhVienId(int sinhVienId) {
        this.sinhVienId = sinhVienId;
    }

    public int getLopHocPhanId() {
        return lopHocPhanId;
    }

    public void setLopHocPhanId(int lopHocPhanId) {
        this.lopHocPhanId = lopHocPhanId;
    }

    public Timestamp getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(Timestamp ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public String getHoTenSinhVien() {
        return hoTenSinhVien;
    }

    public void setHoTenSinhVien(String hoTenSinhVien) {
        this.hoTenSinhVien = hoTenSinhVien;
    }

    public String getMaLopHocPhan() {
        return maLopHocPhan;
    }

    public void setMaLopHocPhan(String maLopHocPhan) {
        this.maLopHocPhan = maLopHocPhan;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public String getTenGiangVien() {
        return tenGiangVien;
    }

    public void setTenGiangVien(String tenGiangVien) {
        this.tenGiangVien = tenGiangVien;
    }

    public String getLichHoc() {
        return lichHoc;
    }

    public void setLichHoc(String lichHoc) {
        this.lichHoc = lichHoc;
    }

    public String getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        this.phongHoc = phongHoc;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
    }

    @Override
    public String toString() {
        return maLopHocPhan + " - " + tenMonHoc + " (" + trangThai + ")";
    }
}

