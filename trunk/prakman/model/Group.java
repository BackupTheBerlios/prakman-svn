package prakman.model;

import java.sql.SQLException;

import prakman.io.Database;

/**
 * Repraesentiert eine Gruppe von Studenten.
 */
public class Group 
{
	private	Integer	groupID;
	private	String	description;
	
	/**
	 * Gruppe mit festgelegter ID und Beschreibung erstellen
	 * */
	public Group(Integer gID, String desc)
	{
		groupID 			= gID;
		this.description		= desc;
	}
	
	/** 
   * Hinzufuegen einer Person zu einer Gruppe
	 * @param Person (per), Group (this)
	 * @return true, falls das Einfuegen klappt, sonst false
	 * */
	public boolean add(Person per)
	{
    Database db = Workspace.getInstance().getDatabase();
    try
    {
      if (per instanceof Student)
      {
        db.addStudentToGroup((Student)per,this);
      }
  		else if (per instanceof Tutor)
  		{
  			db.addTutorToGroup((Tutor)per,this);
  		}
		  return true;
    }
    catch(SQLException e)	// tritt nur auf, wenn der Student/Tutor schon existiert.
    {
    	System.out.println("Person "+per.toString() + " schon in der Gruppe gelistet.");
      //e.printStackTrace();
      return false;
    }
	}
	
	/** 
   * Loeschen einer Person aus einer Gruppe
	 * @param person (per), Group (this)
	 * @return true, falls es klappt, false wenn nicht
	 * */
	public boolean delete(Person per)
	{
		Database db = Workspace.getInstance().getDatabase();
    try
    {
      if (per instanceof Student)
		  {
				db.removeStudentFromGroup((Student)per,this);
  		}
  		else if (per instanceof Tutor)
  		{
  				db.removeTutorFromGroup((Tutor)per,this);
  		}
    
		  return true;
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      return false;
    }
	}
	
	/**
	 * Schreibt die Daten der Gruppe in die Datenbank.
	 */
	public void updateGroup()
	{
		try
		{
			Workspace.getInstance().getDatabase().updateGroup(this.groupID, description);
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
	}
	
	/**
	 * Gruppe zu Event mit der ID eventID hinzufuegen.
	 * @param int eventID - ID des Events zu dem die Gruppe hinzukommt
	 */
	public void addToEvent(int eventID)
	{
		try
		{
			Workspace.getInstance().getDatabase().addGroupToEvent(eventID, description);
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
	}
	
	/**
	 * Gruppe in Event mit der ID eventID einfuegen
	 * Setzt die urspruengliche Gruppen-ID wieder ein.
	 * @param int eventID - ID des Events zu dem die Gruppe hinzukommt
	 */
	public void insertInEvent(int eventID)
	{
		try
		{
			Workspace.getInstance().getDatabase().addGroupToEvent(groupID, eventID, description);
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
	}
	
	/** Ganze Gruppe(Student) loeschen */
	public void deleteAllStudents()
	{
//	   students.clear();	
    System.out.println("TODO: deleteAllStudents()");
	}
	
	/** Ganze Gruppe(Tutor) loeschen */
	public void deleteAllTutors()
	{
//	   tutors.clear();	
    System.out.println("TODO: deleteAllTutors()");
	}
	
	/** Gruppen-ID zurueckgeben */
	public Integer getID()
	{
		return(groupID);
	}
	
	/** Beschreibung zurckgeben */
	public String getDesc()
	{
		return description;
	}
	
	/**
	 * Setzt die Beschreibung dieser Gruppe.
	 * @param String newDescription
	 */
	public void setDesc(String newDesc)
	{
		this.description = newDesc;
	}
}
