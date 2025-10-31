// File: VictoryWorld.java
import greenfoot.*; 

public class VictoryWorld extends World 
{
    public VictoryWorld()
    {
        super(1030, 590, 1);

        GreenfootImage worldBg = new GreenfootImage(getWidth(), getHeight());
        worldBg.setColor(Color.BLACK); 
        worldBg.fill();
        GreenfootImage originalBg = new GreenfootImage("victory.jpg"); 
        
        originalBg.scale(getWidth(), getHeight());
        
        worldBg.drawImage(originalBg, 0, 0);
        setBackground(worldBg);

        addObject(new ReturnToMenuButton(), getWidth() / 2, getHeight() - 80); 

        try {
            GreenfootSound victorySound = new GreenfootSound("victory_theme.mp3");
            victorySound.setVolume(80); 
            victorySound.play(); 
        } catch (Exception e) {
            System.err.println("Peringatan: Gagal memuat suara kemenangan.");
        }
    }
}