    
import greenfoot.*;
import java.util.List;

public abstract class Character extends Actor implements Damageable {

    public enum Faction {
        LIGHT,
        DARK
    }

    protected int maxHealth;
    protected int currentHealth;
    protected int attackDamage;
    protected int attackRange;
    protected int moveSpeed;
    protected boolean facingRight;
    protected HealthBar healthBar;
    protected Faction faction;

    public Character(int maxHealth, int damage, int range, int speed, boolean facingRight, Faction faction) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.attackDamage = damage;
        this.attackRange = range;
        this.moveSpeed = speed;
        this.facingRight = facingRight;
        this.faction = faction;
        this.healthBar = new HealthBar(this);
    }

    @Override
    public void act() {
        updateHealthBar();
    }

    @Override
    public void takeDamage(int amount) {
        currentHealth -= amount;
        if (currentHealth <= 0) {
            currentHealth = 0;
            if (getWorld() != null) {
                 die();
            }
        }
    }

    @Override
    public boolean isAlive() {
        return currentHealth > 0;
    }

    // Metode die() abstrak untuk animasi di kelas anak
    public abstract void die();

    private void updateHealthBar() {
        if (getWorld() != null && this.isAlive()) {
            if (healthBar.getWorld() == null) {
                getWorld().addObject(healthBar, getX(), getY() - 40);
            }
            healthBar.update(currentHealth, maxHealth);
        } else if (healthBar != null && healthBar.getWorld() != null) {
            getWorld().removeObject(healthBar);
        }
    }

    public Faction getFaction() {
        return this.faction;
    }
}