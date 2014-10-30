import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kyle
 */
public class ScriptureReference {

    private Calendar entryDate;
    private ArrayList<Scripture> scriptures;
    private ArrayList<String> topics;
    private String content;

    ScriptureReference() {
        entryDate = new GregorianCalendar();
        scriptures = new ArrayList<>();
        topics = new ArrayList<>();
        content = "";
    }

    ScriptureReference(Calendar date, ArrayList<Scripture> scriptures, ArrayList<String> topics, String content) {
        this.entryDate = date;
        this.scriptures = scriptures;
        this.topics = topics;
        this.content = content;
        fixTopics();
    }

    ScriptureReference(String date, ArrayList<Scripture> scriptures, ArrayList<String> topics, String content) {
        String[] dates = date.split("-|:");
        Calendar cDate = new GregorianCalendar();
        cDate.set(Calendar.YEAR, Integer.parseInt(dates[0]));
        cDate.set(Calendar.MONTH, Integer.parseInt(dates[1]));
        cDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[2]));
        this.entryDate = cDate;
        this.scriptures = scriptures;
        this.topics = topics;
        this.content = content;
        fixTopics();
    }

    public void setDate(int year, int month, int day) {

        entryDate = new GregorianCalendar();
        entryDate.set(Calendar.YEAR, year);
        entryDate.set(Calendar.MONTH, month);
        entryDate.set(Calendar.DAY_OF_MONTH, day);
    }

    /*adds a new scripture*/
    public void addScripture(Scripture toAdd) {
        scriptures.add(toAdd);
    }

    /*looks for the book in the list of scriptures*/
    public Boolean checkBook(String book) {
        for (Scripture s : scriptures) {
            if (s.getBook().equals(book)) {
                return true;
            }
        }
        return false;
    }

    /*finds if a topic is in the list of topics*/
    public Boolean checkTopic(String topic) {
        for (String topics2 : topics) {
            if (topics2.toLowerCase().equals(topic.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /*removes duplicate topics and turns key words into topics*/
    public void fixTopics() {
        Map<String, String> topicMap = new HashMap();

        try {
            Properties prop = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Journal.properties");
            prop.load(inputStream);
            BufferedReader fin = new BufferedReader(new FileReader(prop.getProperty("Topics")));
            String temp;

            while (fin.ready()) {
                temp = fin.readLine();
                String[] tTopics = temp.split(":");
                String topic = tTopics[0];
                String[] terms = tTopics[1].split(",");
                for (String t : terms) {
                    topicMap.put(t, topic);
                }

            }
        } catch (IOException e) {
            System.out.println("loadTopics broke yo");
        }
        Collection<String> hTopics = new HashSet<>();
        ArrayList<String> newTopics = new ArrayList<>();
        for (String t : this.topics) {
            String temp = topicMap.get(t);
            hTopics.add(temp);
        }
        for (String t : hTopics) {
            newTopics.add(t);
        }
        this.topics = newTopics;
    }

    public Calendar getEntryDate() {
        return entryDate;
    }

    /*returns a formated string for the date*/
    public String getEntryDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
        return sdf.format(entryDate.getTime());
    }

    public void setEntryDate(Calendar entryDate) {
        this.entryDate = entryDate;
    }

    public ArrayList<Scripture> getScriptures() {
        return scriptures;
    }

    public void setScriptures(ArrayList<Scripture> scriptures) {
        this.scriptures = scriptures;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<String> topics) {
        this.topics = topics;
        fixTopics();
    }

    public String getContent() {
        return content;
    }

    public String displayContent() {
        String[] temp = this.content.split(" ");
        String toReturn = "";
        for (int i = 0; i < temp.length; i++) {
            if (i % 20 != 0 || i == 0) {
                toReturn += temp[i] + " ";
            } else {
                toReturn += temp[i] + "\n";
            }
        }

        return toReturn;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
