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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.TableColumn;
import prakman.io.Database;
import prakman.io.Saveable;
import prakman.model.*;
import prakman.view.BasePanel;
import prakman.view.MainFrame;
import prakman.view.Table;
import prakman.view.inset.SaveResetEditPanel;

/**
 * Panel zur Anzeige von studentenrelevanten Daten.
 * TODO: Mit TutorPanel ein gemeinsames PersonPanel als Basisklasse erstellen.
 */
public class StudentPanel extends BasePanel
{
  private static final long serialVersionUID = 0;

  private Cursor listViewCursor = new Cursor(Cursor.HAND_CURSOR); 
  
  private Student stdRef;
  
  private JTextField txtLastChange;
  private JTextField txtLastName;  
  private JTextField txtFirstName;
  private JTextField txtEmail;
  private JTextField txtMatNr;
  
  private JPanel      studentContentPanel = new JPanel();
  private JTabbedPane tabbedPane 	        = new JTabbedPane();
  private JScrollPane studentPraktPanel	  = new JScrollPane();
  private JScrollPane studentResultsPanel = new JScrollPane();
  
  Table praks;
  String[] prakColumnNames 	= { "eventID", "Veranstaltung", "Beschreibung", "Gruppen-Nr", "Gruppen-Beschr." };
  
  public StudentPanel(Student std)
  {
	  stdRef = std;	// Referenz setzen
    setLayout(new BorderLayout());

    // SimpleDateFormat fuer gute Datumsdarstellung
	  SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - kk:mm");
	  
    txtLastChange	= new JTextField( sdf.format(std.getDate()) );
    // Felder fuellen
    txtLastName 	= new JTextField(std.getLastName());
    txtFirstName 	= new JTextField(std.getFirstName());
    txtEmail			= new JTextField(std.getEmail());
    txtMatNr 	  	= new JTextField( String.valueOf(std.getMatNr()) );
    
    txtLastChange.setEditable(false);
    txtMatNr.setEditable(false);
    
    //System.out.println(((Student)std).getMatNr());
    // Erstelle GridBagLayout
    GridBagLayout      gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();  
    
    studentContentPanel.setLayout(gbl);    
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    
    gbc.weightx = 0.7; // Wir lassen links und rechts einen kleinen Rand frei
    gbc.weighty = 0.7; // Wir lassen oben und unten einen kleinen Rand frei
    
    gbc.gridx = 1;
    gbc.gridy = 1;
    studentContentPanel.add(new SaveResetEditPanel(this),gbc);
    gbc.gridy = 2;
    studentContentPanel.add(new JLabel("Letzte \u00C4nderung"), gbc);
    gbc.gridx = 2;
    studentContentPanel.add(txtLastChange, gbc);
    gbc.gridx = 1;
    gbc.gridy = 3;
    studentContentPanel.add(new JLabel("Nachname"), gbc);
    gbc.gridx = 2;
    studentContentPanel.add(txtLastName, gbc);   
    gbc.gridx = 1;
    gbc.gridy = 4;
    studentContentPanel.add(new JLabel("Vorname"), gbc); 
    gbc.gridx = 2;
    studentContentPanel.add(txtFirstName, gbc);
    gbc.gridx = 1;
    gbc.gridy = 5;
    studentContentPanel.add(new JLabel("E-Mail"), gbc); 
    gbc.gridx = 2;
    studentContentPanel.add(txtEmail, gbc); 
    gbc.gridx = 1;
    gbc.gridy = 6;
    studentContentPanel.add(new JLabel("MatNr."), gbc); 
    gbc.gridx = 2;
    studentContentPanel.add(txtMatNr, gbc);
    
    // studentPraktPanel erstellen -----------------------------
    
    // Table fuer Praktikumszugehoerigkeiten
    ArrayList<ArrayList<Object>> prakData = new ArrayList<ArrayList<Object>>();
    
    try 
    {
   	  Database db = Workspace.getInstance().getDatabase();
   	  // Events aus der Datenbank holen und in prakData einfgen
   	  ArrayList<Event> events = db.getAttendedEvents(std);
   	  // Umwandlung in "Objects"
   	  for(Event event : events)
	    {
   		  ArrayList<Object> addList = new ArrayList<Object>();
   	    addList.add(event.getID());
   		  addList.add(event.getName());
   		  addList.add(event.getDesc());
   		
   		  Group eventGroup = db.getEventGroup(std, event.getID());
   		  if (eventGroup != null )
   		  {
   		    addList.add(Integer.toString(eventGroup.getID()));
   		    addList.add(eventGroup.getDesc());
   		  }
   		  else
   		  {
   		    addList.add("Keine Gruppe");
   		    addList.add("-");
   		  }
   		  prakData.add(addList);
	    }
    }
    catch(SQLException sqlEx)
    {
   	  sqlEx.printStackTrace();
    }
    
    TableModel  prakTm = new TableModel(prakColumnNames, prakData);
    praks  = new Table(prakTm);
    
    praks.addMouseListener(new MouseAdapter() 
    {
  		public void mouseExited(MouseEvent e)
  		{
  			setCursor(Cursor.getDefaultCursor());
  		}
      
  		public void mouseEntered(MouseEvent e)
  		{
  			setCursor(listViewCursor);
  		}
      
  		// Wichtig um Panel mit Studenten zu oeffnen
  		public void mouseClicked(MouseEvent e)
  		{
  			if (e.getClickCount() > 1)
  			{
  				MainFrame.getInstance().getTabbedPane().addTab(
  						Workspace.getInstance().findEventByID(
  								(Integer)praks.getValueAt(praks.rowAtPoint(e.getPoint()), 0)
  								)
  						);
  			}
  		}
  	
  	});
    
    studentPraktPanel.setViewportView(praks);
    
    // studentResultsPanel erstellen -------------------------------
    
    // Table fuer Noten
    
    String[] columnNames = { "Veranstaltung", "Projekt-Beschreibung", "Note", "Bewertung am", "Abzugeben am" };
    ArrayList<ArrayList<Object>> data = null;
    
    try 
    {
	    data = Workspace.getInstance().getDatabase().getResults(std, "EventName");
    }
    catch(SQLException sqlEx)
    {
    	sqlEx.printStackTrace();
    }

    TableModel tm      = new TableModel(columnNames, data);
    Table		   results = new Table(tm);

    studentResultsPanel.setViewportView(results);
    
    // Spalte eventID ausblenden
    setTableSize();
	 
    // TabbedPane erstellen
    tabbedPane.addTab("Angemeldete Praktika", studentPraktPanel);
    tabbedPane.addTab("Notenlisten", studentResultsPanel);
    
    add(studentContentPanel, BorderLayout.NORTH);
    add(tabbedPane, BorderLayout.CENTER);
  }
  
