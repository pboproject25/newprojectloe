import greenfoot.GreenfootImage;

public class SpriteLoader {
    public static GreenfootImage[] loadSprites(String path, int frameCount) {
        GreenfootImage sheet = new GreenfootImage(path);
        int frameWidth = sheet.getWidth() / frameCount;
        int frameHeight = sheet.getHeight();
        GreenfootImage[] frames = new GreenfootImage[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new GreenfootImage(frameWidth, frameHeight);
            frames[i].drawImage(sheet, -i * frameWidth, 0);
        }
        return frames;
    }
}
