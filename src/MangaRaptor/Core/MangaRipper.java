package MangaRaptor.Core;

import javax.swing.JFrame;

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
        mangaRipper.setTitle("MangaRaptor");
        mangaRipper.setResizable(false);
        mangaRipper.setDefaultCloseOperation(mangaRipper.EXIT_ON_CLOSE);
        mangaRipper.setLocationRelativeTo(null);
        mangaRipper.setVisible(true);

    }

}
