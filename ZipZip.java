import greenfoot.*;
import java.util.List;
import java.util.Comparator;

public class ZipZip extends Character implements Movable, Attackable, Rewardable, RangedUnit {

    // State
    private enum State { IDLE, RUNNING, ATTACKING, DYING }
    private State currentState = State.RUNNING;
    private Character currentTarget = null;

    // Animasi
    private GreenfootImage[] idleSprites, runSprites, dieSprites, attackSprites;

    // Frame
    private int idleFrame = 0, idleFrameDelay = 10, idleFrameCounter = 0; // Idle.png (8 frames)
    private int runFrame = 0, runFrameDelay = 7, runFrameCounter = 0;    // Walk.png (8 frames)
    private int dieFrame = 0, dieFrameDelay = 8, dieFrameCounter = 0;    // Dead.png (5 frames)
    private int attackFrame = 0, attackFrameCounter = 0;
    private boolean attackHasHit = false;

    // Pointer
    private GreenfootImage[] currentAttackSprites;
    private int currentAttackFrameDelay;
    private int currentAttackHitFrame;

    // Konfigurasi Serangan
    private static final int ATTACK_FRAME_DELAY = 6;  // Light_charge.png (13 frames)
    private static final int ATTACK_HIT_FRAME = 10;   // Frame ke-11 (indeks 10) saat petir keluar

    // Statistik
    private static final int MAX_HEALTH = 70;
    private static final int ATTACK_DAMAGE = 35;
    private static final double ATTACK_SPEED_SECONDS = 0.9;
    private static final int ATTACK_RANGE = 300;
    private static final int MOVE_SPEED = 1;
    private static final int REWARD_VALUE = 25;

    private int attackCooldown = (int)(ATTACK_SPEED_SECONDS * 60);
    private int currentCooldown = 0;

    public ZipZip(boolean facingRight) {
        super(MAX_HEALTH, ATTACK_DAMAGE, ATTACK_RANGE, MOVE_SPEED, facingRight, Faction.DARK);

        // Muat sprite (Ganti nama file jika perlu)
        idleSprites = SpriteLoader.loadSprites("dark/zipzip_idle.png", 7);     // Idle.png
        runSprites = SpriteLoader.loadSprites("dark/zipzip_walk.png", 7);      // Walk.png (asumsi ini adalah run/walk)
        dieSprites = SpriteLoader.loadSprites("dark/zipzip_dead.png", 5);      // Dead.png
        attackSprites = SpriteLoader.loadSprites("dark/zipzip_light_charge.png", 13); // Light_charge.png

        if (!this.facingRight) {
            flipAllSprites();
        }

        setImage(idleSprites[0]);
        currentState = State.IDLE;
    }

    private void flipAllSprites() {
        if(idleSprites != null) for (GreenfootImage img : idleSprites) { img.mirrorHorizontally(); }
        if(runSprites != null) for (GreenfootImage img : runSprites) { img.mirrorHorizontally(); }
        if(dieSprites != null) for (GreenfootImage img : dieSprites) { img.mirrorHorizontally(); }
        if(attackSprites != null) for (GreenfootImage img : attackSprites) { img.mirrorHorizontally(); }
    }

    @Override
    public void act() {
         if (getWorld() == null) return;
         super.act();

        if (currentState == State.DYING) {
            animate();
            performAction();
            return;
        }

        if (!isAlive()) {
             if (currentState != State.DYING) {
                die();
            }
            return;
        }

        if (currentCooldown > 0) {
            currentCooldown--;
        }
        updateState();
        animate();
        performAction();
    }

    @Override
    public void die() {
        currentState = State.DYING;
        dieFrame = 0; dieFrameCounter = 0;
        currentTarget = null;
    }

    private void updateState() {
        if (currentState == State.ATTACKING || currentState == State.DYING) {
            return;
        }

        if (currentTarget != null && (currentTarget.getWorld() == null || !currentTarget.isAlive())) {
             currentTarget = null;
        }

        if (currentTarget == null || !isTargetInRange(currentTarget)) {
            currentTarget = findTarget();
        }

        if (currentTarget != null) {
            if (currentCooldown <= 0) {
                currentState = State.ATTACKING;
                setupAttack();
            } else {
                currentState = State.IDLE;
            }
        } else {
            currentState = State.RUNNING;
        }
    }

