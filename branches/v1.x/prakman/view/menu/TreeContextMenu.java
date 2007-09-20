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

import javax.swing.JPopupMenu;
import prakman.model.Workspace;
import prakman.view.TreeLeaf;

/**
 * Basisklasse fuer die ContextMenus des Workspace-Trees.
 */
public class TreeContextMenu extends JPopupMenu
{
  private static final long serialVersionUID = 0;

  /**
   * Gibt ein zum selektierten Objekt passendes TreeContextMenu zurueck.
   * @param obj
   * @return
   */
  public static TreeContextMenu getInstance(Object obj)
  {
    if(obj != null)
    {
      if(obj instanceof TreeLeaf)
        return TreeLeafContextMenu.getInstance(obj);
      else
      {
        String str = obj.toString();
        if(str.equals(Workspace.STUDENT) ||
            str.equals(Workspace.EVENT) || 
            str.equals(Workspace.TUTOR))
          return TreeBranchContextMenu.getInstance(obj);
      }
    }
    return new TreeContextMenu(obj);
  }

  protected Object selectedObject;

  protected TreeContextMenu(Object obj)
  {
    this.selectedObject = obj;
  }
}
