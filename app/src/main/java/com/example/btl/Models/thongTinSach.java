package com.example.btl.Models;

import android.widget.Adapter;

import java.io.Serializable;

public class thongTinSach implements Serializable {
    private String  id, tenSach, theLoai, tacGia, moTa, viTri, path;
    private int soLuong;

    public thongTinSach() {
    }

//    public thongTinSach(String tenSach, String theLoai, String tacGia, String moTa, String viTri, String path, int soLuong) {
//        this.tenSach = tenSach;
//        this.theLoai = theLoai;
//        this.tacGia = tacGia;
//        this.moTa = moTa;
//        this.viTri = viTri;
//        this.path = path;
//        this.soLuong = soLuong;
//    }

    public thongTinSach(String id, String tenSach, String theLoai, String tacGia, String moTa, String viTri, String path, int soLuong) {
        this.id = id;
        this.tenSach = tenSach;
        this.theLoai = theLoai;
        this.tacGia = tacGia;
        this.moTa = moTa;
        this.viTri = viTri;
        this.path = path;
        this.soLuong = soLuong;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public String getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    @Override
    public String toString() {
        return "thongTinSach{" +
                "id='" + id + '\'' +
                ", tenSach='" + tenSach + '\'' +
                ", theLoai='" + theLoai + '\'' +
                ", tacGia='" + tacGia + '\'' +
                ", moTa='" + moTa + '\'' +
                ", viTri='" + viTri + '\'' +
                ", path='" + path + '\'' +
                ", soLuong=" + soLuong +
                '}';
    }
}
