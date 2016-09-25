package MangaRipper.Core;

import MangaRipper.DataStructures.Chapter;
import MangaRipper.DataStructures.Page;
import MangaRipper.DataStructures.StringPair;
import MangaRipper.Services.MangaReader;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by oduibhir on 24/09/16.
 */
public class MangaRipper extends JFrame {

    static MangaRipper mangaRipper;
    ApplicationPanel applicationPanel;

    public static void main(String args[]) {

        mangaRipper = new MangaRipper();
        mangaRipper.applicationPanel = new ApplicationPanel();

        mangaRipper.add(mangaRipper.applicationPanel);

        mangaRipper.pack();
        mangaRipper.setTitle("Manga Ripper");
        mangaRipper.setResizable(false);
        mangaRipper.setDefaultCloseOperation(mangaRipper.EXIT_ON_CLOSE);
        mangaRipper.setLocationRelativeTo(null);
        mangaRipper.setVisible(true);

    }

}
