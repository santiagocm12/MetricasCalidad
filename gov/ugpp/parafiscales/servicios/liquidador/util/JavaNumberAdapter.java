package co.gov.ugpp.parafiscales.servicios.liquidador.util;

/**
 *
 * @author rpadilla
 */
public class JavaNumberAdapter {
    
    public static Long parseStrToLong(final String str) {
        return str == null ? null : Long.valueOf(str);
    }

    public static String parseLongToStr(final Long num) {
        return num == null ? null : num.toString();
    }    
}
