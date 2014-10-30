import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Journal extends Application {

    private String args;
    private ArrayList<ScriptureReference> index;

    @Override
    public void start(Stage primaryStage) {
        index = new ArrayList<>(); //saves all the ScriptureReferences from files and entries
        primaryStage.setTitle("Journal");

        BorderPane border = new BorderPane(); //main display grid

        TextArea txtResults = new TextArea();
        txtResults.setPrefSize(200, 100);

        //the list is where the entries are shown in the main window
        ListView<String> list = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        list.setItems(items);
        list.setPrefWidth(200);
        list.setPrefHeight(1000);

        //this is where all the buttons on the top are made
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");

        //open xml button
        Button buttonOpenXml = new Button("Open Xml");
        buttonOpenXml.setPrefSize(100, 20);

        buttonOpenXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileChooser chooser = new FileChooser();
                File file = chooser.showOpenDialog(primaryStage);
                if (file != null) {
                    index.clear();
                    items.clear();

                    // First set up our updater
                    TextFieldUpdater updater = new TextFieldUpdater();
                    txtResults.clear();
                    updater.text = txtResults;
                    ItemsUpdater update = new ItemsUpdater();
                    update.text = items;

                    XmlParser xp = new XmlParser(file.getPath(), updater);

                    new Thread(new Runnable() { //reads file in a new thread and updates the view
                        public void run() {
                            xp.XmlReader();
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    index.addAll(xp.getSr());
                                    for (ScriptureReference i : index) {
                                        update.update(i.getEntryDateString() + "\n" + i.displayContent());
                                    }

                                }
                            });
                        }
                    }).start();
                }
            }
        });

        // save xml button
        Button buttonSaveXml = new Button("Save XML");
        buttonSaveXml.setPrefSize(100, 20);

        buttonSaveXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { //saves file to XML in a new thread
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save XML");
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    XMLSave xmlSave = new XMLSave(index, file.getPath());
                    try {
                        (new Thread(xmlSave)).start();
                    } catch (Exception ex) {
                        Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        //open a text doc button
        Button buttonOpenText = new Button("Open Text");
        buttonOpenText.setPrefSize(100, 20);

        buttonOpenText.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileChooser chooser = new FileChooser();
                File file = chooser.showOpenDialog(primaryStage);
                if (file != null) {
                    index.clear();
                    items.clear();
                    // First set up our updater
                    TextFieldUpdater updater = new TextFieldUpdater();
                    txtResults.clear();
                    updater.text = txtResults;
                    ItemsUpdater update = new ItemsUpdater();
                    update.text = items;

                    RefFinder rf = new RefFinder(file.getPath(), updater); //Opens txt file in a new thread and updates the view
                    new Thread(new Runnable() {
                        public void run() {
                            rf.readFile();
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    index.addAll(rf.getEntryList());
                                    for (ScriptureReference i : index) {
                                        update.update(i.getEntryDateString() + "\n" + i.displayContent());
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        //save a text doc button
        Button buttonSaveText = new Button("Save Text");
        buttonSaveText.setPrefSize(100, 20);

        buttonSaveText.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { //Saves txt file in a new thread
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save XML");
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    TxtSave ts = new TxtSave(index, file.getPath());
                    (new Thread(ts)).start();
                }
            }
        });

        //add entry button
        Button buttonAddEntry = new Button("Add Entry");
        buttonAddEntry.setPrefSize(100, 20);

        buttonAddEntry.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                final Stage dialog = new Stage(); //making a new window
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(primaryStage);

                BorderPane border2 = new BorderPane();

                TextArea ta2 = new TextArea();

                HBox hbox2 = new HBox();
                hbox2.setPadding(new Insets(15, 12, 15, 12));
                hbox2.setSpacing(10);   // Gap between nodes
                hbox2.setStyle("-fx-background-color: #336699;");

                //save entry button
                Button buttonSave = new Button("Save Entry");
                buttonSave.setPrefSize(100, 20);

                buttonSave.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        if (!ta2.getText().isEmpty()) {
                            //make a new reference and save it in the index
                            ScriptureReference tempSR = new ScriptureReference();
                            tempSR.setContent(ta2.getText());
                            String month = "";
                            String day = "";
                            String year = "";
                            GregorianCalendar gregorianCalendar = new GregorianCalendar();
                            month = String.valueOf(gregorianCalendar.get(GregorianCalendar.MONTH));
                            day = String.valueOf(gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH));
                            year = String.valueOf(gregorianCalendar.get(GregorianCalendar.YEAR));
                            tempSR.setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                            RefFinder rf = new RefFinder();
                            List<ArrayList<String>> parceEntry = rf.parceEntry(tempSR.getContent());
                            for (String ref : parceEntry.get(0)) {
                                System.out.println(ref);
                                tempSR.addScripture(new Scripture(ref));
                            }
                            System.out.println(tempSR.getScriptures());
                            tempSR.setTopics(parceEntry.get(1));
                            index.add(tempSR);

                            items.add(tempSR.getEntryDateString() + "\n" + tempSR.displayContent());
                            dialog.close();
                        } else {
                            ta2.requestFocus();
                        }
                    }
                });
                hbox2.getChildren().add(buttonSave);
                border2.setTop(hbox2);
                border2.setCenter(ta2);

                VBox dialogVbox = new VBox(20);
                dialogVbox.getChildren().add(border2);
                Scene dialogScene = new Scene(dialogVbox, 300, 200);
                dialog.setScene(dialogScene);
                dialog.show();
            }
        });

        hbox.getChildren().addAll(buttonOpenXml, buttonSaveXml, buttonOpenText, buttonSaveText, buttonAddEntry);

        //This is the left side search box
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);   // Gap between nodes
        vbox.setStyle("-fx-background-color: #336699;");

        Label searchLb = new Label("Search");
        final TextField searchBox = new TextField();

        Button search = new Button("Search Now");
        buttonOpenXml.setPrefSize(100, 20);

        /*Code for the search box and button*/
        search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (searchBox.getText().isEmpty()) {
                    items.clear();
                    for (ScriptureReference sr : index) {
                        items.add(sr.getEntryDateString() + "\n" + sr.displayContent());
                    }
                } else if (index.size() <= 0) {
                    items.add("There is not a Journal loaded. Please load a Journal or start a new one by adding an entry");
                    searchBox.clear();
                } else {
                    items.clear();
                    String s = searchBox.getText();
                    searchBox.clear();
                    items.add("The following entries contain your search tearm: " + "\"" + s + "\"" + "\n\n\n");
                    for (ScriptureReference sr : index) {
                        if (sr.getEntryDateString().equals(s) //checks search based on date
                                || sr.getContent().toLowerCase().contains(s.toLowerCase())) { //sees if the content contains something
                            items.add(sr.getEntryDateString() + "\n" + sr.displayContent());
                        }
                        for (String topic : sr.getTopics()) {  //checks the topics to see if the search equals any of the topics
                            if (topic.equals(s)) {
                                items.add(sr.getEntryDateString() + "\n" + sr.displayContent());
                            }
                        }
                    }
                    if (items.size() == 1) {
                        items.clear();
                    }
                }
                if (items.isEmpty()) {
                    items.add("There were no results");
                }
            }
        });

        vbox.getChildren().addAll(searchLb, searchBox, search, txtResults);

        //build the border
        border.setTop(hbox);
        border.setCenter(list);
        border.setLeft(vbox);

        Scene scene = new Scene(border, 700, 500);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Journal.class.getResource("login.css").toExternalForm());
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param a
     */
    /*Gets the args from the command line and saves it in a string*/
    public void getArgs(String[] a) {
        if (a.length > 0) {
            for (String arg : a) {
                arg += arg;
            }
        } else {
            args = "";
        }
    }

    /*returns the string args*/
    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}
