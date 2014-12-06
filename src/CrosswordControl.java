import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class CrosswordControl extends JPanel {
    private static final long serialVersionUID = 4196404821812993244L;
    private Crossword cw;
    private Cell selectedCell;
    private Clue selectedClue;
    private int size;
    private ArrayList<Cell> allCells;
    private ArrayList<Clue> allClues;

    @SuppressWarnings("rawtypes")
    private DefaultListModel jListClues;
    private ListSelectionModel jListSelectionClues;
    @SuppressWarnings("rawtypes")
    private JList list;
    private JTextArea loggingArea;
    private String currentUser = "Guest";
    private boolean isSolvedClueSupport = false;

    public CrosswordControl(Crossword cw) {
        this.cw = cw;
        this.setFocusable(true);
        this.selectedCell = null;
        this.selectedClue = null;
        this.size = cw.size;
        this.setPreferredSize(new Dimension(500, 500));
        this.setLayout(new GridLayout(size, size));
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }

    public void buildGrid() {
        buildWords();
        buildSolidCells();
        buildCharacterCells();
        initClueList();
        initLogArea();
        addListener();
        validate();
    }

    private void buildWords() {
        allClues = new ArrayList<Clue>();
        for (Clue aClue : this.cw.acrossClues) {
            aClue.setDirection(Clue.ACROSS);
            allClues.add(aClue);
        }
        for (Clue dClue : this.cw.downClues) {
            dClue.setDirection(Clue.DOWN);
            allClues.add(dClue);
        }
    }

    private void buildSolidCells() {
        allCells = new ArrayList<Cell>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = new Cell(i, j);
                allCells.add(cell);
                this.add(cell);
            }
        }
    }

    private void buildCharacterCells() {
        for (int i = 0; i < allClues.size(); i++) {
            Clue clue = allClues.get(i);
            if (clue.getDirection() == Clue.ACROSS) {
                for (int j = clue.x, letterIndex = 0; j < (clue.x + clue
                        .getLength()); j++, letterIndex++) {
                    Cell c = findCell(clue.y, j);
                    c.setCellBackground(1);
                    c.setSolid(false);
                    c.setWord(clue);
                    c.setCharacterIndexAcross(letterIndex);
                    if (letterIndex == 0) {
                        c.setClueNumber(clue.number);
                    }
                    c.setTrueValue();
                }
            } else {
                for (int j = clue.y, letterIndex = 0; j < (clue.y + clue
                        .getLength()); j++, letterIndex++) {
                    Cell c = findCell(j, clue.x);
                    c.setCellBackground(1);
                    c.setSolid(false);
                    c.setWord(clue);
                    c.setCharacterIndexDown(letterIndex);
                    if (letterIndex == 0) {
                        c.setClueNumber(clue.number);
                    }
                    c.setTrueValue();
                }
            }
        }
    }

    public void addListener() {
        addKeyListener(new KeyboardControl());
        for (Cell c : allCells) {
            if (!c.isSolid()) {
                c.addMouseListener(new MouseControl());
            }
        }
        jListSelectionClues.addListSelectionListener(new ClueJListListener());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initClueList() {
        jListClues = new DefaultListModel();
        for (Clue clue : allClues) {
            jListClues.addElement("<html>" + String.valueOf(clue.number) + ". "
                    + clue.clue + " (" + clue.getFormat() + ")</html>");
        }
        jListClues.add(0, "ACROSS");
        jListClues.add(cw.acrossClues.size() + 1, "DOWN");
        list = new JList(jListClues);
        jListSelectionClues = list.getSelectionModel();
        jListSelectionClues
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setPreferredSize(new Dimension(260, 500));
    }

    private void initLogArea() {
        loggingArea = new JTextArea();
        loggingArea.setEditable(false);
    }

    @SuppressWarnings("rawtypes")
    public JList getJClueList() {
        return list;
    }

    public JTextArea getLogArea() {
        return loggingArea;
    }

    public Crossword getCrossword() {
        return cw;
    }

    @SuppressWarnings("unchecked")
    private void setStrikeThrough(boolean active) {
        for (int i = 0; i < allClues.size(); i++) {
            if (allClues.get(i).isSolved()) {
                if (active) {
                    if (i < cw.acrossClues.size()) {
                        String s = "<html><s>"
                                + String.valueOf(allClues.get(i).number) + ". "
                                + allClues.get(i).clue + " ("
                                + allClues.get(i).getFormat() + ")</s></html>";
                        jListClues.remove(i + 1);
                        jListClues.add(i + 1, s);
                    } else {
                        String s = "<html><s>"
                                + String.valueOf(allClues.get(i).number) + ". "
                                + allClues.get(i).clue + " ("
                                + allClues.get(i).getFormat() + ")</s></html>";
                        jListClues.remove(i + 2);
                        jListClues.add(i + 2, s);
                    }
                } else {
                    if (i < cw.acrossClues.size()) {
                        String s = "<html>"
                                + String.valueOf(allClues.get(i).number) + ". "
                                + allClues.get(i).clue + " ("
                                + allClues.get(i).getFormat() + ")</html>";
                        jListClues.remove(i + 1);
                        jListClues.add(i + 1, s);
                    } else {
                        String s = "<html>"
                                + String.valueOf(allClues.get(i).number) + ". "
                                + allClues.get(i).clue + " ("
                                + allClues.get(i).getFormat() + ")</html>";
                        jListClues.remove(i + 2);
                        jListClues.add(i + 2, s);
                    }
                }
            }
        }
    }

    public void enableSolvedClueSupport() {
        isSolvedClueSupport = true;
        for (Clue c : allClues) {
            if (c.isSolved()) {
                for (String s : c.getRecords()) {
                    String s1 = (c.getDirection() == 0) ? "Across " : "Down ";
                    loggingArea.append(s1 + "clue number " + c.number
                            + " has solved by " + s + "\n");
                }
            }
        }
        setStrikeThrough(true);
    }

    public void disableSolvedClueSupport() {
        isSolvedClueSupport = false;
        setStrikeThrough(false);
    }

    private void setSelectedClue(Clue clue) {
        if (selectedClue != null)
            deHighLightSelectedWord();
        selectedClue = clue;
        highLightSelectedWord();
    }

    public ArrayList<Clue> getAllClues() {
        return this.allClues;
    }

    private Cell findCell(int x, int y) {
        for (Cell c : allCells) {
            if (c.getxPosition() == x && c.getyPosition() == y)
                return c;
        }
        return null;
    }

    public void highLightSelectedWord() {
        if (selectedClue.getDirection() == Clue.ACROSS) {
            for (int i = selectedClue.x; i < selectedClue.x
                    + selectedClue.getLength(); i++) {
                Cell c = findCell(selectedClue.y, i);
                if (selectedCell != null && c.equals(selectedCell)) {
                    c.setCellBackground(2);
                } else
                    c.setCellBackground(3);
            }

        } else {
            for (int i = selectedClue.y; i < selectedClue.y
                    + selectedClue.getLength(); i++) {
                Cell c = findCell(i, selectedClue.x);
                if (selectedCell != null && c.equals(selectedCell)) {
                    c.setCellBackground(2);
                } else
                    c.setCellBackground(3);
            }
        }
    }

    public void deHighLightSelectedWord() {
        if (selectedClue.getDirection() == Clue.ACROSS) {
            for (int i = selectedClue.x; i < selectedClue.x
                    + selectedClue.getLength(); i++) {
                Cell c = findCell(selectedClue.y, i);
                c.setCellBackground(1);
            }
        } else {
            for (int i = selectedClue.y; i < selectedClue.y
                    + selectedClue.getLength(); i++) {
                Cell c = findCell(i, selectedClue.x);
                c.setCellBackground(1);
            }
        }
    }

    public boolean checkSolution() {
        boolean isTrue = true;
        if (selectedClue != null) {
            if (selectedClue.getDirection() == Clue.ACROSS) {
                for (int i = selectedClue.x; i < selectedClue.x
                        + selectedClue.getLength(); i++) {
                    Cell c = findCell(selectedClue.y, i);
                    if (!c.getCharacter().equals(c.getTrueValue()))
                        isTrue = false;
                }
            } else {
                for (int i = selectedClue.y; i < selectedClue.y
                        + selectedClue.getLength(); i++) {
                    Cell c = findCell(i, selectedClue.x);
                    if (!c.getCharacter().equals(c.getTrueValue()))
                        isTrue = false;
                }
            }

        }
        return isTrue;
    }

    public void checkAllSolution() {
        for (Cell c : allCells) {
            if (!c.isSolid()) {
                if (c.getCharacter().equals(c.getTrueValue()))
                    c.setCellBackground(4);
                else {
                    c.setCellBackground(5);
                }
            }
        }
    }

    private Cell getNextCell() {
        if (selectedClue.getDirection() == Clue.ACROSS) {
            Cell c = findCell(selectedCell.getxPosition(),
                    selectedCell.getyPosition() + 1);
            if (c != null && c.getWordAcross() != null)
                return c;
        } else {
            Cell c = findCell(selectedCell.getxPosition() + 1,
                    selectedCell.getyPosition());
            if (c != null && c.getWordDown() != null)
                return c;
        }
        return null;
    }

    public String getCurrentUser() {
        return this.currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public boolean getIsSolvedClueSupport() {
        return isSolvedClueSupport;
    }

    class MouseControl implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            if (selectedClue != null) {
                deHighLightSelectedWord();
            }
            selectedCell = (Cell) e.getSource();
            if (!selectedCell.isSolid()) {
                selectedClue = selectedCell.getNextWord();
                highLightSelectedWord();
                requestFocus();
                int i = allClues.indexOf(selectedClue);
                if (i < cw.acrossClues.size()) {
                    list.setSelectedIndex(i + 1);
                } else {
                    list.setSelectedIndex(i + 2);
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }

    class KeyboardControl implements KeyListener {

        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
            if (selectedCell != null && selectedCell.isSolid() != true) {
                String s = "";
                if (e.getKeyChar() == KeyEvent.VK_DELETE
                        || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    s = " ";
                    selectedCell.setCharacter(s);
                } else {
                    s = String.valueOf(e.getKeyChar());
                    selectedCell.setCharacter(s);
                    if (checkSolution()) {
                        String date = DateUtils.now();
                        int i = allClues.indexOf(selectedClue);
                        allClues.get(i).addRecord(currentUser, date);
                        allClues.get(i).setSolved();
                        if (isSolvedClueSupport) {
                            setStrikeThrough(true);
                            String s1 = (selectedClue.getDirection() == 0) ? "Across "
                                    : "Down ";
                            loggingArea.append(s1 + "clue number "
                                    + selectedClue.number + " has solved by "
                                    + currentUser + " at " + date + "\n");
                        }
                    }
                    selectedCell = getNextCell();
                }
            }
        }
    }

    class ClueJListListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            int minIndex = e.getFirstIndex();
            int maxIndex = e.getLastIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    if (i > 0) {
                        if (i <= cw.acrossClues.size()) {
                            setSelectedClue(allClues.get(i - 1));
                        } else if (i != cw.acrossClues.size() + 1) {
                            setSelectedClue(allClues.get(i - 2));
                        }
                    }
                }
            }
        }
    }
}
