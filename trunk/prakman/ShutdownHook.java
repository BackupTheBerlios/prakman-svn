/**
 *    Hausarbeit im Fach Benutzeroberflaechen
 *    
 *    Autoren:
 *    Christian Lins <christian.lins@web.de>
 *    Kai Ritterbusch <kai.ritterbusch@osnanet.de>
 *    
 *    Die Quelltexte in digitaler Form sowie eine ausfuehrbare
 *    Datei dieses Programmes sind unter der Webadresse
 *          http://www.christian-lins.de/bo/
 *    zu finden.
 *    
 *    Bei Fehlern oder Hinweise wuerden wir uns ueber eine
 *    E-Mail freuen.
 */

package prakman;

import java.io.File;
import prakman.model.Workspace;

/**
 * Thread-Klasse, die beim Beenden der Anwendung von der virtuelle Maschine
 * gestartet wird um die Persistenz-Konfiguration zu speichern.
 */
public class ShutdownHook 
  extends Thread
{
  public ShutdownHook()
  {
    setDaemon(false);    
  }
  
  /**
   * Diese Methode wird von der VM aufgerufen, nachdem das Programm
   * beendet wurde.
   * Sie speichert die aktuelle Konfiguration und sorgt so fuer 
   * Persistenz der Anwendung.
   */ 
  public void run()
  {
    try
    {     
      String configFile = Config.getAutosaveFile();
      Config.getInstance().save(new File(configFile));
      if(Workspace.getInstance() != null)
      {
        Workspace.getInstance().getDatabase().shutdown();
        Workspace.getInstance().saveConfig();
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
