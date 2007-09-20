package prakman.io;

/**
 * Alle Klassen, die dieses Interface einbinden, koennen vom
 * CSVPorter aus einer CSV-Datei importiert und in eine CSV-Datei
 * exportiert werden.
 * Achtung: diese Klassen benoetigen auf jeden Fall einen
 * Standardkonstruktor, ansonsten schlaegt das Laden fehl.
 * Hinweis: dieses Interface ermoeglicht bislang nur den Listenexport.
 * Fuer den Detailexport/import von einem Event beispielsweise, ist
 * es ungeeignet.
 * @author CL
 */
public interface CSVPortable
{
  /**
   * Gibt die Spaltenname zurueck.
   * @return
   */
  public String[]      getColumnNames();
  
  /**
   * Gibt eine mit Daten gefuellte Zeile zurueck.
   * @return
   */
  public String[]      getRow();
  
  /**
   * Setzt die Werte des CSVPortable Objekts mit den
   * angegebenen Werten.
   * @param row
   */
  public void          setRow(String[] row);
}
