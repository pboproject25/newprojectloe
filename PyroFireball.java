import greenfoot.*;
import java.util.List;

public class PyroFireball extends Projectile {

    // Animasi bola api (jika ada)
    private GreenfootImage[] animationSprites;
    private int animFrame = 0;
    private int animFrameDelay = 5; // Contoh delay
    private int animFrameCounter = 0;
    private boolean hasAnimation = false; // Set true jika ada animasi

    public PyroFireball(Character owner, int speed, int damage) {
        super(owner, speed, damage);

        // !! GANTI PATH DAN JUMLAH FRAME JIKA ADA ANIMASI !!
        // Jika hanya 1 gambar, pakai cara di bawah
        // animationSprites = SpriteLoader.loadSprites("dark/pyro_fireball.png", 8); // Contoh jika animasi 8 frame
        // hasAnimation = true; 

        // Jika hanya satu gambar bola api statis:
        GreenfootImage fireballImage = new GreenfootImage("dark/pyro_fireball_single.png"); // Ganti dengan nama file gambar bola api Anda
        // Sesuaikan ukuran jika perlu
        // fireballImage.scale(20, 20); 

        if (!owner.facingRight) {
             if(hasAnimation){
                 for (GreenfootImage img : animationSprites) { img.mirrorHorizontally(); }
             } else {
                 fireballImage.mirrorHorizontally();
             }
        }
        
        if(hasAnimation && animationSprites != null && animationSprites.length > 0){
             setImage(animationSprites[0]);
        } else {
             setImage(fireballImage);
        }
    }

    @Override
    public void act() {
        if (getWorld() == null) return;

        moveForward();
        checkCollision();
        checkEdge();
        if(hasAnimation) { // Hanya panggil animate jika ada animasi
             animate();
        }
    }
    
    // Metode ini hanya relevan jika bola api punya animasi
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