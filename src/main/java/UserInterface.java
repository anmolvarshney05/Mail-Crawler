import org.apache.commons.io.FilenameUtils;
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

public class UserInterface extends JFrame{
    private JButton browse;
    private JButton run;
    private JTextField directory;
    private JTextField URL;
    private JFileChooser fileChooser;
    private JLabel selectURL;
    private JLabel selectDirectory;
    private JPanel one;
    private JPanel two;
    private JPanel three;
    private JPanel four;
    private JPanel five;

    // Constructor
    public UserInterface(){
        super("Web Crawler");
        initialise();
        addListeners();
        addToLayout();
        pack();
    }

    // Initialise fields
    private void initialise(){
        browse = new JButton("Browse");
        run = new JButton("Run Crawler");
        directory = new JTextField(FilenameUtils.normalize(System.getProperty("user.home") + File.separator + "Desktop"), 30);
        URL = new JTextField("http://mail-archives.apache.org/mod_mbox/maven-users/", 40);
        selectURL = new JLabel("URL");
        selectDirectory = new JLabel("Select Directory");
        one = new JPanel(new FlowLayout());
        two = new JPanel(new FlowLayout());
        three = new JPanel(new FlowLayout());
        four = new JPanel(new FlowLayout());
        five = new JPanel(new FlowLayout());
        directory.setEditable(false);
    }

    // Run Crawler Daemon Thread
    private class backgroundRun extends Thread{
        private int parameter;

        backgroundRun(int param){
            parameter = param;
        }

        @Override
        public void run() {
            try {
                if(parameter == 0) {
                    TrackProgress trackProgress = new TrackProgress(directory.getText() + File.separator + "Mail Archive");
                    trackProgress.deleteProgress();
                }
                Crawler crawler = new Crawler(URL.getText(), directory.getText());
                crawler.run();
            } catch (IOException | NoSuchAlgorithmException exception) {
                return;
            }
        }
    }

    // Add listener(s) for elements
    private void addListeners(){
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
            // Get OptionPane
            private JOptionPane getOptionPane(JComponent parent) {
                JOptionPane pane = null;
                if (!(parent instanceof JOptionPane)) {
                    pane = getOptionPane((JComponent) parent.getParent());
                } else {
                    pane = (JOptionPane) parent;
                }
                return pane;
            }

            // Add listeners to buttons
            private void addListener(JButton button){
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane pane = getOptionPane((JComponent) e.getSource());
                        pane.setValue(button);
                    }
                });
            }

            // Get Buttons to add on Alert
            private Object[] getButtons(){
                JButton startAgain = new JButton("Start Again");
                JButton resume = new JButton("Continue");
                addListener(startAgain);
                addListener(resume);
                Object[] options = {startAgain, resume};
                return options;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    URL urlTemp = new URL(URL.getText());
                } catch (MalformedURLException e1) {
                    JOptionPane.showMessageDialog(null, "Please specify a valid URL", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                TrackProgress trackProgress = new TrackProgress(directory.getText() + File.separator + "Mail Archive");
                int option = 1;
                try {
                    if(trackProgress.fileExists(URL.getText())){
                        Object[] options = getButtons();
                        option = JOptionPane.showOptionDialog(null, "Click Continue to Resume", "Archive Found", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,null, options, options[1]);
                    }
                    backgroundRun thread = new backgroundRun(option);
                    thread.setDaemon(true);
                    thread.start();
                } catch (IOException exception) {}
            }
        });
    }

    // Add elements to layout
    private void addToLayout(){
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        one.add(selectURL);
        two.add(URL);
        three.add(selectDirectory);
        four.add(directory);
        four.add(browse);
        five.add(run);
        add(one);
        add(two);
        add(three);
        add(four);
        add(five);
    }

    public static void main(String args[]){
        UserInterface userInterface = new UserInterface();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        userInterface.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        userInterface.setLocation(dimension.width / 2 - userInterface.getSize().width / 2, dimension.height / 2 - userInterface.getSize().height / 2);
        userInterface.setVisible(true);
    }
}
