package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.OpLiquidarSolTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.AportanteLIQ;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.CobFlex;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.CobParamGeneral;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.HojaCalculoLiquidacionDetalle;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.Nomina;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.NominaDetalle;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.PilaDepurada;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.HojaCalculoLiqSanciones;
import co.gov.ugpp.parafiscales.servicios.liquidador.errortipo.v1.ErrorTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.entity.InRegla;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.jpa.GestorProgramaDao;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.CacheService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

public class NominaModoEjecucion extends AbstractModoEjecucion {

    private static final long serialVersionUID = -2979443613854785520L;
    CobParamGeneral cobParamGeneral = new CobParamGeneral();
    static Map<String, String> LST_ADMINISTRADORA_PILA = null;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NominaModoEjecucion.class);

    @Override
    public List<DatosEjecucionRegla> getDatosEjecucionRegla(GestorProgramaDao gestorProgramaDao, OpLiquidarSolTipo msjOpLiquidarSol) {
        List<Nomina> nominaList = gestorProgramaDao.nominaByIdExpediente(msjOpLiquidarSol.getExpediente().getIdNumExpediente());
        List<NominaDetalle> nomDetList = null;
        List<DatosEjecucionRegla> listDatEjeRegla = new ArrayList<>();
        if (nominaList.size() > 0) {
            nomDetList = gestorProgramaDao.nominaDetalleByIdNomina(nominaList.get(0));
        } else {
            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, "Método: getDatosEjecucionRegla: Error Exception no hay <detalle_nomina>.");
            return listDatEjeRegla;
        }
        cargarLstAdministradoraPila(gestorProgramaDao);
        boolean primerReg = false;
        String nit = "";
        for (NominaDetalle obj : nomDetList) {
            if (!primerReg) {
                AportanteLIQ aportanteLiq = gestorProgramaDao.aportanteLIQById(obj);
                nit = aportanteLiq.getNumeroIdentificacion();
                primerReg = true;
            }
            DatosEjecucionRegla ejecucionRegla = new DatosEjecucionRegla();
            ejecucionRegla.setNomina(nominaList.get(0));
            //ejecucionRegla.setNomina(nominaList);
            ejecucionRegla.getNomina().setNit(new BigInteger(nit));
            //System.out.println("::ANDRES7:: setNit: " + ejecucionRegla.getNomina().getNit());
            ejecucionRegla.setNominaDetalle(obj);
            listDatEjeRegla.add(ejecucionRegla);
        }
        return listDatEjeRegla;
    }

    /**
     * Variables que deben ser reemplazadas por sus valores en los objetos java
     *
     * @return
     */
    @Override
    @SuppressWarnings("RedundantStringConstructorCall")
    public String inyectarValoresRegla(String scriptRegla, DatosEjecucionRegla obj, Map<String, Object> mapVariablesRegla) {
        String script = "";

        // FIXME solucionar error para que busque palabras completas
        if (StringUtils.contains(scriptRegla, "{DIAS_TRABAJADOS_MES}")) {
            script = StringUtils.replace(scriptRegla, "{DIAS_TRABAJADOS_MES}", getNumberToString(obj.getNominaDetalle().getDiasTrabajadosMes()));
        } else {
            script = new String(scriptRegla);
        }
        if (StringUtils.contains(script, "{DIA_LICEN_REMUNERADAS_MES}")) {
            script = StringUtils.replace(script, "{DIA_LICEN_REMUNERADAS_MES}", getNumberToString(obj.getNominaDetalle().getDiasLicenciaRemuneradasMes()));
        }
        if (StringUtils.contains(script, "{DIAS_SUSPENSION_MES}")) {
            script = StringUtils.replace(script, "{DIAS_SUSPENSION_MES}",
                    getNumberToString(obj.getNominaDetalle()
                            .getDiasSuspensionMes()));
        }
        if (StringUtils.contains(script, "{DIAS_HUELGA_LEGAL_MES}")) {
            script = StringUtils.replace(script, "{DIAS_HUELGA_LEGAL_MES}", getNumberToString(obj.getNominaDetalle().getDiasHuelgaLegalMes()));
        }
        if (StringUtils.contains(script, "{DIAS_INCAPACIDADES_MES}")) {
            script = StringUtils.replace(script, "{DIAS_INCAPACIDADES_MES}", getNumberToString(obj.getNominaDetalle().getDiasIncapacidadesMes()));
        }
        if (StringUtils.contains(script, "{DIAS_VACACIONES_MES}")) {
            script = StringUtils.replace(script, "{DIAS_VACACIONES_MES}", getNumberToString(obj.getNominaDetalle().getDiasVacacionesMes()));
        }
        if (StringUtils.contains(script, "{DIA_LICEN_MATERNI_PATERNI_MES}")) {
            script = StringUtils.replace(script, "{DIA_LICEN_MATERNI_PATERNI_MES}", getNumberToString(obj.getNominaDetalle().getDiasLicenciaMaternidadPaternidadMes()));
        }
        if (StringUtils.contains(script, "{IBCVACACIONES}")) {
            script = StringUtils.replace(script, "{IBCVACACIONES}", mapVariablesRegla.get("IBCVACACIONES#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj)).toString());
        }
        return script;
    }

    /**
     * Se hacee busqueda de variables necesitadas por las reglas antes de
     * ejecutar la regla
     *
     * @param errorTipo
     * @param gestorProgramaDao
     * @param obj
     * @param inRegla
     * @param mapVariablesRegla
     * @return
     */
    @Override
    public Object buscarVariablesRegla(List<ErrorTipo> errorTipo, GestorProgramaDao gestorProgramaDao, DatosEjecucionRegla obj,
            InRegla inRegla, Map<String, Object> mapVariablesRegla) {

        switch (inRegla.getCodigo()) {
            case "IBC_PERMISOS_REMUNERADOS":
                if (obj.getNominaDetalle().getDiasLicenciaRemuneradasMes() != null && obj.getNominaDetalle().getDiasLicenciaRemuneradasMes() > 0) {
                    String totalDiasAnteriorKey = "TOTAL_DIAS_REPORTADOS_MES_ANTERIOR#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(totalDiasAnteriorKey)) {
                        mapVariablesRegla.put(totalDiasAnteriorKey, obtenerTotalDiasReportadosMesAnterior(gestorProgramaDao, obj));
                    }
                    String key = "IBC#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(key)) {
                        Integer ibc = 0;
                        if (!obj.getNominaDetalle().getTipoCotizante().equals("31")) {
                            ibc = obtenerIBC(gestorProgramaDao, obj, mapVariablesRegla);
                        } else {
                            ibc = 0;
                        }
                        if (ibc != null) {
                            mapVariablesRegla.put(key, ibc);
                        } else {
                            return "-";
                        }
                    }
                } else {
                    return "-";
                }
                break;
            case "IBC_SUSP_PERMISOS":
                if (obj.getNominaDetalle().getDiasSuspensionMes() != null && obj.getNominaDetalle().getDiasSuspensionMes() > 0) {
                    String totalDiasAnteriorKey = "TOTAL_DIAS_REPORTADOS_MES_ANTERIOR#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(totalDiasAnteriorKey)) {
                        mapVariablesRegla.put(totalDiasAnteriorKey, obtenerTotalDiasReportadosMesAnterior(gestorProgramaDao, obj));
                    }
                    String key = "IBC#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(key)) {
                        Integer ibc = 0;
                        if (!obj.getNominaDetalle().getTipoCotizante().equals("31")) {
                            ibc = obtenerIBC(gestorProgramaDao, obj, mapVariablesRegla);
                        } else {
                            ibc = 0;
                        }
                        if (ibc != null) {
                            mapVariablesRegla.put(key, ibc);
                        } else {
                            return "-";
                        }
                    }
                } else {
                    return "-";
                }
                break;
            case "IBC_VACACIONES":
                if (obj.getNominaDetalle().getDiasVacacionesMes() != null && obj.getNominaDetalle().getDiasVacacionesMes() > 0) {
                    String totalDiasAnteriorKey = "TOTAL_DIAS_REPORTADOS_MES_ANTERIOR#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(totalDiasAnteriorKey)) {
                        mapVariablesRegla.put(totalDiasAnteriorKey, obtenerTotalDiasReportadosMesAnterior(gestorProgramaDao, obj));
                    }
                    String key1 = "IBC#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(key1)) {
                        Integer ibc1 = 0;
                        if (!obj.getNominaDetalle().getTipoCotizante().equals("31")) {
                            ibc1 = obtenerIBC(gestorProgramaDao, obj, mapVariablesRegla);
                        } else {
                            ibc1 = 0;
                        }
                        if (ibc1 != null) {
                            mapVariablesRegla.put(key1, ibc1);
                        } else {
                            return "-";
                        }
                    }
                    String key2 = "IBCVACACIONES#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(key2)) {
                        //INICIO DE REGLA NUEVA
                        BigInteger ibc2 = BigInteger.ZERO;
                        if (!obj.getNominaDetalle().getTipoCotizante().equals("31")) {
                            ibc2 = obtenerIBCvacaciones(gestorProgramaDao, obj);
                        } else {
                            ibc2 = BigInteger.ZERO;
                        }
                        if (ibc2 != null) {
                            mapVariablesRegla.put(key2, ibc2);
                        } else {
                            return "-";
                        }
                    }
                } else {
                    return "-";
                }
                break;
            case "IBC_HUELGA":
                if (obj.getNominaDetalle().getDiasHuelgaLegalMes() != null && obj.getNominaDetalle().getDiasHuelgaLegalMes() > 0) {
                    String totalDiasAnteriorKey = "TOTAL_DIAS_REPORTADOS_MES_ANTERIOR#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(totalDiasAnteriorKey)) {
                        mapVariablesRegla.put(totalDiasAnteriorKey, obtenerTotalDiasReportadosMesAnterior(gestorProgramaDao, obj));
                    }
                    String key = "IBC#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                    if (!mapVariablesRegla.containsKey(key)) {
                        Integer ibc = obtenerIBC(gestorProgramaDao, obj, mapVariablesRegla);
                        if (ibc != null) {
                            mapVariablesRegla.put(key, ibc);
                        } else {
                            return "-";
                        }
                    }
                } else {
                    return "-";
                }
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public String reemplazarVariablesRegla(String scriptRegla, DatosEjecucionRegla obj, Map<String, Object> mapVariablesRegla) {
        // se debe tener encuenta la cedula para buscar el valor por cedula del
        // cliente
        Iterator<?> it = mapVariablesRegla.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (StringUtils.contains((String) pair.getKey(), "#")) {
                String[] variableCedula = StringUtils.splitByWholeSeparator((String) pair.getKey(), "#");
                // FIXME resultado de una regla esta compuesto por
                // REGLA#CEDULA#ANOMES
                if (StringUtils.equals(variableCedula[1], obj.getNominaDetalle().getNumeroIdentificacionActual())
                        && variableCedula[2].equals(obj.getNominaDetalle().getAno().toString() + obj.getNominaDetalle().getMes().toString())) {
                    if (StringUtils.contains(scriptRegla, "{" + variableCedula[0] + "}")) {
                        // tocaria desgranar el codigo para obtener la variable
                        if (pair.getValue() instanceof Number) {
                            scriptRegla = StringUtils.replace(scriptRegla, "{" + variableCedula[0] + "}",
                                    getNumberToString((Number) pair.getValue()));
                        } else if (pair.getValue() instanceof String) {
                            scriptRegla = StringUtils.replace(scriptRegla, "{" + variableCedula[0] + "}", (String) pair.getValue());
                        }
                    }
                }
            } else {
                if (StringUtils.contains(scriptRegla, (String) pair.getKey())) {
                    if (pair.getValue() instanceof Number) {
                        scriptRegla = StringUtils.replace(scriptRegla, (String) pair.getKey(), getNumberToString((Number) pair.getValue()));
                    } else if (pair.getValue() instanceof String) {
                        scriptRegla = StringUtils.replace(scriptRegla, (String) pair.getKey(), (String) pair.getValue());
                    }
                }
            }
        }
        return scriptRegla;
    }

    private Integer obtenerIBC(GestorProgramaDao gestorProgramaDao, DatosEjecucionRegla obj, Map<String, Object> mapVariablesRegla) {

        HojaCalculoLiquidacionDetalle hojaDetalle = gestorProgramaDao.obtenerHojaCalculoLiquidacionDetalle(obj.getNomina(), obj.getNominaDetalle());
        if (hojaDetalle == null) {
            PilaDepurada pilaDepurada = gestorProgramaDao.obtenerPilaDepuradaMesAnterior(obj.getNomina(), obj.getNominaDetalle());
            if (pilaDepurada == null) {
                return null;
            } else {
                String totalDiasAnteriorKey = "TOTAL_DIAS_REPORTADOS_MES_ANTERIOR#" + obj.getNominaDetalle().getNumeroIdentificacionActual() + anyoMesDetalleKey(obj);
                mapVariablesRegla.put(totalDiasAnteriorKey, pilaDepurada.getDiasCotSalud());
                return pilaDepurada.getIbcSalud();
            }
        } else {
            if (hojaDetalle.getIbcCalculadoSalud() != null) {
                return hojaDetalle.getIbcCalculadoSalud().intValue();
            } else {
                return null;
            }
        }
    }

    private BigInteger obtenerIBCvacaciones(GestorProgramaDao gestorProgramaDao, DatosEjecucionRegla obj) {
        try {
            BigDecimal diasVacacionesDisfrutadasMesActual = new BigDecimal(obj.getNominaDetalle().getDiasVacacionesMes().toString());
            //a partir de el "número DOCUMENTO ACTUAL DEL COTIZANTE", se verifica en los meses anteriores, empezando por el más reciente 
            //al más antiguo, cuál de ellos en el campo "número DE DÍAS VACACIONES DISFRUTADAS EN EL MES" es igual a cero (0), 
            //este mes se identifica como el (mes anterior al inicio del disfrute),
            NominaDetalle nominaMesAnteriorDisfrute = gestorProgramaDao.obtenerNominaDetalleAnteriorInicioVacacionesMismoAno(obj.getNominaDetalle());
            //en caso de que hayan varios años traeria el del anterior año, se dejo asi, porque estaba
            //trayendo siempre el año anterior por ordenarlo de menr a mayor pero si hay uno en el mismo mes
            //ese seria el anterior por eso se llama la funcio que no tiene en cuenta el AÑO
            if (nominaMesAnteriorDisfrute == null) {
                //System.out.println("::ANDRES31.0:: vale NULL: " + nominaMesAnteriorDisfrute);
                nominaMesAnteriorDisfrute = gestorProgramaDao.obtenerNominaDetalleAnteriorInicioVacaciones(obj.getNominaDetalle());
            }
            //En caso de no existir registro de nómina del mes anterior
            if (nominaMesAnteriorDisfrute == null) {
                PilaDepurada pilaDepurada;
                //hacer consulta que traiga el menor mes para esta nomina, para este cotizante y menor mes
                //donde dias vacaciones sea mayor a 0
                NominaDetalle verificarAnioAnteriorDisfrute = gestorProgramaDao.obtenerNominaDetalleInicioVacaciones(obj.getNominaDetalle());
                if (verificarAnioAnteriorDisfrute == null) {
                    //se acude a PILA depurada del mes anterior al mes del renglon actual
                    pilaDepurada = gestorProgramaDao.obtenerPilaDepuradaMesAnterior(obj.getNomina(), obj.getNominaDetalle());
                } else {
                    //se acude a PILA depurada haciendo un salto al menor mes donde dias vacaciones sea mayor a 0
                    pilaDepurada = gestorProgramaDao.obtenerPilaDepuradaMesAnterior(obj.getNomina(), verificarAnioAnteriorDisfrute);
                }
                //en caso de no haber mes anterior de PILA,  colocar valor 0 (CERO).
                if (pilaDepurada == null) {
                    return new BigInteger("0");
                } else {
                    BigDecimal ibcSaludMesAnterior = new BigDecimal(pilaDepurada.getIbcSalud().toString()).divide(new BigDecimal(pilaDepurada.getDiasCotSalud().toString()), 2, RoundingMode.HALF_UP).multiply(diasVacacionesDisfrutadasMesActual);
                    return roundValor(ibcSaludMesAnterior).toBigInteger();
                }
            } else {
                //sleep(10000);
                HojaCalculoLiquidacionDetalle liquidacionDetalleNominaMesAnteriorDisfrute = gestorProgramaDao.obtenerHojaCalculoLiqDetalleDeNominaDetalle(nominaMesAnteriorDisfrute);
                BigDecimal totalIbcCalculadoSaludMesAnteriorDisfrute;
                BigDecimal totalDiasReportadosMesAnteriorDisfrute;
                if (obj.getNominaDetalle().getDobleLineaAnterior() == null) {
                    totalIbcCalculadoSaludMesAnteriorDisfrute = new BigDecimal(liquidacionDetalleNominaMesAnteriorDisfrute.getIbcCalculadoSalud());
                    totalDiasReportadosMesAnteriorDisfrute = new BigDecimal(nominaMesAnteriorDisfrute.getTotalDiasReportadosMes().toString());
                } else {
                    totalIbcCalculadoSaludMesAnteriorDisfrute = gestorProgramaDao.sumarIbcCalculadoSaludMesDobleLineaAnterior(obj.getNominaDetalle()); //sumar IBC_CALCULADO_SALUD - HojaCalculoLiquidacionDetalle
                    totalDiasReportadosMesAnteriorDisfrute = gestorProgramaDao.sumarTotalDiasReportadosMesDobleLineaAnterior(obj.getNominaDetalle());  //sumar TOTAL_DIAS_REPORTADOS_MES - nominadetalle
                }
                BigDecimal ibcVacaciones = (totalIbcCalculadoSaludMesAnteriorDisfrute.divide(totalDiasReportadosMesAnteriorDisfrute, 2, RoundingMode.HALF_UP)).multiply(diasVacacionesDisfrutadasMesActual);
                return roundValor(ibcVacaciones).toBigInteger();
            }
        } catch (Exception e) {
            return BigInteger.ZERO;
        }
    }

    private Integer obtenerTotalDiasReportadosMesAnterior(GestorProgramaDao gestorProgramaDao, DatosEjecucionRegla obj) {

        NominaDetalle nomDet = gestorProgramaDao.nominaDetalleAnteriorByNominaDetalleMesAnterior(obj);
        if (nomDet == null) {
            return new Integer("0");
        } else {
            return nomDet.getTotalDiasReportadosMes();
        }
    }

    /**
     * Se hace logica en programacion para aquellas reglas que no tienen formula
     *
     * @param errorTipo
     * @param gestorProgramaDao
     * @param obj
     * @param infoNegocio
     * @param pilaDepurada
     */
    @Override
    public void procesarReglasNoFormula(List<ErrorTipo> errorTipo, GestorProgramaDao gestorProgramaDao, DatosEjecucionRegla obj, Map<String, Object> infoNegocio, PilaDepurada pilaDepurada) {

        // Mayo 26.2016 - Llamado a métodos para manejo de objetos en memoria Caché
        CacheService cacheService = new CacheService();
        cacheService.createInstance();
        cacheService.putAll(CacheService.REGION_COBPARAMGENERAL, gestorProgramaDao.findAll(CobParamGeneral.class));
        Map<String, String> mapMallaval = gestorProgramaDao.obtenerMallaVal(obj.getNominaDetalle());
        BigDecimal tipoIdentificacionAportante = gestorProgramaDao.tipoIdentificacionAportante(obj.getNominaDetalle());
        // Constructor de una nueva clase para ir aislando las reglas que mas se puedan
        NominaModoEjecucionRules nomModEje = new NominaModoEjecucionRules();
        // Regla #1 <IBC PERMISOS REMUNERADOS>
        // Regla #2 <IBC SUSPENSIONES O PERMISOS NO REMUNERADOS>
        // Regla #3 <IBC VACACIONES>
        // Regla #4 <IBC HUELGA>
        // Regla #5 <DIAS_COT_PENSION>
        // Se toma el campo TIP_ACT_NOMINA para ingresar el tipo de acto y nómina que se está liquidando. Esto ayudará para evitar
        // hacer demasiadas consultas.
        String campo = (obj.getNomina().getTipoActo().substring(0, 2)).toUpperCase() + "-" + obj.getNomina().getId();
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIP_ACT_NOMINA" + anyoMesDetalleKey(obj), campo);
        // Se toma el campo AN_COMP_ETAPA_ANT_SALUD para almacenar una "marca" acorde a la combinación de campos TIPO COTIZANTE y SUBTIPO de quien realizó aportes
        // para efectos de reportes y reglas de comparación. Ese campo no se estaba utilizando.
        // Julio 17.2023 - Wilson Rojas. Se coloca 9999 para no tener en cuenta este registro al momento de la comparacion
        //boolean controlReglaComparativa = true;
        //if ("99".equals(obj.getNominaDetalle().getTipoNumeroIdentificacionRealizoAportes()) && "99".equals(obj.getNominaDetalle().getNumeroIdentificacionRealizoAportes())) {
        //infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AN_COMP_ETAPA_ANT_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("9999"));
        //controlReglaComparativa = false;
        //} else {
        //infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AN_COMP_ETAPA_ANT_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        //}
        // WRojas. Ago.10.2022 Se busca la liquidación anterior bien sea <requerimiento> o <ampliacion>. Cuando es requerimiento no aplica
        // Esta línea se colocó a continuación para las reglas comparativas (se hace para comparar la liquidación en línea vs la anterior):
        HojaCalculoLiquidacionDetalle hojaCalLiqDetReqAmpl = null;
        boolean existeLiqAnt = false;
        if ("liquidacion".equals(obj.getNomina().getTipoActo())) {
            hojaCalLiqDetReqAmpl = gestorProgramaDao.getHojaCalculoLiqDetalleSancion(obj.getNomina(), obj.getNominaDetalle());
            existeLiqAnt = true;
        }
        // Se requiere buscar la liquidación generada en el mes anterior de la liquidación en curso
        HojaCalculoLiquidacionDetalle liquidacionDetalleMesAnterior = gestorProgramaDao.obtenerHojaCalculoLiqDetalleMesAnterior(obj.getNomina(), obj.getNominaDetalle(), infoNegocio.get("IDHOJACALCULOLIQUIDACION").toString());
        int diasCotPension = obj.getNominaDetalle().getDiasTrabajadosMes() + obj.getNominaDetalle().getDiasIncapacidadesMes() + obj.getNominaDetalle().getDiasLicenciaRemuneradasMes()
                + obj.getNominaDetalle().getDiasLicenciaMaternidadPaternidadMes() + obj.getNominaDetalle().getDiasVacacionesMes() + obj.getNominaDetalle().getDiasHuelgaLegalMes();
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_PENSION" + anyoMesDetalleKey(obj), diasCotPension);

        // Regla #6 <DIAS_COT_SALUD> 
        int diasCotSalud = diasCotPension + obj.getNominaDetalle().getDiasSuspensionMes();
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_SALUD" + anyoMesDetalleKey(obj), diasCotSalud);
        // Regla #7 <DIAS_COT_RPROF>
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_RPROF" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getDiasTrabajadosMes());
        // Regla #9 <TOTAL PAGOS NO SALARIALES>
        BigDecimal sumValorPagoNoSalarial = gestorProgramaDao.sumaValorLiqConceptoContablePagoNoSalarial(obj.getNomina(), obj.getNominaDetalle());
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj), sumValorPagoNoSalarial);
        // 04.Oct.2019. WR (Req.Trabajadores Independientes)
        // Regla #10 <TOTAL REMUNERADO> y Regla # <TOTAL DEVENGADO> - Regla #11 <PORCENTAJE PAGOS NO SALARIALES>
        BigDecimal sumValorTotalRemunerado = BigDecimal.ZERO;
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_REMUNERADO" + anyoMesDetalleKey(obj), BigDecimal.ZERO);
        BigDecimal sumValorTotalDevengado = BigDecimal.ZERO;
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_DEVENGADO" + anyoMesDetalleKey(obj), BigDecimal.ZERO);
        BigDecimal porPagoNoSalarial = BigDecimal.ZERO;
        // ACuerdo 1035. WROJAS. Abril 26. [si todos los conceptos de nómina que tengan 'TP SALARIAL' son cero, entonces el porcentaje
        // de pagos no salariales también es cero.
        BigDecimal porPagoNoSalarialTMP = gestorProgramaDao.sumaValorLiqConceptoContablePorcentajePagos(obj.getNomina(), obj.getNominaDetalle());
        if (porPagoNoSalarialTMP.compareTo(BigDecimal.ZERO) == 0) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#POR_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        } else {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#POR_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj), porPagoNoSalarial);
        }
        BigDecimal rst = BigDecimal.ZERO;
        BigDecimal resta = BigDecimal.ZERO;
        int diasCotCCF = 0; // Regla #8 <DIAS_COT_CCF>
        // Regla #12 <EXCEDENTE LIMITE DE PAGO NO SALARIAL>
        // 04.Oct.2019. WR (Req.Trabajadores Independientes)
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#EXC_LIM_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        if (!"2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante())) {
            diasCotCCF = obj.getNominaDetalle().getDiasTrabajadosMes() + obj.getNominaDetalle().getDiasVacacionesMes();
            if (!obj.getNominaDetalle().getTipoCotizante().equals("31")) {
                sumValorTotalRemunerado = gestorProgramaDao.sumaValorLiqConceptoContableTotalRemunerado(obj.getNomina(), obj.getNominaDetalle());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_REMUNERADO" + anyoMesDetalleKey(obj), sumValorTotalRemunerado);
                sumValorTotalDevengado = gestorProgramaDao.sumaValorLiqConceptoContableTotalDevengado(obj.getNomina(), obj.getNominaDetalle());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_DEVENGADO" + anyoMesDetalleKey(obj), sumValorTotalDevengado);
            }
            if (sumValorTotalRemunerado.compareTo(BigDecimal.ZERO) != 0) {
                porPagoNoSalarial = sumValorPagoNoSalarial.divide(sumValorTotalRemunerado, 2, RoundingMode.CEILING);
            }
            porPagoNoSalarial = porPagoNoSalarial.multiply(new BigDecimal("100"));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#POR_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj), porPagoNoSalarial);
            // Regla #12 <EXCEDENTE LIMITE DE PAGO NO SALARIAL>
            CobFlex cobFlex = gestorProgramaDao.obtenerCobFlexByFecha(obj.getNominaDetalle());
            BigDecimal por100Dec = new BigDecimal("0");
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#EXC_LIM_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj), por100Dec);
            if (cobFlex != null) {
                por100Dec = new BigDecimal(cobFlex.getPorcentajeFlex());
                por100Dec = por100Dec.divide(new BigDecimal("100"));
                rst = mulValorReglas(por100Dec, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_REMUNERADO" + anyoMesDetalleKey(obj)));
                BigDecimal pagoNoSal = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#POR_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj)));
                if (pagoNoSal.doubleValue() > cobFlex.getPorcentajeFlex().doubleValue()) {
                    resta = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj)), rst);
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#EXC_LIM_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj), resta);
                }
            }
        }
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_CCF" + anyoMesDetalleKey(obj), diasCotCCF);

        // Regla #13 <CODIGO ADMINISTRADORA SALUD> :: Para el caso de Trabajadores Independientes
        // WROJAS. 01.04.2022 - REQ: Artículo 30 -1393
        String codAdmSalud = nomModEje.buscarAdm(obj.getNominaDetalle().getCondEspTrab(), obj.getNominaDetalle().getTipoCotizante(), obj.getNominaDetalle().getSubTipoCotizante());
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_SALUD" + anyoMesDetalleKey(obj), codAdmSalud);
        String nomAdmSalud = LST_ADMINISTRADORA_PILA.get(codAdmSalud);
        // Regla #14 <NOMBRE CORTO ADMINISTRADORA SALUD>
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_SALUD" + anyoMesDetalleKey(obj), nomAdmSalud);

        PilaDepurada pilaDepuradaMesAnterior = gestorProgramaDao.obtenerPilaDepuradaMesAnterior(obj.getNomina(), obj.getNominaDetalle());

        //WROJAS - AJUSTE NOVIEMBRE 22.2019
        // AL VALIDAR <PILA DEPURADA> Para tratar de optimizar el cálculo de las reglas y evitar condicionales.
        if (pilaDepurada != null) {
            // Regla #20 <DIAS COTIZADOS PILA SALUD>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COTIZ_PILA_SALUD" + anyoMesDetalleKey(obj), pilaDepurada.getDiasCotSalud());
            // Regla #21 <IBC PILA SALUD>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PILA_SALUD" + anyoMesDetalleKey(obj), pilaDepurada.getIbcSalud());
            // Regla #22 <TARIFA PILA SALUD>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_SALUD" + anyoMesDetalleKey(obj), pilaDepurada.getTarifaSalud());
            // Regla #23 <COTIZACION PAGADA PILA SALUD>
            if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaSalud().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaSalud().toString()).intValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SALUD" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaSalud().toString());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SALUD" + anyoMesDetalleKey(obj), pilaDepurada.getCotObligatoriaSalud());
                //TK-SD 700817. Regla ajustada para casos mixstos. WR. 30.07.2021
                // Aplica solo para los casos de <independientes>
                if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante())) {
                    if (pilaDepurada.getTipoPlanilla().indexOf('E') >= 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    }
                }
            }
            // Regla #100 <TARIFA PILA ICBF>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_ICBF" + anyoMesDetalleKey(obj), pilaDepurada.getTarifaAportesIcbf());
            String codObsPension = gestorProgramaDao.observacionesNomina(obj.getNominaDetalle(), "OBSERVACIONES_PENSION");
            if (StringUtils.isNotBlank(codObsPension)) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_PENSION" + anyoMesDetalleKey(obj), codObsPension);
                // Regla #29 <NOMBRE CORTO ADMINISTRADORA PENSION>
                String nomAdmPension = LST_ADMINISTRADORA_PILA.get(codObsPension);
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_PENSION" + anyoMesDetalleKey(obj), nomAdmPension);
            }
            // Regla #34 <DIAS COTIZADOS PILA PENSION>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COTIZ_PILA_PENSION" + anyoMesDetalleKey(obj), pilaDepurada.getDiasCotPension());
            // Regla #35 <IBC PILA PENSION>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PILA_PENSION" + anyoMesDetalleKey(obj), pilaDepurada.getIbcPension());
            // Regla #36 <TARIFA PILA PENSION>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_PENSION" + anyoMesDetalleKey(obj), pilaDepurada.getTarifaPension());
            // Regla #37 <COTIZACION PAGADA PILA PENSION>
            if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaPension().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaPension().toString()).intValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaPension().toString());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj), pilaDepurada.getAporteCotObligatoriaPension());
                //TK-SD 700817. Regla ajustada para casos mixstos. WR. 30.07.2021
                // Aplica solo para <independientes>
                if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante())) {
                    if (pilaDepurada.getTipoPlanilla().indexOf('E') >= 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    }
                }
            }
            // Regla #46 <COTIZACION PAGADA PILA FSP SUBCUENTA DE SOLIDARIDAD>
            if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaFspSolid().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaFspSolid().toString()).intValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SOLIDAR" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaFspSolid().toString());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SOLIDAR" + anyoMesDetalleKey(obj), pilaDepurada.getAporteFsolidPensionalSolidaridad());
                //TK-SD 700817. Regla ajustada para casos mixstos. WR. 30.07.2021
                // Aplica solo para <independientes>
                if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante())) {
                    if (pilaDepurada.getTipoPlanilla().indexOf('E') >= 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SOLIDAR" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    }
                }
            }
            // Regla #47 <COTIZACION PAGADA PILA FSP SUBCUENTA DE SUBSISTENCIA>
            if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaFspSubsis().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaFspSubsis().toString()).intValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SUBSIS" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaFspSubsis().toString());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SUBSIS" + anyoMesDetalleKey(obj), pilaDepurada.getAporteFsolidPensionalSubsistencia());
                //TK-SD 700817. Regla ajustada para casos mixstos. WR. 30.07.2021
                // Aplica solo para <independientes>
                if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante())) {
                    if (pilaDepurada.getTipoPlanilla().indexOf('E') >= 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SUBSIS" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    }
                }
            }
            // Regla #55 <TARIFA PILA PENSION ADICIONAL ACT. ALTO RIESGO>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TAR_PILA_PENSION_ACT_ALTORIES" + anyoMesDetalleKey(obj), pilaDepurada.getTariPilaPensAdAltoRiesgo());
            // Regla #62 <CODIGO ADMINISTRADORA ARL>
            if (pilaDepurada.getCodigoArp() != null) {
                if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante()) && (pilaDepurada.getTipoPlanilla().indexOf('E') >= 0)) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_ARL" + anyoMesDetalleKey(obj), null);
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_ARL" + anyoMesDetalleKey(obj), null);
                } else {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_ARL" + anyoMesDetalleKey(obj), pilaDepurada.getCodigoArp());
                    // Regla #63 <NOMBRE CORTO ADMINISTRADORA ARL>
                    String nomAdmArl = LST_ADMINISTRADORA_PILA.get(pilaDepurada.getCodigoArp());
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_ARL" + anyoMesDetalleKey(obj), nomAdmArl);
                }
            }
            // Regla #67 <DIAS COTIZADOS PILA ARL>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COTIZ_PILA_ARL" + anyoMesDetalleKey(obj), pilaDepurada.getDiasCotRprof());
            // Regla #68 <IBC PILA ARL>  
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PILA_ARL" + anyoMesDetalleKey(obj), pilaDepurada.getIbcRprof());
            // Regla #69 <TARIFA PILA ARL> 
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_ARL" + anyoMesDetalleKey(obj), pilaDepurada.getTarifaCentroTrabajo());
            // Regla #80 <DIAS COTIZADOS PILA CCF>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COTI_PILA_CCF" + anyoMesDetalleKey(obj), pilaDepurada.getDiasCotCcf());
            // Regla #81 <IBC PILA CCF>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PILA_CCF" + anyoMesDetalleKey(obj), pilaDepurada.getIbcCcf());
            // Regla #82 <TARIFA PILA CCF>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_CCF" + anyoMesDetalleKey(obj), pilaDepurada.getTarifaAportesCcf());
            // Regla #91 <TARIFA PILA SENA>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_SENA" + anyoMesDetalleKey(obj), pilaDepurada.getTarifaAportesSena());
            // Regla #106 <PLANILLA PILA CARGADA>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PLANILLA_PILA_CARGADA" + anyoMesDetalleKey(obj), pilaDepurada.getPlanilla());
            // Regla #54 <COTIZACION OBLIGATORIA ADICIONAL ACT. ALTO RIESGO>
            if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante()) && (pilaDepurada.getTipoPlanilla().indexOf('E') >= 0)) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO" + anyoMesDetalleKey(obj), new Integer("0"));
            }
            //Regla #65 <TARIFA ARL>
            Float valorTarifaCentroTrabajo = gestorProgramaDao.obtenerTarifaCentroTrabajoPilaDepuradaNominaDetalle(obj.getNomina(), obj.getNominaDetalle());
            if (valorTarifaCentroTrabajo.compareTo(0f) == 0) {    //toma la tarifa mas alta
                Float valorMaximoTarifaCentroTrabajo = gestorProgramaDao.obtenerMaximaTarifaCentroTrabajoPilaDepuradaNominaDetalle(obj.getNomina(), obj.getNominaDetalle());
                if (valorMaximoTarifaCentroTrabajo.compareTo(0f) == 0) {
                    if (pilaDepurada.getTarifaMaxima() != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ARL" + anyoMesDetalleKey(obj), Double.valueOf(pilaDepurada.getTarifaMaxima()));
                    } else {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ARL" + anyoMesDetalleKey(obj), pilaDepurada.getTarifaMaxima());
                    }
                } else {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ARL" + anyoMesDetalleKey(obj), valorMaximoTarifaCentroTrabajo);
                }
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ARL" + anyoMesDetalleKey(obj), valorTarifaCentroTrabajo);
            }
        } else { // Ingresa al ELSE cuando PilaDepurada es NULL
            // Regla #37 <COTIZACION PAGADA PILA PENSION>
            if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaPension().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaPension().toString()).intValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaPension().toString());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            }
            // Regla #23 <COTIZACION PAGADA PILA SALUD>
            if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaSalud().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaSalud().toString()).intValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SALUD" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaSalud().toString());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            }
            // WROjas. Se incluyen estas reglas de acuerdo al análisis hecho con FABIO
            // Regla #46 <COTIZACION PAGADA PILA FSP SUBCUENTA DE SOLIDARIDAD>
            if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaFspSolid().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaFspSolid().toString()).intValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SOLIDAR" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaFspSolid().toString());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SOLIDAR" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            }
            // Regla #47 <COTIZACION PAGADA PILA FSP SUBCUENTA DE SUBSISTENCIA>
            if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaFspSubsis().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaFspSubsis().toString()).intValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SUBSIS" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaFspSubsis().toString());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SUBSIS" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            }
            // Regla #55 <TARIFA PILA PENSION ADICIONAL ACT. ALTO RIESGO>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TAR_PILA_PENSION_ACT_ALTORIES" + anyoMesDetalleKey(obj), new Integer("0"));
            // Regla #62 <CODIGO ADMINISTRADORA ARL> si piladepurada=null 
            // OJO: Wilson Rojas esta línea se ha colocado al inicio del método para cobijar todo el proceso
            // Agosto 10.2022. Esto para realizar el análisis comparativo.
            //HojaCalculoLiquidacionDetalle liquidacionDetalleMesAnterior = gestorProgramaDao.obtenerHojaCalculoLiqDetalleMesAnterior(obj.getNomina(), obj.getNominaDetalle(), infoNegocio.get("IDHOJACALCULOLIQUIDACION").toString());
            if (liquidacionDetalleMesAnterior != null && StringUtils.isNotBlank(liquidacionDetalleMesAnterior.getCodAdmArl())) {
                //System.out.println("::ANDRES1:: ARL " + liquidacionDetalleMesAnterior.getCodAdmSalud());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_ARL" + anyoMesDetalleKey(obj), liquidacionDetalleMesAnterior.getCodAdmArl());
                // Regla #63 <NOMBRE CORTO ADMINISTRADORA ARL>  si piladepurada=null 
                String nomAdmArl = LST_ADMINISTRADORA_PILA.get(liquidacionDetalleMesAnterior.getCodAdmArl());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_ARL" + anyoMesDetalleKey(obj), nomAdmArl);
            } else {
                //se debe realizar la búsqueda con el número DOCUMENTO CON EL QUE REALIZO APORTES DEL COTIZANTE en la PILA DEPURADA 
                //del aportante en el mes inmediatamente anterior se acude a PILA depurada del mes anterior al mes del renglon actual
                //PilaDepurada pilaDepuradaMesAnterior = gestorProgramaDao.obtenerPilaDepuradaMesAnterior(obj.getNomina(), obj.getNominaDetalle());
                //System.out.println("::ANDRES86:: getNumeroIdentificacion: " + obj.getNominaDetalle().getNumeroIdentificacionActual());
                if (pilaDepuradaMesAnterior != null) {
                    //System.out.println("::ANDRES2:: ARL pilaDepuradaMesAnterior: " + pilaDepuradaMesAnterior.getId());
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_ARL" + anyoMesDetalleKey(obj), pilaDepuradaMesAnterior.getCodigoArp());
                    String nomAdmArl = LST_ADMINISTRADORA_PILA.get(pilaDepuradaMesAnterior.getCodigoArp());
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_ARL" + anyoMesDetalleKey(obj), nomAdmArl);
                } else {
                    //En caso de no existir registros del mes fiscalizado en el PILA DEPURADA se debe traer la información registrada en la columna
                    //"OBSERVACIONES APORTANTE ARL"
                    String codigoObservacionArl = gestorProgramaDao.observacionesNomina(obj.getNominaDetalle(), "OBSERVACIONES_ARL");
                    //System.out.println("::ANDRES3:: ARL codigoObservacionSalud: " + codigoObservacionArl);
                    if (codigoObservacionArl != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_ARL" + anyoMesDetalleKey(obj), codigoObservacionArl);
                        String nomAdmArl = LST_ADMINISTRADORA_PILA.get(codigoObservacionArl);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_ARL" + anyoMesDetalleKey(obj), nomAdmArl);
                    } else {
                        //En caso de no existir información en PILA DEPURADA ni tampoco en la columna "OBSERVACIONES APORTANTE ARL" se deja vacío.
                        // Ahora se realiza un ajuste por Art.30 ley1393 / 2010. WRojas.26.04.2022
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_ARL" + anyoMesDetalleKey(obj), "ARL001");
                        String nomAdmArl = LST_ADMINISTRADORA_PILA.get("ARL001");
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_ARL" + anyoMesDetalleKey(obj), nomAdmArl);
                    }
                }
            } // FIN EVALUACIÓN REGLA # 62 CUANDO PILA DEPURADA ES NULL
            //Regla #75 <CODIGO ADMINISTRADORA CCF>  si piladepurada=null 
            if (liquidacionDetalleMesAnterior != null && StringUtils.isNotBlank(liquidacionDetalleMesAnterior.getCodAdmCcf())) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_CCF" + anyoMesDetalleKey(obj), liquidacionDetalleMesAnterior.getCodAdmCcf());
                //Regla #76 <NOMBRE CORTO ADMINISTRADORA CCF>  si piladepurada=null 
                String nomAdmCcf = LST_ADMINISTRADORA_PILA.get(liquidacionDetalleMesAnterior.getCodAdmCcf());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_CCF" + anyoMesDetalleKey(obj), nomAdmCcf);
            } else {
                //se debe realizar la búsqueda con el  (número DOCUMENTO CON EL QUE REALIZO APORTES DEL COTIZANTE)en el PILA DEPURADA del aportante
                //en el mes inmediatamente anterior se acude a PILA depurada del mes anterior al mes del renglon actual
                //PilaDepurada pilaDepuradaMesAnterior = gestorProgramaDao.obtenerPilaDepuradaMesAnterior(obj.getNomina(), obj.getNominaDetalle());
                if (pilaDepuradaMesAnterior != null) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_CCF" + anyoMesDetalleKey(obj), pilaDepuradaMesAnterior.getCodigoCCF());
                    String nomAdmCcf = LST_ADMINISTRADORA_PILA.get(pilaDepuradaMesAnterior.getCodigoCCF());
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_CCF" + anyoMesDetalleKey(obj), nomAdmCcf);
                } else {
                    //En caso de no existir registros del mes fiscalizado en el PILA DEPURADA se debe traer la información registrada en la columna
                    //"OBSERVACIONES APORTANTE CCF"
                    String codigoObservacionCCF = gestorProgramaDao.observacionesNomina(obj.getNominaDetalle(), "OBSERVACIONES_CCF");
                    if (codigoObservacionCCF != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_CCF" + anyoMesDetalleKey(obj), codigoObservacionCCF);
                        //System.out.println("::ANDRES12:: getNominaDetalle: " + obj.getNominaDetalle().getId() + " MES: " + anyoMesDetalleKey(obj) + " CCF: " + codigoObservacionCCF);
                        String nomAdmCcf = LST_ADMINISTRADORA_PILA.get(codigoObservacionCCF);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_CCF" + anyoMesDetalleKey(obj), nomAdmCcf);
                    }
                    //En caso de no existir información en PILA DEPURADA ni tampoco en la columna "OBSERVACIONES APORTANTE CCF" se deja vacío.
                }
            } // FIN REGLA # 75 CUANDO PILA DEPURADA ES NULL
            // Regla #28 <CODIGO ADMINISTRADORA PENSION> si piladepurada=null
            // OJO Con esta regla, se aplica independientemente de PILA DEPURADA
            String codObsPension = gestorProgramaDao.observacionesNomina(obj.getNominaDetalle(), "OBSERVACIONES_PENSION");
            if (StringUtils.isNotBlank(codObsPension)) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_PENSION" + anyoMesDetalleKey(obj), codObsPension);
                // Regla #29 <NOMBRE CORTO ADMINISTRADORA PENSION> si piladepurada=null 
                String nomAdmCcf = LST_ADMINISTRADORA_PILA.get(codObsPension);
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_PENSION" + anyoMesDetalleKey(obj), nomAdmCcf);
            }
            // Regla #69 <TARIFA PILA ARL>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            // Regla #80 <DIAS COTIZADOS PILA CCF>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COTI_PILA_CCF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            //Regla #81 <IBC PILA CCF>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PILA_CCF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            // Regla #82 <TARIFA PILA CCF>
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_CCF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            //Regla #65 <TARIFA ARL>
            Float valorMaximoTarifaCentroTrabajo = gestorProgramaDao.obtenerMaximaTarifaCentroTrabajoPilaDepuradaNominaDetalle(obj.getNomina(), obj.getNominaDetalle());
            if (valorMaximoTarifaCentroTrabajo.compareTo(0f) == 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ARL" + anyoMesDetalleKey(obj), gestorProgramaDao.obtenerMaximaTarifaAportante(obj.getNomina(), obj.getNominaDetalle().getAno().toString()));
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ARL" + anyoMesDetalleKey(obj), valorMaximoTarifaCentroTrabajo);
            }
        } // TERMINA VALIDACION para <PilaDepurada>

        // Regla #15 <IBC PAGOS EN NOMINA SALUD>
        BigDecimal sumNOTpIncapacidad = gestorProgramaDao.sumaValorLiqConceptoContableIbcPagosNomSaludNoTpIncapacidad(obj.getNomina(), obj.getNominaDetalle(), "1");
        sumNOTpIncapacidad = sumValorReglas(sumNOTpIncapacidad, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#EXC_LIM_PAGO_NO_SALARIAL"
                + anyoMesDetalleKey(obj)));
        BigDecimal sumTpIncapacidad = gestorProgramaDao.sumaValorLiqConceptoContableIbcPagosNomSalud(obj.getNomina(), obj.getNominaDetalle());
        if (StringUtils.containsIgnoreCase("X", obj.getNominaDetalle().getSalarioIntegral())) {
            BigDecimal por70 = sumNOTpIncapacidad.multiply(new BigDecimal("70"));// FIXME valor quemado
            sumNOTpIncapacidad = por70.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP); // Linea nueva
        }
        sumNOTpIncapacidad = sumNOTpIncapacidad.add(sumTpIncapacidad);
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PAGOS_NOM_SALUD" + anyoMesDetalleKey(obj), roundValor(sumNOTpIncapacidad));
        // Regla #16 <TOTAL IBC CALCULADO SALUD>
        cobParamGeneral = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "SMMLV" + obj.getNominaDetalle().getAno().toString());
        BigDecimal salarioMinimo = new BigDecimal(cobParamGeneral.getValor().toString());
        
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaNomina = null;
        String mesNomina = obtenerMesNomina(obj.getNominaDetalle().getMes().intValue());
        try {
            fechaNomina = formateador.parse("01/" + mesNomina + "/" + obj.getNominaDetalle().getAno());
        } catch (ParseException ex) {
            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String tieneSalud = gestorProgramaDao.tieneSaludCotizante(obj.getNominaDetalle());
        if (null == tieneSalud) {
            try {//System.out.println("::ERROR ANDRES44:: No encontro codigo tipo cotizante." + tieneSalud)
                ErrorTipo errorObj = new ErrorTipo();
                errorObj.setCodError("IBC_CALCULADO_SALUD");
                errorObj.setValDescError("EXCEPTION ERROR procesarReglasNoFormula: No encontro tipo cotizante en la malla del liquidador Nominadetalle = " + obj.getNominaDetalle().getId());
                errorTipo.add(errorObj);
                throw new Exception("EXCEPTION ERROR procesarReglasNoFormula: No encontro tipo cotizante en la malla del liquidador Nominadetalle = " + obj.getNominaDetalle().getId());
            } catch (Exception ex) {
                Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            switch (tieneSalud) {
                case "NO":
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    break;
                default:
                    // WROJAS - Oct.04.2019 RQ: Trabajadores Independientes.
                    if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante())) {
                        // Se busca el total del ingreso bruto - Tipo de pago
                        boolean seCumpleIBC = false;
                        BigDecimal sumIngresoBruto = gestorProgramaDao.sumaValorLiqConceptoContableTotalIBCCalculadoSaludIngresoBruto(obj.getNomina(), obj.getNominaDetalle());
                        // REQ.artículo 244 ley 1995 de 25.05.2019. Implementado 14.02.2022
                        BigDecimal sumIngresoNeto = gestorProgramaDao.sumaValorLiqConceptoContableTotalIBCCalculadoSaludIngresoNeto(obj.getNomina(), obj.getNominaDetalle());
                        //SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date fechaControlTrabIndep1 = formateador.parse("30/06/2015");
                            Date fechaControlTrabIndep2 = formateador.parse("30/06/2015");
                            // REQ.artículo 244 ley 1995 de 25.05.2019. Implementado 14.02.2022
                            Date fechaControlTrabIndep3 = formateador.parse("31/05/2019"); // Se realiza ajuste para que se cumple >= junio de 2019. 31.03.2022
                            Date fechaRegistroTrabIndep = formateador.parse("01/" + obj.getNominaDetalle().getMes() + "/" + obj.getNominaDetalle().getAno());
                            if (fechaRegistroTrabIndep.before(fechaControlTrabIndep1) && sumIngresoBruto.doubleValue() <= 0) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                seCumpleIBC = true;
                            }
                            if (fechaRegistroTrabIndep.after(fechaControlTrabIndep2) && sumIngresoBruto.doubleValue() < salarioMinimo.doubleValue() && fechaRegistroTrabIndep.before(fechaControlTrabIndep3)) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                seCumpleIBC = true;
                            }
                            if (fechaRegistroTrabIndep.after(fechaControlTrabIndep3) && sumIngresoNeto.doubleValue() < salarioMinimo.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                seCumpleIBC = true;
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        // WROjas - 28.Agosto.2023. Se quita la verificación del mes de enero y se colocó para jurídicas
                        if (!seCumpleIBC) { // Si ninguna de las dos condiciones anteriores se cumple, se verifican los topes
                            BigDecimal sumatoria = sumValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PAGOS_NOM_SALUD" + anyoMesDetalleKey(obj)),
                                    infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PERMISOS_REMUNERADOS" + anyoMesDetalleKey(obj)),
                                    infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SUSP_PERMISOS" + anyoMesDetalleKey(obj)),
                                    infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_VACACIONES" + anyoMesDetalleKey(obj)),
                                    infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_HUELGA" + anyoMesDetalleKey(obj)));

                            CobParamGeneral cobParamGeneral1 = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "MAXIBCSALUD" + obj.getNominaDetalle().getAno().toString());
                            BigDecimal topeIbcSalud = new BigDecimal(cobParamGeneral1.getValor().toString());

                            BigDecimal mulSmmlIbcTope = topeIbcSalud.multiply(salarioMinimo);
                            if (obj.getNominaDetalle().getIbcConcurrenciaIngresosOtrosAportantes().doubleValue() >= mulSmmlIbcTope.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            } else {
                                BigDecimal nuevaSumatoria = sumatoria.add(obj.getNominaDetalle().getIbcConcurrenciaIngresosOtrosAportantes());
                                BigDecimal diaSmml = salarioMinimo.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
                                BigDecimal diasCotSaluddiaSmml = mulValorReglas(diaSmml, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_SALUD" + anyoMesDetalleKey(obj)));
                                BigDecimal valorResultado = new BigDecimal("0");
                                if (nuevaSumatoria.doubleValue() <= mulSmmlIbcTope.doubleValue()) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(sumatoria));
                                    valorResultado = sumatoria;
                                } else {
                                    resta = mulSmmlIbcTope.subtract(obj.getNominaDetalle().getIbcConcurrenciaIngresosOtrosAportantes());
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(resta));
                                    valorResultado = resta;
                                }
                                if (valorResultado.doubleValue() < salarioMinimo.doubleValue()) {
                                    if (valorResultado.doubleValue() < diasCotSaluddiaSmml.doubleValue()) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(diasCotSaluddiaSmml));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(valorResultado));
                                    }
                                }
                            }
                        }  // FInal del condicional que ingresa cuando NO se cumplen las primeras dos condiciones de INDEPENDIENTES
                        // y a la anterior terminación se agregó un nuevo caso de indendientes.
                    } else { // SI NO es Trabajador Independiente, se deje como estaba
                        // 28 agosto.2023. Se verifica si es el mes de enero. Cambio autorizado por FLópez. Antes solo estaba para independientes
                        if (obj.getNominaDetalle().getMes().intValue() == 1) {
                            int anoAnt = obj.getNominaDetalle().getAno().intValue() - 1;
                            cobParamGeneral = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "SMMLV" + anoAnt);
                            salarioMinimo = new BigDecimal(cobParamGeneral.getValor().toString());
                        }
                        BigDecimal sumatoria = sumValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PAGOS_NOM_SALUD" + anyoMesDetalleKey(obj)),
                                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PERMISOS_REMUNERADOS" + anyoMesDetalleKey(obj)),
                                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SUSP_PERMISOS" + anyoMesDetalleKey(obj)),
                                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_VACACIONES" + anyoMesDetalleKey(obj)),
                                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_HUELGA" + anyoMesDetalleKey(obj)));
                        CobParamGeneral cobParamGeneral1 = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "MAXIBCSALUD" + obj.getNominaDetalle().getAno().toString());
                        BigDecimal topeIbcSalud = new BigDecimal("0");
                        if (cobParamGeneral1 != null && cobParamGeneral1.getValor() != 0) {
                            topeIbcSalud = new BigDecimal(cobParamGeneral1.getValor().toString());
                        }
                        BigDecimal mulSmmlIbcTope = topeIbcSalud.multiply(salarioMinimo);
                        if (obj.getNominaDetalle().getIbcConcurrenciaIngresosOtrosAportantes().doubleValue() >= mulSmmlIbcTope.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        } else {
                            BigDecimal nuevaSumatoria = sumatoria.add(obj.getNominaDetalle().getIbcConcurrenciaIngresosOtrosAportantes());
                            BigDecimal diaSmml = salarioMinimo.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
                            BigDecimal diasCotSaluddiaSmml = mulValorReglas(diaSmml, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_SALUD" + anyoMesDetalleKey(obj)));
                            BigDecimal valorResultado = new BigDecimal("0");
                            if (nuevaSumatoria.doubleValue() <= mulSmmlIbcTope.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(sumatoria));
                                valorResultado = sumatoria;
                            } else {
                                resta = mulSmmlIbcTope.subtract(obj.getNominaDetalle().getIbcConcurrenciaIngresosOtrosAportantes());
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(resta));
                                valorResultado = resta;
                            }
                            if (valorResultado.doubleValue() < salarioMinimo.doubleValue()) {
                                if (valorResultado.doubleValue() < diasCotSaluddiaSmml.doubleValue()) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(diasCotSaluddiaSmml));
                                } else {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(valorResultado));
                                }
                            }
                            if ("21".equals(obj.getNominaDetalle().getTipoCotizante()) || "20".equals(obj.getNominaDetalle().getTipoCotizante())
                                    || "19".equals(obj.getNominaDetalle().getTipoCotizante()) || "12".equals(obj.getNominaDetalle().getTipoCotizante())) {
                                BigDecimal rstMulDia = mulValorReglas(diaSmml, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_SALUD"
                                        + anyoMesDetalleKey(obj)));
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(rstMulDia));
                            }
                        }
                    }
                    // Si el <aportante> es "2" se hace este bloque de lo contrario se mantiene el cálculo realizado.
                    break;
            }
        }
        
        // Regla #17 <TARIFA SALUD>
        BigDecimal cantidadEmp = gestorProgramaDao.obtenerEmpleadoPeriodo(obj.getNominaDetalle());
        
        cobParamGeneral = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "SMMLV" + obj.getNominaDetalle().getAno().toString());
        salarioMinimo = new BigDecimal(cobParamGeneral.getValor().toString());
        BigDecimal smml10_local = salarioMinimo.multiply(new BigDecimal("10"));
        try {
            Date fechaCree = formateador.parse("01/11/2013");
            Date fechaRegistro = formateador.parse("01/" + obj.getNominaDetalle().getMes() + "/" + obj.getNominaDetalle().getAno());
            BigDecimal salarioDevengado_local = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_DEVENGADO" + anyoMesDetalleKey(obj)));
            BigDecimal tarifaSalud = gestorProgramaDao.tarifaSalud(obj.getNomina(), obj.getNominaDetalle());
            // WROJAS - Art.30 Ley 1393 se agregó el <tipo cotizante> = 31
            if ("21".equals(obj.getNominaDetalle().getTipoCotizante()) || "20".equals(obj.getNominaDetalle().getTipoCotizante())
                    || "12".equals(obj.getNominaDetalle().getTipoCotizante()) || "19".equals(obj.getNominaDetalle().getTipoCotizante())
                    || "31".equals(obj.getNominaDetalle().getTipoCotizante())) {
                //System.out.println("::ANDRES1:: ingrese a CREE TARIFA_SALUD: " + tarifaSalud);
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD" + anyoMesDetalleKey(obj), tarifaSalud);
            } else {// ultimo cambio hablado con Arley
                //Para los demás "TIPO COTIZANTE" el % debe ser la tarifa % de Salud del trabajador más + tarifa % de Salud del 
                //empleador  registradas, en el módulo del CORE ""Asociar Subsistema""  de la sección ""Administrar parámetros generales 2""
                //aplicable al año y mes que se está fiscalizando teniendo en cuenta las fechas de inicio y fin de cada tarifa.
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD" + anyoMesDetalleKey(obj), tarifaSalud);
                // WROJAS - Art.30 Ley 1393 se agregó el siguiente condicional
                if (tipoIdentificacionAportante.intValue() == 5 && "1".equals(obj.getNominaDetalle().getTipoCotizante())
                        && salarioDevengado_local.doubleValue() < smml10_local.doubleValue()) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("4"));
                } else {
                    //como lo del CRRE que son las dos excepciones siguientes tienen prioridad se deben dejar de ultimas es decir que 
                    //el CREE tiene prioridad sobre la ley 1429. Excepcion 3
                    //Para definir el valor ""devengado"" se tendrá en cuenta el valor registrado en el campo ""TOTAL DEVENGADO""
                    //3- Si en la columna ""CONDICION ESPECIAL DE EMPRESA""  aparece una de las siguientes condiciones, aplicar la tarifa de acuerdo a la siguiente lista:
                    //_______________________________________________________________________________________ 
                    //·  “LEY 1429 Col AÑO 1,2”                =   11%
                    //·  “LEY 1429 Col AÑO 3”                  =   11,38%
                    //·  “LEY 1429 Col AÑO 4”                  =   11,75%
                    //·  “LEY 1429 Col AÑO 5”                  =   12,13%
                    //·  “LEY 1429 AGV AÑO 1 – 8”              =   11%
                    //·  “LEY 1429 AGV AÑO 9”                  =   11,75%
                    //·  “LEY 1429 AGV AÑO 10”                 =   12,13% ,
                    //·  “Soc.declaradas ZF. Art20 Ley1607”    =   12,5%
                    //_______________________________________________________________________________________ 
                    BigDecimal res = nomModEje.tarifaSalud(obj.getNominaDetalle().getCondEspEmp());
                    if (res.compareTo(BigDecimal.ZERO) != 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD" + anyoMesDetalleKey(obj), res);
                    }
                    if ("SI".equals(obj.getNominaDetalle().getIdaportante().getSujetoPasivoImpuestoCree())
                            && tipoIdentificacionAportante.intValue() == 2) {
                        Date fecha1 = formateador.parse("30/11/2013"); // entre diciembre 2013
                        Date fecha2 = formateador.parse("01/01/2017"); // diciembre 2016
                        Date fecha3 = formateador.parse("31/12/2016"); // a partir de enero 2017
                        //excepcion 1
                        //1- Por efectos de la Ley 1607 de 2012, si el campo “SUJETO PASIVO DEL IMPUESTO SOBRE LA RENTA PARA LA EQUIDAD CREE” 
                        //está marcado con ""SI"", el campo ""TIPO DOCUMENTO APORTANTE"" es igual a ""NI""  y  el periodo fiscalizado es posterior 
                        //a noviembre de 2013, se aplicara la tarifa del 4%  a los trabajadores que ""devenguen"", individualmente considerados, 
                        //hasta diez (10) salarios mínimos mensuales legales vigentes.
                        if (fechaRegistro.after(fecha1) && fechaRegistro.before(fecha2) && salarioDevengado_local.doubleValue() <= smml10_local.doubleValue()) {
                            //if (fechaRegistro.after(fecha1) && salarioDevengado_local.doubleValue() <= smml10_local.doubleValue()) {                        
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("4"));
                        }
                        //1- Si el campo “SUJETO PASIVO DEL IMPUESTO SOBRE LA RENTA PARA LA EQUIDAD CREE” 
                        //está marcado con "SI" y  el campo "TIPO DOCUMENTO APORTANTE" es igual a "NI", se aplicará 
                        //la tarifa del 4% así:
                        //a- entre diciembre 2013  y  diciembre 2016, a trabajadores que "devenguen", individualmente considerados, hasta diez (10) SMLMV (Ley 1607/2012)
                        //b- a partir de enero 2017 a trabajadores que "devenguen", individualmente considerados, menos de diez (10) SMLMV (Ley 1819/2016)
                        if (fechaRegistro.after(fecha3) && salarioDevengado_local.doubleValue() < smml10_local.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("4"));
                        }
                    }
                    //excepcion 2
                    //2- si el campo “SUJETO PASIVO DEL IMPUESTO SOBRE LA RENTA PARA LA EQUIDAD CREE” está marcado con ""SI"", el campo ""TIPO DOCUMENTO APORTANTE"" es diferente a ""NI"",  
                    //el periodo fiscalizado es posterior a noviembre de 2013, y existe mas de un trabajador 
                    //en el mes de la nomina que se esta fiscalizando se aplicara la tarifa del 4%  a los trabajadores
                    //que ""devenguen"", individualmente considerados, menos de diez (10) salarios mínimos
                    //mensuales legales vigentes.
                    if ("SI".equals(obj.getNominaDetalle().getIdaportante().getSujetoPasivoImpuestoCree()) && tipoIdentificacionAportante.intValue() != 2
                            && fechaRegistro.after(fechaCree) && cantidadEmp.compareTo(BigDecimal.ONE) > 1
                            && salarioDevengado_local.doubleValue() < smml10_local.doubleValue()) {
                        //System.out.println("::ANDRES16:: ingrese a CREE TARIFA_SALUD: " + new BigDecimal("4"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD" + anyoMesDetalleKey(obj), new BigDecimal("4"));
                    }
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Regla #18 <TARIFA SALUD PARA SUSPENSIONES>
        Date fechaCree1 = null;
        Date fechaCree2 = null;
        Date fechaCree3 = null;
        /*
        String mesNomina = obtenerMesNomina(obj.getNominaDetalle().getMes().intValue());
        Date fechaNomina = null;
        try {
            fechaNomina = formateador.parse("01/" + mesNomina + "/" + obj.getNominaDetalle().getAno());
        } catch (ParseException ex) {
            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        BigDecimal tarifaSaludEmpleador = gestorProgramaDao.tarifaSaludEmpleador(obj.getNomina(), obj.getNominaDetalle());
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD_SUSPENSION" + anyoMesDetalleKey(obj), tarifaSaludEmpleador);
        BigDecimal salarioDevengado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_REMUNERADO" + anyoMesDetalleKey(obj)));
        // SAlario Minimo buscado en Cache
        //cobParamGeneral = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "SMMLV" + obj.getNominaDetalle().getAno().toString());
        //BigDecimal rstSmml = new BigDecimal("0");
        //if (cobParamGeneral != null && cobParamGeneral.getValor() != 0) {
            //rstSmml = new BigDecimal(cobParamGeneral.getValor().toString());
        //}
        BigDecimal smml10 = salarioMinimo.multiply(new BigDecimal("10"));
        // WROJAS. Art. 30 Ley 1393 / 2010
        if (tipoIdentificacionAportante.intValue() == 5 && "1".equals(obj.getNominaDetalle().getTipoCotizante()) && salarioDevengado.doubleValue() <= smml10.doubleValue()) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD_SUSPENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        } else {
            //CASO CREE
            try {
                fechaCree1 = formateador.parse("30/11/2013");
                if ("SI".equals(obj.getNominaDetalle().getIdaportante().getSujetoPasivoImpuestoCree())) {
                    if (tipoIdentificacionAportante.intValue() == 2) {
                        //periodo fiscalizado es posterior a noviembre de 2013
                        //fechaCree1 = formateador.parse("30/11/2013");
                        if (fechaNomina != null && fechaNomina.after(fechaCree1) && salarioDevengado.doubleValue() <= smml10.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD_SUSPENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            //System.out.println("::ANDRES93:: getSujetoPasivoImpuestoCree : " + 0);
                        }
                    } else {
                        //eperiodo fiscalizado es posterior a noviembre de 2013
                        //fechaCree1 = formateador.parse("30/11/2013");
                        if (fechaNomina != null && fechaNomina.after(fechaCree1) && cantidadEmp.compareTo(BigDecimal.ONE) == 1 && salarioDevengado.doubleValue() <= smml10.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD_SUSPENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        }
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Regla #19 <COTIZACION OBLIGATORIA CALCULADA SALUD>
        BigDecimal tmp1 = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SUSP_PERMISOS" + anyoMesDetalleKey(obj)));
        BigDecimal tmp2 = mulValorReglas(tmp1, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD" + anyoMesDetalleKey(obj)));
        BigDecimal resulTmp1 = tmp2.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal multTmpTarifaSusp = mulValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SUSP_PERMISOS"
                + anyoMesDetalleKey(obj)), infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SALUD_SUSPENSION" + anyoMesDetalleKey(obj)));
        BigDecimal resulTmp2 = multTmpTarifaSusp.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = resulTmp2.add(resulTmp1);
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj), roundValor100(total));
        
        // Reglas comparativas - SUBSISTEMA DE SALUD
        if (existeLiqAnt) {
            BigDecimal ibcCalSaluACT = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj)));
            if (hojaCalLiqDetReqAmpl.getIbcCalculadoSalud().doubleValue() < ibcCalSaluACT.doubleValue()) {
                BigDecimal rstACT = new BigDecimal(hojaCalLiqDetReqAmpl.getIbcCalculadoSalud());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_SALUD" + anyoMesDetalleKey(obj), roundValor(rstACT));
            }
            BigDecimal cotizOblCalculadoSaludACT = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj)));
            if (hojaCalLiqDetReqAmpl.getCotizOblCalculadaSalud().doubleValue() < cotizOblCalculadoSaludACT.doubleValue()) {
                BigDecimal rstACT = new BigDecimal(hojaCalLiqDetReqAmpl.getCotizOblCalculadaSalud());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj), roundValor(rstACT));
            }
            if (hojaCalLiqDetReqAmpl.getIbcPagosNomSalud().doubleValue() < sumNOTpIncapacidad.doubleValue()) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PAGOS_NOM_SALUD" + anyoMesDetalleKey(obj), hojaCalLiqDetReqAmpl.getIbcPagosNomSalud());
            }
        }

        // Regla #24 <AJUSTE SALUD>
        // julio 21.2023. Antes se tomaba el aporte liquidado del registro en proceso. Para la regla comparativa, se toma el menor valor entre
        // la liquidación del registro en proceso y la liquidación anterior con los mismos criterios.
        BigDecimal ajusteSalud = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj)));
        BigDecimal cotiPagPilaSalud = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SALUD" + anyoMesDetalleKey(obj)));
        ajusteSalud = ajusteSalud.subtract(cotiPagPilaSalud);
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj), ajusteSalud);
        
        // Regla #25 <CONCEPTO AJUSTE SALUD>
        //BigDecimal ajusteSalud = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj)));
        if (ajusteSalud == null) {
            ajusteSalud = BigDecimal.ZERO;
        }
        
        if ("X".equals(obj.getNominaDetalle().getOmisionSalud())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
        } else {
            if (ajusteSalud.doubleValue() >= 1000) {
                BigDecimal cotPagPilaSalud = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SALUD" + anyoMesDetalleKey(obj)));
                if (cotPagPilaSalud == null) {
                    cotPagPilaSalud = BigDecimal.ZERO;
                }
                // WROJAS. 26.04.2022. Art.30 Ley 1393/2020
                if ("MIN002".equals(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_SALUD" + anyoMesDetalleKey(obj))) || cotPagPilaSalud.compareTo(BigDecimal.ZERO) != 0) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.INEXACTO_DESC);
                } else {
                    String codigoObservacionSalud = gestorProgramaDao.observacionesNomina(obj.getNominaDetalle(), "OBSERVACIONES_SALUD");
                    //PilaDepurada pilaDepuradaMesAnterior = gestorProgramaDao.obtenerPilaDepuradaMesAnterior(obj.getNomina(), obj.getNominaDetalle());
                    if (cotPagPilaSalud.compareTo(BigDecimal.ZERO) == 0) {
                        if (codigoObservacionSalud != null) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.MORA_DESC);
                        } else {
                            if (pilaDepuradaMesAnterior == null) {
                                if (liquidacionDetalleMesAnterior == null) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
                                } else {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
                                }
                            } else {
                                boolean tieneNovedadRet = gestorProgramaDao.obtenerRegistroNovedadPilaDepuradaMesAnterior(pilaDepuradaMesAnterior);
                                if (tieneNovedadRet) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
                                } else {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.MORA_DESC);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Regla #26 <TIPO DE INCUMPLIMIENTO SALUD>
        //ajusteSalud = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj)));
        if (ajusteSalud.intValue() >= 1000) {
            String conceAjuSalud = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_SALUD" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoDescrip(conceAjuSalud));
        } else { // Julio 21.2023 Complemento de la regla comparativa para ajuste de salud.
            //infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SALUD" + anyoMesDetalleKey(obj), null);
            //infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_SALUD" + anyoMesDetalleKey(obj), null);
        }
        // Regla #30 <IBC PAGOS EN NOMINA PENSION>
        sumNOTpIncapacidad = gestorProgramaDao.sumaValorLiqConceptoContableIbcPagosNomSaludNoTpIncapacidad(obj.getNomina(), obj.getNominaDetalle(), "2");
        sumNOTpIncapacidad = sumValorReglas(sumNOTpIncapacidad, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#EXC_LIM_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj)));
        sumTpIncapacidad = gestorProgramaDao.sumaValorLiqConceptoContableIbcPagosNomPension(obj.getNomina(), obj.getNominaDetalle());
        if (StringUtils.containsIgnoreCase("X", obj.getNominaDetalle().getSalarioIntegral())) {
            BigDecimal por70 = sumNOTpIncapacidad.multiply(new BigDecimal("70"));// FIXME valor quemado
            por70 = por70.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            sumNOTpIncapacidad = por70;
        }
        sumNOTpIncapacidad = sumNOTpIncapacidad.add(sumTpIncapacidad);
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PAGOS_NOM_PENSION" + anyoMesDetalleKey(obj), roundValor(sumNOTpIncapacidad));
        // Regla #31 <TOTAL IBC CALCULADO PENSION>
        String tienePension = gestorProgramaDao.tienePension(obj.getNominaDetalle());
        String tienePensionCotizante = gestorProgramaDao.tienePensionCotizante(obj.getNominaDetalle());

        BigDecimal valorResultante = new BigDecimal("0");
        if (tienePension == null && tienePensionCotizante == null) {
            try {
                ErrorTipo errorObj = new ErrorTipo();
                errorObj.setCodError("IBC_CALCULADO_PENSION");
                errorObj.setValDescError("No encontro codigo SUBTIPO_COTIZANTE. Nominadetalle = " + obj.getNominaDetalle().getId());
                errorTipo.add(errorObj);
                throw new Exception("EXCEPTION ERROR procesarReglasNoFormula: No encontro codigo SUBTIPO_COTIZANTE. Nominadetalle = " + obj.getNominaDetalle().getId());
            } catch (Exception ex) {
                Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("NO".equals(tienePension) || "NO".equals(tienePensionCotizante) || "X".equals(obj.getNominaDetalle().getExtranjero_no_obligado_a_cotizar_pension())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        } else {
            if (mapMallaval != null && StringUtils.contains("NO", mapMallaval.get("TIENE_PENSION")) && StringUtils.contains("NO", mapMallaval.get("ALTO_RIESGO"))
                    && StringUtils.contains("NO", mapMallaval.get("TIENE_FSP"))) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            } else {
                if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante())) {
                    // Se busca el total del ingreso bruto - Tipo de pago
                    boolean seCumpleIBCPension = false;
                    BigDecimal sumIngresoBruto = gestorProgramaDao.sumaValorLiqConceptoContableTotalIBCCalculadoSaludIngresoBruto(obj.getNomina(), obj.getNominaDetalle());
                    // REQ.artículo 244 ley 1995 de 25.05.2019. Implementado 14.02.2022
                    BigDecimal sumIngresoNeto = gestorProgramaDao.sumaValorLiqConceptoContableTotalIBCCalculadoSaludIngresoNeto(obj.getNomina(), obj.getNominaDetalle());
                    //formateador = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date fechaControlTrabIndep1 = formateador.parse("30/06/2015");
                        Date fechaControlTrabIndep2 = formateador.parse("30/06/2015");
                        // REQ.artículo 244 ley 1995 de 25.05.2019. Implementado 14.02.2022
                        Date fechaControlTrabIndep3 = formateador.parse("31/05/2019"); // Se realiza ajuste para que se cumple >= junio de 2019. 31.03.2022
                        Date fechaRegistroTrabIndep = formateador.parse("01/" + obj.getNominaDetalle().getMes() + "/" + obj.getNominaDetalle().getAno());
                        if (fechaRegistroTrabIndep.before(fechaControlTrabIndep1) && sumIngresoBruto.doubleValue() <= 0) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            seCumpleIBCPension = true;
                        }
                        if (fechaRegistroTrabIndep.after(fechaControlTrabIndep2) && sumIngresoBruto.doubleValue() < salarioMinimo.doubleValue() && fechaRegistroTrabIndep.before(fechaControlTrabIndep3)) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            seCumpleIBCPension = true;
                        }
                        if (fechaRegistroTrabIndep.after(fechaControlTrabIndep3) && sumIngresoNeto.doubleValue() < salarioMinimo.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            seCumpleIBCPension = true;
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    // Ahora se verifican los topes
                    if (!seCumpleIBCPension) { // Sino se cumplen las primeras dos condiciones se controla con los topes
                        // 28 agosto 2023. Se elimina la verificación de enero. Se coloca para jurídicas
                        BigDecimal sumatoria = sumValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PAGOS_NOM_PENSION" + anyoMesDetalleKey(obj)),
                                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PERMISOS_REMUNERADOS" + anyoMesDetalleKey(obj)),
                                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_VACACIONES" + anyoMesDetalleKey(obj)),
                                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_HUELGA" + anyoMesDetalleKey(obj)));
                        BigDecimal ibcPensionConcurrencia = convertValorRegla(obj.getNominaDetalle().getIbcPenConIngrOtrApo());
                        BigDecimal sumatoriaConIbcPension = sumatoria.add(ibcPensionConcurrencia);

                        CobParamGeneral cobParamGeneral1 = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "MAXIBCPENSION" + obj.getNominaDetalle().getAno().toString());
                        BigDecimal topeIbcPension = new BigDecimal("0");
                        if (cobParamGeneral1 != null && cobParamGeneral1.getValor() != 0) {
                            topeIbcPension = new BigDecimal(cobParamGeneral1.getValor().toString());
                        }
                        BigDecimal rstSmmlPorTopeIbcPension = topeIbcPension.multiply(salarioMinimo);
                        tmp2 = salarioMinimo.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
                        if (ibcPensionConcurrencia.doubleValue() >= rstSmmlPorTopeIbcPension.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        } else {
                            if (sumatoriaConIbcPension.doubleValue() <= rstSmmlPorTopeIbcPension.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(sumatoria));
                                valorResultante = sumatoria;
                            } else {
                                BigDecimal resto = rstSmmlPorTopeIbcPension.subtract(ibcPensionConcurrencia);
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(resto));
                                valorResultante = resto;
                            }
                            BigDecimal diasCotPensiondiaSmml = mulValorReglas(tmp2, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_PENSION" + anyoMesDetalleKey(obj)));
                            if (valorResultante.doubleValue() < salarioMinimo.doubleValue()) {
                                if (valorResultante.doubleValue() < diasCotPensiondiaSmml.doubleValue()) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(diasCotPensiondiaSmml));
                                } else {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(valorResultante));
                                }
                            }
                        }
                    } // Final del condicional al cual se ingresa cuando NO se cumplen las primeras dos condiciones
                } else { // Por este lado del <else> ingresa cuando NO es independientes
                    // Se coloca la verificación de enero. 28.agosto.2023 antes estaba para independientes
                    //BigDecimal salarioMinimoAnterior = new BigDecimal("0");
                    if (obj.getNominaDetalle().getMes().intValue() == 1) {
                        int anoAnt = obj.getNominaDetalle().getAno().intValue() - 1;
                        cobParamGeneral = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "SMMLV" + anoAnt);
                        salarioMinimo = new BigDecimal(cobParamGeneral.getValor().toString());
                    }
                    BigDecimal sumatoria = sumValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PAGOS_NOM_PENSION" + anyoMesDetalleKey(obj)),
                            infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PERMISOS_REMUNERADOS" + anyoMesDetalleKey(obj)),
                            infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_VACACIONES" + anyoMesDetalleKey(obj)),
                            infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_HUELGA" + anyoMesDetalleKey(obj)));
                    BigDecimal ibcPensionConcurrencia = convertValorRegla(obj.getNominaDetalle().getIbcPenConIngrOtrApo());
                    BigDecimal sumatoriaConIbcPension = sumatoria.add(ibcPensionConcurrencia);
                    CobParamGeneral cobParamGeneral1 = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "MAXIBCPENSION" + obj.getNominaDetalle().getAno().toString());
                    BigDecimal topeIbcPension = new BigDecimal("0");
                    if (cobParamGeneral1 != null && cobParamGeneral1.getValor() != 0) {
                        topeIbcPension = new BigDecimal(cobParamGeneral1.getValor().toString());
                    }
                    BigDecimal rstSmmlPorTopeIbcPension = topeIbcPension.multiply(salarioMinimo);
                    tmp2 = salarioMinimo.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
                    if (ibcPensionConcurrencia.doubleValue() >= rstSmmlPorTopeIbcPension.doubleValue()) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    } else {
                        if (sumatoriaConIbcPension.doubleValue() <= rstSmmlPorTopeIbcPension.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(sumatoria));
                            valorResultante = sumatoria;
                        } else {
                            BigDecimal resto = rstSmmlPorTopeIbcPension.subtract(ibcPensionConcurrencia);
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(resto));
                            valorResultante = resto;
                        }
                        BigDecimal diasCotPensiondiaSmml = mulValorReglas(tmp2, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_PENSION" + anyoMesDetalleKey(obj)));
                        if (valorResultante.doubleValue() < salarioMinimo.doubleValue()) {
                            if (valorResultante.doubleValue() < diasCotPensiondiaSmml.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(diasCotPensiondiaSmml));
                            } else {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(valorResultante));
                            }
                        }
                        if ("20".equals(obj.getNominaDetalle().getTipoCotizante())) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(diasCotPensiondiaSmml));
                        }
                    }
                } // Aqui termina el caso del IBC para empresas -> cuando ingresa al <else>
            }
        }
        // Regla #32 <TARIFA PENSION>
        rst = gestorProgramaDao.tarifaPensionPorcentual(obj.getNomina(), obj.getNominaDetalle());
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PENSION" + anyoMesDetalleKey(obj), rst);
        // Regla #33 <COTIZACION OBLIGATORIA PENSION>
        rst = mulValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PENSION" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)));
        rst = rst.divide(new BigDecimal("100"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj), roundValor100(rst));

        // Reglas comparativas - SUBSISTEMA DE PENSION
        if (existeLiqAnt) {
            BigDecimal ibcCalPensionACT = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)));
            if (hojaCalLiqDetReqAmpl.getIbcCalculadoPension() == null){
                hojaCalLiqDetReqAmpl.setIbcCalculadoPension(new BigInteger("0"));
            }
            if (hojaCalLiqDetReqAmpl.getIbcCalculadoPension().doubleValue() < ibcCalPensionACT.doubleValue()) {
                BigDecimal rstACT = new BigDecimal(hojaCalLiqDetReqAmpl.getIbcCalculadoPension());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj), roundValor(rstACT));
            }
            BigDecimal cotizOblCalculadoPensionACT = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj)));
            if (hojaCalLiqDetReqAmpl.getCotizOblPension().doubleValue() < cotizOblCalculadoPensionACT.doubleValue()) {
                BigDecimal rstACT = new BigDecimal(hojaCalLiqDetReqAmpl.getCotizOblPension());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj), roundValor(rstACT));
            }
            if (hojaCalLiqDetReqAmpl.getIbcPagosNomPension().doubleValue() < sumNOTpIncapacidad.doubleValue()) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PAGOS_NOM_PENSION" + anyoMesDetalleKey(obj), hojaCalLiqDetReqAmpl.getIbcPagosNomPension());
            }
        }
        
        // Regla #38 <AJUSTE PENSION>
        // Ajuste Mayo 04.2023. No hubo requerimiento asociado.
        if ("X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj), new Integer("0"));
        } else {
            BigDecimal ajustePension = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj)),
                    infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj)));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj), roundValor100(ajustePension));
            // SD 1197754 - junio 01.2023. Se elimina el criterio del tipo de acto
            // Agosto 17.2023. Se realiza nueva modificación acorde al archivo de reglas de Agosto 17.2023
            if (obj.getNominaDetalle().getAno().intValue() == 2020 && (obj.getNominaDetalle().getMes().intValue() == 4 || obj.getNominaDetalle().getMes().intValue() == 5)) {
                BigDecimal tarPilaPension = new BigDecimal("0");
                if ("liquidacion".equals(obj.getNomina().getTipoActo()) || "recurso".equals(obj.getNomina().getTipoActo())) { // Es Liquidación
                    if (hojaCalLiqDetReqAmpl.getTarifaPilaPension() != null) {
                        tarPilaPension = hojaCalLiqDetReqAmpl.getTarifaPilaPension();
                    }
                } else {
                    tarPilaPension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_PENSION" + anyoMesDetalleKey(obj)));
                }
                tarPilaPension = tarPilaPension.multiply(new BigDecimal("100"));
                if (tarPilaPension.compareTo(new BigDecimal("3")) == 0) {
                    BigDecimal resto = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)),
                            infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_PILA_PENSION" + anyoMesDetalleKey(obj)));
                    if (resto.compareTo(new BigDecimal("1000")) == -1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj), new Integer("0"));
                    } else {
                        resto = resto.multiply(new BigDecimal("16"));
                        resto = resto.divide(new BigDecimal("100"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj), roundValor100(resto));
                    }
                }
            }
        }
        // Regla #39 <CONCEPTO AJUSTE PENSION>
        BigDecimal ajustePension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj)));
        if (ajustePension == null) {
            ajustePension = BigDecimal.ZERO;
        }
        BigDecimal cotPagadaPilaPension = new BigDecimal("0");
        if (ajustePension.intValue() >= 1000 || "X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
            cotPagadaPilaPension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj)));
            if (cotPagadaPilaPension == null) {
                cotPagadaPilaPension = BigDecimal.ZERO;
            }
            if ("X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
                // FIXME verificar el nombre que se le va a dar a esta variable
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_PENSION" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.VDCA_DESC);
            } else {
                // WROJAS. 26.04.2022 Art.30 Ley 1393/2010
                if (cotPagadaPilaPension.compareTo(BigDecimal.ZERO) != 0) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_PENSION" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.INEXACTO_DESC);
                } else if (infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_PENSION" + anyoMesDetalleKey(obj)) == null && cotPagadaPilaPension.compareTo(BigDecimal.ZERO) == 0) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_PENSION" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
                } else if (cotPagadaPilaPension.compareTo(BigDecimal.ZERO) == 0 && infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_PENSION" + anyoMesDetalleKey(obj)) != null) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_PENSION" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.MORA_DESC);
                }
            }
        }
        // Regla #40 <TIPO DE INCUMPLIMIENTO PENSION>
        String conceAjuPension = "";
        ajustePension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj)));
        if (ajustePension.intValue() >= 1000 || "X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
            conceAjuPension = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_PENSION" + anyoMesDetalleKey(obj));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_PENSION" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoPensionFSP(conceAjuPension));
        }
        // Regla #42 <TARIFA FSP SUBCUENTA DE SOLIDARIDAD>
        rst = gestorProgramaDao.tarifaFspSolidaridad(obj.getNominaDetalle(),
                convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj))));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj), rst);
        // Regla #43 <TARIFA FSP SUBCUENTA DE SUBSISTENCIA>
        rst = gestorProgramaDao.tarifaFspSubsistencia(obj.getNomina(), obj.getNominaDetalle(), convertValorRegla(infoNegocio
                .get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj))));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj), rst);
        // Regla #44 <COTIZACION OBLIGATORIA FSP SUBCUENTA DE SOLIDARIDAD>
        rst = mulValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj)));
        rst = rst.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj), roundValor100(rst));
        // Regla #45 <COTIZACION OBLIGATORIA FSP SUBCUENTA DE SUBSISTENCIA>
        rst = mulValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj)));
        rst = rst.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj), roundValor100(rst));
        // Regla #48 <AJUSTE FSP SUBCUENTA DE SOLIDARIDAD> :: Regla #49 <AJUSTE FSP SUBCUENTA DE SUBSISTENCIA>
        if ("X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj), new Integer("0"));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj), new Integer("0"));
        } else {
            BigDecimal ajusteSolid = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD"
                    + anyoMesDetalleKey(obj)), infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SOLIDAR"
                    + anyoMesDetalleKey(obj)));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj), roundValor100(ajusteSolid));
            BigDecimal ajusteFsp = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA"
                    + anyoMesDetalleKey(obj)), infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SUBSIS"
                    + anyoMesDetalleKey(obj)));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj), roundValor100(ajusteFsp));
        }
        // Regla #50 <CONCEPTO AJUSTE FSP>
        rst = sumValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj)));
        String conceptoAjtPension = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_PENSION" + anyoMesDetalleKey(obj));
        BigDecimal ajusteFSP = rst;
        if ("X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
            // FIXME verificar el nombre que se le va a dar a esta variable
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_FSP" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.VDCA_DESC);
        } else if (rst.intValue() >= 1000) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_FSP" + anyoMesDetalleKey(obj), nomModEje.conceptoAjusteFSPaltoRiesgo(conceptoAjtPension));
        }
        //MODIFICACION regla #28 <CODIGO ADMINISTRADORA PENSION>  
        //Y  que el “AJUSTE PENSIÓN” sea menor que mil pesos $1000, se debe colocar el código  FSP001
        // Este bloque se trajo de una versión anterior para una validación por parte del área funcional
        if (ajustePension.intValue() < 1000 && ajusteFSP.intValue() >= 1000) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_PENSION" + anyoMesDetalleKey(obj), "FSP001");
            // si piladepurada=null Regla #29 <NOMBRE CORTO ADMINISTRADORA PENSION>
            String nomAdmCcf = LST_ADMINISTRADORA_PILA.get("FSP001");
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_PENSION" + anyoMesDetalleKey(obj), nomAdmCcf);
        }
        // Regla #51 <TIPO DE INCUMPLIMIENTO FSP>
        String conceptoAjuFsp = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_FSP" + anyoMesDetalleKey(obj));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_FSP" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoPensionFSP(conceptoAjuFsp));
        
        // Regla #53 <TARIFA PENSION ADICIONAL ACT. ALTO RIESGO>
        if ("X".equals(obj.getNominaDetalle().getActividad_alto_riesgo_pension())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TAR_PENSION_ACT_ALTORIESGO" + anyoMesDetalleKey(obj), new Integer("10"));
        } else {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TAR_PENSION_ACT_ALTORIESGO" + anyoMesDetalleKey(obj), new Integer("0"));
        }
        // Regla #54 <COTIZACION OBLIGATORIA ADICIONAL ACT. ALTO RIESGO>
        // TK SD 700817 Regla ajustada para casos mixstos. WR. 30.07.2021
        if ("X".equals(obj.getNominaDetalle().getActividad_alto_riesgo_pension())) {
            rst = mulValorReglas(
                    infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)),
                    infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TAR_PENSION_ACT_ALTORIESGO" + anyoMesDetalleKey(obj)));
            rst = rst.divide(new BigDecimal("100"));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO" + anyoMesDetalleKey(obj), roundValor100(rst));
        } else {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO" + anyoMesDetalleKey(obj), new Integer("0"));
        }
        // Regla #56 <COTIZACION PAGADA PILA PENSION ADICIONAL ACT. ALTO RIESGO>
        if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaAltoRiPe().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaAltoRiPe().toString()).intValue() >= 0) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAG_PILA_PENSION_ACT_ARIES" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaAltoRiPe().toString());
        } else {
            if (pilaDepurada != null) //APOR_COR_OBLI_PEN_ALT_RIESGO      
            {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAG_PILA_PENSION_ACT_ARIES" + anyoMesDetalleKey(obj), pilaDepurada.getAporCorObliPenAltRiesgo());
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAG_PILA_PENSION_ACT_ARIES" + anyoMesDetalleKey(obj), new Integer("0"));
            }
        }
        // Regla #57 <AJUSTE PENSION ADICIONAL ACT. ALTO RIESGO>
        if ("X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj), new Integer("0"));
        } else {
            rst = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO"
                    + anyoMesDetalleKey(obj)), infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAG_PILA_PENSION_ACT_ARIES"
                    + anyoMesDetalleKey(obj)));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj), roundValor100(rst));
        }
        // Regla #58 <CONCEPTO AJUSTE PENSION ADICIONAL ACT. ALTO RIESGO>
        rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj)));
        if ("X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CON_AJUS_PENSION_ACT_ARIESGO" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.VDCA_DESC);
        } else if (rst.intValue() >= 1000) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CON_AJUS_PENSION_ACT_ARIESGO" + anyoMesDetalleKey(obj), nomModEje.conceptoAjusteFSPaltoRiesgo(conceptoAjtPension));
        }
        // Regla #59 <TIPO DE INCUMPLIMIENTO PENSION ADICIONAL ACT. ALTO RIESGO>
        String conceAjuPenActAltoRiesgo = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CON_AJUS_PENSION_ACT_ARIESGO" + anyoMesDetalleKey(obj));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INC_PENSION_ACT_ARIESGO" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoPensionFSP(conceAjuPenActAltoRiesgo));

        // POR AQUI VA LA REVISIÓN - WILSON ROJAS
        // Regla #61 <CALCULO ACTUARIAL>
        if ("X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
            // Las celdas de cargue manual si tienen <-1> indica que en el excel esas celdas están vacías o nulas
            if ((obj.getNominaDetalle().getCargueManualPilaPension()).signum() == -1) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CALCULO_ACTUARIAL" + anyoMesDetalleKey(obj),
                        obj.getNominaDetalle().getValorCalculoActuarial());
            } else {
                rst = obj.getNominaDetalle().getValorCalculoActuarial();
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CALCULO_ACTUARIAL" + anyoMesDetalleKey(obj),
                        rst);
            }
            // Regla #61A <VAL_NET_CAL_ACT> Valor neto para el cálculo actuarial. Aplica cuando CALCULO ACTUARIAL es 'X'
            rst = new BigDecimal(String.valueOf(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CALCULO_ACTUARIAL" + anyoMesDetalleKey(obj))));
            rst = rst.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj))));
            rst = rst.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SOLIDAR" + anyoMesDetalleKey(obj))));
            rst = rst.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAG_PILA_FSP_SUB_SUBSIS" + anyoMesDetalleKey(obj))));
            rst = rst.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAG_PILA_PENSION_ACT_ARIES" + anyoMesDetalleKey(obj))));
            if (rst.doubleValue() >= 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VAL_NET_CAL_ACT" + anyoMesDetalleKey(obj), rst);
            }
        }
        // Regla #64 <IBC ARL>
        cobParamGeneral = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "SMMLV" + obj.getNominaDetalle().getAno().toString());
        salarioMinimo = new BigDecimal(cobParamGeneral.getValor().toString());
                
        if (obj.getNominaDetalle().getDiasTrabajadosMes().equals(0) || "X".equals(obj.getNominaDetalle().getColombiano_en_el_exterior())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        } else {
            if (mapMallaval != null && StringUtils.contains("NO", mapMallaval.get("TIENE_ARP"))) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            } else {
                // SAlario Minimo buscado en Cache
                //cobParamGeneral = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "SMMLV" + obj.getNominaDetalle().getAno().toString());
                //rstSmml = new BigDecimal("0");
                //if (cobParamGeneral != null && cobParamGeneral.getValor() != 0) {
                    //rstSmml = new BigDecimal(cobParamGeneral.getValor().toString());
                //}
                // WROJAS - Oct.04.2011 RQ: TRabajadores Independientes
                if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante())) {
                    // Se busca el total del ingreso bruto - Tipo de pago
                    BigDecimal sumIngresoBruto = gestorProgramaDao.sumaValorLiqConceptoContableTotalIBCCalculadoSaludIngresoBruto(obj.getNomina(), obj.getNominaDetalle());
                    BigDecimal sumIngresoNeto = gestorProgramaDao.sumaValorLiqConceptoContableTotalIBCCalculadoSaludIngresoNeto(obj.getNomina(), obj.getNominaDetalle());
                    //formateador = new SimpleDateFormat("dd/MM/yyyy");
                    boolean seCumpleIBCArl = false;
                    try {
                        Date fechaControlTrabIndep1 = formateador.parse("30/06/2015");
                        Date fechaControlTrabIndep2 = formateador.parse("30/06/2015");
                        // REQ.artículo 244 ley 1995 de 25.05.2019. Implementado 14.02.2022
                        Date fechaControlTrabIndep3 = formateador.parse("31/05/2019"); // Se realiza ajuste para que se cumple >= junio de 2019. 31.03.2022
                        Date fechaRegistroTrabIndep = formateador.parse("01/" + obj.getNominaDetalle().getMes() + "/" + obj.getNominaDetalle().getAno());
                        if (fechaRegistroTrabIndep.before(fechaControlTrabIndep1) && sumIngresoBruto.doubleValue() <= 0) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            seCumpleIBCArl = true;
                        }
                        if (fechaRegistroTrabIndep.after(fechaControlTrabIndep2) && sumIngresoBruto.doubleValue() < salarioMinimo.doubleValue() && fechaRegistroTrabIndep.before(fechaControlTrabIndep3)) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            seCumpleIBCArl = true;
                        }
                        if (fechaRegistroTrabIndep.after(fechaControlTrabIndep3) && sumIngresoNeto.doubleValue() < salarioMinimo.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            seCumpleIBCArl = true;
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    // Aqui inicia la validación de topes para INDEPENDIENTES
                    if (!seCumpleIBCArl) { // SI no se cumplen las condiciones anteriores se verifican los topes
                        tmp1 = gestorProgramaDao.sumaValorLiqConceptoContableNominaDetalle(obj.getNomina(), obj.getNominaDetalle(), "3");
                        tmp2 = sumValorReglas(tmp1, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#EXC_LIM_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj)));
                        // Trabajador independiente y es enero
                        BigDecimal topIbcARL = gestorProgramaDao.topeIbcArl(obj.getNomina(), obj.getNominaDetalle());
                        BigDecimal topeMaxIbc = salarioMinimo.multiply(topIbcARL);
                        BigDecimal resulParcial = tmp2;
                        if ("X".equals(obj.getNominaDetalle().getSalarioIntegral())) {
                            BigDecimal por70 = tmp2.multiply(new BigDecimal("70"));
                            por70 = por70.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                            resulParcial = por70;
                        }
                        BigDecimal ibcArlConcurrencia = convertValorRegla(obj.getNominaDetalle().getIbcArlConIngrOtrApo());
                        tmp2 = resulParcial.add(ibcArlConcurrencia);
                        if (ibcArlConcurrencia.doubleValue() >= topeMaxIbc.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        } else {
                            if (tmp2.doubleValue() <= topeMaxIbc.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(tmp2));
                                valorResultante = tmp2;
                            } else {
                                topeMaxIbc.subtract(ibcArlConcurrencia);
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(topeMaxIbc));
                                valorResultante = topeMaxIbc;
                            }
                            if (valorResultante.doubleValue() < salarioMinimo.doubleValue()) {
                                BigDecimal diaSmml = salarioMinimo.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
                                BigDecimal proporcionSalarioMes = diaSmml.multiply(new BigDecimal(obj.getNominaDetalle().getDiasTrabajadosMes()));
                                if (valorResultante.doubleValue() < proporcionSalarioMes.doubleValue()) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(proporcionSalarioMes));
                                } else {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(valorResultante));
                                }
                            }
                        }
                    } // Aqui finaliza el condicional al cual se ingresa cuando NO se cumplen las primeras dos condiciones de INDEPENDIENTES
                } else { // FInaliza ajuste Trabajadores Independientes el <else> es para el caso de empresas
                    tmp1 = gestorProgramaDao.sumaValorLiqConceptoContableNominaDetalle(obj.getNomina(), obj.getNominaDetalle(), "3");
                    tmp2 = sumValorReglas(tmp1, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#EXC_LIM_PAGO_NO_SALARIAL" + anyoMesDetalleKey(obj)));
                    // 28 agosto 2023. Se quitó para independientes y se colocó para jurídicas
                    //BigDecimal salarioMinimoAnterior = new BigDecimal("0");
                    if (obj.getNominaDetalle().getMes().intValue() == 1) {
                        int anoAnt = obj.getNominaDetalle().getAno().intValue() - 1;
                        cobParamGeneral = (CobParamGeneral) cacheService.get(CacheService.REGION_COBPARAMGENERAL, "SMMLV" + anoAnt);
                        salarioMinimo = new BigDecimal(cobParamGeneral.getValor().toString());
                    }
                    BigDecimal topIbcARL = gestorProgramaDao.topeIbcArl(obj.getNomina(), obj.getNominaDetalle());
                    BigDecimal topeMaxIbc = salarioMinimo.multiply(topIbcARL);
                    BigDecimal resulParcial = tmp2;
                    valorResultante = new BigDecimal("0");
                    if ("X".equals(obj.getNominaDetalle().getSalarioIntegral())) {
                        BigDecimal por70 = tmp2.multiply(new BigDecimal("70"));
                        por70 = por70.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        resulParcial = por70;
                    }
                    BigDecimal ibcArlConcurrencia = convertValorRegla(obj.getNominaDetalle().getIbcArlConIngrOtrApo());
                    tmp2 = resulParcial.add(ibcArlConcurrencia);
                    if (ibcArlConcurrencia.doubleValue() >= topeMaxIbc.doubleValue()) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    } else {
                        if (tmp2.doubleValue() <= topeMaxIbc.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(tmp2));
                            valorResultante = tmp2;
                        } else {
                            topeMaxIbc.subtract(ibcArlConcurrencia);
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(topeMaxIbc));
                            valorResultante = topeMaxIbc;
                        }
                        if (valorResultante.doubleValue() < salarioMinimo.doubleValue()) {
                            BigDecimal diaSmml = salarioMinimo.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
                            BigDecimal proporcionSalarioMes = diaSmml.multiply(new BigDecimal(obj.getNominaDetalle().getDiasTrabajadosMes()));
                            if (valorResultante.doubleValue() < proporcionSalarioMes.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(proporcionSalarioMes));
                            } else {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(valorResultante));
                            }
                        }
                    }
                    if ("21".equals(obj.getNominaDetalle().getTipoCotizante()) || "20".equals(obj.getNominaDetalle().getTipoCotizante())
                            || "19".equals(obj.getNominaDetalle().getTipoCotizante())) {

                        BigDecimal diaSmml = salarioMinimo.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
                        BigDecimal rstMulDia = mulValorReglas(diaSmml, infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#DIAS_COT_RPROF" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj), roundValor(rstMulDia));
                    }
                }
            }
        }
        // Regla #66 <COTIZACION OBLIGATORIA ARL>
        BigDecimal ibc_arl = new BigDecimal(String.valueOf(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ARL" + anyoMesDetalleKey(obj))));
        BigDecimal tarifa_arl = new BigDecimal("0");
        if (infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ARL" + anyoMesDetalleKey(obj)) != null) {
            tarifa_arl = new BigDecimal(String.valueOf(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ARL" + anyoMesDetalleKey(obj))));
        }
        rst = mulValorReglas(ibc_arl, tarifa_arl);
        //rst = rst.divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ARL" + anyoMesDetalleKey(obj), roundValor100(rst));
        // Regla #70 <COTIZACION PAGADA PILA ARL>
        if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaArl().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaArl().toString()).intValue() >= 0) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ARL" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaArl().toString());
        } else {
            if (pilaDepurada != null) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ARL" + anyoMesDetalleKey(obj), pilaDepurada.getCotObligatoriaArp());
                //TK SD 700817. Agosto 08.2021
                if ("2".equals(obj.getNominaDetalle().getIdaportante().getTipoAportante()) && (pilaDepurada.getTipoPlanilla().indexOf('E') >= 0)) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                }
            }
        }
        // Regla #71 <AJUSTE ARL>
        rst = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ARL" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ARL" + anyoMesDetalleKey(obj)));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj), rst);
        // Regla #72 <CONCEPTO AJUSTE ARL>
        rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj)));
        BigDecimal cotPagPilaARL = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ARL" + anyoMesDetalleKey(obj)));
        if (cotPagPilaARL == null) {
            cotPagPilaARL = BigDecimal.ZERO;
        }
        if ("X".equals(obj.getNominaDetalle().getOmisionArl())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ARL" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
        } else {
            if (rst.intValue() >= 1000) {
                if (cotPagPilaARL.compareTo(BigDecimal.ZERO) != 0) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ARL" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.INEXACTO_DESC);
                } else if (cotPagPilaARL.compareTo(BigDecimal.ZERO) == 0 && "ARL001".equals(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_ARL" + anyoMesDetalleKey(obj)))) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ARL" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
                } else if (cotPagPilaARL.compareTo(BigDecimal.ZERO) == 0 && !"ARL001".equals(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_ARL" + anyoMesDetalleKey(obj)))) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ARL" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.MORA_DESC);
                }
            }
        }
        // Regla #73 <TIPO INCUMPLIMIENTO ARL>
        rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj)));
        if (rst.intValue() >= 1000) {
            String conceAjuArl = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ARL" + anyoMesDetalleKey(obj));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_ARL" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoDescrip(conceAjuArl));
        }
        String valorAnteriorCCF = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_CCF" + anyoMesDetalleKey(obj));
        // Regla #75 <CODIGO ADMINISTRADORA CCF>
        // Regla #76 <NOMBRE CORTO ADMINISTRADORA CCF>
        if (StringUtils.isBlank(valorAnteriorCCF)) {
            if (pilaDepurada != null && StringUtils.isNotBlank(pilaDepurada.getCodigoCCF())) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_CCF" + anyoMesDetalleKey(obj), pilaDepurada.getCodigoCCF());
                //System.out.println("::ANDRES20:: pilaDepurada: " + pilaDepurada.getId() + " MES: " + anyoMesDetalleKey(obj) + " CCF: " + pilaDepurada.getCodigoCCF());
                String nomAdmPension = LST_ADMINISTRADORA_PILA.get(pilaDepurada.getCodigoCCF());
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_CCF" + anyoMesDetalleKey(obj), nomAdmPension);
            } else {
                String codObsCCF = gestorProgramaDao.observacionesNomina(obj.getNominaDetalle(), "OBSERVACIONES_CCF");
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_CCF" + anyoMesDetalleKey(obj), codObsCCF);
                String nomAdmCCF = LST_ADMINISTRADORA_PILA.get(codObsCCF);
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NOM_ADM_CCF" + anyoMesDetalleKey(obj), nomAdmCCF);
            }
        }
        // Regla #77 <IBC CCF>
        if (mapMallaval != null && StringUtils.contains("NO", mapMallaval.get("TIENE_CCF"))) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CCF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        } else {
            tmp2 = gestorProgramaDao.sumaValorLiqConceptoContableIBC_CCF(obj.getNominaDetalle(), "0", "0", "0", "4", "0", "0");
            BigDecimal resultado = tmp2;
            if ("X".equals(obj.getNominaDetalle().getSalarioIntegral())) {
                BigDecimal por70 = tmp2.multiply(new BigDecimal("70"));
                por70 = por70.divide(new BigDecimal("100"));
                resultado = por70;
            }
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CCF" + anyoMesDetalleKey(obj), roundValor(resultado));
        }
        // Regla #78 <TARIFA CCF>
        rst = gestorProgramaDao.tarifaCCF(obj.getNomina(), obj.getNominaDetalle());
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_CCF" + anyoMesDetalleKey(obj), rst);
        if (null != obj.getNominaDetalle().getCondEspEmp()) {
            BigDecimal result = nomModEje.tarifaCCF(obj.getNominaDetalle().getCondEspEmp());
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_CCF" + anyoMesDetalleKey(obj), result);
        }
        // Regla #79 <COTIZACION OBLIGATORIA CCF>
        rst = mulValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CCF" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_CCF" + anyoMesDetalleKey(obj)));
        rst = rst.divide(new BigDecimal("100"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CCF" + anyoMesDetalleKey(obj), roundValor100(rst));
        // Regla #83 <COTIZACION PAGADA PILA CCF>
        if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaCcf().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaCcf().toString()).intValue() >= 0) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_CCF" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaCcf().toString());
        } else {
            if (pilaDepurada == null) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_CCF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_CCF" + anyoMesDetalleKey(obj), pilaDepurada.getValorAportesCcfIbcTarifa());
            }
        }
        // Regla #84 <AJUSTE CCF>
        rst = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CCF" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_CCF" + anyoMesDetalleKey(obj)));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj), rst);
        // Regla #85 <CONCEPTO AJUSTE CCF>
        rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj)));
        if ("X".equals(obj.getNominaDetalle().getOmisionCcf())) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_CCF" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
        } else {
            if (rst.intValue() >= 1000) {
                BigDecimal concepAjuste = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_CCF" + anyoMesDetalleKey(obj)));
                if (concepAjuste == null) {
                    concepAjuste = BigDecimal.ZERO;
                }
                if (infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_CCF" + anyoMesDetalleKey(obj)) == null && concepAjuste.compareTo(BigDecimal.ZERO) == 0) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_CCF" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.OMISO_DESC);
                } else if (infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COD_ADM_CCF" + anyoMesDetalleKey(obj)) != null && concepAjuste.compareTo(BigDecimal.ZERO) == 0) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_CCF" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.MORA_DESC);
                } else if (concepAjuste.compareTo(BigDecimal.ZERO) != 0) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_CCF" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.INEXACTO_DESC);
                }
            }
        }
        // Regla #86 <TIPO DE INCUMPLIMIENTO CCF>
        rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj)));
        if (rst.intValue() >= 1000) {
            String conceAjuCcf = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_CCF" + anyoMesDetalleKey(obj));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_CCF" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoDescrip(conceAjuCcf));
        }
        // Regla #88 <IBC SENA>
        //mesNomina = obtenerMesNomina(obj.getNominaDetalle().getMes().intValue());
        if (mapMallaval != null && StringUtils.contains("NO", mapMallaval.get("TIENE_SENA"))) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        } else {
            if (!obj.getNominaDetalle().getTipoCotizante().equals("31")) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj),
                        infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CCF" + anyoMesDetalleKey(obj)));
            } else {
                tmp2 = gestorProgramaDao.sumaValorLiqConceptoContableIBC(obj.getNominaDetalle(), "0", "0", "0", "0", "5", "0");
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), tmp2);
            }
            BigDecimal valorCalculado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_DEVENGADO" + anyoMesDetalleKey(obj)));
            /*try {
                //entre mayo 2013  y  diciembre 2016
                fechaCree1 = formateador.parse("30/04/2013");
                fechaCree2 = formateador.parse("01/01/2017");
                fechaCree3 = formateador.parse("31/12/2016");
            } catch (ParseException ex) {
                Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            // WROJAS - Art.30 Ley 1393 se agregó el siguiente condicional
            if (tipoIdentificacionAportante.intValue() == 5 && "1".equals(obj.getNominaDetalle().getTipoCotizante())
                    && valorCalculado.doubleValue() <= smml10.doubleValue() && fechaNomina.after(fechaCree1) && fechaNomina.before(fechaCree2)) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            } else if (tipoIdentificacionAportante.intValue() == 5 && "1".equals(obj.getNominaDetalle().getTipoCotizante()) && fechaNomina.after(fechaCree3) && valorCalculado.doubleValue() < smml10.doubleValue()) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            } else {
                // CASO DEL CREE
                //1- Si el campo “SUJETO PASIVO DEL IMPUESTO SOBRE LA RENTA PARA LA EQUIDAD CREE” está marcado con ""SI"" y  el campo 
                //""TIPO DOCUMENTO APORTANTE"" es igual a ""NI"", se registrará cero (0) en el IBC SENA así:
                //a- entre mayo 2013  y  diciembre 2016, a trabajadores que ""devenguen"", individualmente considerados, hasta diez (10) SMLMV (Ley 1607/2012).
                //b- a partir de enero 2017 a trabajadores que ""devenguen"", individualmente considerados, menos de diez (10) SMLMV (Ley 1819/2016).
                if ("SI".equals(obj.getNominaDetalle().getIdaportante().getSujetoPasivoImpuestoCree())) {
                    //System.out.println("::ANDRES70:: getSujetoPasivoImpuestoCree igual a SI");
                    if (tipoIdentificacionAportante.intValue() == 2) {
                        try {
                            //entre mayo 2013  y  diciembre 2016
                            fechaCree1 = formateador.parse("30/04/2013");
                            fechaCree2 = formateador.parse("01/01/2017");
                            if (fechaNomina != null && fechaNomina.after(fechaCree1) && fechaNomina.before(fechaCree2) && valorCalculado.doubleValue() <= smml10.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                //System.out.println("::ANDRES93:: getSujetoPasivoImpuestoCree : " + 0);
                            }
                            //a partir de enero 2017 
                            fechaCree1 = formateador.parse("31/12/2016");
                            if (fechaNomina != null && fechaNomina.after(fechaCree1) && valorCalculado.doubleValue() < smml10.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else{
                    //2- si el campo “SUJETO PASIVO DEL IMPUESTO SOBRE LA RENTA PARA LA EQUIDAD CREE” está marcado con ""SI"", 
                    //el campo ""TIPO DOCUMENTO APORTANTE"" es diferente a ""NI"",  el periodo fiscalizado es posterior a abril de 2013, 
                    //y existe mas de un trabajador en el mes de la nomina que se esta fiscalizando,  el IBC SENA será igual a cero (0) 
                    //para los trabajadores que ""devenguen"", individualmente considerados, menos de diez (10) salarios mínimos mensuales legales vigentes.
                    //if (tipoIdentificacionAportante.intValue() != 2) {
                        try {
                            fechaCree1 = formateador.parse("30/04/2013");
                            if (fechaNomina != null && fechaNomina.after(fechaCree1) && cantidadEmp.compareTo(BigDecimal.ONE) > 1 && valorCalculado.doubleValue() < smml10.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        // Regla #89 <TARIFA SENA>
        rst = gestorProgramaDao.tarifaSena(obj.getNomina(), obj.getNominaDetalle());
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SENA" + anyoMesDetalleKey(obj), rst);
        if ("1".equals(obj.getNominaDetalle().getIdaportante().getNaturalezaJuridica()) && "X".equals(obj.getNominaDetalle().getIdaportante().getAportaEsapYMen())) {
            BigDecimal rsttf = new BigDecimal("1");
            rsttf = rsttf.divide(new BigDecimal("2"));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SENA" + anyoMesDetalleKey(obj), rsttf);
        } else {
            if ("2".equals(obj.getNominaDetalle().getIdaportante().getNaturalezaJuridica())) {
                //BigDecimal rst05 = new BigDecimal("1");
                //rst05 = rst05.divide(new BigDecimal("2"));
                //BigDecimal rst15 = new BigDecimal("3");
                //rst15 = rst15.divide(new BigDecimal("2"));
                if (null != obj.getNominaDetalle().getCondEspEmp()) {
                    BigDecimal result = nomModEje.tarifaSena(obj.getNominaDetalle().getCondEspEmp());
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SENA" + anyoMesDetalleKey(obj), result);
                }
            }
        }
        // Regla #90 <COTIZACION OBLIGATORIA SENA>
        rst = mulValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SENA" + anyoMesDetalleKey(obj)));
        rst = rst.divide(new BigDecimal("100"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_SENA" + anyoMesDetalleKey(obj), roundValor100(rst));
        // Regla #92 <COTIZACION PAGADA PILA SENA>
        //Se debe colocar valor de la cotización de SENA registrado en el PILA DEPURADA de 
        //acuerdo a la consulta por número DOCUMENTO APORTANTE, 
        //número NUMERO DOCUMENTO ACTUAL DEL COTIZANTE, mes y año de la fiscalización.
        if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaSena().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaSena().toString()).intValue() >= 0) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SENA" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaSena().toString());
        } else {
            if (pilaDepurada != null && pilaDepurada.getValorAportesParafiscalesSena() != null) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SENA" + anyoMesDetalleKey(obj), pilaDepurada.getValorAportesParafiscalesSena());
            } else {
                PilaDepurada pilaDepuradaRealizoAportes = gestorProgramaDao.obtegerPilaDepuradaNominaDetalleCotizanteRealizoAportes(obj.getNomina(), obj.getNominaDetalle());
                //En caso de no encontrar información, se debe colocar valor de la cotización 
                //de SENA registrado en el PILA DEPURADA de acuerdo a la consulta por número 
                //DOCUMENTO APORTANTE, número DOCUMENTO CON EL QUE REALIZO APORTES DEL COTIZANTE, mes y año de la fiscalización"
                if (pilaDepuradaRealizoAportes != null && pilaDepuradaRealizoAportes.getValorAportesParafiscalesSena() != null) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SENA" + anyoMesDetalleKey(obj), pilaDepuradaRealizoAportes.getValorAportesParafiscalesSena());
                    //System.out.println("::ANDRES06:: getValorAportesParafiscalesSena: " + pilaDepuradaRealizoAportes.getValorAportesParafiscalesSena());
                }
            }
        }
        // Regla #93 <AJUSTE SENA>
        rst = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_SENA" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SENA" + anyoMesDetalleKey(obj)));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SENA" + anyoMesDetalleKey(obj), rst);
        // Regla #94 <CONCEPTO AJUSTE SENA>
        rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SENA" + anyoMesDetalleKey(obj)));
        BigDecimal conceAjuSENA = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_SENA" + anyoMesDetalleKey(obj)));
        if (conceAjuSENA == null) {
            conceAjuSENA = BigDecimal.ZERO;
        }
        if (rst.intValue() >= 1000) {
            if (conceAjuSENA.compareTo(BigDecimal.ZERO) == 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SENA" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.MORA_DESC);
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SENA" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.INEXACTO_DESC);
            }
        }
        // Regla #95 <TIPO DE INCUMPLIMIENTO SENA>
        rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SENA" + anyoMesDetalleKey(obj)));
        if (rst.intValue() >= 1000) {
            String conceAjuSena = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_SENA" + anyoMesDetalleKey(obj));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_SENA" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoDescrip(conceAjuSena));
        }
        // Regla #97 <IBC ICBF>
        if (mapMallaval != null && StringUtils.contains("NO", mapMallaval.get("TIENE_ICBF"))) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ICBF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        } else {
            smml10 = salarioMinimo.multiply(new BigDecimal("10"));
            BigDecimal valorCalculado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TOTAL_DEVENGADO" + anyoMesDetalleKey(obj)));
            if (!obj.getNominaDetalle().getTipoCotizante().equals("31")) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ICBF" + anyoMesDetalleKey(obj),
                        infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CCF" + anyoMesDetalleKey(obj)));
            } else {
                tmp2 = gestorProgramaDao.sumaValorLiqConceptoContableIBC(obj.getNominaDetalle(), "0", "0", "0", "0", "0", "6");
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ICBF" + anyoMesDetalleKey(obj), tmp2);
            }
            try {
                //entre mayo 2013  y  diciembre 2016
                fechaCree1 = formateador.parse("30/04/2013");
                fechaCree2 = formateador.parse("01/01/2017");
                fechaCree3 = formateador.parse("31/12/2016");
            } catch (ParseException ex) {
                Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
            }
            // WROJAS - Art.30 Ley 1393 se agregó el siguiente condicional
            if (tipoIdentificacionAportante.intValue() == 5 && "1".equals(obj.getNominaDetalle().getTipoCotizante())
                    && valorCalculado.doubleValue() <= smml10.doubleValue() && fechaNomina.after(fechaCree1) && fechaNomina.before(fechaCree2)) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            } else if (tipoIdentificacionAportante.intValue() == 5 && "1".equals(obj.getNominaDetalle().getTipoCotizante()) && fechaNomina.after(fechaCree3) && valorCalculado.doubleValue() < smml10.doubleValue()) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
            } else {
                if ("SI".equals(obj.getNominaDetalle().getIdaportante().getSujetoPasivoImpuestoCree())) {
                    // CASO DEL CREE
                    //1- Si el campo “SUJETO PASIVO DEL IMPUESTO SOBRE LA RENTA PARA LA EQUIDAD CREE” está marcado con ""SI"" y  el campo 
                    //""TIPO DOCUMENTO APORTANTE"" es igual a ""NI"", se registrará cero (0) en el IBC ICBF así:
                    //a- entre mayo 2013  y  diciembre 2016, a trabajadores que ""devenguen"", individualmente considerados, hasta diez (10) SMLMV (Ley 1607/2012)
                    //b- a partir de enero 2017 a trabajadores que ""devenguen"", individualmente considerados, menos de diez (10) SMLMV (Ley 1819/2016)
                    if (tipoIdentificacionAportante.intValue() == 2) {
                        try {
                            //entre mayo 2013  y  diciembre 2016
                            fechaCree1 = formateador.parse("30/04/2013");
                            fechaCree2 = formateador.parse("01/01/2017");
                            if (fechaNomina != null && fechaNomina.after(fechaCree1) && fechaNomina.before(fechaCree2) && valorCalculado.doubleValue() <= smml10.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ICBF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                //System.out.println("::ANDRES93:: getSujetoPasivoImpuestoCree : " + 0);
                            }
                            //a partir de enero 2017 
                            fechaCree1 = formateador.parse("31/12/2016");
                            if (fechaNomina != null && fechaNomina.after(fechaCree1) && valorCalculado.doubleValue() < smml10.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ICBF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                //System.out.println("::ANDRES94:: valorCalculado : " + 0);
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else{
                    //2- si el campo “SUJETO PASIVO DEL IMPUESTO SOBRE LA RENTA PARA LA EQUIDAD CREE” está marcado con ""SI"", el campo 
                    //TIPO DOCUMENTO APORTANTE"" es diferente a ""NI"",  el periodo fiscalizado es posterior a abril de 2013, y 
                    //existe mas de un trabajador en el mes de la nomina que se esta fiscalizando,  el IBC ICBF será igual a cero (0) para los trabajadores que 
                    //""devenguen"", individualmente considerados, menos de diez (10) salarios mínimos mensuales legales vigentes.
                    //if (tipoIdentificacionAportante.intValue() != 2) {
                        try {
                            //el periodo fiscalizado es posterior a abril de 2013
                            fechaCree1 = formateador.parse("30/04/2013");
                            if (fechaNomina != null && fechaNomina.after(fechaCree1) && cantidadEmp.compareTo(BigDecimal.ONE) > 1 && valorCalculado.doubleValue() < smml10.doubleValue()) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ICBF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                //System.out.println("::ANDRES95:: tipoIdentificacionAportante : " + 0);
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        // Regla #98 <TARIFA ICBF>
        rst = gestorProgramaDao.tarifaICBF(obj.getNomina(), obj.getNominaDetalle());
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ICBF" + anyoMesDetalleKey(obj), rst);
        //BigDecimal rst15 = new BigDecimal("3");
        //rst15 = rst15.divide(new BigDecimal("2"));
        //BigDecimal rst075 = rst15.divide(new BigDecimal("2"));
        //BigDecimal rst225 = rst075.multiply(new BigDecimal("3"));
        if (null != obj.getNominaDetalle().getCondEspEmp()) {
            BigDecimal result = nomModEje.tarifaICBF(obj.getNominaDetalle().getCondEspEmp());
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ICBF" + anyoMesDetalleKey(obj), result);
        }
        // Regla #99 <COTIZACION OBLIGATORIA ICBF>
        rst = mulValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_ICBF" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_ICBF" + anyoMesDetalleKey(obj)));
        rst = rst.divide(new BigDecimal("100"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ICBF" + anyoMesDetalleKey(obj), roundValor100(rst));
        // Regla #101 <COTIZACION PAGADA PILA ICBF>
        if (StringUtils.isNotBlank(obj.getNominaDetalle().getCargueManualPilaIcbf().toString()) && convertValorRegla(obj.getNominaDetalle().getCargueManualPilaIcbf().toString()).intValue() >= 0) {
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ICBF" + anyoMesDetalleKey(obj), obj.getNominaDetalle().getCargueManualPilaIcbf().toString());
        } else {
            if (pilaDepurada != null && pilaDepurada.getValorAportesParafiscalesIcbf() != null) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ICBF" + anyoMesDetalleKey(obj), pilaDepurada.getValorAportesParafiscalesIcbf());
            } else {
                PilaDepurada pilaDepuradaRealizoAportes = gestorProgramaDao.obtegerPilaDepuradaNominaDetalleCotizanteRealizoAportes(obj.getNomina(), obj.getNominaDetalle());
                if (pilaDepuradaRealizoAportes != null && pilaDepuradaRealizoAportes.getValorAportesParafiscalesIcbf() != null) {
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ICBF" + anyoMesDetalleKey(obj), pilaDepuradaRealizoAportes.getValorAportesParafiscalesIcbf());
                }
            }
        }
        // Regla #102 <AJUSTE ICBF>
        rst = minusValorReglas(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ICBF" + anyoMesDetalleKey(obj)),
                infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ICBF" + anyoMesDetalleKey(obj)));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ICBF" + anyoMesDetalleKey(obj), rst);
        // Regla #103 <CONCEPTO AJUSTE ICBF>
        rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ICBF" + anyoMesDetalleKey(obj)));
        BigDecimal conceAjuICBF = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_PAGADA_PILA_ICBF" + anyoMesDetalleKey(obj)));
        if (conceAjuICBF == null) {
            conceAjuICBF = BigDecimal.ZERO;
        }
        if (rst.intValue() >= 1000) {
            if (conceAjuICBF.compareTo(BigDecimal.ZERO) == 0) {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ICBF" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.MORA_DESC);
            } else {
                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ICBF" + anyoMesDetalleKey(obj), ConstantesGestorPrograma.INEXACTO_DESC);
            }
            // Regla #104 <TIPO DE INCUMPLIMIENTO ICBF>
            String conceAjuIcbf = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ICBF" + anyoMesDetalleKey(obj));
            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_ICBF" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoDescrip(conceAjuIcbf));
        }
        // Regla #104 <TIPO DE INCUMPLIMIENTO ICBF>
        //rst = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ICBF" + anyoMesDetalleKey(obj)));
        //if (rst.intValue() >= 1000) {
            //String conceAjuIcbf = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_ICBF" + anyoMesDetalleKey(obj));
            //infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_ICBF" + anyoMesDetalleKey(obj), nomModEje.tipoIncumplimientoDescrip(conceAjuIcbf));
        //}
        // NUEVO REQUERIMIENTO <LIQUIDACIÓN DE SANCIONES EN LA ETAPA DE REQUERIMIENTO>
        // MORA
        /*
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VAL_SANCION_MORA_LIM" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_MORA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_MORA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_NUM_MESES_SANCION_MORA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        // INEXACTITUD
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_BASE_SANCION_INEX" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_INEX" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_INEX" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        //OMISO
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VAL_SANCION_OMISO_LIM" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_NUM_MESES_SANCION_OMISO" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_OMISO" + anyoMesDetalleKey(obj), new BigDecimal("0"));
        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_OMISO" + anyoMesDetalleKey(obj), new BigDecimal("0"));
*/
        if (obj.getNominaDetalle().getAno().intValue() >= 2017) {
            BigDecimal baseSancion = new BigDecimal("0");
            String tipoIncumplimientoSalud = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_SALUD" + anyoMesDetalleKey(obj));
            String tipoIncumplimientoPension = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_PENSION" + anyoMesDetalleKey(obj));
            String tipoIncumplimientoFSP = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_FSP" + anyoMesDetalleKey(obj));
            String tipoIncumplimientoPensionActRiesgo = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INC_PENSION_ACT_ARIESGO" + anyoMesDetalleKey(obj));
            String tipoIncumplimientoARL = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_ARL" + anyoMesDetalleKey(obj));
            String tipoIncumplimientoCCF = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_CCF" + anyoMesDetalleKey(obj));
            String tipoIncumplimientoSENA = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_SENA" + anyoMesDetalleKey(obj));
            String tipoIncumplimietoICBF = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INCUMPLIMIENTO_ICBF" + anyoMesDetalleKey(obj));
            BigDecimal valorResto = new BigDecimal("0");
            switch (obj.getNomina().getTipoActo()) {
                case "requerimiento":
                    // REGLA # 107 <SANCION POR INEXACTITUD>
                    if (null != tipoIncumplimientoSalud && "INEXACTO".equals(tipoIncumplimientoSalud)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoFSP && "INEXACTO".equals(tipoIncumplimientoFSP)) {
                        // para los meses 4 y 5 no debe sumarse
                        valorResto = valorResto.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                        valorResto = valorResto.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj))));
                        baseSancion = baseSancion.add(valorResto);
                    }
                    if (null != tipoIncumplimientoPensionActRiesgo && "INEXACTO".equals(tipoIncumplimientoPensionActRiesgo)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoARL && "INEXACTO".equals(tipoIncumplimientoARL)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoCCF && "INEXACTO".equals(tipoIncumplimientoCCF)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoSENA && "INEXACTO".equals(tipoIncumplimientoSENA)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SENA" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimietoICBF && "INEXACTO".equals(tipoIncumplimietoICBF)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ICBF" + anyoMesDetalleKey(obj))));
                    }
                    // Decreto 376/2021. Req.RF611604
                    if (obj.getNominaDetalle().getAno().intValue() == 2020 && (obj.getNominaDetalle().getMes().intValue() == 4 || obj.getNominaDetalle().getMes().intValue() == 5)) {
                        BigDecimal tarPilaPension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_PILA_PENSION" + anyoMesDetalleKey(obj)));
                        tarPilaPension = tarPilaPension.multiply(new BigDecimal("100"));
                        if (null != tipoIncumplimientoPension && "INEXACTO".equals(tipoIncumplimientoPension)) {
                            if (tarPilaPension.compareTo(new BigDecimal("3")) == 0) {
                                BigDecimal ibcCalculadoPension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)));
                                BigDecimal cotPagadaPilaPensionN = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj)));
                                ibcCalculadoPension = ibcCalculadoPension.multiply(new BigDecimal("3"));
                                ibcCalculadoPension = ibcCalculadoPension.divide(new BigDecimal("100"));
                                if (ibcCalculadoPension.compareTo(cotPagadaPilaPensionN) == 1) {
                                    ibcCalculadoPension = ibcCalculadoPension.subtract(cotPagadaPilaPensionN);
                                    baseSancion = baseSancion.subtract(valorResto);
                                    baseSancion = baseSancion.add(ibcCalculadoPension);
                                }
                            } else {
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj))));
                            }
                        } else {
                            if (null == tipoIncumplimientoPension && tarPilaPension.compareTo(new BigDecimal("3")) == 0) {
                                baseSancion = baseSancion.subtract(valorResto);
                            }
                        }
                    } else {
                        if (null != tipoIncumplimientoPension && "INEXACTO".equals(tipoIncumplimientoPension)) {
                            baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj))));
                        }
                    }
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_BASE_SANCION_INEX" + anyoMesDetalleKey(obj), roundValor100(baseSancion));
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_INEX" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_INEX" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    } else {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_INEX" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("35"));
                        rst = rst.divide(new BigDecimal("100"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_INEX" + anyoMesDetalleKey(obj), rst);
                    }
                    // REGLA # 108 <SANCION POR OMISION>
                    baseSancion = new BigDecimal("0");
                    //01.09.2021  - WRojas. Ajuste como parte de la regla de pagos por cálculo actuarial de pila
                    conceAjuPension = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_PENSION" + anyoMesDetalleKey(obj));
                    if ("VALOR DETERMINADO POR CALCULO ACTUARIAL".equals(conceAjuPension)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj))));
                        // julio 18.2023. Las operaciones se realizan sobre los valores encontrados en la liquidación actual
                        // estas operaciones antes se sumaban, ahora se restan.
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO" + anyoMesDetalleKey(obj))));
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj))));
                        // Se adiciona el <aporte pendiente por pagar> del subsistema de salud, arl y ccf.
                        // ajuste del 07.10.2021 (Caso reportado por Fredy)
                        // ajuste del 12.05.2022 
                        if (null != tipoIncumplimientoSalud && "OMISO".equals(tipoIncumplimientoSalud)) {
                            baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj))));
                        }
                        if (null != tipoIncumplimientoARL && "OMISO".equals(tipoIncumplimientoARL)) {
                            baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ARL" + anyoMesDetalleKey(obj))));
                        }
                        if (null != tipoIncumplimientoCCF && "OMISO".equals(tipoIncumplimientoCCF)) {
                            baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CCF" + anyoMesDetalleKey(obj))));
                        }
                    } else {
                        if (null != tipoIncumplimientoSalud && "OMISO".equals(tipoIncumplimientoSalud)) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj)));
                        }
                        // WROJAS. Dic.04.2020 Se aplica el caso de <CALCULO_ACTUARIAL> para efectos de calcular la base de la sancion
                        if ("X".equals(obj.getNominaDetalle().getCalculoActuarial())) {
                            baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj))));
                            baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                            baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj))));
                        } else {
                            if (null != tipoIncumplimientoPension && "OMISO".equals(tipoIncumplimientoPension)) {
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj))));
                            }
                            if (null != tipoIncumplimientoFSP && "OMISO".equals(tipoIncumplimientoFSP)) {
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj))));
                            }
                            if (null != tipoIncumplimientoPensionActRiesgo && "OMISO".equals(tipoIncumplimientoPensionActRiesgo)) {
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj))));
                            }
                            if (null != tipoIncumplimientoARL && "OMISO".equals(tipoIncumplimientoARL)) {
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj))));
                            }
                            if (null != tipoIncumplimientoCCF && "OMISO".equals(tipoIncumplimientoCCF)) {
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj))));
                            }
                            if (null != tipoIncumplimientoSENA && "OMISO".equals(tipoIncumplimientoSENA)) {
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SENA" + anyoMesDetalleKey(obj))));
                            }
                            if (null != tipoIncumplimietoICBF && "OMISO".equals(tipoIncumplimietoICBF)) {
                                baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ICBF" + anyoMesDetalleKey(obj))));
                            }
                        }
                    }
                    // Calculamos el número de meses
                    int months = nomModEje.conteoMeses(obj.getNomina(), obj.getNominaDetalle());
                   /* String strDateFormat = "yyyy-MM-dd";
                    SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
                    int months = 0;
                    try {
                        org.joda.time.LocalDate fechaActualSancion = LocalDate.now();
                        if (obj.getNomina().getFechaSancion() != null) {
                            Date fechaSancion = obj.getNomina().getFechaSancion();
                            fechaActualSancion = LocalDate.parse(objSDF.format(fechaSancion));
                        }
                        org.joda.time.LocalDate fechaRegistro = LocalDate.parse(obj.getNominaDetalle().getAno() + "-" + mesNomina + "-01");
                        //DateTime fechaActual = DateTime.now();
                        months = org.joda.time.Months.monthsBetween(fechaRegistro, fechaActualSancion).getMonths() + 1;
                        // Rango de suspensión de términos
                        // REQ: Suspensión de sanciones
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            months = months - 10;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_NUM_MESES_SANCION_OMISO" + anyoMesDetalleKey(obj), months);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_NUM_MESES_SANCION_MORA" + anyoMesDetalleKey(obj), months);
                    } catch (Exception ex) {
                        Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_BASE_SANCION_OMISO" + anyoMesDetalleKey(obj), baseSancion);
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_OMISO" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_OMISO" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VAL_SANCION_OMISO_LIM" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_NUM_MESES_SANCION_OMISO" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    } else {
                        if (baseSancion.signum() == -1) {
                            baseSancion = new BigDecimal("0");
                        }
                        rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(months));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_OMISO" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VAL_SANCION_OMISO_LIM" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_OMISO" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        if (rst.doubleValue() > baseSancion.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VAL_SANCION_OMISO_LIM" + anyoMesDetalleKey(obj), baseSancion);
                        }
                    }
                    // REGLA # 109 <SANCION POR MORA>
                    baseSancion = new BigDecimal("0");
                    if (null != tipoIncumplimientoSalud && "MORA".equals(tipoIncumplimientoSalud)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoPension && "MORA".equals(tipoIncumplimientoPension)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoFSP && "MORA".equals(tipoIncumplimientoFSP)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoPensionActRiesgo && "MORA".equals(tipoIncumplimientoPensionActRiesgo)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoARL && "MORA".equals(tipoIncumplimientoARL)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoCCF && "MORA".equals(tipoIncumplimientoCCF)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimientoSENA && "MORA".equals(tipoIncumplimientoSENA)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SENA" + anyoMesDetalleKey(obj))));
                    }
                    if (null != tipoIncumplimietoICBF && "MORA".equals(tipoIncumplimietoICBF)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ICBF" + anyoMesDetalleKey(obj))));
                    }
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_BASE_SANCION_MORA" + anyoMesDetalleKey(obj), baseSancion);
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_MORA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_NUM_MESES_SANCION_MORA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_MORA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VAL_SANCION_MORA_LIM" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    } else {
                        rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(months));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VALOR_SANCION_MORA" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VAL_SANCION_MORA_LIM" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_TARIFA_SANCION_MORA" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        if (rst.doubleValue() > baseSancion.doubleValue()) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#RQ_VAL_SANCION_MORA_LIM" + anyoMesDetalleKey(obj), baseSancion);
                        }
                    }
                    break;
                case "liquidacion":
                    infoNegocio.put("LIQUIDACION", "S");
                    BigDecimal resultado = new BigDecimal("0");
                    BigDecimal consecHojaCalLiquidacion = convertValorRegla(infoNegocio.get("IDHOJACALCULOLIQUIDACION"));
                    // WRojas. Ago.10.2022 Se busca la liquidación anterior bien sea <requerimiento> o <ampliacion>
                    //HojaCalculoLiquidacionDetalle hojaCalLiqDetReqAmpl = gestorProgramaDao.getHojaCalculoLiqDetalleSancion(obj.getNomina(), obj.getNominaDetalle());
                    // WRojas. Ago.12.2022. Se busca el registro anterior de la liquidación de sanciones para el caso de las fechas del primer pago
                    HojaCalculoLiqSanciones hojaCalLiqSanMesAnterior = gestorProgramaDao.obtenerLiquidacionSancionMesAnterior(obj.getNomina(), obj.getNominaDetalle(), consecHojaCalLiquidacion);
                    // PAGOS AJUSTES A SALUD - ANTERIORES AL RDOC
                    resultado = gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD", "1");
                    resultado = resultado.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD", "2"));
                    resultado = resultado.subtract(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD", "3"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_SALUD" + anyoMesDetalleKey(obj), resultado);
                    // PAGOS AJUSTES A SALUD - FECHA PRIMER PAGO [Ejemplo - to_date(’12-MAY-10′,’DD-MON-YYYY’)]
                    Date resultadoFechaSalud = gestorProgramaDao.fechaPrimerPagoPosterior(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD");
                    if (resultadoFechaSalud != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_SALUD" + anyoMesDetalleKey(obj), resultadoFechaSalud);
                    } else { // sino tiene pago en pila se busca en el mes anterior. Si tampoco se encuentra información queda null.
                        if (null != hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud() && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud())) {
                            if (hojaCalLiqSanMesAnterior != null) {
                                resultadoFechaSalud = hojaCalLiqSanMesAnterior.getFechPpagPostRdocSalud();
                                if (resultadoFechaSalud != null) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_SALUD" + anyoMesDetalleKey(obj), resultadoFechaSalud);
                                }
                            }
                        }
                    }
                    // *****************************************************************************************************************************
                    // PAGOS AJUSTES A SALUD - PAGOS POSTERIORES A LA FECHA DE NOTIFICACIÓN
                    resultado = gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD", "1");
                    resultado = resultado.subtract(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD", "2"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD", "3"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD", "4"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_SALUD", "5"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_SALUD" + anyoMesDetalleKey(obj), resultado);
                    Date resultadoFechaPension = null;
                    // CASO PENSION. Se tiene en cuenta el valor de la tarifa que viene de <LIQ_PILA_SIN_DEPURAR> antes de hacer los cálculos
                    // si la tarifa = 26% se dede sacar el 10% para ALTO RIESGO. <OJO> En este momento está para empresas
                    resultado = gestorProgramaDao.valorTarifaSancion(obj.getNomina(), obj.getNominaDetalle()); // Traigo el valor de la columna TARIFA_PENSION
                    if (resultado.compareTo(new BigDecimal("0.26")) == 0) { // OJO ALTO RIESGO (cuando es el 26, 10 es para alto riesgo y 16 para pensión)
                        // PENDIENTE DE IMPLEMENTAR - PENSIÓN
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_PENSION" + anyoMesDetalleKey(obj), null);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_PENSION" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        // PENDIENTE DE IMPLEMENTAR ALTO RIESGO
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_AR" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_AR" + anyoMesDetalleKey(obj), null);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_AR" + anyoMesDetalleKey(obj), new BigDecimal("0"));

                    } else {
                        // PAGOS AJUSTES A PENSION - ANTERIORES AL RDOC
                        resultado = gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION", "1");
                        resultado = resultado.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION", "2"));
                        resultado = resultado.subtract(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION", "3"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_PENSION" + anyoMesDetalleKey(obj), resultado);
                        // PAGOS AJUSTES A PENSION - FECHA PRIMER PAGO
                        resultadoFechaPension = gestorProgramaDao.fechaPrimerPagoPosterior(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION");
                        if (resultadoFechaPension != null) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_PENSION" + anyoMesDetalleKey(obj), resultadoFechaPension);
                        } else { // sino tiene pago en pila se busca en el mes anterior. Si tampoco se encuentra información queda null.
                            if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension())) {
                                if (hojaCalLiqSanMesAnterior != null) {
                                    resultadoFechaPension = hojaCalLiqSanMesAnterior.getFechPpagPostRdocPension();
                                    if (resultadoFechaPension != null) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_PENSION" + anyoMesDetalleKey(obj), resultadoFechaPension);
                                    }
                                }
                            }
                        }
                        // PAGOS AJUSTES A PENSION - PAGOS POSTERIORES A LA FECHA DE NOTIFICACIÓN
                        resultado = gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION", "1");
                        resultado = resultado.subtract(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION", "2"));
                        resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION", "3"));
                        resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION", "4"));
                        resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_COT_OBLIG_PENSION", "5"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_PENSION" + anyoMesDetalleKey(obj), resultado);
                        // Si el valor es del 16% entonces los valores de ALTO RIESGO ES CERO
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_AR" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_AR" + anyoMesDetalleKey(obj), null);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_AR" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    }
                    // PAGOS AJUSTES A FSP - PAGOS ANTERIORES
                    BigDecimal valor1 = gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID", "1");
                    valor1 = valor1.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SUBSI", "1"));
                    valor1 = valor1.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID", "2"));
                    valor1 = valor1.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SUBSI", "2"));
                    valor1 = valor1.subtract(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID", "3"));
                    valor1 = valor1.subtract(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SUBSI", "3"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_FSP" + anyoMesDetalleKey(obj), valor1);
                    // PAGOS AJUSTES A FSP - POSTERIORES A LA FECHA DE NOTIFICACION
                    BigDecimal valor2 = gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID", "1");
                    valor2 = valor2.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SUBSI", "1"));
                    valor2 = valor2.subtract(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SUBSI", "2"));
                    valor2 = valor2.subtract(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID", "2"));
                    valor2 = valor2.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SUBSI", "3"));
                    valor2 = valor2.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID", "3"));
                    valor2 = valor2.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID", "4"));
                    valor2 = valor2.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SUBSI", "4"));
                    valor2 = valor2.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SUBSI", "5"));
                    valor2 = valor2.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID", "5"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_FSP" + anyoMesDetalleKey(obj), valor2);
                    // PAGOS AJUSTES A FSP - FECHA DEL PRIMER PAGO
                    Date resultadoFechaFSP = gestorProgramaDao.fechaPrimerPagoPosterior(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID");
                    //if (resultadoFechaFSP != null && valor2.compareTo(BigDecimal.ZERO) != 0) { -- julio 18.2023
                    if (resultadoFechaFSP != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_FSP" + anyoMesDetalleKey(obj), resultadoFechaFSP);
                    } else { // sino tiene pago en pila se busca en el mes anterior. Si tampoco se encuentra información queda null.
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP())) {
                            //resultadoFechaFSP = gestorProgramaDao.fechaPrimerPagoPosteriorNoPila(obj.getNomina(), obj.getNominaDetalle(), "APORTE_FSOLID_PENS_SOLID");
                            if (hojaCalLiqSanMesAnterior != null) {
                                resultadoFechaFSP = hojaCalLiqSanMesAnterior.getFechPpagPostRdocFsp();
                                if (resultadoFechaFSP != null) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_FSP" + anyoMesDetalleKey(obj), resultadoFechaFSP);
                                }
                            }
                        }
                    }
                    // PAGOS AJUSTES ARL - PAGOS ANTERIORES
                    resultado = gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP", "1");
                    resultado = resultado.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP", "2"));
                    resultado = resultado.subtract(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP", "3"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_ARL" + anyoMesDetalleKey(obj), resultado);
                    // PAGOS AJUSTES ARL - FECHA DEL PRIMER PAGO
                    Date resultadoFechaARL = gestorProgramaDao.fechaPrimerPagoPosterior(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP");
                    if (resultadoFechaARL != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_ARL" + anyoMesDetalleKey(obj), resultadoFechaARL);
                    } else { // sino tiene pago en pila se busca en el mes anterior. Si tampoco se encuentra información queda null.
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl())) {
                            if (hojaCalLiqSanMesAnterior != null) {
                                resultadoFechaARL = hojaCalLiqSanMesAnterior.getFechPpagPostRdocArl();
                                if (resultadoFechaARL != null) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_ARL" + anyoMesDetalleKey(obj), resultadoFechaARL);
                                }
                            }
                        }
                    }
                    // PAGOS AJUSTES ARL - POSTERIORES A LA FECHA DE NOTIFICACION
                    resultado = gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP", "1");
                    resultado = resultado.subtract(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP", "2"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP", "3"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP", "4"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "COT_OBLIGATORIA_ARP", "5"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_ARL" + anyoMesDetalleKey(obj), resultado);
                    // PAGOS AJUSTES SENA - PAGOS ANTERIORES
                    resultado = gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA", "1");
                    resultado = resultado.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA", "2"));
                    resultado = resultado.subtract(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA", "3"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_SENA" + anyoMesDetalleKey(obj), resultado);
                    // PAGOS AJUSTES SENA - FECHA DEL PRIMER PAGO
                    Date resultadoFechaSena = gestorProgramaDao.fechaPrimerPagoPosterior(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA");
                    if (resultadoFechaSena != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_SENA" + anyoMesDetalleKey(obj), resultadoFechaSena);
                    } else { // sino tiene pago en pila se busca en el mes anterior. Si tampoco se encuentra información queda null.
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoSena() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSena())) {
                            if (hojaCalLiqSanMesAnterior != null) {
                                resultadoFechaSena = hojaCalLiqSanMesAnterior.getFechPpagPostRdocSena();
                                if (resultadoFechaSena != null) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_SENA" + anyoMesDetalleKey(obj), resultadoFechaSena);
                                }
                            }
                        }
                    }
                    // PAGOS AJUSTES SENA - POSTERIORES A LA FECHA DE NOTIFICACION
                    resultado = gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA", "1");
                    resultado = resultado.subtract(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA", "2"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA", "3"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA", "4"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_SENA", "5"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_SENA" + anyoMesDetalleKey(obj), resultado);
                    // PAGOS AJUSTES ICBF - PAGOS ANTERIORES
                    resultado = gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF", "1");
                    resultado = resultado.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF", "2"));
                    resultado = resultado.subtract(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF", "3"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_ICBF" + anyoMesDetalleKey(obj), resultado);
                    // PAGOS AJUSTES ICBF - FECHA DEL PRIMER PAGO
                    Date resultadoFechaIcbf = gestorProgramaDao.fechaPrimerPagoPosterior(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF");
                    if (resultadoFechaIcbf != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_ICBF" + anyoMesDetalleKey(obj), resultadoFechaIcbf);
                    } else { // sino tiene pago en pila se busca en el mes anterior. Si tampoco se encuentra información queda null.
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoIcbf() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoIcbf())) {
                            if (hojaCalLiqSanMesAnterior != null) {
                                resultadoFechaIcbf = hojaCalLiqSanMesAnterior.getFechPpagPostRdocIcbf();
                                if (resultadoFechaIcbf != null) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_ICBF" + anyoMesDetalleKey(obj), resultadoFechaIcbf);
                                }
                            }
                        }
                    }
                    // PAGOS AJUSTES ICBF - POSTERIORES A LA FECHA DE NOTIFICACION
                    resultado = gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF", "1");
                    resultado = resultado.subtract(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF", "2"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF", "3"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF", "4"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APOR_PARAFIS_ICBF", "5"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_ICBF" + anyoMesDetalleKey(obj), resultado);
                    // PAGOS AJUSTES CCF - PAGOS ANTERIORES
                    resultado = gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC", "1");
                    resultado = resultado.add(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC", "2"));
                    resultado = resultado.subtract(gestorProgramaDao.pagosAnterioresAlRdoc(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC", "3"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_CCF" + anyoMesDetalleKey(obj), resultado);
                    // PAGOS AJUSTES CCF - FECHA DEL PRIMER PAGO
                    Date resultadoFechaCcf = gestorProgramaDao.fechaPrimerPagoPosterior(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC");
                    if (resultadoFechaCcf != null) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_CCF" + anyoMesDetalleKey(obj), resultadoFechaCcf);
                    } else { // sino tiene pago en pila se busca en el mes anterior. Si tampoco se encuentra información queda null.
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf())) {
                            if (hojaCalLiqSanMesAnterior != null) {
                                resultadoFechaCcf = hojaCalLiqSanMesAnterior.getFechPpagPostRdocCcf();
                                if (resultadoFechaCcf != null) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#FECH_PPAG_POST_RDOC_CCF" + anyoMesDetalleKey(obj), resultadoFechaCcf);
                                }
                            }
                        }
                    }
                    // PAGOS AJUSTES CCF - POSTERIORES A LA FECHA DE NOTIFICACION
                    resultado = gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC", "1");
                    resultado = resultado.subtract(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC", "2"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC", "3"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC", "4"));
                    resultado = resultado.add(gestorProgramaDao.pagosPosterioresAlRdocAnterioresAlLimite(obj.getNomina(), obj.getNominaDetalle(), "VALOR_APORTES_CCF_IBC", "5"));
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_CCF" + anyoMesDetalleKey(obj), resultado);
                    // *********************************** INICIAMOS PROCESO DE LIQUIDACIÓN DE SANCIONES ***********************************
                    BigDecimal baseSancion35 = new BigDecimal("0"),
                     aporteLiquidado = new BigDecimal("0");
                    //BigDecimal aporteLiquidado = new BigDecimal("0");
                    BigDecimal pagoAnterior = new BigDecimal("0"),
                     limiteBase = new BigDecimal("0");
                    BigDecimal baseSancion60 = new BigDecimal("0");
                    if (hojaCalLiqDetReqAmpl != null) {
                        // 14.07.2023 - Se elimina la verificación del tipo de incumplimiento del acto anterior.
                        // 18.07.2023 - Se agrega la verificación del tipo de incumplimiento de la liquidación en curso.
                        if ((hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud() != null && "INEXACTO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud())) || (tipoIncumplimientoSalud != null && "INEXACTO".equals(tipoIncumplimientoSalud))) {
                            // ************************   SUBSISTEMA DE SALUD - <INEXACTITUD> - CÁLCULO DEL 35%  *********************************
                            aporteLiquidado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj)));
                            limiteBase = aporteLiquidado.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_SALUD" + anyoMesDetalleKey(obj))));
                            if (limiteBase.compareTo(BigDecimal.ZERO) == -1) {
                                limiteBase = new BigDecimal("0");
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#LIM_BASE_SANCION_INEX_SAL" + anyoMesDetalleKey(obj), limiteBase);
                            pagoAnterior = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_SALUD" + anyoMesDetalleKey(obj)));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) { //  hubo pagos anteriores
                                baseSancion35 = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_SALUD" + anyoMesDetalleKey(obj)));
                                rst = new BigDecimal("0");
                                if (baseSancion35.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SAL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (baseSancion35.compareTo(limiteBase) == 1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SAL" + anyoMesDetalleKey(obj), limiteBase);
                                        rst = mulValorReglas(limiteBase, new BigDecimal("35"));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SAL" + anyoMesDetalleKey(obj), baseSancion35);
                                        rst = mulValorReglas(baseSancion35, new BigDecimal("35"));
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_INEX_SAL" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_INEX_SAL" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                            // *******************************  SUBSISTEMA DE SALUD - <INEXACTITUD> - CÁLCULO DEL 60%  *******************************
                            baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj))));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) { // Existencia de pago anterior
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "COT_OBLIGATORIA_SALUD"));
                                baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "COT_OBLIGATORIA_SALUD"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "COT_OBLIGATORIA_SALUD"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "COT_OBLIGATORIA_SALUD"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "COT_OBLIGATORIA_SALUD"));
                            }
                            if (baseSancion60.compareTo(BigDecimal.ZERO) == 1) {
                                // sumamos las bases
                                BigDecimal sumaBases = baseSancion60.add(baseSancion35);
                                //baseSancion60 = baseSancion60.add(baseSancion35);
                                if (sumaBases.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SAL_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (sumaBases.compareTo(limiteBase) == 1) {
                                        baseSancion60 = limiteBase.subtract(baseSancion35);
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SAL_V2" + anyoMesDetalleKey(obj), baseSancion60);
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_INEX_SAL_V2" + anyoMesDetalleKey(obj), new BigDecimal("60"));
                                    rst = mulValorReglas(baseSancion60, new BigDecimal("60"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_INEX_SAL_V2" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                        }
                        // ******************************************* SUBSISTEMA DE PENSION ************************
                        BigDecimal tarPilaPension = new BigDecimal("0");
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension() != null && "INEXACTO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension()) || (tipoIncumplimientoPension != null && "INEXACTO".equals(tipoIncumplimientoPension))) {
                            limiteBase = new BigDecimal("0");
                            aporteLiquidado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj)));
                            limiteBase = aporteLiquidado.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_PENSION" + anyoMesDetalleKey(obj))));
                            //BigDecimal tarPilaPension = new BigDecimal("0");
                            if (hojaCalLiqDetReqAmpl.getTarifaPilaPension() != null) {
                                tarPilaPension = hojaCalLiqDetReqAmpl.getTarifaPilaPension();
                            }
                            tarPilaPension = tarPilaPension.multiply(new BigDecimal("100"));
                            if (obj.getNominaDetalle().getAno().intValue() == 2020 && (obj.getNominaDetalle().getMes().intValue() == 4 || obj.getNominaDetalle().getMes().intValue() == 5) && tarPilaPension.compareTo(new BigDecimal("3")) == 0) {
                                BigDecimal ibcCalculadoP = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)));
                                ibcCalculadoP = ibcCalculadoP.multiply(new BigDecimal("3"));
                                ibcCalculadoP = ibcCalculadoP.divide(new BigDecimal("100"));
                                limiteBase = ibcCalculadoP.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_PENSION" + anyoMesDetalleKey(obj))));
                            }
                            if (limiteBase.compareTo(BigDecimal.ZERO) == -1) {
                                limiteBase = new BigDecimal("0");
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#LIM_BASE_SANCION_INEX_PEN" + anyoMesDetalleKey(obj), limiteBase);
                            //***************************  SUBSISTEMA DE PENSION <INEXACTITUD> - CÁLCULO DEL 35%  **********************************
                            // Agosto 17.2023. Se tra la versión anterior porque las condiciones iniciales se colocaron para el LIMITE_BASE
                            baseSancion35 = new BigDecimal("0");
                            pagoAnterior = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_PENSION" + anyoMesDetalleKey(obj)));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) { //  hubo pagos anteriores
                                baseSancion35 = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_PENSION" + anyoMesDetalleKey(obj)));
                                if (baseSancion35.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_PEN" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (baseSancion35.compareTo(limiteBase) == 1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_PEN" + anyoMesDetalleKey(obj), limiteBase);
                                        rst = mulValorReglas(limiteBase, new BigDecimal("35"));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_PEN" + anyoMesDetalleKey(obj), baseSancion35);
                                        rst = mulValorReglas(baseSancion35, new BigDecimal("35"));
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_INEX_PEN" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_INEX_PEN" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                            //*********************************  SUBSISTEMA DE PENSION <INEXACTITUD> - CÁLCULO DEL 60% *****************************
                            baseSancion60 = new BigDecimal("0");
                            if (hojaCalLiqDetReqAmpl.getTarifaPilaPension() != null) {
                                tarPilaPension = hojaCalLiqDetReqAmpl.getTarifaPilaPension();
                            }
                            tarPilaPension = tarPilaPension.multiply(new BigDecimal("100"));
                            if (obj.getNominaDetalle().getAno().intValue() == 2020 && (obj.getNominaDetalle().getMes().intValue() == 4 || obj.getNominaDetalle().getMes().intValue() == 5) && tarPilaPension.compareTo(new BigDecimal("3")) == 0) {
                                BigDecimal ibcCalculadoP = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#IBC_CALCULADO_PENSION" + anyoMesDetalleKey(obj)));
                                ibcCalculadoP = ibcCalculadoP.multiply(new BigDecimal("3"));
                                ibcCalculadoP = ibcCalculadoP.divide(new BigDecimal("100"));
                                baseSancion60 = ibcCalculadoP.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_PAGADA_PILA_PENSION" + anyoMesDetalleKey(obj))));
                            } else {
                                baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj))));
                                if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) { //  hubo pagos anteriores
                                    baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "APORTE_COT_OBLIG_PENSION"));
                                    baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "APORTE_COT_OBLIG_PENSION"));
                                    baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "APORTE_COT_OBLIG_PENSION"));
                                    baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "APORTE_COT_OBLIG_PENSION"));
                                    baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "APORTE_COT_OBLIG_PENSION"));
                                }
                            }
                            if (baseSancion60.compareTo(BigDecimal.ZERO) == 1) {
                                BigDecimal sumaBases = baseSancion60.add(baseSancion35);
                                //baseSancion60 = baseSancion60.add(baseSancion35);
                                if (sumaBases.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_PEN_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (sumaBases.compareTo(limiteBase) == 1) {
                                        baseSancion60 = limiteBase.subtract(baseSancion35);
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_PEN_V2" + anyoMesDetalleKey(obj), baseSancion60);
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_INEX_PEN_V2" + anyoMesDetalleKey(obj), new BigDecimal("60"));
                                    rst = mulValorReglas(baseSancion60, new BigDecimal("60"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_INEX_PEN_V2" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                        }
                        // **********************************   SUBSISTEMA DE FSP <INEXACTITUD> - CÁLCULO DEL 35%    **********************************
                        baseSancion35 = new BigDecimal("0");
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP() != null && "INEXACTO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP()) || (tipoIncumplimientoFSP != null && "INEXACTO".equals(tipoIncumplimientoFSP))) {
                            aporteLiquidado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj)));
                            aporteLiquidado = aporteLiquidado.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj))));
                            limiteBase = aporteLiquidado.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_FSP" + anyoMesDetalleKey(obj))));
                            if (limiteBase.compareTo(BigDecimal.ZERO) == -1) {
                                limiteBase = new BigDecimal("0");
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#LIM_BASE_SANCION_INEX_FSP" + anyoMesDetalleKey(obj), limiteBase);
                            if (hojaCalLiqDetReqAmpl.getTarifaPilaPension() != null) {
                                tarPilaPension = hojaCalLiqDetReqAmpl.getTarifaPilaPension();
                            }
                            tarPilaPension = tarPilaPension.multiply(new BigDecimal("100"));
                            if (obj.getNominaDetalle().getAno().intValue() == 2020 && (obj.getNominaDetalle().getMes().intValue() == 4 || obj.getNominaDetalle().getMes().intValue() == 5) && tarPilaPension.compareTo(new BigDecimal("3")) == 0) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_FSP" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            } else {
                                baseSancion35 = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_FSP" + anyoMesDetalleKey(obj)));
                                if (baseSancion35.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_FSP" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (baseSancion35.compareTo(limiteBase) == 1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_FSP" + anyoMesDetalleKey(obj), limiteBase);
                                        rst = mulValorReglas(limiteBase, new BigDecimal("35"));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_FSP" + anyoMesDetalleKey(obj), baseSancion35);
                                        rst = mulValorReglas(baseSancion35, new BigDecimal("35"));
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_INEX_FSP" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_INEX_FSP" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                            // **********************************   SUBSISTEMA DE FSP <INEXACTITUD> - CÁLCULO DEL 60%    **********************************
                            baseSancion60 = new BigDecimal("0");
                            if (hojaCalLiqDetReqAmpl.getTarifaPilaPension() != null) {
                                tarPilaPension = hojaCalLiqDetReqAmpl.getTarifaPilaPension();
                            }
                            tarPilaPension = tarPilaPension.multiply(new BigDecimal("100"));
                            if (obj.getNominaDetalle().getAno().intValue() == 2020 && (obj.getNominaDetalle().getMes().intValue() == 4 || obj.getNominaDetalle().getMes().intValue() == 5) && tarPilaPension.compareTo(new BigDecimal("3")) == 0) {
                                infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_FSP_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                            } else {
                                baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                                baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj))));
                                //if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "APORTE_FSOLID_PENS_SOLID"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "APORTE_FSOLID_PENS_SUBSI"));
                                baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "APORTE_FSOLID_PENS_SOLID"));
                                baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "APORTE_FSOLID_PENS_SUBSI"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "APORTE_FSOLID_PENS_SOLID"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "APORTE_FSOLID_PENS_SUBSI"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "APORTE_FSOLID_PENS_SOLID"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "APORTE_FSOLID_PENS_SUBSI"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "APORTE_FSOLID_PENS_SOLID"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "APORTE_FSOLID_PENS_SUBSI"));
                                if (baseSancion60.compareTo(BigDecimal.ZERO) == 1) {
                                    BigDecimal sumaBases = baseSancion60.add(baseSancion35);
                                    //baseSancion60 = baseSancion60.add(baseSancion35);
                                    if (sumaBases.compareTo(new BigDecimal("1000")) == -1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_FSP_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                    } else {
                                        if (sumaBases.compareTo(limiteBase) == 1) {
                                            baseSancion60 = limiteBase.subtract(baseSancion35);
                                        }
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_FSP_V2" + anyoMesDetalleKey(obj), baseSancion60);
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_INEX_FSP_V2" + anyoMesDetalleKey(obj), new BigDecimal("60"));
                                        rst = mulValorReglas(baseSancion60, new BigDecimal("60"));
                                        rst = rst.divide(new BigDecimal("100"));
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_INEX_FSP_V2" + anyoMesDetalleKey(obj), rst);
                                    }
                                }
                            }
                        }
                        // ******************************** SUBSISTEMA DE ALTO RIESGO <INEXACTITUD>  ******************* 
                        baseSancion35 = new BigDecimal("0");
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoAriesgo() != null && "INEXACTO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoAriesgo()) || (tipoIncumplimientoPensionActRiesgo != null && "INEXACTO".equals(tipoIncumplimientoPensionActRiesgo))) {
                            aporteLiquidado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO" + anyoMesDetalleKey(obj)));
                            limiteBase = aporteLiquidado.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_AR" + anyoMesDetalleKey(obj))));
                            if (limiteBase.compareTo(BigDecimal.ZERO) == -1) {
                                limiteBase = new BigDecimal("0");
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#LIM_BASE_SANCION_INEX_ARI" + anyoMesDetalleKey(obj), limiteBase);
                            // ******************* SUBSISTEMA DE ALTO RIESGO <INEXACTITUD> - CÁLCULO DEL 35% ********************
                            pagoAnterior = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_AR" + anyoMesDetalleKey(obj)));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion35 = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_AR" + anyoMesDetalleKey(obj)));
                                if (baseSancion35.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARI" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (baseSancion35.compareTo(limiteBase) == 1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARI" + anyoMesDetalleKey(obj), limiteBase);
                                        rst = mulValorReglas(limiteBase, new BigDecimal("35"));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARI" + anyoMesDetalleKey(obj), baseSancion35);
                                        rst = mulValorReglas(baseSancion35, new BigDecimal("35"));
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_INEX_ARI" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_INEX_ARI" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                            // ******************* SUBSISTEMA DE ALTO RIESGO <INEXACTITUD> - CÁLCULO DEL 60% ********************                   
                            baseSancion60 = new BigDecimal("0");
                            baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj))));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                                baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                            }
                            if (baseSancion60.compareTo(BigDecimal.ZERO) == 1) {
                                BigDecimal sumaBases = baseSancion60.add(baseSancion35);
                                //baseSancion60 = baseSancion60.add(baseSancion35);
                                if (sumaBases.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARI_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (sumaBases.compareTo(limiteBase) == 1) {
                                        baseSancion60 = limiteBase.subtract(baseSancion35);
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARI_V2" + anyoMesDetalleKey(obj), baseSancion60);
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_INEX_ARI_V2" + anyoMesDetalleKey(obj), new BigDecimal("60"));
                                    rst = mulValorReglas(baseSancion60, new BigDecimal("60"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_INEX_ARI_V2" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                        }
                        // ******************************** SUBSISTEMA DE ARL <INEXACTITUD>  ******************* 
                        baseSancion35 = new BigDecimal("0");
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl() != null && "INEXACTO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl()) || (tipoIncumplimientoARL != null && "INEXACTO".equals(tipoIncumplimientoARL))) {
                            aporteLiquidado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ARL" + anyoMesDetalleKey(obj)));
                            limiteBase = aporteLiquidado.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_ARL" + anyoMesDetalleKey(obj))));
                            if (limiteBase.compareTo(BigDecimal.ZERO) == -1) {
                                limiteBase = new BigDecimal("0");
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#LIM_BASE_SANCION_INEX_ARL" + anyoMesDetalleKey(obj), limiteBase);
                            // ******************* SUBSISTEMA DE ARL <INEXACTITUD> - CÁLCULO DEL 35% ********************
                            pagoAnterior = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_ARL" + anyoMesDetalleKey(obj)));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion35 = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_ARL" + anyoMesDetalleKey(obj)));
                                if (baseSancion35.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (baseSancion35.compareTo(limiteBase) == 1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARL" + anyoMesDetalleKey(obj), limiteBase);
                                        rst = mulValorReglas(limiteBase, new BigDecimal("35"));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARL" + anyoMesDetalleKey(obj), baseSancion35);
                                        rst = mulValorReglas(baseSancion35, new BigDecimal("35"));
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_INEX_ARL" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_INEX_ARL" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                            // ******************* SUBSISTEMA DE ARL <INEXACTITUD> - CÁLCULO DEL 60% ********************                   
                            baseSancion60 = new BigDecimal("0");
                            baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj))));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                                baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "COT_OBLIGATORIA_ARP"));
                            }
                            if (baseSancion60.compareTo(BigDecimal.ZERO) == 1) {
                                BigDecimal sumaBases = baseSancion60.add(baseSancion35);
                                //baseSancion60 = baseSancion60.add(baseSancion35);
                                if (sumaBases.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARL_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (sumaBases.compareTo(limiteBase) == 1) {
                                        baseSancion60 = limiteBase.subtract(baseSancion35);
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ARL_V2" + anyoMesDetalleKey(obj), baseSancion60);
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_INEX_ARL_V2" + anyoMesDetalleKey(obj), new BigDecimal("60"));
                                    rst = mulValorReglas(baseSancion60, new BigDecimal("60"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_INEX_ARL_V2" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                        }
                        // ******************************** SUBSISTEMA SENA <INEXACTITUD>  ******************* 
                        baseSancion35 = new BigDecimal("0");
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoSena() != null && "INEXACTO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSena()) || (tipoIncumplimientoSENA != null && "INEXACTO".equals(tipoIncumplimientoSENA))) {
                            aporteLiquidado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_SENA" + anyoMesDetalleKey(obj)));
                            limiteBase = aporteLiquidado.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_SENA" + anyoMesDetalleKey(obj))));
                            if (limiteBase.compareTo(BigDecimal.ZERO) == -1) {
                                limiteBase = new BigDecimal("0");
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#LIM_BASE_SANCION_INEX_SENA" + anyoMesDetalleKey(obj), limiteBase);
                            // ******************* SUBSISTEMA SENA <INEXACTITUD> - CÁLCULO DEL 35% ********************
                            pagoAnterior = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_SENA" + anyoMesDetalleKey(obj)));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion35 = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_SENA" + anyoMesDetalleKey(obj)));
                                if (baseSancion35.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SENA" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (baseSancion35.compareTo(limiteBase) == 1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SENA" + anyoMesDetalleKey(obj), limiteBase);
                                        rst = mulValorReglas(limiteBase, new BigDecimal("35"));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SENA" + anyoMesDetalleKey(obj), baseSancion35);
                                        rst = mulValorReglas(baseSancion35, new BigDecimal("35"));
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_INEX_SENA" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_INEX_SENA" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                            // ******************* SUBSISTEMA SENA <INEXACTITUD> - CÁLCULO DEL 60% ********************                   
                            baseSancion60 = new BigDecimal("0");
                            baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SENA" + anyoMesDetalleKey(obj))));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "VALOR_APOR_PARAFIS_SENA"));
                                baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "VALOR_APOR_PARAFIS_SENA"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "VALOR_APOR_PARAFIS_SENA"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "VALOR_APOR_PARAFIS_SENA"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "VALOR_APOR_PARAFIS_SENA"));
                            }
                            if (baseSancion60.compareTo(BigDecimal.ZERO) == 1) {
                                BigDecimal sumaBases = baseSancion60.add(baseSancion35);
                                //baseSancion60 = baseSancion60.add(baseSancion35);
                                if (sumaBases.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SENA_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (sumaBases.compareTo(limiteBase) == 1) {
                                        baseSancion60 = limiteBase.subtract(baseSancion35);
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_SENA_V2" + anyoMesDetalleKey(obj), baseSancion60);
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_INEX_SENA_V2" + anyoMesDetalleKey(obj), new BigDecimal("60"));
                                    rst = mulValorReglas(baseSancion60, new BigDecimal("60"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_INEX_SENA_V2" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                        }
                        // ******************************** SUBSISTEMA ICBF <INEXACTITUD>  ******************* 
                        baseSancion35 = new BigDecimal("0");
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoIcbf() != null && "INEXACTO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoIcbf()) || (tipoIncumplimietoICBF != null && "INEXACTO".equals(tipoIncumplimietoICBF))) {
                            aporteLiquidado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ICBF" + anyoMesDetalleKey(obj)));
                            limiteBase = aporteLiquidado.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_ICBF" + anyoMesDetalleKey(obj))));
                            if (limiteBase.compareTo(BigDecimal.ZERO) == -1) {
                                limiteBase = new BigDecimal("0");
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#LIM_BASE_SANCION_INEX_ICBF" + anyoMesDetalleKey(obj), limiteBase);
                            // ******************* SUBSISTEMA ICBF <INEXACTITUD> - CÁLCULO DEL 35% ********************
                            pagoAnterior = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_ICBF" + anyoMesDetalleKey(obj)));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion35 = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_ICBF" + anyoMesDetalleKey(obj)));
                                if (pagoAnterior.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ICBF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (baseSancion35.compareTo(limiteBase) == 1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ICBF" + anyoMesDetalleKey(obj), limiteBase);
                                        rst = mulValorReglas(limiteBase, new BigDecimal("35"));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ICBF" + anyoMesDetalleKey(obj), baseSancion35);
                                        rst = mulValorReglas(baseSancion35, new BigDecimal("35"));
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_INEX_ICBF" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_INEX_ICBF" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                            // ******************* SUBSISTEMA ICBF <INEXACTITUD> - CÁLCULO DEL 60% ********************                   
                            baseSancion60 = new BigDecimal("0");
                            baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ICBF" + anyoMesDetalleKey(obj))));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "VALOR_APOR_PARAFIS_ICBF"));
                                baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "VALOR_APOR_PARAFIS_ICBF"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "VALOR_APOR_PARAFIS_ICBF"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "VALOR_APOR_PARAFIS_ICBF"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "VALOR_APOR_PARAFIS_ICBF"));
                            }
                            if (baseSancion60.compareTo(BigDecimal.ZERO) == 1) {
                                BigDecimal sumaBases = baseSancion60.add(baseSancion35);
                                //baseSancion60 = baseSancion60.add(baseSancion35);
                                if (sumaBases.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ICBF_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (sumaBases.compareTo(limiteBase) == 1) {
                                        baseSancion60 = limiteBase.subtract(baseSancion35);
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_ICBF_V2" + anyoMesDetalleKey(obj), baseSancion60);
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_INEX_ICBF_V2" + anyoMesDetalleKey(obj), new BigDecimal("60"));
                                    rst = mulValorReglas(baseSancion60, new BigDecimal("60"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_INEX_ICBF_V2" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                        }
                        // ******************************** SUBSISTEMA CCF <INEXACTITUD>  ******************* 
                        baseSancion35 = new BigDecimal("0");
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf() != null && "INEXACTO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf()) || (tipoIncumplimientoCCF != null && "INEXACTO".equals(tipoIncumplimientoCCF))) {
                            aporteLiquidado = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CCF" + anyoMesDetalleKey(obj)));
                            limiteBase = aporteLiquidado.subtract(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_CCF" + anyoMesDetalleKey(obj))));
                            if (limiteBase.compareTo(BigDecimal.ZERO) == -1) {
                                limiteBase = new BigDecimal("0");
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#LIM_BASE_SANCION_INEX_CCF" + anyoMesDetalleKey(obj), limiteBase);
                            // ******************* SUBSISTEMA CCF <INEXACTITUD> - CÁLCULO DEL 35% ********************
                            pagoAnterior = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAG_ANT_RDOC_CCF" + anyoMesDetalleKey(obj)));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion35 = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#PAGOS_ENTRE_RDOC_CCF" + anyoMesDetalleKey(obj)));
                                if (baseSancion35.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_CCF" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (baseSancion35.compareTo(limiteBase) == 1) {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_CCF" + anyoMesDetalleKey(obj), limiteBase);
                                        rst = mulValorReglas(limiteBase, new BigDecimal("35"));
                                    } else {
                                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_CCF" + anyoMesDetalleKey(obj), baseSancion35);
                                        rst = mulValorReglas(baseSancion35, new BigDecimal("35"));
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_INEX_CCF" + anyoMesDetalleKey(obj), new BigDecimal("35"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_INEX_CCF" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                            // ******************* SUBSISTEMA CCF <INEXACTITUD> - CÁLCULO DEL 60% ********************                   
                            baseSancion60 = new BigDecimal("0");
                            baseSancion60 = baseSancion60.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj))));
                            if (pagoAnterior.compareTo(BigDecimal.ZERO) == 1) {
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "1", "POSTERIOR", "VALOR_APORTES_CCF_IBC"));
                                baseSancion60 = baseSancion60.subtract(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "2", "POSTERIOR", "VALOR_APORTES_CCF_IBC"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "3", "POSTERIOR", "VALOR_APORTES_CCF_IBC"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "4", "POSTERIOR", "VALOR_APORTES_CCF_IBC"));
                                baseSancion60 = baseSancion60.add(gestorProgramaDao.baseSancionInexactitud(obj.getNomina(), obj.getNominaDetalle(), "5", "POSTERIOR", "VALOR_APORTES_CCF_IBC"));
                            }
                            if (baseSancion60.compareTo(BigDecimal.ZERO) == 1) {
                                BigDecimal sumaBases = baseSancion60.add(baseSancion35);
                                //baseSancion60 = baseSancion60.add(baseSancion35);
                                if (sumaBases.compareTo(new BigDecimal("1000")) == -1) {
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_CCF_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                                } else {
                                    if (sumaBases.compareTo(limiteBase) == 1) {
                                        baseSancion60 = limiteBase.subtract(baseSancion35);
                                    }
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_INEX_CCF_V2" + anyoMesDetalleKey(obj), baseSancion60);
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_INEX_CCF_V2" + anyoMesDetalleKey(obj), new BigDecimal("60"));
                                    rst = mulValorReglas(baseSancion60, new BigDecimal("60"));
                                    rst = rst.divide(new BigDecimal("100"));
                                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_INEX_CCF_V2" + anyoMesDetalleKey(obj), rst);
                                }
                            }
                        }
                    } // *********************** AQUI TERMINAN TODAS LAS REGLAS DE INEXACTITUD CUANDO HAY LIQUIDACIÓN ANTERIOR ***********************
                    // *********************     SANCION POR OMISIÓN *** EN SALUD al 10% ***********************
                    baseSancion = new BigDecimal("0");
                    int numMeses = 0;
                    if (tipoIncumplimientoSalud != null && "OMISO".equals(tipoIncumplimientoSalud)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj))));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    } else {
                        if (null != hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud() && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud()) && resultadoFechaSalud != null && resultadoFechaSalud.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj)));
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaSalud, null, "1");
                        }
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (numMeses > 0 && baseSancion.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_SAL" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_OMISO_SAL" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_OMISO_SAL" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_OMISO_SAL" + anyoMesDetalleKey(obj), numMeses);
                    }
                    // *********************     SANCION POR OMISIÓN *** EN SALUD al 5% ***********************
                    baseSancion = new BigDecimal("0");
                    if (null != hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud() && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud()) && resultadoFechaSalud != null && resultadoFechaSalud.before(obj.getNomina().getFechaLimRespRdoc())) {
                        //baseSancion = new BigDecimal(hojaCalLiqDet.getAjusteSalud());
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_SAL_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_OMISO_SAL_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaSalud, null, "1");
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (numMeses > 0 && baseSancion.compareTo(BigDecimal.ZERO) == 1) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_OMISO_SAL_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_OMISO_SAL_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    //**********************************  SANCIONES POR OMISIÓN ***********************************************
                    BigDecimal baseSancionPension = new BigDecimal("0");
                    BigDecimal baseSancionFSP = new BigDecimal("0");
                    BigDecimal baseSancionAR = new BigDecimal("0");
                    conceAjuPension = (String) infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#CONCEPTO_AJUSTE_PENSION" + anyoMesDetalleKey(obj));
                    if ("VALOR DETERMINADO POR CALCULO ACTUARIAL".equals(conceAjuPension) && obj.getNominaDetalle().getFechaFinPagoCalAct() == null) {
                        //************** CASO PENSIONES al 10%
                        baseSancionPension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj)));
                        //************** CASO FSP al 10%
                        baseSancionFSP = baseSancionFSP.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                        baseSancionFSP = baseSancionFSP.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj))));
                        //************** CASO ALTO RIESGO al 10%
                        baseSancionAR = baseSancionAR.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO" + anyoMesDetalleKey(obj))));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    } else {
                        if ("VALOR DETERMINADO POR CALCULO ACTUARIAL".equals(hojaCalLiqDetReqAmpl.getConceptoAjustePension()) && obj.getNominaDetalle().getFechaFinPagoCalAct() != null && obj.getNominaDetalle().getFechaFinPagoCalAct().after(obj.getNomina().getFechaLimRespRdoc())) {
                            //************** CASO PENSIONES al 10%
                            baseSancionPension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj)));
                            //************** CASO FSP al 10%
                            baseSancionFSP = baseSancionFSP.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                            baseSancionFSP = baseSancionFSP.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj))));
                            //************** CASO ALTO RIESGO al 10%
                            baseSancionAR = baseSancionAR.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO" + anyoMesDetalleKey(obj))));
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNominaDetalle().getFechaFinPagoCalAct(), null, "1");
                        }
                    }
                    // ****************** CALCULAMOS OMISIÓN PENSIÓN AL 10%
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_PEN" + anyoMesDetalleKey(obj), baseSancionPension);
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_OMISO_PEN" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (numMeses > 0 && baseSancionPension.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_OMISO_PEN" + anyoMesDetalleKey(obj), numMeses);
                        rst = mulValorReglas(baseSancionPension, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancionPension = baseSancionPension.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancionPension) == 1) {
                            rst = baseSancionPension;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_OMISO_PEN" + anyoMesDetalleKey(obj), rst);
                    }
                    // ****************** CALCULAMOS OMISIÓN ALTO RIESGO AL 10%
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_ARI" + anyoMesDetalleKey(obj), baseSancionAR);
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_OMISO_ARI" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                    if (numMeses > 0 && baseSancionAR.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_OMISO_ARI" + anyoMesDetalleKey(obj), numMeses);
                        rst = mulValorReglas(baseSancionAR, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancionAR = baseSancionAR.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancionAR) == 1) {
                            rst = baseSancionAR;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_OMISO_ARI" + anyoMesDetalleKey(obj), rst);
                    }
                    // ****************** CALCULAMOS OMISIÓN FSP AL 10%
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_FSP" + anyoMesDetalleKey(obj), baseSancionFSP);
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_OMISO_FSP" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                    if (numMeses > 0 && baseSancionFSP.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_OMISO_FSP" + anyoMesDetalleKey(obj), numMeses);
                        rst = mulValorReglas(baseSancionFSP, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancionFSP = baseSancionFSP.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancionFSP) == 1) {
                            rst = baseSancionFSP;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_OMISO_FSP" + anyoMesDetalleKey(obj), rst);
                    }
                    //**********************************  SANCIONES POR OMISIÓN al 5% **********************************************
                    baseSancionPension = new BigDecimal("0");
                    baseSancionFSP = new BigDecimal("0");
                    baseSancionAR = new BigDecimal("0");
                    numMeses = 0;
                    if ("VALOR DETERMINADO POR CALCULO ACTUARIAL".equals(hojaCalLiqDetReqAmpl.getConceptoAjustePension()) && obj.getNominaDetalle().getFechaFinPagoCalAct() != null && obj.getNominaDetalle().getFechaFinPagoCalAct().before(obj.getNomina().getFechaLimRespRdoc())) {
                        //************** CASO PENSIONES al 5%
                        baseSancionPension = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj)));
                        //************** CASO FSP al 5%
                        baseSancionFSP = baseSancionFSP.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                        baseSancionFSP = baseSancionFSP.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj))));
                        //************** CASO ALTO RIESGO al 5%
                        baseSancionAR = baseSancionAR.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COT_OBL_ADIC_ACT_ALTORIESGO" + anyoMesDetalleKey(obj))));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNominaDetalle().getFechaFinPagoCalAct(), null, "1");
                    }
                    // ****************** CALCULAMOS OMISIÓN PENSIÓN AL 5%
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_PEN_V2" + anyoMesDetalleKey(obj), baseSancionPension);
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_OMISO_PEN_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (numMeses > 0 && baseSancionPension.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_OMISO_PEN_V2" + anyoMesDetalleKey(obj), numMeses);
                        rst = mulValorReglas(baseSancionPension, new BigDecimal("5"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        if (rst.compareTo(baseSancionPension) == 1) {
                            rst = baseSancionPension;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_OMISO_PEN_V2" + anyoMesDetalleKey(obj), rst);
                    }
                    // ****************** CALCULAMOS OMISIÓN ALTO RIESGO AL 5%
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_ARI_V2" + anyoMesDetalleKey(obj), baseSancionAR);
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_OMISO_ARI_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                    if (numMeses > 0 && baseSancionAR.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_OMISO_ARI_V2" + anyoMesDetalleKey(obj), numMeses);
                        rst = mulValorReglas(baseSancionAR, new BigDecimal("5"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        if (rst.compareTo(baseSancionAR) == 1) {
                            rst = baseSancionAR;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_OMISO_ARI_V2" + anyoMesDetalleKey(obj), rst);
                    }
                    // ****************** CALCULAMOS OMISIÓN FSP AL 5%
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_FSP_V2" + anyoMesDetalleKey(obj), baseSancionFSP);
                    infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_OMISO_FSP_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                    if (numMeses > 0 && baseSancionFSP.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_OMISO_FSP_V2" + anyoMesDetalleKey(obj), numMeses);
                        rst = mulValorReglas(baseSancionFSP, new BigDecimal("5"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        if (rst.compareTo(baseSancionFSP) == 1) {
                            rst = baseSancionFSP;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_OMISO_FSP_V2" + anyoMesDetalleKey(obj), rst);
                    }
                    // *********************     SANCION POR OMISIÓN *** EN ARL al 10% ***********************
                    baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if (null != tipoIncumplimientoARL && "OMISO".equals(tipoIncumplimientoARL)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj))));
                        // Calculamos el número de meses entre el mes de la nómina que estamos fiscalizando y el mes actual
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    } else {
                        if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl()) && resultadoFechaARL != null && resultadoFechaARL.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ARL" + anyoMesDetalleKey(obj)));
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaARL, null, "1");
                        }
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (numMeses > 0 && baseSancion.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_ARL" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_OMISO_ARL" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_OMISO_ARL" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_OMISO_ARL" + anyoMesDetalleKey(obj), numMeses);
                    }
                    // *********************     SANCION POR OMISIÓN *** EN ARL al 5% ***********************
                    baseSancion = new BigDecimal("0");
                    if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl()) && resultadoFechaARL != null && resultadoFechaARL.before(obj.getNomina().getFechaLimRespRdoc())) {
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ARL" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_ARL_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_OMISO_ARL_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaARL, null, "1");
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        //numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        if (numMeses > 0 && baseSancion.compareTo(BigDecimal.ZERO) == 1) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_OMISO_ARL_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_OMISO_ARL_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    // *********************     SANCION POR OMISIÓN *** CCF al 10% ***********************
                    baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if ("OMISO".equals(tipoIncumplimientoCCF)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj))));
                        // Calculamos el número de meses entre el mes de la nómina que estamos fiscalizando y el mes actual
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    } else {
                        if ("OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf()) && resultadoFechaCcf != null && resultadoFechaCcf.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CCF" + anyoMesDetalleKey(obj)));
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaCcf, null, "1");
                        }
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (numMeses > 0 && baseSancion.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_CCF" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_OMISO_CCF" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_OMISO_CCF" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_OMISO_CCF" + anyoMesDetalleKey(obj), numMeses);
                    }
                    // *********************     SANCION POR OMISIÓN *** CCF al 5% ***********************
                    baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if (hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf() != null && "OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf()) && resultadoFechaCcf != null && resultadoFechaCcf.before(obj.getNomina().getFechaLimRespRdoc())) {
                        //baseSancion = new BigDecimal(hojaCalLiqDet.getAjusteCcf());
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CCF" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_OMISO_CCF_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_OMISO_CCF_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaCcf, null, "1");
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (numMeses > 0 && baseSancion.compareTo(BigDecimal.ZERO) == 1) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_OMISO_CCF_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_OMISO_CCF_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    // *****************************  INICIAN LAS REGLAS DE SANCIONES POR MORA *********************************************************
                    // BASE SANCION POR *** MORA *** EN SALUD al 10%
                    BigDecimal baseSancionSalud = new BigDecimal("0");
                    numMeses = 0;
                    if ("MORA".equals(tipoIncumplimientoSalud)) {
                        baseSancionSalud = baseSancionSalud.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SALUD" + anyoMesDetalleKey(obj))));
                    } else {
                        if (("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud()) && (null == tipoIncumplimientoSalud || "INEXACTO".equals(tipoIncumplimientoSalud))) && resultadoFechaSalud != null && resultadoFechaSalud.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancionSalud = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj)));
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaSalud, null, "1");
                        }
                    }
                    // Ahora calculamos el número de meses
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud()) && "MORA".equals(tipoIncumplimientoSalud)) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    }
                    if ("OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud()) && "MORA".equals(tipoIncumplimientoSalud)) {
                        numMeses = nomModEje.getObtenerMeses(null, obj.getNomina().getFechaSancion(), resultadoFechaSalud, "2");
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (baseSancionSalud.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_SAL" + anyoMesDetalleKey(obj), baseSancionSalud);
                    } else {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_SAL" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                    }
                    if (numMeses > 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_MORA_SAL" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancionSalud, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancionSalud = baseSancionSalud.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancionSalud) == 1) {
                            rst = baseSancionSalud;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_MORA_SAL" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_MORA_SAL" + anyoMesDetalleKey(obj), numMeses);
                    }
                    // BASE SANCION POR *** MORA *** EN SALUD al 5%
                    baseSancionSalud = new BigDecimal("0");
                    numMeses = 0;
                    if (resultadoFechaSalud != null && resultadoFechaSalud.before(obj.getNomina().getFechaLimRespRdoc()) && "MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud()) && ("INEXACTO".equals(tipoIncumplimientoSalud) || tipoIncumplimientoSalud == null)) {
                        baseSancionSalud = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CALCULADA_SALUD" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_SAL_V2" + anyoMesDetalleKey(obj), baseSancionSalud);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_MORA_SAL_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_SAL_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSalud()) && resultadoFechaSalud.after(obj.getNomina().getFechaNoticRdoc()) && resultadoFechaSalud.before(obj.getNomina().getFechaLimRespRdoc())) {
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaSalud, null, "1");
                        }
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (numMeses > 0 && baseSancionSalud.compareTo(BigDecimal.ZERO) == 1) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_MORA_SAL_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancionSalud, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancionSalud) == 1) {
                                rst = baseSancionSalud;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_SAL_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    // *************************************************************************************************************************
                    // BASE SANCION POR *** MORA *** EN PENSIÓN al 10% 
                    baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if ("MORA".equals(tipoIncumplimientoPension)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION" + anyoMesDetalleKey(obj))));
                    } else {
                        if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension()) && (null == tipoIncumplimientoPension || "INEXACTO".equals(tipoIncumplimientoPension)) && resultadoFechaPension != null && resultadoFechaPension.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj)));
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaPension, null, "1");
                        }
                    }
                    // Ahora calculamos el número de meses
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension()) && "MORA".equals(tipoIncumplimientoPension)) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    }
                    if ("OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension()) && "MORA".equals(tipoIncumplimientoPension)) {
                        numMeses = nomModEje.getObtenerMeses(null, obj.getNomina().getFechaSancion(), resultadoFechaPension, "2");
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_PEN" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_MORA_PEN" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_MORA_PEN" + anyoMesDetalleKey(obj), numMeses);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_MORA_PEN" + anyoMesDetalleKey(obj), rst);
                    }
                    // BASE SANCION POR *** MORA *** EN PENSION al 5%
                    baseSancion = new BigDecimal("0");
                    if (resultadoFechaPension != null && resultadoFechaPension.before(obj.getNomina().getFechaLimRespRdoc()) && ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension()) && (null == tipoIncumplimientoPension || "INEXACTO".equals(tipoIncumplimientoPension)))) {
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_PENSION" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_PEN_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_MORA_PEN_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoPension()) && resultadoFechaPension.after(obj.getNomina().getFechaNoticRdoc()) && resultadoFechaPension.before(obj.getNomina().getFechaLimRespRdoc())) {
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaPension, null, "1");
                        }
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_MORA_PEN_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_PEN_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    // *******************************************************************************************************************************
                    // BASE SANCION POR *** MORA *** EN FSP al 10% 
                    baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if ("MORA".equals(tipoIncumplimientoFSP)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SOLIDARIDAD" + anyoMesDetalleKey(obj))));
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_FSP_SUBCUEN_SUBSISTEN" + anyoMesDetalleKey(obj))));
                    } else {
                        if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP()) && (null == tipoIncumplimientoFSP || "INEXACTO".equals(tipoIncumplimientoFSP)) && resultadoFechaFSP != null && resultadoFechaFSP.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj)));
                            baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj))));
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaFSP, null, "1");
                        }
                    }
                    // Ahora calculamos el número de meses
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP()) && "MORA".equals(tipoIncumplimientoFSP)) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    }
                    if ("OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP()) && "MORA".equals(tipoIncumplimientoFSP)) {
                        numMeses = nomModEje.getObtenerMeses(null, obj.getNomina().getFechaSancion(), resultadoFechaFSP, "2");
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_FSP" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_MORA_FSP" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_MORA_FSP" + anyoMesDetalleKey(obj), numMeses);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_MORA_FSP" + anyoMesDetalleKey(obj), rst);
                    }
                    // BASE SANCION POR *** MORA *** EN FSP al 5%
                    baseSancion = new BigDecimal("0");
                    if (resultadoFechaFSP != null && resultadoFechaFSP.before(obj.getNomina().getFechaLimRespRdoc()) && ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP()) && (null == tipoIncumplimientoFSP || "INEXACTO".equals(tipoIncumplimientoFSP)))) {
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SOLIDARIDAD" + anyoMesDetalleKey(obj)));
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_FSP_SUB_SUBSISTENCIA" + anyoMesDetalleKey(obj))));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_FSP_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_MORA_FSP_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoFSP()) && resultadoFechaFSP.after(obj.getNomina().getFechaNoticRdoc()) && resultadoFechaFSP.before(obj.getNomina().getFechaLimRespRdoc())) {
                            numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaFSP, null, "1");
                        }
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_MORA_FSP_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_FSP_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    // *******************************************************************************************************************************
                    // BASE SANCION POR *** MORA *** EN ALTORIESGO al 10%
                    BigDecimal baseSancionAltoRiesgo = new BigDecimal("0");
                    numMeses = 0;
                    if ("MORA".equals(tipoIncumplimientoPensionActRiesgo)) {
                        baseSancionAltoRiesgo = baseSancionAltoRiesgo.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj))));
                    } else {
                        if (("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoAriesgo()) && (null == tipoIncumplimientoPensionActRiesgo || "INEXACTO".equals(tipoIncumplimientoPensionActRiesgo))) && resultadoFechaSalud != null && resultadoFechaSalud.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancionAltoRiesgo = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TIPO_INC_PENSION_ACT_ARIESGO" + anyoMesDetalleKey(obj)));
                        }
                    }
                    // Ahora calculamos el número de meses
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoAriesgo()) && "MORA".equals(tipoIncumplimientoPensionActRiesgo)) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    }
                    //if ("OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoAriesgo()) && "MORA".equals(tipoIncumplimientoPensionActRiesgo)) {
                    //numMeses = getObtenerMeses(resultadoFechaSalud, obj.getNomina().getFechaSancion());
                    //}
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (numMeses > 0 && baseSancionAltoRiesgo.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_ARI" + anyoMesDetalleKey(obj), baseSancionAltoRiesgo);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_MORA_ARI" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancionAltoRiesgo, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancionAltoRiesgo = baseSancionAltoRiesgo.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancionAltoRiesgo) == 1) {
                            rst = baseSancionAltoRiesgo;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_MORA_ARI" + anyoMesDetalleKey(obj), rst);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_MORA_ARI" + anyoMesDetalleKey(obj), numMeses);
                    }
                    // BASE SANCION POR *** MORA *** EN ALTORIESGO al 5%
                    baseSancionAltoRiesgo = new BigDecimal("0");
                    numMeses = 0;
                    if (resultadoFechaSalud != null && resultadoFechaSalud.before(obj.getNomina().getFechaLimRespRdoc()) && "MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoAriesgo()) && ("INEXACTO".equals(tipoIncumplimientoPensionActRiesgo) || tipoIncumplimientoPensionActRiesgo == null)) {
                        baseSancionAltoRiesgo = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_PENSION_ACT_ALTO_RIES" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_ARI_V2" + anyoMesDetalleKey(obj), baseSancionAltoRiesgo);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_MORA_ARI_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_ARI_V2" + anyoMesDetalleKey(obj), new BigDecimal("0"));
                        //if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoAriesgo()) && resultadoFechaSalud.after(obj.getNomina().getFechaNoticRdoc()) && resultadoFechaSalud.before(obj.getNomina().getFechaLimRespRdoc())) {
                        //numMeses = getObtenerMeses(obj.getNominaDetalle(), resultadoFechaSalud);
                        //}
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (numMeses > 0 && baseSancionAltoRiesgo.compareTo(BigDecimal.ZERO) == 1) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_MORA_ARI_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancionAltoRiesgo, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancionAltoRiesgo) == 1) {
                                rst = baseSancionAltoRiesgo;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_ARI_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    // *************************************************************************************************************************
                    // BASE SANCION POR *** MORA *** EN ARL al 10% 
                    baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if (null != tipoIncumplimientoARL && "MORA".equals(tipoIncumplimientoARL)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ARL" + anyoMesDetalleKey(obj))));
                    } else {
                        if (!"MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl()) && resultadoFechaARL != null && resultadoFechaARL.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ARL" + anyoMesDetalleKey(obj)));
                        }
                    }
                    // Ahora calculamos el número de meses
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl()) && (null == tipoIncumplimientoARL || "INEXACTO".equals(tipoIncumplimientoARL)) && resultadoFechaARL != null && resultadoFechaARL.after(obj.getNomina().getFechaLimRespRdoc())) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaARL, null, "1");
                    }
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl()) && null != tipoIncumplimientoARL && "MORA".equals(tipoIncumplimientoARL)) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    }
                    if ("OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl()) && null != tipoIncumplimientoARL && "MORA".equals(tipoIncumplimientoARL)) {
                        numMeses = nomModEje.getObtenerMeses(null, obj.getNomina().getFechaSancion(), resultadoFechaARL, "2");
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 1) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_ARL" + anyoMesDetalleKey(obj), baseSancion);
                    } else {
                        baseSancion = new BigDecimal("0");
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_ARL" + anyoMesDetalleKey(obj), baseSancion);
                    }
                    if (numMeses > 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_MORA_ARL" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_MORA_ARL" + anyoMesDetalleKey(obj), numMeses);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_MORA_ARL" + anyoMesDetalleKey(obj), rst);
                    }
                    // BASE SANCION POR *** MORA *** EN ARL al 5%
                    baseSancion = new BigDecimal("0");
                    if (resultadoFechaARL != null && resultadoFechaARL.before(obj.getNomina().getFechaLimRespRdoc()) && "MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoArl())) {
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ARL" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_ARL_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_MORA_ARL_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaARL, null, "1");
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_MORA_ARL_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_ARL_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    // *******************************************************************************************************************************                    
                    // BASE SANCION POR *** MORA *** EN SENA al 10% 
                    baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if (null != tipoIncumplimientoSENA && "MORA".equals(tipoIncumplimientoSENA)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_SENA" + anyoMesDetalleKey(obj))));
                    } else {
                        if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSena()) && (null == tipoIncumplimientoSENA || "INEXACTO".equals(tipoIncumplimientoSENA)) && resultadoFechaSena != null && resultadoFechaSena.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_SENA" + anyoMesDetalleKey(obj)));
                        }
                    }
                    // Ahora calculamos el número de meses
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSena()) && (null == tipoIncumplimientoSENA || "INEXACTO".equals(tipoIncumplimientoSENA)) && resultadoFechaSena != null && resultadoFechaSena.after(obj.getNomina().getFechaLimRespRdoc())) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaSena, null, "1");
                    }
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSena()) && null != tipoIncumplimientoSENA && "MORA".equals(tipoIncumplimientoSENA)) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_SENA" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_MORA_SENA" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_MORA_SENA" + anyoMesDetalleKey(obj), numMeses);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_MORA_SENA" + anyoMesDetalleKey(obj), rst);
                    }
                    // BASE SANCION POR *** MORA *** SENA al 5%
                    baseSancion = new BigDecimal("0");
                    if (resultadoFechaSena != null && resultadoFechaSena.before(obj.getNomina().getFechaLimRespRdoc()) && "MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoSena())) {
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_SENA" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_SENA_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_MORA_SENA_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaSena, null, "1");
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_MORA_SENA_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_SENA_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }
                    // *******************************************************************************************************************************                    
                    // BASE SANCION POR *** MORA *** EN ICBF al 10% 
                    /*baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if (null != tipoIncumplimietoICBF && "MORA".equals(tipoIncumplimietoICBF)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_ICBF" + anyoMesDetalleKey(obj))));
                    } else {
                        if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoIcbf()) && (null == tipoIncumplimietoICBF || "INEXACTO".equals(tipoIncumplimietoICBF)) && resultadoFechaIcbf != null && resultadoFechaIcbf.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ICBF" + anyoMesDetalleKey(obj)));
                        }
                    }
                    // Ahora calculamos el número de meses
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoIcbf()) && (null == tipoIncumplimietoICBF || "INEXACTO".equals(tipoIncumplimietoICBF)) && resultadoFechaIcbf != null && resultadoFechaIcbf.after(obj.getNomina().getFechaLimRespRdoc())) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaIcbf, null, "1");
                    }
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoIcbf()) && null != tipoIncumplimietoICBF && "MORA".equals(tipoIncumplimietoICBF)) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_ICBF" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_MORA_ICBF" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_MORA_ICBF" + anyoMesDetalleKey(obj), numMeses);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_MORA_ICBF" + anyoMesDetalleKey(obj), rst);
                    }*/
                    // BASE SANCION POR *** MORA *** ICBF al 5%
                    /*baseSancion = new BigDecimal("0");
                    if (resultadoFechaIcbf != null && resultadoFechaIcbf.before(obj.getNomina().getFechaLimRespRdoc()) && "MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoIcbf())) {
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_ICBF" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_ICBF_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_MORA_ICBF_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaIcbf, null, "1");
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_MORA_ICBF_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_ICBF_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }*/
                    // *******************************************************************************************************************************                    
                    // BASE SANCION POR *** MORA *** EN CCF al 10% 
                   /* baseSancion = new BigDecimal("0");
                    numMeses = 0;
                    if (null != tipoIncumplimientoCCF && "MORA".equals(tipoIncumplimientoCCF)) {
                        baseSancion = baseSancion.add(convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#AJUSTE_CCF" + anyoMesDetalleKey(obj))));
                    } else {
                        if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf()) && resultadoFechaCcf != null && resultadoFechaCcf.after(obj.getNomina().getFechaLimRespRdoc())) {
                            baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CCF" + anyoMesDetalleKey(obj)));
                        }
                    }
                    // Ahora calculamos el número de meses
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf()) && (null == tipoIncumplimientoCCF || "INEXACTO".equals(tipoIncumplimientoCCF)) && resultadoFechaCcf != null && resultadoFechaCcf.after(obj.getNomina().getFechaLimRespRdoc())) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaCcf, null, "1");
                    }
                    if ("MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf()) && null != tipoIncumplimientoCCF && "MORA".equals(tipoIncumplimientoCCF)) {
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), obj.getNomina().getFechaSancion(), null, "1");
                    }
                    if ("OMISO".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf()) && null != tipoIncumplimientoCCF && "MORA".equals(tipoIncumplimientoCCF)) {
                        numMeses = nomModEje.getObtenerMeses(null, obj.getNomina().getFechaSancion(), resultadoFechaCcf, "2");
                    }
                    if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                        numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                    } else {
                        numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                    }
                    if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_CCF" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARIFA_SANCION_MORA_CCF" + anyoMesDetalleKey(obj), new BigDecimal("10"));
                        rst = mulValorReglas(baseSancion, new BigDecimal("10"));
                        rst = rst.divide(new BigDecimal("100"));
                        rst = rst.multiply(new BigDecimal(numMeses));
                        baseSancion = baseSancion.multiply(new BigDecimal("2"));
                        if (rst.compareTo(baseSancion) == 1) {
                            rst = baseSancion;
                        }
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MESES_SANCION_MORA_CCF" + anyoMesDetalleKey(obj), numMeses);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALOR_SANCION_MORA_CCF" + anyoMesDetalleKey(obj), rst);
                    }*/
                    // BASE SANCION POR *** MORA *** CCF al 5%
                    /*baseSancion = new BigDecimal("0");
                    if (resultadoFechaCcf != null && resultadoFechaCcf.before(obj.getNomina().getFechaLimRespRdoc()) && "MORA".equals(hojaCalLiqDetReqAmpl.getTipoIncumplimientoCcf())) {
                        baseSancion = convertValorRegla(infoNegocio.get(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#COTIZ_OBL_CCF" + anyoMesDetalleKey(obj)));
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#BASE_SANCION_MORA_CCF_V2" + anyoMesDetalleKey(obj), baseSancion);
                        infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#TARI_SANCION_MORA_CCF_V2" + anyoMesDetalleKey(obj), new BigDecimal("5"));
                        numMeses = nomModEje.getObtenerMeses(obj.getNominaDetalle(), resultadoFechaCcf, null, "1");
                        if (obj.getNominaDetalle().getAno().intValue() <= 2020 && obj.getNominaDetalle().getMes().intValue() <= 5) {
                            numMeses = numMeses - 10; // Se descuentan 10 meses del período de mayo de 2020 a febrero de 2021
                        } else {
                            numMeses = numMeses - nomModEje.restarMesesProporcional(obj.getNominaDetalle().getAno().intValue(), obj.getNominaDetalle().getMes().intValue());
                        }
                        if (baseSancion.compareTo(BigDecimal.ZERO) == 1 && numMeses > 0) {
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#NUM_MES_SANCION_MORA_CCF_V2" + anyoMesDetalleKey(obj), numMeses);
                            rst = mulValorReglas(baseSancion, new BigDecimal("5"));
                            rst = rst.divide(new BigDecimal("100"));
                            rst = rst.multiply(new BigDecimal(numMeses));
                            if (rst.compareTo(baseSancion) == 1) {
                                rst = baseSancion;
                            }
                            infoNegocio.put(obj.getNominaDetalle().getNumeroIdentificacionActual() + "#VALO_SANCION_MORA_CCF_V2" + anyoMesDetalleKey(obj), rst);
                        }
                    }*/
                    break;
            }
        }
        // Finaliza el proceso para una cédula	        
    } // Aquí termina el método de <procesarReglasNoFormula>

    void cargarLstAdministradoraPila(GestorProgramaDao gestorProgramaDao) {
        try {
            LST_ADMINISTRADORA_PILA = gestorProgramaDao.getLstAdministradoraPila();
            //System.out.println("::ANDRES4:: size LST_ADMINISTRADORA_PILA: " + LST_ADMINISTRADORA_PILA.size());
        } catch (Exception e) {
            LOG.error("cargarLstAdministradoraPila - ERROR Exception", e);
        }
    }

    @Override
    public void cerrarLstAdministradoraPila() {
        LST_ADMINISTRADORA_PILA = null;
    }

    private String obtenerMesNomina(int mesNom) {
        String mesNomina = "";
        if (mesNom > 9) {
            mesNomina = String.valueOf(mesNom);
        } else {
            mesNomina = "0" + mesNom;
        }
        return mesNomina;
    }
}
