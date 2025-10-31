import greenfoot.*;

public abstract class Projectile extends Actor {
    protected int speed;
    protected int damage;
    protected Character owner;

    public Projectile(Character owner, int speed, int damage) {
        this.owner = owner;
        this.speed = speed;
        this.damage = damage;
    }

    public void act() {
        if (getWorld() == null) return;
        if (getWorld() instanceof BattleWorld && ((BattleWorld)getWorld()).isPaused()) {
            return; 
        }
        
        moveForward();
        checkCollision();
    }

    protected void moveForward() {
        move(speed * (owner.facingRight ? 1 : -1));
    }

    protected void checkCollision() {
        Actor target = getOneIntersectingObject(Character.class);
        if (target != null && target != owner) {
            ((Character) target).takeDamage(damage);
            getWorld().removeObject(this);
        }
    }
}