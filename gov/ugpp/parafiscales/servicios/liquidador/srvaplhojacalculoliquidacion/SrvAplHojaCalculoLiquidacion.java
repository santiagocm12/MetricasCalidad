package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion;


import co.gov.ugpp.parafiscales.servicios.liquidador.archivotipo.v1.ArchivoTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.contextorespuestatipo.v1.ContextoRespuestaTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.hojacalculotipo.v1.HojaCalculoTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.AbstractSrvApl;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.AppException;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.ErrorUtil;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.LoggerFactory;

/**
 *
 * @author franzjr
 */
@WebService(serviceName = "SrvAplHojaCalculoLiquidacion",
        portName = "portSrvAplHojaCalculoLiquidacion",
        endpointInterface = "co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion.PortSrvAplHojaCalculoLiquidacion")

public class SrvAplHojaCalculoLiquidacion extends AbstractSrvApl {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SrvAplHojaCalculoLiquidacion.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    
    @EJB
    private HojaCalculoLiquidacionFacade hojaCalculoLiquidacionFacade;
    
    public OpConsultarPorIdResTipo opConsultarPorId(OpConsultarPorIdSolTipo msjOpConsultarPorIdSol) throws MsjOpConsultarPorIdFallo {
        LOG.error("Op: opConsultarPorId ::: INIT");

        ContextoRespuestaTipo contextoRespuesta = new ContextoRespuestaTipo();
        ContextoTransaccionalTipo contextoSolicitud = msjOpConsultarPorIdSol.getContextoTransaccional();

        HojaCalculoTipo hojaCalculoTipo;

        try {
            this.initContextoRespuesta(contextoSolicitud, contextoRespuesta);
            ldapAuthentication.validateLdapAuthentication(
                    contextoSolicitud.getIdUsuarioAplicacion(), contextoSolicitud.getValClaveUsuarioAplicacion());
            
            hojaCalculoTipo = hojaCalculoLiquidacionFacade.consultarPorId(msjOpConsultarPorIdSol.getIdHojaCalculoLiquidacion(), contextoSolicitud,entityManager);

        } catch (AppException ex) {
            ex.printStackTrace();
            throw new MsjOpConsultarPorIdFallo("Error ", ErrorUtil.buildFalloTipo(contextoRespuesta, ex), ex);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new MsjOpConsultarPorIdFallo("Error ", ErrorUtil.buildFalloTipo(contextoRespuesta, ex), ex);
        }

        OpConsultarPorIdResTipo consultarPorIdResTipo = new OpConsultarPorIdResTipo();
        consultarPorIdResTipo.setContextoRespuesta(contextoRespuesta);
        consultarPorIdResTipo.setHojaCalculoLiquidacion(hojaCalculoTipo);

        LOG.error("Op: opConsultarPorId ::: END");

        return consultarPorIdResTipo;
    }

    public OpGenerarExcelResTipo opGenerarExcel(OpGenerarExcelSolTipo msjOpGenerarExcelSol) throws MsjOpGenerarExcelFallo {
        LOG.error("Op: OpGenerarExcel ::: INIT");

        ContextoRespuestaTipo contextoRespuesta = new ContextoRespuestaTipo();
        ContextoTransaccionalTipo contextoSolicitud = msjOpGenerarExcelSol.getContextoTransaccional();
        ArchivoTipo hojaCalculoLiquidacionEXCEL;

        try 
        {
            this.initContextoRespuesta(contextoSolicitud, contextoRespuesta);
            ldapAuthentication.validateLdapAuthentication(
                    contextoSolicitud.getIdUsuarioAplicacion(), contextoSolicitud.getValClaveUsuarioAplicacion());
            hojaCalculoLiquidacionEXCEL = hojaCalculoLiquidacionFacade.generarExcel(msjOpGenerarExcelSol.getIdHojaCalculoLiquidacion(), contextoSolicitud,entityManager);
        } catch (AppException ex) {
            ex.printStackTrace();
            throw new MsjOpGenerarExcelFallo("Error ", ErrorUtil.buildFalloTipo(contextoRespuesta, ex), ex);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new MsjOpGenerarExcelFallo("Error ", ErrorUtil.buildFalloTipo(contextoRespuesta, ex), ex);
        }

        OpGenerarExcelResTipo generarExcelResTipo = new OpGenerarExcelResTipo();
        generarExcelResTipo.setContextoRespuesta(contextoRespuesta);
        generarExcelResTipo.setHojaCalculoLiquidacionEXCEL(hojaCalculoLiquidacionEXCEL);

        LOG.error("Op: OpGenerarExcel ::: END");

        return generarExcelResTipo;
    }

}
