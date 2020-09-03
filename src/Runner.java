import javax.swing.*;

public class Runner {


    public static void main(String[] args) {
        System.out.println("Starting program");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        String version = "1.3.6.5";
        boolean testing = true;
        JFrame frame = new JFrame();
        FileManager fm = new FileManager();
        frame.setTitle(version);
        frame.setIconImage(fm.loadImage("Images/IconLogo.png"));
        UI ui = new UI(frame, version, testing);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(ui);
        frame.setVisible(true);
    }


}
