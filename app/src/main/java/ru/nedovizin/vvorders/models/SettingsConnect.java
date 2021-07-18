package ru.nedovizin.vvorders.models;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/** Описание настроек
 *
 */
public class SettingsConnect {
    private String host;
    private String login;
    private String password;

    public SettingsConnect(String host, String login, String password) {
        this.host = host;
        this.login = login;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthBase64() throws UnsupportedEncodingException {
        return "Basic " + Base64.encodeToString((login + ":" + password).getBytes("UTF-8"), Base64.NO_WRAP);
    }
}