    private void setupAttack() {
        attackFrame = 0;
        attackFrameCounter = 0;
        attackHasHit = false;
        currentCooldown = attackCooldown;

        currentAttackSprites = attackSprites;
        currentAttackFrameDelay = ATTACK_FRAME_DELAY;
        currentAttackHitFrame = ATTACK_HIT_FRAME;

        if (currentAttackSprites != null && currentAttackHitFrame >= currentAttackSprites.length) {
             System.out.println("!!! WARNING ZipZip: HitFrame invalid !!!");
             currentAttackHitFrame = Math.max(0, currentAttackSprites.length - 1);
        }
    }

    private void performAction() {
        switch (currentState) {
            case IDLE:
                break;
            case RUNNING:
                currentTarget = null;
                moveForward();
                break;
            case ATTACKING:
                if (attackFrame == currentAttackHitFrame && !attackHasHit) {
                    shootProjectile(currentTarget);
                    attackHasHit = true;
                }
                break;
            case DYING:
                if (dieSprites != null && dieFrame >= dieSprites.length - 1) {
                     if (getWorld() != null) getWorld().removeObject(this);
                }
                break;
        }
    }

    private void animate() {
         switch (currentState) {
            case IDLE:
                idleFrameCounter++;
                if (idleFrameCounter >= idleFrameDelay) {
                    idleFrameCounter = 0;
                    idleFrame = (idleFrame + 1) % idleSprites.length;
                    if (idleSprites != null && idleFrame < idleSprites.length) {
                       setImage(idleSprites[idleFrame]);
                    }
                }
                break;
            case RUNNING:
                runFrameCounter++;
                if (runFrameCounter >= runFrameDelay) {
                    runFrameCounter = 0;
                    runFrame = (runFrame + 1) % runSprites.length;
                    if (runSprites != null && runFrame < runSprites.length) {
                       setImage(runSprites[runFrame]);
                    }
                }
                break;
            case ATTACKING:
                attackFrameCounter++;
                if (attackFrameCounter >= currentAttackFrameDelay) {
                    attackFrameCounter = 0;
                    attackFrame++;
                    if (currentAttackSprites != null && attackFrame >= currentAttackSprites.length) {
                        attackFrame = 0;
                        currentState = State.IDLE;
                         if (idleSprites != null && idleSprites.length > 0) {
                           setImage(idleSprites[0]);
                         }
                    } else {
                         if (currentAttackSprites != null && attackFrame < currentAttackSprites.length) {
                            setImage(currentAttackSprites[attackFrame]);
                         }
                    }
                }
                break;
            case DYING:
                dieFrameCounter++;
                if (dieFrameCounter >= dieFrameDelay) {
                    dieFrameCounter = 0;
                    if (dieSprites != null && dieFrame < dieSprites.length) {
                        setImage(dieSprites[dieFrame]);
                        dieFrame++;
                    }
                }
                break;
        }
    }

    private boolean isTargetInRange(Actor target) {
        if (target == null || target.getWorld() == null) return false;
        int dx = Math.abs(this.getX() - target.getX());
        return dx <= attackRange;
    }

    private boolean isEnemyFaction(Character target) {
        if (target == null) return false;
        return this.getFaction() != target.getFaction();
    }

    private Character findTarget() {
        if (getWorld() == null) return null;
        List<Character> charactersInRange = getObjectsInRange(attackRange, Character.class);
        Character closestEnemy = null;
        int minDistance = Integer.MAX_VALUE;

        for (Character potentialTarget : charactersInRange) {
            if (potentialTarget != null && potentialTarget.getWorld() != null &&
                potentialTarget.isAlive() && potentialTarget != this &&
                isEnemyFaction(potentialTarget)) {
                int distance = Math.abs(this.getX() - potentialTarget.getX());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestEnemy = potentialTarget;
                }
            }
        }
        return closestEnemy;
    }

    @Override
    public void moveForward() {
        move(facingRight ? moveSpeed : -moveSpeed);
    }

    @Override
    public void stop() { }

    @Override
    public void attack(Character target) {
        shootProjectile(target);
    }

    @Override
    public void shootProjectile(Character target) {
        if (getWorld() == null) return;

        int spawnX = getX();
        // Sesuaikan offset Y
        int spawnY = getY() + 10; // Mungkin perlu digeser sedikit ke atas? Misal getY() - 10
        int projectileSpeed = 5; // Petir cepat

        ZipZipLightning projectile = new ZipZipLightning(this, projectileSpeed, this.attackDamage);

        getWorld().addObject(projectile, spawnX, spawnY);
    }

    @Override
    public int getRewardValue() {
        return REWARD_VALUE;
    }
}