
import greenfoot.*;
import java.util.List;

/**
 * Proyektil api dari Kitsune. Extends Projectile.
 * Punya animasi sendiri dan cek batas layar.
 */
public class KitsuneFire extends Projectile {

    private GreenfootImage[] animationSprites;
    private int animFrame = 0;
    private int animFrameDelay = 4;
    private int animFrameCounter = 0;

    /**
     * Constructor untuk KitsuneFire.
     * @param owner Siapa yang menembakkan ini
     * @param speed Kecepatan gerak proyektil
     * @param damage Damage yang diberikan
     */
    public KitsuneFire(Character owner, int speed, int damage) {
        super(owner, speed, damage);

        // Saya perbaiki: file 'kitsune_fire.png' Anda memiliki 10 frame, bukan 11.
        animationSprites = SpriteLoader.loadSprites("dark/kitsune_fire.png", 10); 

        if (!owner.facingRight) {
            for (GreenfootImage img : animationSprites) {
                img.mirrorHorizontally();
            }
        }
        setImage(animationSprites[0]);
    }

    /**
     * Metode act utama.
     */
    @Override
    public void act() {
        if (getWorld() == null) return;

        moveForward();
        checkCollision();
        animate();
        checkEdge();
    }

    /**
     * Memainkan animasi api (spesifik KitsuneFire).
     */
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

    /**
     * Override metode checkCollision dari Projectile.
     * Tambahkan logika pengecekan faksi sebelum memberikan damage.
     */
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

    /**
     * Helper cek faksi (mirip AttackHitbox).
     * @param target Karakter yang terkena proyektil
     * @return true jika target adalah musuh dari owner
     */
    private boolean isEnemyFaction(Character target) {
        if (target == null || owner == null) {
            return false;
        } 
        return owner.getFaction() != target.getFaction();
    }

    /**
     * Memeriksa apakah proyektil sudah keluar layar (spesifik KitsuneFire).
     */
    private void checkEdge() {
        if (getWorld() == null) return;
        if (isAtEdge()) {
            getWorld().removeObject(this);
        }
    }
}