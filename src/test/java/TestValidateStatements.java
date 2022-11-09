import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

@TestMethodOrder(OrderAnnotation.class)
public class TestValidateStatements {

    private static Parser parser;
    private static String folderPath = "";

    // Reference: https://stackoverflow.com/a/23991368/396092
    private BigDecimal parseAmount(String amount){
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
        try {
            decimalFormat.setParseBigDecimal(true);
            return (BigDecimal) decimalFormat.parse(amount.replaceAll("[^\\d.,]",""));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void init(){
        parser = new Parser();
        parser.process(folderPath);
    }



    @Test
    @Order(1)
    public void validate(){

        ArrayList<Output> outputs = parser.getOutputs();
        for (Output output : outputs){
            BigDecimal calcAllDebit = this.parseAmount("0.00");
            BigDecimal calcAllCredit = this.parseAmount("0.00");
            for (String cc : output.getSpecificValuesMap().keySet()){
                BigDecimal totalCredit =  this.parseAmount(output.getSpecificValuesMap().get(cc).get("total_credit")) ;
                BigDecimal totalDebit = this.parseAmount(output.getSpecificValuesMap().get(cc).get("total_debit"));
                ArrayList<StatementLine> statementLines = (ArrayList<StatementLine>) output.getStatementsMap().get(cc);
                BigDecimal calcTotalCredit = this.parseAmount("0.00");
                BigDecimal calcTotalDebit = this.parseAmount("0.00");
                for (StatementLine statementLine : statementLines){
                    if (statementLine.isCredit){
                        calcTotalCredit = calcTotalCredit.add(this.parseAmount(statementLine.getAmount()));
                    } else {
                        calcTotalDebit = calcTotalDebit.add(this.parseAmount(statementLine.getAmount()));
                    }
                }

                assertEquals(0, totalCredit.compareTo(calcTotalCredit));
                assertEquals(0, totalDebit.compareTo(calcTotalDebit));

                calcAllCredit = calcAllCredit.add(calcTotalCredit);
                calcAllDebit = calcAllDebit.add(calcTotalDebit);
            }
            BigDecimal previousBalance = this.parseAmount(output.getMainValuesMap().get("main_previous_balance"));
            BigDecimal currentBalance = this.parseAmount(output.getMainValuesMap().get("main_current_account_balance"));
            BigDecimal calcCurrentBalance = calcAllDebit.subtract(calcAllCredit).add(previousBalance);
            assertEquals(0, calcCurrentBalance.compareTo(currentBalance));
        }


    }


}
