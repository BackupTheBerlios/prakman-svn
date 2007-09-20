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
import prakman.model.Tutor;
import prakman.model.Workspace;
import prakman.view.MainFrame;
import prakman.view.PortableFileChooser;

/**
 * Kontextmenue des Tutor-Astes im Workspace-Baum.
 */
public class TreeBranchTutorContextMenu extends TreeBranchContextMenu
{
  private static final long serialVersionUID = 0;
  
  public TreeBranchTutorContextMenu(Object obj)
  {
    super(obj);
  }
  
  @Override
  public void addClicked()
  {  
	    try
	    {
	    	int id = (Workspace.getInstance().getDatabase().getTutors()).size();	    	
	    	Tutor tut = new Tutor(id,"<unbenannt>","<unbenannt>");
	    	MainFrame.getInstance().getTabbedPane().addTab(tut);
	    }
	    catch(SQLException e){e.printStackTrace();}
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
        ArrayList<Tutor> tuts = Workspace.getInstance().getDatabase()
              .getTutors();
        for (Tutor tut : tuts)
          Workspace.getInstance().getDatabase().deleteTutor(tut, false);
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
    
    ArrayList<Tutor> tuts = Workspace.getInstance().getDatabase()
    .getTutors();
    data = tuts.toArray(new CSVPortable[0]);

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
    
    CSVPortable[] tuts = CSVPorter.importFrom(fc.getSelectedFile()
        .getAbsolutePath(), Tutor.class);

    try
    {
      for (CSVPortable tut : tuts)
        Workspace.getInstance().getDatabase().addTutor((Tutor) tut);
    }
    catch(SQLException e)
    {
      e.printStackTrace();
    }
  }

}
