import greenfoot.*;  

/**
 * Tombol untuk kembali ke Main Menu dari layar Victory atau Game Over.
 */
public class ReturnToMenuButton extends Actor
{
    public ReturnToMenuButton() {
        GreenfootImage buttonImage = new GreenfootImage("Main Menu", 30, Color.BLACK, Color.LIGHT_GRAY);
        buttonImage.setColor(Color.DARK_GRAY);
        buttonImage.drawRect(0, 0, buttonImage.getWidth()-1, buttonImage.getHeight()-1);
        setImage(buttonImage);
    }

    /**
     * Act - Periksa klik mouse.
     */
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            Greenfoot.setWorld(new MainMenu());
        }
    }
}