package prakman.model;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.tree.DefaultMutableTreeNode;
import prakman.io.CSVPortable;
import prakman.io.Saveable;
import prakman.view.TreeLeaf;

/**
 * Event-Klasse beinhaltet Informationen ueber Veranstaltungen.
 */
public class Event 
  extends DefaultMutableTreeNode
  implements CSVPortable, Saveable, TreeLeaf
{
  private static final long serialVersionUID = 0;
  
  private int       eventID;
  private String    eventName;
  private String    desc 		   = "";
  private Timestamp date;
  private Tutor	    tutor	= null;

	/** Konstruktor, der alle Werte festlegt */
	public Event(String eventName, String desc, Timestamp date, Tutor tutor, int eventID)
	{
		this.eventName 	  = eventName;
		this.desc         = desc;
		this.date         = date;
		this.tutor        = tutor;
		this.eventID      = eventID;
	}
	
	/** Konstruktor, fuer spezielle Anwendungen*/
	public Event(String eventName, String desc, Tutor tutor, int eventID)
	{
		this(eventName, desc, new Timestamp(new Date().getTime()), tutor, eventID);
	}
  
  /**
   * Dieser Konstruktor sollte nur vom CSVPorter verwendet werden.
   */
  public Event()
  {
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof Event)
    {
      Event eve = (Event)obj;
      if(eve.getID() == getID()
          && eve.getName().equals(getName()))
        return true;
    }
    return false;
  }
	
	// Methoden, um Werte im Nachhinein zu setzen
	
	/** Beschreibung setzen */
	public void setDesc( String desc )
	{
		this.desc	= desc;
	}
	
	/** Gibt Name des Events zurueck */
	public String getName()
	{
		return this.eventName;
	}
  
  /**
   * Setzt den Namen des Events.
   * @param name
   */
  public void setName(String name)
  {
    this.eventName = name;
  }
  
  /**
   * Setzt den Tutor des Events.
   * @param tut
   */
  public void setTutor(Tutor tut)
  {
    this.tutor = tut;
  }
  
  /**
   * Setzt die ID des Events.
   * @param id
   */
  public void setID(int id)
  {
    this.eventID = id;
  }
	
	/** Gibt die Beschreibung des Events zurueck. */
	public String getDesc()
	{
		return(this.desc);
	}
  
  /** Gibt den Tutor des Events zurueck. */
  public Tutor getTutor()
  {
    return this.tutor;
  }
  
  /** Gibt die ID der Veranstaltung zurueck. */
  public int getID()
  {
	  return(this.eventID);
  }
  
  /** Gibt das Aenderungsdatum des Event-Objekts zurueck. */
  public Timestamp getDate()
  {
	  return this.date;
  }
	
	/** Person hinzufuegen */
	public void add( Student stu )
	{
		try
		{
			Workspace.getInstance().getDatabase().addStudentToEvent(stu, this);			
		}
		catch(SQLException ex)
    {
      ex.printStackTrace();
    }
	}
  
  /** Einen Studenten zum Event hinzufuegen. */
  public void add( int eventID, int studID )
  {
    try
    {
      Workspace.getInstance().getDatabase().addStudentToEvent(eventID, studID);     
    }
    catch(SQLException ex)
    {
      ex.printStackTrace();
    }
  }
	
	/**
	 * Gruppe zum Event hinzufuegen.
	 * @param String desc - Beschreibung der neuen Gruppe
	 */
	public void addGroup(String desc)
	{
		try
		{
			Workspace.getInstance().getDatabase().addGroupToEvent(this.eventID, desc);
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
	}
	
	/**
	 * Termin zum Event hinzufuegen.
	 * @param String desc - Beschreibung der neuen Gruppe
	 */
	public void addTerm(Term term)
	{
		try
		{
			Workspace.getInstance().getDatabase().addTermToEvent(term, this);
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
	}
	
	/**
	 * Projekt zum Event hinzufuegen.
	 * @param String desc - Beschreibung der neuen Gruppe
	 */
	public void addProject(int projectID, String desc)
	{
		try
		{
			Workspace.getInstance().getDatabase().addProjectToEvent(projectID, this, desc);
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
	}
	
	/**
	 * Gruppe aus dem Event loeschen.
	 * @param int groupID - ID der zu loeschenden Gruppe
	 */
	public void removeGroup(int groupID)
	{
		try
		{
			Workspace.getInstance().getDatabase().removeGroup(groupID);
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
	}
	 
  /** Ausgabe im JTree */
  @Override
  public String toString()
  {
    return eventName + ", " + tutor.getLastName(); 
  }
	
  /** 
   * Speichert die Aenderungen an diesem Event.
   */
	public boolean save()
  {
    try
    {
      Workspace.getInstance().getDatabase().saveEvent(this);
      return true;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }
    
  // --------------- CSVPortable Interface Methoden -------------------------
  
  /**
   * Gibt die Spaltenueberschriften fuer den CSV-Export/-Import zurueck.
   */
  public String[] getColumnNames()
  {
    String[] columnNames = {"ID", "Name", "Professor","Beschreibung"};
    return columnNames;
  }
  
  /**
   * Gibt eine Datenzeile zurueck, welche die Daten des Events enthaelt.
   */
  public String[] getRow()
  {
    String[] row = {Integer.toString(eventID), this.eventName, this.tutor.lastName, this.desc};
    return row;
  }
  
  /**
   * Setzt die Daten des Events auf die Werte der uebergebenen Zeile.
   */
  public void setRow(String[] row)
  {
    this.eventID    = Integer.parseInt(row[0]);
    this.eventName  = row[1];
    this.tutor      = new Tutor();  // TODO: Korrekten Tutor wieder hinzufuegen
    this.tutor.setLastName(row[2]);
    this.desc       = row[3];
  }
}
