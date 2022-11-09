import java.util.HashMap;
import java.util.List;

public class Output {


    private HashMap<String, String> mainValuesMap = new HashMap<String, String>();

    // Last 4 digit of cc -> list of values mapping
    private HashMap<String, HashMap<String, String>> specificValuesMap = new HashMap<String, HashMap<String, String>>();

    // Last 4 digit of cc -> list of statement lines
    private HashMap<String, List<StatementLine>> statementsMap = new HashMap<String, List<StatementLine>>();


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
