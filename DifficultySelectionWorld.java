import greenfoot.*;  

public class DifficultySelectionWorld extends World
{
    
    public DifficultySelectionWorld()
    {
        
        super(1030, 590, 1); 

        
        GreenfootImage worldBg = new GreenfootImage(getWidth(), getHeight());
        worldBg.setColor(Color.BLACK); 
        worldBg.fill();

        
        GreenfootImage originalBg = new GreenfootImage("bgpilihdifficulty.jpg");

        
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
        int startY = 290 ; 
        int spacingY = 95;   

        addObject(new DifficultyButton("Easy"), buttonX, startY);
        addObject(new DifficultyButton("Normal"), buttonX, startY + spacingY);
        addObject(new DifficultyButton("Hard"), buttonX, startY + spacingY * 2);
        
        addObject(new BackButton(new MainMenu()), 60, 40);

        LegendOfElkaidu.playMenuMusic();
        
        setPaintOrder(BackButton.class, DifficultyButton.class);
    }
}