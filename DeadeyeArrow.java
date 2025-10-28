import greenfoot.*;
import java.util.List;

public class DeadeyeArrow extends Projectile {

    public DeadeyeArrow(Character owner, int speed, int damage) {
        super(owner, speed, damage);

        GreenfootImage arrowImage = new GreenfootImage("dark/Deadeye_Arrow.png");

        if (!owner.facingRight) {
            arrowImage.mirrorHorizontally();
        }

        setImage(arrowImage);
    }

    @Override
    public void act() {
        if (getWorld() == null) return;

        moveForward();
        checkCollision();
        checkEdge();
    }

    @Override
    protected void checkCollision() {
        if (getWorld() == null) return;

        Actor targetActor = getOneIntersectingObject(Character.class);

        if (targetActor != null && targetActor != owner && targetActor instanceof Character) {
            Character targetChar = (Character) targetActor;

            if (isEnemyFaction(targetChar)) {
                targetChar.takeDamage(this.damage);
                getWorld().addObject(new DamageText(this.damage), targetChar.getX(), targetChar.getY());
                getWorld().removeObject(this);
            }
        }
    }

    private boolean isEnemyFaction(Character target) {
         if (target == null || owner == null) return false;
         return owner.getFaction() != target.getFaction();
    }

    private void checkEdge() {
        if (getWorld() == null) return;
        if (isAtEdge()) {
            getWorld().removeObject(this);
        }
    }
}