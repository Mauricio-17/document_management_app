package com.maurice.DocumentManagement.utils;

import com.maurice.DocumentManagement.entities.DocumentDetail;
import com.maurice.DocumentManagement.exceptions.NotAcceptableRequestException;

import java.util.Collection;
import java.util.List;

public class Validators {

    public static void minimum(String value, short minimum){
        if(value.length() < minimum){
            throw new NotAcceptableRequestException("The field must contain at least "+minimum+" characters");
        }
    }

    public static void maximum(String value, short maximum){
        if(value.length() > maximum){
            throw new NotAcceptableRequestException("The field must contain less than "+maximum+" characters");
        }
    }


}
