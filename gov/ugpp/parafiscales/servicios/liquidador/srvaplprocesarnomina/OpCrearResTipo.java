
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import co.gov.ugpp.parafiscales.servicios.liquidador.contextorespuestatipo.v1.ContextoRespuestaTipo;


/**
 * <p>Clase Java para OpCrearResTipo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="OpCrearResTipo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nomina" type="{http://www.ugpp.gov.co/Liquidador/SrvAplNomina}NominaTipo" minOccurs="0"/>
 *         &lt;element name="contextoRespuesta" type="{http://www.ugpp.gov.co/esb/schema/ContextoRespuestaTipo/v1}ContextoRespuestaTipo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpCrearResTipo", propOrder = {
    "nomina",
    "contextoRespuesta"
})
public class OpCrearResTipo {

    protected NominaTipo nomina;
    protected ContextoRespuestaTipo contextoRespuesta;

    /**
     * Obtiene el valor de la propiedad nomina.
     * 
     * @return
     *     possible object is
     *     {@link NominaTipo }
     *     
     */
    public NominaTipo getNomina() {
        return nomina;
    }

    /**
     * Define el valor de la propiedad nomina.
     * 
     * @param value
     *     allowed object is
     *     {@link NominaTipo }
     *     
     */
    public void setNomina(NominaTipo value) {
        this.nomina = value;
    }

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

}
