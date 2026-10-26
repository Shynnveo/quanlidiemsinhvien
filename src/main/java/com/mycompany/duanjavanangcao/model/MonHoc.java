package com.mycompany.duanjavanangcao.model;

/**
 * Model ánh xạ bảng MonHoc trong CSDL duanjavanangcao
 */
public class MonHoc {

    private int monHocId;
    private String maMonHoc;
    private String tenMonHoc;
    private int soTinChi;
    private String moTa;
    private boolean trangThai;

    public MonHoc() {
    }

    public MonHoc(int monHocId, String maMonHoc, String tenMonHoc, int soTinChi, String moTa, boolean trangThai) {
        this.monHocId = monHocId;
        this.maMonHoc = maMonHoc;
        this.tenMonHoc = tenMonHoc;
        this.soTinChi = soTinChi;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public int getMonHocId() {
        return monHocId;
    }

    public void setMonHocId(int monHocId) {
        this.monHocId = monHocId;
    }

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maMonHoc + " - " + tenMonHoc + " (" + soTinChi + " TC)";
    }
}

