package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina;

import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.AportanteLIQ;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.EnvioInformacionExterna;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.Expediente;
//import co.gov.ugpp.parafiscales.servicios.liquidador.entity.HojaCalculoLiquidacion;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.InformacionExternaDefiniti;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.Nomina;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.TipoIdentificacion;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.Trazabilidad;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaLastThread;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaMultiThread;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaParentThread;
import static co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaParentThread.cargarListaTiposDocumentos;
//import static co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaParentThread.convertToString;
import static co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaParentThread.getTipoDOcumentoBySigla;
import static co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaParentThread.isDaemonNominaPool;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.DateUtil;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.PropsReader;
import co.gov.ugpp.parafiscales.servicios.liquidador.enviotipo.v1.EnvioTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.jpa.InformacionExternaDefinitiDao;
import static co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread.NominaParentThread.convertToString;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.IntegracionBPM;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ejb.TransactionManagement;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
//import javax.xml.ws.BindingProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.LoggerFactory;

//import java.time.LocalDate;

/**
 *
 * @author --
 */
@TransactionManagement
public class NominaAsincronicaThread extends Thread implements java.io.Serializable {

    private static final long serialVersionUID = 7038902816875760113L;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NominaAsincronicaThread.class);
    protected static final Object SYNCHRONIZED_OBJECT = new Object();

    private static final String NOMBRE = "NominaThread";
    private Nomina nomina;
    private ContextoTransaccionalTipo contextoSolicitud;

    private String idExpediente = null;
    private EnvioTipo envio;
    private EntityManager em;
    private InformacionExternaDefinitiDao informacionExternaDefinitiDao;

    public NominaAsincronicaThread() {
    }

    /**
     * Constructor con parametros
     *
     * @param idExpediente
     * @param envio
     * @param contextoSolicitud
     * @param entityManager
     * @param error
     */
    public NominaAsincronicaThread(String idExpediente, EnvioTipo envio,
            ContextoTransaccionalTipo contextoSolicitud, EntityManager entityManager, boolean error) {
        this.contextoSolicitud = contextoSolicitud;
        this.idExpediente = idExpediente;
        this.envio = envio;
        this.em = entityManager;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(Thread.currentThread().getName().replace("pool", NOMBRE));
        synchronized (SYNCHRONIZED_OBJECT) {
            ejecutarPrograma();
        }
    }

    /**
     * Metodo de ejecucion principal
     */
    @SuppressWarnings("SleepWhileInLoop")
    public void ejecutarPrograma() {
        if (isDaemonNominaPool()) {
            LOG.error("#ERROR.NOMINA.DEMONIO# No se puede ejecutar porque existe un proceso bloqueando la ejecución.");
            try {
                Thread.currentThread().interrupt();
            } catch (Exception se) {
                //LOG.error("Exception " + Thread.currentThread().getName(), se);
                se.printStackTrace();
                IntegracionBPM.servicioIntegracionRecibirNomina("0", "ERROR EXCEPTION", "Error procesando la nomina", contextoSolicitud);
                return;
            }
        }
        while (NominaParentThread.isOcupado()) {
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {
                LOG.error("Exception - " + Thread.currentThread().getName(), ex);
            }
        }

        NominaParentThread.ocuparHilo();
        UserTransaction transaction = null;
        ExecutorService executorService = null;
        boolean lastThreadRun = false;
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        try {
            try {
                transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
            } catch (Exception ex) {
                throw new Exception(ex);
            }
            // WRojas 20.08.2021
            registroTrazabilidad(transaction, idExpediente, "Fase 1:Cargue de cabecera", "Valida Expediente/Radicado", "Parámetros desde el BPM");
            if (StringUtils.isBlank(idExpediente) || envio == null || envio.getRadicacion() == null
                    || StringUtils.isBlank(envio.getRadicacion().getIdRadicadoSalida())) {
                registroTrazabilidad(transaction, idExpediente, "Fase 1:Cargue de cabecera", "Valida Expediente/Radicado", "Se reciben valores nulos");
                //invocarServicioIntegracion("0", "ERROR EXCEPCION NOMINA", "Los parametros de entrada: expediente y/o idRadicadoSalida no pueden ser nulos");
                throw new Exception("ERROR PREVIO CARGUE DE NOMINA: Los parametros de entrada: expediente y/o idRadicadoSalida no pueden ser nulos");
            }
            //WRojas 20.08.2021
            registroTrazabilidad(transaction, idExpediente, "Fase 1:Cargue de cabecera", "Valida Expediente/Radicado", "Proceso OK");
            registroTrazabilidad(transaction, idExpediente, "Fase 2:Cargue de cabecera", "Validación tabla: Información Externa Definitiva", "Se busca en la tabla temporal");
            String idRadicadoEnvio = envio.getRadicacion().getIdRadicadoSalida();
            informacionExternaDefinitiDao = new InformacionExternaDefinitiDao(this.em);
            InformacionExternaDefiniti informacionExternaDefinitiva = informacionExternaDefinitiDao.informacionExternaDefinitiPorRadicado(idRadicadoEnvio);

            if (informacionExternaDefinitiva == null) {
                registroTrazabilidad(transaction, idExpediente, "Fase 2:Cargue de cabecera", "Validación tabla: Información Externa Definitiva", "No hubo archivo temporal");
                //invocarServicioIntegracion("0", "ERROR EXCEPCION NOMINA", "El objeto 'Envio' no tiene archivos asociados");
                throw new Exception("ERROR PREVIO CARGUE DE NOMINA: El objeto 'Envio' no tiene archivos asociados");
            }
            //WRojas 20.08.2021
            registroTrazabilidad(transaction, idExpediente, "Fase 2:Cargue de cabecera", "Validación tabla: Información Externa Definitiva", "Proceso OK");
            registroTrazabilidad(transaction, idExpediente, "Fase 3:Cargue de cabecera", "Validando existencia Expediente", "Tabla - Expediente");
            EnvioInformacionExterna envioInformacionExterna = new EnvioInformacionExterna();
            envioInformacionExterna.setIdRadicadoEnvio(idRadicadoEnvio);
            List<Expediente> expedienteLiquidador;

            Query query = em.createNamedQuery("Expe.findByRadicado", Expediente.class);
            query.setMaxResults(1);
            query.setParameter("idExpediente", idExpediente);
            expedienteLiquidador = query.getResultList();

            if (expedienteLiquidador == null || expedienteLiquidador.isEmpty()) {
                registroTrazabilidad(transaction, idExpediente, "Fase 3:Cargue de cabecera", "Validando existencia Expediente", "Expediente no existe");
                //invocarServicioIntegracion("0", "ERROR EXCEPCION NOMINA", "No se encuentra el expediente con el id: " + idExpediente);
                throw new Exception("ERROR PREVIO CARGUE DE NOMINA: No se encuentra el expediente con el id: " + idExpediente);
            }
            Expediente expediente = expedienteLiquidador.get(0);
            registroTrazabilidad(transaction, idExpediente, "Fase 3:Cargue de cabecera", "Validando existencia Expediente", "Proceso OK");            
            //Expediente expediente = Expediente.class.cast(expedienteLiquidador.get(0));
            
            Date fechaTemporal = new Date();
            Timestamp tiempoTemporal = new Timestamp(fechaTemporal.getTime());

            nomina = new Nomina();
            nomina.setFechacreacion(DateUtil.currentCalendar());
            nomina.setIdexpediente(expediente);
            if (expedienteLiquidador.get(0).getNominaLiquidadorList() == null) {
                expedienteLiquidador.get(0).setNominaLiquidadorList(new ArrayList<Nomina>());
            }

            expedienteLiquidador.get(0).getNominaLiquidadorList().add(nomina);
            Sheet sheet = null;
            
            try {
                //toca dejarlo como error para que pueda aparecer en el log de produccion 
                LOG.error("ProcesoNómina: inicia proceso -> informacionExternaDefinitiva:"
                        + informacionExternaDefinitiva.getId() + " - " + informacionExternaDefinitiva.getValNombreArchivo());
                registroTrazabilidad(transaction, idExpediente, "Fase 4:Cargue de cabecera", "Inicia creación del objeto <nomina>", "Almacenando datos");
                nomina.setArchivo(informacionExternaDefinitiva.getValContenidoArchivo());
                nomina.setNombreArchivo("TEMP-" + Long.toString(tiempoTemporal.getTime()) + "-" + informacionExternaDefinitiva.getId().toString() + "-" + informacionExternaDefinitiva.getValNombreArchivo());
                nomina.setIdinformacionexternadefinitiva(BigInteger.valueOf(informacionExternaDefinitiva.getId()));
                nomina.setIdUsuarioCreacion(contextoSolicitud.getIdUsuario());

                try ( // DETERMINAR ULTIMA FILA CON DATOS VERIFICANDO CELDAS QUE SON OBLIGATORIAS
                    InputStream inputStream = new ByteArrayInputStream(informacionExternaDefinitiva.getValContenidoArchivo())) {
                    Workbook wb1 = WorkbookFactory.create(inputStream);
                    sheet = wb1.getSheetAt(0);
                    inputStream.close();
                }
                int numeroUltimaFila1 = 5;
                int ultimaFilaSegunPoi = sheet.getLastRowNum();
                String celdaObligatoriaI;
                String celdaObligatoriaJ;

                do {
                    numeroUltimaFila1++;
                    Row fila = sheet.getRow(numeroUltimaFila1);
                    if (fila != null) {
                        celdaObligatoriaI = convertToString(fila.getCell(8, Row.CREATE_NULL_AS_BLANK)); // Tipo de documento actual del cotizante
                        celdaObligatoriaJ = convertToString(fila.getCell(9, Row.CREATE_NULL_AS_BLANK)); // Número de documento actual del cotizante
                    } else {
                        celdaObligatoriaI = null;
                        celdaObligatoriaJ = null;
                    }
                } while (!StringUtils.isBlank(celdaObligatoriaI) && !StringUtils.isBlank(celdaObligatoriaJ)
                        && numeroUltimaFila1 < ultimaFilaSegunPoi);

                nomina.setNumEmpleado((numeroUltimaFila1 - 5));

            } catch (Exception e) {
                throw new Exception(e);
            }
            try {
                registroTrazabilidad(transaction, idExpediente, "Fase 5: Almacenando el tipo de Acto", "En proceso creación del objeto <nomina>", "Seteando datos");
                int filasCabecera = 5;
                Row fila = sheet.getRow(3);
                nomina.setTipoActo(convertToString(fila.getCell(9, Row.CREATE_NULL_AS_BLANK)).toLowerCase());
                // WROJAS. Ajuste para gestionar las tildes para el caso de <liquidación> y se implementa de una vez para 
                // la fase de <ampliación>
                if ("liquida".equals(nomina.getTipoActo().substring(0,7))){
                    nomina.setTipoActo("liquidacion");
                }
                if ("ampliac".equals(nomina.getTipoActo().substring(0,7))){
                    nomina.setTipoActo("ampliacion");
                }                
                // Se incluye la celda K4 que contiene la fecha para liquidación de sanciones
                try {
                    if (HSSFDateUtil.isCellDateFormatted(fila.getCell(10, Row.CREATE_NULL_AS_BLANK))) {
                        if (!StringUtils.isBlank(convertToString(fila.getCell(10, Row.CREATE_NULL_AS_BLANK)))) {
                            formatoFecha.format(fila.getCell(10, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                            nomina.setFechaSancion(formatoFecha.getCalendar().getTime());
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Error NOMINA: Fecha Celda <k4> --> " + e);
                }
                // WROJAS. Inclusión de la celda L4 para <Fecha de notificación del RDOC>
                try {
                    if (HSSFDateUtil.isCellDateFormatted(fila.getCell(11, Row.CREATE_NULL_AS_BLANK))) {
                        if (!StringUtils.isBlank(convertToString(fila.getCell(11, Row.CREATE_NULL_AS_BLANK)))) {
                            formatoFecha.format(fila.getCell(11, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                            nomina.setFechaNoticRdoc(formatoFecha.getCalendar().getTime());
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Error NOMINA: Fecha Celda <L4> --> " + e);
                }
                // WROJAS. Inclusión de la celda M4 para <Fecha límite para responder el RDOC>
                try {
                    if (HSSFDateUtil.isCellDateFormatted(fila.getCell(12, Row.CREATE_NULL_AS_BLANK))) {
                        if (!StringUtils.isBlank(convertToString(fila.getCell(12, Row.CREATE_NULL_AS_BLANK)))) {
                            formatoFecha.format(fila.getCell(12, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                            nomina.setFechaLimRespRdoc(formatoFecha.getCalendar().getTime());
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Error NOMINA: Fecha Celda <M4> --> " + e);
                }
                // WROJAS. Inclusión de la celda N4 para <pago sanción Omiso>
                String valorString = convertToString(fila.getCell(13, Row.CREATE_NULL_AS_BLANK));
                if ("".equals(valorString)){
                    valorString="0";
                }
                nomina.setPagoSancionOmiso(new Integer(valorString));
                // WROJAS. Inclusión de la celda O4 para <pago sanción mora>
                valorString = convertToString(fila.getCell(14, Row.CREATE_NULL_AS_BLANK));
                if ("".equals(valorString)){
                    valorString="0";
                }
                nomina.setPagoSancionMora(new Integer(valorString));                
                // WROJAS. Inclusión de la celda P4 para <pago sanción Inexacto>
                valorString = convertToString(fila.getCell(15, Row.CREATE_NULL_AS_BLANK));
                if ("".equals(valorString)){
                    valorString="0";
                }
                nomina.setPagoSancionInexac(new Integer(valorString));                
                
                //*********************DATOS APORTANTE*****************************//
                //fila = sheet.getRow(3);
                TipoIdentificacion tipoIdentificacion = null;
                cargarListaTiposDocumentos(em);
                tipoIdentificacion = getTipoDOcumentoBySigla(convertToString(fila.getCell(1, Row.CREATE_NULL_AS_BLANK)));
                if (tipoIdentificacion == null) {
                    throw new Exception("El tipoIdentificacion del aportante es incorrecto.");
                }
                AportanteLIQ aportante = null;
                query = em.createNamedQuery("Aportante.findByIdentificacionAndIdTipoIdentificacion");
                query.setMaxResults(1);
                query.setParameter("numeroIdentificacion", convertToString(fila.getCell(2, Row.CREATE_NULL_AS_BLANK)));
                query.setParameter("idTipoIdentificacion", tipoIdentificacion.getId());

                try {
                    aportante = (AportanteLIQ) query.getSingleResult();
                } catch (Exception ex) {
                    aportante = null;
                }
                if (aportante == null) {
                    aportante = nuevoAportante(fila, tipoIdentificacion);
                }
                aportante.setNaturalezaJuridica(convertToString(fila.getCell(4, Row.CREATE_NULL_AS_BLANK))); // Celda E4
                aportante.setTipoAportante(convertToString(fila.getCell(5, Row.CREATE_NULL_AS_BLANK))); // Celda F4
                aportante.setAportaEsapYMen(convertToString(fila.getCell(6, Row.CREATE_NULL_AS_BLANK)).toLowerCase()); // Celda G4
                aportante.setSujetoPasivoImpuestoCree(convertToString(fila.getCell(7, Row.CREATE_NULL_AS_BLANK)).toUpperCase()); // Celda H4
                
                try {
                    transaction.begin();
                    if (aportante.getId() == null || aportante.getId() <= 0L) {
                        em.persist(aportante);
                    } else {
                        em.merge(aportante);
                    }
                    transaction.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new Exception(ex);
                }
                nomina.setNit(new BigInteger(aportante.getNumeroIdentificacion()));
                //int numeroUltimaFila = nomina.getNumEmpleado(); // Instrucción nueva
                // Se almacena el objeto <nomina>
                registroTrazabilidad(transaction, idExpediente, "Fase 6: Iniciando persistencia", "En proceso creación del objeto <nomina>", "Iniciando...");
                try {
                    transaction.begin();
                    em.persist(nomina);
                    transaction.commit();
                    registroTrazabilidad(transaction, idExpediente, "Fase 7: Termina persistencia", "Almacenamiento del objeto <nomina>", "Finalizado");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //IntegracionBPM.servicioIntegracionRecibirNomina("0", "ERROR EXCEPTION NOMINA", "Error procesando la nomina", contextoSolicitud);
                    throw new Exception(ex);
                }                
                // Verificación de la parte dinámica
                int cantidadColumnas = 69;
                int cantidadColumnasDinamicas = -1;
                String contenidoCelda = "";
                do {
                    cantidadColumnasDinamicas++;
                    contenidoCelda = convertToString(fila.getCell(cantidadColumnas + cantidadColumnasDinamicas, Row.CREATE_NULL_AS_BLANK));
                } while (!contenidoCelda.isEmpty());

                //*********************FIN DETERMINAR ULTIMA FILA VALIDA*****************************//
                //FORMATO A UTILIZAR EN LAS FECHAS SUMINISTRADAS POR EL EXCEL
                List<String> lstDocumentos = new ArrayList<>();
                String numeroDoc = "";
                String tipoDoc = "";
                int numeroUltimaFila = nomina.getNumEmpleado() + 5; // Instrucción nueva
                for (int j = filasCabecera + 1; j <= numeroUltimaFila; j++) {
                    fila = sheet.getRow(j);
                    numeroDoc = convertToString(fila.getCell(9, Row.CREATE_NULL_AS_BLANK));  // Columna J7 hacia abajo
                    tipoDoc = convertToString(fila.getCell(10, Row.CREATE_NULL_AS_BLANK));  // Columna K7 hacia abajo
                    String cadena = numeroDoc + ";" + tipoDoc;
                    if (lstDocumentos.isEmpty() || !lstDocumentos.contains(cadena)) {
                        lstDocumentos.add(cadena);
                    }
                }
                int sizeLstDoc = lstDocumentos.size();
                NominaMultiThread.setParameters(sheet, aportante, cantidadColumnasDinamicas, contextoSolicitud, nomina, sizeLstDoc, Thread.activeCount());
                executorService = Executors.newFixedThreadPool(Integer.parseInt(PropsReader.getKeyParam("parametroPoolSize")));
                for (String auxiliar : lstDocumentos) {
                    List<Row> lstFilas = new ArrayList<>();
                    for (int j = filasCabecera + 1; j <= numeroUltimaFila; j++) {
                        fila = sheet.getRow(j);
                        numeroDoc = convertToString(fila.getCell(9, Row.CREATE_NULL_AS_BLANK));
                        tipoDoc = convertToString(fila.getCell(10, Row.CREATE_NULL_AS_BLANK));
                        String cadena = numeroDoc + ";" + tipoDoc;
                        if (auxiliar.equals(cadena) && !cadena.equals(";")) {
                            lstFilas.add(fila);
                        }
                    }
                    if (lstFilas.size() > 0) {
                        NominaMultiThread multiThread = new NominaMultiThread(lstFilas);
                        multiThread.setPriority(Thread.MAX_PRIORITY);
                        executorService.submit(multiThread);
                    }
                }
                NominaLastThread nlt = new NominaLastThread(nomina.getId().toString(), "1", "Correcto", contextoSolicitud);
                nlt.setPriority(Thread.MIN_PRIORITY);
                executorService.submit(nlt);
                lastThreadRun = true;
            } catch (Exception e) {
                LOG.error("Error NOMINA: obteniendo NIT aportante o Fecha sanción --> " + e);
                 if (executorService != null) { // El pool de hilos ya se ha iniciado
                    if (lastThreadRun) { // el lastThread libera al pool de hilos
                        NominaParentThread.unError();
                    } else { // hay que deterner inmediatamente el pool de hilos y liberarlo
                        executorService.shutdownNow();
                        executorService = null;
                        NominaParentThread.liberarHilo();
                    }
                } else {
                    // en el caso que se presente un error y no se haya iniciado 
                    // el pool de hilos hay que liberar el pool de hilos para que se 
                    // puedan ejecutar los demas
                    NominaParentThread.liberarHilo();
                }
                //throw new Exception(e);
            } finally {
                // LOG.info("Asincrona.em.getFlushMode(): " + em.getFlushMode());
            }
        } catch (Exception e) {
            LOG.error("ERROR EXCEPTION NOMINA - Cabecera línea 368: " + e.getMessage());
            e.printStackTrace();
            try {
                IntegracionBPM.servicioIntegracionRecibirNomina(nomina.getId().toString(), "ERROR EXCEPTION", "Error procesando la nomina", contextoSolicitud);
                if (executorService != null) { // El pool de hilos ya se ha iniciado
                    if (lastThreadRun) { // el lastThread libera al pool de hilos
                        NominaParentThread.unError();
                    } else { // hay que deterner inmediatamente el pool de hilos y liberarlo
                        executorService.shutdownNow();
                        executorService = null;
                        NominaParentThread.liberarHilo();
                    }
                } else {
                    // en el caso que se presente un error y no se haya iniciado 
                    // el pool de hilos hay que liberar el pool de hilos para que se 
                    // puedan ejecutar los demas
                    NominaParentThread.liberarHilo();
                }
            } catch (Exception ee) {
                if (executorService != null) { // El pool de hilos ya se ha iniciado
                    if (lastThreadRun) { // el lastThread libera al pool de hilos
                        NominaParentThread.unError();
                    } else { // hay que deterner inmediatamente el pool de hilos y liberarlo
                        executorService.shutdownNow();
                        executorService = null;
                        NominaParentThread.liberarHilo();
                    }
                } else {
                    // en el caso que se presente un error y no se haya iniciado 
                    // el pool de hilos hay que liberar el pool de hilos para que se 
                    // puedan ejecutar los demas
                    NominaParentThread.liberarHilo();
                }
            }

            

        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }
    }

    /**
     * Metodo que retorna un aportante nuevo
     *
     * @param fila
     * @param tipoIdentificacion
     * @return
     */
    private AportanteLIQ nuevoAportante(Row fila, TipoIdentificacion tipoIdentificacion) throws Exception {

        AportanteLIQ aportante = new AportanteLIQ();
        aportante.setNumeroIdentificacion(convertToString(fila.getCell(2, Row.CREATE_NULL_AS_BLANK)));

        aportante.setTipoIdentificacion(tipoIdentificacion);

        aportante.setPrimerNombre(convertToString(fila.getCell(3, Row.CREATE_NULL_AS_BLANK)));
        aportante.setIdUsuarioCreacion(contextoSolicitud.getIdUsuario());
        return aportante;
    }

    private void registroTrazabilidad(UserTransaction transaction, String idExpediente, String nomFase, String nomDatos, String obs) {
        java.text.SimpleDateFormat formatoFecha = new java.text.SimpleDateFormat("dd/MM/yyyy");
        //Date fechaHoy = new Date();
        long miliseconds = System.currentTimeMillis();
        Date date = new Date(miliseconds);
        
        
        String sqlId = "SELECT trazabilidad_seq_id.NEXTVAL FROM DUAL";
        Query queryId = em.createNativeQuery(sqlId);
        Object idObj = queryId.getSingleResult();
        //obj.setFechaCreacion(Calendar.getInstance());
        Trazabilidad trazabilidad = new Trazabilidad();
        trazabilidad.setId((BigDecimal) idObj);
        trazabilidad.setIdExpediente(idExpediente);
        trazabilidad.setServicio("OpCrearNomina");
        trazabilidad.setFechaHora(date);
        //trazabilidad.setFechaHora(DateUtil.currentCalendar());
        trazabilidad.setFase(nomFase);
        trazabilidad.setDatos(nomDatos);
        trazabilidad.setObservacion(obs);
        
        try {
            transaction.begin();
            em.persist(trazabilidad);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            //IntegracionBPM.servicioIntegracionRecibirNomina("0", "ERROR EXCEPTION NOMINA", "Error procesando la nomina", contextoSolicitud);
            //throw new Exception(ex);
        }
    }

}
