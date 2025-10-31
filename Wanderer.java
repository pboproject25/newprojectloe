import greenfoot.*;
import java.util.List;

public class Wanderer extends Character implements Movable, Attackable, Rewardable, MeleeUnit {

    private enum State { IDLE, RUNNING, ATTACKING, DYING }
    private State currentState = State.RUNNING;
    private Character currentTarget = null;
    private GreenfootImage[] idleSprites, runSprites, dieSprites, attack1Sprites, attack2Sprites;
    private int idleFrame = 0, idleFrameDelay = 8, idleFrameCounter = 0;
    private int runFrame = 0, runFrameDelay = 6, runFrameCounter = 0;
    private int dieFrame = 0, dieFrameDelay = 6, dieFrameCounter = 0;
    private int attackFrame = 0, attackFrameCounter = 0;
    private boolean attackHasHit = false;
    private GreenfootImage[] currentAttackSprites;
    private int currentAttackFrameDelay;
    private int currentAttackHitFrame;
    private static final int ATTACK_1_FRAME_DELAY = 5, ATTACK_1_HIT_FRAME = 3;
    private static final int ATTACK_2_FRAME_DELAY = 7, ATTACK_2_HIT_FRAME = 2;
    private static final int MAX_HEALTH = 100;
    private static final int ATTACK_DAMAGE = 22;
    private static final double ATTACK_SPEED_SECONDS = 1.0;
    private static final int ATTACK_RANGE = 5;
    private static final int MOVE_SPEED = 2;
    private static final int REWARD_VALUE = 18;
    private int attackCooldown = (int)(ATTACK_SPEED_SECONDS * 60);
    private int currentCooldown = 0;

    public Wanderer(boolean facingRight) {
        super(MAX_HEALTH, ATTACK_DAMAGE, ATTACK_RANGE, MOVE_SPEED, facingRight, Faction.LIGHT);
        try {
            GreenfootSound spawnSound = new GreenfootSound("WandererSpawn.wav");
            spawnSound.setVolume(75);
            spawnSound.play();
        } catch (Exception e) {
            System.err.println("Peringatan: Gagal memuat suara spawn untuk " + this.getClass().getName());
        }
        idleSprites = SpriteLoader.loadSprites("light/wanderer_idle.png", 6);
        runSprites = SpriteLoader.loadSprites("light/wanderer_run.png", 6);
        dieSprites = SpriteLoader.loadSprites("light/wanderer_die.png", 4);
        attack1Sprites = SpriteLoader.loadSprites("light/wanderer_attack1.png", 6);
        attack2Sprites = SpriteLoader.loadSprites("light/wanderer_attack2.png", 4);
        if (!this.facingRight) {
            flipAllSprites();
        }
        setImage(runSprites[0]);
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
        if (getWorld() instanceof BattleWorld && ((BattleWorld)getWorld()).isPaused()) {
            return; 
        }
        
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
            GreenfootSound dieSound = new GreenfootSound("Wandererdie.wav");
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
             System.out.println("!!! WARNING Wanderer: HitFrame invalid !!!");
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
                    try {
                        String attackSoundFile = (currentAttackSprites == attack1Sprites) ? "WandererAttack.wav" : "WandererAttack.wav";
                        GreenfootSound attackSound = new GreenfootSound(attackSoundFile);
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
        
        int targetRadius = 0;
        if (target instanceof Character) {
            targetRadius = ((Character)target).getHitboxRadius();
        } else if (target.getImage() != null) {
            targetRadius = target.getImage().getWidth() / 2;
        }
        
        int ourRadius = this.getHitboxRadius();
        int dx = Math.abs(this.getX() - target.getX());
        
        int gap = dx - targetRadius - ourRadius;
        
        return gap <= this.attackRange;
    }

    private boolean isEnemyFaction(Character target) {
        if (target == null) return false;
        return this.getFaction() != target.getFaction();
    }

     private Character findTarget() {
        if (getWorld() == null) return null;
        List<Character> charactersInRange = getObjectsInRange(9999, Character.class);
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
        slash(target);
    }

    @Override
    public void slash(Character target) {
        if (getWorld() == null) return;
        int hitboxOffsetX = (facingRight ? 30 : -30);
        int spawnX = getX() + hitboxOffsetX;
        int spawnY = getY();
        AoeSlashHitbox hitbox = new AoeSlashHitbox(this, this.attackDamage, 5);
        getWorld().addObject(hitbox, spawnX, spawnY);
    }

    @Override
    public int getRewardValue() {
        return REWARD_VALUE;
    }
}