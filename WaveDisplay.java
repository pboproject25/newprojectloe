import greenfoot.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.Font;

public class WaveDisplay extends Actor
{
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BG_COLOR = new Color(0, 0, 0, 150);
    private static final int FONT_SIZE = 20;
    private static final int DISPLAY_WIDTH = 200;
    private static final int DISPLAY_HEIGHT = 50;
    private greenfoot.Font displayFont = new greenfoot.Font(true, false, FONT_SIZE); 

    public WaveDisplay() {
        updateDisplay(1, 0); 
    }

    
    public void updateDisplay(int waveNumber, int timeLeft) {
        GreenfootImage image = new GreenfootImage(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        image.setColor(BG_COLOR);
        image.fill();
        image.setColor(TEXT_COLOR);
        image.setFont(displayFont);
        
        String waveText = "Wave: " + waveNumber;
        String timeText = "Time Left: " + Math.max(0, timeLeft) + "s"; 

        java.awt.Font awtFont = new java.awt.Font("Arial", java.awt.Font.BOLD, FONT_SIZE);
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        
        int waveTextWidth = (int) awtFont.getStringBounds(waveText, frc).getWidth();
        int waveTextX = (DISPLAY_WIDTH - waveTextWidth) / 2;
        
        int timeTextWidth = (int) awtFont.getStringBounds(timeText, frc).getWidth();
        int timeTextX = (DISPLAY_WIDTH - timeTextWidth) / 2;

        image.drawString(waveText, waveTextX, FONT_SIZE); 
        image.drawString(timeText, timeTextX, FONT_SIZE * 2 + 5); 

        setImage(image);
    }
}