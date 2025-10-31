import greenfoot.*;

/**
 * Actor sederhana hanya untuk menampilkan gambar (seperti teks).
 * Tidak memiliki interaksi klik.
 */
public class LabelActor extends Actor {

    /**
     * Constructor
     * @param image Gambar yang akan ditampilkan (biasanya GreenfootImage berisi teks).
     */
    public LabelActor(GreenfootImage image) {
        setImage(image);
    }

}