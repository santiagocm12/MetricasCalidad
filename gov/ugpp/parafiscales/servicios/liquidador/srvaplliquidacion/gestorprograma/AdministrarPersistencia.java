package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma;

//import co.gov.ugpp.parafiscales.servicios.liquidador.entity.HojaCalculoLiqSanciones;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.OpLiquidarSolTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.HojaCalculoLiquidacion;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.entity.InInstPrograma;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.entity.InInstProgramaRegla;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.entity.InRegla;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.jpa.JPAEntityDao;
import java.io.Serializable;
import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.persistence.PersistenceContext;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement
public class AdministrarPersistencia implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AdministrarPersistencia.class);

    public AdministrarPersistencia() {
    }

    public AdministrarPersistencia(EntityManager paramEntityManager) {
        this.entityManager = paramEntityManager;
    }

    /**
     *
     * @param idPrograma
     * @param msjOpLiquidarSol
     * @return 
     */
    public InInstPrograma registrarInicioEjecucionInstanciaPrograma(String idPrograma, OpLiquidarSolTipo msjOpLiquidarSol) {
        JPAEntityDao jpaEntityDao = new JPAEntityDao(getEntityManager());

        InInstPrograma obj = new InInstPrograma();
        obj.setIdInstanciaPrograma(UUID.randomUUID().toString());
        obj.setFecCreacionRegistro(new Date());
        obj.setIdPrograma(idPrograma);
        obj.setIdUsuarioCreacion(msjOpLiquidarSol.getContextoTransaccional().getIdUsuario());
        //  this.entityManager.getTransaction().begin();
        jpaEntityDao.save(obj, true);
        //  this.entityManager.getTransaction().commit();
        return obj;
    }

    public void registrarFinEjecucionInstanciaPrograma(InInstPrograma inInstPrograma, String idPrograma,
            OpLiquidarSolTipo msjOpLiquidarSol) {
        JPAEntityDao jpaEntityDao = new JPAEntityDao(getEntityManager());

        InInstPrograma obj = jpaEntityDao.findById(InInstPrograma.class, inInstPrograma.getIdInstanciaPrograma());

        //  this.entityManager.getTransaction().begin();
        obj.setFecModificacionRegistro(new Date());
        //System.out.println("== YESID === FINALIZA EL PROGRAMA "+obj.getFecModificacionRegistro());

        jpaEntityDao.save(obj, true);

        //  this.entityManager.getTransaction().commit();
    }

    public InInstProgramaRegla registrarInicioEjecucionRegla(Integer numOrder, InInstPrograma inInstPrograma, String idRegla,
            String idPrograma, OpLiquidarSolTipo msjOpLiquidarSol) {
        JPAEntityDao jpaEntityDao = new JPAEntityDao(getEntityManager());

        InInstProgramaRegla obj = new InInstProgramaRegla();
        obj.setId(UUID.randomUUID().toString());
        obj.setFecCreacionRegistro(new Date());
        obj.setIdInstanciaPrograma(inInstPrograma.getIdInstanciaPrograma());
        obj.setIdPrograma(idPrograma);
        obj.setIdRegla(idRegla);
        obj.setIdUsuarioCreacion(msjOpLiquidarSol.getContextoTransaccional().getIdUsuario());
        obj.setNumOrder(numOrder);

        // this.entityManager.getTransaction().begin();
        jpaEntityDao.save(obj, true);

        //  this.entityManager.getTransaction().commit();
        return obj;
    }

    public void registrarFinEjecucionRegla(InInstProgramaRegla inInstProgramaRegla, InInstPrograma inInstPrograma,
            String idRegla, String idPrograma, OpLiquidarSolTipo msjOpLiquidarSol) {
        JPAEntityDao jpaEntityDao = new JPAEntityDao(getEntityManager());

        InInstProgramaRegla instObj = jpaEntityDao.findById(InInstProgramaRegla.class, inInstProgramaRegla.getId());

        // this.entityManager.getTransaction().begin();
        instObj.setFecModificacionRegistro(new Date());

        jpaEntityDao.save(instObj, true);

        //  this.entityManager.getTransaction().commit();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public HojaCalculoLiquidacion registrarHojaCalculoLiquidacion(OpLiquidarSolTipo msjOpLiquidarSol) {

        String sqlId = "SELECT seqhojacalculoliquidador.NEXTVAL FROM DUAL";
        Query queryId = getEntityManager().createNativeQuery(sqlId);
        Object idObj = queryId.getSingleResult();

        //this.entityManager.getTransaction().begin(); //OJO WMRR
        HojaCalculoLiquidacion obj = new HojaCalculoLiquidacion();
        obj.setId((BigDecimal) idObj);
        obj.setIdexpediente(msjOpLiquidarSol.getExpediente().getIdNumExpediente());
        obj.setFechaCreacion(Calendar.getInstance());

        String sql = "INSERT INTO LIQ_HOJA_CALCULO_LIQUIDACION (ID,IDEXPEDIENTE,FECHACREACION) VALUES (?,?,?) ";

        Query query = getEntityManager().createNativeQuery(sql);
        query.setParameter(1, idObj);
        query.setParameter(2, msjOpLiquidarSol.getExpediente().getIdNumExpediente());
        query.setParameter(3, Calendar.getInstance());

        query.executeUpdate();

        //this.entityManager.getTransaction().commit(); // OJO WMRR
        return obj;

    }
