package prakman.model;

import java.sql.Timestamp;

/**
 * Representiert ein Projekt einer Veranstaltung.
 */
public class Project
{
  private String  description;
  private int     id;
  private Timestamp    startDate, endDate;
  
  public Project(int id, String description, Timestamp start, Timestamp end)
  {
    this.description = description;
    this.id          = id;
    this.startDate   = start;
    this.endDate     = end;
  }

  /**
   * @return Beschreibung des Projekts.
   */
  public String getDescription()
  {
    return this.description;
  }

  /**
   * @return ID des Projekts.
   */
  public int getID()
  {
    return this.id;
  }

  public Timestamp getEndDate()
  {
    return this.endDate;
  }
  
  public Timestamp getStartDate()
  {
    return this.startDate;
  }
}
