/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package querystore;

import com.formdev.flatlaf.FlatIntelliJLaf;
import java.io.File;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author partho
 */
public class QueryStore {

    /**
     * Returns a stable storage path in the user's home directory.
     * This ensures the app finds its data regardless of where the JAR is run from.
     */
    public static String getBaseStoragePath() {
        String userHome = System.getProperty("user.home");
        File storageDir = new File(userHome, ".querystore");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir.getAbsolutePath();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AuthFrame();
            }
        });
    }
    
}
