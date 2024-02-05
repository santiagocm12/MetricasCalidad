
package co.gov.ugpp.parafiscales.servicios.liquidador.util;

import co.gov.ugpp.parafiscales.servicios.liquidador.util.DateUtil;
import java.util.Calendar;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter1
    extends XmlAdapter<String, Calendar>
{


    public Calendar unmarshal(String value) {
        return (DateUtil.parseStrDateTimeToCalendar(value));
    }

    public String marshal(Calendar value) {
        return (DateUtil.parseCalendarToStrDateTime(value));
    }

}
