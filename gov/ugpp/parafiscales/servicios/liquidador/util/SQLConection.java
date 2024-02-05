package co.gov.ugpp.parafiscales.servicios.liquidador.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import co.gov.ugpp.parafiscales.servicios.liquidador.transversales.AppException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

/**
 * Clase que gestiona las conexiones a base de datos externas no validadas por
 * el modelo de datos
 *
 * @author franzjr
 */
public class SQLConection {

    private Properties ugppSQLProperties;

    private final String driverClassName;

    private final String urlPILA;
    private final String usernamePILA;
    private final String passwordPILA;
    private final String tablePILA;

    private final String urlRUA;
    private final String usernameRUA;
    private final String passwordRUA;
    private final String tableRUA;
    private final String tableJOINRUA;

    private final String urlAportante;
    private final String usernameAportante;
    private final String passwordAportante;
    private final String queryAportante;

    private final String urlLiquidador;
    private final String usernameLiquidador;
    private final String passwordLiquidador;
    private final String queryLiquidador;

    private static final SQLConection sqlSingleton = new SQLConection();

    public static SQLConection getInstance() {
        return sqlSingleton;
    }

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SQLConection.class);

    private static final String file = "sql.properties";
    private static final Properties props = loadProperties();
    private Connection connection;

    private static Properties loadProperties() {
        final Properties props = new Properties();
        InputStream input = null;
        try {
            input = SQLConection.class.getClassLoader().getResourceAsStream(file);
            props.load(input);
        } catch (IOException ex) {            
            try {
                throw new AppException("Archivo de configuración de LDAP no encontrado", ex);
            } catch (AppException ex1) {
                Logger.getLogger(SQLConection.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    LOG.warn("No se pudo cerrar el archivo ldap.properties", ex);
                }
            }
        }
        return props;
    }

    public static final Properties obtainProperties() {
        return props;
    }

    /**
     * Constructor encargado de leer las propiedades de las base de datos,
     * tablas y esquemas.
     */
    public SQLConection() {

        Logger.getLogger(SQLConection.class.getName()).log(Level.INFO, "Op: SQLConection ::: INIT");

        ugppSQLProperties = loadProperties();

        driverClassName = ugppSQLProperties.getProperty("database.driverClassNameDefault");

        urlPILA = ugppSQLProperties.getProperty("database.urlPILA");
        usernamePILA = ugppSQLProperties.getProperty("database.usernamePILA");
        passwordPILA = ugppSQLProperties.getProperty("database.passwordPILA");
        tablePILA = ugppSQLProperties.getProperty("database.tablePILA");

        urlRUA = ugppSQLProperties.getProperty("database.urlRUA");
        usernameRUA = ugppSQLProperties.getProperty("database.usernameRUA");
        passwordRUA = ugppSQLProperties.getProperty("database.passwordRUA");
        tableRUA = ugppSQLProperties.getProperty("database.tableRUA");
        tableJOINRUA = ugppSQLProperties.getProperty("database.tableJOINRUA");

        urlAportante = ugppSQLProperties.getProperty("database.urlAportante");
        usernameAportante = ugppSQLProperties.getProperty("database.usernameAportante");
        passwordAportante = ugppSQLProperties.getProperty("database.passwordAportante");
        queryAportante = ugppSQLProperties.getProperty("database.queryAportante");

        //QA
        urlLiquidador = ugppSQLProperties.getProperty("database.urlLiquidador");
        usernameLiquidador = ugppSQLProperties.getProperty("database.usernameLiquidador");
        passwordLiquidador = ugppSQLProperties.getProperty("database.passwordLiquidador");
        
        //PRODUCCION
        //urlLiquidador = ugppSQLProperties.getProperty("database.urlLiquidadorPRO");
        //usernameLiquidador = ugppSQLProperties.getProperty("database.usernameLiquidadorPRO");
        //passwordLiquidador = ugppSQLProperties.getProperty("database.passwordLiquidadorPRO");

        queryLiquidador = ugppSQLProperties.getProperty("database.queryLiquidador");

        Logger.getLogger(SQLConection.class.getName()).log(Level.INFO, "Op: SQLConection ::: END");

    }
    
    public ResultSet procesoDobleLineaAntesDeLiquidar(String idhojacalculoliquidacion) throws Exception {

        ResultSet result = null;
        String sql = null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(this.urlLiquidador, this.usernameLiquidador, this.passwordLiquidador);

            sql = "BEGIN USP_CALCULA_VLRS(?); END;";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setInt(1, Integer.parseInt(idhojacalculoliquidacion));
            cs.execute();
            result = cs.getResultSet();
            //System.out.println("::Liquidador:: se ejecuta SQL: BEGIN USP_REEMPLAZA_VLRS("+ idhojacalculoliquidacion +"); END;");

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("::ERROR EXCEPTION procesoDobleLineaAntesDeLiquidar: " + idhojacalculoliquidacion + "-" + sql + "-" + "-" + e);
            throw e;
        }

        return result;

    }
   
    
    public ResultSet consultaLiquidador(int modo, String idExpediente, String tipoIdientificacion, String numIdentificacion, String parametro) throws Exception {
        ResultSet result = null;
        
        try {
            System.out.println("DATA: modo:" + modo + " idExpediente: " + idExpediente  + " tipoIdientificacion: " + tipoIdientificacion + 
                               " numIdentificacion: " + numIdentificacion + 
                               " parametro: " + parametro);

            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(this.urlLiquidador, this.usernameLiquidador, this.passwordLiquidador);

            String resultado = "";
            //MOD ANDRES: 27/10/2016
            //String sql = queryLiquidador; 
            String sql = "BEGIN usp_hoja_trabajo2(?,?); END;";

            
            LOG.debug("INFO generarExcel QUERY 1: " + sql);
            
            
            CallableStatement cs = connection.prepareCall(sql);
            cs.setInt(1, Integer.parseInt(parametro));
            cs.registerOutParameter(2, java.sql.Types.VARCHAR);
            cs.execute();
            resultado = cs.getString(2);
            
            LOG.debug("INFO generarExcel QUERY 2: " + resultado);
            
            cs = connection.prepareCall(resultado);
            cs.execute();
            
            LOG.debug("INFO generarExcel QUERY 3: " + cs.getResultSet().getFetchSize());
            
            result = cs.getResultSet();
              

        } catch (Exception e) {
            e.printStackTrace();
            LOG.debug("EXCEPCION ERROR generarExcel " + e);
            throw e;
        }
        
        return result;
    }
    
    
    
    
       
    public ResultSet liquidadorDobleLineaProceso2B(String idhojacalculoliquidacion) throws Exception {
        
        ResultSet result = null;
        String sql = null;
        
        try {
            
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(this.urlLiquidador, this.usernameLiquidador, this.passwordLiquidador);

            sql = "BEGIN USP_REEMPLAZA_VLRS(?); END;";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setInt(1, Integer.parseInt(idhojacalculoliquidacion));
            cs.execute();
            result = cs.getResultSet();
            //System.out.println("::Liquidador:: se ejecuta SQL: BEGIN USP_REEMPLAZA_VLRS("+ idhojacalculoliquidacion +"); END;");
              
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("::ERROR EXCEPTION liquidadorDobleLineaProceso2B: " + idhojacalculoliquidacion + "-" + sql + "-" + "-" + e);
            throw e;
        }
        
        return result;
    }

public ResultSet liquidadorComparacionVariacionIBC(String idhojacalculoliquidacion) throws Exception {
        
        ResultSet result = null;
        String sql = null;
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(this.urlLiquidador, this.usernameLiquidador, this.passwordLiquidador);
            sql = "BEGIN USP_COMPARACION_IBC (?); END;";
            CallableStatement cs = connection.prepareCall(sql);
            cs.setInt(1, Integer.parseInt(idhojacalculoliquidacion));
            cs.execute();
            result = cs.getResultSet();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("::ERROR EXCEPTION liquidadorComparacionVariacionIBC: " + idhojacalculoliquidacion + "-" + sql + "-" + "-" + e);
            throw e;
        }
        return result;
    }


    public void closeConnection() throws SQLException {
        if (connection != null) {
            if (!connection.isClosed()) {
                connection.close();
            }
        }
    }

}
