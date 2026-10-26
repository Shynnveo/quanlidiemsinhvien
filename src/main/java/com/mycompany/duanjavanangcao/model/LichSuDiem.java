package com.mycompany.duanjavanangcao.model;

import java.sql.Timestamp;

/**
 * Model ánh xạ bảng LichSuDiem trong CSDL duanjavanangcao
 */
public class LichSuDiem {

    private Integer lichSuDiemId;
    private int bangDiemId;
    private Double diemCCCu;
    private Double diemGKCu;
    private Double diemCKCu;
    private Double diemTongKetCu;
    private String diemChuCu;
    private Double diemHe4Cu;

    private Double diemCCMoi;
    private Double diemGKMoi;
    private Double diemCKMoi;
    private Double diemTongKetMoi;
    private String diemChuMoi;
    private Double diemHe4Moi;

    private String lyDo;
    private int nguoiThayDoiId;
    private Timestamp thoiGianThayDoi;

    // Các trường bổ trợ hiển thị
    private String tenNguoiThayDoi;
    private String maSinhVien;
    private String hoTenSinhVien;
    private String maLopHocPhan;
    private String tenMonHoc;

    public LichSuDiem() {
    }

    public Integer getLichSuDiemId() {
        return lichSuDiemId;
    }

    public void setLichSuDiemId(Integer lichSuDiemId) {
        this.lichSuDiemId = lichSuDiemId;
    }

    public int getBangDiemId() {
        return bangDiemId;
    }

    public void setBangDiemId(int bangDiemId) {
        this.bangDiemId = bangDiemId;
    }

    public Double getDiemCCCu() {
        return diemCCCu;
    }

    public void setDiemCCCu(Double diemCCCu) {
        this.diemCCCu = diemCCCu;
    }

    public Double getDiemGKCu() {
        return diemGKCu;
    }

    public void setDiemGKCu(Double diemGKCu) {
        this.diemGKCu = diemGKCu;
    }

    public Double getDiemCKCu() {
        return diemCKCu;
    }

    public void setDiemCKCu(Double diemCKCu) {
        this.diemCKCu = diemCKCu;
    }

    public Double getDiemTongKetCu() {
        return diemTongKetCu;
    }

    public void setDiemTongKetCu(Double diemTongKetCu) {
        this.diemTongKetCu = diemTongKetCu;
    }

    public String getDiemChuCu() {
        return diemChuCu;
    }

    public void setDiemChuCu(String diemChuCu) {
        this.diemChuCu = diemChuCu;
    }

    public Double getDiemHe4Cu() {
        return diemHe4Cu;
    }

    public void setDiemHe4Cu(Double diemHe4Cu) {
        this.diemHe4Cu = diemHe4Cu;
    }

    public Double getDiemCCMoi() {
        return diemCCMoi;
    }

    public void setDiemCCMoi(Double diemCCMoi) {
        this.diemCCMoi = diemCCMoi;
    }

    public Double getDiemGKMoi() {
        return diemGKMoi;
    }

    public void setDiemGKMoi(Double diemGKMoi) {
        this.diemGKMoi = diemGKMoi;
    }

    public Double getDiemCKMoi() {
        return diemCKMoi;
    }

    public void setDiemCKMoi(Double diemCKMoi) {
        this.diemCKMoi = diemCKMoi;
    }

    public Double getDiemTongKetMoi() {
        return diemTongKetMoi;
    }

    public void setDiemTongKetMoi(Double diemTongKetMoi) {
        this.diemTongKetMoi = diemTongKetMoi;
    }

    public String getDiemChuMoi() {
        return diemChuMoi;
    }

    public void setDiemChuMoi(String diemChuMoi) {
        this.diemChuMoi = diemChuMoi;
    }

    public Double getDiemHe4Moi() {
        return diemHe4Moi;
    }

    public void setDiemHe4Moi(Double diemHe4Moi) {
        this.diemHe4Moi = diemHe4Moi;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public int getNguoiThayDoiId() {
        return nguoiThayDoiId;
    }

    public void setNguoiThayDoiId(int nguoiThayDoiId) {
        this.nguoiThayDoiId = nguoiThayDoiId;
    }

    public Timestamp getThoiGianThayDoi() {
        return thoiGianThayDoi;
    }

    public void setThoiGianThayDoi(Timestamp thoiGianThayDoi) {
        this.thoiGianThayDoi = thoiGianThayDoi;
    }

    public String getTenNguoiThayDoi() {
        return tenNguoiThayDoi;
    }

    public void setTenNguoiThayDoi(String tenNguoiThayDoi) {
        this.tenNguoiThayDoi = tenNguoiThayDoi;
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
}

