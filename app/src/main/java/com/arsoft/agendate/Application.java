package com.arsoft.agendate;


import com.arsoft.agendate.json.User;

import java.net.CookieHandler;
import java.net.CookieManager;

//import android.webkit.CookieManager;
//import android.os.Build;


/**
 * Created by larcho on 7/29/16.
 */
public class Application extends android.app.Application {

    private static CookieManager cookieManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Application.cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        //Idealmente ya bajar una llave pública con la URL final
        User.downloadPublicKey(getApplicationContext());
    }

    public static CookieManager getCookieManager() {
        return cookieManager;
    }


}
