package com.example.housesforrent.Adapters;

public class Post {
    String ID;
    String tieuDe;
    String gia;
    String dienTich;
    String thumnailURL;
    String diaChi;

    public String getThumnailURL() {
        return thumnailURL;
    }


    public void setThumnailURL(String thumnailURL) {
        this.thumnailURL = thumnailURL;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public Post(String ID, String tieuDe, String gia, String dienTich, String diaChi) {
        this.ID = ID;
        this.tieuDe = tieuDe;
        this.gia = gia;
        this.dienTich = dienTich;
        this.diaChi = diaChi;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getGia() {
        return gia;
    }

    public void setGia(String gia) {
        this.gia = gia;
    }

    public String getDienTich() {
        return dienTich;
    }

    public void setDienTich(String dienTich) {
        this.dienTich = dienTich;
    }
}
