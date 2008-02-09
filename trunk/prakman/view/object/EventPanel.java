package prakman.view.object;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.TableColumn;
import prakman.io.Database;
import prakman.io.Saveable;
import prakman.model.Event;
import prakman.model.Group;
import prakman.model.Student;
import prakman.model.Term;
import prakman.model.TableModel;
import prakman.model.Workspace;
import prakman.view.AttendantsFrame;
import prakman.view.BasePanel;
import prakman.view.GroupFrame;
import prakman.view.ListFrame;
import prakman.view.MainFrame;
import prakman.view.ProjectFrame;
import prakman.view.Table;
import prakman.view.inset.SaveResetEditPanel;
import prakman.view.inset.TabbedEditable;
import prakman.view.inset.TabbedEditPanel;
import prakman.view.menu.StudentToGroupMenu;
import prakman.view.menu.PrintGroupMenu;
import prakman.model.Tutor;

public class EventPanel 
  extends BasePanel 
  implements TabbedEditable
{
  private static final long serialVersionUID = 0;
  private static final String[] termColumnNames   = { "#", "Matr-Nr.", "Datum", "Teilnehmer" };
  private static final String[] prakColumnNames   = { "#", "Matr-Nr.", "Nachname", "Vorname", "Gruppe" };
  private static final String[] resultColumnNames = { "#", "Beschreibung", "Vergeben am", "Abzugeben am", "ID" };
  private static final String[] groupsColumnNames = { "#", "Nummer", "Beschreibung" };
  
  private EventPanel thisPanel;	// Referenz auf das aktuelle EventPanel

  private Cursor      listViewCursor  = new Cursor(Cursor.HAND_CURSOR);
  private Event       event;
  private JTextField  txtEventName;
  private JComboBox   cmbTutorName      = new JComboBox();
  private JTextField  txtEventDesc;
  private JPanel      eventContentPanel = new JPanel();
  
  private JScrollPane eventMemberPanel	= new JScrollPane();
  private JScrollPane eventResultPanel	= new JScrollPane();
  private JScrollPane eventTermsPanel		= new JScrollPane();
  private JScrollPane	eventGroupsPanel 	= new JScrollPane();
  
  private JTabbedPane tabbedPane 	= new JTabbedPane();
  private ArrayList<Tutor> cmbContent;
  private Table terms;
  private Table praks;
  private Table results;
  private Table groups;
  
  /** Konstruktor */
  public EventPanel(Event e)
  {
    this.event = e;
    this.thisPanel = this;
    setLayout(new BorderLayout());
    //  Felder fuellen
    txtEventName   = new JTextField(((prakman.model.Event)event).getName());
    
    try
    {
      cmbContent = Workspace.getInstance().getDatabase().getTutors();
    }
    catch (Exception err) 
    {
      err.printStackTrace();
    }
    // JComboBox fuellen
    for(Tutor t : cmbContent)
      cmbTutorName.addItem(t.toString());
    
    // JComboBox auf richtigen Wert setzen
    cmbTutorName.setSelectedItem(((prakman.model.Event)event).getTutor().toString());
    
    //txtTutorName   = new JTextField(((prakman.model.Event)event).getTutor().getLastName());
    txtEventDesc	 = new JTextField(((prakman.model.Event)event).getDesc());
        
    GridBagLayout      gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    
    eventContentPanel.setLayout(gbl);    
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    
    gbc.weightx = 0.7; // Wir lassen links und rechts einen kleinen Rand frei
    gbc.weighty = 0.7; // Wir lassen oben und unten einen kleinen Rand frei
    gbc.gridy = 1;
    gbc.gridx = 1;
    eventContentPanel.add(new SaveResetEditPanel(this),gbc);
    gbc.gridy = 2;
    eventContentPanel.add(new JLabel("Veranstaltung"), gbc);
    gbc.gridx = 2;
    eventContentPanel.add(txtEventName, gbc);
    gbc.gridx = 1;
    gbc.gridy = 3;
    eventContentPanel.add(new JLabel("Verantwortlicher Professor"), gbc);
    gbc.gridx = 2;
    eventContentPanel.add(cmbTutorName, gbc);
    gbc.gridx = 1;
    gbc.gridy = 4;
    eventContentPanel.add(new JLabel("Beschreibung"), gbc);
    gbc.gridx = 2;
    eventContentPanel.add(txtEventDesc, gbc);
    
    // eventMemberPanel erstellen -----------------------------
    this.refreshPraksTable();
    // eventResultPanel erstellen -----------------------------
    this.refreshProjectsTable();
    // eventTermsPanel erstellen -----------------------------
    this.refreshTermsTable();
    // eventGroupsPanel erstellen ---------------------------------
    this.refreshGroupsTable();	
    
    // -------------------------- Tabbed Pane ---------------------
    
    tabbedPane.addTab("Teilnehmer", eventMemberPanel);
    tabbedPane.addTab("Projekte", eventResultPanel);
    tabbedPane.addTab("Termine", eventTermsPanel);
    tabbedPane.addTab("Gruppen", eventGroupsPanel);
    
    this.add(eventContentPanel, BorderLayout.NORTH);
    this.add(tabbedPane, BorderLayout.CENTER);
    this.add(new TabbedEditPanel(this), BorderLayout.SOUTH);
  }
  
  /** Equals ueberschreiben */
  public boolean equals(Object obj)
  {
    if(obj instanceof EventPanel)
      return this.getID() == ((EventPanel)obj).getID();
    else
      return false;
  }
  
  /** Getter: Identifikation */
  public int getID()
  {
	  return(event.getID());
  }
  
  /** Getter: EventName */
  public String getEventName()
  {
	  return(txtEventName.getText());
  }
  
  /*
  public String getTutorLastName()
  {
	  return(txtTutorName.getText());
  }
  */
  /** Getter: Date */
  public Timestamp getDate()
  {
	  return(event.getDate());
  }
  
  /** Getter: Desc */
  public String getDesc()
  {
	  return(txtEventDesc.getText());
  }
  
  /** Getter: Event */
  public Event getEvent()
  {
	  return(event);
  }

  /** Setter: txtEventDesc */
  public void setTxtEventDesc(String t)
  {
	  txtEventDesc.setText(t);
  }
  
  /** Setter: txtEventName */
  public void setTxtEventName(String t)
  {
    System.out.println("setter");
	  txtEventName.setText(t);
  }
  
  /**
   * Table getGroupsTable()
   * 
   * Gibt die Gruppen-Tabelle zurueck, damit ausgewaehlte Gruppen geloescht werden koennen
   * @return Table groups
   * @author lys
   */
  public Table getGroupsTable()
  {
  	return(this.groups);
  }
  
  /**
   * Table getTermTable()
   * 
   * Gibt die Termin-Tabelle zurueck, damit ausgewaehlte Termine geloescht werden koennen
   * @return Table terms
   * @author AD
   */
  public Table getTermsTable()
  {
  	return(this.terms);
  }
  
  /**
   * Table getStudentsTable()
   * 
   * Gibt die Studenten-Tabelle zurueck, damit ausgewaehlte Studenten geloescht werden koennen
   * @return Table students
   * @author AD
   */
  public Table getStudentsTable()
  {
  	return(this.praks);
  }
  
  /**
   * Table getProjectsTable()
   * 
   * Gibt die Projekt-Tabelle zurueck, damit ausgewaehlte Projekte geloescht werden koennen
   * @return Table projects
   * @author AD
   */
  public Table getProjectsTable()
  {
  	return(this.results);
  }
  
  /**
   * refreshGroupsTable()
   * 
   * Aktualisiert die Daten im groups-Table, damit dieser neu angezeigt werden kann
   * 
   * @author PR
   */
  public void refreshGroupsViewPane()
  {
  	ArrayList<ArrayList<Object>> groupData = new ArrayList<ArrayList<Object>>();
		 
		try
		{			 
			// alle im Event gelisteten Gruppen auslesen
			ArrayList<Group> groupInfo = Workspace.getInstance().getDatabase().getGroupsInEvent(event.getID());
			ArrayList<Object> subData = null;

			for(Group g : groupInfo)
			{
				subData = new ArrayList<Object>();
				
				subData.add(false);			// Checkbox zum Gruppe loeschen
				subData.add(g.getID());	// Gruppennummer
				subData.add(g.getDesc());	// Gruppenbeschreibung
				
				groupData.add(subData);
			}
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
    
    TableModel groupTm = new TableModel(groupsColumnNames, groupData);
    groups  = new Table(groupTm);
    
    groups.addMouseListener(new MouseAdapter() 
      {
        public void mouseExited(MouseEvent e)
        {
          setCursor(Cursor.getDefaultCursor());
        }
        
        public void mouseEntered(MouseEvent e)
        {
          setCursor( listViewCursor );
        }
        
        // Wichtig um Panel mit Studenten zu oeffnen
        public void mouseClicked(MouseEvent e)
        {
          // Popup-Menu
          if (e.getModifiers() == 4)  // bei Rechtsklick
          {
            PrintGroupMenu pg = new PrintGroupMenu(
                (Integer)groups.getValueAt(groups.rowAtPoint(e.getPoint()), 1));
            pg.show(e.getComponent(), e.getX(), e.getY());
          } 
          else if (e.getClickCount() > 1)
          {
            GroupFrame gf = new GroupFrame(
                (Integer)groups.getValueAt(groups.rowAtPoint(e.getPoint()), 1));
            gf.addWindowListener(new WindowAdapter() 
              {
                public void windowClosed(WindowEvent e) 
                {
                  thisPanel.refreshGroupsTable();
                }
              });
          }
        }
      });
  }
  
  /**
   * refreshGroupsTable()
   * 
   * Table refreshen, der die Gruppen im Event anzeigt
   * 
   * @author PR
   */
  public void refreshGroupsTable()
  {
  	this.refreshGroupsViewPane();
    eventGroupsPanel.setViewportView(groups);
  }
  
// Aufbauen des Praktikum-Tables
  
  /**
   * refreshPraksTable()
   * 
   * Table refreshen, der die Mitglied des Events anzeigt
   * 
   * @author PR
   */
  public void refreshPraksTable()
  {
    ArrayList<ArrayList<Object>> memberData = new ArrayList<ArrayList<Object>>();
    try 
    {
   	  // Teilnehmer aus der Datenbank holen
   	  Database db 	= Workspace.getInstance().getDatabase();
   	  ArrayList<Student> studentData 	= db.getStudentsInEvent((event).getID());
   	  
   	  // Daten in ausgabefaehiges Format umkopieren
   	  for(Student std : studentData)
   	  {
   		  ArrayList<Object> subData = new ArrayList<Object>();
   		  subData.add(false);
   		  subData.add(std.getMatNr());
   		  subData.add(std.getLastName());
   		  subData.add(std.getFirstName());
   		 
        // Hier zusaetzlich Infos ueber die Gruppe des Studenten holen
   		  Group memberGroup = db.getEventGroup(std, event.getID());
   		  if (memberGroup != null)
   		  	subData.add(memberGroup.getID());
   		  else
   		  	subData.add(0);
   		 	
   		  memberData.add(subData);
   	  }
    }
    catch(SQLException sqlEx)
    {
   	  sqlEx.printStackTrace();
    }
    
    TableModel prakTm = new TableModel(prakColumnNames, memberData);
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
        // Popup-Menu
        if (e.getModifiers() == 4)  // bei Rechtsklick
        {
          // Selektierte Zeilen bzw. die darin gelisteten Matr-Nummern holen
          ArrayList<Integer> matNrn = new ArrayList<Integer>();
          for(int i : praks.getSelectedRows())
          {
            matNrn.add((Integer)praks.getValueAt(i, 1));
          }
          StudentToGroupMenu pm = new StudentToGroupMenu(thisPanel, matNrn);
          pm.show(e.getComponent(), e.getX(), e.getY());
        }
        // Doppelklick -> Studenten oeffnen
        if (e.getClickCount() > 1)
        {
          MainFrame.getInstance().getTabbedPane().addTab(
              Workspace.getInstance().findStudentByID(
                  (Integer)praks.getValueAt(praks.rowAtPoint(e.getPoint()), 1))
              );
        }
      }
 	
    });
    
    eventMemberPanel.setViewportView(praks);
  }
  
  /**
   * refreshTermsTable()
   * 
   * Table refreshen, der die Mitglied des Events anzeigt
   * 
   * @author AD
   */
  public void refreshTermsTable()
  {
	  ArrayList<ArrayList<Object>> termPanelData = new ArrayList<ArrayList<Object>>();
	    try 
	    {
	   	 // SimpleDateFormat fuer gute Datumsdarstellung
			 SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
			  
	   	 // Teilnehmer aus der Datenbank holen
	   	 ArrayList<Term> termData 	= Workspace.getInstance().getDatabase().getTermsInEvent(((Event)event).getID());
	   	  
	   	 // Daten in ausgabefaehiges Format umkopieren
	   	 for(Term trm : termData)
	   	 {
	   		 ArrayList<Object> subData = new ArrayList<Object>();
	   		 subData.add(trm.getTermID());
	   		 subData.add(false);
	   		 subData.add(sdf.format(trm.getDate()));
	   		 subData.add(Workspace.getInstance().getDatabase().countActualAttendantsInEventPerTerm(trm.getEventID(), trm.getTermID())
	   		 + " / " + Workspace.getInstance().getDatabase().countExpectedAttendantsInEvent(((Event)event).getID()));
	  
	   		 termPanelData.add(subData);
	   	 }
	    }
	    catch(SQLException sqlEx)
	    {
	   	  sqlEx.printStackTrace();
	    }
	    
	    TableModel termTm = new TableModel(termColumnNames, termPanelData);
	    terms  = new Table(termTm);
	    
	    // Spaltenbreite setzen bzw. termID ausblenden
	    setTableSize();
	    
	    terms.addMouseListener( new MouseListener() {
	   	// Unwichtige Events
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
	 		public void mouseExited(MouseEvent e)
	 		{
	 			setCursor(Cursor.getDefaultCursor());
	 		}
	 		public void mouseEntered(MouseEvent e)
	 		{
	 			setCursor( listViewCursor );
	 		}
			// Wichtig um Panel mit Studenten zu oeffnen
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() > 1)
				{
          try
          {
  					//new AttendantsFrame( event.getID(), (Integer)terms.getValueAt(terms.rowAtPoint(e.getPoint()),0 ),thisPanel );
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
            String dateStr = terms.getValueAt(terms.rowAtPoint(e.getPoint()), 2).toString();
            
            new AttendantsFrame(
                event.getID(), 
                (Integer)terms.getValueAt(terms.rowAtPoint(e.getPoint()),0 ), 
                new Timestamp(sdf.parse(dateStr).getTime()),
                thisPanel);
          }
          catch(Exception ex)
          {
            ex.printStackTrace();
          }
				}
			}
		
		});
	    
	    eventTermsPanel.setViewportView(terms);
	    
	     
  }
 
  /**
   * refreshProjectsTable()
   * 
   * Table refreshen, der die Mitglied des Events anzeigt
   * 
   * @author AD
   */
  public void refreshProjectsTable()
  {
	  ArrayList<ArrayList<Object>> resultData = new ArrayList<ArrayList<Object>>();

	  // Teilnehmer aus der Datenbank holen
	  Database db 	= Workspace.getInstance().getDatabase();
	  
	  ArrayList<Integer> projects	= db.getProjectsInEvent(event.getID());
	  
	  // Beschreibung der Projekte holen und in ausgabefaehiges Format
	  // umkopieren
	  
	  for(int proj : projects)
	  {
		  ArrayList<Object> subData = new ArrayList<Object>();
		  subData.add(false);
		  subData.add(db.getProjectDesc(proj));
		  // Datum zusammensetzen
		  try
		  {
			  // SimpleDateFormat fuer gute Datumsdarstellung
			  SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			  
			  // Vergabedatum
			  Timestamp date = db.getProjectDate(proj);
			  
			  //Problem ist das hier manchmal auch nix drin ist!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			  subData.add(sdf.format(date));
			  // Abgabedatum
			  // Das Abgabedatum kann nicht festgelegt sein. In dem Fall: Open End
			  Timestamp deadline = db.getProjectDeadline(proj);
			  if (deadline == new Timestamp(Long.MAX_VALUE) 
			  	|| deadline == null)	// es gibt keine feste Deadline
			  {
			  	subData.add("Unbegrenzt");
			  }
			  else
			  {
			  	subData.add(sdf.format(deadline));
			  }
			  subData.add(proj);//ID
		  }
		  catch(SQLException sqlEx)
		  {
			  sqlEx.printStackTrace();
		  }

		  resultData.add(subData);
	  }
    
    TableModel resultTm = new TableModel(resultColumnNames, resultData);
    results  			= new Table(resultTm);
   
    eventResultPanel.setViewportView(results);
     
    results.addMouseListener(new MouseAdapter() 
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
   		    // Popup-Menu
   		    if (e.getModifiers() == 4)  // bei Rechtsklick
   		    {
   		      System.out.println("Rechtsklick");
   		    } 
   		    else if (e.getClickCount() > 1)
   		    {
   		      new ProjectFrame((Integer)results.getValueAt(results.rowAtPoint(e.getPoint()), 4), 
                getID(), thisPanel);
   		    }
   		  }
      });
  }
  
  /**
   * Spalte termID ausblenden
   */
  public void setTableSize()
  {
    // Spaltenbreite der Checkboxen setzen
    TableColumn tc0 = terms.getColumn(termColumnNames[0]); // termID
    TableColumn tc1 = results.getColumn(resultColumnNames[4]);//Projekt ID
    
    tc0.setMinWidth(0);
    tc0.setMaxWidth(0); 
    
    tc1.setMinWidth(0);
    tc1.setMaxWidth(0); 
  }
  
  public Saveable getSaveable()
  {
    // Das Objekt wird geupdatet...
    event.setDesc(txtEventDesc.getText());
    event.setName(txtEventName.getText());
    for(Tutor i : cmbContent)
      if(i.toString().equals(cmbTutorName.getSelectedItem().toString()))       
        event.setTutor(cmbContent.get(cmbContent.indexOf(i)));
    // und zurueckgegeben
    return event;
  }
  
  public void reset()
  {
    txtEventDesc.setText(event.getDesc());
    txtEventName.setText(event.getName());
  }
  
  public void addClicked()
  {
    // Student hinzufuegen
    if(tabbedPane.getSelectedIndex() == 0)
    {
      TableModel tm = null;
      String[] termColumnNames  = { "#", "MatNr", "Nachname", "Vorname" };
      try
      {
        //TODO:
        tm = new TableModel(termColumnNames, Workspace.getInstance().getDatabase().getStudentsForTableModel(getEvent().getID()));
        new ListFrame(
               tm,
               "Studenten hinzufuegen",
               this
               );        
      }
      catch(SQLException ex)
      {
        ex.printStackTrace();
      }         
    }
    else if(tabbedPane.getSelectedIndex() == 3)
    {
      // Gruppe hinzufuegen
      EventPanel ep = this;
      ep.getEvent().addGroup("Ohne Beschreibung");
      ep.refreshGroupsTable();
      JOptionPane.showMessageDialog(null, "Neue Gruppe hinzugef\u00FCgt");
    }
    else if(tabbedPane.getSelectedIndex() == 1) // Projekt hinzufuegen
    {
      try
      {
        EventPanel ep = this;
        int newID = Workspace.getInstance().getDatabase().getNewProjectID();
        ep.getEvent().addProject(newID, "<unbenannt>");
        new ProjectFrame(newID, getID(), this);
        ep.refreshProjectsTable();
      }
      catch(SQLException ex)
      {
        ex.printStackTrace();
      }
    }
    else if(tabbedPane.getSelectedIndex() == 2) // Termin hinzufuegen
    {
      try
      {
        int   termID = Workspace.getInstance().getDatabase().getNewTermID();         
        new AttendantsFrame(getEvent().getID(), termID, new Timestamp(new Date().getTime()), this);
      }
      catch(SQLException ex)
      {
        ex.printStackTrace();
      }
    }
  }
  
  public void removeClicked()
  {
    if(tabbedPane.getSelectedIndex() == 0)
    {
      // Student entfernen
      EventPanel ep = (EventPanel)this;
      Table students  = ep.getStudentsTable();
      Database db   = Workspace.getInstance().getDatabase();
      boolean del   = false;
      try
      {
        for(int i = 0; i < students.getRowCount(); i++)
        {
          if ((Boolean)(students.getValueAt(i, 0))==true) // Checkbox "Loeschen" ausgewaehlt?
          {
            del = true;
            //db.removeGroup((Integer)(groups.getValueAt(i, 1)));
            db.removeStudentFromEvent((Integer)(students.getValueAt(i, 1)), ep.getEvent().getID());
          }
        }
        if (del)
          ep.refreshPraksTable();
        JOptionPane.showMessageDialog(null, "Student erfolgreich gel\u00F6scht");
      }
      catch(SQLException sqlEx)
      {
        sqlEx.printStackTrace();
      }
    }
    else if(tabbedPane.getSelectedIndex() == 3)
    {
      // Gruppe entfernen
      EventPanel ep = (EventPanel)this;
      Table groups  = ep.getGroupsTable();
      Database db   = Workspace.getInstance().getDatabase();
      boolean del   = false;
      try
      {
        for(int i = 0; i < groups.getRowCount(); i++)
        {
          if ((Boolean)(groups.getValueAt(i, 0))==true) // Checkbox "Loeschen" ausgewaehlt?
          {
            del = true;
            db.removeGroup((Integer)(groups.getValueAt(i, 1)));
          }
        }
        if (del)
          ep.refreshGroupsTable();
        JOptionPane.showMessageDialog(null, "Gruppe erfolgreich gel\u00F6scht");
      }
      catch(SQLException sqlEx)
      {
        sqlEx.printStackTrace();
      }
    }
    else if(tabbedPane.getSelectedIndex() == 1)
    {  
      // Projekt entfernen
      EventPanel ep = (EventPanel)this;
      Table projects  = ep.getProjectsTable();
      Database db   = Workspace.getInstance().getDatabase();
      boolean del   = false;
      try
      {
        for(int i = 0; i < projects.getRowCount(); i++)
        {
          if ((Boolean)(projects.getValueAt(i, 0))==true) // Checkbox "Loeschen" ausgewaehlt?
          {
            del = true;
            db.removeProject((Integer)(projects.getValueAt(i, 4)));
          }
        }
        if (del)
          ep.refreshProjectsTable();
        JOptionPane.showMessageDialog(this, "Projekt erfolgreich gel\u00F6scht");
      }
      catch(SQLException sqlEx)
      {
        sqlEx.printStackTrace();
      }
    }
    else if(tabbedPane.getSelectedIndex() == 2)
    {
      // Termin entfernen
      EventPanel ep = (EventPanel)this;
      Table terms = ep.getTermsTable();
      Database db   = Workspace.getInstance().getDatabase();
      boolean del   = false;
      try
      {
        for(int i = 0; i < terms.getRowCount(); i++)
        {
          if ((Boolean)(terms.getValueAt(i, 1)) == true)  // Checkbox "Loeschen" ausgewaehlt?
          {
            del = true;
            db.removeTerm( (Integer)(terms.getValueAt(i, 0)));
          }
        }
        if (del)
          ep.refreshTermsTable();
          JOptionPane.showMessageDialog(null, "Termin erfolgreich gel\u00F6scht");
      }
      catch(SQLException sqlEx)
      {
        sqlEx.printStackTrace();
      }
    }
  }
  
  
  
  public void selectDeselectAll(boolean status)
  {
    switch(tabbedPane.getSelectedIndex()) 
    {
    case 0:
      for(int i = 0; i<getStudentsTable().getRowCount(); i++)
        getStudentsTable().getModel().setValueAt(status, i, 0);   
        break; 
    case 1:
      for(int i = 0; i<getProjectsTable().getRowCount(); i++)
        getProjectsTable().getModel().setValueAt(status, i, 0);   
        break; 
    case 2:
      for(int i = 0; i<terms.getRowCount(); i++)
        terms.getModel().setValueAt(status, i, 1);   
        break;
    case 3:
      for(int i = 0; i<getGroupsTable().getRowCount(); i++)
        getGroupsTable().getModel().setValueAt(status, i, 0);   
        break;      
    default:        
        break;  
    }
  }
  
  /** alle auswaehlen */
  public void selectAllClicked()
  {    
    selectDeselectAll(true);
  }    
  
  public void deselectAllClicked()
  {
    selectDeselectAll(false);
  }
}
