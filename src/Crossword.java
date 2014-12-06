import java.io.Serializable;
import java.util.ArrayList;

class Crossword implements Serializable {
    private static final long serialVersionUID = 555768116023483762L;
    final ArrayList<Clue> acrossClues, downClues;
    final String title;
    final int size;

    Crossword(String title, int size, ArrayList<Clue> acrossClues,
              ArrayList<Clue> downClues) {
        this.title = title;
        this.size = size;
        this.acrossClues = acrossClues;
        this.downClues = downClues;
    }
}

class Clue implements Serializable {
    private static final long serialVersionUID = -8337075140858786452L;
    final int number, x, y;
    final String clue, answer;

    public final static int ACROSS = 0;
    public final static int DOWN = 1;
    private int direction;
    private boolean isSolved = false;
    private ArrayList<String> userNames = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();

    Clue(int number, int x, int y, String clue, String answer) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.clue = clue;
        this.answer = answer;
    }

    public String getFormat() {
        StringBuffer format = new StringBuffer();
        if (answer.indexOf(" ") == -1 && answer.indexOf("-") == -1)
            format.append(answer.length());
        if (answer.indexOf(" ") != -1) {
            String[] c = answer.split(" ");
            for (String e : c) {
                if (!e.equals(c[c.length - 1])) {
                    format.append(e.length());
                    format.append(",");
                } else
                    format.append(e.length());
            }
        }
        if (answer.indexOf("-") != -1) {
            String[] c = answer.split("-");
            for (String e : c) {
                if (!e.equals(c[c.length - 1])) {
                    format.append(e.length());
                    format.append("-");
                } else
                    format.append(e.length());
            }
        }
        return format.toString();
    }

    public void addRecord(String user, String date) {
        userNames.add(user);
        dates.add(date);
    }

    public ArrayList<String> getRecords() {
        ArrayList<String> record = new ArrayList<String>();
        if (!userNames.isEmpty()) {
            String s = null;
            for (int i = 0; i < userNames.size(); i++) {
                s = userNames.get(i) + " at " + dates.get(i);
                record.add(s);
            }
        }
        return record;
    }

    public int getLength() {
        return this.answer.length();
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setSolved() {
        this.isSolved = true;
    }

    public boolean isSolved() {
        return isSolved;
    }
}