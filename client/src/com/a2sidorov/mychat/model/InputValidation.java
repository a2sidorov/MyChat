package com.a2sidorov.mychat.model;


public abstract class InputValidation {

    public static boolean isAddressValid(String address) {
        return address.matches("^[0-2]?[0-9]?[0-9]{1}." +
                "[0-2]?[0-9]?[0-9]{1}." +
                "[0-2]?[0-9]?[0-9]{1}." +
                "[0-2]?[0-9]?[0-9]{1}$");
    }

    public static boolean isPortValid(String port) {
        return port.matches("[0-9]{1,5}");
    }

    public static boolean isNicknameValid(String nickname) {
        return nickname.matches("[a-zA-Z0-9_$]{1,15}");
    }
}
