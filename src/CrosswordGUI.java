import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

class CrosswordGUI extends JFrame {
    private static final long serialVersionUID = 856729362811627651L;
    private JMenu fileMenu, aboutMenu;
    private CrosswordControl mainGridPanel;
    private JPanel mainScreen;
    private Crossword crossword;
    private AnagramPanel anagramPanel;
    private JTextField usernameTextField;
    private JLabel currentUserLabel;
    private JCheckBox solveClueSupportButton;
    private JButton playButton, checkAllButton, clearLogButton;
    private JButton loadCW, saveCW, loadCWXML, saveCWXML, loadCurrent,
            saveCurrent;
    private JScrollPane clueView, logView;
    private Container content;
    private transient FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(
            "Save crossword game file (*.cwx)", "cwx");

    public CrosswordGUI(Crossword crossword) {
        super("Crossword Application");
        this.crossword = crossword;
        this.mainGridPanel = new CrosswordControl(crossword);
        this.anagramPanel = new AnagramPanel();
    }

    public void init() {
        content = this.getContentPane();
        JMenuBar menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        JMenuItem saveCWIt = new JMenuItem("Save Crossword File");
        saveCWIt.addActionListener(new SaveCrossWord());
        JMenuItem loadCWIt = new JMenuItem("Load Crossword File");
        loadCWIt.addActionListener(new LoadCrossWord());
        JMenuItem saveCWXMLIt = new JMenuItem("Save Crossword XML File");
        saveCWXMLIt.addActionListener(new SaveCrossWordXML());
        JMenuItem loadCWXMLIt = new JMenuItem("Load Crossword XML File");
        loadCWXMLIt.addActionListener(new LoadCrossWordXML());
        JMenuItem saveCWCurrentIt = new JMenuItem("Save Current Crossword Game");
        saveCWCurrentIt.addActionListener(new SaveCurrentGame());
        JMenuItem loadCWCurrentIt = new JMenuItem("Load Current Crossword Game");
        loadCWXMLIt.addActionListener(new LoadCurrentGame());
        JMenuItem exitIt = new JMenuItem("Exit");
        exitIt.setMnemonic('E');
        exitIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(saveCWIt);
        fileMenu.add(loadCWIt);
        fileMenu.addSeparator();
        fileMenu.add(saveCWXMLIt);
        fileMenu.add(loadCWXMLIt);
        fileMenu.addSeparator();
        fileMenu.add(saveCWCurrentIt);
        fileMenu.add(loadCWCurrentIt);
        fileMenu.addSeparator();
        fileMenu.add(exitIt);

        aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic('A');
        JMenuItem creditIt = new JMenuItem("Credit");
        creditIt.setMnemonic('C');
        creditIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane
                        .showMessageDialog(
                                content,
                                "Crossword Application\nAuthor : Truc Nguyen Lam\nVersion : 0.1 alpha",
                                "Credits", JOptionPane.PLAIN_MESSAGE);
            }
        });

        aboutMenu.add(creditIt);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        content.add(menuBar, BorderLayout.NORTH);

        mainScreen = new JPanel();
        Border etchedBorder1 = BorderFactory
                .createEtchedBorder(EtchedBorder.RAISED);
        Border etchedBorder2 = BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED);
        mainScreen.setBorder(BorderFactory.createTitledBorder(etchedBorder1,
                " " + this.crossword.title + " ", TitledBorder.TOP,
                TitledBorder.CENTER));
        mainScreen.add(mainGridPanel);
        content.add(mainScreen);

        JPanel cluePanel = new JPanel();
        cluePanel.setBorder(BorderFactory.createTitledBorder(etchedBorder1,
                " Clues ", TitledBorder.TOP, TitledBorder.CENTER));
        clueView = new JScrollPane();
        clueView.setAutoscrolls(true);
        clueView.setPreferredSize(new Dimension(260, 500));

        cluePanel.add(clueView);
        content.add(cluePanel, BorderLayout.WEST);

        anagramPanel.setBorder(BorderFactory.createTitledBorder(etchedBorder1,
                " Anagram Support ", TitledBorder.TOP, TitledBorder.CENTER));
        content.add(anagramPanel, BorderLayout.EAST);

        JPanel controlPanel = new JPanel(new GridLayout(1, 3));
        JPanel savePanel = new JPanel(new GridLayout(3, 2));
        savePanel.setBorder(BorderFactory.createTitledBorder(etchedBorder2,
                " LOAD/SAVE ", TitledBorder.TOP, TitledBorder.CENTER));
        loadCW = new JButton("Load Crossword");
        loadCW.addActionListener(new LoadCrossWord());
        saveCW = new JButton("Save Crossword");
        saveCW.addActionListener(new SaveCrossWord());
        loadCWXML = new JButton("Load Crossword XML");
        loadCWXML.addActionListener(new LoadCrossWordXML());
        saveCWXML = new JButton("Save Crossword XML");
        saveCWXML.addActionListener(new SaveCrossWordXML());
        loadCurrent = new JButton("Load Current Game");
        loadCurrent.addActionListener(new LoadCurrentGame());
        saveCurrent = new JButton("Save Current Game");
        saveCurrent.addActionListener(new SaveCurrentGame());
        savePanel.add(loadCW);
        savePanel.add(saveCW);
        savePanel.add(loadCWXML);
        savePanel.add(saveCWXML);
        savePanel.add(loadCurrent);
        savePanel.add(saveCurrent);

        JPanel userPanel = new JPanel(new GridLayout(3, 1));
        userPanel.setBorder(BorderFactory.createTitledBorder(etchedBorder2,
                " USER PANEL ", TitledBorder.TOP, TitledBorder.CENTER));
        JPanel userPanel_1 = new JPanel(new GridLayout(1, 3));
        JLabel label_1 = new JLabel("Username:");
        usernameTextField = new JTextField(10);
        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());
        userPanel_1.add(label_1);
        userPanel_1.add(usernameTextField);
        userPanel_1.add(playButton);
        JPanel userPanel_2 = new JPanel(new GridLayout(1, 3));
        JLabel label_2 = new JLabel("Current User: ");
        currentUserLabel = new JLabel("Guest");
        checkAllButton = new JButton("Check All");
        checkAllButton.setEnabled(false);
        checkAllButton.addActionListener(new CheckAllSolutions());
        userPanel_2.add(label_2);
        userPanel_2.add(currentUserLabel);
        userPanel_2.add(checkAllButton);
        JPanel userPanel_3 = new JPanel();
        clearLogButton = new JButton("Clear Log Area");
        clearLogButton.addActionListener(new ClearLogListener());
        solveClueSupportButton = new JCheckBox("Solved Clue Support");
        solveClueSupportButton.addActionListener(new SolvedClueSupportButton());
        userPanel_3.add(solveClueSupportButton);
        userPanel_3.add(clearLogButton);
        userPanel.add(userPanel_1);
        userPanel.add(userPanel_2);
        userPanel.add(userPanel_3);
        JPanel logPanel = new JPanel(new GridLayout(1, 1));
        logPanel.setBorder(BorderFactory.createTitledBorder(etchedBorder2,
                " LOG ", TitledBorder.TOP, TitledBorder.CENTER));
        logView = new JScrollPane();
        logView.setAutoscrolls(true);

        logPanel.add(logView);
        controlPanel.add(savePanel);
        controlPanel.add(userPanel);
        controlPanel.add(logPanel);
        content.add(controlPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public void initGame() {
        mainGridPanel.buildGrid();
        clueView.getViewport().setView(mainGridPanel.getJClueList());
        logView.getViewport().setView(mainGridPanel.getLogArea());
    }

    public void saveCurrentGame() {
        File newFile = null;
        ObjectOutputStream output = null;
        JFileChooser saveFile = new JFileChooser();
        saveFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveFile.addChoosableFileFilter(fileFilter);
        saveFile.setAcceptAllFileFilterUsed(false);
        int returnVal = saveFile.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            newFile = saveFile.getSelectedFile();
            if (!fileFilter.accept(newFile)) {
                newFile = new File(newFile.getAbsolutePath() + ".cwx");
            }
            if (newFile.exists()) {
                int actionDialog = JOptionPane.showConfirmDialog(null,
                        "Replace existing file?");
                if (actionDialog == JOptionPane.NO_OPTION)
                    return;
                if (actionDialog == JOptionPane.CANCEL_OPTION)
                    return;
            }
        }
        try {
            if (newFile != null) {
                output = new ObjectOutputStream(new FileOutputStream(newFile));
                output.writeObject(mainGridPanel);
                output.flush();
                output.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCurrentGame() {
        File newFile = null;
        ObjectInputStream input = null;
        JFileChooser loadFile = new JFileChooser();
        loadFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadFile.addChoosableFileFilter(fileFilter);
        loadFile.setAcceptAllFileFilterUsed(false);
        int returnVal = loadFile.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            newFile = loadFile.getSelectedFile();
        if (newFile == null) {
            return;
        } else {
            try {
                input = new ObjectInputStream(new FileInputStream(newFile));
                mainGridPanel = (CrosswordControl) input.readObject();
                input.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            mainScreen.removeAll();
            Border etchedBorder1 = BorderFactory
                    .createEtchedBorder(EtchedBorder.RAISED);
            mainScreen.setBorder(BorderFactory.createTitledBorder(
                    etchedBorder1, " " + mainGridPanel.getCrossword().title
                            + " ", TitledBorder.TOP, TitledBorder.CENTER));
            mainScreen.add(mainGridPanel);
            mainGridPanel.addListener();
            clueView.getViewport().setView(mainGridPanel.getJClueList());
            logView.getViewport().setView(mainGridPanel.getLogArea());
            currentUserLabel.setText(mainGridPanel.getCurrentUser());
            if (mainGridPanel.getIsSolvedClueSupport())
                solveClueSupportButton.setSelected(true);
            validate();
        }
    }

    class PlayButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            currentUserLabel.setText(usernameTextField.getText());
            validate();
            mainGridPanel.setCurrentUser(usernameTextField.getText());
        }
    }

    class SolvedClueSupportButton implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            if (solveClueSupportButton.isSelected()) {
                mainGridPanel.enableSolvedClueSupport();
                checkAllButton.setEnabled(true);
            } else {
                mainGridPanel.disableSolvedClueSupport();
                checkAllButton.setEnabled(false);
            }
        }
    }

    class CheckAllSolutions implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            mainGridPanel.checkAllSolution();
        }
    }

    class LoadCrossWord implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            crossword = CrosswordIO.readPuzzle();
            if (crossword != null) {
                mainGridPanel = new CrosswordControl(crossword);
                mainScreen.removeAll();
                Border etchedBorder1 = BorderFactory
                        .createEtchedBorder(EtchedBorder.RAISED);
                mainScreen.setBorder(BorderFactory.createTitledBorder(
                        etchedBorder1, " " + crossword.title + " ",
                        TitledBorder.TOP, TitledBorder.CENTER));
                mainScreen.add(mainGridPanel);
                validate();
                initGame();
                solveClueSupportButton.setSelected(false);
            }
        }
    }

    class SaveCrossWord implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            CrosswordIO.writePuzzle(crossword);
        }
    }

    class LoadCrossWordXML implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            crossword = CrosswordIO.readXMLPuzzle();
            if (crossword != null) {
                mainGridPanel = new CrosswordControl(crossword);
                mainScreen.removeAll();
                Border etchedBorder1 = BorderFactory
                        .createEtchedBorder(EtchedBorder.RAISED);
                mainScreen.setBorder(BorderFactory.createTitledBorder(
                        etchedBorder1, " " + crossword.title + " ",
                        TitledBorder.TOP, TitledBorder.CENTER));
                mainScreen.add(mainGridPanel);
                validate();
                initGame();
                solveClueSupportButton.setSelected(false);
            }
        }
    }

    class SaveCrossWordXML implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            CrosswordIO.writeXMLPuzzle(crossword);
        }
    }

    class LoadCurrentGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            loadCurrentGame();
        }
    }

    class SaveCurrentGame implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            saveCurrentGame();
        }
    }

    class ClearLogListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            mainGridPanel.getLogArea().setText("");
        }
    }
}
