/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Snake extends JFrame implements ActionListener{
    MainMenu mainMenu;
    
    private boolean running = false;
    private boolean paused = false;
    private int fps = 30;
    private int frameCount = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Snake();
    }

    public Snake() {
        mainMenu = new MainMenu();
        mainMenu.setVisible(true);
        mainMenu.addKeyListener(new TAdapter());
        
        running = true;
        
        runGameLoop();
    }
    
    public void runGameLoop() {
        Thread loop = new Thread() {
            public void run() {
                gameLoop();
            }
        };
        loop.start();
    }
    
    //<editor-fold defaultstate="collapsed" desc="GAME LOOP">
    //Only run this in another Thread!
    private void gameLoop() {
        //This value would probably be stored elsewhere.
        final double GAME_HERTZ = 20.0;
        //Calculate how many ns each frame should take for our target game hertz.
        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
        //At the very most we will update the game this many times before a new render.
        //If you're worried about visual hitches more than perfect timing, set this to 1.
        final int MAX_UPDATES_BEFORE_RENDER = 5;
        //We will need the last update time.
        double lastUpdateTime = System.nanoTime();
        //Store the last time we rendered.
        double lastRenderTime = System.nanoTime();

        //If we are able to get as high as this FPS, don't render again.
        final double TARGET_FPS = 60;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        //Simple way of finding FPS.
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (running) {
            double now = System.nanoTime();
            int updateCount = 0;

            if (!paused) {
                //Do as many game updates as we need to, potentially playing catchup.
                while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                    updateGame();
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;
                }

                //If for some reason an update takes forever, we don't want to do an insane number of catchups.
                //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
                if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }

                //Render. To do so, we need to calculate interpolation for a smooth render.
                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
                drawGame(interpolation);
                lastRenderTime = now;

                //Update the frames we got.
                int thisSecond = (int) (lastUpdateTime / 1000000000);
                if (thisSecond > lastSecondTime) {
                    System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                    fps = frameCount;
                    frameCount = 0;
                    lastSecondTime = thisSecond;
                }

                //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
                while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                    Thread.yield();

                    //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                    //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                    //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }

                    now = System.nanoTime();
                }
            }
        }
    }
    //</editor-fold> //THIS IS THE GAME LOOP

    //UPDATE GAME
    private void updateGame() {
        if (mainMenu.snakeBoard.isVisible() && mainMenu.snakeBoard.isGameOver == false){
            mainMenu.snakeBoard.update();
        }
        
        if (mainMenu.snakeBoard.isVisible() && mainMenu.snakeBoard.isGameOver == true){
            
            //Asks if user wants to restart
            int choice = JOptionPane.showConfirmDialog(null, "GAME OVER! Retry?");
            if (choice == 0){
                mainMenu.snakeBoard.resetGame();
            } else {
                mainMenu.snakeBoard.setVisible(false);
            }
        }
    }

    private void drawGame(float interpolation) {
        if (mainMenu.snakeBoard.isVisible() && mainMenu.snakeBoard.isGameOver == false){
            mainMenu.snakeBoard.setInterpolation(interpolation);
            mainMenu.snakeBoard.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
    //KEYBOARD
    
    private class TAdapter extends KeyAdapter {
        
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            
            if (key == KeyEvent.VK_LEFT){
                mainMenu.snakeBoard.moveLeft();
            }
            
            else if (key == KeyEvent.VK_RIGHT){
                mainMenu.snakeBoard.moveRight();
            }
            
            else if (key == KeyEvent.VK_UP){
                mainMenu.snakeBoard.moveTop();
            }
            
            else if (key == KeyEvent.VK_DOWN){
                mainMenu.snakeBoard.moveBottom();
            }
        }
        
    }

}
