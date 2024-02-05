
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.enviotipo.v1.EnvioTipo;


/**
 * <p>Clase Java para OpCrearSolTipo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="OpCrearSolTipo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="envio" type="{http://www.ugpp.gov.co/schema/SolicitarInformacion/EnvioTipo/v1}EnvioTipo" minOccurs="0"/>
 *         &lt;element name="idExpediente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contextoTransaccional" type="{http://www.ugpp.gov.co/esb/schema/ContextoTransaccionalTipo/v1}ContextoTransaccionalTipo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpCrearSolTipo", propOrder = {
    "envio",
    "idExpediente",
    "contextoTransaccional"
})
public class OpCrearSolTipo {

    protected EnvioTipo envio;
    protected String idExpediente;
    protected ContextoTransaccionalTipo contextoTransaccional;

    /**
     * Obtiene el valor de la propiedad envio.
     * 
     * @return
     *     possible object is
     *     {@link EnvioTipo }
     *     
     */
    public EnvioTipo getEnvio() {
        return envio;
    }

    /**
     * Define el valor de la propiedad envio.
     * 
     * @param value
     *     allowed object is
     *     {@link EnvioTipo }
     *     
     */
    public void setEnvio(EnvioTipo value) {
        this.envio = value;
    }

    /**
     * Obtiene el valor de la propiedad idExpediente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdExpediente() {
        return idExpediente;
    }

    /**
     * Define el valor de la propiedad idExpediente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdExpediente(String value) {
        this.idExpediente = value;
    }

    /**
     * Obtiene el valor de la propiedad contextoTransaccional.
     * 
     * @return
     *     possible object is
     *     {@link ContextoTransaccionalTipo }
     *     
     */
    public ContextoTransaccionalTipo getContextoTransaccional() {
        return contextoTransaccional;
    }

    /**
     * Define el valor de la propiedad contextoTransaccional.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextoTransaccionalTipo }
     *     
     */
    public void setContextoTransaccional(ContextoTransaccionalTipo value) {
        this.contextoTransaccional = value;
    }

}
