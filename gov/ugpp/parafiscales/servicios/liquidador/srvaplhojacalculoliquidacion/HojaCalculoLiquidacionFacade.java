package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion;

import co.gov.ugpp.parafiscales.servicios.liquidador.archivotipo.v1.ArchivoTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.hojacalculotipo.v1.HojaCalculoTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.AppException;
import java.io.Serializable;
import javax.persistence.EntityManager;

/**
 *
 * @author franzjr
 */
public interface HojaCalculoLiquidacionFacade extends Serializable {
    public HojaCalculoTipo consultarPorId(String idHojaCalculoLiquidacion, ContextoTransaccionalTipo contextoSolicitud, EntityManager entityManager) throws AppException ;
    public ArchivoTipo generarExcel(String idHojaCalculoLiquidacion, ContextoTransaccionalTipo contextoSolicitud, EntityManager entityManager) throws AppException ;

}
