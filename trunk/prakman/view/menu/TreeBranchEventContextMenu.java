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
import prakman.model.Event;
import prakman.model.Tutor;
import prakman.model.Workspace;
import prakman.view.MainFrame;
import prakman.view.PortableFileChooser;

/**
 * Kontextmenue des Veranstaltungs-Astes des Workspace-Baumes.
 */
public class TreeBranchEventContextMenu extends TreeBranchContextMenu
{
  private static final long serialVersionUID = 0;
  
  public TreeBranchEventContextMenu(Object obj)
  {
    super(obj);
    
    // Wir veraendern die Listen-Import Buttons
    remove(mnuExport);
    mnuImport.setText("Import...");
  }
  
  @Override
  public void addClicked()
  {
	  try
	  {
		  	Tutor[] tut = (Workspace.getInstance().getDatabase().getTutors()).toArray(new Tutor[0]);
		  	String[] msg = { "Bitte w\u00E4hlen sie einen Dozenten:" };

	    	Tutor value = (Tutor)JOptionPane.showInputDialog(
	    					this,
	    					msg,
	    					"Auswahl",
	    					JOptionPane.QUESTION_MESSAGE,
	    					null, 
	    					tut,
	    					tut[0]); 
	    
	    
	    	if (value!=null)
	    	{
	    		int id = Workspace.getInstance().getDatabase().getNewEventID();	    	
	    		Event evt = new Event("<unbenannt>","",Workspace.getInstance().findTutorByID(value.getID()),id);
	    		MainFrame.getInstance().getTabbedPane().addTab(evt);
	    	}
	    	else
	    		System.out.println("Abgebrochen");
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
        ArrayList<Event> events = Workspace.getInstance().getDatabase()
              .getEvents();
        for (Event event : events)
          Workspace.getInstance().getDatabase().deleteEvent(event);
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
    
    ArrayList<Event> events = Workspace.getInstance().getDatabase().getEvents();
    data = events.toArray(new CSVPortable[0]);

    CSVPorter.exportTo(fc.getSelectedFile().getAbsolutePath(), data);
  }

  @Override
  public void importClicked()
  {
    TreeLeafEventContextMenu.importClicked();
    /*// Code fuer den ListenImport - DEAKTIVIERT
    JFileChooser fc = new PortableFileChooser();
    fc.setMultiSelectionEnabled(false);
    fc.showOpenDialog(null);
    
    if(fc.getSelectedFile() == null)
      return;
    
    CSVPortable[] events = CSVPorter.importFrom(fc.getSelectedFile()
        .getAbsolutePath(), Event.class);

    try
    {
      for (CSVPortable event : events)
        Workspace.getInstance().getDatabase().addEvent((Event) event);
    }
    catch(SQLException e)
    {
      e.printStackTrace();
    }
    */
  }

}
