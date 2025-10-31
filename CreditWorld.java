import greenfoot.*;

public class CreditWorld extends World
{
    public CreditWorld()
    {    
        super(1030, 590, 1); 
        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(greenfoot.Color.BLACK);
        bg.fill();
        setBackground(bg);

        GreenfootImage title = new GreenfootImage("Credits", 48, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(title), getWidth() / 2, 80);

        GreenfootImage progTitle = new GreenfootImage("Programmer:", 32, greenfoot.Color.YELLOW, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(progTitle), getWidth() / 2, 150);

        GreenfootImage prog1 = new GreenfootImage("Daffarael Anaqi Ali", 28, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(prog1), getWidth() / 2, 190);
        GreenfootImage prog2 = new GreenfootImage("Mikail Samyth Habibillah", 28, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(prog2), getWidth() / 2, 220);
        GreenfootImage prog3 = new GreenfootImage("Duha Alul Bariq", 28, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(prog3), getWidth() / 2, 250);

        GreenfootImage designTitle = new GreenfootImage("Design Character & More:", 32, greenfoot.Color.YELLOW, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(designTitle), getWidth() / 2, 320);

        GreenfootImage design1 = new GreenfootImage("craftpix", 28, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(design1), getWidth() / 2, 360);
        GreenfootImage design2 = new GreenfootImage("pixelartspirites", 28, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(design2), getWidth() / 2, 390);
        GreenfootImage design3 = new GreenfootImage("Free and Premium Game Assets (GUI, Sprite, Tilesets)", 28, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(design3), getWidth() / 2, 420);
        GreenfootImage design4 = new GreenfootImage("Gemini, ChatGPT for Image)", 28, greenfoot.Color.WHITE, new greenfoot.Color(0,0,0,0));
        addObject(new LabelActor(design3), getWidth() / 2, 420);

        addObject(new BackButton(new MainMenu()), 60, 40);
        
        setPaintOrder(BackButton.class, LabelActor.class);
    }
}