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

            Row balanceRow = sheet.createRow(rowNum);
            balanceRow.createCell(0).setCellValue("Current Balance");
            balanceRow.createCell(1).setCellValue(output.getMainValuesMap().get("main_current_account_balance"));
            rowNum++;
            Row prevRow = sheet.createRow(rowNum);
            prevRow.createCell(0).setCellValue("Previous Balance");
            prevRow.createCell(1).setCellValue(output.getMainValuesMap().get("main_previous_balance"));
            rowNum++;
            Row minRow = sheet.createRow(rowNum);
            minRow.createCell(0).setCellValue("Mininum Payment");
            minRow.createCell(1).setCellValue(output.getMainValuesMap().get("main_current_account_balance"));
            rowNum++;


            sheet.createRow(rowNum);
            rowNum++;
            sheet.createRow(rowNum);
            rowNum++;

            for(String cc : output.getStatementsMap().keySet()) {
                ArrayList<StatementLine> statementLines = (ArrayList<StatementLine>) output.getStatementsMap().get(cc);

                Row ccRow = sheet.createRow(rowNum);
                ccRow.createCell(0).setCellValue("Credit Card Number");
                ccRow.createCell(1).setCellValue(cc);
                rowNum+=1;
                sheet.createRow(rowNum);
                rowNum++;


                // Why didn't I separate them during the data process? No idea, I am dumb. Not going to refractor too much at this point.
                ArrayList<StatementLine> debits = new ArrayList<StatementLine>();
                ArrayList<StatementLine> credits = new ArrayList<StatementLine>();
                for (StatementLine statementLine : statementLines){
                    if (statementLine.isCredit){
                        credits.add(statementLine);
                    } else {
                        debits.add(statementLine);
                    }
                }
                // Credit
                Row title1 = sheet.createRow(rowNum);
                title1.createCell(1).setCellValue("CREDIT");
                rowNum+=1;
                for (StatementLine statementLine : credits){
                    Row stRow = sheet.createRow(rowNum);
                    stRow.createCell(0).setCellValue(statementLine.getPostingDate());
                    stRow.createCell(1).setCellValue(statementLine.getTransactionDate());
                    stRow.createCell(2).setCellValue(statementLine.getDescription());
                    stRow.createCell(3).setCellValue(statementLine.getAmount());
                    rowNum+=1;
                }
                Row total1 = sheet.createRow(rowNum);
                total1.createCell(2).setCellValue("TOTAL CREDIT");
                total1.createCell(3).setCellValue(output.getSpecificValuesMap().get(cc).get("total_credit"));
                rowNum+=1;
                sheet.createRow(rowNum);
                rowNum++;

                // Debit
                Row title2 = sheet.createRow(rowNum);
                title2.createCell(1).setCellValue("DEBIT");
                rowNum++;
                for (StatementLine statementLine : debits){
                    Row stRow = sheet.createRow(rowNum);
                    stRow.createCell(0).setCellValue(statementLine.getPostingDate());
                    stRow.createCell(1).setCellValue(statementLine.getTransactionDate());
                    stRow.createCell(2).setCellValue(statementLine.getDescription());
                    stRow.createCell(3).setCellValue(statementLine.getAmount());
                    rowNum+=1;
                }
                Row total2 = sheet.createRow(rowNum);
                total2.createCell(2).setCellValue("TOTAL DEBIT");
                total2.createCell(3).setCellValue(output.getSpecificValuesMap().get(cc).get("total_debit"));
                rowNum+=1;
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
