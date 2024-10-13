
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 20;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;
    Tile food;
    Random random;

    Timer gameLoop;
    int velocityx;
    int velocityy;
    boolean gameOver = false;
    int score = 0;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.lightGray);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityx = 0;
        velocityy = 1;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameOver) {
            draw(g);
        } else {
            drawGameOver(g);
        }
    }

    public void draw(Graphics g) {
        // Draw food
        g.setColor(Color.red);
        g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        // Draw snake head
        g.setColor(Color.black);
        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        // Draw snake body
        for (Tile bodyPart : snakeBody) {
            g.fillRect(bodyPart.x * tileSize, bodyPart.y * tileSize, tileSize, tileSize);
        }

        // Draw score
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 20);
    }

    public void drawGameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Game Over", boardWidth / 3, boardHeight / 2);

        // Display final score
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Score: " + score, boardWidth / 3 + 30, boardHeight / 2 + 40);
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public boolean checkSelfCollision() {
        for (Tile bodyPart : snakeBody) {
            if (collision(snakeHead, bodyPart)) {
                return true;
            }
        }
        return false;
    }

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));  // Add a new body part
            placeFood();
            score++;  // Increase the score when snake eats food
        }

        // Move the body (from last to first) to follow the head
        for (int i = snakeBody.size() - 1; i > 0; i--) {
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }

        if (!snakeBody.isEmpty()) {
            snakeBody.get(0).x = snakeHead.x;
            snakeBody.get(0).y = snakeHead.y;
        }

        // Move the snake head
        snakeHead.x += velocityx;
        snakeHead.y += velocityy;

        // Check if snake hits the wall or itself
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize || snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize || checkSelfCollision()) {
            gameOver = true;
            gameLoop.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();  // Refreshes the panel (calls paintComponent)
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_UP && velocityy != 1) {
                velocityx = 0;
                velocityy = -1;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityy != -1) {
                velocityx = 0;
                velocityy = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityx != 1) {
                velocityx = -1;
                velocityy = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityx != -1) {
                velocityx = 1;
                velocityy = 0;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
