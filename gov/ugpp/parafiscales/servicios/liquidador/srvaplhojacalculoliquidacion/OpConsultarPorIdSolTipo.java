
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion;

import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para OpConsultarPorIdSolTipo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="OpConsultarPorIdSolTipo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contextoTransaccional" type="{http://www.ugpp.gov.co/esb/schema/ContextoTransaccionalTipo/v1}ContextoTransaccionalTipo"/>
 *         &lt;element name="idHojaCalculoLiquidacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpConsultarPorIdSolTipo", propOrder = {
    "contextoTransaccional",
    "idHojaCalculoLiquidacion"
})
public class OpConsultarPorIdSolTipo {

    @XmlElement(required = true)
    protected ContextoTransaccionalTipo contextoTransaccional;
    @XmlElement(required = true)
    protected String idHojaCalculoLiquidacion;

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

    /**
     * Obtiene el valor de la propiedad idHojaCalculoLiquidacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdHojaCalculoLiquidacion() {
        return idHojaCalculoLiquidacion;
    }

    /**
     * Define el valor de la propiedad idHojaCalculoLiquidacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdHojaCalculoLiquidacion(String value) {
        this.idHojaCalculoLiquidacion = value;
    }

}
