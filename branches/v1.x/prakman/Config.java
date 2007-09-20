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

package prakman;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Verwaltet die Konfiguration des Programmes. Die Klasse wird sowohl
 * fuer die zentrale PrakMan Konfiguration als auch fuer die
 * Konfiguration eines Workspaces verwendet.
 * @author Christian Lins
 * @author Kai Ritterbusch
 */
public class Config
{
  // Konstanten fuer die Eigenschaften
  public static final String CRASHTEST        = "CrashTest";
  public static final String USE_EXTERNAL_DB  = "UseExternalDB";
  public static final String LAST_WORKSPACE_0 = "LastWorkspace0";
  public static final String LAST_WORKSPACE_1 = "LastWorkspace1";
  public static final String LAST_WORKSPACE_2 = "LastWorkspace2";
  public static final String LAST_WORKSPACE_3 = "LastWorkspace3";
  public static final String LAST_WORKSPACE_4 = "LastWorkspace4"; 
  
  public static final String AUTOSAVEFILE = ".prakman_config";
  
  private static Config instance = null; 

  /**
   * Gibt die Singleton-Instanz dieser Klasse zurueck. 
   * Sie speichert Programm-weite Einstellungen. Fuer Workspace-Einstellungen bitte
   * Workspace.getConfig() verwenden.
   * @return Singleton-Instanz dieser Klasse.
   */
  public static synchronized Config getInstance()
  {
    if(instance == null)
    {
      instance = new Config();
      try
      {
        instance.load(getAutosaveFile());
      }
      catch(IOException e)
      {
        // Ignoriere Ladefehler
      }
    }
    
    return instance;
  }
  
  /**
   * @return Vollstaendiger Dateiname der Standard-Persistenz-Datei.
   */
  static String getAutosaveFile()
  {
    // Home-Verzeichnis des aktuellen Users
    String homeDir = System.getProperty("user.home");
    if(!homeDir.endsWith(File.separator))
      homeDir = homeDir + File.separator;
    
    return homeDir + Config.AUTOSAVEFILE;
  }
  
  private Properties prop = new Properties();
  
  /**
   * Erzeugt eine leere Konfiguration.
   */
  public Config()
  {
  }
  
 
  /**
   * Laedt die Konfiguration von einer vorher gespeicherten
   * XML-Datei.
   * @param file
   * @throws IOException
   */
  public void load(File file)
    throws IOException
  {
    prop.loadFromXML(new FileInputStream(file));
    System.out.println("Lade Konfiguration von " + file.getName());
  }
  
  /**
   * Laedt die Konfiguration von einer vorher gespeicherten
   * XML-Datei.
   * @param filename
   * @throws IOException
   */
  public void load(String filename)
    throws IOException
  {
    load(new File(filename));
  }
  
  /**
   * Speichert die Konfiguration im XML-Format.
   * @param file Datei, in der die Konfiguration gesichert wird.
   * @throws Exception
   */
  public void save(File file)
    throws Exception
  {     
    prop.storeToXML(new FileOutputStream(file), "");
    System.out.println("Speichern der Konfiguration in " + file.getAbsolutePath());
  }
  
  /**
   * Gibt den Wert der Konfiguration am angegebenen Schluessel zurueck.
   * @param key Indexschluessel
   * @param def Standardwert
   * @return Konfigurationswert oder Standardwert, wenn der angegebene
   * Schluessel ungueltig ist.
   */
  public String get(String key, String def)
  {
    try
    {
      String val = prop.getProperty(key);
      if(val == null)
        return def;
      else
        return val;
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
      return def;
    }
  }
  
  /**
   * Gibt den Wert der Konfiguration am angegebenen Schluessel zurueck.
   * @param key Indexschluessel
   * @param def Integer Standardwert
   * @return Konfigurationswert oder Standardwert, wenn der angegebene
   * Schluessel ungueltig ist.
   */
  public int get(String key, int def)
  {
    try
    {
      String v = prop.getProperty(key);
      if(v == null)
        return def;
      return Integer.parseInt(v);
    }
    catch(Exception e)
    {
      System.err.println("Error parsing value of " + key + ": " + e.getMessage());
      return def;
    }
  }
  
  /**
   * Gibt den Wert der Konfiguration am angegebenen Schluessel zurueck.
   * @param key Indexschluessel
   * @param def Farb-Standardwert
   * @return Konfigurationswert oder Standardwert, wenn der angegebene
   * Schluessel ungueltig ist.
   */
  public Color get(String key, Color def)
  {
    int rgb = get(key, def.getRGB());
    return new Color(rgb);
  }
  
  /**
   * Setzt einen Wert in der Konfiguration. Ein eventuell schon
   * vorhandener Wert mit gleichem Schluessel wird ersetzt.
   * @param key Der Indexschluessel
   * @param value Der Wert
   */
  public void set(String key, String value)
  {
    prop.setProperty(key, value);
  }
  
  /**
   * Setzt einen Wert in der Konfiguration. Ein eventuell schon
   * vorhandener Wert mit gleichem Schluessel wird ersetzt.
   * @param key Der Indexschluessel
   * @param value Der Wert
   */
  public void set(String key, int value)
  {
    set(key, Integer.toString(value));
  }
  
  /**
   * Setzt einen Wert in der Konfiguration. Ein eventuell schon
   * vorhandener Wert mit gleichem Schluessel wird ersetzt.
   * @param key Der Indexschluessel
   * @param value Der Wert
   */
  public void set(String key, Color value)
  {
    set(key, value.getRGB());
  }
}
