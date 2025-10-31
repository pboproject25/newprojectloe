import greenfoot.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.Font;

public class CreditButton extends Actor
{
    public CreditButton() {
        GreenfootImage img = new GreenfootImage(150, 40);
        img.setColor(new greenfoot.Color(50, 50, 50, 200));
        img.fill();
        
        greenfoot.Font font = new greenfoot.Font("Arial", true, false, 24);
        img.setFont(font);
        img.setColor(greenfoot.Color.WHITE);
        
        java.awt.Font awtFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 24);
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        int textWidth = (int) awtFont.getStringBounds("Credit", frc).getWidth();
        
        int x = (150 - textWidth) / 2;
        int y = 29;

        img.drawString("Credit", x, y);
        img.setColor(greenfoot.Color.WHITE);
        img.drawRect(0, 0, 149, 39);
        setImage(img);
    }
    
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            Greenfoot.setWorld(new CreditWorld());
        }
    }
}