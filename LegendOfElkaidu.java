import greenfoot.*;

public class LegendOfElkaidu extends World
{
    public static String selectedDifficulty = "Normal"; 
    public static Character.Faction playerFaction = Character.Faction.LIGHT;
    private static GreenfootSound menuMusic;

    
    public static void playMenuMusic() {
        
        if (menuMusic == null || !menuMusic.isPlaying()) {
            
            
            
            try {
                menuMusic = new GreenfootSound("mainmenu_theme.mp3"); 
                menuMusic.setVolume(70); 
                menuMusic.playLoop(); 
            } catch (Exception e) {
                System.err.println("Peringatan: Gagal memuat musik menu.");
            }
        }
        
        
        
    }

    
    public static void stopMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
    }
    

    
    public LegendOfElkaidu() {   
        this(1030, 590, 1);
    }
    
    public LegendOfElkaidu(int width, int height, int cellSize) {
        super(width, height, cellSize); 
    }
}