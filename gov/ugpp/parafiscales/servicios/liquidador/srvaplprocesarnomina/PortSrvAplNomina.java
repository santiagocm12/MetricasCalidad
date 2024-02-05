
package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.8
 * Generated source version: 2.2
 * 
 */
@WebService(name = "portSrvAplNomina", targetNamespace = "http://www.ugpp.gov.co/Liquidador/SrvAplNomina")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.ObjectFactory.class,
    co.gov.ugpp.parafiscales.servicios.liquidador.errortipo.v1.ObjectFactory.class,
    co.gov.ugpp.parafiscales.servicios.liquidador.fallotipo.v1.ObjectFactory.class,
    co.gov.ugpp.parafiscales.servicios.liquidador.identificaciontipo.v1.ObjectFactory.class,
    co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.ObjectFactory.class,
    co.gov.ugpp.parafiscales.servicios.liquidador.archivotipo.v1.ObjectFactory.class,
    co.gov.ugpp.parafiscales.servicios.liquidador.enviotipo.v1.ObjectFactory.class,
    co.gov.ugpp.parafiscales.servicios.liquidador.radicaciontipo.v1.ObjectFactory.class
})
public interface PortSrvAplNomina {


    
    @WebMethod(operationName = "OpCrear", action = "http://www.ugpp.gov.co/Liquidador/SrvAplNomina/OpCrear")
    @WebResult(name = "OpCrearRes", targetNamespace = "http://www.ugpp.gov.co/Liquidador/SrvAplNomina", partName = "msjOpCrearRes")
    public OpCrearResTipo opCrear(
        @WebParam(name = "OpCrearSol", targetNamespace = "http://www.ugpp.gov.co/Liquidador/SrvAplNomina", partName = "msjOpCrearSol")
        OpCrearSolTipo msjOpCrearSol)
        throws MsjOpCrearFallo
    ;

}