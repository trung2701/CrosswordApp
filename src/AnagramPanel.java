import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

class AnagramPanel extends JPanel {
    private static final long serialVersionUID = 7316484074653864825L;
    private JTextField input;
    private JTextArea output;
    private JProgressBar progressBar;
    private AnagramSwingWorker worker;

    public AnagramPanel() {
        setLayout(new BorderLayout());
        input = new JTextField(10);
        output = new JTextArea();
        output.setEditable(false);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        JPanel inputPanel = new JPanel();
        JScrollPane sp = new JScrollPane(output);
        JButton anagram = new JButton("Generate");
        anagram.addActionListener(new anagramListener());
        inputPanel.add(input);
        inputPanel.add(anagram);
        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        JPanel buttonPanel = new JPanel();
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new clearAnagram());
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new stopAnagram());
        buttonPanel.add(clearButton);
        buttonPanel.add(stopButton);
        controlPanel.add(buttonPanel);
        controlPanel.add(progressBar);
        add(inputPanel, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    class AnagramSwingWorker extends SwingWorker<String, String> {
        private String s;
        private int size, count;

        public AnagramSwingWorker(String s) {
            this.s = s;
            this.size = permutation(this.s.length());
            this.count = 0;
        }

        public String doInBackground() {
            output.append("Start generating anagrams\n");
            permutation(s, "");
            return "";
        }

        private void permutation(String input, String des) {
            if (input.equals("")) {
                publish(des + "\n");
                setProgress(100 * count / size);
            }
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (input.indexOf(c, i + 1) != -1)
                    continue;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                if (isCancelled()) {
                    return;
                }
                permutation(input.substring(0, i) + input.substring(i + 1), des
                        + c);
            }
            count++;
        }

        private int permutation(int n) {
            if (n == 0 || n == 1) {
                return 1;
            }
            int result = 1;
            for (int i = 1; i <= n; i++) {
                result *= i;
            }
            return result;
        }

        protected void process(List<String> cs) {
            for (String c : cs) {
                output.append(c);
            }
        }

        protected void done() {
            if (!isCancelled()) {
                setProgress(100);
                output.append("Finish generating anagrams\n");
            }
        }

    }

    class anagramListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            final String s;
            s = input.getText();
            worker = new AnagramSwingWorker(s);
            worker.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equals(evt.getPropertyName())) {
                        progressBar.setValue((Integer) evt.getNewValue());
                    }
                }
            });
            worker.execute();
        }
    }

    class stopAnagram implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            if (worker != null) {
                worker.cancel(true);
                worker = null;
            }
        }
    }

    class clearAnagram implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            output.setText("");
        }
    }
}
