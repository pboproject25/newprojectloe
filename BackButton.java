import greenfoot.*;

public class BackButton extends Actor
{
    private World returnWorld;

    public BackButton(World returnWorld) {
        this.returnWorld = returnWorld;
        GreenfootImage img = new GreenfootImage(" < Back ", 24, Color.WHITE, new Color(0,0,0,180));
        img.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);
        setImage(img);
    }
    
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            Greenfoot.setWorld(returnWorld);
        }
    }
}