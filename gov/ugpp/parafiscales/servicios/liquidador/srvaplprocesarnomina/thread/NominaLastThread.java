package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread;

import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.IntegracionBPM;
import org.slf4j.LoggerFactory;

/**
 * @since 04/Ago/2017
 * @author UT_TECNOLOGI
 */
public class NominaLastThread extends NominaParentThread {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NominaLastThread.class);
    String id;
    String codigo;
    String mensaje;
    ContextoTransaccionalTipo contextoTransaccional;

    /**
     * Constructor de la clase NominaLastThread Este debe ser siempre el ultimo
     * hilo en ejecutarse
     *
     * @param id
     * @param codigo
     * @param mensaje
     */
    public NominaLastThread(String id, String codigo, String mensaje, ContextoTransaccionalTipo contextoTransaccional) {
        this.id = id;
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.contextoTransaccional = contextoTransaccional;
    }

    /**
     * Metodo principal de ejecucion del hilo
     */
    @Override
    public void run() {
        // LOG.info("Get in NominaLastThread() - " + Thread.currentThread().getName());
        if (!ERROR) {
            iniciarTarea();
            // LOG.info("Start NominaLastThread() - " + Thread.currentThread().getName());

            IntegracionBPM.servicioIntegracionRecibirNomina(id, codigo, mensaje, contextoTransaccional);
        }
        LOG.info("End NominaLastThread() - " + Thread.currentThread().getName());
        liberarHilo();
    }

}