  /** Equals ueberschreiben */
  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof StudentPanel)
      return this.getID() == ((StudentPanel)obj).getID();
    else
      return false;
  }
  
  /**
   * @return Matrikelnummer des Studenten. 
   */
  public int getID()
  {
	  return Integer.parseInt(txtMatNr.getText());
  }
  
  /**
   * @return Der Vorname des Studenten.
   **/
  public String getFirstName()
  {
	  return txtFirstName.getText();
  }
  
  /**
   * @return Der Nachname des Studenten. 
   **/
  public String getLastName()
  {
	  return txtLastName.getText();
  }
  
  /**
   * @return Aenderungsdatum des Events.
   **/
  public Timestamp getDate()
  {
	  return stdRef.getDate();
  }
  
  /**
   * @return E-Mail-Adresse des Studenten.
   **/
  public String getEmail()
  {
  	return txtEmail.getText();
  }
  
  /** 
   * @return Der Student dieses Panels.
   **/
  public Student getStudent()
  {
    return this.stdRef;
  }
  
  /** 
   * Setzt den Nachnamen des Studenten. Dieser wird zunaechst nur im Panel gespeichert.
   * Erst wenn der User auf den Save-Button drueckt wird der Name in das Tutor-
   * Objekt uebernommen.
   **/
  public void setTxtLastName(String t)
  {
    txtLastName.setText(t);
  }
  
  /** 
   * Setzt den Vornamen des Studenten. Dieser wird zunaechst nur im Panel gespeichert.
   * Erst wenn der User auf den Save-Button drueckt wird der Name in das Tutor-
   * Objekt uebernommen.
   **/
  public void setTxtFirstName(String t)
  {
    txtFirstName.setText(t);
  }
  
  /**
   * Setzt die E-Mail des Studenten.
   * @param t
   */
  public void setTxtEmail(String t)
  {
  	this.txtEmail.setText(t);
  }
  
  /**
   * Spalte eventID ausblenden
   */
  public void setTableSize()
  {
    // Spaltenbreite der Checkboxen setzen
    TableColumn tc0 = praks.getColumn(prakColumnNames[0]); // eventID
    
    tc0.setMinWidth(0);
    tc0.setMaxWidth(0); 
  }
  
  /**
   * Gibt ein Studentenobjekt mit den Inhalten des
   * Panels zurueck.
   */
  public Saveable getSaveable()
  {
    return new Student(getLastName(), getFirstName(), getID(), getEmail(), getDate());
  }
  
  /**
   * Verwirft die Aenderungen des StudentPanels.
   */
  public void reset()
  {
    this.txtEmail.setText(stdRef.getEmail());
    this.txtFirstName.setText(stdRef.getFirstName());
    this.txtLastName.setText(stdRef.getLastName());
  }
  
}