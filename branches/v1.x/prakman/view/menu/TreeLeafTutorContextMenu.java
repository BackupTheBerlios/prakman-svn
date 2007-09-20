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
import java.util.ArrayList;

import javax.swing.JOptionPane;

import prakman.model.Tutor;
import prakman.model.Workspace;
import prakman.view.MainFrame;

/**
 * Kontextmenue fuer die Tutor-Blaetter im Workspace-Tree.
 */
public class TreeLeafTutorContextMenu extends TreeLeafContextMenu
{
  private static final long serialVersionUID = 0;
  
  public TreeLeafTutorContextMenu(Object obj)
  {
    super(obj);
  }
  
  @Override
  public void deleteClicked()
  {
    boolean delete = false;
    boolean force = false;
    int yesNo = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
        "Wirklich " + selectedObject.toString() + " l\u00F6schen?");
    if (yesNo == 0) // Ja
    {
      delete = true;
      try
      {
        // Sind Events betroffen?
        ArrayList<ArrayList<Object>> affectedEvents = Workspace
            .getInstance().getDatabase().getOverseenProjects(
                (Tutor) selectedObject);
        // Sind Gruppen betroffen?
        ArrayList<ArrayList<Object>> affectedGroups = Workspace
            .getInstance().getDatabase().getOverseenGroups(
                (Tutor) selectedObject);
        // Ist eins davon der Fall, so werden mit ziemlicher Sicherheit
        // noch mehr Tabellen betroffen sein
        // Wir geben eine DEUTLICHE Warnung aus!
        if (affectedEvents.size() > 0 || affectedGroups.size() > 0)
        {
          String overseenEvents = "\nEr/Sie betreut folgende Veranstaltung(en):\n";
          for (int i = 0; i < affectedEvents.size(); i++)
          {
            overseenEvents = overseenEvents + affectedEvents.get(i).get(0)
                + ", " + affectedEvents.get(i).get(1) + "\n";
          }
          String overseenGroups = "\nEr/Sie betreut folgende Gruppe(n):\n";
          for (int i = 0; i < affectedGroups.size(); i++)
          {
            overseenGroups = overseenGroups + "Gruppe Nr."
                + affectedGroups.get(i).get(2) + " in "
                + affectedGroups.get(i).get(0) + ", "
                + affectedGroups.get(i).get(1) + "\n";
          }
          int reallySure = JOptionPane
              .showConfirmDialog(
                  MainFrame.getInstance(),
                  selectedObject.toString()
                      + " ist noch aktiv gelistet:\n"
                      + overseenEvents
                      + overseenGroups
                      + "\nZudem werden alle Noten aus den betroffenen Veranstaltungen "
                      + "unwiderruflich gel\u00F6scht!"
                      + "\n\nWollen Sie die genannten Daten zus\u00E4tzlich l\u00F6schen, "
                      + "w\u00E4hlen Sie 'Ja'. Wollen Sie nur den Tutor entfernen, w\u00E4hlen Sie 'Nein'."
                      + "\nZum Abbrechen des Vorgangs w\u00E4hlen Sie 'Abbrechen'.");
          if (reallySure == 0) // Ja
          {
            force = true;
          }
          else if (reallySure == 2) // Abbrechen
          {
            delete = false;
            System.out.println("Abgebrochen");
          }
        }
        if (delete)
        {
          Workspace.getInstance().getDatabase().deleteTutor(
              (Tutor) selectedObject, force);
          JOptionPane.showMessageDialog(MainFrame.getInstance(),
              selectedObject.toString() + " erfolgreich gel\u00F6scht.",
              "Tutor l\u00F6schen", JOptionPane.INFORMATION_MESSAGE);
          Workspace.getInstance().updateTree("");
          super.deleteClicked();
        }
      }
      catch (SQLException sqlEx)
      {
        System.out.println("L\u00F6schen gescheitert.");
        sqlEx.printStackTrace();
      }
    }
    else
    // Abgebrochen oder Nein
    {
      System.out.println("Abgebrochen");
    }

  }
}
