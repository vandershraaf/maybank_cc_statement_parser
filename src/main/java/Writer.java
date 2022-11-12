import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Writer {


    public void outputExcel(Output output, int index){
        int rowNum = 0;
        OutputStream fileOut = null;
        Workbook wb = new XSSFWorkbook();
        try {
            fileOut = new FileOutputStream(index + ".xlsx");
            Sheet sheet = wb.createSheet("Statements");

            // Statement month
            Row monthRow = sheet.createRow(rowNum);
            monthRow.createCell(0).setCellValue("Statement Month");
            monthRow.createCell(1).setCellValue( (output.getStatementMonth() % 10 == 1 ? "" : "0") + output.getStatementMonth()
                                                        + "/" + output.getStatementYear()  );
            rowNum++;
            sheet.createRow(rowNum);
            rowNum++;
            for(String cc : output.getStatementsMap().keySet()) {
                ArrayList<StatementLine> statementLines = (ArrayList<StatementLine>) output.getStatementsMap().get(cc);

                Row ccRow = sheet.createRow(rowNum);
                ccRow.createCell(0).setCellValue("Credit Card Number");
                ccRow.createCell(1).setCellValue(cc);
                rowNum+=1;
                for (StatementLine statementLine : statementLines){
                    Row stRow = sheet.createRow(rowNum);
                    stRow.createCell(0).setCellValue(statementLine.getPostingDate());
                    stRow.createCell(1).setCellValue(statementLine.getTransactionDate());
                    stRow.createCell(2).setCellValue(statementLine.getDescription());
                    stRow.createCell(3).setCellValue(statementLine.getAmount());
                    rowNum+=1;
                }
                sheet.createRow(rowNum);
                rowNum++;
            }
            wb.write(fileOut);
            wb.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fileOut.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
