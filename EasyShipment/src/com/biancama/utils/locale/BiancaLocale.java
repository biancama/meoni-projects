package com.biancama.utils.locale;

import java.io.Serializable;

public class BiancaLocale implements Serializable {

    private static final long serialVersionUID = 1116656232817008992L;

    private final String[] codes;

    private final String lngGeoCode;

    public BiancaLocale(String lngGeoCode) {
        this.lngGeoCode = lngGeoCode;
        codes = BiancaGeoCode.parseLanguageCode(lngGeoCode);
    }

    public String getCountryCode() {
        return codes[1];
    }

    public String getExtensionCode() {
        return codes[1];
    }

    public String getLanguageCode() {
        return codes[0];
    }

    public String getLngGeoCode() {
        return lngGeoCode;
    }

    @Override
    public String toString() {
        return BiancaGeoCode.toLongerNative(lngGeoCode);
    }

    @Override
    public boolean equals(Object l) {
        if (l == null || !(l instanceof BiancaLocale)) { return false; }
        return this.getLngGeoCode().equals(((BiancaLocale) l).getLngGeoCode());
    }

}
