package com.mycompany.duanjavanangcao.model;

import java.sql.Timestamp;

/**
 * Model ánh xạ bảng ThongBao trong CSDL duanjavanangcao
 */
public class ThongBao {

    private int thongBaoId;
    private int taiKhoanId;
    private String tieuDe;
    private String noiDung;
    private String loaiThongBao;
    private Timestamp thoiGianTao;
    private boolean daDoc;
    private Timestamp thoiGianDoc;

    public ThongBao() {
    }

    public ThongBao(int thongBaoId, int taiKhoanId, String tieuDe, String noiDung,
                    String loaiThongBao, Timestamp thoiGianTao, boolean daDoc, Timestamp thoiGianDoc) {
        this.thongBaoId = thongBaoId;
        this.taiKhoanId = taiKhoanId;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.loaiThongBao = loaiThongBao;
        this.thoiGianTao = thoiGianTao;
        this.daDoc = daDoc;
        this.thoiGianDoc = thoiGianDoc;
    }

    public int getThongBaoId() {
        return thongBaoId;
    }

    public void setThongBaoId(int thongBaoId) {
        this.thongBaoId = thongBaoId;
    }

    public int getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(int taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getLoaiThongBao() {
        return loaiThongBao;
    }

    public void setLoaiThongBao(String loaiThongBao) {
        this.loaiThongBao = loaiThongBao;
    }

    public Timestamp getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(Timestamp thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    public boolean isDaDoc() {
        return daDoc;
    }

    public void setDaDoc(boolean daDoc) {
        this.daDoc = daDoc;
    }

    public Timestamp getThoiGianDoc() {
        return thoiGianDoc;
    }

    public void setThoiGianDoc(Timestamp thoiGianDoc) {
        this.thoiGianDoc = thoiGianDoc;
    }
}

