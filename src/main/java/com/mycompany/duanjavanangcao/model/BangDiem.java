package com.mycompany.duanjavanangcao.model;

import java.sql.Timestamp;

/**
 * Model ánh xạ bảng BangDiem trong CSDL duanjavanangcao
 */
public class BangDiem {

    private Integer bangDiemId;
    private int dangKyId;
    private Double diemCC;
    private Double diemGK;
    private Double diemCK;
    private Double diemTongKet;
    private String diemChu;
    private Double diemHe4;
    private Timestamp ngayCapNhat;
    private Integer nguoiCapNhatId;
    private String ghiChu;

    // Các trường liên kết hiển thị trên giao diện
    private int sinhVienId;
    private String maSinhVien;
    private String hoTenSinhVien;
    private String tenLop;
    private int lopHocPhanId;
    private String maLopHocPhan;
    private String tenMonHoc;
    private int soTinChi;
    private String tenGiangVien;
    private String tenHocKy;
    private String ketQua;

    public BangDiem() {
    }

    public Integer getBangDiemId() {
        return bangDiemId;
    }

    public void setBangDiemId(Integer bangDiemId) {
        this.bangDiemId = bangDiemId;
    }

    public int getDangKyId() {
        return dangKyId;
    }

    public void setDangKyId(int dangKyId) {
        this.dangKyId = dangKyId;
    }

    public Double getDiemCC() {
        return diemCC;
    }

    public void setDiemCC(Double diemCC) {
        this.diemCC = diemCC;
    }

    public Double getDiemGK() {
        return diemGK;
    }

    public void setDiemGK(Double diemGK) {
        this.diemGK = diemGK;
    }

    public Double getDiemCK() {
        return diemCK;
    }

    public void setDiemCK(Double diemCK) {
        this.diemCK = diemCK;
    }

    public Double getDiemTongKet() {
        return diemTongKet;
    }

    public void setDiemTongKet(Double diemTongKet) {
        this.diemTongKet = diemTongKet;
    }

    public String getDiemChu() {
        return diemChu;
    }

    public void setDiemChu(String diemChu) {
        this.diemChu = diemChu;
    }

    public Double getDiemHe4() {
        return diemHe4;
    }

    public void setDiemHe4(Double diemHe4) {
        this.diemHe4 = diemHe4;
    }

    public Timestamp getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Timestamp ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public Integer getNguoiCapNhatId() {
        return nguoiCapNhatId;
    }

    public void setNguoiCapNhatId(Integer nguoiCapNhatId) {
        this.nguoiCapNhatId = nguoiCapNhatId;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
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

    public String getHoTenSinhVien() {
        return hoTenSinhVien;
    }

    public void setHoTenSinhVien(String hoTenSinhVien) {
        this.hoTenSinhVien = hoTenSinhVien;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public int getLopHocPhanId() {
        return lopHocPhanId;
    }

    public void setLopHocPhanId(int lopHocPhanId) {
        this.lopHocPhanId = lopHocPhanId;
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

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
    }

    public String getKetQua() {
        return ketQua;
    }

    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }

    @Override
    public String toString() {
        if (tenMonHoc != null && !tenMonHoc.trim().isEmpty()) {
            String strCK = (diemCK != null) ? String.valueOf(diemCK) : "--";
            String strTK = (diemTongKet != null) ? String.valueOf(diemTongKet) : "--";
            return maLopHocPhan + " - " + tenMonHoc + " [CK: " + strCK + ", TK: " + strTK + "]";
        }
        return maSinhVien + " - " + hoTenSinhVien + ": TK = " + diemTongKet;
    }
}

