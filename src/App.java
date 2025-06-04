import com.hengbai.ui.MainScreen;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("数字华容道");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(460, 518);
        //frame.setResizable(false);
        //frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);

        MainScreen mainScreen = new MainScreen(frame);
        frame.setContentPane(mainScreen.getPanel());
        frame.setVisible(true);
    }
}
