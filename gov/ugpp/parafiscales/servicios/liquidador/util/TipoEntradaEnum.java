package co.gov.ugpp.parafiscales.servicios.liquidador.util;

/**
 *
 * @author everis Colombia
 */
public enum TipoEntradaEnum {

    IN("IN"),
    OUT("OUT");

    private final String tipoEntrada;

    TipoEntradaEnum(String code) {
        this.tipoEntrada = code;
    }

    public String getCode() {
        return tipoEntrada;
    }
    
    @Override
    public String toString(){
        return tipoEntrada;
    }
}
