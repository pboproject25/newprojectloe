import greenfoot.*;
import java.util.Random;

public class SummonButton extends Actor {
    private String unitName;   
    private int unitCost;    
    private GreenfootImage buttonImage; 
    private GreenfootImage disabledImage; 
    private boolean canAfford = false; 
    
    private static Random random = new Random();

    public SummonButton(String unitClassName, int cost, String iconFileName) {
        this.unitName = unitClassName;
        this.unitCost = cost;

        
        buttonImage = new GreenfootImage(80, 80); 
        buttonImage.setColor(greenfoot.Color.DARK_GRAY);
        buttonImage.fill();
        buttonImage.setColor(greenfoot.Color.LIGHT_GRAY);
        buttonImage.drawRect(0, 0, buttonImage.getWidth() - 1, buttonImage.getHeight() - 1);

        
        try {
            GreenfootImage icon = new GreenfootImage(iconFileName);
            
            icon.scale(60, 40); 
            buttonImage.drawImage(icon, (buttonImage.getWidth() - icon.getWidth()) / 2, 5); 
        } catch (IllegalArgumentException e) {
            
             greenfoot.Font smallFont = new greenfoot.Font("Arial", true, false, 12);
             buttonImage.setFont(smallFont);
             buttonImage.setColor(greenfoot.Color.WHITE);
             
             GreenfootImage tempName = new GreenfootImage(unitName, 12, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
             tempName.setFont(smallFont);
             int textWidth = tempName.getWidth();
             buttonImage.drawString(unitName, (buttonImage.getWidth() - textWidth) / 2, 25);
             
             System.err.println("Warning: Icon file not found: " + iconFileName);
        }

        
        greenfoot.Font costFont = new greenfoot.Font("Arial", false, false, 16);
        buttonImage.setFont(costFont);
        buttonImage.setColor(greenfoot.Color.YELLOW);
        String costText = "$" + unitCost;
        
        
        GreenfootImage tempTextImg = new GreenfootImage(costText, 16, greenfoot.Color.YELLOW, new greenfoot.Color(0,0,0,0));
        tempTextImg.setFont(costFont);
        int costTextWidth = tempTextImg.getWidth();
        buttonImage.drawString(costText, (buttonImage.getWidth() - costTextWidth) / 2, buttonImage.getHeight() - 10);


        
        disabledImage = new GreenfootImage(buttonImage); 
        disabledImage.setColor(new greenfoot.Color(0, 0, 0, 150)); 
        disabledImage.fillRect(0, 0, disabledImage.getWidth(), disabledImage.getHeight());

        setImage(buttonImage); 
    }

    
    public void act() {
        if (getWorld() == null) return;
        if (getWorld() instanceof BattleWorld && ((BattleWorld)getWorld()).isPaused()) {
            return; 
        }
        
        World world = getWorld();
        if (world == null || !(world instanceof BattleWorld)) return; 

        BattleWorld battleWorld = (BattleWorld) world;
        int currentGold = battleWorld.getPlayerGold();

        
        if (currentGold >= unitCost) {
            if (!canAfford) { 
                setImage(buttonImage);
                canAfford = true;
            }
        } else {
            if (canAfford) { 
                setImage(disabledImage);
                canAfford = false;
            }
        }

        
        if (canAfford && Greenfoot.mouseClicked(this)) {
            summonUnit(battleWorld);
        }
    }

    
    private void summonUnit(BattleWorld world) {
        
        world.spendGold(unitCost);

        
        Character.Faction playerFaction = LegendOfElkaidu.playerFaction;
        boolean facingRight = (playerFaction == Character.Faction.LIGHT); 

        
        int playerBaseMapX = world.getPlayerBaseMapX(); 
        
        int spawnMapX = playerBaseMapX + (facingRight ? BattleWorld.SPAWN_OFFSET : -BattleWorld.SPAWN_OFFSET);
        int baseMapY = world.getBaseMapY(); 
        int randomOffsetY = random.nextInt(60) - 30; 
        int spawnMapY = baseMapY + randomOffsetY;
        


        
        Character newUnit = null;
        switch (unitName) {
            
            case "Aegis":   newUnit = new Aegis(facingRight);   break;
            case "Baldwin":   newUnit = new Baldwin(facingRight);   break;
            case "BarbarianKing": newUnit = new BarbarianKing(facingRight); break;
            case "Hayato":   newUnit = new Hayato(facingRight);   break;
            case "Renshiro":   newUnit = new Renshiro(facingRight);   break;
            case "TheChosenOne":newUnit = new TheChosenOne(facingRight);break;
            case "Wanderer":   newUnit = new Wanderer(facingRight);   break;
            case "YunZhao":   newUnit = new YunZhao(facingRight);   break;
            
            case "Deadeye":   newUnit = new Deadeye(facingRight);   break;
            case "Kitsune":   newUnit = new Kitsune(facingRight);   break;
            case "Kunoichi":   newUnit = new Kunoichi(facingRight);   break;
            case "Masamune":   newUnit = new Masamune(facingRight);   break;
            case "Oniro":   newUnit = new Oniro(facingRight);   break;
            case "Pyro":   newUnit = new Pyro(facingRight);   break;
            case "ZipZip":   newUnit = new ZipZip(facingRight);   break;
            default:
                System.err.println("Error: Nama kelas unit tidak dikenal: " + unitName);
                 world.addGold(unitCost); 
                return; 
        }

        if (newUnit != null) {
            
            int currentScrollX = world.getCurrentScrollX();
            world.addObject(newUnit, spawnMapX - currentScrollX, spawnMapY);
            
            
        }
    }
}