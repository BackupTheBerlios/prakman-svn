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

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Stellt FileChooser mit einem FileFilter der exportierbaren und
 * importierbaren Dateitypen bereit.
 * @author CL
 */
public class PortableFileChooser extends JFileChooser
{
  private static final long serialVersionUID = 0;
  
  /**
   * FileFilter einlesen
   */
  private static class CSVFileFilter extends FileFilter
  {
    public boolean accept(File file)
    {
      return file.isDirectory() || file.getName().endsWith(".csv");
    }
    
    /**
     *  Beschreibung auslesen
     */
    public String getDescription()
    {
      return "Comma Separated Values (*.csv)";
    }
  }
  
  public PortableFileChooser()
  {
    addChoosableFileFilter(new CSVFileFilter());
  }
  
  /**
   * @return Selektierte Datei zurueckgeben
   */
  public File getSelectedFile()
  {
    File file = super.getSelectedFile();
    if(file == null)
      return null;
    
    FileFilter ff = getFileFilter();
    if(!ff.accept(file))
    {
      if(ff instanceof CSVFileFilter)
      {
        file = new File(file.getAbsolutePath() + ".csv");
      }
    }
    
    return file;
  }
}
