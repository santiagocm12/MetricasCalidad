package co.gov.ugpp.parafiscales.servicios.liquidador.srvaplhojacalculoliquidacion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;

/**
 *
 * @author Mauricio Guerrero
 */
public class CrearExcelHojaCalculoLiquidacion {

    public byte[] CrearExcelLiquidacion(ResultSet rs) throws IOException, Exception {
        Workbook wb;
        try {
            wb = CargarEncabezadosLiquidacion(rs);
        } catch (Exception ex) {
            throw ex;
        }
        Sheet hoja1 = wb.getSheet("LIQUIDADOR");

        Font fuente = wb.createFont();
        fuente.setFontHeightInPoints((short) 11);
        fuente.setFontName("Arial Narrow");
        fuente.setBoldweight((short) 5);

        CellStyle estilo = wb.createCellStyle();
        estilo.setWrapText(true);

        estilo = wb.createCellStyle();
        estilo.setFont(fuente);

        estilo.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        estilo.setBorderTop(BorderFormatting.BORDER_THIN);
        estilo.setBorderBottom(BorderFormatting.BORDER_THIN);
        estilo.setBorderLeft(BorderFormatting.BORDER_THIN);
        estilo.setBorderRight(BorderFormatting.BORDER_THIN);

        int fila = 0;

        Row row;
        Cell cell;
        try {
            while (rs.next()) {
                fila++;
                row = hoja1.createRow(fila);
                for (int columnaCelda = 1; columnaCelda < rs.getMetaData().getColumnCount(); columnaCelda++) {
                    cell = row.createCell(columnaCelda - 1);
                    cell.setCellStyle(estilo);
                    if (rs.getString(columnaCelda) != null) {
                        cell.setCellValue(rs.getString(columnaCelda));
                    } else {
                        cell.setCellValue("");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CrearExcelHojaCalculoLiquidacion.class.getName()).log(Level.SEVERE, null, ex);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write(bos);
        } catch (IOException ex) {
            Logger.getLogger(CrearExcelHojaCalculoLiquidacion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();

        return bytes;
    }

    public Workbook CargarEncabezadosLiquidacion(ResultSet rs) throws Exception {
        Workbook wb = new HSSFWorkbook();

        String safeName = WorkbookUtil.createSafeSheetName("LIQUIDADOR");
        Sheet hoja1 = wb.createSheet(safeName);

        Font fuente = wb.createFont();
        fuente.setFontHeightInPoints((short) 11);
        fuente.setFontName("Arial Narrow");
        fuente.setBoldweight((short) 7);

        CellStyle estilo = wb.createCellStyle();
        estilo.setWrapText(true);

        estilo = wb.createCellStyle();
        estilo.setFont(fuente);

        estilo.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        estilo.setBorderTop(BorderFormatting.BORDER_THIN);
        estilo.setBorderBottom(BorderFormatting.BORDER_THIN);
        estilo.setBorderLeft(BorderFormatting.BORDER_THIN);
        estilo.setBorderRight(BorderFormatting.BORDER_THIN);

        Row row = hoja1.createRow(0);
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int columnCell = 1; columnCell < columnCount; columnCell++) {
                Cell cell = row.createCell(columnCell - 1);
                cell.setCellStyle(estilo);
                cell.setCellValue(rsmd.getColumnName(columnCell));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return wb;
    }

}
