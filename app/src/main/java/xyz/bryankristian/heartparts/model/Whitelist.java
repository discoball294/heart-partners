package xyz.bryankristian.heartparts.model;

public class Whitelist {
    private String userUID;
    private String whitelistEmail;
    private String whitelistName;
    private String whitelistTelepon;
    private String OSPlayerId;

    public Whitelist(){

    }

    public Whitelist(String userUID, String whitelistEmail, String whitelistName, String whitelistTelepon, String OSPlayerId) {
        this.userUID = userUID;
        this.whitelistEmail = whitelistEmail;
        this.whitelistName = whitelistName;
        this.whitelistTelepon = whitelistTelepon;
        this.OSPlayerId = OSPlayerId;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getWhitelistEmail() {
        return whitelistEmail;
    }

    public void setWhitelistEmail(String whitelistEmail) {
        this.whitelistEmail = whitelistEmail;
    }

    public String getWhitelistName() {
        return whitelistName;
    }

    public void setWhitelistName(String whitelistName) {
        this.whitelistName = whitelistName;
    }

    public String getWhitelistTelepon() {
        return whitelistTelepon;
    }

    public void setWhitelistTelepon(String whitelistTelepon) {
        this.whitelistTelepon = whitelistTelepon;
    }

    public String getOSPlayerId() {
        return OSPlayerId;
    }

    public void setOSPlayerId(String OSPlayerId) {
        this.OSPlayerId = OSPlayerId;
    }
}
