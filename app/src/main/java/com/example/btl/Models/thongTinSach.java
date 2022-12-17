package com.example.btl.Models;

import java.io.Serializable;

public class thongTinSach implements Serializable {
    private String tenSach, theLoai, tacGia, path;
    private int soLuong;

    public thongTinSach() {
    }

    public thongTinSach(String tenSach, String theLoai, String tacGia, String path, int soLuong) {
        this.tenSach = tenSach;
        this.theLoai = theLoai;
        this.tacGia = tacGia;
        this.path = path;
        this.soLuong = soLuong;
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
                "tenSach='" + tenSach + '\'' +
                ", theLoai='" + theLoai + '\'' +
                ", tacGia='" + tacGia + '\'' +
                ", path='" + path + '\'' +
                ", soLuong=" + soLuong +
                '}';
    }
}
