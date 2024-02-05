package co.gov.ugpp.parafiscales.servicios.liquidador.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jmunocab
 */
public final class PropsReader {

    private static final List<ResourceBundle> resourceFiles = getResourceBundle();

    public static List<ResourceBundle> getResourceBundle() {
        List<ResourceBundle> resourceBundles = new ArrayList<ResourceBundle>();
        resourceBundles.add(ResourceBundle.getBundle("messages"));
        return resourceBundles;
    }

    public static String getPropertyRS(String llave) {
        String bundleProperty = null;
        for (ResourceBundle resourceBundle : resourceFiles) {
            try {
                if(StringUtils.isNotBlank(bundleProperty)){
                    break;
                }
                bundleProperty = resourceBundle.getString(llave);
            } catch (MissingResourceException mre){}
        }
        return bundleProperty;
    }

    public static String getKeyParam(String pKey, String... pParams) {

        String keyValue = getPropertyRS(pKey);
        if (pParams != null) {
            MessageFormat mf = new MessageFormat(keyValue);
            keyValue = mf.format(((pParams)));
        }
        return keyValue;
    }
}
