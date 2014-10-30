import java.io.File;
import java.io.InputStream;
import static java.lang.Integer.parseInt;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kyle collaborate with Tyler Scott
 */
public class XmlParser implements Runnable {

    private LinkedList<ScriptureReference> sr;
    private String fileName;
    private Updater updater;

    @Override
    public void run() {

    }

    XmlParser(String fileName, Updater updater) {
        this.fileName = fileName;
        this.updater = updater;
    }

    /*reads the xml and builds a linked list of scripture references*/
    public void XmlReader() {
        sr = new LinkedList<>();
        try {
            File fXmlFile;

            /*opens the file*/
            if (fileName.equals("")) {
                String temp = "Journal.properties";
                Properties prop = new Properties();
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(temp);
                prop.load(inputStream);

                fXmlFile = new File(prop.getProperty("XMLFile")); //as defined in the properties file
                fileName = prop.getProperty("XMLFile");
            } else {
                fXmlFile = new File(fileName);
            }

            System.out.println("Loading file: \"" + fileName + "\"...");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("entry");
            //counters
            int entryCount = 0;
            int scriptureCount = 0;
            int topicCount = 0;
            for (int i = 0; i < nList.getLength(); i++) {
                ArrayList<Scripture> scriptures = new ArrayList<>();
                ArrayList<String> topics = new ArrayList<>();
                String date;
                String content;

                Node nNode = nList.item(i);
                Thread.sleep(1000);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    date = eElement.getAttribute("date");
                    for (int j = 0; j < eElement.getElementsByTagName("scripture").getLength(); j++) {
                        scriptureCount++;
                        String book = "";
                        String chapter = "";
                        String startverse = "";
                        String endverse = "";
                        Scripture tempScripture;

                        /*trust me it works*/
                        for (int k = 0; eElement.getElementsByTagName("scripture").item(j).getAttributes().item(k) != null; k++) {
                            //if k == 3
                            if (k == 0) {
                                book = eElement.getElementsByTagName("scripture").item(j).getAttributes().item(k).getTextContent();
                            } else if (k == 1) {
                                chapter = eElement.getElementsByTagName("scripture").item(j).getAttributes().item(k).getTextContent();
                            } else if (k == 2) {
                                endverse = eElement.getElementsByTagName("scripture").item(j).getAttributes().item(k).getTextContent();
                            } else if (k == 3) {
                                startverse = eElement.getElementsByTagName("scripture").item(j).getAttributes().item(k).getTextContent();
                            }
                        }

                        //System.out.println(book + " " + chapter + " " + startverse);
                        tempScripture = new Scripture(book, parseInt(chapter), parseInt(startverse), parseInt(endverse));

                        scriptures.add(tempScripture);
                        //System.out.println();
                    }

                    for (int j = 0; j < eElement.getElementsByTagName("topic").getLength(); j++) {
                        topicCount++;
                        topics.add(eElement.getElementsByTagName("topic").item(j).getTextContent());
                    }

                    content = eElement.getElementsByTagName("content").item(0).getTextContent().replaceAll("\\n\\s+", "\n");
                    Calendar tdate;
                    tdate = new GregorianCalendar();
                    String[] dateSplit = date.split("-");
                    tdate.set(Calendar.YEAR, parseInt(dateSplit[0]));
                    tdate.set(Calendar.MONTH, parseInt(dateSplit[1]) - 1);
                    tdate.set(Calendar.DAY_OF_MONTH, parseInt(dateSplit[2]));
                    //Calendar date, ArrayList<Scripture> scriptures, ArrayList<String> topics, String content
                    ScriptureReference tempSR = new ScriptureReference(tdate, scriptures, topics, content);
                    sr.add(tempSR);
                }
                entryCount++;
                updater.update(entryCount, scriptureCount, topicCount);
            }
        } catch (Exception e) {
            System.out.println("XmlReader broke yo");
        }
    }

    /*display to make sure it all works*/
    public void display() {
        RefFinder rf = new RefFinder();
        List<String> readBooks = rf.readBooks();
        List<ArrayList<String>> readTopics = rf.readTopics();
        System.out.println("Scripture References:");
        for (String readBook : readBooks) {
            Boolean flag = true;
            for (ScriptureReference sr1 : sr) {

                if (sr1.checkBook(readBook)) {
                    if (flag) {
                        System.out.println(readBook);
                        flag = false;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println("             " + sdf.format(sr1.getEntryDate().getTime()));
                }
            }
        }

        System.out.println("Topic References:");
        for (ArrayList<String> topic : readTopics) {
            Boolean flag = true;
            for (ScriptureReference sr1 : sr) {

                if (sr1.checkTopic(topic.get(0))) {
                    if (flag) {
                        System.out.println(topic.get(0));
                        flag = false;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println("             " + sdf.format(sr1.getEntryDate().getTime()));
                }
            }
        }
    }

    public LinkedList<ScriptureReference> getSr() {
        return sr;
    }
}
