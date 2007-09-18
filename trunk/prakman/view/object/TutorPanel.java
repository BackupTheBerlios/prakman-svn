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

package prakman.view.object;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.TableColumn;
import prakman.io.Saveable;
import prakman.model.*;
import prakman.view.BasePanel;
import prakman.view.ListFrame;
import prakman.view.Table;
import prakman.view.inset.SaveResetEditPanel;
import prakman.view.inset.TabbedEditable;
import prakman.view.inset.TabbedEditPanel;

/**
 * Panel zur Anzeige von tutorrelevanten Daten.
 */
public class TutorPanel extends BasePanel implements TabbedEditable
{
  private static final long serialVersionUID = 0;
	
  private Tutor	tutRef;
	
  private JTextField txtLastChange;
  private JTextField txtLastName;  
  private JTextField txtFirstName;
  private JTextField txtTutorID;
  
  private JPanel tutorContentPanel      = new JPanel();
  private JPanel tutorTabbedPanePanel   = new JPanel();
  private JTabbedPane tabbedPane 	      = new JTabbedPane();
  private JScrollPane tutorPraktPanel	  = new JScrollPane();
  private SaveResetEditPanel editPanel;
  private Table praks;
  private TableModel prakTm; 
  //private JScrollPane tutorResultsPanel= new JScrollPane();
  String[] prakColumnNames  = { "EventID","TutorID","#", "Veranstaltung", "Beschreibung" };
  
  public TutorPanel(Object tut)
  {
	  tutRef = (Tutor)tut;	// Referenz auf den Tutor setzen
    setLayout(new BorderLayout());
    
    // SimpleDateFormat fuer gute Datumsdarstellung
	  SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - kk:mm");
	 
    // Felder fuellen
	  txtLastChange	= new JTextField(sdf.format(((Tutor)tut).getDate()));
    txtLastName 	= new JTextField(((Tutor)tut).getLastName());
    txtFirstName 	= new JTextField(((Tutor)tut).getFirstName());
    txtTutorID	  = new JTextField( String.valueOf(((Tutor)tut).getID()) );
    
    txtLastChange.setEditable(false);
    txtTutorID.setEditable(false);
    
    System.out.println(((Tutor)tut).getID());
    // Erstelle GridBagLayout
    GridBagLayout      gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();  
    
    tutorContentPanel.setLayout(gbl);    
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    
    gbc.weightx = 0.7; // Wir lassen links und rechts einen kleinen Rand frei
    gbc.weighty = 0.7; // Wir lassen oben und unten einen kleinen Rand frei
    
    gbc.gridx = 1;
    gbc.gridy = 1;
    editPanel = new SaveResetEditPanel(this);
    tutorContentPanel.add(editPanel,gbc);
    gbc.gridy = 2;
    tutorContentPanel.add(new JLabel("Letzte \u00C4nderung"), gbc);
    gbc.gridx = 2;
    tutorContentPanel.add(txtLastChange, gbc);
    gbc.gridx = 1;
    gbc.gridy = 3;
    tutorContentPanel.add(new JLabel("Nachname"), gbc);
    gbc.gridx = 2;
    tutorContentPanel.add(txtLastName, gbc);   
    gbc.gridx = 1;
    gbc.gridy = 4;
    tutorContentPanel.add(new JLabel("Vorname"), gbc); 
    gbc.gridx = 2;
    tutorContentPanel.add(txtFirstName, gbc); 
    gbc.gridx = 1;
    gbc.gridy = 5;
    tutorContentPanel.add(new JLabel("Tutor-ID"), gbc); 
    gbc.gridx = 2;
    tutorContentPanel.add(txtTutorID, gbc);
    
    // tutorPraktPanel erstellen -----------------------------
    
    // Table fr Praktikumszugehrigkeiten

    ArrayList<ArrayList<Object>> prakData = new ArrayList<ArrayList<Object>>();
    
    try 
    {
   	  // Events aus der Datenbank holen und in prakData einfgen
   	  prakData = Workspace.getInstance().getDatabase().getOverseenProjects((Tutor)tut);
    }
    catch(SQLException sqlEx)
    {
   	  sqlEx.printStackTrace();
    }
    
    // TableModel setzen
    prakTm = new TableModel(prakColumnNames, prakData);
    praks  = new Table(prakTm);    
    
    // Spaltenbreite setzen
    setTableSize();
    
    tutorPraktPanel.setViewportView(praks);   
    tutorTabbedPanePanel.setLayout(new BorderLayout());
    tutorTabbedPanePanel.add(tutorPraktPanel, BorderLayout.CENTER);
    tutorTabbedPanePanel.add(new TabbedEditPanel(this), BorderLayout.SOUTH);
    
   
    // TabbedPane erstellen
    tabbedPane.addTab("Angebotene Praktika", tutorTabbedPanePanel);
    
    if( ((Tutor)tut).getID() == 0 )
    {
      tutorContentPanel.setEnabled(false);      
      tabbedPane.setEnabled(false);
      praks.setEnabled(false);
      tutorTabbedPanePanel.setEnabled(false);
      editPanel.setEnabled(false);
    }
    add(tutorContentPanel, BorderLayout.NORTH);
    add(tabbedPane, BorderLayout.CENTER);
    
  }
  
