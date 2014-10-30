import javafx.application.Platform;
import javafx.collections.ObservableList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kyle
 */
public class ItemsUpdater {

    public ObservableList<String> text;

    public void update(String toAdd) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                text.add(toAdd);
            }
        });

    }
}
