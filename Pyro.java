import greenfoot.*;
import java.util.List;

public class Pyro extends Character implements Movable, Attackable, Rewardable, RangedUnit {

    private enum State { IDLE, RUNNING, ATTACKING, DYING }
    private State currentState = State.RUNNING;
    private Character currentTarget = null;
    private GreenfootImage[] idleSprites, runSprites, dieSprites, attackSprites;
    private int idleFrame = 0, idleFrameDelay = 9, idleFrameCounter = 0;
    private int runFrame = 0, runFrameDelay = 7, runFrameCounter = 0;
    private int dieFrame = 0, dieFrameDelay = 7, dieFrameCounter = 0;
    private int attackFrame = 0, attackFrameCounter = 0;
    private boolean attackHasHit = false;
    private GreenfootImage[] currentAttackSprites;
    private int currentAttackFrameDelay;
    private int currentAttackHitFrame;
    private static final int ATTACK_FRAME_DELAY = 6;
    private static final int ATTACK_HIT_FRAME = 4;
    private static final int MAX_HEALTH = 85;
    private static final int ATTACK_DAMAGE = 25;
    private static final double ATTACK_SPEED_SECONDS = 1.3;
    private static final int ATTACK_RANGE = 320;
    private static final int MOVE_SPEED = 1;
    private static final int REWARD_VALUE = 18;
    private int attackCooldown = (int)(ATTACK_SPEED_SECONDS * 60);
    private int currentCooldown = 0;

    public Pyro(boolean facingRight) {
        super(MAX_HEALTH, ATTACK_DAMAGE, ATTACK_RANGE, MOVE_SPEED, facingRight, Faction.DARK);
        try {
            GreenfootSound spawnSound = new GreenfootSound("Pyro_spawn.wav");
            spawnSound.setVolume(75);
            spawnSound.play();
        } catch (Exception e) {
            System.err.println("Peringatan: Gagal memuat suara spawn untuk " + this.getClass().getName());
        }
        idleSprites = SpriteLoader.loadSprites("dark/pyro_idle.png", 7);
        runSprites = SpriteLoader.loadSprites("dark/pyro_run.png", 6);
        dieSprites = SpriteLoader.loadSprites("dark/pyro_die.png", 6);
        attackSprites = SpriteLoader.loadSprites("dark/pyro_attack.png", 14);
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
        try {
            GreenfootSound dieSound = new GreenfootSound("Pyro_die.wav");
            dieSound.setVolume(80);
            dieSound.play();
        } catch (Exception e) {
             System.err.println("Peringatan: Gagal memuat suara die untuk " + this.getClass().getName());
        }
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
        
        if (currentTarget == null) {
            currentTarget = findTarget();
        }

        if (currentTarget != null) {
            if (isTargetInRange(currentTarget)) {
                if (currentCooldown <= 0) {
                    currentState = State.ATTACKING;
                    setupAttack();
                } else {
                    currentState = State.IDLE;
                }
            } else {
                currentState = State.RUNNING;
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
             System.out.println("!!! WARNING Pyro: HitFrame invalid !!!");
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
                    try {
                        GreenfootSound attackSound = new GreenfootSound("Pyro_attack.wav");
                        attackSound.setVolume(70);
                        attackSound.play();
                    } catch (Exception e) {
                         System.err.println("Peringatan: Gagal memuat suara attack untuk " + this.getClass().getName());
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
        
        int targetRadius = 0;
        if (target instanceof Character) {
            targetRadius = ((Character)target).getHitboxRadius();
        } else if (target.getImage() != null) {
            targetRadius = target.getImage().getWidth() / 2;
        }
        
        int ourRadius = this.getHitboxRadius();
        int dx = Math.abs(this.getX() - target.getX());
        
        // Jarak antara tepi unit dan tepi target
        int gap = dx - targetRadius - ourRadius;
        
        return gap <= this.attackRange;
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
            if (potentialTarget != null &&
                potentialTarget.getWorld() != null &&
                potentialTarget.isAlive() &&
                potentialTarget != this &&
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
        int projectileSpeed = 3;
        PyroFireball projectile = new PyroFireball(this, projectileSpeed, this.attackDamage);
        getWorld().addObject(projectile, spawnX, spawnY);
    }

    @Override
    public int getRewardValue() {
        return REWARD_VALUE;
    }
}