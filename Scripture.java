
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kyle
 */
public class Scripture {

    private String book;
    private int chapter;
    private int startVerse;
    private int endVerse;

    Scripture() {
        book = "";
        chapter = 0;
        startVerse = 0;
        endVerse = 0;
    }

    Scripture(String book, int chapter, int startVerse, int endVerse) {
        this.book = book;
        this.chapter = chapter;
        this.startVerse = startVerse;
        this.endVerse = endVerse;
    }

    Scripture(String book, int chapter, int startVerse) {
        this.book = book;
        this.chapter = chapter;
        this.startVerse = startVerse;
        this.endVerse = startVerse;
    }

    Scripture(String everything) {
        book = "";
        chapter = 0;
        startVerse = 0;
        endVerse = 0;
        String[] splits = everything.split(" |:|-");
        if (splits[0].matches("-?\\d+")) {
            splits[0] = splits[0] + " " + splits[1];
            splits[1] = "";
        }

        for (String split : splits) {
            if (split.equals("chapter") || split.equals("")) {
                continue;
            }
            if (this.book.equals("")) {
                this.book = split;
            } else if (this.chapter == 0) {
                this.chapter = Integer.parseInt(split);
            } else if (this.startVerse == 0) {
                this.startVerse = Integer.parseInt(split);
            } else if (this.endVerse == 0) {
                this.endVerse = Integer.parseInt(split);
            }
        }
    }

    /*returns true if the scripture is the same*/
    public Boolean lookupScripture(Scripture toFind) {
        if (toFind.getBook().equals(book) && toFind.getChapter() == chapter && lookupVerse(toFind.getStartVerse(), toFind.getEndVerse())) {
            return true;
        }
        return false;
    }

    /*returns true if the verse is in the range of verses*/
    public Boolean lookupVerse(int sVerse, int eVerse) {
        for (int i = sVerse; sVerse <= eVerse; i++) { //if any verse match return true
            if (i >= startVerse && i <= endVerse) {
                return true;
            }
        }
        return false;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getStartVerse() {
        return startVerse;
    }

    public void setStartVerse(int startVerse) {
        this.startVerse = startVerse;
    }

    public int getEndVerse() {
        return endVerse;
    }

    public void setEndVerse(int endVerse) {
        this.endVerse = endVerse;
    }

}
