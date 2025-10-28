import greenfoot.*;
import java.util.List;
import java.util.Comparator;

public class Kunoichi extends Character implements Movable, Attackable, Rewardable, MeleeUnit {

    // State
    private enum State { IDLE, RUNNING, ATTACKING, DYING }
    private State currentState = State.RUNNING;
    private Character currentTarget = null;

    // Animasi
    private GreenfootImage[] idleSprites, runSprites, dieSprites, attack1Sprites, attack2Sprites;

    // Frame
    private int idleFrame = 0, idleFrameDelay = 8, idleFrameCounter = 0; // Idle.png (8 frames)
    private int runFrame = 0, runFrameDelay = 6, runFrameCounter = 0; // Run.png (8 frames)
    private int dieFrame = 0, dieFrameDelay = 7, dieFrameCounter = 0; // Dead.png (5 frames)
    private int attackFrame = 0, attackFrameCounter = 0;
    private boolean attackHasHit = false;

    // Pointer
    private GreenfootImage[] currentAttackSprites;
    private int currentAttackFrameDelay;
    private int currentAttackHitFrame;

    // Konfigurasi Serangan (Sesuaikan hit frame)
    private static final int ATTACK_1_FRAME_DELAY = 5, ATTACK_1_HIT_FRAME = 4; // Attack_1.png (6 frames), hit di frame ke-4 (indeks 4)
    private static final int ATTACK_2_FRAME_DELAY = 5, ATTACK_2_HIT_FRAME = 5; // Attack_2.png (7 frames), hit di frame ke-5 (indeks 5)

    // Statistik
    private static final int MAX_HEALTH = 110;
    private static final int ATTACK_DAMAGE = 20;
    private static final double ATTACK_SPEED_SECONDS = 0.6;
    // Statistik lain (samakan dengan Oniro atau sesuaikan)
    private static final int ATTACK_RANGE = 50;
    private static final int MOVE_SPEED = 2; // Kunoichi biasanya cepat
    private static final int REWARD_VALUE = 20; // Sesuaikan

    private int attackCooldown = (int)(ATTACK_SPEED_SECONDS * 60); // 0.6 * 60 = 36 frames
    private int currentCooldown = 0;

    public Kunoichi(boolean facingRight) {
        super(MAX_HEALTH, ATTACK_DAMAGE, ATTACK_RANGE, MOVE_SPEED, facingRight, Faction.DARK);

        // Muat sprite (Ganti nama file jika perlu)
        idleSprites = SpriteLoader.loadSprites("dark/kunoichi_idle.png", 9);     // Idle.png
        runSprites = SpriteLoader.loadSprites("dark/kunoichi_run.png", 8);       // Run.png
        dieSprites = SpriteLoader.loadSprites("dark/kunoichi_dead.png", 5);      // Dead.png
        attack1Sprites = SpriteLoader.loadSprites("dark/kunoichi_attack1.png", 6);// Attack_1.png
        attack2Sprites = SpriteLoader.loadSprites("dark/kunoichi_attack2.png", 8);// Attack_2.png

        if (!this.facingRight) {
            flipAllSprites();
        }

        setImage(runSprites[0]); // Mulai dengan berlari
    }

    private void flipAllSprites() {
        if(idleSprites != null) for (GreenfootImage img : idleSprites) { img.mirrorHorizontally(); }
        if(runSprites != null) for (GreenfootImage img : runSprites) { img.mirrorHorizontally(); }
        if(dieSprites != null) for (GreenfootImage img : dieSprites) { img.mirrorHorizontally(); }
        if(attack1Sprites != null) for (GreenfootImage img : attack1Sprites) { img.mirrorHorizontally(); }
        if(attack2Sprites != null) for (GreenfootImage img : attack2Sprites) { img.mirrorHorizontally(); }
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
        if (Greenfoot.getRandomNumber(2) == 0) {
            currentAttackSprites = attack1Sprites;
            currentAttackFrameDelay = ATTACK_1_FRAME_DELAY;
            currentAttackHitFrame = ATTACK_1_HIT_FRAME;
        } else {
            currentAttackSprites = attack2Sprites;
            currentAttackFrameDelay = ATTACK_2_FRAME_DELAY;
            currentAttackHitFrame = ATTACK_2_HIT_FRAME;
        }

        if (currentAttackSprites != null && currentAttackHitFrame >= currentAttackSprites.length) {
             System.out.println("!!! WARNING Kunoichi: HitFrame invalid !!!");
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
                    slash(null);
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
                    if (idleSprites != null && idleSprites.length > 0 && idleFrame < idleSprites.length) { // Pengaman
                       setImage(idleSprites[idleFrame]);
                    }
                }
                break;
            case RUNNING:
                runFrameCounter++;
                if (runFrameCounter >= runFrameDelay) {
                    runFrameCounter = 0;
                    runFrame = (runFrame + 1) % runSprites.length;
                    if (runSprites != null && runSprites.length > 0 && runFrame < runSprites.length) { // Pengaman
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
                         if (currentAttackSprites != null && attackFrame < currentAttackSprites.length) { // Pengaman
                            setImage(currentAttackSprites[attackFrame]);
                         }
                    }
                }
                break;
            case DYING:
                dieFrameCounter++;
                if (dieFrameCounter >= dieFrameDelay) {
                    dieFrameCounter = 0;
                    if (dieSprites != null && dieFrame < dieSprites.length) { // Pengaman
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
        slash(target);
    }

    @Override
    public void slash(Character target) {
        if (getWorld() == null) return;
        int hitboxOffsetX = (facingRight ? 25 : -25); // Kunoichi mungkin jangkauannya pendek
        int spawnX = getX() + hitboxOffsetX;
        int spawnY = getY();
        AttackHitbox hitbox = new AttackHitbox(this, this.attackDamage, 4); // Durasi hitbox singkat
        getWorld().addObject(hitbox, spawnX, spawnY);
    }

    @Override
    public int getRewardValue() {
        return REWARD_VALUE;
    }
}