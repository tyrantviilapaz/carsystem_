import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Car Selling System");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1080, 720);
                frame.setLocationRelativeTo(null);
                frame.setContentPane(new Login());
                frame.setVisible(true);
            }
        });
    }
}

