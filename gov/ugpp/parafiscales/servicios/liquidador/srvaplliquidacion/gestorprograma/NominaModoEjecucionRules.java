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
import co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma.jpa.JPAEntityDao;
import co.gov.ugpp.parafiscales.servicios.liquidador.util.CacheService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;

public class NominaModoEjecucionRules {
    
    
    public BigDecimal tarifaSalud(String condEspEmp) {
        BigDecimal valor = new BigDecimal("0");
        if ("LEY 1429 Col AÑO 1,2".equals(condEspEmp) || ("LEY 1429 AGV AÑO 1-8".equals(condEspEmp))) {
            valor = new BigDecimal("11");
        }
        if ("LEY 1429 Col AÑO 3".equals(condEspEmp)) {
            valor = new BigDecimal("11.38");
        }
        if ("LEY 1429 Col AÑO 4".equals(condEspEmp) || ("LEY 1429 AGV AÑO 9".equals(condEspEmp))) {
            valor = new BigDecimal("11.75");
        }
        if ("LEY 1429 Col AÑO 5".equals(condEspEmp) || ("LEY 1429 AGV AÑO 10".equals(condEspEmp))) {
            valor = new BigDecimal("12.13");
        }
        if ("Soc.declaradas ZF. Art20 Ley1607".equals(condEspEmp)) {
            valor = new BigDecimal("12.5");
            //System.out.println("::ANDRES14:: ingrese a CREE TARIFA_SALUD: " + new BigDecimal("12.5"));
        }

        return valor;
    }
    
     public int restarMesesProporcional(int anio, int mes) {
        int resto = 0;
        if ((anio >= 2020 && mes >= 5) && (anio <= 2021 && mes <= 2)) {
            switch (mes) {
                case 5:resto = 9;break;case 6:resto = 8;break;case 7:resto = 7;break;case 8:resto = 6;break;
                case 9:resto = 5;break;case 10:resto = 4;break;case 11:resto = 3;break;case 12:resto = 2;break;
                case 1:resto = 1;break;default:resto = 0;break;
            }
        }
        return resto;
    }
    
    public BigDecimal tarifaICBF(String condEspEmp) {
        BigDecimal valor = new BigDecimal("0");
        BigDecimal rst15 = new BigDecimal("3");
        rst15 = rst15.divide(new BigDecimal("2"));
        BigDecimal rst075 = rst15.divide(new BigDecimal("2"));
        BigDecimal rst225 = rst075.multiply(new BigDecimal("3"));

        switch (condEspEmp) {
            case "LEY 590/2000 AÑO 1":
            case "LEY 1429 Col AÑO 3":
                valor = rst075;
                break;
            case "LEY 1429 Col AÑO 1,2":
            case "LEY 1429 AGV AÑO 1-8":
                valor = new BigDecimal("0");
                break;
            case "LEY 1429 Col AÑO 4":
            case "LEY 1429 AGV AÑO 9":
            case "LEY 590/2000 AÑO 2":
                valor = rst15;
                break;
            case "LEY 1429 Col AÑO 5":
            case "LEY 1429 AGV AÑO 10":
            case "LEY 590/2000 AÑO 3":
                valor = rst225;
                break;
            case "Soc.declaradas ZF.Art20 Ley 1607":
                valor = new BigDecimal("3");
                break;
            case "Conv.Sub.Fam.Art.17Ley 344/96":
                valor = new BigDecimal("0");
                break;
            default:
                break;
        }

        return valor;
    }
    
    public BigDecimal tarifaCCF(String condEspEmp) {
        BigDecimal valor = new BigDecimal("0");
        switch (condEspEmp) {
            case "LEY 590/2000 AÑO 1":
            case "LEY 1429 Col AÑO 3":
                valor = new BigDecimal("1");
                break;
            case "LEY 590/2000 AÑO 2":
            case "LEY 1429 Col AÑO 4":
            case "LEY 1429 AGV AÑO 9":
                valor = new BigDecimal("2");
                break;
            case "LEY 590/2000 AÑO 3":
            case "LEY 1429 Col AÑO 5":
            case "LEY 1429 AGV AÑO 10":
                valor = new BigDecimal("3");
                break;
            case "LEY 1429 Col AÑO 1,2":
            case "LEY 1429 AGV AÑO 1-8":
                valor = new BigDecimal("0");
                break;
            default:
                break;
        }

        return valor;
    }
    
