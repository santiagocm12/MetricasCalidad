
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para NominaTipo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="NominaTipo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idNomina" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoEstado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NominaTipo", propOrder = {
    "idNomina",
    "codigoEstado",
    "estado"
})
public class NominaTipo {

    @XmlElement(required = true)
    protected String idNomina;
    protected String codigoEstado;
    protected String estado;

    /**
     * Obtiene el valor de la propiedad idNomina.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdNomina() {
        return idNomina;
    }

    /**
     * Define el valor de la propiedad idNomina.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdNomina(String value) {
        this.idNomina = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoEstado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoEstado() {
        return codigoEstado;
    }

    /**
     * Define el valor de la propiedad codigoEstado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoEstado(String value) {
        this.codigoEstado = value;
    }

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

}
