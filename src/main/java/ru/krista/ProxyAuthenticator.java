package ru.krista;



import java.net.*;

public class ProxyAuthenticator extends Authenticator {

private PasswordAuthentication auth;

ProxyAuthenticator(String user, String password) {
    auth = new PasswordAuthentication(user, password == null ? new char[]{} : password.toCharArray());
}

protected PasswordAuthentication getPasswordAuthentication() {
    return auth;
}
}