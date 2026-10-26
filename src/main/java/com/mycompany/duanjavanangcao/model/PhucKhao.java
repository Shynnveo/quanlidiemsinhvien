package com.mycompany.duanjavanangcao.model;

import java.util.Date;

/**
 * Model ánh xạ bảng PhucKhao trong CSDL duanjavanangcao
 */
public class PhucKhao {

    private int phucKhaoId;
    private int bangDiemId;
    private int sinhVienId;
    private Date ngayGui;
    private String lyDo;
    private String trangThai; // "Chờ xử lý", "Đang xử lý", "Đã chấp nhận", "Không chấp nhận"
    private String ketQuaXuLy;
    private Integer nguoiXuLyId;
    private Date ngayXuLy;

    // Các trường dữ liệu bổ trợ để hiển thị lên bảng và giao diện
    private String maSinhVien;
    private String hoTenSinhVien;
    private String tenLop;
    private Integer sinhVienTaiKhoanId;
    private String maLopHocPhan;
    private String tenMonHoc;
    private int soTinChi;
    private int hocKyId;
    private String tenHocKy;
    private Double diemCC;
    private Double diemGK;
    private Double diemCK;
    private Double diemTongKet;
    private String diemChu;
    private String tenNguoiXuLy;

    public PhucKhao() {
        this.trangThai = "Chờ xử lý";
    }

    public PhucKhao(int bangDiemId, int sinhVienId, String lyDo) {
        this.bangDiemId = bangDiemId;
        this.sinhVienId = sinhVienId;
        this.lyDo = lyDo;
        this.trangThai = "Chờ xử lý";
        this.ngayGui = new Date();
    }

    // Getters và Setters
    public int getPhucKhaoId() {
        return phucKhaoId;
    }

    public void setPhucKhaoId(int phucKhaoId) {
        this.phucKhaoId = phucKhaoId;
    }

    public int getBangDiemId() {
        return bangDiemId;
    }

    public void setBangDiemId(int bangDiemId) {
        this.bangDiemId = bangDiemId;
    }

    public int getSinhVienId() {
        return sinhVienId;
    }

    public void setSinhVienId(int sinhVienId) {
        this.sinhVienId = sinhVienId;
    }

    public Date getNgayGui() {
        return ngayGui;
    }

    public void setNgayGui(Date ngayGui) {
        this.ngayGui = ngayGui;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getKetQuaXuLy() {
        return ketQuaXuLy;
    }

    public void setKetQuaXuLy(String ketQuaXuLy) {
        this.ketQuaXuLy = ketQuaXuLy;
    }

    public Integer getNguoiXuLyId() {
        return nguoiXuLyId;
    }

    public void setNguoiXuLyId(Integer nguoiXuLyId) {
        this.nguoiXuLyId = nguoiXuLyId;
    }

    public Date getNgayXuLy() {
        return ngayXuLy;
    }

    public void setNgayXuLy(Date ngayXuLy) {
        this.ngayXuLy = ngayXuLy;
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

    public Integer getSinhVienTaiKhoanId() {
        return sinhVienTaiKhoanId;
    }

    public void setSinhVienTaiKhoanId(Integer sinhVienTaiKhoanId) {
        this.sinhVienTaiKhoanId = sinhVienTaiKhoanId;
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

    public int getHocKyId() {
        return hocKyId;
    }

    public void setHocKyId(int hocKyId) {
        this.hocKyId = hocKyId;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
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

    public String getTenNguoiXuLy() {
        return tenNguoiXuLy;
    }

    public void setTenNguoiXuLy(String tenNguoiXuLy) {
        this.tenNguoiXuLy = tenNguoiXuLy;
    }
}
