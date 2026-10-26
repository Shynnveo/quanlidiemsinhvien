package com.mycompany.duanjavanangcao.model;

import java.sql.Date;

/**
 * Model ánh xạ bảng TieuChiCanhCao trong CSDL duanjavanangcao
 */
public class TieuChiCanhCao {

    private int tieuChiId;
    private String maTieuChi;
    private String tenTieuChi;
    private String loaiCanhCao;
    private String moTa;
    private Double gpaHocKyToiDa;
    private Double gpaTichLuyToiDa;
    private Integer soTinChiNoToiThieu;
    private Integer soTinChiDatToiDa;
    private Date ngayApDung;
    private Date ngayKetThuc;
    private boolean trangThai;

    public TieuChiCanhCao() {
        this.trangThai = true;
    }

    public TieuChiCanhCao(int tieuChiId, String maTieuChi, String tenTieuChi, String loaiCanhCao,
                          String moTa, Double gpaHocKyToiDa, Double gpaTichLuyToiDa,
                          Integer soTinChiNoToiThieu, Integer soTinChiDatToiDa,
                          Date ngayApDung, Date ngayKetThuc, boolean trangThai) {
        this.tieuChiId = tieuChiId;
        this.maTieuChi = maTieuChi;
        this.tenTieuChi = tenTieuChi;
        this.loaiCanhCao = loaiCanhCao;
        this.moTa = moTa;
        this.gpaHocKyToiDa = gpaHocKyToiDa;
        this.gpaTichLuyToiDa = gpaTichLuyToiDa;
        this.soTinChiNoToiThieu = soTinChiNoToiThieu;
        this.soTinChiDatToiDa = soTinChiDatToiDa;
        this.ngayApDung = ngayApDung;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public int getTieuChiId() {
        return tieuChiId;
    }

    public void setTieuChiId(int tieuChiId) {
        this.tieuChiId = tieuChiId;
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

    public String getLoaiCanhCao() {
        return loaiCanhCao;
    }

    public void setLoaiCanhCao(String loaiCanhCao) {
        this.loaiCanhCao = loaiCanhCao;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Double getGpaHocKyToiDa() {
        return gpaHocKyToiDa;
    }

    public void setGpaHocKyToiDa(Double gpaHocKyToiDa) {
        this.gpaHocKyToiDa = gpaHocKyToiDa;
    }

    public Double getGpaTichLuyToiDa() {
        return gpaTichLuyToiDa;
    }

    public void setGpaTichLuyToiDa(Double gpaTichLuyToiDa) {
        this.gpaTichLuyToiDa = gpaTichLuyToiDa;
    }

    public Integer getSoTinChiNoToiThieu() {
        return soTinChiNoToiThieu;
    }

    public void setSoTinChiNoToiThieu(Integer soTinChiNoToiThieu) {
        this.soTinChiNoToiThieu = soTinChiNoToiThieu;
    }

    public Integer getSoTinChiDatToiDa() {
        return soTinChiDatToiDa;
    }

    public void setSoTinChiDatToiDa(Integer soTinChiDatToiDa) {
        this.soTinChiDatToiDa = soTinChiDatToiDa;
    }

    public Date getNgayApDung() {
        return ngayApDung;
    }

    public void setNgayApDung(Date ngayApDung) {
        this.ngayApDung = ngayApDung;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tenTieuChi + " (" + maTieuChi + ")";
    }
}

