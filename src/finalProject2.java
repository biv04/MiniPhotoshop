
import javax.swing.*;
import java.awt.*;

public class finalProject2{
    private static final int WIDTH = 1400;
    private static final int HEIGHT = 800;

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI(){
        JFrame frame = new mainPanel(WIDTH,HEIGHT);
        frame.setVisible( true );
    }
}
//####################################################################

