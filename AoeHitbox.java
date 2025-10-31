import greenfoot.*;
import java.util.List;
import java.util.ArrayList;

public class AoeHitbox extends Actor {
    private Character owner;
    private int damage;
    private int duration;
    private List<Actor> hitTargets; 

    public AoeHitbox(Character owner, int damage, int duration, int radius) {
        this.owner = owner;
        this.damage = damage;
        this.duration = duration;
        hitTargets = new ArrayList<Actor>();

        GreenfootImage img = new GreenfootImage(radius, radius);
        
        setImage(img);
    }

    
    public void act() {
        if (getWorld() == null) return;

        if (duration > 0 && hitTargets.isEmpty()) { 
            checkHit();
        }

        duration--;
        if (duration <= 0) {
            getWorld().removeObject(this);
        }
    }

    
    private void checkHit() {
        if (getWorld() == null) return;
        
        
        List<Character> enemies = getIntersectingObjects(Character.class);

        for (Character enemy : enemies) {
            
            if (enemy != owner && !hitTargets.contains(enemy) && isEnemyFaction(enemy)) {
                
                
                enemy.takeDamage(damage);
                getWorld().addObject(new DamageText(damage), enemy.getX(), enemy.getY());
                
                
                hitTargets.add(enemy); 
            }
        }
    }

    
    private boolean isEnemyFaction(Character target) {
        if (target == null || owner == null) {
            return false;
        } 
        return owner.getFaction() != target.getFaction();
    }
}