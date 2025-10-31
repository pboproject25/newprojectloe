import greenfoot.*;

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
        if (getWorld() == null) return;
        if (getWorld() instanceof BattleWorld && ((BattleWorld)getWorld()).isPaused()) {
            return; 
        }
        
        if (healthBar != null) {
             updateHealthBar();
        }
    }

    @Override
    public void takeDamage(int amount) {
        currentHealth -= amount;
        if (currentHealth <= 0) {
            currentHealth = 0;
            
            World world = getWorld();
            if (world != null) {

                
                
                if (world instanceof BattleWorld && this instanceof Rewardable) {
                    BattleWorld battleWorld = (BattleWorld) world;
                    
                    if (this.faction != LegendOfElkaidu.playerFaction) {
                        
                        int reward = ((Rewardable) this).getRewardValue();
                        battleWorld.addGold(reward); 
                    }
                }
                

                
                die();
            }
        }
    }

    @Override
    public boolean isAlive() {
        return currentHealth > 0;
    }

    
    public abstract void die();

    private void updateHealthBar() {
        
        if (getWorld() != null && healthBar != null && this.isAlive()) {
            int actorHeight = (getImage() != null) ? getImage().getHeight() : 50;
            int healthBarY = getY() - actorHeight - 10; 

            if (healthBar.getWorld() == null) {
                
                getWorld().addObject(healthBar, getX(), healthBarY);
            }
            
            healthBar.setLocation(getX(), healthBarY);
            healthBar.update(currentHealth, maxHealth);

        } else if (healthBar != null && healthBar.getWorld() != null) {
            
            getWorld().removeObject(healthBar);
        }
    }

    public Faction getFaction() {
        return this.faction;
    }
    
    public int getHitboxRadius() {
        if (getImage() != null) {
            return getImage().getWidth() / 4;
        }
        return 15;
    }
}