import greenfoot.*;  

public class FactionSelectionWorld extends World
{
    
    public FactionSelectionWorld()
    {
        
        super(1030, 590, 1); 

        
        GreenfootImage worldBg = new GreenfootImage(getWidth(), getHeight());
        worldBg.setColor(Color.BLACK); 
        worldBg.fill();

        
        GreenfootImage originalBg = new GreenfootImage("bgpilihfaction.jpg");

        
        double worldRatio = (double)getWidth() / getHeight(); 
        double imageRatio = (double)originalBg.getWidth() / originalBg.getHeight(); 

        int scaledWidth;
        int scaledHeight;

        if (imageRatio > worldRatio) { 
            scaledWidth = getWidth();
            scaledHeight = (int)(getWidth() / imageRatio);
        } else { 
            scaledHeight = getHeight();
            scaledWidth = (int)(getHeight() * imageRatio);
        }

        
        originalBg.scale(scaledWidth, scaledHeight);

        
        int drawX = (getWidth() - scaledWidth) / 2;
        int drawY = (getHeight() - scaledHeight) / 2; 

        
        worldBg.drawImage(originalBg, drawX, drawY);

        
        setBackground(worldBg);

        
        
        int buttonX = getWidth() / 2;     
        int startY = 285; 
        int spacingY = 95;  

        addObject(new FactionButton("Light", Character.Faction.LIGHT), buttonX, startY);
        addObject(new FactionButton("Dark", Character.Faction.DARK), buttonX, startY + spacingY);
        
        addObject(new BackButton(new DifficultySelectionWorld()), 60, 40);
        
        LegendOfElkaidu.playMenuMusic();
        
        setPaintOrder(BackButton.class, FactionButton.class);
    }
}