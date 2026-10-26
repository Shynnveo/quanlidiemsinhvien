package com.mycompany.duanjavanangcao.model;

import com.mycompany.duanjavanangcao.util.DiemUtil;
import java.sql.Timestamp;

/**
 * Model ánh xạ bảng KetQuaHocTap trong CSDL duanjavanangcao
 */
public class KetQuaHocTap {

    private Integer ketQuaHocTapId;
    private int sinhVienId;
    private int hocKyId;
    private Double gpaHocKy;
    private Double gpaTichLuy;
    private int soTinChiDangKy;
    private int soTinChiDat;
    private int tongTinChiTichLuy;
    private Timestamp ngayCapNhat;

    // Các trường liên kết hiển thị
    private String maSinhVien;
    private String hoTenSinhVien;
    private String tenLop;
    private String maHocKy;
    private String tenHocKy;
    private String namHoc;

    public KetQuaHocTap() {
    }

    public Integer getKetQuaHocTapId() {
        return ketQuaHocTapId;
    }

    public void setKetQuaHocTapId(Integer ketQuaHocTapId) {
        this.ketQuaHocTapId = ketQuaHocTapId;
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

    public int getSoTinChiDangKy() {
        return soTinChiDangKy;
    }

    public void setSoTinChiDangKy(int soTinChiDangKy) {
        this.soTinChiDangKy = soTinChiDangKy;
    }

    public int getSoTinChiDat() {
        return soTinChiDat;
    }

    public void setSoTinChiDat(int soTinChiDat) {
        this.soTinChiDat = soTinChiDat;
    }

    public int getTongTinChiTichLuy() {
        return tongTinChiTichLuy;
    }

    public void setTongTinChiTichLuy(int tongTinChiTichLuy) {
        this.tongTinChiTichLuy = tongTinChiTichLuy;
    }

    public Timestamp getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Timestamp ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
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

    public String getXepLoaiHocKy() {
        return DiemUtil.xepLoaiGPA4(gpaHocKy);
    }

    public String getXepLoaiTichLuy() {
        return DiemUtil.xepLoaiGPA4(gpaTichLuy);
    }

    @Override
    public String toString() {
        return maSinhVien + " - " + hoTenSinhVien + ": GPA HK=" + gpaHocKy + ", Tích lũy=" + gpaTichLuy;
    }
}

