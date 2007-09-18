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

import java.sql.SQLException;

import javax.swing.JOptionPane;

import prakman.model.Student;
import prakman.model.Workspace;
import prakman.view.MainFrame;

/**
 * Kontextmenue fuer die Studenten-Blaetter des Workspace-Baumes.
 */
public class TreeLeafStudentContextMenu extends TreeLeafContextMenu
{
  private static final long serialVersionUID = 0;
  
  public TreeLeafStudentContextMenu(Object obj)
  {
    super(obj);
  }
  
  /**
   * Loeschen-Item wurde geklickt.
   */
  @Override
  public void deleteClicked()
  {
    int yesNo = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
        "Wirklich " + selectedObject.toString() + " l\u00F6schen?");
    if (yesNo == 0) // Ja
    {
      try
      {
        Workspace.getInstance().getDatabase().deleteStudent(
            (Student) selectedObject);
        JOptionPane.showMessageDialog(MainFrame.getInstance(),
            selectedObject.toString() + " erfolgreich gel\u00F6scht.",
            "Student l\u00F6schen", JOptionPane.INFORMATION_MESSAGE);
        Workspace.getInstance().updateTree("");
      }
      catch (SQLException sqlEx)
      {
        System.out.println("L\u00F6schen gescheitert.");
        sqlEx.printStackTrace();
      }
      
      super.deleteClicked();
    }
    else
    // Abgebrochen oder Nein
    {
      System.out.println("Abgebrochen");
    }

  }
}
