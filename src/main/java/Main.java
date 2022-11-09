import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {


    private HashMap<String, Regex> regexMap = new HashMap<String, Regex>();

    private HashMap<String, String> mainValuesMap = new HashMap<String, String>();

    // Last 4 digit of cc -> list of values mapping
    private HashMap<String, HashMap<String, String>> specificValuesMap = new HashMap<String, HashMap<String, String>>();


    // Last 4 digit of cc -> list of statement lines
    private HashMap<String, List<StatementLine>> statementsMap = new HashMap<String, List<StatementLine>>();


    public Main (){
        this.configure();
    }

    public static void main(String[] args){
       Main main = new Main();
       main.process();
    }

    public void process(){
        this.writeFile();
        this.readFile();
    }


    private void configure(){

        // Regex for current card
        // .+\s(\d{4}+\s+\d{4}+\s+\d{4}+\s+\d{4}+)
        //  --> Doesn't require specific mapping, we will handle it in the main codes

        // Summary
        SingleRegex singleRegex = new SingleRegex();
        singleRegex.setRegex("Account Number\\/Nombor Akaun Current Balance.+");
        singleRegex.setCurrent(false);
        singleRegex.setRegexNext("(\\d{4}+\\s+\\d{4}+\\s+\\d{4}+\\s+\\d{4}+)\\s+(.+)+\\s(.+)");
        singleRegex.getValueMapping().put(1, "main_credit_card");
        singleRegex.getValueMapping().put(2, "main_current_account_balance");
        singleRegex.getValueMapping().put(3, "main_minimum_amount_to_pay");
        singleRegex.setMatchesCount(3);
        regexMap.put(singleRegex.getRegex(), singleRegex);

        // Previous balance
        SingleRegex singleRegex2 = new SingleRegex();
        singleRegex2.setRegex("YOUR PREVIOUS STATEMENT BALANCE\\s+(.+)");
        singleRegex2.setCurrent(true);
        singleRegex2.getValueMapping().put(1, "main_previous_balance");
        singleRegex2.setMatchesCount(1);
        regexMap.put(singleRegex2.getRegex(), singleRegex2);


        StatementRegex stmtRegexDebit = new StatementRegex();
        stmtRegexDebit.setCredit(false);
        // (\d{2}\/\d{2})\s+(\d{2}\/\d{2})\s+(.+)\s+([A-Z]{2})\s+([\d,]+\.\d{2})
        stmtRegexDebit.setRegex("(\\d{2}/\\d{2})\\s+(\\d{2}/\\d{2})\\s+(.+)\\s+([\\d,]+\\.\\d{2})");
        stmtRegexDebit.setMatchesCount(4);
        regexMap.put(stmtRegexDebit.getRegex(), stmtRegexDebit);


        // Credit statement regex
        StatementRegex stmtRegexCredit = new StatementRegex();
        stmtRegexCredit.setCredit(true);
            // (\d{2}\/\d{2})\s+(\d{2}\/\d{2})\s+(.+)\s+([\d,]+\.\d{2})CR
        stmtRegexCredit.setRegex("(\\d{2}/\\d{2})\\s+(\\d{2}/\\d{2})\\s+(.+)\\s+([\\d,]+\\.\\d{2})CR");
        stmtRegexCredit.setMatchesCount(4);
        regexMap.put(stmtRegexCredit.getRegex(), stmtRegexCredit);

        // Total credit
        SingleRegex totalCredit = new SingleRegex();
        totalCredit.setCurrent(false);
        totalCredit.setRegex("\\(JUMLAH KREDIT\\)");
        totalCredit.setRegexNext("(.+)");
        totalCredit.setMatchesCount(1);
        totalCredit.getValueMapping().put(1, "total_credit");
        totalCredit.setSpecificCard(true);
        regexMap.put(totalCredit.getRegex(), totalCredit);

        // Total debit
        SingleRegex totalDebit = new SingleRegex();
        totalDebit.setCurrent(false);
        totalDebit.setRegex("\\(JUMLAH DEBIT\\)");
        totalDebit.setRegexNext("(.+)");
        totalDebit.setMatchesCount(1);
        totalDebit.getValueMapping().put(1, "total_debit");
        totalDebit.setSpecificCard(true);
        regexMap.put(totalDebit.getRegex(), totalDebit);

    }

    private void processSingleRegex(String line, String currentCard, String regex, SingleRegex singleRegex){
        Pattern pattern = Pattern.compile(regex); // For single regex with isCurrent=false, the regex value is different than the main ones
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()){
            for (Integer group : singleRegex.getValueMapping().keySet()){
                String match = matcher.group(group);
                String key = singleRegex.getValueMapping().get(group);
                if (singleRegex.isSpecificCard){
                    this.specificValuesMap.computeIfAbsent(currentCard, k -> new HashMap<String, String>());
                    this.specificValuesMap.get(currentCard).put(key, match);
                } else {
                    this.mainValuesMap.put(key, match);
                }
            }
        }
    }

    private void processStatementRegex(String line, String currentCard, StatementRegex statementRegex){
        Pattern pattern = Pattern.compile(statementRegex.getRegex());
        Matcher matcher = pattern.matcher(line);
        StatementLine statementLine = new StatementLine();
        while (matcher.find()){
            statementLine.setPostingDate(matcher.group(1));
            statementLine.setTransactionDate(matcher.group(2));
            statementLine.setDescription(matcher.group(3));
            if (statementRegex.isCredit()){
                statementLine.setAmount(matcher.group(4));
            } else {
                // For now, we will capture only 4 groups. Description will include country code as well
                statementLine.setAmount(matcher.group(4));
            }
        }
        statementLine.setCredit(statementRegex.isCredit);
        this.statementsMap.computeIfAbsent(currentCard, k -> new ArrayList<StatementLine>());
        this.statementsMap.get(currentCard).add(statementLine);

    }

    private void readFile(){
        File input = new File("output.xml");
        try {
            Document document = Jsoup.parse(input);
            Elements pages = document.getElementsByClass("page");
            String currentcc = null;
            boolean isNextRegex = false; // Set to true only if the value will be found in next line.
            String nextRegex = null;
            SingleRegex currentRegex = null;
            for (Element page : pages){
                for (Element p : page.getElementsByTag("p")){
                    // https://stackoverflow.com/questions/5454172/prevent-jsoup-from-discarding-extra-whitespace
                    if (p.childNodeSize() > 0){
                        Stream<String> lines = ((TextNode) p.childNode(0)).getWholeText().lines();
                        for (Object strline : lines.toArray()){
                            String line = (String) strline;
                            if (isNextRegex == true){
                                // Extract the value now
                                //System.out.println("Process next regex now");
                                this.processSingleRegex(line, currentcc, nextRegex, currentRegex);
                                isNextRegex = false;
                                nextRegex = null;
                            } else {
                                // Regex for current card
                                // .+\s\d{4}+\s+\d{4}+\s+\d{4}+\s+(\d{4}+)
                                // Check if the line contains current credit card
                                Pattern pattern = Pattern.compile(".+\\s\\d{4}+\\s+\\d{4}+\\s+\\d{4}+\\s+(\\d{4}+)");
                                Matcher matcher = pattern.matcher(line);
                                if (matcher.find()){
                                    String last4Digit = matcher.group(1);
                                    currentcc = last4Digit;
                                } else {
                                    // Check for other regex
                                    for (String regex : regexMap.keySet()){
                                        if (line.matches(regex)){
                                            Regex r = regexMap.get(regex);
                                            if (r instanceof SingleRegex){
                                                SingleRegex singleRegex = (SingleRegex) r;
                                                if (singleRegex.isCurrent){
                                                    this.processSingleRegex(line, currentcc, singleRegex.getRegex(), singleRegex);
                                                } else {
                                                    // Will process the value in the next line
                                                    isNextRegex = true;
                                                    nextRegex = singleRegex.getRegexNext();
                                                    currentRegex = singleRegex;
                                                }
                                            } else {
                                                StatementRegex statementRegex = (StatementRegex) r;
                                                this.processStatementRegex(line, currentcc, statementRegex);
                                            }
                                        }

                                    }

                                }
                            }

                        }

                    }


                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void writeFile(){
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("output.xml"));
            bufferedWriter.write(parseToHTML());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (TikaException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String parseToHTML() throws IOException, SAXException, TikaException {
        ContentHandler handler = new ToXMLContentHandler();
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = Main.class.getResourceAsStream("<filename>")) {
            parser.parse(stream, handler, metadata);
            return handler.toString();
        }
    }

    public HashMap<String, Regex> getRegexMap() {
        return regexMap;
    }

    public void setRegexMap(HashMap<String, Regex> regexMap) {
        this.regexMap = regexMap;
    }

    public HashMap<String, String> getMainValuesMap() {
        return mainValuesMap;
    }

    public void setMainValuesMap(HashMap<String, String> mainValuesMap) {
        this.mainValuesMap = mainValuesMap;
    }

    public HashMap<String, HashMap<String, String>> getSpecificValuesMap() {
        return specificValuesMap;
    }

    public void setSpecificValuesMap(HashMap<String, HashMap<String, String>> specificValuesMap) {
        this.specificValuesMap = specificValuesMap;
    }

    public HashMap<String, List<StatementLine>> getStatementsMap() {
        return statementsMap;
    }

    public void setStatementsMap(HashMap<String, List<StatementLine>> statementsMap) {
        this.statementsMap = statementsMap;
    }

}
