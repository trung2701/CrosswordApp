import javax.swing.SwingUtilities;

public class CrosswordApp {
    public static void main(String[] args) {
        CrosswordExample ex = new CrosswordExample();
        final Crossword cw = ex.getPuzzle();
        final CrosswordGUI cwGUI = new CrosswordGUI(cw);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                cwGUI.init();
                cwGUI.initGame();
            }
        });
    }
}
