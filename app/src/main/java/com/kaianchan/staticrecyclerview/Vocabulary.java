package com.kaianchan.staticrecyclerview;

public class Vocabulary {

    private String vPort;
    private String vChin;

    // Constructor
    public Vocabulary(String vChin, String vPort) {
        this.vChin = vChin;
        this.vPort = vPort;
    }


    // Getter

    public String getvPort() {
        return vPort;
    }

    public String getvChin() {
        return vChin;
    }
}
