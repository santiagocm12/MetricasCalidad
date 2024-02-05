
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion;

import co.gov.ugpp.parafiscales.servicios.liquidador.contextorespuestatipo.v1.ContextoRespuestaTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.hojacalculotipo.v1.HojaCalculoTipo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para OpConsultarPorIdResTipo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="OpConsultarPorIdResTipo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contextoRespuesta" type="{http://www.ugpp.gov.co/esb/schema/ContextoRespuestaTipo/v1}ContextoRespuestaTipo"/>
 *         &lt;element name="hojaCalculoLiquidacion" type="{http://www.ugpp.gov.co/schema/Liquidaciones/HojaCalculoTipo/v1}HojaCalculoTipo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpConsultarPorIdResTipo", propOrder = {
    "contextoRespuesta",
    "hojaCalculoLiquidacion"
})
public class OpConsultarPorIdResTipo {

    @XmlElement(required = true)
    protected ContextoRespuestaTipo contextoRespuesta;
    protected HojaCalculoTipo hojaCalculoLiquidacion;

    /**
     * Obtiene el valor de la propiedad contextoRespuesta.
     * 
     * @return
     *     possible object is
     *     {@link ContextoRespuestaTipo }
     *     
     */
    public ContextoRespuestaTipo getContextoRespuesta() {
        return contextoRespuesta;
    }

    /**
     * Define el valor de la propiedad contextoRespuesta.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextoRespuestaTipo }
     *     
     */
    public void setContextoRespuesta(ContextoRespuestaTipo value) {
        this.contextoRespuesta = value;
    }

    /**
     * Obtiene el valor de la propiedad hojaCalculoLiquidacion.
     * 
     * @return
     *     possible object is
     *     {@link HojaCalculoTipo }
     *     
     */
    public HojaCalculoTipo getHojaCalculoLiquidacion() {
        return hojaCalculoLiquidacion;
    }

    /**
     * Define el valor de la propiedad hojaCalculoLiquidacion.
     * 
     * @param value
     *     allowed object is
     *     {@link HojaCalculoTipo }
     *     
     */
    public void setHojaCalculoLiquidacion(HojaCalculoTipo value) {
        this.hojaCalculoLiquidacion = value;
    }

}
