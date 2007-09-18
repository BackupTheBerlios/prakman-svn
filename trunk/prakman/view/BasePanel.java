/**
 *    Hausarbeit im Fach Software Engineering
 *    ~PrakMan~ Die Praktika-Verwaltung
 *    
 *    Autoren:
 *    Andreas Depping <andreas@intus-music.de>
 *    Christian Lins <christian.lins@web.de>
 *    Kai Ritterbusch <kai.ritterbusch@osnanet.de>
 *    Philipp Rollwage <philipp.rollwage@fh-osnabrueck.de>
 *    
 *    Die Quelltexte in digitaler Form sowie eine ausfuehrbare
 *    Datei dieses Programmes sind unter der Webadresse
 *          http://prakman.berlios.de/
 *          http://developer.berlios.de/projects/prakman/
 *    zu finden.
 *    
 *    Bei Fehlern oder Hinweisen wuerden wir uns ueber eine
 *    E-Mail freuen.
 */

package prakman.view;

import javax.swing.JPanel;
import prakman.io.Saveable;

/**
 * Basisklasse aller Panels, die im TabbedPane angezeigt werden
 * koennen und die ueber Save/Reset-Buttons verfuegen.
 */
public abstract class BasePanel
  extends JPanel
{
  public boolean equals(Object obj)
  {
    if(obj instanceof BasePanel)
      return this.getID() == ((BasePanel)obj).getID();
    else
      return false;
  }
  
  public abstract int getID();
  
  /**
   * Gibt den zu speichernden Inhalt des TabbedPane zurück.
   * @return
   */
  public abstract Saveable getSaveable();
  
  /**
   * Setzt die Werte des inneren Objekts auf einen vorher gespeicherten 
   * Wert zurueck.
   */
  public abstract void reset();
}
