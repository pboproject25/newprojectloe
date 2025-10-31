import greenfoot.*;
import java.util.List;
import java.util.ArrayList;

public class AttackHitbox extends Actor {
    private Character owner;
    private int damage;
    private int duration;
    private List<Actor> hitTargets;

    public AttackHitbox(Character owner, int damage, int duration) {
        this.owner = owner;
        this.damage = damage;
        this.duration = duration;
        hitTargets = new ArrayList<Actor>();
        GreenfootImage img = new GreenfootImage(40, 40);
        setImage(img); 
    }

    public void act() {
        if (getWorld() == null) return;
        if (getWorld() instanceof BattleWorld && ((BattleWorld)getWorld()).isPaused()) {
            return; 
        }

        if (owner != null && owner.getWorld() != null) {
            int hitboxOffsetX = (owner.facingRight ? 30 : -30);
            int centerY = owner.getY() - (owner.getImage() != null ? owner.getImage().getHeight() / 2 : 25);
            setLocation(owner.getX() + hitboxOffsetX, centerY);
        } else if (getWorld() != null) {
            getWorld().removeObject(this);
            return;
        }

        duration--;
        if (duration <= 0 && getWorld() != null) {
            getWorld().removeObject(this);
        } else {
            checkHit();
        }
    }

    
    private void checkHit() {
        if (getWorld() == null) return;
        List<Character> enemies = getIntersectingObjects(Character.class);
        for (Character enemy : enemies) {
            
            if (enemy != owner && !hitTargets.contains(enemy) && isEnemyFaction(enemy)) {
                
                
                enemy.takeDamage(damage);
                getWorld().addObject(new DamageText(damage), enemy.getX(), enemy.getY() - (enemy.getImage() != null ? enemy.getImage().getHeight() / 2 : 25));
                hitTargets.add(enemy); 
                duration = 0; 
                getWorld().removeObject(this); 
                return; 
                
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