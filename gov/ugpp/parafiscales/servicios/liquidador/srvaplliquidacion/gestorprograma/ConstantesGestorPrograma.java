package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplliquidacion.gestorprograma;

public class ConstantesGestorPrograma
{
   public static final String OMISO_NOMBRE = "OMISO";
   public static final String MORA_NOMBRE  = "MORA";
   public static final String INEXACTO_NOMBRE  = "INEXACTO";
   
   //public static final Integer OMISO    = 1450001;
   //public static final Integer MORA     = 1450002;
   //public static final Integer INEXACTO = 1450003;
   //public static final Integer VDCA = 1450004;
   
   public static final String OMISO_DESC = "NO REGISTRA AFILIACION NI PAGO AL SUBSISTEMA";
   public static final String MORA_DESC = "NO REGISTRA PAGO DE APORTES";
   public static final String INEXACTO_DESC = "REGISTRO PAGO DE APORTES POR VALOR INFERIOR";
   public static final String VDCA_DESC = "VALOR DETERMINADO POR CALCULO ACTUARIAL";
   
   public static final String INEXACTO_JUS_VAR_SAL = "El IBC fue calculado de acuerdo a la información de Nómina";
   public static final String MORA_JUS_VAR_SAL = "No se evidencia pago en PILA";
   public static final String OMISO_JUS_VAR_SAL = "No se evidencia afiliación al subsistema";
   
   public static final String INEXACTO_JUS_VAR_PEN = "El IBC fue determinado de acuerdo a los ingresos mensuales.";
   public static final String MORA_JUS_VAR_PEN = "No se evidencia pago en PILA.";
   public static final String OMISO_JUS_VAR_PEN = "No se evidencia afiliación al subsistema.";
   

}
