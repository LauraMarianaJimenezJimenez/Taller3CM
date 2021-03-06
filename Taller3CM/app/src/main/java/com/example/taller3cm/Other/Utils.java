package com.example.taller3cm.Other;

public class Utils {

    public static boolean validateEmail(String email)
    {
        if(!email.isEmpty())
        {
            if(!email.matches("[\\w.-]+@[\\w]+(\\.[\\w]{2,})+"))
                return false;
            return true;
        }
        else return false;
    }

    public static boolean validatePassword(String password)
    {
        if(!password.isEmpty())
        {
            if(password.length() < 6)
                return false;
            return true;
        }
        else return false;
    }
}
