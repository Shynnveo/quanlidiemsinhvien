package com.mycompany.duanjavanangcao.model;

import java.sql.Date;

/**
 * Model ánh xạ bảng HocKy trong CSDL duanjavanangcao
 */
public class HocKy {

    private int hocKyId;
    private String maHocKy;
    private String tenHocKy;
    private String namHoc;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String trangThai; // "Đang học", "Chưa bắt đầu", "Đã kết thúc"

    public HocKy() {
    }

    public HocKy(int hocKyId, String maHocKy, String tenHocKy, String namHoc, Date ngayBatDau, Date ngayKetThuc, String trangThai) {
        this.hocKyId = hocKyId;
        this.maHocKy = maHocKy;
        this.tenHocKy = tenHocKy;
        this.namHoc = namHoc;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public int getHocKyId() {
        return hocKyId;
    }

    public void setHocKyId(int hocKyId) {
        this.hocKyId = hocKyId;
    }

    public String getMaHocKy() {
        return maHocKy;
    }

    public void setMaHocKy(String maHocKy) {
        this.maHocKy = maHocKy;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tenHocKy + " (" + namHoc + ")";
    }
}

