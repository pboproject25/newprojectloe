import greenfoot.*;
import java.util.List;

public class ZipZipLightning extends Projectile {

    // Animasi petir
    private GreenfootImage[] animationSprites;
    private int animFrame = 0;
    private int animFrameDelay = 4; // Delay animasi petir (sesuaikan)
    private int animFrameCounter = 0;
    private boolean hasAnimation = true; // Kita punya animasi dari Charge.png

    public ZipZipLightning(Character owner, int speed, int damage) {
        super(owner, speed, damage);

        // Muat animasi dari Charge.png (10 frames)
        animationSprites = SpriteLoader.loadSprites("dark/zipzip_charge.png", 10);
        hasAnimation = true;

        if (!owner.facingRight) {
             if(hasAnimation && animationSprites != null){
                 for (GreenfootImage img : animationSprites) { img.mirrorHorizontally(); }
             }
        }

        if(hasAnimation && animationSprites != null && animationSprites.length > 0){
             setImage(animationSprites[0]);
        } else {
             // Fallback jika tidak ada animasi (seharusnya tidak terjadi)
             setImage(new GreenfootImage(10,10)); // Gambar kotak kecil
        }
    }

    @Override
    public void act() {
        if (getWorld() == null) return;

        moveForward();
        checkCollision();
        checkEdge();
        if(hasAnimation) {
             animate();
        }
    }

    private void animate() {
        if (!hasAnimation || animationSprites == null || animationSprites.length == 0) return;

        animFrameCounter++;
        if (animFrameCounter >= animFrameDelay) {
            animFrameCounter = 0;
            animFrame = (animFrame + 1) % animationSprites.length;
            if (animFrame < animationSprites.length) {
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