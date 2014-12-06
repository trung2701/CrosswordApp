import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

class CrosswordIO {
    private static FileNameExtensionFilter
            fileFilter1 = new FileNameExtensionFilter("Save crossword file (*.cw)", "cw"),
            fileFilter2 = new FileNameExtensionFilter("Save crossword xml file (*.xml)", "xml");

    public static Crossword readPuzzle() {
        File newFile = null;
        ObjectInputStream input = null;
        JFileChooser loadFile = new JFileChooser();
        Crossword cw = null;
        loadFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadFile.addChoosableFileFilter(fileFilter1);
        loadFile.setAcceptAllFileFilterUsed(false);
        int returnVal = loadFile.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            newFile = loadFile.getSelectedFile();
        if (newFile == null) {
            return null;
        }
        try {
            input = new ObjectInputStream(new FileInputStream(newFile));
            cw = (Crossword) input.readObject();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cw;
    }

    public static void writePuzzle(Crossword cw) {
        File newFile = null;
        ObjectOutputStream output = null;
        JFileChooser saveFile = new JFileChooser();
        saveFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveFile.addChoosableFileFilter(fileFilter1);
        saveFile.setAcceptAllFileFilterUsed(false);
        int returnVal = saveFile.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            newFile = saveFile.getSelectedFile();
            if (!fileFilter1.accept(newFile)) {
                newFile = new File(newFile.getAbsolutePath() + ".cw");
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
                output.writeObject(cw);
                output.flush();
                output.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Crossword readXMLPuzzle() {
        String title = null;
        int size = 0;
        ArrayList<Clue> acrossClues = new ArrayList<Clue>();
        ArrayList<Clue> downClues = new ArrayList<Clue>();

        File newFile = null;
        JFileChooser loadFile = new JFileChooser();
        loadFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadFile.addChoosableFileFilter(fileFilter2);
        loadFile.setAcceptAllFileFilterUsed(false);
        int returnVal = loadFile.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            newFile = loadFile.getSelectedFile();
        if (newFile == null) {
            return null;
        } else {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document dom = null;

            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                dom = db.parse(newFile);
            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (DOMException se) {
                se.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            Element rootEle = dom.getDocumentElement();
            NodeList titleList = rootEle.getElementsByTagName("Title");
            if (titleList != null && titleList.getLength() > 0) {
                Element titleEle = (Element) titleList.item(0);
                title = titleEle.getFirstChild().getNodeValue();
            }

            NodeList sizeList = rootEle.getElementsByTagName("Size");
            if (sizeList != null && sizeList.getLength() > 0) {
                Element sizeEle = (Element) sizeList.item(0);
                size = Integer.parseInt(sizeEle.getFirstChild().getNodeValue());
            }

            NodeList acrossClueL = rootEle.getElementsByTagName("Across_clues");
            if (acrossClueL != null && acrossClueL.getLength() > 0) {
                Element acrossCluesEle = (Element) acrossClueL.item(0);
                NodeList acrossClueList = acrossCluesEle
                        .getElementsByTagName("Clue_Element");
                if (acrossClueList != null && acrossClueList.getLength() > 0) {
                    for (int i = 0; i < acrossClueList.getLength(); i++) {
                        Element clueEle = (Element) acrossClueList.item(i);
                        Clue clue = getClue(clueEle);
                        acrossClues.add(clue);
                    }
                }
            }

            NodeList downClueL = rootEle.getElementsByTagName("Down_clues");
            if (downClueL != null && downClueL.getLength() > 0) {
                Element downCluesEle = (Element) downClueL.item(0);
                NodeList downClueList = downCluesEle
                        .getElementsByTagName("Clue_Element");
                if (downClueList != null && downClueList.getLength() > 0) {
                    for (int i = 0; i < downClueList.getLength(); i++) {
                        Element clueEle = (Element) downClueList.item(i);
                        Clue clue = getClue(clueEle);
                        downClues.add(clue);
                    }
                }
            }
            return new Crossword(title, size, acrossClues, downClues);
        }
    }

    private static Clue getClue(Element clueEle) {
        int number = getIntValue(clueEle, "Number");
        int x = getIntValue(clueEle, "X_coordinate");
        int y = getIntValue(clueEle, "Y_coordinate");
        String clue = getTextValue(clueEle, "Clue");
        String answer = getTextValue(clueEle, "Answer");

        Clue c = new Clue(number, x, y, clue, answer);
        return c;
    }

    private static String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        return textVal;
    }

    private static int getIntValue(Element ele, String tagName) {
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    public static void writeXMLPuzzle(Crossword cw) {
        File newFile = null;
        JFileChooser saveFile = new JFileChooser();
        saveFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveFile.addChoosableFileFilter(fileFilter2);
        saveFile.setAcceptAllFileFilterUsed(false);
        int returnVal = saveFile.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            newFile = saveFile.getSelectedFile();
            if (!fileFilter1.accept(newFile)) {
                newFile = new File(newFile.getAbsolutePath() + ".xml");
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

        if (newFile != null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document dom;
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                dom = db.newDocument();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                return;
            }

            Element rootEle = dom.createElement("Crossword");
            dom.appendChild(rootEle);
            Element titileEle = dom.createElement("Title");
            titileEle.setAttribute("Nil", "123");
            titileEle.setAttribute("Nilas", "123");
            Text titleText = dom.createTextNode(cw.title);
            titileEle.appendChild(titleText);
            rootEle.appendChild(titileEle);
            Element sizeElement = dom.createElement("Size");
            Text sizeText = dom.createTextNode(String.valueOf(cw.size));
            sizeElement.appendChild(sizeText);
            rootEle.appendChild(sizeElement);
            Element acrossClues = dom.createElement("Across_clues");
            for (Clue c : cw.acrossClues) {
                Element clue = createClueElement(c, dom);
                acrossClues.appendChild(clue);
            }
            rootEle.appendChild(acrossClues);

            Element downClues = dom.createElement("Down_clues");
            for (Clue c : cw.downClues) {
                Element clue = createClueElement(c, dom);
                downClues.appendChild(clue);
            }
            rootEle.appendChild(downClues);

            Source source = new DOMSource(dom);
            Result result = new StreamResult(newFile);
            Transformer xformer;
            try {
                xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(source, result);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerFactoryConfigurationError e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        } else
            return;
    }

    private static Element createClueElement(Clue clue, Document dom) {
        Element clueEle = dom.createElement("Clue_Element");

        Element numberEle = dom.createElement("Number");
        Text numberText = dom.createTextNode(String.valueOf(clue.number));
        numberEle.appendChild(numberText);
        clueEle.appendChild(numberEle);

        Element xEle = dom.createElement("X_coordinate");
        Text xText = dom.createTextNode(String.valueOf(clue.x));
        xEle.appendChild(xText);
        clueEle.appendChild(xEle);

        Element yEle = dom.createElement("Y_coordinate");
        Text yText = dom.createTextNode(String.valueOf(clue.y));
        yEle.appendChild(yText);
        clueEle.appendChild(yEle);

        Element clueSEle = dom.createElement("Clue");
        Text clueSText = dom.createTextNode(clue.clue);
        clueSEle.appendChild(clueSText);
        clueEle.appendChild(clueSEle);

        Element answerEle = dom.createElement("Answer");
        Text answerText = dom.createTextNode(clue.answer);
        answerEle.appendChild(answerText);
        clueEle.appendChild(answerEle);

        return clueEle;
    }
}
