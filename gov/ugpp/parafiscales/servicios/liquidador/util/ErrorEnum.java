package co.gov.ugpp.parafiscales.servicios.liquidador.util;

/**
 *
 * @author jmunocab
 */
public enum ErrorEnum {

    EXITO("1", "Operación realizada satisfactoriamente."),
    FALLO("0", "Error al realizar la operación."),
    ERROR_NO_ESPERADO("100", "Ha ocurrido un error no esperado. Consultar el log para mas información."),
    ERROR_BASE_DATOS("101", "Ha ocurrido un error de base de datos."),
    ERROR_ENTIDAD_EXISTENTE("106", "Ya existe una entidad con el identificador proporcionado."),
    ERROR_LOGIN("102", "La combinacion de usuario y password, no fue encontrada en el directorio activo."),
    ERROR_PERSISTIR_CONTEXTO_TRANSACCIONAL("103", "No fue posible persistir el contexto transaccional en la auditoria."),
    ERROR_INTEGRIDAD_REFERENCIAL("104", "Violacion de Integridad Referencial en la base de datos."),
    ERROR_NEGOCIO("105", "Violación de datos de negocio."),
    ERROR_VALIDACION_ARCHIVO("106", "Error al validar el archivo.");

    private final String code;
    private final String message;

    ErrorEnum(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return message + ": " + code;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
