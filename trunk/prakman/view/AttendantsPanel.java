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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import prakman.io.Database;
import prakman.io.Saveable;
import prakman.model.Student;
import prakman.model.TableModel;
import prakman.model.Term;
import prakman.model.Workspace;
import prakman.view.inset.SaveResetEditPanel;
import prakman.view.inset.TabbedEditable;
import prakman.view.inset.TabbedEditPanel;

/**
 * Diese Klasse stellt die Anwesenheitsliste der Studenten fuer 
 * ein Event dar. 
 */
public class AttendantsPanel extends BasePanel
  implements Saveable, TabbedEditable
{
  /**
   * Die Spaltennamen der Anwesenheitstabelle.
   */
  public static final String[] ATTENDANTS_COLUMN_NAMES 
    = { "Anwesend", "Matr-Nr", "Nachname", "Vorname"};

  private static final long serialVersionUID = 0;
   
  private Timestamp date;
  private int eventID;
  private int termID;
  private JTextField txtDate;
  private Term term;
//  private boolean newItem = true;  
  private Table attendants;
  private JPanel attendantsContentPanel = new JPanel();
  private JScrollPane attendantsViewPane  = new JScrollPane();  
  private AttendantsFrame reference;
  
  public AttendantsPanel(int evID, int terID, Timestamp date, AttendantsFrame ref)
  {
    this(evID, terID, ref);
    //newItem = false;
    this.date = date;
    txtDate.setText(date.toLocaleString());
    txtDate.setEnabled(false);
  }
  
  public AttendantsPanel(int evID, int terID, AttendantsFrame ref)
  {
    this.eventID = evID;
    this.termID  = terID;
    
    this.reference = ref;
    setLayout(new BorderLayout());
    
    // attendantsContentPanel layouten
    GridBagLayout      gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    attendantsContentPanel.setLayout(gbl);    
    gbc.fill    = GridBagConstraints.HORIZONTAL;
      
    gbc.weightx = 0.7; // Wir lassen links und rechts einen kleinen Rand frei
    gbc.weighty = 0.7; // Wir lassen oben und unten einen kleinen Rand frei
    gbc.gridy = 1;
    gbc.gridx = 1;
 
    // Panel Erstellen
    JPanel actionPanel   = new JPanel();
    JPanel dateTimePanel = new JPanel();    
    
    // Date-Time Panel
    txtDate = new JTextField(20);
    JButton    btnDate = new JButton("Datum");    
    dateTimePanel.setLayout(new FlowLayout());
    dateTimePanel.add(new JLabel("Termin"));
    dateTimePanel.add(txtDate);
    dateTimePanel.add(btnDate);
    
    btnDate.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        DateChooser dateChooser = new DateChooser(reference, new GregorianCalendar());
        if (dateChooser.showDateChooser() == DateChooser.OK_OPTION)
        {          
          Timestamp stamp = new Timestamp(dateChooser.getDate().getTimeInMillis());
          date = new Timestamp(stamp.getTime());
          txtDate.setText(stamp.toLocaleString());//sdf.format(stamp));
        }
      }
    });
    
    actionPanel.setLayout(new BorderLayout());
    actionPanel.add(attendantsContentPanel, BorderLayout.NORTH);
    actionPanel.add(dateTimePanel,  BorderLayout.SOUTH);
    
    attendantsContentPanel.add(new SaveResetEditPanel(this),gbc);

    // attendantsViewPane fuellen
    this.setAttendants(new Table(this.getAttendantsViewPane()));
    getViewPane().setViewportView(getAttendants());
    
    this.add(actionPanel, BorderLayout.NORTH);
    this.add(attendantsViewPane, BorderLayout.CENTER);
    
    TabbedEditPanel editPanel = new TabbedEditPanel(this);
    editPanel.remove(editPanel.getAddButton());
    editPanel.remove(editPanel.getRemoveButton());
    this.add(editPanel, BorderLayout.SOUTH);
  }
  
  /** 
   * Anwesenheitsdaten aus der Datenbank lesen und als
   * fertiges TableModel zurueckgeben
   * @return TableModel
   */
  public TableModel getAttendantsViewPane()
  {
    ArrayList<ArrayList<Object>> attendantsData = new ArrayList<ArrayList<Object>>();
     
    try
    {
      Database db = Workspace.getInstance().getDatabase();
       
      ArrayList<Student> studentsInEvent = db.getStudentsInEvent(eventID);
      ArrayList<Student> actualStudentsInEvent = db.getActualAttendantsInEventPerTerm(eventID, termID);
      
      for(Student stud : studentsInEvent)
      {
        boolean attended = false;
        ArrayList<Object> subData = new ArrayList<Object>();
        for(Student actStud : actualStudentsInEvent)
        {
          if (actStud.getMatNr() == stud.getMatNr())
          {
            attended = true;
            break;
          }
        }
        if (attended)
          subData.add(new Boolean(true));
        else
          subData.add(new Boolean(false));
        subData.add(stud.getMatNr());
        subData.add(stud.getLastName());
        subData.add(stud.getFirstName());
        attended = false;
        
        attendantsData.add(subData);
      }
    }
    catch(SQLException sqlEx)
    {
      sqlEx.printStackTrace();
    }
     
    return( new TableModel(AttendantsPanel.ATTENDANTS_COLUMN_NAMES, attendantsData) );
  }
  
  private JScrollPane getViewPane()
  {
    return this.attendantsViewPane;
  }
  
  Table getAttendants()
  {
    return this.attendants;
  }
  
  void setAttendants(Table tbl)
  {
    this.attendants = tbl;
  }
  
  /**
   * Gibt immer 0 zurueck. Noetig fuer das Saveable-Interface.
   */
  @Override
  public int getID()
  {
    return 0;
  }

  /**
   * @return Gibt das aktuelle Objekt zurueck.
   */
  @Override
  public Saveable getSaveable()
  {
    return this;
  }

  /**
   *  Setzt die Anwesenheitstabelle zurueck.
   */
  @Override
  public void reset()
  {
	  this.setAttendants(new Table(this.getAttendantsViewPane()));
	  this.getViewPane().setViewportView(this.getAttendants());
  }
  
  /**
   * Speichert die Anwesenheitsliste.
   * Diese Methode wird automatisch vom SaveResetEditPanel
   * aufgerufen, wenn der Speichern-Button geklickt wurde.
   */
  public boolean save()
  {
  	ArrayList<ArrayList<Object>> attended	= new ArrayList<ArrayList<Object>>();
	  TableModel tm = (TableModel)(this.getAttendants().getModel());
	  for(int i = 0; i < this.getAttendants().getRowCount(); i++)
	  {
		  ArrayList<Object> subData = new ArrayList<Object>();
		  
		  subData.add( (Integer)tm.getValueAt(i, 1) );		  
		  if ( (tm.getValueAt(i, 0)).equals(true) )
			  subData.add(true);
		  else
			  subData.add(false);
		  
		  attended.add(subData);
	  }
	  try
	  {
      term       = new Term(termID, eventID, date);
      reference.getEventPanel().getEvent().addTerm(term);
		  Workspace.getInstance().getDatabase().setAttendanceStatus(this.termID, attended);
      reference.getEventPanel().refreshTermsTable();      
		  return true;
	  }
	  catch(Exception sqlEx)
	  {
		  sqlEx.printStackTrace();
		  return false;
	  }
  }

  /**
   * Leere Interface-Methode.
   */
  public void addClicked()
  {
  }
  
  /**
   * Leere Interface-Methode.
   */
  public void removeClicked()
  {
  }
  
  /**
   * Wird vom Alles-Selektieren-Button aufgerufen.
   */
  public void selectAllClicked()
  {
    AttendantsFrame ap = (AttendantsFrame)getParent().getParent().getParent();
    prakman.view.Table tab = ap.getAttendantsTable();
          
    for(int i = 0; i < tab.getRowCount(); i++)
    {
      tab.setValueAt(true, i, 0);
    }
  }
  
  /**
   * Wird vom Alles-Deselektieren-Button aufgerufen.
   */
  public void deselectAllClicked()
  {
    AttendantsFrame ap = (AttendantsFrame)getParent().getParent().getParent();
    
    prakman.view.Table tab = ap.getAttendantsTable();
    
    for(int i = 0; i < tab.getRowCount(); i++)
    {
      tab.setValueAt(false, i, 0);
    }
  }
}
