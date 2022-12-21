package com.example.btl.Models;

import java.io.Serializable;

public class phieuMuon implements Serializable {
    private String id, idSach, tenSachMuon, nguoiMuon, SDT, ngayMuon, ngayTra, ghiChu;
    private int trangThai;

    public phieuMuon() {
    }

    public phieuMuon(String id,String idSach, String tenSachMuon, String nguoiMuon, String SDT, String ngayMuon, String ngayTra, String ghiChu, int trangThai) {
        this.id = id;
        this.idSach = idSach;
        this.tenSachMuon = tenSachMuon;
        this.nguoiMuon = nguoiMuon;
        this.SDT = SDT;
        this.ngayMuon = ngayMuon;
        this.ngayTra = ngayTra;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public String getIdSach() {
        return idSach;
    }

    public void setIdSach(String idSach) {
        this.idSach = idSach;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenSachMuon() {
        return tenSachMuon;
    }

    public void setTenSachMuon(String tenSachMuon) {
        this.tenSachMuon = tenSachMuon;
    }

    public String getNguoiMuon() {
        return nguoiMuon;
    }

    public void setNguoiMuon(String nguoiMuon) {
        this.nguoiMuon = nguoiMuon;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public String getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(String ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public String getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(String ngayTra) {
        this.ngayTra = ngayTra;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "phieuMuon{" +
                "id='" + id + '\'' +
                ", idSach='" + idSach + '\'' +
                ", tenSachMuon='" + tenSachMuon + '\'' +
                ", nguoiMuon='" + nguoiMuon + '\'' +
                ", SDT='" + SDT + '\'' +
                ", ngayMuon='" + ngayMuon + '\'' +
                ", ngayTra='" + ngayTra + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                ", trangThai=" + trangThai +
                '}';
    }
}
