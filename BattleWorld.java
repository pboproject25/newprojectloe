import greenfoot.*;
import java.util.List;
import java.util.Random;
import java.awt.Color; 

public class BattleWorld extends World {
    private static final int SCREEN_WIDTH = 1030;
    private static final int SCREEN_HEIGHT = 590;
    private static final int MAP_WIDTH = 1696;
    private static final int MAP_HEIGHT = 608;
    public static final int BASE_MAP_EDGE_OFFSET = 80;
    public static final int SPAWN_OFFSET = 170;
    private static final int SCROLL_SPEED = 5;
    
    private static final int BASE_Y_OFFSET_EASY = 80;
    private static final int BASE_Y_OFFSET_NORMAL = 92;
    private static final int BASE_Y_OFFSET_HARD = 55;

    private GreenfootImage fullMapImage;
    private int scrollX = 0;

    private int playerGold = 250; 
    private int goldTimer = 0;
    private int goldInterval = 75;
    private int goldAmountPerInterval = 8; 

    private GoldCounter goldDisplay;
    private Base playerBase;
    private Base enemyBase;
    private int playerBaseMapX, playerBaseMapY, enemyBaseMapX, enemyBaseMapY;
    private Character.Faction aiFaction;
    private Random random = new Random();
    private GreenfootSound themeMusic;

    private int currentWave = 0; 
    private int waveTimer = 0;
    private int waveDuration = 1800; 
    private int timeRemainingInSeconds = 0;
    private WaveDisplay waveDisplay;
    
    private boolean isSpawning = false;
    private int enemiesToSpawn = 0;
    private int swarmSpawnTimer = 0;
    private static final int SWARM_SPAWN_INTERVAL = 15; 
    
    private boolean isPaused = false;
    private PauseButton pauseButton;
    private PauseMenuButton resumeButton;
    private PauseMenuButton retryButton;
    private PauseMenuButton menuButton;
    
    public BattleWorld() {
        super(SCREEN_WIDTH, SCREEN_HEIGHT, 1, false); 
        LegendOfElkaidu.stopMenuMusic(); 

        Character.Faction playerFaction = LegendOfElkaidu.playerFaction;
        aiFaction = (playerFaction == Character.Faction.LIGHT) ? Character.Faction.DARK : Character.Faction.LIGHT;

        String mapFileName;
        int currentBaseYOffset = 0;
        switch (LegendOfElkaidu.selectedDifficulty) {
            case "Easy":
                mapFileName = "EASY.jpg";
                currentBaseYOffset = BASE_Y_OFFSET_EASY;
                break;
            case "Hard":
                mapFileName = "HARD.jpg";
                currentBaseYOffset = BASE_Y_OFFSET_HARD;
                break;
            case "Normal":
            default:
                mapFileName = "NORMAL.jpg";
                currentBaseYOffset = BASE_Y_OFFSET_NORMAL;
                break;
        }
        try {
            fullMapImage = new GreenfootImage(mapFileName);
            fullMapImage.scale(MAP_WIDTH, MAP_HEIGHT);
        } catch (IllegalArgumentException e) {
            System.err.println("Error memuat gambar map: " + mapFileName + " - Menggunakan background default.");
            fullMapImage = new GreenfootImage(MAP_WIDTH, MAP_HEIGHT);
            fullMapImage.setColor(new greenfoot.Color(50, 20, 20));
            fullMapImage.fill();
        }

        playerBaseMapY = MAP_HEIGHT / 2 + currentBaseYOffset;
        enemyBaseMapY = MAP_HEIGHT / 2 + currentBaseYOffset;
        if (playerFaction == Character.Faction.LIGHT) {
            playerBaseMapX = BASE_MAP_EDGE_OFFSET;
            enemyBaseMapX = MAP_WIDTH - BASE_MAP_EDGE_OFFSET;
            scrollX = 0;
        } else {
            playerBaseMapX = MAP_WIDTH - BASE_MAP_EDGE_OFFSET;
            enemyBaseMapX = BASE_MAP_EDGE_OFFSET;
            scrollX = MAP_WIDTH - SCREEN_WIDTH;
        }

        playerBase = new Base(true, playerFaction);
        enemyBase = new Base(false, playerFaction);
        addObject(playerBase, playerBaseMapX - scrollX, playerBaseMapY);
        addObject(enemyBase, enemyBaseMapX - scrollX, enemyBaseMapY);

        goldDisplay = new GoldCounter();
        addObject(goldDisplay, 100, 30);
        goldDisplay.updateDisplay(playerGold); 
        waveDisplay = new WaveDisplay();
        addObject(waveDisplay, SCREEN_WIDTH / 2, 35);

        pauseButton = new PauseButton();
        addObject(pauseButton, SCREEN_WIDTH - 40, 35);

        resumeButton = new PauseMenuButton("Resume");
        retryButton = new PauseMenuButton("Retry");
        menuButton = new PauseMenuButton("Main Menu");

        prepareSummonButtons(); 
        startNextWave(); 
        
        setPaintOrder(
            PauseMenuButton.class, 
            PauseButton.class,
            BackButton.class,
            WaveDisplay.class, 
            GoldCounter.class, 
            SummonButton.class
        );

        updateBackgroundScroll();
        try {
            themeMusic = new GreenfootSound("battleworld_theme.mp3"); 
            themeMusic.setVolume(70);
            themeMusic.playLoop();
        } catch (Exception e) {
            System.err.println("Peringatan: Gagal memuat file musik pertempuran.");
        }
    }
    
