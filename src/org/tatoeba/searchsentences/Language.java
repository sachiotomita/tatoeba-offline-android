package org.tatoeba.searchsentences;

/**
 * This class represents a written language.
 * 
 * @author qdii
 * 
 */
public class Language
{
    public static final int NB_CHARS = 5;

    public Language( char code[] )
    {
        this.code = new char[NB_CHARS];
        for ( int i = 0; i < NB_CHARS; ++i )
        {
            this.code[i] = i < code.length ? code[i] : '\0';
        }

    }

    public boolean equals( Language other )
    {
        int max = Math.min( other.code.length, NB_CHARS );
        for ( int i = 0; i < max; ++i )
        {
            if ( other.code[i] != this.code[i] )
                return false;
        }
        return true;
    }

    public char[] name()
    {
        return code;
    }

    public String toString()
    {
        return new String( code );
    }

    public static String FRA = "fra";
    /*
     * private enum CODE { acm, afr, ain, ang, ara, arq, arz, ast, avk, aze,
     * bel, ben, ber, bod, bos, bre, bul, cat, ces, cha, ckt, cmn, cor, cycl,
     * cym, dan, deu, dsb, ell, eng, epo, est, eus, ewe, fao, fin, fra, fry,
     * gla, gle, glg, grn, heb, hil, hin, hrv, hsb, hun, hye, ido, ile, ina,
     * ind, isl, ita, jbo, jpn, kat, kaz, khm, kor, ksh, kur, lad, lao, lat,
     * lit, lld, lvs, lzh, mal, mar, mlg, mlt, mon, mri, nan, nds, nld, nob,
     * non, nov, npi, oci, orv, oss, pes, pms, pnb, pol, por, prg, que, qya,
     * roh, ron, rus, san, scn, sjn, slk, slv, spa, sqi, srp, swe, swh, tat,
     * tel, tgk, tgl, tha, tlh, toki, tpi, tpw, tur, uig, ukr, urd, uzb, vie,
     * vol, wuu, xal, xho, yid, yue, zsm }
     */
    private char code[];

}
