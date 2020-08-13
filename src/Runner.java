import javax.swing.*;

public class Runner {


    public static void main(String[] args) {
        System.out.println("Starting program");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame();
        FileManager fm = new FileManager();
        frame.setTitle("0.2.0.0");
        frame.setIconImage(fm.loadImage("Images/IconLogo.png"));
        UI ui = new UI(frame);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(ui);
        frame.setVisible(true);
    }


}
