
import java.util.ArrayList;
import java.util.Scanner;

public class Main {


    public static void main(String[] args){
        Parser parser = new Parser();
        System.out.println("Enter input folder: ");
        Scanner scanner = new Scanner(System.in);
        String folderPath = scanner.nextLine();
        parser.process(folderPath);
        ArrayList<Output> outputs = parser.getOutputs();
        for (Output output : outputs){
            System.out.println(output.getMainValuesMap());
            System.out.println(output.getStatementsMap());
        }
    }



}
