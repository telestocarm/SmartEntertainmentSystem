package io.nandandesai.smartentertainmentsystem.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    private SharedPreferences sharedPreferences;
    public Settings(Context context){
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getYTSSiteLink(){
        return sharedPreferences.getString("yts_site_link", "https://yts.lt");
    }

    public Boolean getUseProxyForYTS(){
        return sharedPreferences.getBoolean("yts_proxy_pref",false);
    }

    public String getTorProxyAddress(){
        return sharedPreferences.getString("tor_proxy_ip","127.0.0.1");
    }

    public int getTorProxyPort(){
        return Integer.parseInt(sharedPreferences.getString("tor_proxy_port", "9050"));
    }


}
