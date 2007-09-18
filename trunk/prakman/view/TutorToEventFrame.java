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
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.TableColumn;

import prakman.model.*;
import prakman.view.object.TutorPanel;

/**
 * Klasse die einen Tutor einen Event zuordnet
 */
public class TutorToEventFrame extends BaseFrame 
{
  private static final long serialVersionUID = 0;
  private Table table;
  private TableModel tm;
  private String[] prakColumnNames  = { "EventID", "TutorID", "#", "Veranstaltung", "Beschreibung" };
  private JPanel   actionPanel = new JPanel();
  private JButton  btnOk = new JButton("OK");
  private JButton  btnClose = new JButton("Abbruch");
  private TutorPanel tutorPanel; 
  
  /** Konstruktor */
  public TutorToEventFrame(TutorPanel tut)
  {
    super("Veranstaltungen zuordnen");
    this.tutorPanel = tut;
    setLayout(new BorderLayout());
    try
    {
       tm    = new TableModel(prakColumnNames, Workspace.getInstance().getDatabase().getUnassignedEvents());    
       table = new Table(tm);
       setTableSize();
       getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
    }
    catch (Exception e) 
    {
      e.printStackTrace();
    }
    
    // Hinzufuegen
    btnOk.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          addEvents();
          ((TutorPanel)tutorPanel).rewritePrakTable();
          setVisible(false);
        }
        catch (Exception err) 
        { 
          err.printStackTrace();
        }
      }
    });
    
    // Schliessen Button
    btnClose.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        setVisible(false);
      }
    });
    
    
    actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER,4,4));
    actionPanel.add(btnOk);
    actionPanel.add(btnClose);
    getContentPane().add(actionPanel, BorderLayout.SOUTH);
    this.setLocation(MainFrame.getInstance().getLocation());
    this.pack();
    this.setVisible(true);
  }
  
  /** Setzt die Breite der Spalten */
  public void setTableSize()
  {
    // Spaltenbreite der Checkboxen setzen
    TableColumn tc0 = table.getColumn(prakColumnNames[0]); // EventID
    TableColumn tc1 = table.getColumn(prakColumnNames[1]); // TutorID
    TableColumn tc2 = table.getColumn(prakColumnNames[2]); // Checkbox    
    
    tc0.setMinWidth(0);
    tc0.setMaxWidth(0);
    tc1.setMinWidth(0);
    tc1.setMaxWidth(0);   
    tc2.setMaxWidth(10);
    tc2.setMinWidth(10);
  }
  
  /** Hinzufuegen der ausgewaehlten Events */
  public void addEvents() throws SQLException
  {
    for(int i=0; i < tm.getRowCount(); i++)
      if(tm.getValueAt(i, 2).equals(true))
       Workspace.getInstance().getDatabase().setEventToTutor(
           Integer.parseInt(tm.getValueAt(i, 0).toString()),tutorPanel.getTutor().getID());
  }
  
}
