package com.mycompany.duanjavanangcao.model;

import java.util.Date;

/**
 * Model ánh xạ bảng CanhCaoHocTap trong CSDL duanjavanangcao
 */
public class CanhCaoHocTap {

    private int canhCaoId;
    private int sinhVienId;
    private int hocKyId;
    private int tieuChiId;
    private Date ngayXet;
    private String lyDo;
    private String mucCanhCao; // "Cảnh báo mức 1", "Cảnh báo mức 2", "Cảnh báo mức 3 (Buộc thôi học)"
    private String trangThai;  // "Đang áp dụng", "Đã khắc phục", "Đã hủy"
    private Integer nguoiXetId;
    private String ghiChu;

    // Các trường dữ liệu liên kết bổ trợ hiển thị trên bảng & giao diện
    private String maSinhVien;
    private String hoTenSinhVien;
    private String tenLop;
    private Integer taiKhoanSinhVienId;
    private String maHocKy;
    private String tenHocKy;
    private String maTieuChi;
    private String tenTieuChi;
    private String tenNguoiXet;
    private Double gpaHocKy;
    private Double gpaTichLuy;

    public CanhCaoHocTap() {
        this.mucCanhCao = "Cảnh báo mức 1";
        this.trangThai = "Đang áp dụng";
        this.ngayXet = new Date();
    }

    public CanhCaoHocTap(int sinhVienId, int hocKyId, int tieuChiId, String lyDo,
                         String mucCanhCao, String trangThai, Integer nguoiXetId, String ghiChu) {
        this.sinhVienId = sinhVienId;
        this.hocKyId = hocKyId;
        this.tieuChiId = tieuChiId;
        this.lyDo = lyDo;
        this.mucCanhCao = mucCanhCao;
        this.trangThai = trangThai;
        this.nguoiXetId = nguoiXetId;
        this.ghiChu = ghiChu;
        this.ngayXet = new Date();
    }

    public int getCanhCaoId() {
        return canhCaoId;
    }

    public void setCanhCaoId(int canhCaoId) {
        this.canhCaoId = canhCaoId;
    }

    public int getSinhVienId() {
        return sinhVienId;
    }

    public void setSinhVienId(int sinhVienId) {
        this.sinhVienId = sinhVienId;
    }

    public int getHocKyId() {
        return hocKyId;
    }

    public void setHocKyId(int hocKyId) {
        this.hocKyId = hocKyId;
    }

    public int getTieuChiId() {
        return tieuChiId;
    }

    public void setTieuChiId(int tieuChiId) {
        this.tieuChiId = tieuChiId;
    }

    public Date getNgayXet() {
        return ngayXet;
    }

    public void setNgayXet(Date ngayXet) {
        this.ngayXet = ngayXet;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getMucCanhCao() {
        return mucCanhCao;
    }

    public void setMucCanhCao(String mucCanhCao) {
        this.mucCanhCao = mucCanhCao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getNguoiXetId() {
        return nguoiXetId;
    }

    public void setNguoiXetId(Integer nguoiXetId) {
        this.nguoiXetId = nguoiXetId;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
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

    public Integer getTaiKhoanSinhVienId() {
        return taiKhoanSinhVienId;
    }

    public void setTaiKhoanSinhVienId(Integer taiKhoanSinhVienId) {
        this.taiKhoanSinhVienId = taiKhoanSinhVienId;
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

    public String getMaTieuChi() {
        return maTieuChi;
    }

    public void setMaTieuChi(String maTieuChi) {
        this.maTieuChi = maTieuChi;
    }

    public String getTenTieuChi() {
        return tenTieuChi;
    }

    public void setTenTieuChi(String tenTieuChi) {
        this.tenTieuChi = tenTieuChi;
    }

    public String getTenNguoiXet() {
        return tenNguoiXet;
    }

    public void setTenNguoiXet(String tenNguoiXet) {
        this.tenNguoiXet = tenNguoiXet;
    }

    public Double getGpaHocKy() {
        return gpaHocKy;
    }

    public void setGpaHocKy(Double gpaHocKy) {
        this.gpaHocKy = gpaHocKy;
    }

    public Double getGpaTichLuy() {
        return gpaTichLuy;
    }

    public void setGpaTichLuy(Double gpaTichLuy) {
        this.gpaTichLuy = gpaTichLuy;
    }
}

