package com.a2sidorov.mychat;

public abstract class InputValidation {

    public static boolean isValidNickname(String nickname) {
        return nickname.matches("[A-Za-z0-9]{1,15}");
    }

}
