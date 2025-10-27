import greenfoot.*;
import java.util.List;
import java.util.Comparator;

public class Renshiro extends Character implements Movable, Attackable, Rewardable, RangedUnit {

    private enum State { IDLE, RUNNING, ATTACKING, DYING }
    private State currentState = State.RUNNING;
    private Character currentTarget = null;

    private GreenfootImage[] idleSprites, runSprites, dieSprites, attackSprites;

    private int idleFrame = 0, idleFrameDelay = 8, idleFrameCounter = 0;
    private int runFrame = 0, runFrameDelay = 6, runFrameCounter = 0;
    private int dieFrame = 0, dieFrameDelay = 8, dieFrameCounter = 0; 
    private int attackFrame = 0, attackFrameCounter = 0;
    private boolean attackHasHit = false;

    private GreenfootImage[] currentAttackSprites;
    private int currentAttackFrameDelay;
    private int currentAttackHitFrame;

    // Animasi panah 
    private static final int ATTACK_FRAME_DELAY = 5; 
    private static final int ATTACK_HIT_FRAME = 8;  

    // Statistik
    private static final int MAX_HEALTH = 100;
    private static final int ATTACK_DAMAGE = 25;
    private static final double ATTACK_SPEED_SECONDS = 1.8;
    private static final int ATTACK_RANGE = 350; 
    private static final int MOVE_SPEED = 1;    
    private static final int REWARD_VALUE = 20;

    private int attackCooldown = (int)(ATTACK_SPEED_SECONDS * 60);
    private int currentCooldown = 0;

    public Renshiro(boolean facingRight) {
        super(MAX_HEALTH, ATTACK_DAMAGE, ATTACK_RANGE, MOVE_SPEED, facingRight, Faction.LIGHT);

        idleSprites = SpriteLoader.loadSprites("light/renshiro_idle.png", 9);   
        runSprites = SpriteLoader.loadSprites("light/renshiro_walk.png", 8);      
        dieSprites = SpriteLoader.loadSprites("light/renshiro_dead.png", 5);      
        attackSprites = SpriteLoader.loadSprites("light/renshiro_shot.png", 14);    

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
        for (GreenfootImage img : attackSprites) { img.mirrorHorizontally(); }
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
             System.out.println("!!! WARNING Renshiro: HitFrame invalid !!!");
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
        int spawnY = getY() + 10; 
        int projectileSpeed = 7; 

        RenshiroArrow projectile = new RenshiroArrow(this, projectileSpeed, this.attackDamage);

        getWorld().addObject(projectile, spawnX, spawnY);
    }

    @Override
    public int getRewardValue() {
        return REWARD_VALUE;
    }
}