    public boolean isPaused() {
        return isPaused;
    }

    public void showPauseMenu() {
        isPaused = true;
        addObject(resumeButton, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 - 60);
        addObject(retryButton, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        addObject(menuButton, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 + 60);
        if (themeMusic != null) {
            themeMusic.pause();
        }
    }

    public void hidePauseMenu() {
        isPaused = false;
        removeObject(resumeButton);
        removeObject(retryButton);
        removeObject(menuButton);
        if (themeMusic != null) {
            themeMusic.play();
        }
    }

    public void act() {
        if (isPaused) return;

        int oldScrollX = scrollX;
        handleScrollingInput();
        updateBackgroundScroll();

        generateGold();
        handleSpawning();
        runWaveTimer();
        checkWaveEndConditions(); 
        checkWinLossCondition(); 

        if (scrollX != oldScrollX) {
             int scrollDelta = oldScrollX - scrollX;
             adjustActorPositions(scrollDelta);
        }
    }

    private void handleScrollingInput() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            if (mouse.getX() < 50) scrollX -= SCROLL_SPEED;
            else if (mouse.getX() > SCREEN_WIDTH - 50) scrollX += SCROLL_SPEED;
        }
        if (Greenfoot.isKeyDown("a") || Greenfoot.isKeyDown("left")) scrollX -= SCROLL_SPEED;
        if (Greenfoot.isKeyDown("d") || Greenfoot.isKeyDown("right")) scrollX += SCROLL_SPEED;
        scrollX = Math.max(0, scrollX);
        scrollX = Math.min(MAP_WIDTH - SCREEN_WIDTH, scrollX);
    }
    private void updateBackgroundScroll() {
        if (fullMapImage == null) return;
        GreenfootImage bg = getBackground();
        bg.setColor(greenfoot.Color.BLACK);
        bg.fill();
        int drawY = (MAP_HEIGHT > SCREEN_HEIGHT) ? (MAP_HEIGHT - SCREEN_HEIGHT) / 2 : 0;
        bg.drawImage(fullMapImage, -scrollX, -drawY);
    }
    private void adjustActorPositions(int scrollDelta) {
         List<Actor> allActors = getObjects(Actor.class);
         for (Actor actor : allActors) {
             if (!(actor instanceof GoldCounter || actor instanceof WaveDisplay || actor instanceof SummonButton || actor instanceof PauseButton || actor instanceof PauseMenuButton)) {
                 if (actor.getWorld() != null) {
                     actor.setLocation(actor.getX() + scrollDelta, actor.getY());
                 }
             }
         }
     }

    public int getPlayerBaseMapX() { return playerBaseMapX; }
    public int getEnemyBaseMapX() { return enemyBaseMapX; }
    public int getBaseMapY() { return enemyBaseMapY; }
    public int getCurrentScrollX() { return scrollX; }
    
    private void handleSpawning() {
        if (isSpawning && enemiesToSpawn > 0) {
            swarmSpawnTimer++;
            if (swarmSpawnTimer >= SWARM_SPAWN_INTERVAL) { 
                spawnEnemyUnit(); 
                enemiesToSpawn--; 
                swarmSpawnTimer = 0;
            }
        } 
        else if (enemiesToSpawn == 0 && isSpawning) {
            isSpawning = false; 
            System.out.println("Spawn untuk Wave " + currentWave + " selesai. Fase bertarung dimulai.");
        }
    }
    private void spawnEnemyUnit() {
        Character newEnemy = null;
        boolean facingRightAI = (aiFaction == Character.Faction.LIGHT);
        int rand;
        if (aiFaction == Character.Faction.DARK) {
            if (currentWave == 1) { newEnemy = random.nextBoolean() ? new Oniro(facingRightAI) : new Kunoichi(facingRightAI); } 
            else if (currentWave == 2) {
                rand = random.nextInt(3); 
                if (rand == 0) { newEnemy = new Oniro(facingRightAI); }
                else if (rand == 1) { newEnemy = new Kunoichi(facingRightAI); }
                else { newEnemy = new Pyro(facingRightAI); }
            } 
            else { 
                rand = random.nextInt(100); 
                if (rand < 20) { newEnemy = new Oniro(facingRightAI); }
                else if (rand < 40) { newEnemy = new Kunoichi(facingRightAI); }
                else if (rand < 55) { newEnemy = new Pyro(facingRightAI); }
                else if (rand < 70) { newEnemy = new Deadeye(facingRightAI); }
                else if (rand < 80) { newEnemy = new ZipZip(facingRightAI); }
                else if (rand < 90) { newEnemy = new Kitsune(facingRightAI); }
                else { newEnemy = new Masamune(facingRightAI); }
            }
        } else {
            if (currentWave == 1) { newEnemy = random.nextBoolean() ? new Baldwin(facingRightAI) : new Aegis(facingRightAI); }
            else if (currentWave == 2) {
                rand = random.nextInt(3);
                if (rand == 0) { newEnemy = new Baldwin(facingRightAI); }
                else if (rand == 1) { newEnemy = new Aegis(facingRightAI); }
                else { newEnemy = new Wanderer(facingRightAI); }
            }
            else { 
                rand = random.nextInt(100);
                if (rand < 15) { newEnemy = new Baldwin(facingRightAI); }
                else if (rand < 30) { newEnemy = new Aegis(facingRightAI); }
                else if (rand < 45) { newEnemy = new Wanderer(facingRightAI); }
                else if (rand < 60) { newEnemy = new TheChosenOne(facingRightAI); }
                else if (rand < 70) { newEnemy = new Hayato(facingRightAI); }
                else if (rand < 80) { newEnemy = new Renshiro(facingRightAI); }
                else if (rand < 90) { newEnemy = new YunZhao(facingRightAI); }
                else { newEnemy = new BarbarianKing(facingRightAI); }
            }
        }
        if (newEnemy != null) {
            int spawnMapX = enemyBaseMapX + (facingRightAI ? BattleWorld.SPAWN_OFFSET : -BattleWorld.SPAWN_OFFSET);
            int baseY = getBaseMapY();
            int randomOffsetY = random.nextInt(60) - 30;
            int spawnMapY = baseY + randomOffsetY;
            addObject(newEnemy, spawnMapX - scrollX, spawnMapY);
        }
    }

    public boolean isPathBlocked(Character asker, int targetMapX, int targetMapY, Character.Faction targetFaction, int targetWidth) {
         if (asker == null) return false;
         List<Character> characters = getObjects(Character.class);
         int halfWidth = targetWidth / 2;
         int askerMapX = asker.getX() + scrollX; 
         for (Character other : characters) {
             if (other != null && other != asker && other.getFaction() == targetFaction && other.isAlive()) {
                 int otherMapX = other.getX() + scrollX;
                 int otherMapY = other.getY();
                 int otherHalfWidth = (other.getImage() != null) ? other.getImage().getWidth() / 2 : halfWidth;
                 if (Math.abs(askerMapX - otherMapX) < (halfWidth + otherHalfWidth) * 0.7 &&
                     Math.abs(targetMapY - otherMapY) < 20) {
                     if (asker.facingRight && otherMapX > askerMapX && otherMapX < targetMapX + halfWidth ) {
                         return true;
                     } else if (!asker.facingRight && otherMapX < askerMapX && otherMapX > targetMapX - halfWidth) {
                         return true;
                     }
                 }
             }
         }
         return false;
     }

    private void generateGold() { goldTimer++; if (goldTimer >= goldInterval) { playerGold += goldAmountPerInterval; if (goldDisplay != null) { goldDisplay.updateDisplay(playerGold); } goldTimer = 0; } }
    
    private void checkWinLossCondition() {
        if (enemyBase != null && enemyBase.getWorld() != null && !enemyBase.isAlive()) {
            stopMusic();
            Greenfoot.setWorld(new VictoryWorld());
        } else if (playerBase != null && playerBase.getWorld() != null && !playerBase.isAlive()) {
            stopMusic();
            Greenfoot.setWorld(new GameOverWorld());
        }
    }
    
    public int getPlayerGold() { return playerGold; }
    public void spendGold(int amount) { if (playerGold >= amount) { playerGold -= amount; if (goldDisplay != null) { goldDisplay.updateDisplay(playerGold); } } }
    public void addGold(int amount) { if (amount > 0) { playerGold += amount; if (goldDisplay != null) goldDisplay.updateDisplay(playerGold); } }
    public void stopMusic() { if (themeMusic != null) { themeMusic.stop(); } }

    private void prepareSummonButtons() {
        Character.Faction pf = LegendOfElkaidu.playerFaction;
        int buttonY = SCREEN_HEIGHT - 50; 
        int buttonSpacing = 100;
        removeObjects(getObjects(SummonButton.class));
        
        int numButtons;
        int totalWidth;
        int startX;

        if (pf == Character.Faction.LIGHT){
           numButtons = 8;
           totalWidth = (numButtons - 1) * buttonSpacing;
           startX = (SCREEN_WIDTH - totalWidth) / 2;
           
           addObject(new SummonButton("Baldwin", 30, "icon_baldwin.png"), startX, buttonY);
           addObject(new SummonButton("Aegis", 45, "icon_aegis.png"), startX + buttonSpacing, buttonY);
           addObject(new SummonButton("Wanderer", 75, "icon_wanderer.png"), startX + buttonSpacing * 2, buttonY);
           addObject(new SummonButton("TheChosenOne", 85, "icon_chosen.png"), startX + buttonSpacing * 3, buttonY);
           addObject(new SummonButton("Hayato", 90, "icon_hayato.png"), startX + buttonSpacing * 4, buttonY);
           addObject(new SummonButton("YunZhao", 90, "icon_yunzhao.png"), startX + buttonSpacing * 5, buttonY);
           addObject(new SummonButton("Renshiro", 110, "icon_renshiro.png"), startX + buttonSpacing * 6, buttonY);
           addObject(new SummonButton("BarbarianKing", 135, "icon_barbarian.png"), startX + buttonSpacing * 7, buttonY);
        } else {
           numButtons = 7;
           totalWidth = (numButtons - 1) * buttonSpacing;
           startX = (SCREEN_WIDTH - totalWidth) / 2;
           
           addObject(new SummonButton("Deadeye", 40, "icon_deadeye.png"), startX, buttonY);
           addObject(new SummonButton("Kunoichi", 90, "icon_kunoichi.png"), startX + buttonSpacing, buttonY);
           addObject(new SummonButton("Pyro", 105, "icon_pyro.png"), startX + buttonSpacing * 2, buttonY);
           addObject(new SummonButton("Kitsune", 120, "icon_kitsune.png"), startX + buttonSpacing * 3, buttonY); 
           addObject(new SummonButton("ZipZip", 120, "icon_zipzip.png"), startX + buttonSpacing * 4, buttonY); 
           addObject(new SummonButton("Oniro", 125, "icon_oniro.png"), startX + buttonSpacing * 5, buttonY); 
           addObject(new SummonButton("Masamune", 135, "icon_masamune.png"), startX + buttonSpacing * 6, buttonY);
        }
    }
    
    private void runWaveTimer() {
        if (!isSpawning) { waveTimer++; }
        if (waveTimer % 60 == 0) { updateWaveDisplay(); }
    }
    
    private void checkWaveEndConditions() {
        if (isSpawning) { 
            return; 
        }
        if (playerBase == null || !playerBase.isAlive() || enemyBase == null || !enemyBase.isAlive()) { 
            return; 
        }
        if (waveTimer >= waveDuration) {
            System.out.println("Wave " + currentWave + " berakhir (Waktu Habis).");
            endCurrentWave();
            return;
        }
    }
    
    private int getLivingEnemyCount() {
        List<Character> allActors = getObjects(Character.class);
        int count = 0;
        for (Character c : allActors) {
            if (c.getFaction() == aiFaction && c.isAlive() && !(c instanceof Base)) {
                count++;
            }
        }
        return count;
    }

    private void endCurrentWave() {
        System.out.println("Wave " + currentWave + " telah selesai!");
        startNextWave(); 
    }

    private void startNextWave() {
        currentWave++; 
        waveTimer = 0; 
        
        try {
            GreenfootSound hornSound = new GreenfootSound("war_horn.wav");
            hornSound.setVolume(85); 
            hornSound.play();
            
            GreenfootSound screamSound = new GreenfootSound("crowd_screaming.mp3");
            screamSound.setVolume(75); 
            screamSound.play();
            
        } catch (Exception e) {
            System.err.println("Peringatan: Gagal memuat file suara 'war_horn.wav' atau 'crowd_screaming.mp3'.");
        }
        
        if (currentWave > 1) {
            int bonusGold = 100 + (currentWave * 25); 
            playerGold += bonusGold;
            System.out.println("Wave " + (currentWave - 1) + " selesai. Bonus Gold: +" + bonusGold);
            if (goldDisplay != null) goldDisplay.updateDisplay(playerGold);
        }
        
        updateWaveDisplay();
        
        enemiesToSpawn = 5 + (currentWave * 2); 
        swarmSpawnTimer = 0;
        isSpawning = true;
        System.out.println("Memulai Wave " + currentWave + ", akan spawn " + enemiesToSpawn + " unit.");
    }
    
    private void updateWaveDisplay() {
        if (waveDisplay != null && waveDisplay.getWorld() != null) {
            if (isSpawning) {
                timeRemainingInSeconds = waveDuration / 60;
            } else {
                timeRemainingInSeconds = Math.max(0, (waveDuration - waveTimer) / 60);
            }
            waveDisplay.updateDisplay(currentWave, timeRemainingInSeconds);
        }
    }
    
}