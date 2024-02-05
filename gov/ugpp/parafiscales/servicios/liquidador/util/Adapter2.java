
package co.gov.ugpp.parafiscales.servicios.liquidador.util;

import co.gov.ugpp.parafiscales.servicios.liquidador.util.JavaNumberAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter2
    extends XmlAdapter<String, Long>
{


    public Long unmarshal(String value) {
        return (JavaNumberAdapter.parseStrToLong(value));
    }

    public String marshal(Long value) {
        return (JavaNumberAdapter.parseLongToStr(value));
    }

}
