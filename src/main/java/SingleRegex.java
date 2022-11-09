import java.util.HashMap;

public class SingleRegex extends Regex {

    // Whether the actual value is in current line, or next line
    public boolean isCurrent;



    // Whether value of this regex is tied to specific card
    public boolean isSpecificCard;
    // If isCurrent = false, parse value in the next line using this regex
    public String regexNext;

    // Map regex match index to its value mapping
    public HashMap<Integer, String> valueMapping = new HashMap<Integer, String>();



    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public String getRegexNext() {
        return regexNext;
    }

    public void setRegexNext(String regexNext) {
        this.regexNext = regexNext;
    }


    public HashMap<Integer, String> getValueMapping() {
        return valueMapping;
    }

    public void setValueMapping(HashMap<Integer, String> valueMapping) {
        this.valueMapping = valueMapping;
    }

    public boolean isSpecificCard() {
        return isSpecificCard;
    }

    public void setSpecificCard(boolean specificCard) {
        isSpecificCard = specificCard;
    }
}
