package co.gov.ugpp.parafiscales.servicios.liquidador.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rpadilla
 */
public class DateUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    public static final DateFormat anoMesDia = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
    public static final DateFormat diaMesAno = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
    public static final DateFormat anoMesDiaTHoraMinutoSegundo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);
    public static final DateFormat anoMesDiaHoraMinutos = new SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.ROOT);

    public static Calendar currentCalendar() {
        final Calendar calendar = new GregorianCalendar();
        return calendar;
    }

    public static Calendar parseYearMonthToCalendar(final Long year, final Short month) {
        final Calendar calendar = new GregorianCalendar(year.intValue(), month, 0);
        return calendar;
    }

    public static Calendar parseStrDateTimeToCalendar(final String srcObj){
        if (StringUtils.isBlank(srcObj)) {
            return null;
        }
        try {
            diaMesAno.setLenient(false);
            final Calendar cal = currentCalendar();
            final Date date = diaMesAno.parse(srcObj);
            cal.setTime(date);
            return cal;
        } catch (final ParseException pa) {
            //LOG.warn("No se pudo parsear la fecha con formato estándar, intentando con otro formato");
            try {
                anoMesDiaHoraMinutos.setLenient(false);
                final Calendar cal = currentCalendar();
                final Date date = anoMesDiaHoraMinutos.parse(srcObj);
                cal.setTime(date);
                return cal;
            } catch (ParseException ex) {
                try {
                    anoMesDiaTHoraMinutoSegundo.setLenient(false);
                    final Calendar cal = currentCalendar();
                    final Date date = anoMesDiaTHoraMinutoSegundo.parse(srcObj);
                    cal.setTime(date);
                    return cal;
                } catch (ParseException pe) {
                    try {
                        return javax.xml.bind.DatatypeConverter.parseDateTime(srcObj);
                    } catch (IllegalArgumentException iae) {
                        System.out.println("::ERROR:: Exception no se pudo parsear la Fecha DateTime: " + iae);
                        return null;
                    }
                }
            }
        }
    }

    public static String parseCalendarToStrDateTime(final Calendar srcObj) {
        return srcObj == null ? null : javax.xml.bind.DatatypeConverter.printDateTime(srcObj);
    }

    public static Calendar parseStrDateToCalendar(final String str) {
        return str == null ? null : javax.xml.bind.DatatypeConverter.parseDate(str);
    }

    public static String parseCalendarToStrDate(final Calendar cal) {
        String vFechaConvertida = null;
        if (cal != null) {
            anoMesDiaHoraMinutos.setTimeZone(TimeZone.getDefault());
            vFechaConvertida = anoMesDiaHoraMinutos.format(cal.getTime());
        }
        return vFechaConvertida;
    }

    public static String parseCalendarToStrDateBPMFormat(final Calendar cal) {
        String vFechaConvertida = null;
        if (cal != null) {
            anoMesDiaTHoraMinutoSegundo.setTimeZone(TimeZone.getDefault());
            vFechaConvertida = anoMesDiaTHoraMinutoSegundo.format(cal.getTime());
        }
        return vFechaConvertida;
    }

    public static String parseCalendarToStrDateNoHour(final Calendar cal) {
        String vFechaConvertida = null;
        if (cal != null) {
            anoMesDia.setTimeZone(TimeZone.getDefault());
            vFechaConvertida = anoMesDia.format(cal.getTime());
        }
        return vFechaConvertida;
    }

    public static Calendar parseStrTimeToCalendar(final String str) {
        return str == null ? null : javax.xml.bind.DatatypeConverter.parseTime(str);
    }

    public static String parseCalendarToStrTime(final Calendar cal) {
        return cal == null ? null : javax.xml.bind.DatatypeConverter.printTime(cal);
    }

    public static String parseCalendarToString(final Calendar cal) {
        return cal.get(cal.YEAR) + "-"
                + (cal.get(cal.MONTH) + 1) + "-"
                + (cal.get(cal.DAY_OF_MONTH));
    }

    public static String parseDateToString(final Date date) {
        return anoMesDia.format(date);
    }
    
    public static String parseDateToStringTHoraMinutoSegundo(final Date date) {
        return anoMesDiaTHoraMinutoSegundo.format(date);
    }

    public static boolean validarFormatoFecha(String fecha) {

        try {
            anoMesDia.parse(fecha);
        } catch (ParseException e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }

}
