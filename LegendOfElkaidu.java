import greenfoot.*;

public class LegendOfElkaidu extends World
{
    /**
     * Constructor default.
     * Dipanggil jika kita hanya menulis 'new LegendOfElkaidu()'.
     * Ini akan memanggil constructor lain di bawah dengan ukuran default 800x400.
     */
    public LegendOfElkaidu()
    {    
        this(800, 400, 1); 
    }
    
    /**
     * Constructor BARU yang kita tambahkan.
     * Constructor ini menerima pengaturan ukuran dan meneruskannya
     * ke 'super' (yaitu constructor kelas World).
     */
    public LegendOfElkaidu(int width, int height, int cellSize)
    {
        super(width, height, cellSize); 
    }
}