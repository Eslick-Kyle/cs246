import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kyle
 */
public class XMLSave implements Runnable {

    private ArrayList<ScriptureReference> entryList;
    private String file;

    @Override
    public void run() {
        try {
            buildXmlDocument();
        } catch (Exception ex) {
            Logger.getLogger(XMLSave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    XMLSave(ArrayList<ScriptureReference> entryList, String file) {
        this.entryList = entryList;
        this.file = file;
    }

    /*builds a Xml doc and sends it to XML save*/
    public void buildXmlDocument() throws Exception {
        System.out.println(entryList);
        System.out.println(file);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = (Element) doc.createElement("journal");
        doc.appendChild(root);

        for (ScriptureReference entry : entryList) {
            Element jEntry = doc.createElement("entry"); //makes and element
            root.appendChild(jEntry);                    //attaches it to the root

            // set attribute to entry element
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Attr attr = doc.createAttribute("date");
            attr.setValue(sdf.format(entry.getEntryDate().getTime()));
            jEntry.setAttributeNode(attr);

            for (Scripture s : entry.getScriptures()) {
                // create scripter elements
                Element script = doc.createElement("scripture");
                jEntry.appendChild(script);

                attr = doc.createAttribute("book");
                attr.setValue(s.getBook());
                script.setAttributeNode(attr);

                attr = doc.createAttribute("chapter");
                attr.setValue(Integer.toString(s.getChapter()));
                script.setAttributeNode(attr);

                attr = doc.createAttribute("startverse");
                attr.setValue(Integer.toString(s.getStartVerse()));
                script.setAttributeNode(attr);

                attr = doc.createAttribute("endverse");
                attr.setValue(Integer.toString(s.getEndVerse()));
                script.setAttributeNode(attr);
            }

            for (String t : entry.getTopics()) {
                // create topic elements
                Element topic = doc.createElement("topic");
                topic.appendChild(doc.createTextNode(t));
                jEntry.appendChild(topic);
            }

            // add the content to the file
            Element content = doc.createElement("content");
            content.appendChild(doc.createTextNode(entry.getContent()));
            jEntry.appendChild(content);
        }
        saveDocument(doc);
    }

    /*saves the doc to file*/
    public void saveDocument(Document doc) throws Exception {
        System.out.println("Saving document: " + file);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(file));

        //Result output = new StreamResult(new File(file));
        //Source input = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        transformer.transform(source, result);
    }
}
