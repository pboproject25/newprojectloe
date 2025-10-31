import greenfoot.*;  

public class MainMenu extends World 
{
    
    public MainMenu()
    {   
        super(1030, 590, 1); 
        setBackground("MainMenuLegendOfElkaidu.jpg");        
        
        StartButton startBtn = new StartButton();
        addObject(startBtn, getWidth() / 2 + 5, getHeight() / 2 + 5); 
        
        CreditButton creditBtn = new CreditButton();
        addObject(creditBtn, getWidth() / 2 + 5, getHeight() / 2 + 75);
        
        LegendOfElkaidu.playMenuMusic();
        
        setPaintOrder(StartButton.class, CreditButton.class);
    }
}