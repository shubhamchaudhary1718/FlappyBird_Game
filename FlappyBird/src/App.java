import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int boardWidth = 360;
        int boardHeight = 640;

        // Create the JFrame (game window)
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setResizable(false); // Prevent resizing
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the app when the window is closed

        // Create the FlappyBird game instance and add it to the frame
        FlappyBird flappyBirdGame = new FlappyBird();
        frame.add(flappyBirdGame);
        frame.pack(); // Resize the window to fit the game

        // Request focus so that the game can immediately start responding to key inputs
        flappyBirdGame.requestFocusInWindow();

        // Display the window
        frame.setVisible(true);
    }
}