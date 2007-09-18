package prakman.model;

import java.sql.SQLException;
import java.sql.Timestamp;
import prakman.io.CSVPortable;
import prakman.io.Database;
import prakman.io.Saveable;

/**
 * Repraesentiert einen Studenten.
 */
public class Student 
  extends Person
  implements CSVPortable, Saveable
{
  private static final long serialVersionUID = 0;
  
  private int    matNr;
  private String email;
	
	/** Konstruktor */
	public Student(String lastName, String firstName, int matNr, String email, Timestamp date)
	{
	   this(lastName, firstName, matNr, email);
	   this.date      = date;
	}
  
  public Student(String lastName, String firstName, int matNr, String email)
  {
     this.lastName  = lastName;
     this.firstName = firstName;
     this.matNr     = matNr;
     this.email     = email;
  }
  
  public Student(int matNr)
  {
    this("<unbenannt>", "<unbenannt>", matNr, "unbekannte Adresse");
  }
  
  /**
   * Dieser Standardkonstruktor sollte nur vom CSVPorter verwenden.
   */
  public Student()
  {
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof Student)
    {
      Student std = (Student)obj;
      if(std.getMatNr() == getMatNr())
        return true;
    }
    return false;
  }
	
	/** Getter fuer die MatrikelNummer */
	public int getMatNr()
	{
		return this.matNr;
	}
	
	/** 
   * Gibt die E-Mail-Adresse des Studenten zurueck.
	 */
	public String getEmail()
	{
		return this.email;
	}
	
	/** Ausgabe im JTree */
  @Override
	public String toString()
	{
		return lastName + ", " + firstName; 
	}

	/** 
   * Diesen Studenten in die Datenbank eintragen oder, falls vorhanden, updaten
	 * @return boolean - true, falls speichern klappt, sonst false
	 **/ 
	public boolean save()
	{
		Database db = Workspace.getInstance().getDatabase();
		try
		{
			db.saveStudent(this);
      return true;
		}
		catch(SQLException sqlEx)
		{
      sqlEx.printStackTrace();
			return false;
		}
	}
  
  // --------------- CSVPortable Interface Methoden -------------------------
  
  /**
   * Gibt die Spaltenueberschriften fuer ein Student-Objekt zurueck.
   */
  public String[] getColumnNames()
  {
    String[] columnNames = {"Vorname", "Name","Email", "Matrikelnummer"};
    return columnNames;
  }
  
  /**
   * Gibt eine Zeile mit den Daten dieses Objekts zurueck.
   */
  public String[] getRow()
  {
    String[] row = {firstName, lastName, email, Integer.toString(matNr)};
    return row;
  }
  
  /**
   * Setzt die Daten dieses Objekts auf die Werte in der uebergebenen Zeile.
   */
  public void setRow(String[] row)
  {
    this.firstName = row[0];
    this.lastName  = row[1];
    this.email     = row[2];
    this.matNr     = Integer.parseInt(row[3]);
  }
  
  /**
   * Setzt die Matrikelnummer des Studenten.
   * @param id
   */
  public void setMatNr(int id)
  {
    this.matNr = id;
  }
}
