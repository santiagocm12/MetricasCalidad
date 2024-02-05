package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion;


import co.gov.ugpp.parafiscales.servicios.liquidador.archivotipo.v1.ArchivoTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.contextotransaccionaltipo.v1.ContextoTransaccionalTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.detallehojacalculotipo.v1.DetalleHojaCalculoTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.HojaCalculoLiquidacion;
import co.gov.ugpp.parafiscales.servicios.liquidador.entity.HojaCalculoLiquidacionDetalle;
import co.gov.ugpp.parafiscales.servicios.liquidador.errortipo.v1.ErrorTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.hojacalculotipo.v1.HojaCalculoTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.jpa.HojaCalculoLiquidacionDao;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.AppException;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.DateUtil;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.ErrorEnum;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.ErrorUtil;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.SQLConection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;
/**
 *
 * @author franzjr
 */
@Stateless
@TransactionManagement
public class HojaCalculoLiquidacionFacadeImpl implements HojaCalculoLiquidacionFacade {
   
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HojaCalculoLiquidacionFacadeImpl.class);
    @Override
    public HojaCalculoTipo consultarPorId(String idHojaCalculoLiquidacion, ContextoTransaccionalTipo contextoSolicitud, EntityManager entityManager) throws AppException {

        final List<ErrorTipo> errorTipoList = new ArrayList<ErrorTipo>();

        try {
            HojaCalculoTipo hojaCalculoTipo = null;
            if (idHojaCalculoLiquidacion != null && !idHojaCalculoLiquidacion.equals("")) {

                HojaCalculoLiquidacionDao hojaCalculoLiquidacionDao = new HojaCalculoLiquidacionDao(entityManager);
                HojaCalculoLiquidacion result = hojaCalculoLiquidacionDao.hojaCalculoLiquidacionPorId(idHojaCalculoLiquidacion);

                if (result != null) {
                    hojaCalculoTipo = new HojaCalculoTipo();
                    hojaCalculoTipo.setCodEstado(result.getCodesstado());
                    hojaCalculoTipo.setDesEstado(result.getDesestado());

                    hojaCalculoTipo.setEsIncumplimiento(result.getEsincumplimiento() != null ? (result.getEsincumplimiento() == '1' ? true : false) : false);

                    if (result.getId() != null) {
                        hojaCalculoTipo.setIdHojaCalculoLiquidacion(result.getId().toString());
                    }

                    for (HojaCalculoLiquidacionDetalle detalleEnt : result.getHojaCalculoLiquidacionDetalCollection()) {
                        DetalleHojaCalculoTipo detalleSrv = assembleServicio(detalleEnt);
                        hojaCalculoTipo.getDetalleHojaCalculo().add(detalleSrv);
                    }
                    return hojaCalculoTipo;
                } else {
                    errorTipoList.add(ErrorUtil.buildErrorTipo(ErrorEnum.ERROR_INTEGRIDAD_REFERENCIAL, "El HojaCalculoLiquidacion buscando con el id: " + idHojaCalculoLiquidacion + " no existe"));
                    throw new AppException(errorTipoList);
                }

            } else {
                errorTipoList.add(ErrorUtil.buildErrorTipo(ErrorEnum.ERROR_INTEGRIDAD_REFERENCIAL, "El campo idHojaCalculoLiquidacion es obligatorio"));
                throw new AppException(errorTipoList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorTipoList.add(ErrorUtil.buildErrorTipo(ErrorEnum.ERROR_NO_ESPERADO,e.getMessage()));
            throw new AppException(errorTipoList);
        }

    }

    @Override
    public ArchivoTipo generarExcel(String idHojaCalculoLiquidacion, ContextoTransaccionalTipo contextoSolicitud, EntityManager entityManager) throws AppException {

        final List<ErrorTipo> errorTipoList = new ArrayList<ErrorTipo>();

        try {
            HojaCalculoLiquidacionDao hojaCalculoLiquidacionDao = new HojaCalculoLiquidacionDao(entityManager);
            HojaCalculoLiquidacion hojaCalculoLiquidacion = hojaCalculoLiquidacionDao.hojaCalculoLiquidacionPorId(idHojaCalculoLiquidacion);
            
            if (hojaCalculoLiquidacion == null) {
                //errorTipoList.add(ErrorUtil.buildErrorTipo(ErrorEnum.ERROR_INTEGRIDAD_REFERENCIAL, "El HojaCalculoLiquidacion buscando con el id: " + idHojaCalculoLiquidacion + " no existe"));
                LOG.error("EXCEPCION ERROR generarExcel: El HojaCalculoLiquidacion buscando con el id: " + idHojaCalculoLiquidacion + " no existe, se seteo a 0");
                    idHojaCalculoLiquidacion = "0";
                    //throw new AppException(errorTipoList);
                }
                ArchivoTipo archivo = new ArchivoTipo();
                archivo.setCodTipoMIMEArchivo("xls");

                //if (idHojaCalculoLiquidacion != null && !idHojaCalculoLiquidacion.equals("")) 
                //{
                SQLConection sqlConection = SQLConection.getInstance();
                ResultSet rs = sqlConection.consultaLiquidador(4, "", "", "", idHojaCalculoLiquidacion);

                if (rs != null) 
                {
                    archivo.setValContenidoArchivo(new CrearExcelHojaCalculoLiquidacion().CrearExcelLiquidacion(rs));
                    return archivo;
                } 
                else 
                {
                    errorTipoList.add(ErrorUtil.buildErrorTipo(ErrorEnum.ERROR_INTEGRIDAD_REFERENCIAL, "EXCEPCION ERROR generarExcel consultaLiquidador con ID: " + idHojaCalculoLiquidacion + "  no devolvio resultados"));
                    LOG.error("EXCEPCION ERROR generarExcel consultaLiquidador con ID: " + idHojaCalculoLiquidacion + " no devolvio resultados");
                    throw new AppException(errorTipoList);
                }
                //}

                /*} else {
                    errorTipoList.add(ErrorUtil.buildErrorTipo(ErrorEnum.ERROR_INTEGRIDAD_REFERENCIAL, "El campo idHojaCalculoLiquidacion es obligatorio"));
                    LOG.error("EXCEPCION ERROR generarExcel: El campo idHojaCalculoLiquidacion es obligatorio");
                    throw new AppException(errorTipoList);
                }*/

        } catch (Exception e) {
            e.printStackTrace();
            errorTipoList.add(ErrorUtil.buildErrorTipo(ErrorEnum.ERROR_NO_ESPERADO,e.getMessage()));
            throw new AppException(errorTipoList);
        }

    }

    public DetalleHojaCalculoTipo assembleServicio(HojaCalculoLiquidacionDetalle entidad) throws Exception {
        DetalleHojaCalculoTipo detalleHojaCalculo = new DetalleHojaCalculoTipo();

        try {

            if (entidad.getIdhojacalculoliquidacion() != null) {
                detalleHojaCalculo.setIdHojaCalculoLiquidacionDetalle(entidad.getIdhojacalculoliquidacion().getId().toString());
            }
            detalleHojaCalculo.setIdNominaDetalle(entidad.getIdnominadetalle().toString());
            detalleHojaCalculo.setIdPilaDetalle(entidad.getIdpiladetalle().toString());
            detalleHojaCalculo.setIdConciliacionContableDetalle(entidad.getIdconciliacioncontabledetalle().toString());
            if (entidad.getAportaId() != null) {
                detalleHojaCalculo.setAPORTAId(entidad.getAportaId().longValue());
            }
            if (entidad.getAportaTipoIdentificacion() != null) {
                detalleHojaCalculo.setAPORTATipoIdentificacion(entidad.getAportaTipoIdentificacion().longValue());
            }
            detalleHojaCalculo.setAPORTANumeroIdentificacion(entidad.getAportaNumeroIdentificacion());
            detalleHojaCalculo.setAPORTAPrimerNombre(entidad.getAportaPrimerNombre());
            detalleHojaCalculo.setAPORTAClase(entidad.getAportaClase());
            detalleHojaCalculo.setAPORTAAporteEsapYMen(entidad.getAportaAporteEsapYMen());
            detalleHojaCalculo.setAPORTAExcepcionLey12332008(entidad.getAportaExcepcionLey12332008());
            
            /*
            if (entidad.getCotizId() != null) {
                detalleHojaCalculo.setCOTIZId(entidad.getCotizId().longValue());
            }
            */
            
            if (entidad.getCotizTipoDocumento() != null) {
                detalleHojaCalculo.setCOTIZTipoDocumento(entidad.getCotizTipoDocumento().longValue());
            }
            detalleHojaCalculo.setCOTIZNumeroIdentificacion(entidad.getCotizNumeroIdentificacion());
            detalleHojaCalculo.setCOTIZNombre(entidad.getCotizNombre());
            if (entidad.getCotizTipoCotizante() != null) {
                detalleHojaCalculo.setCOTIZTipoCotizante(entidad.getCotizTipoCotizante().longValue());
            }
            if (entidad.getCotizSubtipoCotizante() != null) {
                detalleHojaCalculo.setCOTIZSubtipoCotizante(entidad.getCotizSubtipoCotizante().longValue());
            }
            detalleHojaCalculo.setCOTIZExtranjeroNoCotizar(entidad.getCotizExtranjeroNoCotizar());
            detalleHojaCalculo.setCOTIZColombianoEnElExt(entidad.getCotizColombianoEnElExt());
            detalleHojaCalculo.setCOTIZActividadAltoRiesgoPe(entidad.getCotizActividadAltoRiesgoPe());
            if (entidad.getCotizdAno() != null) {
                detalleHojaCalculo.setCOTIZDAno(entidad.getCotizdAno().longValue());
            }
            if (entidad.getCotizdMes() != null) {
                detalleHojaCalculo.setCOTIZDMes(entidad.getCotizdMes().longValue());
            }
            
            detalleHojaCalculo.setCOTIZDIng(entidad.getCotizdIng());

            if (entidad.getCotizdIngFecha() != null) {
                detalleHojaCalculo.setCOTIZDIngFecha(DateUtil.parseStrDateTimeToCalendar(DateUtil.parseDateToString(entidad.getCotizdIngFecha())));
            }

            detalleHojaCalculo.setCOTIZDRet(entidad.getCotizdRet());
            if (entidad.getCotizdRetFecha() != null) {
                detalleHojaCalculo.setCOTIZDRetFecha(DateUtil.parseStrDateTimeToCalendar(DateUtil.parseDateToString(entidad.getCotizdRetFecha())));
            }
            //detalleHojaCalculo.setCOTIZDSln(entidad.getCotizdSln());
            
            
            // Se agregan los nuevos 106 campos - WR
            
            
            //SE DEBERIA QUITAR
            detalleHojaCalculo.setIbcPermisosRemunerados(entidad.getIbcPermisosRemunerados().longValue());
            detalleHojaCalculo.setIbcSuspPermisos(entidad.getIbcSuspPermisos().longValue());
            detalleHojaCalculo.setIbcVacaciones(entidad.getIbcVacaciones().longValue());
            detalleHojaCalculo.setIbcHuelga(entidad.getIbcHuelga().longValue());
            detalleHojaCalculo.setDiasCotPension(entidad.getDiasCotPension().longValue());
            detalleHojaCalculo.setDiasCotSalud(entidad.getDiasCotSalud().longValue());
            detalleHojaCalculo.setDiasCotRprof(entidad.getDiasCotRprof().longValue());
            detalleHojaCalculo.setDiasCotCcf(entidad.getDiasCotCcf().longValue());
            detalleHojaCalculo.setPagoNoSalarial(entidad.getPagoNoSalarial().longValue());
            detalleHojaCalculo.setTotalRemunerado(entidad.getTotalRemunerado().longValue());
            detalleHojaCalculo.setPorPagoNoSalarial(entidad.getPorPagoNoSalarial().longValue());
            detalleHojaCalculo.setExcLimPagoNoSalarial(entidad.getExcLimPagoNoSalarial().longValue());
            detalleHojaCalculo.setCodAdmSalud(entidad.getCodAdmSalud());
            detalleHojaCalculo.setIbcPagosNomSalud(entidad.getIbcPagosNomSalud().longValue());
            detalleHojaCalculo.setIbcCalculadoSalud(entidad.getIbcCalculadoSalud().longValue());
            detalleHojaCalculo.setTarifaSalud(entidad.getTarifaSalud());
            detalleHojaCalculo.setTarifaSaludSuspension(entidad.getTarifaSaludSuspension());
            detalleHojaCalculo.setCotizOblCalculadaSalud(entidad.getCotizOblCalculadaSalud().longValue());
            detalleHojaCalculo.setDiasCotizPilaSalud(entidad.getDiasCotizPilaSalud().longValue());
            detalleHojaCalculo.setIbcPilaSalud(entidad.getIbcPilaSalud().longValue());
            detalleHojaCalculo.setTarifaPilaSalud(entidad.getTarifaPilaSalud());
            detalleHojaCalculo.setCotizPagadaPilaSalud(entidad.getCotizPagadaPilaSalud().longValue());
            detalleHojaCalculo.setAjusteSalud(entidad.getAjusteSalud().longValue());
            //detalleHojaCalculo.setConceptoAjusteSalud(entidad.getConceptoAjusteSalud());
            detalleHojaCalculo.setTipoIncumplimientoSalud(entidad.getTipoIncumplimientoSalud());
            detalleHojaCalculo.setIbcPagosNomPension(entidad.getIbcPagosNomPension().longValue());
            detalleHojaCalculo.setIbcCalculadoPension(entidad.getIbcCalculadoPension().longValue());
            detalleHojaCalculo.setTarifaPension(entidad.getTarifaPension());
            detalleHojaCalculo.setCotizOblPension(entidad.getCotizOblPension().longValue());
            detalleHojaCalculo.setDiasCotizPilaPension(entidad.getDiasCotizPilaPension().longValue());
            detalleHojaCalculo.setIbcPilaPension(entidad.getIbcPilaPension().longValue());
            detalleHojaCalculo.setTarifaPilaPension(entidad.getTarifaPilaPension());
            detalleHojaCalculo.setCotPagadaPilaPension(entidad.getCotPagadaPilaPension().longValue());
            detalleHojaCalculo.setAjustePension(entidad.getAjustePension().longValue());
            detalleHojaCalculo.setTarifaFspSubcuenSolidaridad(entidad.getTarifaFspSubcuenSolidaridad().longValue());
            detalleHojaCalculo.setTarifaFspSubcuenSubsisten(entidad.getTarifaFspSubcuenSubsisten().longValue());
            detalleHojaCalculo.setCotizOblFspSubSolidaridad(entidad.getCotizOblFspSubSolidaridad().longValue());
            detalleHojaCalculo.setCotizOblFspSubSubsistencia(entidad.getCotizOblFspSubSubsistencia().longValue());
            detalleHojaCalculo.setCotizPagPilaFspSubSolidar(entidad.getCotizPagPilaFspSubSolidar().longValue());
            detalleHojaCalculo.setCotizPagPilaFspSubSubsis(entidad.getCotizPagPilaFspSubSubsis().longValue());
            detalleHojaCalculo.setAjusteFspSubcuenSolidaridad(entidad.getAjusteFspSubcuenSolidaridad().longValue());
            detalleHojaCalculo.setAjusteFspSubcuenSubsisten(entidad.getAjusteFspSubcuenSubsisten().longValue());
            detalleHojaCalculo.setCalculoActuarial(entidad.getCalculoActuarial().longValue());
            detalleHojaCalculo.setCodAdmArl(entidad.getCodAdmArl());
            detalleHojaCalculo.setIbcArl(entidad.getIbcArl().longValue());
            detalleHojaCalculo.setTarifaArl(entidad.getTarifaArl());
            detalleHojaCalculo.setCotizOblArl(entidad.getCotizOblArl().longValue());
            detalleHojaCalculo.setDiasCotizPilaArl(entidad.getDiasCotizPilaArl().longValue());
            detalleHojaCalculo.setIbcPilaArl(entidad.getIbcPilaArl().longValue());
            detalleHojaCalculo.setTarifaPilaArl(entidad.getTarifaPilaArl());
            detalleHojaCalculo.setCotizPagadaPilaArl(entidad.getCotizPagadaPilaArl().longValue());
            detalleHojaCalculo.setAjusteArl(entidad.getAjusteArl().longValue());
            detalleHojaCalculo.setConceptoAjusteArl(entidad.getConceptoAjusteArl());
            detalleHojaCalculo.setTipoIncumplimientoArl(entidad.getTipoIncumplimientoArl());
            detalleHojaCalculo.setCodAdmCcf(entidad.getCodAdmCcf());
            detalleHojaCalculo.setIbcCcf(entidad.getIbcCcf().longValue());
            detalleHojaCalculo.setTarifaCcf(entidad.getTarifaCcf());
            detalleHojaCalculo.setCotizOblCcf(entidad.getCotizOblCcf().longValue());
            detalleHojaCalculo.setDiasCotiPilaCcf(entidad.getDiasCotiPilaCcf().longValue());
            detalleHojaCalculo.setIbcPilaCcf(entidad.getIbcPilaCcf().longValue());
            detalleHojaCalculo.setTarifaPilaCcf(entidad.getTarifaPilaCcf());
            detalleHojaCalculo.setCotizPagadaPilaCcf(entidad.getCotizPagadaPilaCcf().longValue());
            detalleHojaCalculo.setAjusteCcf(entidad.getAjusteCcf().longValue());
            detalleHojaCalculo.setConceptoAjusteCcf(entidad.getConceptoAjusteCcf());
            detalleHojaCalculo.setTipoIncumplimientoCcf(entidad.getTipoIncumplimientoCcf());
            detalleHojaCalculo.setIbcSena(entidad.getIbcSena().longValue());
            detalleHojaCalculo.setTarifaSena(entidad.getTarifaSena());
            detalleHojaCalculo.setCotizOblSena(entidad.getCotizOblSena().longValue());
            detalleHojaCalculo.setTarifaPilaSena(entidad.getTarifaPilaSena());
            detalleHojaCalculo.setCotizPagadaPilaSena(entidad.getCotizPagadaPilaSena().longValue());
            //detalleHojaCalculo.setAjusteSena(entidad.getAjusteSena());
            detalleHojaCalculo.setConceptoAjusteSena(entidad.getConceptoAjusteSena());
            detalleHojaCalculo.setTipoIncumplimientoSena(entidad.getTipoIncumplimientoSena());
            detalleHojaCalculo.setIbcIcbf(entidad.getIbcIcbf().longValue());
            detalleHojaCalculo.setTarifaIcbf(entidad.getTarifaIcbf());
            detalleHojaCalculo.setCotizOblIcbf(entidad.getCotizOblIcbf().longValue());
            detalleHojaCalculo.setTarifaPilaIcbf(entidad.getTarifaPilaIcbf());
            detalleHojaCalculo.setCotizPagadaPilaIcbf(entidad.getCotizPagadaPilaIcbf().longValue());
            detalleHojaCalculo.setAjusteIcbf(entidad.getAjusteIcbf().longValue());
            detalleHojaCalculo.setConceptoAjusteIcbf(entidad.getConceptoAjusteIcbf());
            detalleHojaCalculo.setTipoIncumplimientoIcbf(entidad.getTipoIncumplimientoIcbf());
            
            
        
            
            //detalleHojaCalculo.setPlanillaPilaCargada(entidad.getPlanillaCargada());
            /**
            for (ConceptoContableHclDetalleDTO conceptoContable : entidad.getConceptoContableHclDetalleCollection()) {
                ConceptoContableTipo conceptoContableTipo = conceptoContableAssembler.assembleServicio(conceptoContable);
                detalleHojaCalculo.getConceptoContableTipo().add(conceptoContableTipo);
            }

            for (AportesIndependienteHclDetalleDTO aporteIndependiente : entidad.getAportesIndependienteHclDetaCollection()) {
                AportesIndependienteTipo aportesIndependienteTipo = aportesIndependienteAssembler.assembleServicio(aporteIndependiente);
                detalleHojaCalculo.getAportesIndependienteTipo().add(aportesIndependienteTipo);
            }**/
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;

        }

        return detalleHojaCalculo;
    }


}
