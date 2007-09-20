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

package prakman.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import prakman.io.Resource;
import prakman.io.Saveable;
import prakman.model.Event;
import prakman.model.Student;
import prakman.model.Tutor;
import prakman.view.MainFrame;

/**
 * ContextMenu fuer die einzelnen Blatt-Eintraege (z.B. Studenten, Events und 
 * Tutoren).
 */
public abstract class TreeLeafContextMenu extends TreeContextMenu
{
  /**
   * Gibt die Instanz einer TreeLeafContextMenu-Spezialisierung zurueck.
   * Anhand des uebergebenen Objekt wird eine Spezialisierung aus-
   * gewaehlt.
   * @param obj
   * @return
   */
  public static TreeLeafContextMenu getInstance(Object obj)
  {
    if(obj instanceof Event)
      return new TreeLeafEventContextMenu(obj);
    else if(obj instanceof Student)
      return new TreeLeafStudentContextMenu(obj);
    else if(obj instanceof Tutor)
      return new TreeLeafTutorContextMenu(obj);
    
    return null;
  }
  
  private JMenuItem mnuEdit = new JMenuItem("Bearbeiten", Resource
      .getImage("resource/gfx/icons/edit-find-replace_16_16.png"));

  private JMenuItem mnuDelete = new JMenuItem("L\u00F6schen", Resource
      .getImage("resource/gfx/icons/edit-delete_16_16.png"));
  
  protected TreeLeafContextMenu(Object obj)
  {
    super(obj);
    
    add(mnuEdit);
    add(mnuDelete);
    
    mnuEdit.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        editClicked();
      }
    });
    
    mnuDelete.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        deleteClicked();
      }
    });
  }
  
  public void deleteClicked()
  {
    // Entferne das Tab des selektierten Objekts,
    // sofern es geoeffnet ist.
    int tabIndex = MainFrame.getInstance().getTabbedPane()
      .getTabIndex((Saveable)selectedObject);
    if(tabIndex >= 0)
    {
      System.out.println("Schließe Tab...");
      MainFrame.getInstance().getTabbedPane().removeTabAt(tabIndex);
    }
    else
      System.out.println("Kein Tab geöffnet!");
  }
  
  public void editClicked()
  {
    MainFrame.getInstance().getTabbedPane().addTab(selectedObject);
  }
}
