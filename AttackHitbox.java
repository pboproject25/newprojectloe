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
        //img.setColor(new Color(255, 0, 0, 100)); // Merah (untuk debug)
        img.fillRect(0, 0, 40, 40);
        img.setColor(new Color(0, 0, 0, 0)); // Transparan 
        setImage(img);
    }

    public void act() {
        if (owner != null && owner.getWorld() != null) {
            int hitboxOffsetX = (owner.facingRight ? 30 : -30);
            setLocation(owner.getX() + hitboxOffsetX, owner.getY());
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

    /**
     * Cek tabrakan dengan target yang valid.
     */
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

    /**
     * Helper untuk cek faksi musuh.
     */
    private boolean isEnemyFaction(Character target) {
        if (target == null || owner == null) {
            return false;
        } 
        return owner.getFaction() != target.getFaction();
    }
}