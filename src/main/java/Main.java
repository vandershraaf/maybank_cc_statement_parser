
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class Main {

    File inputFolder;
    File outputFolder;
    boolean isTransactionOnly;

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
        final JLabel label = new JLabel();
        JButton button = new JButton("Choose input folder");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if(option == JFileChooser.APPROVE_OPTION){
                    inputFolder = fileChooser.getSelectedFile();
                    label.setText("Input : " + inputFolder.getAbsolutePath());
                }else{
                    label.setText("Open command canceled");
                }
            }
        });
        panel.add(button);
        panel.add(label);
        frame.getContentPane().add(panel, BorderLayout.NORTH);


        JPanel panel2 = new JPanel();
        LayoutManager layout2 = new FlowLayout();
        panel2.setLayout(layout2);
        final JLabel label2 = new JLabel();
        JButton button2 = new JButton("Choose output folder");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if(option == JFileChooser.APPROVE_OPTION){
                    outputFolder = fileChooser.getSelectedFile();
                    label2.setText("Output : " + outputFolder.getAbsolutePath());
                }else{
                    label2.setText("Open command canceled");
                }
            }
        });
        panel2.add(button2);
        panel2.add(label2);

        JCheckBox checkBox = new JCheckBox("Only transactions in output?", false);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()){
                    isTransactionOnly = true;
                } else {
                    isTransactionOnly = false;
                }
            }
        });
        panel2.add(checkBox);

        frame.getContentPane().add(panel2, BorderLayout.CENTER);


        JPanel panelSubmit = new JPanel();
        LayoutManager layout3 = new FlowLayout();
        panelSubmit.setLayout(layout3);
        JButton submit = new JButton("Go!");
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Parser parser = new Parser();
                parser.process(inputFolder);
                ArrayList<Output> outputs = parser.getOutputs();
                for (Output output : outputs){
                    System.out.println(output.getMainValuesMap());
                    System.out.println(output.getStatementsMap());
                    Writer writer = new Writer();
                    if (isTransactionOnly){
                        writer.outputExcelTransactionsOnly(output, outputFolder);
                    } else {
                        writer.outputExcel(output, outputFolder);
                    }
                }
            }
        });
        panelSubmit.add(submit);
        frame.getContentPane().add(panelSubmit, BorderLayout.SOUTH);

    }






}
