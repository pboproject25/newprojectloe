import greenfoot.*;

public class PauseButton extends Actor
{
    public PauseButton() {
        GreenfootImage img = new GreenfootImage(30, 30);
        img.setColor(new greenfoot.Color(0, 0, 0, 180));
        img.fill();
        
        img.setColor(greenfoot.Color.WHITE);
        img.fillRect(9, 8, 5, 14); 
        img.fillRect(16, 8, 5, 14); 

        img.setColor(new greenfoot.Color(200, 200, 200));
        img.drawRect(0, 0, 29, 29);
        setImage(img);
    }
    
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            World world = getWorld();
            if (world instanceof BattleWorld) {
                ((BattleWorld)world).showPauseMenu();
            }
        }
    }
}