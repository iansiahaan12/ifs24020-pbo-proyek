package org.delcom.app;

import org.delcom.app.views.KoleksiView;
import javax.swing.SwingUtilities;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        try {
            // Menggunakan tampilan bawaan sistem operasi (Windows/Mac)
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Generate Dummy UUID untuk user sementara
            UUID dummyUserId = UUID.randomUUID(); 
            
            // Buka Aplikasi
            new KoleksiView(dummyUserId).setVisible(true);
        });
    }
}