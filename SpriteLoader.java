import greenfoot.GreenfootImage;

public class SpriteLoader {

    public static GreenfootImage[] loadSprites(String path, int frameCount) {
        return loadSprites(path, frameCount, 0, 1);
    }

    public static GreenfootImage[] loadSprites(String path, int frameCount, int rowNumber, int totalRows) {
        GreenfootImage sheet;
        try {
            sheet = new GreenfootImage(path);
        } catch (Exception e) {
            System.err.println("Sprite sheet tidak ditemukan di: " + path);
            return new GreenfootImage[0]; 
        }

        int frameWidth = sheet.getWidth() / frameCount;
        int frameHeight = sheet.getHeight() / totalRows;
        GreenfootImage[] frames = new GreenfootImage[frameCount];
        int yOffset = rowNumber * frameHeight;

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new GreenfootImage(frameWidth, frameHeight);
            
            int xOffset = i * frameWidth;
            
            frames[i].drawImage(sheet, -xOffset, -yOffset);
        }
        
        return frames;
    }
}