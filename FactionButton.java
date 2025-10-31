import greenfoot.*;  

public class FactionButton extends Actor
{
    
    private Character.Faction factionToSelect;
    
    public FactionButton(String label, Character.Faction faction) {
        
        this.factionToSelect = faction;
        int buttonWidth = 200; 
        int buttonHeight = 65; 

        GreenfootImage transparentButton = new GreenfootImage(buttonWidth, buttonHeight);
        
        setImage(transparentButton); 
    }

    
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            
            LegendOfElkaidu.playerFaction = this.factionToSelect;
            Greenfoot.setWorld(new BattleWorld());
        }
    }
}