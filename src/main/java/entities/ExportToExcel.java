package entities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ExportToExcel extends QueryObject{

    public ExportToExcel() {

    }

    public void startExport(String report, LocalDate fromDate, LocalDate toDate){
        Object[][] data = null;
        List<String> headers = new ArrayList<>();
        Double[] totalsMoney;

        int[] totalsCount;
        int rowCount;
        int columnCount;
        int currRow = 0;
        int currColumn = 0;
        DateTimeFormatter formatterExtension = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String reportName = report + "_" + formatterExtension.format(LocalDateTime.now());


        switch (report){
            case "Accounting":{
                statement = "SELECT c.name, e.picnic_date_time, e.event_address, ii.item_desc, ii.item_quantity, ii.item_cost, ii.item_supplier_cost, i.discount_percentage FROM invoice_item ii\n" +
                        "JOIN invoice i on ii.invoice_id = i.id AND i.is_paid = 1\n" +
                        "JOIN event e on i.id = e.invoice_id AND (e.picnic_date_time BETWEEN '" + fromDate.toString() + "' AND '" + toDate.toString() + "')\n" +
                        "JOIN customer c on e.customer_id = c.id\n" +
                        "ORDER BY e.picnic_date_time;";
                columnCount = 10;
                headers.add("Customer Name");
                headers.add("Picnic Date");
                headers.add("Address");
                headers.add("Item");
                headers.add("Quantity");
                headers.add("Unit Price");
                headers.add("Unit Cost");
                headers.add("Revenue");
                headers.add("Expense");
                headers.add("Profit");
                break;
            }
            case "Conversion Rate": {
                statement = "SELECT\n" +
                        "(SELECT COUNT(*) FROM event\n" +
                        "JOIN invoice i on event.invoice_id = i.id AND i.is_paid = 0\n" +
                        "WHERE picnic_date_time BETWEEN '" + fromDate.toString() + "' AND '" + toDate.toString() + "') as non_paid,\n" +
                        "(SELECT COUNT(*) FROM event\n" +
                        "JOIN invoice i on event.invoice_id = i.id AND i.is_paid = 1\n" +
                        "WHERE picnic_date_time BETWEEN '" + fromDate.toString() + "' AND '" + toDate.toString() + "') as paid;";
                columnCount = 5;
                headers.add("From");
                headers.add("To");
                headers.add("Paid");
                headers.add("Not Paid");
                headers.add("Rate");
                break;
            }
            case "Best Locations":{
                statement = "SELECT TRIM(e1.event_location) as location,\n" +
                        "       COUNT(*) as location_requested,\n" +
                        "       (SELECT Count(*) FROM event e2 JOIN invoice i on e2.invoice_id = i.id AND is_paid =1 WHERE TRIM(e2.event_location) = TRIM(e1.event_location)) as location_paid\n" +
                        "       FROM event e1 GROUP BY TRIM(e1.event_location)\n" +
                        "       ORDER BY location_paid DESC;";
                columnCount = 3;
                headers.add("Location");
                headers.add("Requests");
                headers.add("Paid");
                break;
            }
            case "Most Picnic Types":{
                statement = "SELECT e1.event_type as type,\n" +
                        "       COUNT(*) as type_requested,\n" +
                        "       (SELECT Count(*) FROM event e2 JOIN invoice i on e2.invoice_id = i.id AND is_paid =1 WHERE e2.event_type = e1.event_type) as type_paid\n" +
                        "FROM event e1 GROUP BY e1.event_type\n" +
                        "ORDER BY type_paid DESC;";
                columnCount = 3;
                headers.add("Occasion");
                headers.add("Requested");
                headers.add("Paid");
                break;
            }
            case "Best Addons":{
                statement = "SELECT ini.item_desc,\n" +
                        "       COUNT(ini.item_desc) as item_count_requested,\n" +
                        "       (SELECT COUNT(ii.item_desc) FROM invoice_item ii JOIN invoice i on ii.invoice_id = i.id AND i.is_paid = 1 WHERE ini.item_desc = ii.item_desc) as item_count_paid,\n" +
                        "       (SELECT SUM(ii.item_cost) FROM invoice_item ii JOIN invoice i on ii.invoice_id = i.id AND i.is_paid = 1 WHERE ini.item_desc = ii.item_desc) as revenue,\n" +
                        "       (SELECT SUM(ii.item_supplier_cost) FROM invoice_item ii JOIN invoice i on ii.invoice_id = i.id AND i.is_paid = 1 WHERE ini.item_desc = ii.item_desc) as expense,\n" +
                        "       ((SELECT SUM(ii.item_cost) FROM invoice_item ii JOIN invoice i on ii.invoice_id = i.id AND i.is_paid = 1 WHERE ini.item_desc = ii.item_desc) - (SELECT SUM(ii.item_supplier_cost) FROM invoice_item ii JOIN invoice i on ii.invoice_id = i.id AND i.is_paid = 1 WHERE ini.item_desc = ii.item_desc)) as profit\n" +
                        "FROM invoice_item ini\n" +
                        "JOIN addon a on ini.item_desc = a.name AND a.type_code = 'AD'\n" +
                        "GROUP BY ini.item_desc\n" +
                        "ORDER BY item_count_paid DESC;";
                columnCount = 6;
                headers.add("Addon");
                headers.add("Requested");
                headers.add("Paid");
                headers.add("Revenue");
                headers.add("Expense");
                headers.add("Profit");
                break;
            }
            default:
                columnCount = 0;
                break;
        }

        totalsMoney = new Double[columnCount];
        totalsCount = new int[columnCount];
        for(int i = 0; i < columnCount; i++){
            totalsMoney[i] = 0.0;
            totalsCount[i] = 0;
        }

        try {
            executeQuery(statement);
            if(resultSet != null){
                resultSet.last();
                rowCount = resultSet.getRow();
                resultSet.beforeFirst();
            } else return;

            data = new Object[rowCount + 2][columnCount];
            for(String string : headers){
                data[currRow][currColumn] = string;
                currColumn++;
            }
            currRow++;

            while(resultSet.next()) {
                switch (report) {
                    case "Accounting": {
                        double discount = 1 - (resultSet.getDouble("discount_percentage") / 100.0);
                        data[currRow][0] = resultSet.getString("name");
                        data[currRow][1] = resultSet.getDate("picnic_date_time");
                        data[currRow][2] = resultSet.getString("event_address");
                        data[currRow][3] = resultSet.getString("item_desc");
                        int quantity = resultSet.getInt("item_quantity");
                        data[currRow][4] = quantity;
                        double cost = resultSet.getDouble("item_cost");
                        double expense = resultSet.getDouble("item_supplier_cost");
                        data[currRow][5] = cost * discount;
                        data[currRow][6] = expense  * -1;
                        data[currRow][7] = quantity * cost * discount;
                        data[currRow][8] = quantity * expense  * -1;
                        data[currRow][9] = (quantity * cost * discount) + (quantity * expense * -1);

                        totalsMoney[7] += quantity * cost * discount;
                        totalsMoney[8] += quantity * expense  * -1;
                        totalsMoney[9] += (quantity * cost * discount) + (quantity * expense * -1);
                        break;
                    }

                    case "Conversion Rate":{
                        data[currRow][0] = fromDate;
                        data[currRow][1] = toDate;
                        int paid = resultSet.getInt("paid");
                        int nonPaid = resultSet.getInt("non_paid");
                        data[currRow][2] = paid;
                        data[currRow][3] = nonPaid;
                        DecimalFormat df = new DecimalFormat("0.00");
                        data[currRow][4] =  df.format((((double) paid / ((double)nonPaid + paid)) * 100)) + "%";
                        break;
                    }
                    case "Best Locations":{
                        data[currRow][0] = resultSet.getString("location");
                        data[currRow][1] = resultSet.getInt("location_requested");
                        data[currRow][2] = resultSet.getInt("location_paid");

                        totalsCount[1] += resultSet.getInt("location_requested");
                        totalsCount[2] += resultSet.getInt("location_paid");
                        break;
                    }
                    case "Most Picnic Types":{
                        data[currRow][0] = resultSet.getString("type");
                        data[currRow][1] = resultSet.getInt("type_requested");
                        data[currRow][2] = resultSet.getInt("type_paid");

                        totalsCount[1] += resultSet.getInt("type_requested");
                        totalsCount[2] += resultSet.getInt("type_paid");
                        break;
                    }
                    case "Best Addons":{
                        data[currRow][0] = resultSet.getString("item_desc");
                        data[currRow][1] = resultSet.getInt("item_count_requested");
                        data[currRow][2] = resultSet.getInt("item_count_paid");
                        data[currRow][3] = resultSet.getDouble("revenue");
                        data[currRow][4] = resultSet.getDouble("expense") * -1;
                        data[currRow][5] = resultSet.getDouble("profit");

                        totalsCount[1] += resultSet.getInt("item_count_requested");
                        totalsCount[2] += resultSet.getInt("item_count_paid");
                        totalsMoney[3] += resultSet.getInt("revenue");
                        totalsMoney[4] += resultSet.getInt("expense") * -1;
                        totalsMoney[5] += resultSet.getInt("profit");
                        break;
                    }
                    default:
                        break;
                }
                currRow++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }

        // Totals
        switch (report){
            case "Accounting":{
                data[currRow][6] = "Totals:";
                data[currRow][7] = totalsMoney[7];
                data[currRow][8] = totalsMoney[8];
                data[currRow][9] = totalsMoney[9];
                break;
            }
            case "Conversion Rate": {
                break;
            }
            case "Best Locations":{
                data[currRow][0] = "Totals:";
                data[currRow][1] = totalsCount[1];
                data[currRow][2] = totalsCount[2];
                break;
            }
            case "Most Picnic Types":{
                data[currRow][0] = "Totals:";
                data[currRow][1] = totalsCount[1];
                data[currRow][2] = totalsCount[2];
                break;
            }
            case "Best Addons":{
                data[currRow][0] = "Totals:";
                data[currRow][1] = totalsCount[1];
                data[currRow][2] = totalsCount[2];
                data[currRow][3] = totalsMoney[3];
                data[currRow][4] = totalsMoney[4];
                data[currRow][5] = totalsMoney[5];
                break;
            }
            default:
                break;
        }

        export(data, report, reportName);
    }

    public void export(Object[][] data, String sheetName, String reportName){
        if(data == null)
            return;
        if(data.length < 1)
            return;

        int totalRows = data.length;
        int currColumn = 0;
        int currRow = 0;

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        CellStyle cellStyle;

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(currRow);
        currRow++;
        for(Object header : data[0]){
            Cell cell = headerRow.createCell(currColumn);
            cell.setCellValue((String) header);
            cell.setCellStyle(headerStyle);
            currColumn++;
        }

        while(currRow < totalRows){
            currColumn = 0;
            Row row =  sheet.createRow(currRow);
            for(Object cellValue : data[currRow]){
                Cell cell = row.createCell(currColumn);
                cellStyle = workbook.createCellStyle();
                if(cellValue instanceof String){
                    cell.setCellValue((String) cellValue);
                }
                else if(cellValue instanceof Integer){
                    cell.setCellValue((Integer) cellValue);
                    cellStyle.setDataFormat((short)1);
                    cell.setCellStyle(cellStyle);
                }
                else if(cellValue instanceof Double){
                    cell.setCellValue((Double) cellValue);
                    cellStyle.setDataFormat((short)0x7);
                    cell.setCellStyle(cellStyle);
                }
                else if(cellValue instanceof Float){
                    cell.setCellValue((Float) cellValue);
                    cellStyle.setDataFormat((short)2);
                    cell.setCellStyle(cellStyle);
                }
                else if(cellValue instanceof Date){
                    cell.setCellValue((Date) cellValue);
                    cellStyle.setDataFormat((short)0xf);
                    cell.setCellStyle(cellStyle);
                }
                else if(cellValue instanceof LocalDate){
                    cell.setCellValue((LocalDate) cellValue);
                    cellStyle.setDataFormat((short)0xf);
                    cell.setCellStyle(cellStyle);
                }

                currColumn++;
            }
            currRow++;
        }

        for(int i = 0; i < data[currRow - 1].length; i++){
            Cell cell = sheet.getRow(currRow - 1).getCell(i);
            cell.getCellStyle().setFont(headerFont);
            sheet.autoSizeColumn(i);
        }

        // Charts
        switch (sheetName){
            case "Accounting":{
                break;
            }
            case "Conversion Rate": {
                XSSFDrawing drawing = sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = drawing.createAnchor(0,0,0,0,0,4,6,20);

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("Conversion Rate");
                chart.setTitleOverlay(false);
                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.TOP_RIGHT);

                XDDFDataSource<String> category = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                        new CellRangeAddress(0, 0, 2, 3));
                XDDFNumericalDataSource<Double> value = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                        new CellRangeAddress(1, 1, 2, 3));

                XDDFChartData chartData =  chart.createData(ChartTypes.PIE, null, null);
                chartData.setVaryColors(true);
                chartData.addSeries(category, value);
                chart.plot(chartData);

                break;
            }
            case "Best Locations":{
                XSSFDrawing drawing = sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = drawing.createAnchor(0,0,0,0,0,totalRows + 1,7,35);

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("Locations");
                chart.setTitleOverlay(false);
                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.TOP_RIGHT);

                XDDFDataSource<String> category = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                        new CellRangeAddress(1, totalRows - 2, 0, 0 ));
                XDDFNumericalDataSource<Double> value = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                        new CellRangeAddress(1, totalRows - 2, 2, 2));

                XDDFChartData chartData =  chart.createData(ChartTypes.PIE, null, null);
                chartData.setVaryColors(true);
                chartData.addSeries(category, value);
                chart.plot(chartData);
                break;
            }
            case "Most Picnic Types":{
                XSSFDrawing drawing = sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = drawing.createAnchor(0,0,0,0,0,totalRows + 1,7,35);

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("Occasions");
                chart.setTitleOverlay(false);
                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.TOP_RIGHT);

                XDDFDataSource<String> category = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                        new CellRangeAddress(1, totalRows - 2, 0, 0 ));
                XDDFNumericalDataSource<Double> value = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                        new CellRangeAddress(1, totalRows - 2, 2, 2));

                XDDFChartData chartData =  chart.createData(ChartTypes.PIE, null, null);
                chartData.setVaryColors(true);
                chartData.addSeries(category, value);
                chart.plot(chartData);
                break;
            }
            case "Best Addons":{
                break;
            }
            default:
                break;
        }

        try{
            String filePath = System.getProperty("user.home") + "/Downloads/" + reportName + ".xlsx";
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            outputStream.close();
            Desktop.getDesktop().open(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
