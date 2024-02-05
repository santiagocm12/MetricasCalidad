package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina;

import co.gov.ugpp.parafiscales.servicios.liquidador.contextorespuestatipo.v1.ContextoRespuestaTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.AbstractSrvApl;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.AppException;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaParentThread;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.ErrorUtil;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author franzjr
 */
@WebService(serviceName = "SrvAplNomina",
        portName = "portSrvAplNomina",
        endpointInterface = "co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.PortSrvAplNomina")

public class SrvAplNomina extends AbstractSrvApl {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SrvAplNomina.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Metodo principal WS
     *
     * @param msjOpCrearSol
     * @return
     * @throws MsjOpCrearFallo
     */
    public OpCrearResTipo opCrear(OpCrearSolTipo msjOpCrearSol) throws MsjOpCrearFallo {
        LOG.error("Op: OpCrearNomina ::: INIT");

        ContextoRespuestaTipo contextoRespuesta = new ContextoRespuestaTipo();
        ContextoTransaccionalTipo contextoSolicitud = msjOpCrearSol.getContextoTransaccional();
        OpCrearResTipo crearResTipo = new OpCrearResTipo();
        NominaTipo nomina;

        try {
            initContextoRespuesta(contextoSolicitud, contextoRespuesta);
            nomina = new NominaTipo();
            nomina.setIdNomina("0");
            crearResTipo.setNomina(nomina);
            crearResTipo.setContextoRespuesta(contextoRespuesta);
            if (msjOpCrearSol.getIdExpediente().equalsIgnoreCase("Reset")) {
                String result = NominaParentThread.resetNominaPool();
                LOG.info(result);
                crearResTipo.getContextoRespuesta().setCodEstadoTx(result);
            } else {
                ldapAuthentication.validateLdapAuthentication(contextoSolicitud.getIdUsuarioAplicacion(),
                        contextoSolicitud.getValClaveUsuarioAplicacion());
                //ASINCRONICO
                NominaAsincronicaThread nominaAsincronica = new NominaAsincronicaThread(msjOpCrearSol.getIdExpediente(),
                        msjOpCrearSol.getEnvio(), contextoSolicitud, entityManager, false);
                nominaAsincronica.start();
            }

        } catch (AppException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MsjOpCrearFallo("Error ", ErrorUtil.buildFalloTipo(contextoRespuesta, ex), ex);
        } catch (RuntimeException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MsjOpCrearFallo("Error ", ErrorUtil.buildFalloTipo(contextoRespuesta, ex), ex);
        }

        LOG.error("Op: OpCrearNomina ::: END");

        return crearResTipo;
    }

}