/*
    public void guardarResultadoRegla(List<DatosEjecucionRegla> listDatEjeRegla, InRegla regla,
            OpLiquidarSolTipo msjOpLiquidarSol, HojaCalculoLiquidacion hojaCalculoLiquidacion, Boolean primeraRegla,
            Map<String, Object> infoNegocio) {
        //LOG.info("Inside by guardarResultadoRegla()");
        // this.entityManager.getTransaction().begin();

        BigDecimal idHojaDetal = null;

        for (int i = 0; i < listDatEjeRegla.size(); i++) {
            DatosEjecucionRegla obj = listDatEjeRegla.get(i);

            if (primeraRegla) {
                String sqlId = "SELECT seqhojacalculoliquidadordetal.NEXTVAL FROM DUAL";
                Query queryId = getEntityManager().createNativeQuery(sqlId);
                idHojaDetal = (BigDecimal) queryId.getSingleResult();

                String sql = "INSERT INTO HOJA_CALCULO_LIQUIDACION_DETAL (ID,IDHOJACALCULOLIQUIDACION,IDNOMINADETALLE,IDCONCILIACIONCONTABLEDETALLE,APORTA_ID,APORTA_APORTE_ESAP_Y_MEN,APORTA_CLASE,APORTA_EXCEPCION_LEY_1233_2008,"
                        + "APORTA_PRIMER_NOMBRE, APORTA_NUMERO_IDENTIFICACION,COTIZ_ID,COTIZ_ACTIVIDAD_ALTO_RIESGO_PE,COTIZ_COLOMBIANO_EN_EL_EXT, COTIZ_EXTRANJERO_NO_COTIZAR,COTIZ_SUBTIPO_COTIZANTE,COTIZ_TIPO_COTIZANTE,"
                        + "COTIZ_NOMBRE, COTIZ_NUMERO_IDENTIFICACION, COTIZD_ANO, COTIZD_MES, "
                        + regla.getCodigo()
                        + ",FEC_CREACION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

                Query query = getEntityManager().createNativeQuery(sql);
                query.setParameter(1, idHojaDetal);
                query.setParameter(2, hojaCalculoLiquidacion.getId());
                query.setParameter(3, obj.getNominaDetalle().getId());
                query.setParameter(4, 0);// FIXME dato de ejemplo
                query.setParameter(5, obj.getNominaDetalle().getIdaportante().getId());
                query.setParameter(6, obj.getNominaDetalle().getIdaportante().getAportaEsapYMen());
                query.setParameter(7, obj.getNominaDetalle().getIdaportante().getClase());
                query.setParameter(8, obj.getNominaDetalle().getIdaportante().getExcepcionLey12332008());
                query.setParameter(9, obj.getNominaDetalle().getIdaportante().getPrimerNombre());
                query.setParameter(10, obj.getNominaDetalle().getIdaportante().getNumeroIdentificacion());
                
                // Datos del cotizante
                //query.setParameter(11, obj.getNominaDetalle().getIdcotizante().getId());
                
                query.setParameter(12, obj.getNominaDetalle().getActividad_alto_riesgo_pension());
                query.setParameter(13, obj.getNominaDetalle().getColombiano_en_el_exterior());
                query.setParameter(14, obj.getNominaDetalle().getExtranjero_no_obligado_a_cotizar_pension());
                query.setParameter(15, obj.getNominaDetalle().getSubTipoCotizante());
                query.setParameter(16, obj.getNominaDetalle().getTipoCotizante());
                query.setParameter(17, obj.getNominaDetalle().getNombre());
                query.setParameter(18, obj.getNominaDetalle().getNumeroIdentificacionActual());
                query.setParameter(19, obj.getNominaDetalle().getAno());
                query.setParameter(20, obj.getNominaDetalle().getMes());

                String key = obj.getNominaDetalle().getNumeroIdentificacionActual() + "#" + regla.getCodigo() + "#"
                        + obj.getNominaDetalle().getAno().toString() + obj.getNominaDetalle().getMes().toString();
                Object result = infoNegocio.get(key);
                query.setParameter(21, result);

                query.setParameter(22, Calendar.getInstance());

                query.executeUpdate();
            } else {
                String sql = "UPDATE HOJA_CALCULO_LIQUIDACION_DETAL SET " + regla.getCodigo()
                        + " = ?, FEC_MODIFICACION = ? WHERE IDHOJACALCULOLIQUIDACION = ? AND IDNOMINADETALLE = ?";

                String key = obj.getNominaDetalle().getNumeroIdentificacionActual() + "#" + regla.getCodigo() + "#"
                        + obj.getNominaDetalle().getAno().toString() + obj.getNominaDetalle().getMes().toString();
                Object result = infoNegocio.get(key);

                Query query = getEntityManager().createNativeQuery(sql);
                query.setParameter(1, result);
                query.setParameter(2, Calendar.getInstance());
                query.setParameter(3, hojaCalculoLiquidacion.getId());
                query.setParameter(4, obj.getNominaDetalle().getId());

                query.executeUpdate();
            }

        }

        //   this.entityManager.getTransaction().commit();
    }
*/
    public void guardarResultadoReglaNominaDetalle(DatosEjecucionRegla obj, List<InRegla> reglaList,
            OpLiquidarSolTipo msjOpLiquidarSol, HojaCalculoLiquidacion hojaCalculoLiquidacion, Map<String, Object> infoNegocio) {
        // LOG.info("Inside by guardarResultadoReglaNominaDetalle()");
        //  this.entityManager.getTransaction().begin();
        final String parameters_To_Set = "Parameters_To_Set";
        final String values_To_Set = "Values_To_Set";
        try {
            BigDecimal idHojaDetal = null;
            InRegla regla = null;
            String key = null;
            String sqlId = "SELECT seqhojacalculoliquidadordetal.NEXTVAL FROM DUAL";
            Query queryId = getEntityManager().createNativeQuery(sqlId);
            idHojaDetal = (BigDecimal) queryId.getSingleResult();

            String sql = "INSERT INTO HOJA_CALCULO_LIQUIDACION_DETAL (ID,IDHOJACALCULOLIQUIDACION,IDNOMINADETALLE,IDCONCILIACIONCONTABLEDETALLE,APORTA_ID,APORTA_APORTE_ESAP_Y_MEN,APORTA_CLASE,APORTA_EXCEPCION_LEY_1233_2008,"
                    + "APORTA_PRIMER_NOMBRE, APORTA_NUMERO_IDENTIFICACION,COTIZ_ID,COTIZ_ACTIVIDAD_ALTO_RIESGO_PE,COTIZ_COLOMBIANO_EN_EL_EXT, COTIZ_EXTRANJERO_NO_COTIZAR,COTIZ_SUBTIPO_COTIZANTE,COTIZ_TIPO_COTIZANTE,"
                    + "COTIZ_NOMBRE, COTIZ_NUMERO_IDENTIFICACION, COTIZD_ANO, COTIZD_MES, FEC_CREACION, " 
                    + parameters_To_Set + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + values_To_Set + ") ";

            // Se cargan los nombres de campos a actualizar
            for (int i = 0; i < reglaList.size(); i++) 
            {
                regla = reglaList.get(i);
                key = obj.getNominaDetalle().getNumeroIdentificacionActual() + "#" + regla.getCodigo() + "#"
                        + obj.getNominaDetalle().getAno().toString() + obj.getNominaDetalle().getMes().toString();
                
                //System.out.println("::ANDRES8:: key: " + key);
                
                if(infoNegocio.get(key) != null) 
                {
                    sql = sql.replace(parameters_To_Set, (regla.getCodigo() + ", " + parameters_To_Set));
                    sql = sql.replace(values_To_Set, ("?, " + values_To_Set));
                }
            }
            
            //System.out.println("::ANDRES6:: size listRegla: " + sql);
            
            sql = sql.replace(", " + parameters_To_Set, "") ;
            
            //System.out.println("::ANDRES7:: parameters_To_Set: " + parameters_To_Set);
            
            sql = sql.replace(", " + values_To_Set, "") ;

            //System.out.println("::ANDRES8:: values_To_Set" + values_To_Set);
            

            //System.out.println("::ANDRES9:: SQL" + sql);

            
            
            Query query = getEntityManager().createNativeQuery(sql);

            query.setParameter(1, idHojaDetal);
            query.setParameter(2, hojaCalculoLiquidacion.getId());
            query.setParameter(3, obj.getNominaDetalle().getId());
            query.setParameter(4, 0);// FIXME dato de ejemplo
            query.setParameter(5, obj.getNominaDetalle().getIdaportante().getId());
            query.setParameter(6, obj.getNominaDetalle().getIdaportante().getAportaEsapYMen());
            query.setParameter(7, obj.getNominaDetalle().getIdaportante().getClase());
            query.setParameter(8, obj.getNominaDetalle().getIdaportante().getExcepcionLey12332008());
            query.setParameter(9, obj.getNominaDetalle().getIdaportante().getPrimerNombre());
            query.setParameter(10, obj.getNominaDetalle().getIdaportante().getNumeroIdentificacion()); // Datos del cotizante
            query.setParameter(11, null);
            query.setParameter(12, obj.getNominaDetalle().getActividad_alto_riesgo_pension());
            query.setParameter(13, obj.getNominaDetalle().getColombiano_en_el_exterior());
            query.setParameter(14, obj.getNominaDetalle().getExtranjero_no_obligado_a_cotizar_pension());
            query.setParameter(15, obj.getNominaDetalle().getSubTipoCotizante());
            query.setParameter(16, obj.getNominaDetalle().getTipoCotizante());
            query.setParameter(17, obj.getNominaDetalle().getNombreCotizante());
            query.setParameter(18, obj.getNominaDetalle().getNumeroIdentificacionActual());
            query.setParameter(19, obj.getNominaDetalle().getAno());
            query.setParameter(20, obj.getNominaDetalle().getMes());
            query.setParameter(21, Calendar.getInstance());

            // Se cargan los valores de parametros almacenados en el Map
            int cont = 22;
            
            for (int i = 0; i < reglaList.size(); i++) {
                regla = reglaList.get(i);
                key = obj.getNominaDetalle().getNumeroIdentificacionActual() + "#" + regla.getCodigo() + "#"
                        + obj.getNominaDetalle().getAno().toString() + obj.getNominaDetalle().getMes().toString();
                
                //System.out.println("::ANDRES10:: key: " + key);
                
                if(infoNegocio.get(key) != null) {
                    Object value = infoNegocio.get(key);
                    //System.out.println("::ANDRES11:: contador: " + cont + " value: " + value);
                    if(value.getClass().equals(String.class)) {
                        value = value.toString().trim();
                    }
                    query.setParameter((cont++), value);
                }
            }

            //LOG.info("::ANDRES12:: Cantidad de columnas a realizar update: " + reglaList.size());
            
            query.executeUpdate();

            //  this.entityManager.getTransaction().commit();
            // LOG.info("Outside by guardarResultadoReglaNominaDetalle()");
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public void guardarResultadoLiquidacionSanciones(DatosEjecucionRegla obj,HojaCalculoLiquidacion hojaCalculoLiquidacion,  Map<String, Object> infoNegocio) {
        // LOG.info("Inside by guardarResultadoReglaNominaDetalle()");
        //  this.entityManager.getTransaction().begin();
        ArrayList<String> listaReglas = new ArrayList<>();
        listaReglas.add("PAG_ANT_RDOC_SALUD");listaReglas.add("FECH_PPAG_POST_RDOC_SALUD");listaReglas.add("PAGOS_ENTRE_RDOC_SALUD");
        listaReglas.add("PAG_ANT_RDOC_PENSION");listaReglas.add("FECH_PPAG_POST_RDOC_PENSION");listaReglas.add("PAGOS_ENTRE_RDOC_PENSION");
        listaReglas.add("PAG_ANT_RDOC_AR");listaReglas.add("FECH_PPAG_POST_RDOC_AR");listaReglas.add("PAGOS_ENTRE_RDOC_AR");        
        listaReglas.add("PAG_ANT_RDOC_FSP");listaReglas.add("FECH_PPAG_POST_RDOC_FSP");listaReglas.add("PAGOS_ENTRE_RDOC_FSP");
        listaReglas.add("PAG_ANT_RDOC_ARL");listaReglas.add("FECH_PPAG_POST_RDOC_ARL");listaReglas.add("PAGOS_ENTRE_RDOC_ARL");        
        listaReglas.add("PAG_ANT_RDOC_SENA");listaReglas.add("FECH_PPAG_POST_RDOC_SENA");listaReglas.add("PAGOS_ENTRE_RDOC_SENA");
        listaReglas.add("PAG_ANT_RDOC_ICBF");listaReglas.add("FECH_PPAG_POST_RDOC_ICBF");listaReglas.add("PAGOS_ENTRE_RDOC_ICBF");
        listaReglas.add("PAG_ANT_RDOC_CCF");listaReglas.add("FECH_PPAG_POST_RDOC_CCF");listaReglas.add("PAGOS_ENTRE_RDOC_CCF"); // 24 campos
        // <INEXACTITUD> PARA CADA SUBSISTEMA
        listaReglas.add("BASE_SANCION_INEX_SAL");listaReglas.add("TARIFA_SANCION_INEX_SAL");listaReglas.add("VALOR_SANCION_INEX_SAL"); // 35%
        listaReglas.add("BASE_SANCION_INEX_SAL_V2");listaReglas.add("TARI_SANCION_INEX_SAL_V2");listaReglas.add("VALO_SANCION_INEX_SAL_V2"); // 60%
        listaReglas.add("LIM_BASE_SANCION_INEX_SAL"); // Límite base sanción SALUD
        listaReglas.add("BASE_SANCION_INEX_PEN");listaReglas.add("TARIFA_SANCION_INEX_PEN");listaReglas.add("VALOR_SANCION_INEX_PEN"); // 35%
        listaReglas.add("BASE_SANCION_INEX_PEN_V2");listaReglas.add("TARI_SANCION_INEX_PEN_V2");listaReglas.add("VALO_SANCION_INEX_PEN_V2"); // 60%
        listaReglas.add("LIM_BASE_SANCION_INEX_PEN"); // Límite base sanción PENSIÓN
        listaReglas.add("BASE_SANCION_INEX_FSP");listaReglas.add("TARIFA_SANCION_INEX_FSP");listaReglas.add("VALOR_SANCION_INEX_FSP"); // 35%
        listaReglas.add("BASE_SANCION_INEX_FSP_V2");listaReglas.add("TARI_SANCION_INEX_FSP_V2");listaReglas.add("VALO_SANCION_INEX_FSP_V2"); // 60%
        listaReglas.add("LIM_BASE_SANCION_INEX_FSP"); // Límite base sanción FSP
        listaReglas.add("BASE_SANCION_INEX_ARI");listaReglas.add("TARIFA_SANCION_INEX_ARI");listaReglas.add("VALOR_SANCION_INEX_ARI"); // 35%
        listaReglas.add("BASE_SANCION_INEX_ARI_V2");listaReglas.add("TARI_SANCION_INEX_ARI_V2");listaReglas.add("VALO_SANCION_INEX_ARI_V2"); // 60%
        listaReglas.add("LIM_BASE_SANCION_INEX_ARI"); // Límite base sanción ARI
        listaReglas.add("BASE_SANCION_INEX_ARL");listaReglas.add("TARIFA_SANCION_INEX_ARL");listaReglas.add("VALOR_SANCION_INEX_ARL"); //35%
        listaReglas.add("BASE_SANCION_INEX_ARL_V2");listaReglas.add("TARI_SANCION_INEX_ARL_V2");listaReglas.add("VALO_SANCION_INEX_ARL_V2"); //60%
        listaReglas.add("LIM_BASE_SANCION_INEX_ARL"); // Límite base sanción ARL
        listaReglas.add("BASE_SANCION_INEX_SENA");listaReglas.add("TARIFA_SANCION_INEX_SENA");listaReglas.add("VALOR_SANCION_INEX_SENA"); // 35%
        listaReglas.add("BASE_SANCION_INEX_SENA_V2");listaReglas.add("TARI_SANCION_INEX_SENA_V2");listaReglas.add("VALO_SANCION_INEX_SENA_V2"); // 60%
        listaReglas.add("LIM_BASE_SANCION_INEX_SENA"); // Límite base sanción SENA
        listaReglas.add("BASE_SANCION_INEX_ICBF");listaReglas.add("TARIFA_SANCION_INEX_ICBF");listaReglas.add("VALOR_SANCION_INEX_ICBF"); // 35%
        listaReglas.add("BASE_SANCION_INEX_ICBF_V2");listaReglas.add("TARI_SANCION_INEX_ICBF_V2");listaReglas.add("VALO_SANCION_INEX_ICBF_V2"); // 60%
        listaReglas.add("LIM_BASE_SANCION_INEX_ICBF"); // Límite base sanción ICBF
        listaReglas.add("BASE_SANCION_INEX_CCF");listaReglas.add("TARIFA_SANCION_INEX_CCF");listaReglas.add("VALOR_SANCION_INEX_CCF"); // 35%
        listaReglas.add("BASE_SANCION_INEX_CCF_V2");listaReglas.add("TARI_SANCION_INEX_CCF_V2");listaReglas.add("VALO_SANCION_INEX_CCF_V2"); // 60%
        listaReglas.add("LIM_BASE_SANCION_INEX_CCF"); // Límite base sanción CCF ---> 56 campos
        // <OMISION> PARA CADA SUBSISTEMA
        listaReglas.add("BASE_SANCION_OMISO_SAL");listaReglas.add("TARIFA_SANCION_OMISO_SAL");listaReglas.add("NUM_MESES_SANCION_OMISO_SAL");
        listaReglas.add("VALOR_SANCION_OMISO_SAL"); // Aplicando una tarifa del 10% - SALUD
        listaReglas.add("BASE_SANCION_OMISO_SAL_V2");listaReglas.add("TARI_SANCION_OMISO_SAL_V2");listaReglas.add("NUM_MES_SANCION_OMISO_SAL_V2");
        listaReglas.add("VALO_SANCION_OMISO_SAL_V2"); // Aplicando una tarifa del 5%  - SALUD
        listaReglas.add("BASE_SANCION_OMISO_ARL");listaReglas.add("TARIFA_SANCION_OMISO_ARL");listaReglas.add("NUM_MESES_SANCION_OMISO_ARL");
        listaReglas.add("VALOR_SANCION_OMISO_ARL"); // Aplicando una tarifa del 10% - ARL
        listaReglas.add("BASE_SANCION_OMISO_ARL_V2");listaReglas.add("TARI_SANCION_OMISO_ARL_V2");listaReglas.add("NUM_MES_SANCION_OMISO_ARL_V2");
        listaReglas.add("VALO_SANCION_OMISO_ARL_V2"); // Aplicando una tarifa del 5%  - ARL
        listaReglas.add("BASE_SANCION_OMISO_CCF");listaReglas.add("TARIFA_SANCION_OMISO_CCF");listaReglas.add("NUM_MESES_SANCION_OMISO_CCF");
        listaReglas.add("VALOR_SANCION_OMISO_CCF"); // Aplicando una tarifa del 10% - CCF
        listaReglas.add("BASE_SANCION_OMISO_CCF_V2");listaReglas.add("TARI_SANCION_OMISO_CCF_V2");listaReglas.add("NUM_MES_SANCION_OMISO_CCF_V2");
        listaReglas.add("VALO_SANCION_OMISO_CCF_V2"); // Aplicando una tarifa del 5%  - CCF  --> 24 campos (104 campos en total)
        listaReglas.add("BASE_SANCION_OMISO_PEN");listaReglas.add("TARIFA_SANCION_OMISO_PEN");listaReglas.add("NUM_MESES_SANCION_OMISO_PEN");
        listaReglas.add("VALOR_SANCION_OMISO_PEN"); // Aplicando una tarifa del 10% - PENSIÓN
        listaReglas.add("BASE_SANCION_OMISO_PEN_V2");listaReglas.add("TARI_SANCION_OMISO_PEN_V2");listaReglas.add("NUM_MES_SANCION_OMISO_PEN_V2");
        listaReglas.add("VALO_SANCION_OMISO_PEN_V2"); // Aplicando una tarifa del 5% - PENSIÓN
        listaReglas.add("BASE_SANCION_OMISO_FSP");listaReglas.add("TARIFA_SANCION_OMISO_FSP");listaReglas.add("NUM_MESES_SANCION_OMISO_FSP");
        listaReglas.add("VALOR_SANCION_OMISO_FSP"); // Aplicando una tarifa del 10% - FSP
        listaReglas.add("BASE_SANCION_OMISO_FSP_V2");listaReglas.add("TARI_SANCION_OMISO_FSP_V2");listaReglas.add("NUM_MES_SANCION_OMISO_FSP_V2");
        listaReglas.add("VALO_SANCION_OMISO_FSP_V2"); // Aplicando una tarifa del 5% - FSP
        listaReglas.add("BASE_SANCION_OMISO_ARI");listaReglas.add("TARIFA_SANCION_OMISO_ARI");listaReglas.add("NUM_MESES_SANCION_OMISO_ARI");
        listaReglas.add("VALOR_SANCION_OMISO_ARI"); // Aplicando una tarifa del 10% - ALTO RIESGO
        listaReglas.add("BASE_SANCION_OMISO_ARI_V2");listaReglas.add("TARI_SANCION_OMISO_ARI_V2");listaReglas.add("NUM_MES_SANCION_OMISO_ARI_V2");
        listaReglas.add("VALO_SANCION_OMISO_ARI_V2"); // Aplicando una tarifa del 5% - ALTO RIESGO
        
        
        // <MORA> PARA CADA SUBSISTEMA
        listaReglas.add("BASE_SANCION_MORA_SAL");listaReglas.add("TARIFA_SANCION_MORA_SAL");listaReglas.add("NUM_MESES_SANCION_MORA_SAL");
        listaReglas.add("VALOR_SANCION_MORA_SAL"); // Aplicando una tarifa del 10% MORA SALUD
        listaReglas.add("BASE_SANCION_MORA_SAL_V2");listaReglas.add("TARI_SANCION_MORA_SAL_V2");listaReglas.add("NUM_MES_SANCION_MORA_SAL_V2");
        listaReglas.add("VALO_SANCION_MORA_SAL_V2"); // Aplicando una tarifa del 5% MORA SALUD
        listaReglas.add("BASE_SANCION_MORA_PEN");listaReglas.add("TARIFA_SANCION_MORA_PEN");listaReglas.add("NUM_MESES_SANCION_MORA_PEN");
        listaReglas.add("VALOR_SANCION_MORA_PEN"); // Aplicando una tarifa del 10% MORA PENSION       
        listaReglas.add("BASE_SANCION_MORA_PEN_V2");listaReglas.add("TARI_SANCION_MORA_PEN_V2");listaReglas.add("NUM_MES_SANCION_MORA_PEN_V2");
        listaReglas.add("VALO_SANCION_MORA_PEN_V2"); // Aplicando una tarifa del 5% MORA PENSION    --> 16 campos     
        
        listaReglas.add("BASE_SANCION_MORA_FSP");listaReglas.add("TARIFA_SANCION_MORA_FSP");listaReglas.add("NUM_MESES_SANCION_MORA_FSP");
        listaReglas.add("VALOR_SANCION_MORA_FSP"); // Aplicando una tarifa del 10% MORA FSP
        listaReglas.add("BASE_SANCION_MORA_FSP_V2");listaReglas.add("TARI_SANCION_MORA_FSP_V2");listaReglas.add("NUM_MES_SANCION_MORA_FSP_V2");
        listaReglas.add("VALO_SANCION_MORA_FSP_V2"); // Aplicando una tarifa del 5% MORA PENSION  
        
        listaReglas.add("BASE_SANCION_MORA_ARL");listaReglas.add("TARIFA_SANCION_MORA_ARL");listaReglas.add("NUM_MESES_SANCION_MORA_ARL");
        listaReglas.add("VALOR_SANCION_MORA_ARL"); // Aplicando una tarifa del 10% MORA ARL
        listaReglas.add("BASE_SANCION_MORA_ARL_V2");listaReglas.add("TARI_SANCION_MORA_ARL_V2");listaReglas.add("NUM_MES_SANCION_MORA_ARL_V2");
        listaReglas.add("VALO_SANCION_MORA_ARL_V2"); // Aplicando una tarifa del 5% MORA PENSION  
        
        listaReglas.add("BASE_SANCION_MORA_SENA");listaReglas.add("TARIFA_SANCION_MORA_SENA");listaReglas.add("NUM_MESES_SANCION_MORA_SENA");
        listaReglas.add("VALOR_SANCION_MORA_SENA"); // Aplicando una tarifa del 10% MORA SENA
        listaReglas.add("BASE_SANCION_MORA_SENA_V2");listaReglas.add("TARI_SANCION_MORA_SENA_V2");listaReglas.add("NUM_MES_SANCION_MORA_SENA_V2");
        listaReglas.add("VALO_SANCION_MORA_SENA_V2"); // Aplicando una tarifa del 5% MORA SENA 
        
        listaReglas.add("BASE_SANCION_MORA_ARI");listaReglas.add("TARIFA_SANCION_MORA_ARI");listaReglas.add("NUM_MESES_SANCION_MORA_ARI");
        listaReglas.add("VALOR_SANCION_MORA_ARI"); // Aplicando una tarifa del 10% MORA Alto Riesgo
        listaReglas.add("BASE_SANCION_MORA_ARI_V2");listaReglas.add("TARI_SANCION_MORA_ARI_V2");listaReglas.add("NUM_MES_SANCION_MORA_ARI_V2");
        listaReglas.add("VALO_SANCION_MORA_ARI_V2"); // Aplicando una tarifa del 5% MORA Alto Riesgo  
        
        listaReglas.add("BASE_SANCION_MORA_ICBF");listaReglas.add("TARIFA_SANCION_MORA_ICBF");listaReglas.add("NUM_MESES_SANCION_MORA_ICBF");
        listaReglas.add("VALOR_SANCION_MORA_ICBF"); // Aplicando una tarifa del 10% MORA Icbf
        listaReglas.add("BASE_SANCION_MORA_ICBF_V2");listaReglas.add("TARI_SANCION_MORA_ICBF_V2");listaReglas.add("NUM_MES_SANCION_MORA_ICBF_V2");
        listaReglas.add("VALO_SANCION_MORA_ICBF_V2"); // Aplicando una tarifa del 5% MORA Icbf 
        
        listaReglas.add("BASE_SANCION_MORA_CCF");listaReglas.add("TARIFA_SANCION_MORA_CCF");listaReglas.add("NUM_MESES_SANCION_MORA_CCF");
        listaReglas.add("VALOR_SANCION_MORA_CCF"); // Aplicando una tarifa del 10% MORA CCF
        listaReglas.add("BASE_SANCION_MORA_CCF_V2");listaReglas.add("TARI_SANCION_MORA_CCF_V2");listaReglas.add("NUM_MES_SANCION_MORA_CCF_V2");
        listaReglas.add("VALO_SANCION_MORA_CCF_V2"); // Aplicando una tarifa del 5% MORA CCF  
        

        try {
            String sqlId = "SELECT LIQ_HOJA_CAL_SAN_ID_SEQ.NEXTVAL FROM DUAL";
            Query queryId = getEntityManager().createNativeQuery(sqlId);
            BigDecimal consecutivo = (BigDecimal) queryId.getSingleResult();
            String key = null;
            String sql = "INSERT INTO HOJA_CALCULO_LIQ_SANCIONES(ID,IDHOJACALCULOLIQUIDACION,IDNOMINADETALLE,ANO_LIQUIDACION,MES_LIQUIDACION,"
                    +"NUM_IDENTI_REALIZO_APORTES,"
                    +"PAG_ANT_RDOC_SALUD,FECH_PPAG_POST_RDOC_SALUD,PAGOS_ENTRE_RDOC_SALUD,PAG_ANT_RDOC_PENSION,FECH_PPAG_POST_RDOC_PENSION,"
                    +"PAGOS_ENTRE_RDOC_PENSION,PAG_ANT_RDOC_AR,FECH_PPAG_POST_RDOC_AR,PAGOS_ENTRE_RDOC_AR,PAG_ANT_RDOC_FSP,FECH_PPAG_POST_RDOC_FSP,"
                    +"PAGOS_ENTRE_RDOC_FSP,PAG_ANT_RDOC_ARL,FECH_PPAG_POST_RDOC_ARL,PAGOS_ENTRE_RDOC_ARL,PAG_ANT_RDOC_SENA,FECH_PPAG_POST_RDOC_SENA,"
                    +"PAGOS_ENTRE_RDOC_SENA,PAG_ANT_RDOC_ICBF,FECH_PPAG_POST_RDOC_ICBF,PAGOS_ENTRE_RDOC_ICBF,PAG_ANT_RDOC_CCF,FECH_PPAG_POST_RDOC_CCF,"
                    +"PAGOS_ENTRE_RDOC_CCF," 
                    +"BASE_SANCION_INEX_SAL,TARIFA_SANCION_INEX_SAL,VALOR_SANCION_INEX_SAL,BASE_SANCION_INEX_SAL_V2,TARI_SANCION_INEX_SAL_V2,"
                    +"VALO_SANCION_INEX_SAL_V2,LIM_BASE_SANCION_INEX_SAL,"
                    +"BASE_SANCION_INEX_PEN,TARIFA_SANCION_INEX_PEN,VALOR_SANCION_INEX_PEN,BASE_SANCION_INEX_PEN_V2,TARI_SANCION_INEX_PEN_V2,"
                    +"VALO_SANCION_INEX_PEN_V2,LIM_BASE_SANCION_INEX_PEN,"
                    +"BASE_SANCION_INEX_FSP,TARIFA_SANCION_INEX_FSP,VALOR_SANCION_INEX_FSP,BASE_SANCION_INEX_FSP_V2,TARI_SANCION_INEX_FSP_V2,"
                    +"VALO_SANCION_INEX_FSP_V2,LIM_BASE_SANCION_INEX_FSP,"
                    +"BASE_SANCION_INEX_ARI,TARIFA_SANCION_INEX_ARI,VALOR_SANCION_INEX_ARI,BASE_SANCION_INEX_ARI_V2,"
                    +"TARI_SANCION_INEX_ARI_V2,VALO_SANCION_INEX_ARI_V2,LIM_BASE_SANCION_INEX_ARI,"
                    +"BASE_SANCION_INEX_ARL,TARIFA_SANCION_INEX_ARL,VALOR_SANCION_INEX_ARL,BASE_SANCION_INEX_ARL_V2,TARI_SANCION_INEX_ARL_V2,"
                    +"VALO_SANCION_INEX_ARL_V2,LIM_BASE_SANCION_INEX_ARL,"
                    +"BASE_SANCION_INEX_SENA,TARIFA_SANCION_INEX_SENA,VALOR_SANCION_INEX_SENA,BASE_SANCION_INEX_SENA_V2,TARI_SANCION_INEX_SENA_V2,"
                    +"VALO_SANCION_INEX_SENA_V2,LIM_BASE_SANCION_INEX_SENA,"
                    +"BASE_SANCION_INEX_ICBF,TARIFA_SANCION_INEX_ICBF,VALOR_SANCION_INEX_ICBF,BASE_SANCION_INEX_ICBF_V2,TARI_SANCION_INEX_ICBF_V2,"
                    +"VALO_SANCION_INEX_ICBF_V2,LIM_BASE_SANCION_INEX_ICBF,"
                    +"BASE_SANCION_INEX_CCF,TARIFA_SANCION_INEX_CCF,VALOR_SANCION_INEX_CCF,BASE_SANCION_INEX_CCF_V2,TARI_SANCION_INEX_CCF_V2,"
                    +"VALO_SANCION_INEX_CCF_V2,LIM_BASE_SANCION_INEX_CCF,"
                    +"BASE_SANCION_OMISO_SAL,TARIFA_SANCION_OMISO_SAL,NUM_MESES_SANCION_OMISO_SAL,VALOR_SANCION_OMISO_SAL,BASE_SANCION_OMISO_SAL_V2,"
                    +"TARI_SANCION_OMISO_SAL_V2,NUM_MES_SANCION_OMISO_SAL_V2,VALO_SANCION_OMISO_SAL_V2,"
                    +"BASE_SANCION_OMISO_ARL,TARIFA_SANCION_OMISO_ARL,NUM_MESES_SANCION_OMISO_ARL,VALOR_SANCION_OMISO_ARL,BASE_SANCION_OMISO_ARL_V2,"
                    +"TARI_SANCION_OMISO_ARL_V2,NUM_MES_SANCION_OMISO_ARL_V2,VALO_SANCION_OMISO_ARL_V2,"
                    +"BASE_SANCION_OMISO_CCF,TARIFA_SANCION_OMISO_CCF,NUM_MESES_SANCION_OMISO_CCF,VALOR_SANCION_OMISO_CCF,"   
                    +"BASE_SANCION_OMISO_CCF_V2,TARI_SANCION_OMISO_CCF_V2,NUM_MES_SANCION_OMISO_CCF_V2,VALO_SANCION_OMISO_CCF_V2,"
                    +"BASE_SANCION_OMISO_PEN,TARIFA_SANCION_OMISO_PEN,NUM_MESES_SANCION_OMISO_PEN,VALOR_SANCION_OMISO_PEN,"
                    +"BASE_SANCION_OMISO_PEN_V2,TARI_SANCION_OMISO_PEN_V2,NUM_MES_SANCION_OMISO_PEN_V2,VALO_SANCION_OMISO_PEN_V2,"
                    +"BASE_SANCION_OMISO_FSP,TARIFA_SANCION_OMISO_FSP,NUM_MESES_SANCION_OMISO_FSP,VALOR_SANCION_OMISO_FSP,"
                    +"BASE_SANCION_OMISO_FSP_V2,TARI_SANCION_OMISO_FSP_V2,NUM_MES_SANCION_OMISO_FSP_V2,VALO_SANCION_OMISO_FSP_V2,"
                    +"BASE_SANCION_OMISO_ARI,TARIFA_SANCION_OMISO_ARI,NUM_MESES_SANCION_OMISO_ARI,VALOR_SANCION_OMISO_ARI,"
                    +"BASE_SANCION_OMISO_ARI_V2,TARI_SANCION_OMISO_ARI_V2,NUM_MES_SANCION_OMISO_ARI_V2,VALO_SANCION_OMISO_ARI_V2,"
                    +"BASE_SANCION_MORA_SAL,TARIFA_SANCION_MORA_SAL,NUM_MESES_SANCION_MORA_SAL,VALOR_SANCION_MORA_SAL,BASE_SANCION_MORA_SAL_V2,"
                    +"TARI_SANCION_MORA_SAL_V2,NUM_MES_SANCION_MORA_SAL_V2,VALO_SANCION_MORA_SAL_V2,BASE_SANCION_MORA_PEN,TARIFA_SANCION_MORA_PEN,"
                    +"NUM_MESES_SANCION_MORA_PEN,VALOR_SANCION_MORA_PEN,BASE_SANCION_MORA_PEN_V2,TARI_SANCION_MORA_PEN_V2,NUM_MES_SANCION_MORA_PEN_V2,"
                    +"VALO_SANCION_MORA_PEN_V2,BASE_SANCION_MORA_FSP,TARIFA_SANCION_MORA_FSP,NUM_MESES_SANCION_MORA_FSP,VALOR_SANCION_MORA_FSP,"
                    +"BASE_SANCION_MORA_FSP_V2,TARI_SANCION_MORA_FSP_V2,NUM_MES_SANCION_MORA_FSP_V2,VALO_SANCION_MORA_FSP_V2,"
                    +"BASE_SANCION_MORA_ARL,TARIFA_SANCION_MORA_ARL,NUM_MESES_SANCION_MORA_ARL,VALOR_SANCION_MORA_ARL,"
                    +"BASE_SANCION_MORA_ARL_V2,TARI_SANCION_MORA_ARL_V2,NUM_MES_SANCION_MORA_ARL_V2,VALO_SANCION_MORA_ARL_V2,"
                    +"BASE_SANCION_MORA_SENA,TARIFA_SANCION_MORA_SENA,NUM_MESES_SANCION_MORA_SENA,VALOR_SANCION_MORA_SENA,"
                    +"BASE_SANCION_MORA_SENA_V2,TARI_SANCION_MORA_SENA_V2,NUM_MES_SANCION_MORA_SENA_V2,VALO_SANCION_MORA_SENA_V2,"  
                    +"BASE_SANCION_MORA_ARI,TARIFA_SANCION_MORA_ARI,NUM_MESES_SANCION_MORA_ARI,VALOR_SANCION_MORA_ARI,"
                    +"BASE_SANCION_MORA_ARI_V2,TARI_SANCION_MORA_ARI_V2,NUM_MES_SANCION_MORA_ARI_V2,VALO_SANCION_MORA_ARI_V2," 
                    +"BASE_SANCION_MORA_ICBF,TARIFA_SANCION_MORA_ICBF,NUM_MESES_SANCION_MORA_ICBF,VALOR_SANCION_MORA_ICBF,"
                    +"BASE_SANCION_MORA_ICBF_V2,TARI_SANCION_MORA_ICBF_V2,NUM_MES_SANCION_MORA_ICBF_V2,VALO_SANCION_MORA_ICBF_V2," 
                    +"BASE_SANCION_MORA_CCF,TARIFA_SANCION_MORA_CCF,NUM_MESES_SANCION_MORA_CCF,VALOR_SANCION_MORA_CCF,"
                    +"BASE_SANCION_MORA_CCF_V2,TARI_SANCION_MORA_CCF_V2,NUM_MES_SANCION_MORA_CCF_V2,VALO_SANCION_MORA_CCF_V2)"                     
                    +" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                    +"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                    +"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
          
            Query query = getEntityManager().createNativeQuery(sql);

            query.setParameter(1, consecutivo);
            query.setParameter(2, hojaCalculoLiquidacion.getId());
            query.setParameter(3, obj.getNominaDetalle().getId());
            query.setParameter(4, obj.getNominaDetalle().getAno());
            query.setParameter(5, obj.getNominaDetalle().getMes());
            query.setParameter(6, obj.getNominaDetalle().getNumeroIdentificacionActual());
            
            int cont=7;
            for (int i = 0; i < listaReglas.size(); i++) {
                key = obj.getNominaDetalle().getNumeroIdentificacionActual() + "#" + listaReglas.get(i) + "#"
                        + obj.getNominaDetalle().getAno().toString() + obj.getNominaDetalle().getMes().toString();
                
                if(infoNegocio.get(key) != null) {
                    Object value = infoNegocio.get(key);
                    //System.out.println("::ANDRES11:: contador: " + cont + " value: " + value);
                    if(value.getClass().equals(String.class)) {
                        value = value.toString().trim();
                    }
                    query.setParameter((cont), value);
                    
                }else{
                    query.setParameter((cont), null);
                    //query.setParameter((cont), BigDecimal.ZERO);
                }
                cont++;
            }
            query.executeUpdate();

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
    
    public void medirTiempos(HojaCalculoLiquidacion hojaCalculoLiquidacion, DatosEjecucionRegla obj, java.sql.Timestamp tiempoini, java.sql.Timestamp tiempofin, String Regla) {

        String sql = "INSERT INTO HOJA_CALCULO_LIQUIDACION_TMP  (IDHOJACALCULOLIQUIDACION,IDNOMINADETALLE, FEC_INICIO,FEC_FIN,REGLA) "
                + " VALUES (?,?,?,?,?)";
        Query query = getEntityManager().createNativeQuery(sql);

        query.setParameter(1, hojaCalculoLiquidacion.getId());
        query.setParameter(2, obj.getNominaDetalle().getId());
        query.setParameter(3, tiempoini);
        query.setParameter(4, tiempofin);
        query.setParameter(5, Regla);
        query.executeUpdate();

    }
    
}
