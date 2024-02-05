package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplprocesarnomina.thread;

import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.Nomina;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.TipoIdentificacion;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.PropsReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.slf4j.LoggerFactory;

/**
 * @since 02/Ago/2017
 * @author UT_TECNOLOGIC
 */
public abstract class NominaParentThread extends Thread implements java.io.Serializable {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NominaParentThread.class);
    protected static final Object SYNCHRONIZED_OBJECT = new Object();

    protected static boolean OCUPADO = false;
    protected static boolean ERROR = false;

    private static int POOL_SIZE = 0;

    private static final String NOMBRE = "NominaPool";
    private static Integer COUNT_THREAD = 0;
    protected static Integer TOTAL_THREAD;
    protected static Integer INI_THREAD;

    protected static Nomina NOMINA;
    protected static ContextoTransaccionalTipo CONTEXTO_SOLICITUD;

    protected static List<TipoIdentificacion> LST_TIPOS_DE_IDENTIFICACION;
    private static List<EntityManager> LIST_EM;

    protected EntityManager em, em1;
    

    /**
     * Constructor por defecto
     */
    public NominaParentThread() {
    }

    /**
     * metodo encargado de setear los valores de los atributos estaticos de la
     * clase
     *
     * @param contextoSolicitud
     * @param nomina
     * @param totalThread
     * @param iniThread
     */
    public static void setParameters(ContextoTransaccionalTipo contextoSolicitud,
            Nomina nomina, Integer totalThread, Integer iniThread) {
        NominaParentThread.TOTAL_THREAD = totalThread;
        NominaParentThread.INI_THREAD = iniThread;

        NominaParentThread.NOMINA = nomina;
        NominaParentThread.CONTEXTO_SOLICITUD = contextoSolicitud;
        NominaParentThread.POOL_SIZE = Integer.parseInt(PropsReader.getKeyParam("parametroPoolSize"));
    }

    /**
     * Metodo para llevar la cuenta de hilos terminados
     */
    public void endingThread() {
        synchronized (SYNCHRONIZED_OBJECT) {
            COUNT_THREAD++;
        }
    }

    /**
     * Metodo encargado de determinar cuando se puede ejecutar el ultimo hilo
     *
     * @return retorna una bandera indicando que ya se puede ejecutar y realiza
     * la notificacion a los otros hilos
     */
    public boolean isPermitido() {
        boolean result = false;
        if (COUNT_THREAD.equals(TOTAL_THREAD) || COUNT_THREAD > TOTAL_THREAD) {
            result = true;
        }
        return result;
    }

    /**
     * Metodo encargado de detener la tarea hasta que no se ejcuten todos los
     * hilos pendientes
     */
    public void iniciarTarea() {
        while (!isPermitido()) {
            if (INI_THREAD.equals(Thread.activeCount()) || INI_THREAD > Thread.activeCount()) {
                endingThread();
            }
        }
    }

    /**
     * Metodo encargado de convertir el contenido de una celda en texto
     *
     * @param cell
     * @return
     */
    public static String convertToString(Cell cell) {
        String valor = null;

        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    valor = cell.getBooleanCellValue() + "";
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    valor = String.format("%.0f", cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    valor = "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    valor = "";
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    valor = String.format("%.0f", cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    valor = cell.getStringCellValue();
                    break;
                default:
                    valor = cell.getStringCellValue();
                    break;
            }
        }

        return valor;
    }

    /**
     * Metodo encargado de cargar la lista de tipo documento en cache
     *
     * @param em
     */
    public static void cargarListaTiposDocumentos(EntityManager em) {
        if (em != null && LST_TIPOS_DE_IDENTIFICACION == null) 
        {
            try {
                if (!isError() && LST_TIPOS_DE_IDENTIFICACION == null) {
                    Query query = em.createQuery("SELECT t FROM TipoIdentificacion t");
                    LST_TIPOS_DE_IDENTIFICACION = query.getResultList();
                }
            } catch (Exception ex) {
                LOG.error("Error - cargarListaTiposDocumentos", ex);
            }
        }
    }

    

    
    
    /**
     * metodo que retorna el tipo documento correspondiente a la sigla ingresada
     * de la lista cache
     *
     * @param sigla
     * @return
     */
    public static TipoIdentificacion getTipoDOcumentoBySigla(String sigla) {
        
        TipoIdentificacion result = null;
        if (LST_TIPOS_DE_IDENTIFICACION != null && !LST_TIPOS_DE_IDENTIFICACION.isEmpty()
                && sigla != null && !sigla.isEmpty()) {
            for (TipoIdentificacion aux : LST_TIPOS_DE_IDENTIFICACION) {

                if (sigla.equals(aux.getSigla())) {
                    result = aux;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Metodo encargado de crear un nuevo cotizante
     *
     * @param fila
     * @param tipoIdentificacion
     * @return
     * @throws Exception
     */
    
    /*
    protected CotizanteLIQ nuevoCotizante(Row fila, TipoIdentificacion tipoIdentificacion) throws Exception {
        CotizanteLIQ cotizante = new CotizanteLIQ();
        int columna = 9;
        cotizante.setNumeroIdentificacion(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        cotizante.setTipoIdentificacion(tipoIdentificacion);
        columna = 12;
        cotizante.setNombre(convertToString(fila.getCell(columna, Row.CREATE_NULL_AS_BLANK)));
        cotizante.setIdUsuarioCreacion(CONTEXTO_SOLICITUD.getIdUsuario());
        return cotizante;
    }
    */

    /**
     * Metodo que devuelve un caracter cero
     *
     * @param numero
     * @return
     */
    protected String convertToZero(String numero) {
        if (numero.equals("")) {
            return "0";
        }
        return numero;
    }

    // WRojas. Agosto 08.2021
    // TK SD 712380. Cuando la celda estaba <null> por defecto se colocaba un cero. 
    // Es necesario ahora, identificar si realmente viene <null> porque ahora es 
    // necesario utilizar el <cero>
    protected String convertToZero1(String numero) {
        if (numero.equals("")) {
            return "-1";
        }
        return numero;
    }
        
    /**
     * Metodo encargado de reemplazar el nombre del hilo
     */
    protected void setNewName() {
        Thread.currentThread().setName(Thread.currentThread().getName().replace("pool", NOMBRE));
    }

    /**
     * ************************* GETER AND SETER *****************************
     */
    /**
     * Metodo que indica cuando puede iniciar la ejecución el pool de hilos
     *
     * @return
     */
    public static boolean isOcupado() {
        return NominaParentThread.OCUPADO;
    }

    /**
     * Metodo encargado de indicar que el hilo se encuentra OCUPADO en una
     * ejecucion
     */
    public static void ocuparHilo() {
        synchronized (SYNCHRONIZED_OBJECT) {
            clearPool();
            NominaParentThread.OCUPADO = true;
        }
    }

    /**
     * Metodo encargado de cerrar los atributos estaticos al final la ejecucion
     * y principalmente indicar que ya se ecuentra disponible
     */
    public static void liberarHilo() {
        synchronized (SYNCHRONIZED_OBJECT) {
            NominaParentThread.ERROR = false;
            NominaParentThread.COUNT_THREAD = 0;
            NominaParentThread.TOTAL_THREAD = null;
            NominaParentThread.INI_THREAD = null;
            NominaParentThread.LST_TIPOS_DE_IDENTIFICACION = null;
            clearPool();
            NominaParentThread.OCUPADO = false;
        }
    }

    /**
     * metodo geter de ERROR
     *
     * @return
     */
    public static boolean isError() {
        return ERROR;
    }

    /**
     * Metodo seter de ERROR
     *
     * @param error
     */
    public static void setError(boolean error) {
        NominaParentThread.ERROR = error;
    }

    /**
     * Metodo que indica si el hilo debe continuar o para por algun ERROR en
     * cualquier otro hilo
     */
    public void stopByError() {
        if (isError()) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * metodo encargado de indicar que se ha producido un error
     */
    public static void unError() {
        synchronized (SYNCHRONIZED_OBJECT) {
            NominaParentThread.ERROR = true;
        }
    }

    /**
     * Metodo encargado de cargar y devolver la lista de EntityManager
     *
     * @return
     */
    public static List<EntityManager> getListEm() {
        if (LIST_EM == null) {
            LIST_EM = new ArrayList<>();
        }
        int size = LIST_EM.size();
        if (size < POOL_SIZE) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("co.gov.ugpp.parafiscales.wsappPu");
            for (int i = size; i < POOL_SIZE; i++) {
                LIST_EM.add(emf.createEntityManager());
            }
        }
        return LIST_EM;
    }

    /**
     * Metodo encargado de buscar la entityManager en la posicion pos de la
     * lista estatica LIST_EM
     *
     * @param pos
     * @return
     */
    protected EntityManager getEntityManager(int pos) {
        EntityManager result = null;
        if (getListEm() != null && LIST_EM.size() > 0) {
            result = LIST_EM.get(pos);
            if (result == null || !result.isOpen()) {
                try {
                    LIST_EM.remove(pos);
                    // asegura que sin importar que, el objeto que devuelva siempre va ser diferente de null y va a estar abierto
                    result = getEntityManager(pos);
                } catch (Exception ex) {
                    LOG.error("Exception - getEntityManager()", ex);
                }

            }
        }
        return result;
    }

    /**
     * Metodo get de COUNT_THREAD
     *
     * @return
     */
    protected static Integer getCOUNT_THREAD() {
        return COUNT_THREAD;
    }

    /**
     * Metodo que limpia la lista de entityManager
     */
    private static void clearPool() {
        if (LIST_EM != null && !LIST_EM.isEmpty()) {
            for (EntityManager aux : LIST_EM) {
                try {
                    if (aux != null && aux.isOpen()) {
                        // limpiar los que no sean nulos
                        aux.clear();
                    } else {
                        // remover los nulos
                        LIST_EM.remove(aux);
                    }
                } catch (Exception ex) {
                    LOG.error("Exception - clearPool()", ex);
                }
            }
        }
    }

    /**
     * Metodo encargado de identificar procesos demonios y eliminarlos
     *
     * @return
     */
    public static boolean isDaemonNominaPool() {
        List<Thread> lstThreads = getAllNominaParentsThreads();
        int contaDem = 0;
        boolean result = false;
        try {
            if (isOcupado() && !lstThreads.isEmpty()) {
                for (Thread tmp : lstThreads) {
                    // eliminamos los hilos demonios
                    if (tmp != null) {
                        if (tmp.isDaemon()) {
                            contaDem++;
                            try {
                                LOG.warn("isDaemonNominaPool().demonio: " + tmp.getName());
                                tmp.interrupt();
                                contaDem--;
                            } catch (SecurityException es) {
                                LOG.error("isDaemonNominaPool().SecurityException: - " + tmp.getName(), es);
                            }
                        }
                    }
                }
                if (contaDem > 0) { // Solo notifica cuando no sea posible eliminar los demonios
                    result = true;
                }
            }
        } catch (Exception ex) {
            LOG.error("Error - isDaemonNominaPool()", ex);
        }
        return result;
    }

    /**
     * metodo encargado de buscar y devolver todos los hilos asociados al
     * proceso de liquidacion
     *
     * @return una lista con todos los hilos que coincidan
     */
    private static List<Thread> getAllNominaParentsThreads() {
        Set<Thread> threadSetList = Thread.getAllStackTraces().keySet();
        List<Thread> lstThreads = new ArrayList<>();
        for (Thread tmp : threadSetList) {
            if (tmp.getName().startsWith(NOMBRE)) {
                lstThreads.add(tmp);
            }
        }
        return lstThreads;
    }

    /**
     * Metodo encargado de resetear y eliminar todo el pool de hilos de
     * liquidador y libear el pool para que pueda ser usado nuevamente
     *
     * @return
     */
    public static String resetNominaPool() {
        // LOG.info("Op: resetNominaPool ::: INIT");
        String result = "";
        try {
            List<Thread> lstThreads = getAllNominaParentsThreads();
            int hilos = 0;
            int demonios = 0;
            int errores = 0;
            int tam = lstThreads.size();
            if (!lstThreads.isEmpty()) {
                for (Thread tmp : lstThreads) {
                    // eliminamos todos los hilos
                    if (tmp != null) {
                        try {
                            if (tmp.isDaemon()) {
                                LOG.warn("resetNominaPool().demonio: " + tmp.getName());
                                demonios++;
                            } else {
                                hilos++;
                            }
                            tmp.interrupt();
                        } catch (SecurityException es) {
                            LOG.error("resetNominaPool().SecurityException.demonio: - " + tmp.getName(), es);
                            errores++;
                        }
                    }
                }
            }
            result = NOMBRE + " - Hilos (" + hilos + "/" + tam + ") "
                    + " - Demonios (" + demonios + "/" + tam + ") "
                    + " - errores tratando de interrumpir (" + errores + "/" + tam + ") ";
        } catch (Exception ex) {
            LOG.error("Error - resetNominaPool()", ex);
        } finally {
            liberarHilo();
            // LOG.info("Op: resetNominaPool ::: END");
        }
        return result;
    }

}
