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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * Basisklasse alle verwendeten Frames
 * Ausnahme Mainframe, StartDialog
 */
public abstract class BaseFrame extends JFrame
{
  private static final long serialVersionUID = 0;
  
  /** Windowlistener des BaseFrames */
  protected static class BaseFrameWindowListener
    extends WindowAdapter
  {  
    public BaseFrameWindowListener(JFrame parent)
    {
      if(!(parent instanceof MainFrame))
        parent.addWindowListener(this);
    }
    
    public void windowOpened(WindowEvent e)
    {
      MainFrame.getInstance().setEnabled(false);
    }
    
    public void windowClosing(WindowEvent e)
    {
      MainFrame.getInstance().setEnabled(true);
    }
  }
  
  public BaseFrame()
  {
    new BaseFrameWindowListener(this);
    setAlwaysOnTop(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }
  
  public BaseFrame(String title)
  {
    this();
    setTitle(title);
  }
  
  /** Sichtbarkeite einstellen */
  public void setVisible(boolean state)
  {
    super.setVisible(state);
    MainFrame.getInstance().setEnabled(!state);
  }
}
