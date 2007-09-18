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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import prakman.io.CSVPortable;
import prakman.io.CSVPorter;
import prakman.model.Student;
import prakman.model.Workspace;
import prakman.view.MainFrame;
import prakman.view.PortableFileChooser;

/**
 * Kontextmenue des Studenten-Ueberordners im Workspace-Baum.
 */
public class TreeBranchStudentContextMenu extends TreeBranchContextMenu
{
  private static final long serialVersionUID = 0;
  
  public TreeBranchStudentContextMenu(Object obj)
  {
    super(obj);
  }
  
  @Override
  public void addClicked()
  {
    String[] msg = { "Bitte geben Sie die Matrikelnummer des neuen Studenten ein:" };
    String newMatNr = JOptionPane.showInputDialog(msg);
    if (newMatNr != null)
    {
      int matNr = 1;
      try
      {
        matNr = Integer.parseInt(newMatNr);
      }
      catch(Exception e)
      {
        String[] msgA = 
        {"Bitte geben Sie eine dezimale ZAHL ein!"};
        JOptionPane.showMessageDialog(null, msgA, "Neuer Student", JOptionPane.WARNING_MESSAGE);
        addClicked();
        return;
      }
      
      if(matNr < 0)
      {
        String[] msgA = 
        {"Eine negative Matrikelnummer ist nicht zulässig!",
          "Bitte wählen Sie eine andere Matrikelnummer!"};
        JOptionPane.showMessageDialog(null, msgA, "Neuer Student", JOptionPane.WARNING_MESSAGE);
        addClicked();
        return;
      }
      
      Student std = Workspace.getInstance().getDatabase().getStudent(matNr);
      if(std != null)
      {
        String[] msgA = 
          {"Ein Student mit dieser Matrikelnummer existiert bereits!",
            "Bitte wählen Sie eine andere Matrikelnummer!"};
        JOptionPane.showMessageDialog(null, msgA, "Neuer Student", JOptionPane.WARNING_MESSAGE);
        addClicked();
        return;
      }
      
      // Erstelle neuen Studenten und oeffne ihn in einem Panel
      std = new Student(Integer.parseInt(newMatNr));
      MainFrame.getInstance().getTabbedPane().addTab(std);
    }

  }

  @Override
  public void deleteAllClicked()
  {
    
    String[] msg = { "M\u00F6chten Sie wirklich die gesamte Liste l\u00F6schen?" };
    int result = JOptionPane.showConfirmDialog(null, msg, "Frage",
        JOptionPane.YES_NO_OPTION);

    if (result == JOptionPane.YES_OPTION)
    {
      try
      {
        ArrayList<Student> stds = Workspace.getInstance().getDatabase()
              .getStudents();
        for (Student s : stds)
          Workspace.getInstance().getDatabase().deleteStudent(s);
      }
      catch (SQLException ex)
      {
        ex.printStackTrace();
      }
    }

  }

  @Override
  public void exportClicked()
    throws Exception
  {
    JFileChooser fc = new PortableFileChooser();
    fc.setMultiSelectionEnabled(false);
    fc.showSaveDialog(null);
    
    if(fc.getSelectedFile() == null)
      return;
    
    CSVPortable[] data = null;
    
    ArrayList<Student> stds = Workspace.getInstance().getDatabase().getStudents();
    data = stds.toArray(new CSVPortable[0]);
    
//    CSVPorter.exportTo(fc.getSelectedFile().getAbsolutePath(), data);
    if(CSVPorter.exportTo(fc.getSelectedFile().getAbsolutePath(), data))
    {
      String[] msg = {"Erfolgreich exportiert!"};
      JOptionPane.showMessageDialog(MainFrame.getInstance(), msg);
    }
    else 
    {
      String[] msg = {"Beim Export ist ein Fehler aufgetreten!"};
      JOptionPane.showMessageDialog(MainFrame.getInstance(), msg);
    }
  }

  @Override
  public void importClicked()
  {
    JFileChooser fc = new PortableFileChooser();
    fc.setMultiSelectionEnabled(false);
    fc.showOpenDialog(null);
    
    if(fc.getSelectedFile() == null)
      return;
    
    CSVPortable[] stds = CSVPorter.importFrom(fc.getSelectedFile()
        .getAbsolutePath(), Student.class);

    try
    {
      for (CSVPortable std : stds)
        Workspace.getInstance().getDatabase().addStudent((Student) std);
    }
    catch(SQLException e)
    {
      e.printStackTrace();
    }
  }

}
