import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kyle
 */
public class TxtSave implements Runnable {

    private ArrayList<ScriptureReference> entryList;
    private String filename;

    @Override
    public void run() {
        try {
            saveTxt(textSave());
        } catch (Exception ex) {
            Logger.getLogger(XMLSave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    TxtSave(ArrayList<ScriptureReference> entryList, String filename) {
        this.entryList = entryList;
        this.filename = filename;
    }

    /*Builds a string to save send to the saveTxt function*/
    public String textSave() {
        String toSave = "";
        for (ScriptureReference entry : entryList) {
            toSave += "-----\n";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            toSave += sdf.format(entry.getEntryDate().getTime());
            toSave += "\n";

            // add the content to the file
            toSave += entry.getContent();
            toSave += "\n";
        }
        return toSave;
    }

    /*writes a string to file*/
    public void saveTxt(String toSave) {
        System.out.println("Saving document: " + filename);
        try {
            PrintWriter out = new PrintWriter(filename);
            out.print(toSave);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TxtSave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
