
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class Main {

    File inputFolder;

    public static void main(String[] args){
        Main main = new Main();
        main.createWindow();
    }


    private void createWindow() {
        JFrame frame = new JFrame("Maybank CC Statement Parser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createUI(frame);
        frame.setSize(560, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createUI(final JFrame frame){
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);

        JButton button = new JButton("Choose folder");
        final JLabel label = new JLabel();

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if(option == JFileChooser.APPROVE_OPTION){
                    inputFolder = fileChooser.getSelectedFile();
                    label.setText("File Selected: " + inputFolder.getAbsolutePath());
                }else{
                    label.setText("Open command canceled");
                }
            }
        });

        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Parser parser = new Parser();
                parser.process(inputFolder);
                ArrayList<Output> outputs = parser.getOutputs();
                for (Output output : outputs){
                    System.out.println(output.getMainValuesMap());
                    System.out.println(output.getStatementsMap());
                    Writer writer = new Writer();
                    writer.outputExcel(output);
                }


            }
        });
        panel.add(button);
        panel.add(label);
        panel.add(submit);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }






}
