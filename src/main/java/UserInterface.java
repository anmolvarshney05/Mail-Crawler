import org.apache.commons.io.FilenameUtils;
import javax.swing.WindowConstants;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class UserInterface extends JFrame{
    private JButton browse;
    private JButton run;
    private JTextField directory;
    private JTextField URL;
    private JFileChooser fileChooser;
    private JLabel selectURL;
    private JLabel selectDirectory;
    private JLabel error;
    private JPanel one;
    private JPanel two;
    private JPanel three;
    private JPanel four;
    private JPanel five;
    private JPanel six;

    public UserInterface(){
        super("Web Crawler");
        initialise();
        addListener();
        addToLayout();
        pack();
    }

    private void initialise(){
        browse = new JButton("Browse");
        run = new JButton("Run Crawler");
        directory = new JTextField(FilenameUtils.normalize(System.getProperty("user.home") + File.separator + "Desktop"), 30);
        URL = new JTextField("http://mail-archives.apache.org/mod_mbox/maven-users/", 40);
        selectURL = new JLabel("URL");
        selectDirectory = new JLabel("Select Directory");
        error = new JLabel("");
        one = new JPanel(new FlowLayout());
        two = new JPanel(new FlowLayout());
        three = new JPanel(new FlowLayout());
        four = new JPanel(new FlowLayout());
        five = new JPanel(new FlowLayout());
        six = new JPanel(new FlowLayout());
        directory.setEditable(false);
    }

    private class backgroundRun extends Thread{
        @Override
        public void run() {
            Crawler crawler = new Crawler(URL.getText(), directory.getText());
            try {
                crawler.run();
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException exception) {
                return;
            }
        }
    }

    private void addListener(){
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new java.io.File("."));
                fileChooser.setDialogTitle("Browse");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    directory.setText(fileChooser.getSelectedFile().toString());
                }
            }
        });
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(URL.getText().equals("")){
                    error.setText("Please specify URL");
                }
                else if(directory.getText().equals("")){
                    error.setText("Please select a Directory");
                }
                else{
                    backgroundRun thread = new backgroundRun();
                    thread.setDaemon(true);
                    thread.start();
                }
            }
        });
    }

    private void addToLayout(){
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        one.add(selectURL);
        two.add(URL);
        three.add(selectDirectory);
        four.add(directory);
        four.add(browse);
        five.add(run);
        six.add(error);
        add(one);
        add(two);
        add(three);
        add(four);
        add(five);
        add(six);
    }

    public static void main(String args[]){
        UserInterface userInterface = new UserInterface();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        userInterface.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        userInterface.setLocation(dimension.width / 2 - userInterface.getSize().width / 2, dimension.height / 2 - userInterface.getSize().height / 2);
        userInterface.setVisible(true);
    }
}
