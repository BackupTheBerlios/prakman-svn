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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import prakman.model.Student;
import prakman.model.TableModel;
import prakman.model.Workspace;
import prakman.view.object.EventPanel;
import prakman.view.object.TutorPanel;

/**
 * Abgeleitet von BaseFrame
 * Basisklasse aller Frames, die Listen mit auswaehlbarer
 * Checkbox anzeigen
 */
public class ListFrame extends BaseFrame
{
  private static final long serialVersionUID = 0;
  
  // Buttons
  private JButton  btnOk       = new JButton("OK");
  private JButton  btnClose    = new JButton("Abbruch");
  // ActionPanel
  private JPanel   actionPanel = new JPanel();
  // Tabelle
  private Table       table;
  private TableModel  tm;
  // Referenz
  private Object   panelRef;
  
  public ListFrame(TableModel tableModel, String title, Object panelReference)
  {
    setTitle(title);
    this.panelRef = panelReference;    
    setLayout(new BorderLayout());
    table = new Table(tableModel);
    // TableModelSetzen
    this.tm = tableModel;
    //table.setModel(tm);
    
    // Hinzufuegen
    btnOk.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(panelRef instanceof TutorPanel)        
          OkActionTutorPanel();        
        else if(panelRef instanceof EventPanel)
          OkActionEventStudentPanel();
        else if(panelRef instanceof ProjectFrame)
          OkActionProjectFrame();
      }
    });
    
    // schliessen des Fensters
    btnClose.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {       
        setVisible(false);        
      }
    });
    
    // Tabelle hinzufuegen
    getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
    
    // ActionPanel
    actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER,4,4));
    actionPanel.add(btnOk);
    actionPanel.add(btnClose);
    getContentPane().add(actionPanel, BorderLayout.SOUTH);  
    
    // Werte setzen    
    init();
    
    // Sichtbar machen
    setLocation(MainFrame.getInstance().getLocation());
    pack();
    setVisible(true);
  }
  
  /**
   * OK-Button gedrueckt
   * Ausgewaehlte Projekte speichern
   */
  private void OkActionProjectFrame()
  {
    ProjectFrame pf = (ProjectFrame)panelRef;
    
    // Selektierte Studenten hinzufuegen
    for(int n = 0; n < tm.getRowCount(); n++)
    {
      if(((Boolean)tm.getValueAt(n, 0)) == true)
      {
        Student std = Workspace.getInstance().getDatabase()
          .getStudent((Integer)tm.getValueAt(n, 2));
        pf.addStudent(std);
      }
    }
    setVisible(false);
  }
  
  /**
   * OK-Button gedrueckt
   * Ausgewaehlte Tutoren speichern
   */
  private void OkActionTutorPanel()
  {
    try
    {
      for(int i = 0; i < tm.getRowCount(); i++)
        if(tm.getValueAt(i, 2).equals(true))
         Workspace.getInstance().getDatabase().setEventToTutor(
             Integer.parseInt(tm.getValueAt(i, 0).toString()),((TutorPanel)panelRef).getTutor().getID());
      ((TutorPanel)panelRef).rewritePrakTable();
      setVisible(false);
    }
    catch (Exception err) 
    { 
      err.printStackTrace();
    }
  }
  
  /**
   * OK-Button gedrueckt
   * Ausgewaehlte Events speichern
   */
  private void OkActionEventStudentPanel()
  {
    try
    {
      for(int i = 0; i < tm.getRowCount(); i++)
        if(tm.getValueAt(i, 0).equals(true))          
         ((EventPanel)panelRef).getEvent().add( ((EventPanel)panelRef).getEvent().getID(), Integer.parseInt(tm.getValueAt(i, 1).toString()));
      ((EventPanel)panelRef).refreshPraksTable(); 
      setVisible(false);
    }
    catch (Exception err) 
    { 
      err.printStackTrace();
    }
  }
  
  /** Spezielle Werte (z.B. Spaltenbreite) setzen */ 
  public void init()
  {
   if(panelRef instanceof TutorPanel)
   {    
     // Spaltenbreite der Checkboxen setzen
     TableColumn tc0 = table.getColumn(tm.getColumnName(0)); // EventID
     TableColumn tc1 = table.getColumn(tm.getColumnName(1)); // TutorID
     TableColumn tc2 = table.getColumn(tm.getColumnName(2)); // Checkbox    
     
     tc0.setMinWidth(0);
     tc0.setMaxWidth(0);
     tc1.setMinWidth(0);
     tc1.setMaxWidth(0);   
     tc2.setMaxWidth(10);
     tc2.setMinWidth(10);
   }
  }
  
}
