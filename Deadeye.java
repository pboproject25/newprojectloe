import greenfoot.*;
import java.util.List;
import java.util.Comparator;

public class Deadeye extends Character implements Movable, Attackable, Rewardable, RangedUnit, MeleeUnit {

    private enum State { IDLE, RUNNING, ATTACKING, DYING }
    private State currentState = State.RUNNING;
    private Character currentTarget = null;

    private GreenfootImage[] idleSprites, runSprites, dieSprites;
    private GreenfootImage[] meleeAttackSprites, rangedAttackSprites;

    private int idleFrame = 0, idleFrameDelay = 8, idleFrameCounter = 0;
    private int runFrame = 0, runFrameDelay = 6, runFrameCounter = 0;
    private int dieFrame = 0, dieFrameDelay = 7, dieFrameCounter = 0;
    private int attackFrame = 0, attackFrameCounter = 0;
    private boolean attackHasHit = false;

    private GreenfootImage[] currentAttackSprites;
    private int currentAttackFrameDelay;
    private int currentAttackHitFrame;

    private static final int MELEE_ATTACK_FRAME_DELAY = 6;
    private static final int MELEE_ATTACK_HIT_FRAME = 2;
    private static final int RANGED_ATTACK_FRAME_DELAY = 5;
    private static final int RANGED_ATTACK_HIT_FRAME = 14;

    private static final int MAX_HEALTH = 80;
    private static final int ATTACK_DAMAGE = 18;
    private static final double ATTACK_SPEED_SECONDS = 1.0;
    private static final int RANGED_ATTACK_RANGE = 250;
    private static final int MELEE_ATTACK_RANGE = 50;
    private static final int MOVE_SPEED = 1;
    private static final int REWARD_VALUE = 1;
    private static final int PROJECTILE_SPEED = 7;

    private int attackCooldown = (int)(ATTACK_SPEED_SECONDS * 60);
    private int currentCooldown = 0;

    public Deadeye(boolean facingRight) {
        super(MAX_HEALTH, ATTACK_DAMAGE, RANGED_ATTACK_RANGE, MOVE_SPEED, facingRight, Faction.DARK);

        idleSprites = SpriteLoader.loadSprites("dark/Deadeye_idle.png", 7);
        runSprites = SpriteLoader.loadSprites("dark/Deadeye_walk.png", 8);
        dieSprites = SpriteLoader.loadSprites("dark/Deadeye_die.png", 5);
        meleeAttackSprites = SpriteLoader.loadSprites("dark/Deadeye_attack.png", 3);
        rangedAttackSprites = SpriteLoader.loadSprites("dark/Deadeye_AttackPanah.png", 15);

        if (!this.facingRight) {
            flipAllSprites();
        }

        setImage(idleSprites[0]);
        currentState = State.IDLE;
    }

    private void flipAllSprites() {
        for (GreenfootImage img : idleSprites) { img.mirrorHorizontally(); }
        for (GreenfootImage img : runSprites) { img.mirrorHorizontally(); }
        for (GreenfootImage img : dieSprites) { img.mirrorHorizontally(); }
        for (GreenfootImage img : meleeAttackSprites) { img.mirrorHorizontally(); }
        for (GreenfootImage img : rangedAttackSprites) { img.mirrorHorizontally(); }
    }

    @Override
    public void act() {
        if (getWorld() == null) return;
        super.act();

        if (currentState == State.DYING) {
            animate(); performAction(); return;
        }
        if (!isAlive()) {
             if (currentState != State.DYING) { die(); }
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

    private boolean isTargetInMeleeRange(Actor target) {
        if (target == null || target.getWorld() == null) return false;
        int dx = Math.abs(this.getX() - target.getX());
        return dx <= MELEE_ATTACK_RANGE;
    }

    private boolean isTargetInRange(Actor target) {
        if (target == null || target.getWorld() == null) return false;
        int dx = Math.abs(this.getX() - target.getX());
        return dx <= attackRange; 
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
                if (isTargetInMeleeRange(currentTarget)) {
                    setupAttack(true);
                } else {
                    setupAttack(false);
                }
            } else {
                currentState = State.IDLE;
            }
        } else {
            currentState = State.RUNNING;
        }
    }

    private void setupAttack(boolean isMelee) {
        attackFrame = 0;
        attackFrameCounter = 0;
        attackHasHit = false;
        currentCooldown = attackCooldown;
        currentState = State.ATTACKING;

        if (isMelee) {
            currentAttackSprites = meleeAttackSprites;
            currentAttackFrameDelay = MELEE_ATTACK_FRAME_DELAY;
            currentAttackHitFrame = MELEE_ATTACK_HIT_FRAME;
        } else {
            currentAttackSprites = rangedAttackSprites;
            currentAttackFrameDelay = RANGED_ATTACK_FRAME_DELAY;
            currentAttackHitFrame = RANGED_ATTACK_HIT_FRAME;
        }

        if (currentAttackSprites != null && currentAttackHitFrame >= currentAttackSprites.length) {
             System.out.println("!!! WARNING DEADEYE: HitFrame (" + currentAttackHitFrame + ") invalid for attack (" + currentAttackSprites.length + " frames) !!!");
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
                    if (currentAttackSprites == meleeAttackSprites) {
                        slash(null);
                    } else {
                        shootProjectile(currentTarget);
                    }
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
                    if (idleSprites != null && idleSprites.length > 0) { 
                        setImage(idleSprites[idleFrame]); 
                    }
                }
                break;
            case RUNNING:
                runFrameCounter++;
                if (runFrameCounter >= runFrameDelay) {
                    runFrameCounter = 0;
                    runFrame = (runFrame + 1) % runSprites.length;
                    if (runSprites != null && runSprites.length > 0) { 
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

    private boolean isEnemyFaction(Character target) {
        if (target == null) return false;
        return this.getFaction() != target.getFaction();
    }

    private Character findTarget() {
        if (getWorld() == null) return null;
        List<Character> charactersInRange = getObjectsInRange(RANGED_ATTACK_RANGE, Character.class);
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
    }

    @Override
    public void shootProjectile(Character target) {
        if (getWorld() == null) return;
        int spawnX = getX();
        int spawnY = getY() + 10;
        DeadeyeArrow projectile = new DeadeyeArrow(this, PROJECTILE_SPEED, this.attackDamage);
        getWorld().addObject(projectile, spawnX, spawnY);
    }

    @Override
    public void slash(Character target) {
        if (getWorld() == null) return;
        int hitboxOffsetX = (facingRight ? 30 : -30);
        int spawnX = getX() + hitboxOffsetX;
        int spawnY = getY();
        AttackHitbox hitbox = new AttackHitbox(this, this.attackDamage, 5); 
        getWorld().addObject(hitbox, spawnX, spawnY);
    }

    @Override
    public int getRewardValue() {
        return REWARD_VALUE;
    }
}