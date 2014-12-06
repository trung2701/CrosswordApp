import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

class Cell extends JPanel {
    private static final long serialVersionUID = 8905401206161904180L;
    private int xPosition, yPosition;
    private String characterDisplay, clueDisplay;
    private boolean isSolid;
    private Clue wordAcross, wordDown;
    private int characterIndexAcross, characterIndexDown;
    private boolean directionSwitch;
    private String trueValue;
    private int toDraw;

    public Cell(int xPosition, int yPosition) {
        super();
        isSolid = true;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.directionSwitch = false;
        isSolid = true;
        clueDisplay = " ";
        characterDisplay = " ";
        toDraw = 0;
    }

    public void paint(Graphics g) {
        super.paint(g);
        switch (toDraw) {
            case 0:// Draw solid cell
                g.setColor(Color.BLACK);
                break;
            case 1:// Draw default background
                g.setColor(Color.LIGHT_GRAY);
                break;
            case 2:// Draw selected cell
                g.setColor(Color.ORANGE);
                break;
            case 3:// Draw related to selected cell
                g.setColor(Color.YELLOW);
                break;
            case 4:// Draw true cell
                g.setColor(Color.BLUE);
                break;
            case 5:// Draw false cell
                g.setColor(Color.RED);
                break;
        }
        g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), true);
        g.setXORMode(getBackground());
        if (toDraw != 0) {
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, this.getWidth() / 2));
            g.drawString(characterDisplay, this.getWidth() / 3,
                    2 * this.getWidth() / 3);
            g.setFont(new Font("Time New Roman", Font.BOLD, this.getWidth() / 5));
            g.drawString(clueDisplay, this.getWidth() / 8, this.getHeight() / 4);
        }
    }

    public void setClueNumber(int i) {
        clueDisplay = String.valueOf(i);
        repaint();
    }

    public void setCharacter(String s) {
        characterDisplay = s.toUpperCase();
        repaint();
    }

    public void setCellBackground(int i) {
        toDraw = i;
        repaint();
    }

    public void setWord(Clue w) {
        if (w.getDirection() == Clue.ACROSS)
            wordAcross = w;
        else if (w.getDirection() == Clue.DOWN)
            wordDown = w;
    }

    public Clue getNextWord() {
        if (directionSwitch) {
            directionSwitch = false;
            if (wordAcross != null)
                return wordAcross;
            else
                return wordDown;
        } else {
            directionSwitch = true;
            if (wordDown != null)
                return wordDown;
            else
                return wordAcross;
        }
    }

    public Clue getWordAcross() {
        return wordAcross;
    }

    public Clue getWordDown() {
        return wordDown;
    }

    public String getCharacter() {
        return characterDisplay;
    }

    public void setCharacterIndexAcross(int i) {
        this.characterIndexAcross = i;
    }

    public int getCharacterIndexAcross() {
        return this.characterIndexAcross;
    }

    public void setCharacterIndexDown(int i) {
        this.characterIndexDown = i;
    }

    public int getCharacterIndexDown() {
        return this.characterIndexDown;
    }

    public void setSolid(boolean isSolid) {
        this.isSolid = isSolid;
    }

    public boolean isSolid() {
        return this.isSolid;
    }

    public int getxPosition() {
        return this.xPosition;
    }

    public int getyPosition() {
        return this.yPosition;
    }

    public void setTrueValue() {
        if (wordAcross != null) {
            trueValue = String.valueOf(wordAcross.answer
                    .charAt(characterIndexAcross));
        }
        if (wordDown != null) {
            trueValue = String.valueOf(wordDown.answer
                    .charAt(characterIndexDown));
        }
    }

    public String getTrueValue() {
        return trueValue.toUpperCase();
    }

    public boolean equals(Cell other) {
        if (xPosition == other.xPosition && yPosition == other.yPosition)
            return true;
        else
            return false;
    }
}
