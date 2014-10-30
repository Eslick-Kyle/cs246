import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kyle Eslick
 */
public class RefFinder implements Runnable {

    private ArrayList<ScriptureReference> entryList;
    private String filename;
    private Updater updater;

    @Override
    public void run() {

    }

    RefFinder(String filename, Updater updater) {
        this.filename = filename;
        this.updater = updater;
    }

    RefFinder() {

    }

    /*reads the file provided and pulls out scriptural references*/
    public ArrayList<ScriptureReference> readFile() {
        System.out.println("Reading from file: " + filename);
        entryList = new ArrayList<>();

        List<String> books = readBooks();
        List<ArrayList<String>> topics = readTopics();

        //checks to make sure that the filename provided is not empty and prompts for a new filename if so
        if (filename.equals("")) {
            Scanner cin = new Scanner(System.in);
            System.out.print("No filename was entered please enter a filename: ");
            filename = cin.nextLine();
        }
        int entryCount = 0;
        int scriptureCount = 0;
        int topicCount = 0;
        String text; //to save each line of text
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {

            while (in.ready()) { //goes till end of file
                text = in.readLine();
                ArrayList<String> reference = new ArrayList<>();
                ArrayList<String> topicList = new ArrayList<>();
                String content = "";
                String date = "";

                if (text.equals("-----")) { //new entry is found
                    text = in.readLine();
                }
                while (!text.equals("-----")) { //goes till end of entry

                    if (text.matches("\\d{4}-\\d{2}-\\d{2}")) { //finds dates
                        date = text;
                    }

                    for (String book : books) {
                        //for each book in the scrpitures check the text to see if it has a scripture refference
                        String regx = book + "( chapter [0-9]{1,}) |" + book + " ([0-9]{1,}+:[0-9]{1,}+(-[0-9]{1,})*)";
                        reference.addAll(regexChecker(regx, text));
                    }

                    for (ArrayList<String> topic : topics) { //gets all the topics
                        for (String t : topic) {
                            String regx = t;
                            topicList.addAll(regexChecker(regx, text));
                        }
                    }

                    if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) { //finds dates
                        content += text.trim() + " ";
                    }
                    if (in.ready()) {
                        text = in.readLine();
                    } else {
                        break;
                    }
                }

                //convert the strings to actual scripture references
                ArrayList<Scripture> scriptureList = new ArrayList<>();
                for (String ref : reference) {
                    scriptureList.add(new Scripture(ref));
                    scriptureCount++;
                }

                //if the date is empty there are no more to add
                if (!date.equals("") && !content.equals("")) {
                    ScriptureReference temp = new ScriptureReference(date, scriptureList, topicList, content.trim());
                    entryList.add(temp);
                    entryCount++;
                    topicCount += temp.getTopics().size();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RefFinder.class.getName()).log(Level.SEVERE, null, ex);
                }
                updater.update(entryCount, scriptureCount, topicCount);

            }

            in.close();
        } catch (IOException e) {
            //if opening the file fails this is the error
            System.out.println("Error opening file: " + filename);
            System.exit(0);
        }

        return entryList;
    }

    /*Used to find the scriptures and topics in a string*/
    public List<ArrayList<String>> parceEntry(String toCheck) {

        List<ArrayList<String>> toReturn = new ArrayList<>();
        List<String> books = readBooks();
        List<ArrayList<String>> topics = readTopics();
        toReturn.add(new ArrayList<>());
        toReturn.add(new ArrayList<>());

        for (String book : books) {
            //for each book in the scrpitures check the text to see if it has a scripture refference
            String regx = book + "( chapter [0-9]{1,}) |" + book + " ([0-9]{1,}+:[0-9]{1,}+(-[0-9]{1,})*)";
            toReturn.get(0).addAll(regexChecker(regx, toCheck));
        }

        for (ArrayList<String> topic : topics) {
            for (String t : topic) {
                String regx = t;
                toReturn.get(1).addAll(regexChecker(regx, toCheck));
            }
        }

        return toReturn;
    }

    /*Checks the regular expression agains the string and pritns out if it found something and what it is*/
    public static ArrayList<String> regexChecker(String regx, String toCheck) {
        ArrayList<String> references = new ArrayList<>();
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(toCheck);

        while (matcher.find()) {
            references.add(matcher.group());
        }
        return references;
    }

    /*This will read the list of topics and return the Map of all the topics
     with the key as the topic name and all the terms as a list for the value*/
    public Map<String, List<String>> loadTopics() {
        Map<String, List<String>> topicMap = new HashMap();

        try {
            String temp = "Journal.properties";
            Properties prop = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(temp);
            prop.load(inputStream);
            BufferedReader fin = new BufferedReader(new FileReader(prop.getProperty("Topics")));
            temp = "";

            while (fin.ready()) {
                temp = fin.readLine();
                String[] topics = temp.split(":");
                String topic = topics[0];
                String[] terms = topics[1].split(",");
                List<String> topicList = new ArrayList<>();
                topicList.addAll(Arrays.asList(terms));
                topicMap.put(topic, topicList);
            }
        } catch (IOException e) {
            System.out.println("loadTopics broke yo");
        }
        return topicMap;

    }

    /*gets the books with the chapters from file*/
    public Map<String, Integer> readBooksAndChapters() {
        Map<String, Integer> books = new HashMap<>();
        try {
            String temp = "Journal.properties";
            Properties prop = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(temp);
            prop.load(inputStream);
            BufferedReader fin = new BufferedReader(new FileReader(prop.getProperty("BooksAndChapters")));
            temp = "";
            while (fin.ready()) {
                temp = fin.readLine();
                String[] bookChap = temp.split(":");
                books.put(bookChap[0], Integer.parseInt(bookChap[1]));
            }
        } catch (IOException e) {
            System.out.println("readBooks broke yo");
        }
        return books;

    }

    /*Gets just the books from file*/
    public List<String> readBooks() {
        List<String> books = new ArrayList<>();
        try {
            String temp = "Journal.properties";
            Properties prop = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(temp);
            prop.load(inputStream);
            BufferedReader fin = new BufferedReader(new FileReader(prop.getProperty("Books")));
            temp = "";
            while (fin.ready()) {
                temp = fin.readLine();
                books.add(temp);
            }
        } catch (IOException e) {
            System.out.println("readBooks broke yo");
        }
        return books;

    }

    /*gets the topics with their key words from file*/
    public List<ArrayList<String>> readTopics() {
        List<ArrayList<String>> topics = new ArrayList<>();
        try {
            String temp = "Journal.properties";
            Properties prop = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(temp);
            prop.load(inputStream);
            BufferedReader fin = new BufferedReader(new FileReader(prop.getProperty("Topics")));
            temp = "";
            while (fin.ready()) {
                temp = fin.readLine();
                String[] temp2 = temp.split(":|,");
                ArrayList<String> temp3 = new ArrayList<>();
                temp3.addAll(Arrays.asList(temp2));
                topics.add(temp3);
            }
        } catch (IOException e) {
            System.out.println("readBooks broke yo");
        }
        return topics;
    }

    public ArrayList<ScriptureReference> getEntryList() {
        return entryList;
    }

    public void setEntryList(ArrayList<ScriptureReference> entryList) {
        this.entryList = entryList;
    }

}
