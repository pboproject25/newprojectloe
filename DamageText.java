import greenfoot.*;

public class DamageText extends Actor {
    private int life = 30;

    public DamageText(int damage) {
        GreenfootImage img = new GreenfootImage("-" + damage, 20, Color.RED, new Color(0, 0, 0, 0));
        setImage(img);
    }

    public void act() {
        setLocation(getX(), getY() - 1);
        life--;
        if (life <= 0) getWorld().removeObject(this);
    }
}
