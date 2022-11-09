import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

@TestMethodOrder(OrderAnnotation.class)
public class TestValidateStatements {

    private static Main main;
    private static BigDecimal calcAllDebit;
    private static BigDecimal calcAllCredit;

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
        main = new Main();
        main.process();
    }

    @Test
    @Order(1)
    public void validateEachCard(){
        calcAllDebit = this.parseAmount("0.00");
        calcAllCredit = this.parseAmount("0.00");
        for (String cc : main.getSpecificValuesMap().keySet()){
            BigDecimal totalCredit =  this.parseAmount(main.getSpecificValuesMap().get(cc).get("total_credit")) ;
            BigDecimal totalDebit = this.parseAmount(main.getSpecificValuesMap().get(cc).get("total_debit"));
            ArrayList<StatementLine> statementLines = (ArrayList<StatementLine>) main.getStatementsMap().get(cc);
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


    }

    @Test
    @Order(2)
    public void validateTotal(){
        BigDecimal previousBalance = this.parseAmount(main.getMainValuesMap().get("main_previous_balance"));
        BigDecimal currentBalance = this.parseAmount(main.getMainValuesMap().get("main_current_account_balance"));
        BigDecimal calcCurrentBalance = calcAllDebit.subtract(calcAllCredit).add(previousBalance);
        assertEquals(0, calcCurrentBalance.compareTo(currentBalance));
    }





}
