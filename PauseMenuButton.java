import greenfoot.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.Font;

public class PauseMenuButton extends Actor
{
    private String action;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;

    public PauseMenuButton(String action) {
        this.action = action;
        
        GreenfootImage img = new GreenfootImage(BUTTON_WIDTH, BUTTON_HEIGHT);
        img.setColor(new greenfoot.Color(50, 50, 50, 220));
        img.fill();

        greenfoot.Font font = new greenfoot.Font("Arial", true, false, 28);
        img.setFont(font);
        img.setColor(greenfoot.Color.WHITE);

        java.awt.Font awtFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 28);
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        int textWidth = (int) awtFont.getStringBounds(action, frc).getWidth();
        
        int x = (BUTTON_WIDTH - textWidth) / 2;
        int y = 35; 

        img.drawString(action, x, y);
        
        img.setColor(greenfoot.Color.WHITE);
        img.drawRect(0, 0, BUTTON_WIDTH - 1, BUTTON_HEIGHT - 1);
        
        setImage(img);
    }
    
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            BattleWorld world = (BattleWorld)getWorld();
            
            switch(action) {
                case "Resume":
                    world.hidePauseMenu();
                    break;
                case "Retry":
                    world.stopMusic();
                    Greenfoot.setWorld(new BattleWorld());
                    break;
                case "Main Menu":
                    world.stopMusic();
                    Greenfoot.setWorld(new MainMenu());
                    break;
            }
        }
    }
}