package co.gov.ugpp.parafiscales.servicios.liquidador.util;

import co.gov.ugpp.parafiscales.servicios.liquidador.contextorespuestatipo.v1.ContextoRespuestaTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.errortipo.v1.ErrorTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.fallotipo.v1.FalloTipo;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.AppException;

/**
 *
 * @author jmunocab
 */
public class ErrorUtil {

    public static FalloTipo buildFalloTipo(final ContextoRespuestaTipo cr,
            final Exception ex) {

        cr.setCodEstadoTx(ErrorEnum.FALLO.getCode());

        final FalloTipo falloTipo = new FalloTipo();
        falloTipo.setContextoRespuesta(cr);

        if (ex instanceof AppException) {
            final AppException appEx = (AppException) ex;
            if (appEx.getErrorTipoList() == null || appEx.getErrorTipoList().isEmpty()) {
                falloTipo.getErrores().add(buildErrorTipo(appEx.getErrorEnum(), ex.getMessage()));
            } else {
                falloTipo.getErrores().addAll(appEx.getErrorTipoList());
            }
        } else {
            falloTipo.getErrores().add(buildErrorTipo(ErrorEnum.ERROR_NO_ESPERADO, ex.getMessage()));
        }

        return falloTipo;
    }

    public static ErrorTipo buildErrorTipo(final ErrorEnum errorEnum, final String descErrorTecnico) {

        final ErrorTipo errorTipo = new ErrorTipo();
        errorTipo.setCodError(errorEnum.getCode());
        errorTipo.setValDescError(errorEnum.getMessage());
        errorTipo.setValDescErrorTecnico(descErrorTecnico);

        return errorTipo;
    }
}
