/**
 * Interface untuk unit yang dapat menerima damage.
 */
public interface Damageable {
    void takeDamage(int amount);
    boolean isAlive();
}
