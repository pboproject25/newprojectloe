import greenfoot.*;  

public class StartButton extends Actor
{
    public StartButton() {
        int buttonWidth = 205; 
        int buttonHeight = 60; 
        GreenfootImage transparentButton = new GreenfootImage(buttonWidth, buttonHeight);
        setImage(transparentButton); 
    }

    
    public void act() 
    {
        if (Greenfoot.mouseClicked(this)) {
            
            Greenfoot.setWorld(new DifficultySelectionWorld()); 
        }
    }  
}