package com.mycompany.duanjavanangcao.model;

/**
 * Model ánh xạ bảng LopHocPhan trong CSDL duanjavanangcao
 */
public class LopHocPhan {

    private int lopHocPhanId;
    private String maLopHocPhan;
    private int monHocId;
    private int hocKyId;
    private Integer giangVienId;
    private String nhom;
    private Integer siSoToiDa;
    private String lichHoc;
    private String phongHoc;
    private String trangThai; // "Đang mở", "Đã khóa", "Đã hủy"

    // Các trường bổ trợ hiển thị thông tin kèm theo trên giao diện
    private String tenMonHoc;
    private String tenHocKy;
    private String tenGiangVien;
    private int soTinChi;
    private int siSoHienTai;

    public LopHocPhan() {
    }

    public LopHocPhan(int lopHocPhanId, String maLopHocPhan, int monHocId, int hocKyId,
                      Integer giangVienId, String nhom, Integer siSoToiDa, String lichHoc,
                      String phongHoc, String trangThai) {
        this.lopHocPhanId = lopHocPhanId;
        this.maLopHocPhan = maLopHocPhan;
        this.monHocId = monHocId;
        this.hocKyId = hocKyId;
        this.giangVienId = giangVienId;
        this.nhom = nhom;
        this.siSoToiDa = siSoToiDa;
        this.lichHoc = lichHoc;
        this.phongHoc = phongHoc;
        this.trangThai = trangThai;
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

    public int getMonHocId() {
        return monHocId;
    }

    public void setMonHocId(int monHocId) {
        this.monHocId = monHocId;
    }

    public int getHocKyId() {
        return hocKyId;
    }

    public void setHocKyId(int hocKyId) {
        this.hocKyId = hocKyId;
    }

    public Integer getGiangVienId() {
        return giangVienId;
    }

    public void setGiangVienId(Integer giangVienId) {
        this.giangVienId = giangVienId;
    }

    public String getNhom() {
        return nhom;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
    }

    public Integer getSiSoToiDa() {
        return siSoToiDa;
    }

    public void setSiSoToiDa(Integer siSoToiDa) {
        this.siSoToiDa = siSoToiDa;
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
    }

    public String getTenGiangVien() {
        return tenGiangVien;
    }

    public void setTenGiangVien(String tenGiangVien) {
        this.tenGiangVien = tenGiangVien;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public int getSiSoHienTai() {
        return siSoHienTai;
    }

    public void setSiSoHienTai(int siSoHienTai) {
        this.siSoHienTai = siSoHienTai;
    }

    @Override
    public String toString() {
        return maLopHocPhan + " (" + (tenMonHoc != null ? tenMonHoc : "") + ")";
    }
}

