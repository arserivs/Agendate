package com.arsoft.agendate.json.typeconverter;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;
import com.arsoft.agendate.json.User;

/**
 * Created by larcho on 1/26/17.
 */

public class StringToMenuStatus extends StringBasedTypeConverter<StringToMenuStatus.MenuStatus> {

    @Override
    public MenuStatus getFromString(String string) {
        if(string.equals("A")) {
            return MenuStatus.ACTIVE;
        } else if(string.equals("I")) {
            return MenuStatus.DISABLED;
        } else if(string.equals("O")) {
            return MenuStatus.HIDDEN;
        } else {
            return MenuStatus.HIDDEN;
        }
    }

    @Override
    public String convertToString(MenuStatus object) {
        switch (object) {
            case ACTIVE:
                return "A";
            case DISABLED:
                return "I";
            case HIDDEN:
                return "O";
            default:
                return "";
        }
    }

    public enum MenuStatus {
        ACTIVE, DISABLED, HIDDEN;
    }




}
