package com.mycompany.duanjavanangcao.model;

/**
 * Model ánh xạ bảng Lop trong CSDL duanjavanangcao
 */
public class Lop {

    private int lopId;
    private String maLop;
    private String tenLop;
    private String khoaHoc;
    private String nganhHoc;
    private String khoa;
    private boolean trangThai;

    public Lop() {
    }

    public Lop(int lopId, String maLop, String tenLop, String khoaHoc, String nganhHoc, String khoa, boolean trangThai) {
        this.lopId = lopId;
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.khoaHoc = khoaHoc;
        this.nganhHoc = nganhHoc;
        this.khoa = khoa;
        this.trangThai = trangThai;
    }

    public int getLopId() {
        return lopId;
    }

    public void setLopId(int lopId) {
        this.lopId = lopId;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(String khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public String getNganhHoc() {
        return nganhHoc;
    }

    public void setNganhHoc(String nganhHoc) {
        this.nganhHoc = nganhHoc;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maLop + " - " + tenLop;
    }
}

