package motorola.gui;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class UALPanel extends JPanel {
    private ImageIcon ualImage;

    public UALPanel() {
        setPreferredSize(new Dimension(250,300));
        setBorder(Theme.createTitledBorder("UAL"));
        setBackground(Theme.PANEL_LIGHT);
        loadImage();
    }

    private void loadImage() {
        try {
            // Chercher l'image dans le dossier resources avec différents chemins et noms
            String[] possiblePaths = {
                // Nouvelle image
                "resources/WhatsApp Image 2025-12-22 at 22.28.28.jpeg",
                "MOTOROLA/resources/WhatsApp Image 2025-12-22 at 22.28.28.jpeg",
                "bin/resources/WhatsApp Image 2025-12-22 at 22.28.28.jpeg",
                
                // Anciennes images
                "motorola/resources/ual.jpg.jpeg",
                "motorola/resources/ual.jpg",
                "resources/ual.jpg.jpeg",
                "resources/ual.jpg"
            };
            
            for (String imagePath : possiblePaths) {
                File imageFile = new File(imagePath);
                if (imageFile.exists() && imageFile.length() > 100) {
                    System.out.println("✓ Image trouvée: " + imageFile.getAbsolutePath());
                    ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                    
                    // Charger et redimensionner pour remplir le cadre
                    Image img = icon.getImage();
                    Image scaledImg = img.getScaledInstance(230, 430, Image.SCALE_SMOOTH);
                    ualImage = new ImageIcon(scaledImg);
                    
                    System.out.println("✓ Image affichée dans le panneau UAL");
                    return;
                }
            }
            System.err.println("❌ Aucune image trouvée dans les chemins: " + String.join(", ", possiblePaths));
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de l'image UAL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (ualImage != null) {
            g.drawImage(ualImage.getImage(), 7, 15, this);
        }
    }
}
