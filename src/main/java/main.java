import app.Menu;
import com.formdev.flatlaf.FlatLightLaf;
import models.*;

import javax.swing.*;
import java.io.PrintStream;

public class main {

    public static void main(String[] args) {
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(new java.io.OutputStream() {
            @Override
            public void write(int b) {
                // Discard output
            }
        }));

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            // Optionally log or handle the exception
        } finally {
            // Restore original System.err
            System.setErr(originalErr);
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            new Menu().setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AppData.saveData();
        }));
    }
}

