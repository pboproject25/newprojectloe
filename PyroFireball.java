import greenfoot.*;
import java.util.List;

public class PyroFireball extends Projectile {
    
    private GreenfootImage[] animationSprites;
    private int animFrame = 0;
    private int animFrameDelay = 5;
    private int animFrameCounter = 0;

    public PyroFireball(Character owner, int speed, int damage) {
        super(owner, speed, damage);
        
        
        try {
            animationSprites = SpriteLoader.loadSprites("dark/pyro_fireball_single.png", 12); 
        } catch (Exception e) {
            animationSprites = new GreenfootImage[1];
            animationSprites[0] = new GreenfootImage(20, 20);
            animationSprites[0].setColor(Color.RED);
            animationSprites[0].fill();
        }
        if (!owner.facingRight) {
            for (GreenfootImage img : animationSprites) { img.mirrorHorizontally(); }
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
        checkEdge();
        animate();
    }
    
    private void animate() {
        
        if (animationSprites == null || animationSprites.length == 0) return;
        animFrameCounter++;
        if (animFrameCounter >= animFrameDelay) {
            animFrameCounter = 0;
            animFrame = (animFrame + 1) % animationSprites.length;
            setImage(animationSprites[animFrame]);
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
                int durasiLedakan = 2; 
                getWorld().addObject(new AoeHitbox(this.owner, this.damage, durasiLedakan, radiusLedakan), getX(), getY());
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