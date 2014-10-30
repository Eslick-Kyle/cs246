import javafx.application.Platform;
import javafx.scene.control.TextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kyle
 */
public class TextFieldUpdater implements Updater{

    public TextArea text;

    public void update(int entryCount, int scriptureCount, int topicCount) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                text.setText("Number of entries found: " + Integer.toString(entryCount) + "\n" + "Number of Scripturs found: " + Integer.toString(scriptureCount) + " \n" + "Number of topics found: " + Integer.toString(topicCount));
            }
        });

    }
}
