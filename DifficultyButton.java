import greenfoot.*;  

public class DifficultyButton extends Actor
{
    private String difficultyLevel;

    public DifficultyButton(String level) {
        this.difficultyLevel = level;

        int buttonWidth = 220; 
        int buttonHeight = 75; 
    
        GreenfootImage transparentButton = new GreenfootImage(buttonWidth, buttonHeight);        
        setImage(transparentButton); 
    }

    
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            
            LegendOfElkaidu.selectedDifficulty = this.difficultyLevel;
            Greenfoot.setWorld(new FactionSelectionWorld());
        }
    }
}