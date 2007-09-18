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

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import prakman.io.Saveable;
import prakman.model.Student;
import prakman.model.Tutor;
import prakman.view.object.EventPanel;
import prakman.view.object.StudentPanel;
import prakman.view.object.TutorPanel;
import prakman.view.ui.TabbedPaneUI;
import prakman.io.Resource;

/**
 * Eigene Spezialisierung des JTabbedPane.
 */
public class TabbedPane 
  extends JTabbedPane
{
  private static final long serialVersionUID = 0;
  
  public TabbedPane()
  {   
    setUI(new TabbedPaneUI(this));
  }
  
  /** Uberschriebene addTab Methode */  
  public void addTab(Object per)
  {    
    BasePanel newTab  = null;
    ImageIcon   newIcon = null;
    
    if(per instanceof Student)
    {
      newIcon = Resource.getImage("resource/gfx/icons/face-smile_16_16.png");
      newTab  = new StudentPanel((Student)per);
    }
    else if(per instanceof prakman.model.Event)
    {
      newTab = new EventPanel((prakman.model.Event)per);
    }
    else if(per instanceof Tutor)       
    {
      newIcon = Resource.getImage("resource/gfx/icons/face-glasses_16_16.png");
      newTab  = new TutorPanel(per);
    }
  
    BasePanel oldTab = checkIfExists(newTab);
    if(oldTab != null)
    {
      setSelectedComponent(oldTab);
    }
    else
    {
      super.insertTab(per.toString(), newIcon, newTab, per.toString(), 0);
      super.setSelectedIndex(0); 
      //super.addTab(per.toString(), newIcon, newTab);
    }
    
  }
  
  /**
   * Gibt den Tab Index des Tabs zurueck, welche das gegebene Objekt
   * enthaelt oder -1 falls kein derartiges Tab geoeffnet ist.
   * @param obj
   * @return
   */
  public int getTabIndex(Saveable obj)
  {
    for(int n = 0; n < getTabCount(); n++)
    {
      BasePanel p = (BasePanel)getComponentAt(n);
      if(p.getSaveable().equals(obj))
        return n;
    }
    return -1;
  }
   
  /** Pruefen ob ein Tab schon vorhanden ist */
  private BasePanel checkIfExists(BasePanel obj)
  {
    for(int n = 0 ; n < getComponents().length; n++)
    {
      BasePanel tp = (BasePanel)getComponents()[n];
      if(obj.equals(tp))
        return tp;
    }
    return null;
  }
}