    public BigDecimal tarifaSena(String condEspEmp) {
        BigDecimal valor = new BigDecimal("0");
        BigDecimal rst05 = new BigDecimal("1");
        rst05 = rst05.divide(new BigDecimal("2"));
        BigDecimal rst15 = new BigDecimal("3");
        rst15 = rst15.divide(new BigDecimal("2"));
        switch (condEspEmp) {
            case "LEY 590/2000 AÑO 1":
            case "LEY 1429 Col AÑO 3":
                valor = rst05;
                break;
            case "LEY 590/2000 AÑO 2":
            case "LEY 1429 Col AÑO 4":
            case "LEY 1429 AGV AÑO 9":
                valor = new BigDecimal("1");
                break;
            case "LEY 590/2000 AÑO 3":
            case "LEY 1429 Col AÑO 5":
            case "LEY 1429 AGV AÑO 10":
                valor = rst15;
                break;
            case "LEY 1429 Col AÑO 1,2":
            case "LEY 1429 AGV AÑO 1-8":
            case "Excepcion SENA Art.181,Ley 223/95":
                valor = new BigDecimal("0");
                break;
            case "Soc.declaradas ZF.Art20 Ley 1607":
                valor = new BigDecimal("2");
                break;
            default:
                break;
        }

        return valor;

    }
    
    public String tipoIncumplimientoDescrip(String datoIncumplimiento) {
        String resultado = "";

        if (null != datoIncumplimiento) {
            switch (datoIncumplimiento) {
                case ConstantesGestorPrograma.OMISO_DESC:
                    resultado = ConstantesGestorPrograma.OMISO_NOMBRE;
                    break;
                case ConstantesGestorPrograma.MORA_DESC:
                    resultado = ConstantesGestorPrograma.MORA_NOMBRE;
                    break;
                case ConstantesGestorPrograma.INEXACTO_DESC:
                    resultado = ConstantesGestorPrograma.INEXACTO_NOMBRE;
                    break;
                default:
                    break;
            }
        }
        return resultado;
    }
    
    public String tipoIncumplimientoPensionFSP(String datoIncumplimiento) {
        String resultado = "";

        if (null != datoIncumplimiento) {
            switch (datoIncumplimiento) {
                case ConstantesGestorPrograma.VDCA_DESC:
                case ConstantesGestorPrograma.OMISO_DESC:
                    resultado = ConstantesGestorPrograma.OMISO_NOMBRE;
                    break;
                case ConstantesGestorPrograma.MORA_DESC:
                    resultado = ConstantesGestorPrograma.MORA_NOMBRE;
                    break;
                case ConstantesGestorPrograma.INEXACTO_DESC:
                    resultado = ConstantesGestorPrograma.INEXACTO_NOMBRE;
                    break;
                default:
                    break;
            }
        }
        return resultado;
    }
    
       
     public String conceptoAjusteFSPaltoRiesgo(String datoIncumplimiento) {
        String resultado = ConstantesGestorPrograma.INEXACTO_DESC;

        if (null != datoIncumplimiento) {
            switch (datoIncumplimiento) {
                case ConstantesGestorPrograma.VDCA_DESC:
                    resultado = ConstantesGestorPrograma.OMISO_DESC;
                    break;
                case ConstantesGestorPrograma.MORA_DESC:
                    resultado = ConstantesGestorPrograma.MORA_DESC;
                    break;
                case ConstantesGestorPrograma.INEXACTO_DESC:
                    resultado = ConstantesGestorPrograma.INEXACTO_DESC;
                    break;
                default:
                    break;
            }
        }
        return resultado;
    }
     
