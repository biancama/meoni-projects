package com.biancama.utils.gui;

import java.math.BigDecimal;

import com.biancama.gui.UserIO;
import com.biancama.utils.locale.BiancaL;

public class ValidatorUtils {
    private ValidatorUtils(){}

    public static BigDecimal checkNumber(String value){
        BigDecimal test = null;
       
            try{
                test = new BigDecimal((String) value);
            }catch(NumberFormatException ex){
                UserIO.getInstance().requestConfirmDialog(UserIO.NO_COUNTDOWN | UserIO.NO_CANCEL_OPTION, BiancaL.L("shipment.quantity.validator", "Insert digit!"));
                return null;
            }      
       
        return test;
    }
    
    public static Integer checkInteger(String value){
        Integer test = null;
       
            try{
                test = new Integer((String) value);
            }catch(NumberFormatException ex){
                UserIO.getInstance().requestConfirmDialog(UserIO.NO_COUNTDOWN | UserIO.NO_CANCEL_OPTION, BiancaL.L("shipment.quantity.validator", "Insert digit!"));
                return null;
            }      
       
        return test;
    }
}
