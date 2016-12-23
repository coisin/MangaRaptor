package MangaRaptor.Core;

import javax.swing.JFrame;

/**
 * Created by oduibhir on 24/09/16.
 */
public class MangaRaptor extends JFrame {

    static MangaRaptor mangaRaptor;
    ApplicationPanel applicationPanel;

    public static void main(String args[]) {

        mangaRaptor = new MangaRaptor();
        mangaRaptor.applicationPanel = new ApplicationPanel();
        
        mangaRaptor.add(mangaRaptor.applicationPanel);

        mangaRaptor.pack();
        mangaRaptor.setTitle("MangaRaptor");
        mangaRaptor.setResizable(false);
        mangaRaptor.setDefaultCloseOperation(mangaRaptor.EXIT_ON_CLOSE);
        mangaRaptor.setLocationRelativeTo(null);
        mangaRaptor.setVisible(true);

    }

}
