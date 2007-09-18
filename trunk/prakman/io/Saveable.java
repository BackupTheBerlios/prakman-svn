package prakman.io;

/**
 * Interface aller speicherbarer Objekte.
 */
public interface Saveable
{
  /**
   * Speichert das Objekt in der Datenbank.
   * @return
   */
  public boolean save();
}
