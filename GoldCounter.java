import greenfoot.*;

public class GoldCounter extends Actor
{
    private static final Color TEXT_COLOR = Color.YELLOW;
    private static final Color BG_COLOR = new Color(0, 0, 0, 150); 
    private static final int FONT_SIZE = 24;
    private static final int DISPLAY_WIDTH = 150;
    private static final int DISPLAY_HEIGHT = 30;

    
    private Font displayFont = new Font(true, false, FONT_SIZE); 

    public GoldCounter() {
        updateDisplay(0); 
    }

    
    public void updateDisplay(int goldAmount) {
        GreenfootImage image = new GreenfootImage(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        image.setColor(BG_COLOR);
        image.fill(); 

        image.setColor(TEXT_COLOR);
        image.setFont(displayFont);
        String text = "Gold: " + goldAmount;
        
        image.drawString(text, 10, FONT_SIZE); 

        setImage(image);
    }
}