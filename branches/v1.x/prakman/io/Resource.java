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
 *          http://www.christian-lins.de/se/
 *    zu finden.
 *    
 *    Bei Fehlern oder Hinweise wuerden wir uns ueber eine
 *    E-Mail freuen.
 */

package prakman.io;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import javax.swing.*;

/**
 * Stellt statische Methoden zum Laden von
 * einzelnen Resourcen zur Verfuegung.
 */
public class Resource
{
  /**
   * Laedt eine Bilddatei von einer lokalen Resource.
   * @param name
   * @return Gibt null zurueck, falls das Bild nicht
   * gefunden oder geladen werden konnte.
   */
  public static ImageIcon getImage(String name)
  {
    URL url = 
      ClassLoader.getSystemClassLoader().getResource(name);
    
    if(url == null)
      return null;
    
    return new ImageIcon(url);
  }
  
  /**
   * Laedt eine Resource und gibt einen Verweis auf sie als
   * URL zurueck.
   * @return
   */
  public static URL getAsURL(String name)
  {
    return ClassLoader.getSystemClassLoader().getResource(name);
  }
  
  /**
   * Laedt eine Resource und gibt einen InputStream darauf
   * zurueck.
   * @param name
   * @return
   */
  public static InputStream getAsStream(String name)
  {
    try
    {
      URL url = getAsURL(name);
      return url.openStream();
    }
    catch(IOException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Laedt eine Textdatei komplett in einen String.
   */
  public static String getAsString(String name, boolean withNewline)
  {
    try
    {
      BufferedReader in  = new BufferedReader(
          new InputStreamReader(getAsStream(name), Charset.forName("UTF-8")));
      StringBuffer   buf = new StringBuffer();

      for(;;)
      {
        String line = in.readLine();
        if(line == null)
          break;

        buf.append(line);
        if(withNewline)
          buf.append('\n');
      }

      return buf.toString();
    }
    catch(Exception e)
    {
      return null;
    }
  }
}
