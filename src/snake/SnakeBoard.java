/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class SnakeBoard extends JPanel{

    private BufferedImage gameBg;
    private BufferedImage snakeHeadLeft;
    private BufferedImage currentHead;
    private BufferedImage snakeHeadRight;
    private BufferedImage snakeHeadTop;
    private BufferedImage snakeHeadBottom;
    private BufferedImage snakeBody;
    private BufferedImage snakeFood;
    private BufferedImage snakePowerUp;
    
    public boolean isGameOver;
    
    private final int BOX_STARTX = 39;
    private final int BOX_STARTY = 19;
    private final int BOX_WIDTH = 897;
    private final int BOX_HEIGHT = 531;
    
    private int dots;
    private int allDots = 300;
    
    private int appleX;
    private int appleY;
    
    private int x[] = new int[allDots];
    private int y[] = new int[allDots];
    
    private int pastMove;
    
    private float interpolation;
    private float snakeX, snakeY;
    private float lastSnakeX, lastSnakeY;
    
    public int score = 0;
    public int highScore = 0;
    

    public void setInterpolation(float interpolation) {
        this.interpolation = interpolation;
    }
    
    int fps;
    public SnakeBoard() {
        loadImages();
        locateApple();
        
        pastMove = 1;
        currentHead = snakeHeadRight;
        //creates the dots
        dots = 4;
        for (int i = 0; i < dots; i++){
           x[i] = 350 - i * 14;
           y[i] = BOX_HEIGHT/14/2 * 14;
        }
        
        
        this.setSize(960,560);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        doDrawing(g);
    }
    
    private void doDrawing(Graphics g){
        
        //sets the background
        g.drawImage(gameBg,0 ,0 , this);
        int drawX = (int)((snakeX - lastSnakeX) * interpolation + lastSnakeX);
        int drawY = (int)((snakeY - lastSnakeY) * interpolation + lastSnakeY);
        
        g.drawImage(snakeFood, appleX, appleY, this);
        
        for (int i = 0; i < dots; i++){
            if (i == 0){
                g.drawImage(currentHead, x[i], y[i], this);
            } else {
                g.drawImage(snakeBody, x[i], y[i], this);
            }
        }
        
        
        //sets the SCORE
        String txtScore = "Score: " + score;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Roboto Thin", 0, 24));
        g.drawString(txtScore, BOX_WIDTH - 116, BOX_HEIGHT - 5);
        
    }
    
    //load all images
    private void loadImages() {
        gameBg = loadImage("gameBg");
        snakeHeadLeft = loadImage("snakeHeadLeft2");
        snakeHeadRight = loadImage("snakeHeadRight2");
        snakeHeadTop = loadImage("snakeHeadTop2");
        snakeHeadBottom = loadImage("snakeHeadBottom2");
        snakeBody = loadImage("snakeBody2");
        snakeFood = loadImage("snakeFood");
        snakePowerUp = loadImage("snakePowerUp");
    }
    
    //loads images
    public BufferedImage loadImage(String fileName){
        BufferedImage img;
        try {
             img = ImageIO.read(getClass().getResource("/images/" + fileName + ".png"));
            return img;
        } catch (IOException ex) {
            Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    //loads images
    public BufferedImage loadImage(String fileName, int width, int height){
        BufferedImage img;
        try {
             img = ImageIO.read(new File("src/images/" + fileName + ".png"));
            return img;
        } catch (IOException ex) {
            Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void update(){
        
        
        //moves the snake
        for (int i = dots; i > 0; i--){
            x[i] = x[(i - 1)];
            y[i] = y[(i - 1)];
        }
        
        //checks for apples
        if ((x[0] == appleX) && (y[0] == appleY)){
            dots++;
            score++;
            locateApple();
        }
        
        if (pastMove == 1){
            x[0] += 14;
        }
        else if (pastMove == 2){
            x[0] -= 14;
        }
        else if (pastMove == 3){
            y[0] += 14;
        }
        else if (pastMove == 4){
            y[0] -= 14;
        }
        
        //checks for the collision of snake before anything else
        for (int i = dots; i > 0; i--){
            if ((x[0] == x[i]) && (y[0] == y[i])){
                isGameOver = true;
            }
        }
        
        if (x[0] < BOX_STARTX || y[0] < BOX_STARTY || x[0] > BOX_WIDTH ||  y[0] > BOX_HEIGHT){//GAME OVER
            
            //for highscore
            if (score > highScore){
                highScore = score;
            }
            isGameOver = true;
        }
        
        fps++;
    }
    
    //MOVEMENTS
    public void moveLeft(){
        if (pastMove != 1 && pastMove!= 2){
            pastMove = 2;
            currentHead = snakeHeadLeft;
            System.out.println(pastMove);
        }
    }
    public void moveRight(){
        if (pastMove != 2 && pastMove != 1){
            pastMove = 1;
            currentHead = snakeHeadRight;
            
            System.out.println(pastMove);
        }
    }
    public void moveTop(){
        if (pastMove != 3 && pastMove != 4){
            pastMove = 4;
            currentHead = snakeHeadTop;
            
            System.out.println(pastMove);
        }
    }
    public void moveBottom(){
        if (pastMove != 4 && pastMove != 3){
            pastMove = 3;
            currentHead = snakeHeadBottom;
            System.out.println(pastMove);
        }
    }

    private void locateApple() {
        double randX = (Math.random() * (((BOX_WIDTH)/14)-2)) + (BOX_STARTX/14) + 1;
        double  randY = (Math.random() * (((BOX_HEIGHT)/14)-2)) + (BOX_STARTY/14) + 1;
        appleX = (int) (randX) * 14;
        appleY = (int) (randY) * 14;
        System.out.println((int)randX);
        System.out.println((int)randY);
    }
    
    public void resetGame(){
        score = 0;
        
        pastMove = 1;
        currentHead = snakeHeadRight;
        //creates the dots
        for (int i = 0; i < dots; i++){
            x[i] = 0;
            y[i] = 0;
        }
        dots = 4;
        for (int i = 0; i < dots; i++){
           x[i] = 350 - i * 14;
           y[i] = BOX_HEIGHT/14/2 * 14;
        }
        locateApple();
        isGameOver = false;
    }

    
    
}
