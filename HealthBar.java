import greenfoot.*;

public class HealthBar extends Actor {
    private Character owner;

    public HealthBar(Character owner) {
        this.owner = owner;
        update(owner.currentHealth, owner.maxHealth);
    }

    public void update(int current, int max) {
        GreenfootImage img = new GreenfootImage(50, 5);
        img.setColor(Color.RED);
        img.fillRect(0, 0, 50, 5);
        img.setColor(Color.GREEN);
        img.fillRect(0, 0, (int) ((current / (double) max) * 50), 5);
        setImage(img);
    }

    public void act() {
        if (owner.getWorld() == null) return;
        setLocation(owner.getX(), owner.getY() - 40);
    }
}
