package com.onysakura.utilities.web.api.epay;

public class ManagerLogin {

    public static void main(String[] args) throws Exception {
        String username = "vvvv";
        String password = "vvvvvv";
        String token = ManagerFunctions.validateCodeToken();
        String codeImage = ManagerFunctions.validateCode(token);
        String code = ManagerFunctions.getCode(codeImage);
        try {
            ManagerFunctions.login(token, code, username, password + "wrong");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            ManagerFunctions.login(token, code, username, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
