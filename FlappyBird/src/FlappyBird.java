import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images.
    Image backgroundImage;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird dimensions.
    int birdWidth = 34;
    int birdHeight = 24;

    // Bird objects.
    Bird bird1, bird2, bird3;

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Bird {
        int x;
        int y;
        int width;
        int height;
        Image img;
        int velocityY = 0;
        double score = 0;
        boolean isGameOver = false;

        Bird(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.width = birdWidth;
            this.height = birdHeight;
            this.img = img;
        }
    }

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic.
    int velocityX = -4; // Moves pipe to the left (makes it look like the birds are moving right).
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images.
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird1.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Initialize the three birds at different vertical positions.
        bird1 = new Bird(boardWidth / 8, boardHeight / 2 - 50, birdImg);
        bird2 = new Bird(boardWidth / 8, boardHeight / 2, birdImg);
        bird3 = new Bird(boardWidth / 8, boardHeight / 2 + 50, birdImg);

        pipes = new ArrayList<Pipe>();

        // Timer for placing pipes.
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        // Game loop timer.
        gameLoop = new Timer(1000 / 60, this); // 1000/60 = 16.6 ms per frame.
        gameLoop.start();
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background.
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);

        // Draw birds.
        drawBird(g, bird1);
        drawBird(g, bird2);
        drawBird(g, bird3);

        // Draw pipes.
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Display scores.
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Bird 1: " + (int) bird1.score, 10, 30);
        g.drawString("Bird 2: " + (int) bird2.score, 10, 60);
        g.drawString("Bird 3: " + (int) bird3.score, 10, 90);

        // Game over check for all birds.
        if (bird1.isGameOver && bird2.isGameOver && bird3.isGameOver) {
            g.drawString("GAME OVER", 100, 300);
            declareWinner(g);
        }
    }

    public void drawBird(Graphics g, Bird bird) {
        if (!bird.isGameOver) {
            g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        }
    }

    public void declareWinner(Graphics g) {
        double highestScore = Math.max(Math.max(bird1.score, bird2.score), bird3.score);
        String winner = "";
        if (bird1.score == highestScore) winner = "Bird 1";
        else if (bird2.score == highestScore) winner = "Bird 2";
        else if (bird3.score == highestScore) winner = "Bird 3";

        g.drawString("Winner: " + winner, 100, 350);
    }

    public void move() {
        // Apply gravity and movement to each bird.
        updateBird(bird1);
        updateBird(bird2);
        updateBird(bird3);

        // Move pipes and check for collisions.
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed) {
                if (bird1.x > pipe.x + pipe.width) {
                    pipe.passed = true;
                    if (!bird1.isGameOver) bird1.score += 0.5;
                }
                if (bird2.x > pipe.x + pipe.width) {
                    pipe.passed = true;
                    if (!bird2.isGameOver) bird2.score += 0.5;
                }
                if (bird3.x > pipe.x + pipe.width) {
                    pipe.passed = true;
                    if (!bird3.isGameOver) bird3.score += 0.5;
                }
            }

            // Check collisions for each bird.
            checkCollision(bird1, pipe);
            checkCollision(bird2, pipe);
            checkCollision(bird3, pipe);
        }
    }

    public void updateBird(Bird bird) {
        if (!bird.isGameOver) {
            bird.velocityY += gravity;
            bird.y += bird.velocityY;
            bird.y = Math.max(bird.y, 0);

            if (bird.y > boardHeight) {
                bird.isGameOver = true;
            }
        }
    }

    public void checkCollision(Bird bird, Pipe pipe) {
        if (!bird.isGameOver && collision(bird, pipe)) {
            bird.isGameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        // Stop the game loop if all birds are done.
        if (bird1.isGameOver && bird2.isGameOver && bird3.isGameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!bird1.isGameOver) bird1.velocityY = -9;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            if (!bird2.isGameOver) bird2.velocityY = -9;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (!bird3.isGameOver) bird3.velocityY = -9;
        }

        // Restart the game if all birds are over.
        if (bird1.isGameOver && bird2.isGameOver && bird3.isGameOver) {
            resetGame();
        }
    }

    public void resetGame() {
        bird1.y = boardHeight / 2 - 50;
        bird2.y = boardHeight / 2;
        bird3.y = boardHeight / 2 + 50;

        bird1.velocityY = bird2.velocityY = bird3.velocityY = 0;
        bird1.score = bird2.score = bird3.score = 0;
        bird1.isGameOver = bird2.isGameOver = bird3.isGameOver = false;

        pipes.clear();
        placePipeTimer.start();
        gameLoop.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
