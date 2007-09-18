package prakman.model;

import java.sql.SQLException;
import java.sql.Timestamp;

import prakman.io.Database;
import prakman.io.Saveable;

/**
 * Repraesentiert einen Tutor.
 */
public class Tutor 
  extends Person
  implements Saveable
{
  private static final long serialVersionUID = 0;
  
	private int tutorID = -1;
	
	/** Konstruktor zum erstellen eines Professor-Objekts */
	public Tutor(int tutID, String lastName, String firstName, Timestamp date)
	{
		this.lastName  = lastName;
		this.firstName = firstName;
		this.tutorID	= tutID;
		this.date		= date;
	}
  
  public Tutor(int tutID, String lastName)
  {
    this.tutorID  = tutID;
    this.lastName = lastName;
  }

  public Tutor(int tutID, String lastName, String firstname)
  {
    this.tutorID  = tutID;
    this.lastName = lastName;
    this.firstName = firstname;
  }
  
  public Tutor()
  {
  }
  
  public boolean equals(Object obj)
  {
    if(obj instanceof Tutor)
    {
      Tutor tut = (Tutor)obj;
      if(tut.getID() == getID() &&
          tut.getFirstName().equals(getFirstName()) &&
          tut.getLastName().equals(getLastName()))
        return true;
    }
    return false;
  }
	
  /**
   * @return Die ID dieses Tutors.
   */
	public int getID()
	{
		return tutorID;
	}
  
  /**
   * Setzt die ID des Tutors.
   * @param id
   */
  public void setID(int id)
  {
    this.tutorID = id;
  }

	/** 
   * Diesen Tutor in die Datenbank eintragen oder, falls vorhanden, updaten
	 * @return boolean - true, falls speichern klappt, sonst false
	 **/
	public boolean save()
	{
		Database db = Workspace.getInstance().getDatabase();
		try
		{
			db.saveTutor(this);
		}
		catch(SQLException sqlEx)
		{
			return false;
		}
		return true;
	}
  
  // --------------- CSVPortable Interface Methoden -------------------------
  
  /**
   * Gibt die Spaltennamen fuer die Tutor-Objekte zurueck.
   */
  public String[] getColumnNames()
  {
    String[] columnNames = {"Vorname", "Name", "ID"};
    return columnNames;
  }
  
  /**
   * Gibt eine Datenzeile mit den Daten dieses Tutor-Objekts zurueck.
   */
  public String[] getRow()
  {
    String[] row = {firstName, lastName, Integer.toString(tutorID)};
    return row;
  }
  
  /**
   * Setzt die Daten des Objekts auf die Werte der uebergebenen Zeile.
   */
  public void setRow(String[] row)
  {
    this.firstName = row[0];
    this.lastName  = row[1];
    this.tutorID   = Integer.parseInt(row[2]);
  }
}
