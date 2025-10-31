import greenfoot.*;
import java.util.List;

public class KitsuneFire extends Projectile {

    private GreenfootImage[] animationSprites;
    private int animFrame = 0;
    private int animFrameDelay = 4;
    private int animFrameCounter = 0;

    public KitsuneFire(Character owner, int speed, int damage) {
        super(owner, speed, damage);

        animationSprites = SpriteLoader.loadSprites("dark/kitsune_fire.png", 10); 

        if (!owner.facingRight) {
            for (GreenfootImage img : animationSprites) {
                img.mirrorHorizontally();
            }
        }
        
        if (animationSprites != null && animationSprites.length > 0) {
            setImage(animationSprites[0]);
        }
    }

    @Override
    public void act() {
        if (getWorld() == null) return;

        moveForward();
        checkCollision(); 
        animate();
        checkEdge();
    }

    private void animate() {
        animFrameCounter++;
        if (animFrameCounter >= animFrameDelay) {
            animFrameCounter = 0;
            animFrame = (animFrame + 1) % animationSprites.length;
            if (animationSprites != null && animFrame < animationSprites.length) {
                setImage(animationSprites[animFrame]);
            }
        }
    }

    @Override
    protected void checkCollision() {
        if (getWorld() == null) return;

        Actor targetActor = getOneIntersectingObject(Character.class);

        if (targetActor != null && targetActor != owner && targetActor instanceof Character) {
            Character targetChar = (Character) targetActor;

            if (isEnemyFaction(targetChar)) {
                
                
                int radiusLedakan = 80; 
                int durasiLedakan = 5;  
                
                
                getWorld().addObject(new AoeHitbox(this.owner, this.damage, durasiLedakan, radiusLedakan), getX(), getY());
                
                
                getWorld().removeObject(this);
                
                
            }
        }
    }

    private boolean isEnemyFaction(Character target) {
        if (target == null || owner == null) {
            return false;
        } 
        return owner.getFaction() != target.getFaction();
    }

    private void checkEdge() {
        if (getWorld() == null) return;
        if (isAtEdge()) {
            getWorld().removeObject(this);
        }
    }
}