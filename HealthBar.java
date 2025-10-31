import greenfoot.*;

public class HealthBar extends Actor {
    private Character owner;

    public HealthBar(Character owner) {
        this.owner = owner;
        if(owner != null) {
            update(owner.currentHealth, owner.maxHealth);
        }
    }

    public void update(int current, int max) {
        if (max <= 0) max = 1; 
        
        GreenfootImage img = new GreenfootImage(50, 5);
        img.setColor(Color.RED);
        img.fillRect(0, 0, 50, 5);
        img.setColor(Color.GREEN);
        
        int currentClamped = Math.min(current, max);
        
        img.fillRect(0, 0, (int) ((currentClamped / (double) max) * 50), 5);
        setImage(img);
    }

}