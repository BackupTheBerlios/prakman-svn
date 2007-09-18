package prakman.model;

import java.sql.*;

import javax.swing.tree.DefaultMutableTreeNode;
import prakman.io.CSVPortable;
import prakman.io.Saveable;
import prakman.view.TreeLeaf;

/**
 * Basisklasse von Student und Tutor.
 */
public abstract class Person 
  extends DefaultMutableTreeNode
  implements CSVPortable, Saveable, TreeLeaf
{
	protected String    lastName   = "<lastname>";
	protected String    firstName  = "<firstname>";
	protected Timestamp date       = new Timestamp(System.currentTimeMillis());
	
	/** Setzt den Nachnamen der Person */
	public void setLastName(String lastName, Timestamp date)
	{
	  this.lastName = lastName;
    this.date = date;
	}
  
  /** Setzt den Nachnamen der Person */
  public void setLastName(String lastName)
  {
    this.lastName = lastName;   
  }
  
	/** Setter Vorname */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	/** Getter Nachname */
	public String getLastName()
	{
		return lastName;
	}
	/** Getter Vorname */
	public String getFirstName()
	{
		return firstName;
	}
	/** Getter Datum */
	public Timestamp getDate()
	{
		return date;
	}
  
	/** Abstrakte Save-Methode (es gibt keine "Personen" in der Datenbank) */
	public abstract boolean save();
  
  /** Ausgabe im JTree */
  public String toString()
  {
    return lastName + ", " + firstName; 
  }
}
