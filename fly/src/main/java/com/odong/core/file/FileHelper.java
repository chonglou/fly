package com.odong.core.file;

import com.odong.core.file.csv.Column;
import com.odong.core.file.csv.Csv;
import com.odong.core.file.excel.Cell;
import com.odong.core.file.excel.Excel;
import com.odong.core.file.excel.Table;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午4:53
 */
@Component
public class FileHelper {
    private CellProcessor getCellProcessor(Column column) {
        CellProcessor type = null;
        switch (column.getType()) {

            case BOOL:
                type = new FmtBool("Y", "N");
                break;
            case DATE:
                type = new FmtDate("yyyy-MM-dd hh:mm:ss");
                break;
        }
        return type == null ?
                (column.isOptional() ? new Optional() : new NotNull()) :
                (column.isOptional() ? new Optional(type) : new NotNull(type));
    }

    public void write(Csv csv) {
        try (ICsvListWriter writer = new CsvListWriter(new FileWriter(csv.getName()), CsvPreference.STANDARD_PREFERENCE)) {
            int cols = csv.getColumns().size();
            CellProcessor[] processors = new CellProcessor[cols];
            String[] headers = new String[cols];
            for (int i = 0; i < cols; i++) {
                Column column = csv.getColumns().get(i);
                processors[i] = getCellProcessor(column);
                headers[i] = column.getName();
            }
            writer.writeHeader(headers);
            //遍历（列）
            for (int i = 0; i < csv.getSize(); i++) {
                List<Object> objects = new ArrayList<>();
                //遍历（行）
                for (int j = 0; j < cols; j++) {
                    objects.add(csv.getColumns().get(j).item(i));
                }
                writer.write(objects, processors);
            }

        } catch (IOException e) {
            logger.error("生成csv文件出错", e);
        }
    }

    public void write(Excel excel) {
        HSSFWorkbook book = new HSSFWorkbook();

        /*
        // 创建字体样式
        HSSFFont font = book.createFont();
        font.setFontName("Verdana");
        font.setBoldweight((short) 100);
        font.setFontHeight((short) 300);
        font.setColor(HSSFColor.BLUE.index);
        // 创建单元格样式
        HSSFCellStyle headStyle = book.createCellStyle();
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        headStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 设置边框
        //headStyle.setBottomBorderColor(HSSFColor.RED.index);
        headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        headStyle.setFont(font);// 设置字体
        */


        for (Table table : excel.getTables()) {
            HSSFSheet sheet = book.createSheet(table.getName());


            // 创建Excel的sheet的一行
            HSSFRow head = sheet.createRow(0);
            //head.setHeight((short) 300);// 设定行的高度
            for (int i = 0; i < table.getColumns().size(); i++) {
                // 创建一个Excel的单元格
                HSSFCell cell = head.createCell(i);
                //cell.setCellStyle(headStyle);
                cell.setCellValue(table.getColumns().get(i).getName());
            }
            //遍历（行）
            for (int i = 0; i < table.getSize(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                //遍历（列）
                for (int j = 0; j < table.getColumns().size(); j++) {
                    HSSFCell cell = row.createCell(j);
                    Cell c = table.getColumns().get(j).cell(i);

                    if (c.getValue() instanceof Date) {
                        cell.setCellValue((Date) c.getValue());
                    } else if (c.getValue() instanceof Number) {
                        cell.setCellValue((Double) c.getValue());
                    } else {
                        cell.setCellValue((String) c.getValue());
                    }
                    if (c.getLink() != null) {
                        HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
                        link.setAddress(c.getLink());
                        cell.setHyperlink(link);
                    }
                }
            }
            //设置excel每列宽度
            for (int i = 0; i < table.getColumns().size(); i++) {
                //sheet.setColumnWidth(i, table.getColumns().get(i).getWidth());
                sheet.autoSizeColumn(i);
            }

        }

        try (FileOutputStream os = new FileOutputStream(excel.getName())) {
            book.write(os);
        } catch (IOException e) {
            logger.error("生成Excel文件出错", e);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(FileHelper.class);
}
