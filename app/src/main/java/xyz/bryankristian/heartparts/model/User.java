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
    private String email;
    private String oneSignalPlayerId;

    public User(){

    }

    public User(String nama, String tempatLahir, String tanggalLahir, String alamat, String telepon, String beratBadan, String email, String oneSignalPlayerId) {
        this.nama = nama;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.alamat = alamat;
        this.telepon = telepon;
        this.beratBadan = beratBadan;
        this.email = email;
        this.oneSignalPlayerId = oneSignalPlayerId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOneSignalPlayerId() {
        return oneSignalPlayerId;
    }

    public void setOneSignalPlayerId(String oneSignalPlayerId) {
        this.oneSignalPlayerId = oneSignalPlayerId;
    }
}