    public int conteoMeses(Nomina nom, NominaDetalle nomDetalle) {
        int numMeses = 0;

        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaSancion = nom.getFechaSancion();

        try {
            Date primeraFecha = formateador.parse("2020-04-30"); // Inicia en mayo de 2020
            Date segundaFecha = formateador.parse("2021-01-31"); // Finaliza en febrero de 2021
            //Date fechaActualSancion = LocalDate.now();
            Date fechaActualSancion = formateador.parse(formateador.format(fechaSancion));
            Date fechaNomina = formateador.parse(nomDetalle.getAno() + "-" + nomDetalle.getMes() + "-01");

            org.joda.time.LocalDate fecha1 = LocalDate.now();
            org.joda.time.LocalDate fecha2 = LocalDate.now();
            org.joda.time.LocalDate pFecha = LocalDate.now();
            org.joda.time.LocalDate sFecha = LocalDate.now();

            fecha1 = LocalDate.parse(formateador.format(fechaNomina));
            fecha2 = LocalDate.parse(formateador.format(fechaActualSancion));
            pFecha = LocalDate.parse(formateador.format(primeraFecha));
            sFecha = LocalDate.parse(formateador.format(segundaFecha));

             if (fechaNomina.before(primeraFecha) && fechaActualSancion.before(primeraFecha)) {
                numMeses = org.joda.time.Months.monthsBetween(fecha1, fecha2).getMonths() + 1;
            }
            if (fechaNomina.before(primeraFecha) && fechaActualSancion.after(primeraFecha) && fechaActualSancion.before(segundaFecha)) {
                numMeses = org.joda.time.Months.monthsBetween(fecha1, pFecha).getMonths() + 1;
            }
            if (fechaNomina.after(primeraFecha) && fechaActualSancion.after(segundaFecha)) {
                numMeses = org.joda.time.Months.monthsBetween(pFecha, fecha2).getMonths() + 1;
            }
            //numMeses = org.joda.time.Months.monthsBetween(fechaNomina, fechaActualSancion).getMonths() + 1;
        } catch (Exception ex) {
            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
        }

        return numMeses;

    }
 
    public int getObtenerMeses(NominaDetalle nominaDetalle, Date fecha1, Date fecha2, String tipo) {

        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
        int months = 0;
        try {
            org.joda.time.LocalDate fechaActual1 = LocalDate.now();
            org.joda.time.LocalDate fechaActual2 = LocalDate.now();
            if ("1".equals(tipo)) {
                fechaActual1 = LocalDate.parse(objSDF.format(fecha1));
                org.joda.time.LocalDate fechaNomina = LocalDate.parse(nominaDetalle.getAno() + "-" + nominaDetalle.getMes() + "-01");
                months = org.joda.time.Months.monthsBetween(fechaNomina, fechaActual1).getMonths() + 1;
            } else {
                fechaActual1 = LocalDate.parse(objSDF.format(fecha1));
                fechaActual2 = LocalDate.parse(objSDF.format(fecha2));
                months = org.joda.time.Months.monthsBetween(fechaActual2, fechaActual1).getMonths() + 1;
            }
        } catch (Exception ex) {
            Logger.getLogger(NominaModoEjecucion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return months;
    }
    
    public String buscarAdm(String condEspEmp, String tipoCot, String subTipoCot) {
        String codAdm = "";
        if ("RÉGIMEN ESPECIAL FFMM".equals(condEspEmp) || "RÉGIMEN ESPECIAL MAGISTERIO".equals(condEspEmp) || "RÉGIMEN ESPECIAL ECOPETROL".equals(condEspEmp) || "RÉGIMEN ESPECIAL UNIVERSIDAD ESTATAL".equals(condEspEmp) || "30".equals(tipoCot) || "6".equals(subTipoCot)) {
            codAdm = "MIN002";
        } else { // <else> para el tipo de aportante diferente de independiente y además no pertence a alguno de los regiménes especiales
            codAdm = "MIN001";
        }
        return codAdm;
    }
    
   
}
