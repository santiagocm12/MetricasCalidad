
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import co.gov.ugpp.parafiscales.servicios.liquidador.fallotipo.v1.FalloTipo;



@XmlRegistry
public class ObjectFactory {

    private final static QName _OpCrearFallo_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplNomina", "OpCrearFallo");
    private final static QName _OpCrearRes_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplNomina", "OpCrearRes");
    private final static QName _OpCrearSol_QNAME = new QName("http://www.ugpp.gov.co/Liquidador/SrvAplNomina", "OpCrearSol");


    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OpCrearSolTipo }
     * 
     */
    public OpCrearSolTipo createOpCrearSolTipo() {
        return new OpCrearSolTipo();
    }

    /**
     * Create an instance of {@link OpCrearResTipo }
     * 
     */
    public OpCrearResTipo createOpCrearResTipo() {
        return new OpCrearResTipo();
    }

    /**
     * Create an instance of {@link NominaTipo }
     * 
     */
    public NominaTipo createNominaTipo() {
        return new NominaTipo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FalloTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplNomina", name = "OpCrearFallo")
    public JAXBElement<FalloTipo> createOpCrearFallo(FalloTipo value) {
        return new JAXBElement<FalloTipo>(_OpCrearFallo_QNAME, FalloTipo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpCrearResTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplNomina", name = "OpCrearRes")
    public JAXBElement<OpCrearResTipo> createOpCrearRes(OpCrearResTipo value) {
        return new JAXBElement<OpCrearResTipo>(_OpCrearRes_QNAME, OpCrearResTipo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OpCrearSolTipo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ugpp.gov.co/Liquidador/SrvAplNomina", name = "OpCrearSol")
    public JAXBElement<OpCrearSolTipo> createOpCrearSol(OpCrearSolTipo value) {
        return new JAXBElement<OpCrearSolTipo>(_OpCrearSol_QNAME, OpCrearSolTipo.class, null, value);
    }

}
