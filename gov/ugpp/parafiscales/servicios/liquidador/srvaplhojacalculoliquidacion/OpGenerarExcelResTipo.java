
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion;

import co.gov.ugpp.parafiscales.servicios.liquidador.archivotipo.v1.ArchivoTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.contextorespuestatipo.v1.ContextoRespuestaTipo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para OpGenerarExcelResTipo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="OpGenerarExcelResTipo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contextoRespuesta" type="{http://www.ugpp.gov.co/esb/schema/ContextoRespuestaTipo/v1}ContextoRespuestaTipo"/>
 *         &lt;element name="hojaCalculoLiquidacionEXCEL" type="{http://www.ugpp.gov.co/schema/GestionDocumental/ArchivoTipo/v1}ArchivoTipo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpGenerarExcelResTipo", propOrder = {
    "contextoRespuesta",
    "hojaCalculoLiquidacionEXCEL"
})
public class OpGenerarExcelResTipo {

    @XmlElement(required = true)
    protected ContextoRespuestaTipo contextoRespuesta;
    protected ArchivoTipo hojaCalculoLiquidacionEXCEL;

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
     * Obtiene el valor de la propiedad hojaCalculoLiquidacionEXCEL.
     * 
     * @return
     *     possible object is
     *     {@link ArchivoTipo }
     *     
     */
    public ArchivoTipo getHojaCalculoLiquidacionEXCEL() {
        return hojaCalculoLiquidacionEXCEL;
    }

    /**
     * Define el valor de la propiedad hojaCalculoLiquidacionEXCEL.
     * 
     * @param value
     *     allowed object is
     *     {@link ArchivoTipo }
     *     
     */
    public void setHojaCalculoLiquidacionEXCEL(ArchivoTipo value) {
        this.hojaCalculoLiquidacionEXCEL = value;
    }

}
