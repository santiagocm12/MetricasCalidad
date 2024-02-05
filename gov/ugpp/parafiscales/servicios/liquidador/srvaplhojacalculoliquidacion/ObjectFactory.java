
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion;

import co.gov.ugpp.parafiscales.servicios.liquidador.fallotipo.v1.FalloTipo;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the co.gov.ugpp.liquidador.srvaplhojacalculoliquidacion package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _OpGenerarExcelSol_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", "OpGenerarExcelSol");
    private final static QName _OpConsultarPorIdFallo_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", "OpConsultarPorIdFallo");
    private final static QName _OpConsultarPorIdRes_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", "OpConsultarPorIdRes");
    private final static QName _OpConsultarPorIdSol_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", "OpConsultarPorIdSol");
    private final static QName _OpGenerarExcelFallo_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", "OpGenerarExcelFallo");
    private final static QName _OpGenerarExcelRes_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", "OpGenerarExcelRes");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: co.gov.ugpp.liquidador.srvaplhojacalculoliquidacion
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OpGenerarExcelSolTipo }
     * 
     */
    public OpGenerarExcelSolTipo createOpGenerarExcelSolTipo() {
        return new OpGenerarExcelSolTipo();
    }

    /**
     * Create an instance of {@link OpConsultarPorIdSolTipo }
     * 
     */
    public OpConsultarPorIdSolTipo createOpConsultarPorIdSolTipo() {
        return new OpConsultarPorIdSolTipo();
    }

    /**
     * Create an instance of {@link OpConsultarPorIdResTipo }
     * 
     */
    public OpConsultarPorIdResTipo createOpConsultarPorIdResTipo() {
        return new OpConsultarPorIdResTipo();
    }

    /**
     * Create an instance of {@link OpGenerarExcelResTipo }
     * 
     */
    public OpGenerarExcelResTipo createOpGenerarExcelResTipo() {
        return new OpGenerarExcelResTipo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpGenerarExcelSolTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", name = "OpGenerarExcelSol")
    public JAXBElement<OpGenerarExcelSolTipo> createOpGenerarExcelSol(OpGenerarExcelSolTipo value) {
        return new JAXBElement<OpGenerarExcelSolTipo>(_OpGenerarExcelSol_QNAME, OpGenerarExcelSolTipo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FalloTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", name = "OpConsultarPorIdFallo")
    public JAXBElement<FalloTipo> createOpConsultarPorIdFallo(FalloTipo value) {
        return new JAXBElement<FalloTipo>(_OpConsultarPorIdFallo_QNAME, FalloTipo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpConsultarPorIdResTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", name = "OpConsultarPorIdRes")
    public JAXBElement<OpConsultarPorIdResTipo> createOpConsultarPorIdRes(OpConsultarPorIdResTipo value) {
        return new JAXBElement<OpConsultarPorIdResTipo>(_OpConsultarPorIdRes_QNAME, OpConsultarPorIdResTipo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpConsultarPorIdSolTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", name = "OpConsultarPorIdSol")
    public JAXBElement<OpConsultarPorIdSolTipo> createOpConsultarPorIdSol(OpConsultarPorIdSolTipo value) {
        return new JAXBElement<OpConsultarPorIdSolTipo>(_OpConsultarPorIdSol_QNAME, OpConsultarPorIdSolTipo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FalloTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", name = "OpGenerarExcelFallo")
    public JAXBElement<FalloTipo> createOpGenerarExcelFallo(FalloTipo value) {
        return new JAXBElement<FalloTipo>(_OpGenerarExcelFallo_QNAME, FalloTipo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpGenerarExcelResTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplHojaCalculoLiquidacion", name = "OpGenerarExcelRes")
    public JAXBElement<OpGenerarExcelResTipo> createOpGenerarExcelRes(OpGenerarExcelResTipo value) {
        return new JAXBElement<OpGenerarExcelResTipo>(_OpGenerarExcelRes_QNAME, OpGenerarExcelResTipo.class, null, value);
    }

}
