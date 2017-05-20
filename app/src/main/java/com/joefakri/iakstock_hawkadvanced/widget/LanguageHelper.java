package com.joefakri.iakstock_hawkadvanced.widget;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by deny on bandung.
 */

public class LanguageHelper {
    public static void changeLocale(Resources res, String locale) {

        Configuration config;
        config = new Configuration(res.getConfiguration());
        switch (locale) {
            case "ar-rEG":
                config.locale = new Locale("ar-rEG");
                break;
            case "en-rUS":
                config.locale = new Locale("en-rUS");
                break;
            case "ar":
                config.locale = new Locale("ar");
                break;
            case "en":
                config.locale = new Locale("en");
                break;
            default:
                config.locale = Locale.ENGLISH;
                break;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
