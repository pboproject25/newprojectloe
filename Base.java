import greenfoot.*;

public class Base extends Character {
    
    private static final int BASE_MAX_HEALTH = 1000;
    private static final int BASE_WIDTH = 300;
    private static final int BASE_HEIGHT = 260;

    private GreenfootImage fullHealthSprite;
    private GreenfootImage damagedSprite;
    private GreenfootImage destroyedSprite;
    
    private int currentState; 

    public Base(boolean isPlayerBase, Character.Faction playerFaction) {
        super(BASE_MAX_HEALTH, 0, 0, 0, true, determineFaction(isPlayerBase, playerFaction));
        
        String fullHealthPath;
        String damagedPath;
        String destroyedPath;

        if (getFaction() == Character.Faction.LIGHT) {
            fullHealthPath = "Light_Full.png";
            damagedPath = "Light_Half.png";
            destroyedPath = "Light_Break.png";
        } else {
            fullHealthPath = "Dark_Full.png";
            damagedPath = "Dark_Half.png";
            destroyedPath = "Dark_Break.png";
        }

        try {
            fullHealthSprite = new GreenfootImage(fullHealthPath);
            fullHealthSprite.scale(BASE_WIDTH, BASE_HEIGHT);
        } catch (Exception e) {
            System.err.println("Gagal memuat sprite Base: " + fullHealthPath);
            fullHealthSprite = createPlaceholderImage();
        }
        
        try {
            damagedSprite = new GreenfootImage(damagedPath);
            damagedSprite.scale(BASE_WIDTH, BASE_HEIGHT);
        } catch (Exception e) {
            System.err.println("Gagal memuat sprite Base: " + damagedPath);
            damagedSprite = createPlaceholderImage();
        }

        try {
            destroyedSprite = new GreenfootImage(destroyedPath);
            destroyedSprite.scale(BASE_WIDTH, BASE_HEIGHT);
        } catch (Exception e) {
            System.err.println("Gagal memuat sprite Base: " + destroyedPath);
            destroyedSprite = createPlaceholderImage();
        }

        if (getFaction() == Character.Faction.LIGHT) {
            fullHealthSprite.mirrorHorizontally();
            damagedSprite.mirrorHorizontally();
            destroyedSprite.mirrorHorizontally();
        }

        this.currentState = 1;
        setImage(fullHealthSprite); 
    }

    private GreenfootImage createPlaceholderImage() {
        GreenfootImage img = new GreenfootImage(BASE_WIDTH, BASE_HEIGHT);
        img.setColor(Color.RED);
        img.fill();
        return img;
    }

    private static Character.Faction determineFaction(boolean isPlayerBase, Character.Faction playerFaction) {
        if (isPlayerBase) return playerFaction;
        else return (playerFaction == Character.Faction.LIGHT) ? Character.Faction.DARK : Character.Faction.LIGHT;
    }

    @Override
    public void act() {
        super.act(); 
        updateImage(); 
    }

    private void updateImage() {
        if (currentHealth <= 0) {
            if (currentState != 3) {
                setImage(destroyedSprite);
                currentState = 3;
            }
        } else if (currentHealth <= maxHealth / 2) {
            if (currentState != 2) {
                setImage(damagedSprite);
                currentState = 2;
            }
        } else {
            if (currentState != 1) {
                setImage(fullHealthSprite);
                currentState = 1;
            }
        }
    }

    @Override
    public void die() {
        
    }
    
    @Override
    public int getHitboxRadius() {
        return BASE_WIDTH / 2;
    }
}