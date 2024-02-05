package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread;

import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.AportanteLIQ;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.AportesIndependiente;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.ConceptoContable;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.ConceptoContableDetalle;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.Nomina;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.NominaDetalle;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.TransactionManagement;
import javax.naming.InitialContext;
import javax.naming.NamingException;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.LoggerFactory;

/**
 * @since 03/Ago/2017
 * @author UT_TECNOLOGI
 */
@TransactionManagement
public class NominaMultiThread extends NominaParentThread {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NominaMultiThread.class);
    private static int cantidadColumnasDinamicas;
    private static Sheet sheet;
    private static AportanteLIQ aportante;

    private UserTransaction transaction;
    private List<Row> lstFilas;

    /**
     * Constructor por defecto
     */
    public NominaMultiThread() {
    }

    /**
     * Constructor con parametros
     *
     * @param lstFilas
     */
    public NominaMultiThread(List<Row> lstFilas) {
        this.lstFilas = lstFilas;
    }

    /**
     * metodo encargado de setear los valores de los atributos estaticos de la
     * clase
     *
     * @param sheet
     * @param aportante
     * @param cantidadColumnasDinamicas
     * @param contextoSolicitud
     * @param nomina
     * @param totalThread
     * @param iniThread
     */
    public static void setParameters(Sheet sheet, AportanteLIQ aportante,
            int cantidadColumnasDinamicas, ContextoTransaccionalTipo contextoSolicitud,
            Nomina nomina, Integer totalThread, Integer iniThread) {
        NominaMultiThread.sheet = sheet;
        NominaMultiThread.aportante = aportante;
        NominaMultiThread.cantidadColumnasDinamicas = cantidadColumnasDinamicas;
        NominaParentThread.setParameters(contextoSolicitud, nomina, totalThread, iniThread);
    }

    /**
     * Metodo principal de ejecucion de los hilos
     */
    @Override
    public void run() {
        setNewName();
        stopByError();
        asignarEntityManager();
        ejecutarThread();
        endingThread();
        em.clear();
    }

    /**
     * Metodo encargado de la ejecucion del proceso principal del hilo
     */
    void ejecutarThread() {

        try {
            transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");

        } catch (NamingException ex) {
            LOG.error("ERROR NOMINA. Error de transaccion. Ex: " + ex);
            ex.printStackTrace();
            stopByError();
        }
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        int columna = 0;
        try {
            for (Row tmp : lstFilas) {
                NominaDetalle nominaDetalle = new NominaDetalle();
                nominaDetalle.setIdaportante(aportante);
                columna = 1;
                nominaDetalle.setTipoCotizante(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 2;
                nominaDetalle.setSubTipoCotizante(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 3;
                nominaDetalle.setCondEspEmp(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 4;
                nominaDetalle.setCondEspTrab(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 5;
                nominaDetalle.setExtranjero_no_obligado_a_cotizar_pension(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 6;
                nominaDetalle.setColombiano_en_el_exterior(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 7;
                nominaDetalle.setActividad_alto_riesgo_pension(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 8;
                nominaDetalle.setTipoNumeroIdentificacionActual(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 9;
                nominaDetalle.setNumeroIdentificacionActual(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 10; // En esta columna se guarda el tipo de cotizante del acto anterior.
                nominaDetalle.setTipoNumeroIdentificacionRealizoAportes(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 11; // En esta columna se guarda el subtipo de cotizante del acto anterior.
                nominaDetalle.setNumeroIdentificacionRealizoAportes(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 12;
                nominaDetalle.setNombreCotizante(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 13;
                nominaDetalle.setCargoTrabajador(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 14;
                nominaDetalle.setAno(new BigInteger(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 15;
                nominaDetalle.setMes(new BigInteger(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 16;
                nominaDetalle.setSalarioIntegral(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 17;
                nominaDetalle.setNovedadIncapacidad(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 18;
                nominaDetalle.setNovedadLicenciaMaternidadPaternidad(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 19;
                nominaDetalle.setNovedadPermisoLicenciaRemunerada(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 20;
                nominaDetalle.setNovedadSuspension(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 21;
                nominaDetalle.setNovedadVacaciones(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 22;
                nominaDetalle.setDiasTrabajadosMes(new Integer(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 23;
                nominaDetalle.setDiasIncapacidadesMes(new Integer(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 24;
                nominaDetalle.setDiasLicenciaMaternidadPaternidadMes(new Integer(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 25;
                nominaDetalle.setDiasLicenciaRemuneradasMes(new Integer(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 26;
                nominaDetalle.setDiasSuspensionMes(new Integer(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 27;
                nominaDetalle.setDiasVacacionesMes(new Integer(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 28;
                nominaDetalle.setDiasHuelgaLegalMes(new Integer(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 29;
                nominaDetalle.setTotalDiasReportadosMes(new Integer(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 30;
                nominaDetalle.setIng(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 31;
                if (HSSFDateUtil.isCellDateFormatted(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK))) {
                    if (!StringUtils.isBlank(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))) {
                        formatoFecha.format(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                        nominaDetalle.setIngFecha(formatoFecha.getCalendar().getTime());
                    }
                }
                columna = 32;
                nominaDetalle.setRet(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 33;
                if (HSSFDateUtil.isCellDateFormatted(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK))) {
                    if (!StringUtils.isBlank(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))) {
                        formatoFecha.format(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                        nominaDetalle.setRetFecha(formatoFecha.getCalendar().getTime());
                    }
                }
                columna = 34;
                if (HSSFDateUtil.isCellDateFormatted(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK))) {
                    if (!StringUtils.isBlank(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))) {
                        formatoFecha.format(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                        nominaDetalle.setFechaInicioVacaciones(formatoFecha.getCalendar().getTime());
                    }
                }
                columna = 35;
                if (HSSFDateUtil.isCellDateFormatted(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK))) {
                    if (!StringUtils.isBlank(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))) {
                        formatoFecha.format(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                        nominaDetalle.setFechaTerminaVacaciones(formatoFecha.getCalendar().getTime());
                    }
                }
                columna = 36;
                if (HSSFDateUtil.isCellDateFormatted(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK))) {
                    if (!StringUtils.isBlank(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))) {
                        formatoFecha.format(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                        nominaDetalle.setFechaInicioLicenciaRemu(formatoFecha.getCalendar().getTime());
                    }
                }
                columna = 37;
                if (HSSFDateUtil.isCellDateFormatted(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK))) {
                    if (!StringUtils.isBlank(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))) {
                        formatoFecha.format(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                        nominaDetalle.setFechaInicioSuspension(formatoFecha.getCalendar().getTime());
                    }
                }
                columna = 38;
                if (HSSFDateUtil.isCellDateFormatted(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK))) {
                    if (!StringUtils.isBlank(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))) {
                        formatoFecha.format(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                        nominaDetalle.setFechaInicioHuelga(formatoFecha.getCalendar().getTime());
                    }
                }
                columna = 39;
                nominaDetalle.setCalculoActuarial(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 40;
                nominaDetalle.setValorCalculoActuarial(new BigDecimal(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 41;
                //nominaDetalle.setSalBasico(new BigInteger(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                // El dato de esta columna fue modificado para incluir la fecha final de pago para cálculo actuarial
                // Se creo en la tabla una nueva columna FECH_FIN_PAG_CAL_ACT
                if (HSSFDateUtil.isCellDateFormatted(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK))) {
                    if (!StringUtils.isBlank(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))) {
                        formatoFecha.format(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK).getDateCellValue());
                        nominaDetalle.setFechaFinPagoCalAct(formatoFecha.getCalendar().getTime());
                    }
                }
                columna = 42;
                nominaDetalle.setIbcConcurrenciaIngresosOtrosAportantes(new BigDecimal(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 43;
                nominaDetalle.setIbcPenConIngrOtrApo(new BigDecimal(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 44;
                nominaDetalle.setIbcArlConIngrOtrApo(new BigDecimal(convertToZero(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                //nominaDetalle.setNomina(NOMINA);
                //nominaDetalle.setIdUsuarioCreacion(CONTEXTO_SOLICITUD.getIdUsuario());
                //COLUMNAS NUEVAS EN TOTAL 9 PARA PROCESAR PAGOS POR FUERA DE PILA MANUALES PLANILLA TIPO M 
                columna = 55;
                nominaDetalle.setCargueManualPilaSalud(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 56;
                nominaDetalle.setCargueManualPilaPension(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 57;
                nominaDetalle.setCargueManualPilaFspSolid(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 58;
                nominaDetalle.setCargueManualPilaFspSubsis(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 59;
                nominaDetalle.setCargueManualPilaAltoRiPe(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 60;
                nominaDetalle.setCargueManualPilaArl(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 61;
                nominaDetalle.setCargueManualPilaCcf(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 62;
                nominaDetalle.setCargueManualPilaSena(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 63;
                nominaDetalle.setCargueManualPilaIcbf(new BigDecimal(convertToZero1(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)))));
                columna = 64;
                nominaDetalle.setOmisionSalud(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 65;
                nominaDetalle.setOmisionPension(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 66;
                nominaDetalle.setOmisionArl(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
                columna = 67;
                nominaDetalle.setOmisionCcf(convertToString(tmp.getCell(columna, Row.CREATE_NULL_AS_BLANK)));

                nominaDetalle.setNomina(NOMINA);
                nominaDetalle.setIdUsuarioCreacion(CONTEXTO_SOLICITUD.getIdUsuario());

                try {
                    stopByError(); // parada por ERROR
                    transaction.begin();
                    em.persist(nominaDetalle);
                    em.flush();
                    transaction.commit();
                    //LOG.info("Set NOMINADETALLE: " + nominaDetalle.getId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LOG.error("EXCEPCION Columna: " + (columna + 1) + " PERSISTIR NOMINADETALLE Ex: " + ex);
                    setError(true);
                    throw new Exception(ex);
                } finally {
                    //LOG.info("nominaDetalle: " + nominaDetalle  + " - Estado: " + transaction.getStatus());
                }
                //*********************FIN NOMINA DETALLE *****************************//
                nuevoAportanteIndependiente(tmp, transaction, nominaDetalle);

                //********************* INICIO CONCEPTO CONTABLES **************//
                int contConceptosContables = 0;
                int valorTotalConcepto = 0;
                String tipoPagoUgpp;
                String contenidoCeldaLocal = null;

                if (cantidadColumnasDinamicas >= 1) {
                    int cantidadColumnas = 69;
                    //RECORRE DE IZQIERDA A DERECHA LAS COLUMNAS DINAMICAS
                    while (contConceptosContables < cantidadColumnasDinamicas) {
                        try {
                            //TOMA LA PRIMERA COLUMNA DINAMICA DE LA FILA 6
                            contenidoCeldaLocal = convertToString(tmp.getCell(cantidadColumnas + contConceptosContables, Row.CREATE_NULL_AS_BLANK));
                            contenidoCeldaLocal = contenidoCeldaLocal.replace(',', '.');
                            if (!contenidoCeldaLocal.equals("")) {
                                ConceptoContable conceptoContable = new ConceptoContable();
                                Row filaConceptoContableColumnaDinamica = sheet.getRow(0);
                                conceptoContable.setJustificacionCambioUgpp(convertToString(filaConceptoContableColumnaDinamica.getCell(cantidadColumnas + contConceptosContables, Row.CREATE_NULL_AS_BLANK)));
                                filaConceptoContableColumnaDinamica = sheet.getRow(1);
                                tipoPagoUgpp = convertToString(filaConceptoContableColumnaDinamica.getCell(cantidadColumnas + contConceptosContables, Row.CREATE_NULL_AS_BLANK));
                                conceptoContable.setTipoPagoUgpp(tipoPagoUgpp);
                                if (tipoPagoUgpp.equals("TP NO SALARIAL") && Integer.parseInt(contenidoCeldaLocal) > 0) {
                                    try {
                                        valorTotalConcepto += Integer.parseInt(contenidoCeldaLocal);

                                    } catch (NumberFormatException ex) {
                                        LOG.error("EXCEPCION Columna: " + (columna + 1) + " Valor total concepto. Ex: " + ex);
                                        throw new Exception(ex);
                                    }
                                }
                                filaConceptoContableColumnaDinamica = sheet.getRow(2);
                                conceptoContable.setNombreConceptoUgpp(convertToString(filaConceptoContableColumnaDinamica.getCell(cantidadColumnas + contConceptosContables, Row.CREATE_NULL_AS_BLANK)));

                                filaConceptoContableColumnaDinamica = sheet.getRow(3);
                                conceptoContable.setSubsistemas(convertToString(filaConceptoContableColumnaDinamica.getCell(cantidadColumnas + contConceptosContables, Row.CREATE_NULL_AS_BLANK)));
                                String[] subsistemas = convertToString(filaConceptoContableColumnaDinamica.getCell(cantidadColumnas + contConceptosContables, Row.CREATE_NULL_AS_BLANK)).split(";");
                                if (subsistemas.length > 0 && !contenidoCeldaLocal.equals("0")) {
                                    for (String valor : subsistemas) {
                                        nuevoConceptoContableDetalle(valor, nominaDetalle,
                                                contenidoCeldaLocal, tipoPagoUgpp, transaction);
                                    }
                                }
                                filaConceptoContableColumnaDinamica = sheet.getRow(4);
                                conceptoContable.setCuentaContable(convertToString(filaConceptoContableColumnaDinamica.getCell(cantidadColumnas + contConceptosContables, Row.CREATE_NULL_AS_BLANK)));

                                filaConceptoContableColumnaDinamica = sheet.getRow(5);
                                conceptoContable.setNombreConceptoAportante(convertToString(filaConceptoContableColumnaDinamica.getCell(cantidadColumnas + contConceptosContables, Row.CREATE_NULL_AS_BLANK)));
                                conceptoContable.setValor(new BigDecimal(convertToZero(contenidoCeldaLocal)));
                                conceptoContable.setIdnominadetalle(nominaDetalle);
                                conceptoContable.setIdUsuarioCreacion(CONTEXTO_SOLICITUD.getIdUsuario());

                                try {
                                    stopByError(); // parada por ERROR
                                    transaction.begin();
                                    em.persist(conceptoContable);
                                    em.flush();
                                    transaction.commit();
                                    //LOG.info("Set CONCEPTOCONTABLE: " + conceptoContable.getId());
                                } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
                                    LOG.error("EXCEPCION Columna: " + (columna + 1) + " PERSISTIR CONCEPTOCONTABLE Ex: " + ex);
                                    throw new Exception(ex);
                                }

                            }
                        } catch (Exception e) {
                            LOG.error("Error NOMINA Columna: " + (columna + 1) + " contConceptosContables: " + (contConceptosContables + 1) + ". Error en registro de conceptos contables. Ex: " + e);
                            throw new Exception(e);
                        }
                        contConceptosContables++;
                    }

                    if (contConceptosContables == cantidadColumnasDinamicas && valorTotalConcepto > 0) {
                        guardarConceptoContableDetalle(nominaDetalle, valorTotalConcepto, transaction);
                    }
                }
                //********************* FIN CONCEPTO CONTABLES ************************//
            } // AQUI TERMINA EL RECORRIDO DEL ARCHIVO FILA x FILA DEL EXCEL

            //Long dif = Calendar.getInstance().getTime().getTime() - start.getTime().getTime();
            //LOG.info("End process NominaMultiThread (" + (getCOUNT_THREAD() + 1)
            //        + "/" + TOTAL_THREAD + ") " + Thread.currentThread().getName()
            //        + " [" + dif + " ms]");
        } catch (Exception e) {
            LOG.error("EXCEPCION NOMINA Columna: " + (columna + 1) + ": Ex: " + e.getMessage());
            e.printStackTrace();
            setError(true);
        } finally {
            // LOG.info("em.getFlushMode(End): " + em.getFlushMode());            
        }
    }

    /**
     * Metodo encargado de crear un nuevo Aportante Independiente
     *
     * @param fila
     * @param tipoIdentificacion
     * @param transaction
     * @param nominaDetalle
     * @throws Exception
     */
    private void nuevoAportanteIndependiente(Row fila, UserTransaction transaction, NominaDetalle nominaDetalle) throws Exception {
        //********************* DATOS APORTES INDEPENDIENTES **************//
        AportesIndependiente aportesIndependiente = new AportesIndependiente();
        int columna = 45; //OBSERVACIONES APORTANTE
        aportesIndependiente.setObservacionesAportante(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 46; //OBSERVACIONES UGPP
        aportesIndependiente.setObservacionesUgpp(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 47; //OBSERVACIONES APORTANTE SALUD
        aportesIndependiente.setObservacionesSalud(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 48; //OBSERVACIONES APORTANTE PENSION
        aportesIndependiente.setObservacionesPension(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 49; //OBSERVACIONES APORTANTE FSP
        aportesIndependiente.setObservacionesFsp(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 50; //OBSERVACIONES APORTANTE PENSION
        aportesIndependiente.setObservacionesAltoRiesgo(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 51;//OBSERVACIONES APORTANTE ARL
        aportesIndependiente.setObservacionesArl(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 52;//OBSERVACIONES APORTANTE CCF
        aportesIndependiente.setObservacionesCcf(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 53;//OBSERVACIONES APORTANTE SENA
        aportesIndependiente.setObservacionesSena(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        columna = 54;//OBSERVACIONES APORTANTE ICBF
        aportesIndependiente.setObservacionesIcbf(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        aportesIndependiente.setIdnominadetalle(nominaDetalle);
        aportesIndependiente.setIdUsuarioCreacion(CONTEXTO_SOLICITUD.getIdUsuario());

        try {
            stopByError(); // parada por ERROR
            transaction.begin();
            em.persist(aportesIndependiente);
            em.flush();
            transaction.commit();
            //LOG.info("Set APORTESINDEPENDIENTE: " + aportesIndependiente.getId());
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            LOG.error("EXCEPCION PERSISTIR APORTESINDEPENDIENTE: " + ex);
            throw new Exception(ex);
        }
        //********************* DATOS APORTES INDEPENDIENTES **************//
    }

    /**
     * Metodo encargado de crear un nuevo Concepto contable detalle
     *
     * @param valor
     * @param nominaDetalle
     * @param contenidoCelda
     * @param tipoPagoUgpp
     * @param transaction
     * @throws Exception
     */
    private void nuevoConceptoContableDetalle(String valor, NominaDetalle nominaDetalle,
            String contenidoCelda, String tipoPagoUgpp, UserTransaction transaction) throws Exception {

        ConceptoContableDetalle conceptoContableDetalle = new ConceptoContableDetalle();

        if (!valor.equals("")) {
            conceptoContableDetalle.setIdSubsistema(new BigDecimal(valor));
        } else {
            conceptoContableDetalle.setIdSubsistema(new BigDecimal(0));
        }
        conceptoContableDetalle.setIdnominadetalle(nominaDetalle);
        conceptoContableDetalle.setValor(new BigDecimal(convertToZero(contenidoCelda)));
        conceptoContableDetalle.setTipoPagoUgpp(tipoPagoUgpp);

        try {
            stopByError(); // parada por ERROR
            transaction.begin();
            em.persist(conceptoContableDetalle);
            em.flush();
            transaction.commit();
            //LOG.info("Set CONCEPTOCONTABLEDETALLE: " + conceptoContableDetalle.getId());
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            LOG.error("EXCEPCION PERSISTIR CONCEPTOCONTABLEDETALLE: " + ex);
            throw new Exception(ex);
        }
    }

    /**
     * Metodo encargado de guardar los detalles de los conceptos contables
     *
     * @param nominaDetalle
     * @param valorTotalConcepto
     * @param transaction
     * @throws Exception
     */
    private void guardarConceptoContableDetalle(NominaDetalle nominaDetalle, int valorTotalConcepto,
            UserTransaction transaction) throws Exception {
        ConceptoContableDetalle conceptoContableDetalle = new ConceptoContableDetalle();
        conceptoContableDetalle.setIdSubsistema(new BigDecimal("99"));
        conceptoContableDetalle.setIdnominadetalle(nominaDetalle);
        conceptoContableDetalle.setValor(new BigDecimal(convertToZero(Integer.toString(valorTotalConcepto))));
        conceptoContableDetalle.setTipoPagoUgpp("TP NO SALARIAL");

        try {
            stopByError(); // parada por ERROR
            transaction.begin();
            em.persist(conceptoContableDetalle);
            em.flush();
            transaction.commit();
            //LOG.info("Set CONCEPTOCONTABLEDETALLE TOTAL TPNOSALARIAL: " + conceptoContableDetalle.getId());
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            LOG.error("EXCEPCION PERSISTIR CONCEPTOCONTABLEDETALLE TOTAL TPNOSALARIAL: " + ex);
            throw new Exception(ex);
        }
    }

    /**
     * Metodo encargado de encontrar y devolver el identificador del hilo
     * asignado
     *
     * @return
     */
    private int getIdThread() {
        String[] arrayName = Thread.currentThread().getName().split("-");
        String sId = arrayName[arrayName.length - 1];
        int id = (Integer.valueOf(sId) - 1);
        return id;
    }

    /**
     * Metodo encargado de asignar las EntityManager correspondiente para su
     * ejecucion
     */
    private void asignarEntityManager() {
        em = getEntityManager(getIdThread());
    }

}