  /** Equals ueberschreiben */
  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof TutorPanel)
      return this.getID() == ((TutorPanel)obj).getID();
    else
      return false;
  }
  
  /**
   * @return ID des Tutors. 
   */
  public int getID()
  {
	  return Integer.parseInt(txtTutorID.getText());
  }
  
  /**
   * @return Vorname des Tutors. 
   */
  public String getFirstName()
  {
	  return txtFirstName.getText();
  }
  
  /** 
   * @return Nachname des Tutors.
   */
  public String getLastName()
  {
	  return txtLastName.getText();
  }
  
  /** 
   * @return Aenderungsdatum des Panels.
   */
  public Timestamp getDate()
  {
	  return tutRef.getDate();
  }
  
  /**
   * Gibt den enthaltenen Tutor zurueck. 
   */
  public Tutor getTutor()
  {
    return this.tutRef;
  }
  
  /** Gibt das TabbedPane des Panels zurueck. */
  public JTabbedPane getTabbedPane()
  {
    return this.tabbedPane;
  }
  
  /** Gibt die Praktika-Tabelle des Tutor-Panels zurueck. */
  public Table getPrakTable()
  {
    return this.praks;
  }
  
  /** 
   * Setzt den Nachnamen des Tutors. Dieser wird zunaechst nur im Panel gespeichert.
   * Erst wenn der User auf den Save-Button drueckt wird der Name in das Tutor-
   * Objekt uebernommen.
   **/
  public void setTxtLastName(String t)
  {
	  txtLastName.setText(t);
  }
  
  /** 
   * Setzt den Vornamen des Tutors. Dieser wird zunaechst nur im Panel gespeichert.
   * Erst wenn der User auf den Save-Button drueckt wird der Name in das Tutor-
   * Objekt uebernommen.
   **/
  public void setTxtFirstName(String t)
  {
	  txtFirstName.setText(t);
  }
  
  /** Tabelle neu schreiben */
  public void rewritePrakTable()
  {    
    remove(praks);
    
    ArrayList<ArrayList<Object>> prakData = new ArrayList<ArrayList<Object>>();
    try 
    {
      // Events aus der Datenbank holen und in prakData einfuegen
      prakData = Workspace.getInstance().getDatabase().getOverseenProjects(tutRef);
    }
    catch(SQLException sqlEx)
    {
      sqlEx.getMessage();
      sqlEx.printStackTrace();
    }
    prakTm = new TableModel(prakColumnNames, prakData);
    praks = new Table(prakTm);  
    setTableSize();
    tutorPraktPanel.setViewportView(praks);  
  }
  
  private void setTableSize()
  {
    // Spaltenbreite der Checkboxen setzen
    TableColumn tc0 = praks.getColumn(prakColumnNames[0]); // EventID
    TableColumn tc1 = praks.getColumn(prakColumnNames[1]); // TutorID
    TableColumn tc2 = praks.getColumn(prakColumnNames[2]); // Checkbox    
    
    tc0.setMinWidth(0);
    tc0.setMaxWidth(0);       
    tc1.setMinWidth(0);
    tc1.setMaxWidth(0);    
    tc2.setMaxWidth(10);
    tc2.setMinWidth(10);   
  }
  
  @Override
  public int hashCode()
  {
    return getID();
  }
  
  /**
   * Gibt ein neues Tutor-Objekt zurueck, das die Daten des TutorPanels
   * enthaelt.
   */
  public Saveable getSaveable()
  {
    return new Tutor(getID(), getLastName(), getFirstName(), getDate());
  }
  
  /**
   * Verwirft die Aenderungen des Tutor-Panels.
   */
  public void reset()
  {
    this.txtFirstName.setText(tutRef.getFirstName());
    this.txtLastName.setText(tutRef.getLastName());
  }
  
  /**
   * Der Hinzufuegen-Button wurde gedrueckt.
   */
  public void addClicked()
  {
    TableModel tm = null;
    try
    {
      tm = new TableModel(prakColumnNames, Workspace.getInstance().getDatabase().getUnassignedEvents());
    }
    catch (Exception e) 
    {
      e.printStackTrace();
    }
    new ListFrame(tm, "Veranstaltungen zuordnen", this);
  }
  
  /**
   * Der Entfernen-Button wurde geklickt.
   */
  public void removeClicked()
  {
    int reallySure = JOptionPane.showConfirmDialog(null, "Wirklich l\u00f6schen ?");
    if (reallySure != 0) // Abbruch oder Nein
      return;

    TableModel model = (TableModel)getPrakTable().getModel();        
    System.out.println("--");

    for(int i = 0; i < model.getRowCount(); i++)
    {
      if(model.getValueAt(i, 2).equals(true))
      {
        int eventID = Integer.parseInt(model.getValueAt(i, 0).toString());
        int tutorID = Integer.parseInt(model.getValueAt(i, 1).toString());
        
        try
        {
          Workspace.getInstance().getDatabase().deleteAssignment(eventID, tutorID);
          rewritePrakTable();
        }
        catch (SQLException err) 
        {
          err.printStackTrace();
        }
      }
    }
  }

  /**
   * Der Alles-Selektieren-Button wurde gedrueckt.
   */
  public void selectAllClicked()
  {
    for(int n = 0; n < prakTm.getRowCount(); n++)
      prakTm.setValueAt(true, n, 2);
    System.out.println("select all");
  }
  
  /**
   * Der Alles-Deselktieren-Button wurde gedrueckt.
   */
  public void deselectAllClicked()
  {
    for(int n = 0; n < prakTm.getRowCount(); n++)
      prakTm.setValueAt(false, n, 2);
  }
}
