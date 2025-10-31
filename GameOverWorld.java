import greenfoot.*; 

public class GameOverWorld extends World 
{
    public GameOverWorld()
    {
        super(1030, 590, 1);

        GreenfootImage worldBg = new GreenfootImage(getWidth(), getHeight());
        worldBg.setColor(Color.BLACK); 
        worldBg.fill();
        
        GreenfootImage originalBg = new GreenfootImage("gameover.jpg"); 
        
        originalBg.scale(getWidth(), getHeight());
        
        worldBg.drawImage(originalBg, 0, 0);
        setBackground(worldBg);

        addObject(new ReturnToMenuButton(), getWidth() / 2, getHeight() - 80); 

        try {
            GreenfootSound defeatSound = new GreenfootSound("defeat_theme.mp3");
            defeatSound.setVolume(80); 
            defeatSound.play(); 
        } catch (Exception e) {
            System.err.println("Peringatan: Gagal memuat suara kekalahan.");
        }
    }
}