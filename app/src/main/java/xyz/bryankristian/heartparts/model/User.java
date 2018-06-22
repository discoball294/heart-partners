package xyz.bryankristian.heartparts.model;

/**
 * Created by bryanasakristian on 6/12/18.
 */

public class User {
    private String nama;
    private String tempatLahir;
    private String tanggalLahir;
    private String alamat;
    private String telepon;
    private String beratBadan;

    public User(){

    }

    public User(String nama, String tempatLahir, String tanggalLahir, String alamat, String telepon, String beratBadan){
        this.nama = nama;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.alamat = alamat;
        this.telepon = telepon;
        this.beratBadan = beratBadan;
    }

    public String getNama() {
        return nama;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getTelepon() {
        return telepon;
    }

    public String getBeratBadan() {
        return beratBadan;
    }
}
