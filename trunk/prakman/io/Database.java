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

package prakman.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.*;
import prakman.Config;
import prakman.model.*;

/**
 * Diese Klasse stellt das Bindeglied zwischen der Model-Architektur und der in
 * Datenbank im Hintergrund dar. Alle Abfragen der Packages model bzw view
 * laufen über diese Klasse.
 */
public class Database
{
  public static final String CONFIG_HOST         = "DatabaseHost";
  public static final String CONFIG_PORT         = "DatabasePort";
  public static final String CONFIG_DRIVER       = "DatabaseDriver";
  public static final String CONFIG_DATABASE     = "DatabaseData";
  public static final String CONFIG_USER         = "DatabaseUser";
  public static final String CONFIG_PASSWORD     = "DatabasePassword";
  public static final String CONFIG_TABLE_PREFIX = "DatabaseTablePrefix";
  public static final String DEFAULT_HOST        = "jdbc:hsqldb:file:";
  public static final String DEFAULT_PORT        = "";
  public static final String DEFAULT_DRIVER      = "org.hsqldb.jdbcDriver";
  public static final String DEFAULT_DATABASE    = "prakman";
  //public static final String DEFAULT_DATABASE    = "prakman?useUnicode=true&characterEncoding=utf-8";
  public static final String DEFAULT_USER        = "localhost";
  public static final String DEFAULT_PASSWORD    = "";
  public static final String DEFAULT_PREFIX      = "pm";
  private Connection         connection;
  //private Statement          stmt;
  private Workspace          workspace;

  /**
   * @param workspace Notwendig fuer die Datenbank-Einstellungen.
   */
  public Database(Workspace workspace) throws ClassNotFoundException
  {
    this.workspace = workspace;
    String driver = workspace.getConfig().get(CONFIG_DRIVER, DEFAULT_DRIVER);
    Class.forName(driver);
  }

  /**
   * Schliesst die Datenbank-Verbindung und beendet die interne Datenbank.
   * @throws SQLException
   */
  public void shutdown() throws SQLException
  {
    Statement stmt = connection.createStatement();
    if (stmt != null)
    {
      try
      {
        stmt.execute("SHUTDOWN"); // Dieser Befehl wird nur von HSQLDB unterstuetzt
      }
      catch(Exception e){} // Ignoriere Exception
    }
    this.disconnect();
  }

  /**
   * Stellt die Datenbankverbindung her.
   * @throws SQLException
   */
  public void connect() throws SQLException
  {
    //System.out.println("Datebase::connect()");
    String host = workspace.getConfig().get(CONFIG_HOST, DEFAULT_HOST);
    String user = workspace.getConfig().get(CONFIG_USER, DEFAULT_USER);
    String pw   = workspace.getConfig().get(CONFIG_PASSWORD, DEFAULT_PASSWORD);
    String db   = workspace.getConfig().get(CONFIG_DATABASE, DEFAULT_DATABASE);
    
    if(host.equals(DEFAULT_HOST))
    {
      // Interne Datenbank ohne User/Passwort
      this.connection = DriverManager.getConnection(host + Workspace.getInstance().getPath() + "/"+ db + "/");
      System.out.println("Verwende interne Datenbank");
    }
    else
    {
      // Externe Datenbank
      this.connection = DriverManager.getConnection(host + db, user, pw);
      System.out.println("Verwende externe Datenbank: " + host);
    }
    
    //this.connection.setAutoCommit(false);
    //this.stmt = this.connection.createStatement();
  }

  /**
   * Beendet die Datenbank-Verbindung. Falls die interne Datenbank
   * verwendet wird, so wird sie NICHT beendet (siehe shutdown()).
   * @throws SQLException
   */
  public void disconnect() throws SQLException
  {
    System.out.println("Database::disconnect()");
    if (connection != null)
      this.connection.close();
  }

  /**
   * Loescht alle PrakMan-Tabellen aus der Datenbank.
   */
  public void deleteDatabase() throws SQLException
  {
    Statement stmt = connection.createStatement();
    String sqlCode = Resource.getAsString("prakman/io/sql/DeleteTables.sql",
        false);
    stmt.execute(sqlCode);
  }

  /**
   * Erstellt alle notwendigen PrakMan-Tabellen in der Datenbank.
   * @throws SQLException Falls bereits Tabellen gleichen Namens existieren.
   */
  public void createTables() throws SQLException
  {
    Statement stmt = connection.createStatement();
    System.out.println("Lege neue Tabellen an");
    String sqlCode = Resource.getAsString("prakman/io/sql/CreateTables.sql", true);
    
    sqlCode = sqlCode.replaceAll("PREFIX", Config.getInstance().get(
        CONFIG_TABLE_PREFIX, DEFAULT_PREFIX));   
    String[] strSQL = sqlCode.split(";");
    for(String str : strSQL)
    {
       stmt.addBatch(str);
       System.out.println(str);
    }
    stmt.executeBatch();
    this.sync();
    //System.out.println(sqlCode);
    //stmt.execute(sqlCode);
  }

  /** Prueft ob die Tabellen schon angelegt sind */
  public void checkTables() throws SQLException
  {
    try
    {
      Statement stmt = connection.createStatement();
      stmt.executeQuery("SELECT * FROM "
          + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
          + "_student");
      System.out.println("Tabellen schon angelegt");
    }
    catch (Exception e)
    {
      createTables();
    }
  }

  /** Erstellt einen neuen Studenten */
  public void addStudent(Student stud) throws SQLException
  {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("INSERT INTO "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_student (LastName, FirstName, MatrikelNo, Email) VALUES( " + "'"
        + stud.getLastName() + "'" + "," + "'" + stud.getFirstName() + "'"
        + "," + stud.getMatNr() + ", '" + stud.getEmail() + "')");
    this.sync();
  }

  /** Updatet einen Studenten */
  public void updateStudent(Student stud) throws SQLException
  {    
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("UPDATE "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_student set " + "LastName   = '" + stud.getLastName() + "', "
        + "FirstName  = '" + stud.getFirstName() + "', Email = '" + stud.getEmail() +
        "' where MatrikelNo = " + stud.getMatNr());
    this.sync();
  }

  /** Loescht einen Studenten */
  public void deleteStudent(Student stud) throws SQLException
  {
    Statement stmt = connection.createStatement();
    // student
    // groupToStudent
    // results
    // projectToStudent
    stmt.addBatch("DELETE FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_student WHERE MatrikelNo=" + stud.getMatNr());
    stmt.addBatch("DELETE FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_groupToStudent WHERE MatrikelNo=" + stud.getMatNr());
    stmt.addBatch("DELETE FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_projectToStudent WHERE MatrikelNo=" + stud.getMatNr());
    stmt.addBatch("DELETE FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_results WHERE MatrikelNo=" + stud.getMatNr());
    // ausfuehren
    stmt.executeBatch();
    this.sync();
  }

  /** Erstellt einen neuen Tutor */
  public void addTutor(Tutor tut) throws SQLException
  {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("Insert into "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_tutor (LastName, FirstName, TutorID) Values( " + "'"
        + tut.getLastName() + "'" + "," + "'" + tut.getFirstName() + "'" + ","
        + tut.getID() + ")");
    this.sync();
  }

  /** Updatet einen vorhandenen Tutor */
  public void updateTutor(Tutor tut) throws SQLException
  {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("UPDATE "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_tutor set " + "LastName   = '" + tut.getLastName() + "', "
        + "FirstName  = '" + tut.getFirstName() + "'" + " WHERE TutorID = "
        + tut.getID());
    this.sync();
  }

  /**
   * Loescht einen Tutor
   * @param Tutor tut - der Tutor, der geloescht werden soll
   * @param boolean force - alles mitloeschen, was mit dem Tutor zusammenhaengt? -> true
   * sonst false.
   **/
  public void deleteTutor(Tutor tut, boolean force) throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    if (!force) // nicht alles mitloeschen
    {
      // Tutor auf N.N setzen
      stmt.addBatch("UPDATE " + pref + "_event SET TutorID=0 "
          + "WHERE TutorID=" + tut.getID());
      stmt.addBatch("UPDATE " + pref + "_groupToTutor SET TutorID=0 "
          + "WHERE TutorID=" + tut.getID());
      // alten Tutor loeschen - siehe unter der Klammer
    }
    else
    // alles, was mit dem Tutor zusammenhaengt, loeschen
    {
      String groupIDs = "";
      String eventIDs = "";
      String projectIDs = "";
      // merken, welche Gruppe(n) der Tutor betreut
      ResultSet rs = stmt.executeQuery("SELECT GroupID FROM " + pref
          + "_groupToTutor WHERE TutorID=" + tut.getID());
      ArrayList<Integer> groups = new ArrayList<Integer>();
      while (rs.next())
      {
        groups.add(rs.getInt("GroupID"));
      }
      rs.close();
      // String mit betreuten Gruppen (IDs) zusammenbauen
      for (int i = 0; i < groups.size(); i++)
        groupIDs = groupIDs + groups.get(i)
            + ((i + 1) < groups.size() ? " OR GroupID=" : "");
      // merken, welche Events ein Tutor DIREKT betreut
      rs = stmt.executeQuery("SELECT EventID FROM " + pref
          + "_event WHERE TutorID=" + tut.getID());
      ArrayList<Integer> events = new ArrayList<Integer>();
      while (rs.next())
      {
        events.add(rs.getInt("EventID"));
      }
      rs.close();
      // merken, welche Projekte in diesen betreuten Events sind
      ArrayList<Integer> projects = new ArrayList<Integer>();
      // macht nur Sinn, wenn Events (implizit/explizit) betroffen sind
      if (events.size() > 0)
      {
        // String mit betreuten Events (IDs) zusammenbauen
        for (int i = 0; i < events.size(); i++)
          eventIDs = eventIDs + events.get(i)
              + ((i + 1) < events.size() ? " OR EventID=" : "");
        rs = stmt.executeQuery("SELECT ProjectID FROM " + pref
            + "_project WHERE EventID=" + eventIDs);
        while (rs.next())
        {
          projects.add(rs.getInt("ProjectID"));
        }
        rs.close();
        // String mit betroffenen Projekten zusammenbauen
        for (int i = 0; i < projects.size(); i++)
          projectIDs = projectIDs + projects.get(i)
              + ((i + 1) < projects.size() ? " OR ProjectID=" : "");
      }
      // Jetzt koennen wir mit dem Loeschen beginnen
      // Geloescht werden muss in:
      // tutor
      // groupToTutor
      // event
      // groupToStudent
      // eventToStudent
      // project
      // projectToStudent
      // results
      // group
      stmt.addBatch("DELETE FROM " + pref + "_groupToTutor WHERE TutorID="
          + tut.getID());
      if (events.size() > 0)
      {
        // event
        stmt.addBatch("DELETE FROM " + pref + "_event WHERE EventID="
            + eventIDs);
        // eventToStudent (mit EventID)
        stmt.addBatch("DELETE FROM " + pref
            + "_eventToStudent WHERE EventID=" + eventIDs);
      }
      if (groups.size() > 0)
      {
        // groupToStudent
        stmt.addBatch("DELETE FROM " + pref
            + "_groupToStudent WHERE GroupID=" + groupIDs);
        // group
        stmt.addBatch("DELETE FROM " + pref
            + "_group WHERE GroupID=" + groupIDs);
      }
      if (projects.size() > 0)
      {
        // projects
        stmt.addBatch("DELETE FROM " + pref + "_project WHERE ProjectID="
            + projectIDs);
        // projectToStudent
        stmt.addBatch("DELETE FROM " + pref
            + "_projectToStudent WHERE ProjectID=" + projectIDs);
        // results
        stmt.addBatch("DELETE FROM " + pref + "_results WHERE ProjectID="
            + projectIDs);
      }
    }
    // tutor loeschen
    stmt.addBatch("DELETE FROM " + pref + "_tutor WHERE TutorID="
        + tut.getID());
    // ausfuehren
    stmt.executeBatch();
    this.sync();
  }

  /**
   * Neues Event erstellen.
   * @param Event e - Das Event, dessen Daten in die Datenbank geschrieben werden sollen.
   */
  public void addEvent(Event e) throws SQLException
  {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("INSERT INTO "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_event(EventID, TutorID, EventName, Description) VALUES( " + e.getID() + ","
        + e.getTutor().getID() + "," + "'" + e.getName() + "'," + "'"
        + e.getDesc() + "')");
    this.sync();
  }

  /**
   * Vorhandenes Event updaten.
   * @param Event e - Event, welches upgedatet werden soll, uebergeben. Diese Daten werden
   * dann in die Datenbank geschrieben.
   */
  public void updateEvent(Event e) throws SQLException
  {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("UPDATE "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_event SET " + "TutorID=" + e.getTutor().getID()
        + ", EventName='" + e.getName() + "', Description='" + e.getDesc() + "' "
        + "WHERE EventID=" + e.getID());
    this.sync();
  }

  
  /**
   * Loescht ein Event.
   * @param Event event - Event, das geloescht werden soll, muss uebergeben werden
   **/
  public void deleteEvent(prakman.model.Event event) throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    String groupIDs = "";
    String projectIDs = "";
    // merken, welche Gruppe(n) es in diesem Event gibt
    ResultSet rs = stmt.executeQuery("SELECT GroupID FROM " + pref
        + "_group WHERE EventID=" + event.getID());
    ArrayList<Integer> groups = new ArrayList<Integer>();
    
    while (rs.next())
    {
      groups.add(rs.getInt("GroupID"));
    }
    rs.close();
    
    // TermIds sammeln
    ArrayList<Integer> termIDs = new ArrayList<Integer>();
    ResultSet rsTerm = stmt.executeQuery("SELECT TermID FROM " + pref
        + "_term WHERE EventID=" + event.getID());
    while (rsTerm.next())
    {
      termIDs.add(rsTerm.getInt("TermID"));
    }
    rs.close();
    
    // String mit betroffenen Gruppen (IDs) zusammenbauen
    for (int i = 0; i < groups.size(); i++)
      groupIDs = groupIDs + groups.get(i)
          + ((i + 1) < groups.size() ? " OR GroupID=" : "");
    // merken, welche Projekte im betroffenen Event sind
    
    
    ArrayList<Integer> projects = getProjectsInEvent(event.getID());
    // String mit betroffenen Projekten zusammenbauen
    for (int i = 0; i < projects.size(); i++)
      projectIDs = projectIDs + projects.get(i)
          + ((i + 1) < projects.size() ? " OR ProjectID=" : "");
    
    System.out.println(projectIDs);
    
    // Jetzt koennen wir mit dem Loeschen beginnen
    // Geloescht werden muss in:
    // groupToTutor
    // event
    // groupToStudent
    // eventToStudent
    // project
    // projectToStudent
    // results
    // group
    

  	// group
  	stmt.addBatch("DELETE FROM " + pref 
  			+ "_group WHERE EventID = " + event.getID() );
    // eventToStudent
    stmt.addBatch("DELETE FROM " + pref + "_eventToStudent WHERE EventID="
        + event.getID());
    if (groups.size() > 0)
    {
    	// groupToTutor
      stmt.addBatch("DELETE FROM " + pref + "_groupToTutor WHERE GroupID="
          + groupIDs);
      // groupToStudent
      stmt.addBatch("DELETE FROM " + pref
          + "_groupToStudent WHERE GroupID=" + groupIDs);
    }
    if (projects.size() > 0)
    {
      // projects
      stmt.addBatch("DELETE FROM " + pref + "_project WHERE ProjectID="
          + projectIDs);
      // projectToStudent
      stmt.addBatch("DELETE FROM " + pref
          + "_projectToStudent WHERE ProjectID=" + projectIDs);
      // results
      stmt.addBatch("DELETE FROM " + pref + "_results WHERE ProjectID="
          + projectIDs);
    }
    
    // Terms loeschen     
    stmt.addBatch("DELETE FROM " + pref + "_term WHERE EventID="
        + event.getID());
    for(int termID : termIDs)
      stmt.addBatch("DELETE FROM "
          + pref + "_termToStudent WHERE termID=" + termID);
    
    // event loeschen
    stmt.addBatch("DELETE FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_event WHERE EventID=" + event.getID());

    // ausfuehren
    stmt.executeBatch();
    this.sync();
  }
  
  /** 
   * Fuegt einem Event eine neue Gruppe hinzu. Erwartet eine EventID und die Gruppe,
   * die hinzugefuegt werden soll, als Parameter.
   * @param int EventID - ID des Events zu dem eine Gruppe hinzugefuegt werden soll
   * @param String desc - Beschreibung der neuen Gruppe
   */
  public void addGroupToEvent(int eventID, String desc) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	// Hoechste Gruppen-Nr. herausfinden
  	ResultSet rs = stmt.executeQuery("SELECT max(GroupID) AS MaxGroupID FROM " + pref + "_group" );
  	int maxGroupID = 0;
  	while(rs.next()) // TODO: Das ist nicht optimal...
  	{
  		maxGroupID = rs.getInt("MaxGroupID");
  	}
  	// Neue Gruppe in die Datenbank einfuegen
  	stmt.executeUpdate("INSERT INTO " + pref + "_group (GroupID,EventID,Description) " +
  			"VALUES (" + (maxGroupID+1) + "," + eventID + ",'" + desc + "')");
  	
  	this.sync();
  }
  
  /** 
   * Fuegt einem Event eine neue Gruppe hinzu. Erwartet die urspruengliche groupID,
   * eine EventID und eine Beschreibung fuer die Gruppe,
   * die hinzugefuegt werden soll, als Parameter.
   * @param int groupID - ID der Gruppe
   * @param int EventID - ID des Events zu dem eine Gruppe hinzugefuegt werden soll
   * @param String desc - Beschreibung der neuen Gruppe
   */
  public void addGroupToEvent(int groupID, int eventID, String desc) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	// Neue Gruppe in die Datenbank einfuegen
  	stmt.executeUpdate("INSERT INTO " + pref + "_group (GroupID,EventID,Description) " +
  			"VALUES (" + groupID + "," + eventID + ",'" + desc + "')");
  	
  	this.sync();
  }
  
  /**
   * Updatet die Beschreibung einer Gruppe. Erwartet die Gruppennummer und die
   * neue Beschreibung der Gruppe.
   * @param int GroupID, String desc
   */
  public void updateGroup(int groupID, String desc) throws SQLException
  {
    Statement stmt = connection.createStatement();
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
  	
  	stmt.executeUpdate("UPDATE " + pref + "_group SET Description = '" + desc + "' WHERE GroupID=" + groupID);
  	
  	this.sync();
  }
  
  /** 
   * Diese Methode entfernt eine Gruppe - und setzt dementsprechend all ihre Mitglieder
   * auf gruppenlos.
   * @param int groupID - ID der zu loeschenden Gruppe
   */
  public void removeGroup(int groupID) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	stmt.addBatch("DELETE FROM " + pref + "_groupToStudent WHERE GroupID=" + groupID);
  	stmt.addBatch("DELETE FROM " + pref + "_group WHERE GroupID=" + groupID);
  	stmt.executeBatch();
  	
  	this.sync();
  }
  
  /** Gibt die Liste aller Termine zurueck */
  public ArrayList<Term> getTerms() throws SQLException
  {	  
    Statement stmt = connection.createStatement();
	  ArrayList<Term> terms = new ArrayList<Term>();
	  ResultSet rs = stmt.executeQuery("SELECT * FROM "+
			Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
			+"_term");
    if(rs == null)
      throw new SQLException("Connection is closed!");  
	
	  while(rs.next())
	  {	  		 
	    terms.add(new Term(rs.getInt("TermID"), rs.getInt("EventID"),rs.getTimestamp("DateEdit")));		  
	  }	  
	  return terms;
  }
  
  /** 
   * Gibt die naechste freie Project-ID zurueck.
   */
  public int getNewProjectID() throws SQLException
  {	  
    Statement stmt = connection.createStatement();

	  ResultSet rs = stmt.executeQuery("SELECT MAX(ProjectID) AS C FROM "+
			Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
			+"_project");
    
	  if(rs == null)
	    throw new SQLException("Connection is closed!");  
	
	  if(rs.next())
      return rs.getInt("C") + 1;
    else
      return 1;
  }
  
  /** 
   * Gibt die naechste freie Term-ID zurueck.
   */
  public int getNewTermID() throws SQLException
  {   
    Statement stmt = connection.createStatement();

    ResultSet rs = stmt.executeQuery("SELECT MAX(TermID) as C FROM "+
      Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
      +"_term");
    
    if(rs == null)
      throw new SQLException("Connection is closed!");  
  
    if(rs.next())
      return rs.getInt("C") + 1;
    else
      return 1;
  }
  
  /** 
   * Gibt die naechste freie Project-ID zurueck.
   */
  public int getNewEventID() throws SQLException
  {   
    Statement stmt = connection.createStatement();

    ResultSet rs = stmt.executeQuery("SELECT MAX(EventID) as C FROM "+
      Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
      +"_event");
    
    if(rs == null)
      throw new SQLException("Connection is closed!");  
  
    if(rs.next())
      return rs.getInt("C") + 1;
    else
      return 1;
  }
  
  /** Holt einen neuen Studenten, null bei nicht existierendem Studenten */
  public Student getStudent(int matrNo)
  {
    try
    {
      Statement stmt = connection.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM "+
				  Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
				  +"_student where MatrikelNo = " + matrNo);
		  rs.next();
		  return new Student(
		  		rs.getString("LastName"), rs.getString("FirstName"),
		  		rs.getInt("MatrikelNo"), rs.getString("Email"), rs.getTimestamp("DateEdit")
		  		);
	  }
	  catch(SQLException sqlEx)
	  {
		  return null;
	  }
  }
  
  /** Holt die Note eines Studenten */
  public String getMark(int matNr, int projectID)
  {
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(
        "Select Result from "+
        Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        +"_results where MatrikelNo = " + matNr +" and ProjectID = " + projectID);
      rs.next();
      return rs.getString("Result");
    }
    catch (Exception e) 
    {
      return ""; // TODO: null zurueckgeben!
    }
  }
  
  /** Setzt die Note eines Studenten */
  public void setMark(int matNr, int projectID, String mark) throws SQLException
  {
    Statement stmt = connection.createStatement();
    try
    {      
      stmt.executeQuery(
        "Insert into "+
        Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        +"_results (MatrikelNo,ProjectID,RESULT) VALUES ("+ matNr +","+projectID+",'"+ mark +"')");      
    }
    catch (SQLException e) 
    {      
      stmt.executeUpdate(
        "UPDATE " +  
        Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX) +
        "_results SET result = '" + mark +"' where MatrikelNo = " + matNr + " and projectID = "+ projectID
      );
      
    }
  }

  /** Gibt die Liste der Studenten zurueck */
  public ArrayList<Student> getStudents() throws SQLException
  {
    Statement stmt = connection.createStatement();
	  ArrayList<Student> students = new ArrayList<Student>();
	  ResultSet rs = stmt.executeQuery("SELECT * FROM "+
			Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
			+"_student ORDER BY LASTNAME");
    if(rs == null)
      throw new SQLException("Connection is closed!");  
	
	  while(rs.next())
    {	  		 
	    students.add(
	    		new Student(rs.getString("LastName"), rs.getString("FirstName"),
	    				rs.getInt("MatrikelNo"), rs.getString("Email"), rs.getTimestamp("DateEdit"))
	    		);		  
	  }	  
	  return students;
  }
  
  /** Gibt die Liste der Studenten zurueck */
  public ArrayList< ArrayList<Object> > getStudentsForTableModel(int eventID) 
    throws SQLException
  {
    Statement stmt = connection.createStatement();
    ArrayList<ArrayList<Object>> students = new ArrayList<ArrayList<Object>>();
    ResultSet rs = stmt.executeQuery("SELECT * FROM "+
      Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_student WHERE MatrikelNo NOT IN (SELECT MatrikelNo FROM " 
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)+"_eventToStudent "
        + "WHERE EventID = "+ eventID +") ORDER BY LASTNAME");
    if(rs == null)
      throw new SQLException("Connection is closed!");  
  
    while(rs.next())
    {  
      ArrayList<Object> innerEvents = new ArrayList<Object>();
      innerEvents.add(new Boolean(false));
      innerEvents.add(rs.getInt("MatrikelNo"));
      innerEvents.add(rs.getString("LastName"));
      innerEvents.add(rs.getString("FirstName"));      
      innerEvents.add(rs.getString("Email"));
      students.add(innerEvents);            
    }   
    return students;
  }

  /** Gibt die Liste der Studenten zurueck */
  public ArrayList<Student> getFilteredStudents(String strFilter)
      throws SQLException
  {
    Statement stmt = connection.createStatement();
    if (strFilter.equals(""))
      return getStudents();
    ArrayList<Student> students = new ArrayList<Student>();
    ResultSet rs = stmt.executeQuery("Select * from "+
      Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
      + "_student" +
        " where lower(LastName) like '"+ strFilter.toLowerCase() + "%'"  +
        " or lower(FirstName) like '"+ strFilter.toLowerCase() + "%' ORDER BY LASTNAME" 
    );    

    if(rs == null)
      throw new SQLException("Connection is closed!");  
  
    while(rs.next())
    {        
      students.add(
      		new Student(rs.getString("LastName"), rs.getString("FirstName"),
      				rs.getInt("MatrikelNo"), rs.getString("Email"), rs.getTimestamp("DateEdit"))
      		);     
    }   
    return students;
  }

  /** Gibt einen Tutor zurueck */
  public Tutor getTutor(int tutorID)
  {
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM "
          + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
          + "_tutor where TutorID = " + tutorID + " ORDER BY LastName");
      rs.next();
      return new Tutor(rs.getInt("TutorID"), rs.getString("LastName"), rs
          .getString("FirstName"), rs.getTimestamp("DateEdit"));
    }
    catch (SQLException sqlEx)
    {
      return null;
    }
  }

  /** 
   * Gibt ein Event zurueck 
   * @return null, falls das Event nicht gefunden wurde.
   **/
  public Event getEvent(int eventID)
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("Select e.*, t.* from " + pref
          + "_event e INNER JOIN " + pref
          + "_tutor t ON e.TutorID=t.TutorID WHERE EventID = " + eventID);
      rs.next();
      return new Event(rs.getString("EventName"), rs.getString("Description"), rs
          .getTimestamp("DateEdit"), new Tutor(rs.getInt("TutorID"), rs
          .getString("LastName"), rs.getString("FirstName"), rs
          .getTimestamp("DateEdit")), eventID);
    }
    catch (SQLException sqlEx)
    {
      return null;
    }
  }
  
  /**
   * Erwartet eine Gruppen-ID, gibt ein komplettes Gruppenobjekt aus der Datenbank zurueck.
   * @param int groupID - ID der gesuchten Gruppe
   * @return Group g - gesuchte Gruppe; null falls nicht gefunden
   */
  public Group getGroup(int groupID) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	ResultSet rs = stmt.executeQuery("SELECT Description FROM " + pref + "_group WHERE GroupID=" + groupID);
  	
  	if(rs.next())
  		return(new Group(groupID,rs.getString("Description")));
  	else
  		return(null);
  }

  /** Gibt die Liste der Studenten zurueck */
  public ArrayList<Tutor> getTutors() throws SQLException
  {
    Statement stmt = connection.createStatement();
    ArrayList<Tutor> tutors = new ArrayList<Tutor>();
    ResultSet rs = stmt.executeQuery("SELECT * FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_tutor ORDER BY LastName");
    while (rs.next())
    {
      tutors.add(new Tutor(rs.getInt("TutorID"), rs.getString("LastName"), rs
          .getString("FirstName"), rs.getTimestamp("DateEdit")));
    }
    return tutors;
  }

  /**
   * @param strFilter
   * @return Eine gefilterte Liste von Tutoren.
   * @throws SQLException
   */
  public ArrayList<Tutor> getFilteredTutors(String strFilter)
      throws SQLException
  {
    Statement stmt = connection.createStatement();
    if (strFilter.equals(""))
      return getTutors();
    ArrayList<Tutor> tutors = new ArrayList<Tutor>();
    ResultSet rs = stmt.executeQuery("Select * from "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_tutor" + " where lower(LastName) like '" + strFilter.toLowerCase() + "%'"
        + " or lower(FirstName) like '" + strFilter.toLowerCase() + "%'");
    while (rs.next())
    {
      tutors.add(new Tutor(rs.getInt("TutorID"), rs.getString("LastName"), rs
          .getString("FirstName"), rs.getTimestamp("DateEdit")));
    }
    return tutors;
  }

  /** Gibt die Liste aller Veranstaltungen zurueck */
  public ArrayList<Event> getEvents() throws SQLException
  {
    Statement stmt = connection.createStatement();
    ArrayList<Event> events = new ArrayList<Event>();
    String prefix = Config.getInstance().get(CONFIG_TABLE_PREFIX,
        DEFAULT_PREFIX);
    String tutorTable = prefix + "_tutor";
    String eventTable = prefix + "_event";
    ResultSet rs = stmt.executeQuery("Select " + eventTable + ".*,"
        + tutorTable + ".* " + "from " + eventTable + " left join "
        + tutorTable + " on " + eventTable + ".TutorID = " + tutorTable
        + ".TutorID ORDER BY EventName");
    while (rs.next())
    {
      events.add(new Event(rs.getString("EventName"), rs.getString("Description"), rs
          .getTimestamp("DateEdit"), new Tutor(rs.getInt("TutorID"), rs
          .getString("LastName"), rs.getString("FirstName"), rs
          .getTimestamp("DateEdit")), rs.getInt("EventID")));
    }
    return events;
  }

  /** Gibt die Liste aller Veranstaltungen zurueck */
  public ArrayList<Event> getFilteredEvents(String strFilter)
      throws SQLException
  {
    Statement stmt = connection.createStatement();
    if (strFilter.equals(""))
      return getEvents();
    ArrayList<Event> events = new ArrayList<Event>();
    String prefix = Config.getInstance().get(CONFIG_TABLE_PREFIX,
        DEFAULT_PREFIX);
    String tutorTable = prefix + "_tutor";
    String eventTable = prefix + "_event";
    ResultSet rs = stmt.executeQuery("Select " + eventTable + ".*,"
        + tutorTable + ".* " + "from " + eventTable + " left join "
        + tutorTable + " on " + eventTable + ".TutorID = " + tutorTable
        + ".TutorID" + " where lower(LastName) like '" + strFilter.toLowerCase() + "%'"
        + " or lower(FirstName) like '" + strFilter.toLowerCase() + "%'" + " or lower(EventName) like '"
        + strFilter.toLowerCase() + "%'");
    while (rs.next())
    {
      events.add(new Event(rs.getString("EventName"), rs.getString("Description"), rs
          .getTimestamp("DateEdit"), new Tutor(rs.getInt("TutorID"), rs
          .getString("LastName"), rs.getString("FirstName"), rs
          .getTimestamp("DateEdit")), rs.getInt("EventID")));
    }
    return events;
  }

  /**
   * Gibt alle Studenten zurueck, die dem angegebenen Event zugeordnet sind.
   * @param
   * @return Studenten einer bestimmten Veranstaltung zurueckgeben
   */
  public ArrayList<Student> getStudentsInEvent(int eventID) throws SQLException
  {
	  String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
	  ArrayList<Student> students = new ArrayList<Student>();
    Statement stmt = connection.createStatement();
	  ResultSet rs = stmt.executeQuery("SELECT S.* FROM " + pref + "_eventToStudent eTS "
	  		+ "INNER JOIN " + pref + "_student S ON eTS.MatrikelNo = S.MatrikelNo WHERE "
	  		+ "eTS.EventID = " + eventID );

	  while(rs.next())
	  {
		  students.add(new Student(rs.getString("LastName"),rs.getString("FirstName"),
				  			rs.getInt("MatrikelNo"),rs.getString("Email"),rs.getTimestamp("DateEdit")));
	  }
	  return(students);
  }
  
  /**
   * Gibt alle Studenten einer Veranstaltung zurueck, die noch keinem
   * Projekt zugeordnet sind.
   * @param eventID
   * @param projectID
   * @return
   * @throws SQLException
   */
  public ArrayList<Student> getUnassignedStudentsInEvent(int eventID, int projectID) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    ArrayList<Student> students = new ArrayList<Student>();
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT S.* FROM " + pref + "_eventToStudent eTS "
        + "INNER JOIN " + pref + "_student S ON eTS.MatrikelNo = S.MatrikelNo WHERE "
        + "eTS.EventID = " + eventID +" and S.MatrikelNo not in (SELECT MatrikelNo from " + pref + "_projectToStudent WHERE ProjectID = "+ projectID +" ) ");

    while(rs.next())
    {
      students.add(new Student(rs.getString("LastName"),rs.getString("FirstName"),
                rs.getInt("MatrikelNo"),rs.getString("Email"),rs.getTimestamp("DateEdit")));
    }
    return students;
  }

  /**
   * Gibt die Studenten eines Projektes zurueck.
   * @param
   * @return Studenten einer bestimmten Veranstaltung zurueckgeben
   */
  public Integer[] getStudentsInProject(int projectID) throws SQLException
  {
	  String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
	  ArrayList<Integer> students = new ArrayList<Integer>();
    Statement stmt = connection.createStatement();
	  ResultSet rs = stmt.executeQuery("SELECT MatrikelNo FROM " + pref + "_projectToStudent pTS "
	  		+ "WHERE ProjectID = " + projectID );

	  while(rs.next())
	  {
		  students.add(rs.getInt("MatrikelNo"));
	  }
	  return(students.toArray(new Integer[0]));
  }

  /**
   * Gibt die Studenten einer Gruppe zurueck.
   * @param  int groupID
   * @return Studenten einer bestimmten Gruppe zurueckgeben
   */
  public ArrayList<Student> getStudentsInGroup(int groupID) throws SQLException
  {
	  String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
	  ArrayList<Student> students = new ArrayList<Student>();
    Statement stmt = connection.createStatement();
	  ResultSet rs = stmt.executeQuery("SELECT S.* FROM " + pref + "_groupToStudent gTS "
	  		+ "INNER JOIN " + pref + "_student S ON gTS.MatrikelNo = S.MatrikelNo WHERE "
	  		+ "gTS.GroupID = " + groupID );

	  while(rs.next())
	  {
		  students.add(new Student(rs.getString("LastName"),rs.getString("FirstName"),
				  			rs.getInt("MatrikelNo"),rs.getString("Email"),rs.getTimestamp("DateEdit")));
	  }
	  return(students);
  }
  
  /**
   * Gibt die Termine einer Veranstaltung zurueck.
   * @param EventID
   * @return ArrayList<Term> - Liste aller Termine (Term(s)) in einer Veranstaltung
   */
  public ArrayList<Term> getTermsInEvent(int EventID) throws SQLException
  {
    Statement stmt = connection.createStatement();
    ArrayList<Term> terms = new ArrayList<Term>();
    ResultSet rs = stmt.executeQuery("SELECT TermID, EventID, DateEdit FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_term WHERE EventID=" + EventID + " order by DateEdit");
    while (rs.next())
    {
      terms.add(new Term(rs.getInt("TermID"), rs.getInt("EventID"), rs
          .getTimestamp("DateEdit")));
    }
    return terms;
  }

  /**
   * Gibt die Anzahl der zu erwarteten Teilnehmer zurueck.
   * @param int EventID
   * @return int (Anzahl der erwarteten Teilnehmer)
   */
  public int countExpectedAttendantsInEvent(int EventID) throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    ResultSet rs = stmt
        .executeQuery("SELECT COUNT(MatrikelNo) AS Attendants FROM " + pref
            + "_eventToStudent WHERE EventID=" + EventID);
    if (rs.next())
      return (rs.getInt("Attendants"));
    else
      return (0);
  }

  /**
   * Gibt die tatsaechliche Anzahl der Teilnehmer zurueck.
   * @param int EventID
   * @param int TermID
   * @return int (Anzahl der tatsaechlichen Teilnehmer)
   */
  public int countActualAttendantsInEventPerTerm(int EventID, int TermID)
      throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    ResultSet rs = stmt
        .executeQuery("SELECT COUNT(MatrikelNo) AS Attendants FROM " + pref
            + "_termToStudent tTS INNER JOIN " + pref + "_term T "
            + "ON tTS.TermID=T.TermID WHERE T.EventID=" + EventID
            + " AND tTS.TermID=" + TermID);
    if (rs.next())
      return (rs.getInt("Attendants"));
    else
      return (0);
  }

  /**
   * Gibt die tatsaechliche Anzahl von Teilnehmern zurueck, die
   * an einem spezifischen Termin teilgenommen haben.
   * @param int eventID
   * @param int termID
   * @return ArrayList<Student>
   */
  public ArrayList<Student> getActualAttendantsInEventPerTerm(int eventID,
      int termID) throws SQLException
  {
	  String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
	  ArrayList<Student> attendants = new ArrayList<Student>();
    Statement stmt = connection.createStatement();
	  ResultSet rs = stmt.executeQuery("SELECT * FROM " + pref + "_student S " +
			  "INNER JOIN " + pref + "_termToStudent tTS ON S.MatrikelNo=tTS.MatrikelNo " +
			  "WHERE tTS.TermID=" + termID );
	  
	  while(rs.next())
		  attendants.add(new Student(rs.getString("LastName"), rs.getString("FirstName"),
				  rs.getInt("MatrikelNo"), rs.getString("Email"), rs.getTimestamp("DateEdit")));
	  
	  return(attendants);
  }

  /**
   * Setzt den Anwesenheitsstatus.
   * @param   int termID, ArrayList<ArrayList<Object>>
   * Letzteres enthaelt eine Liste von ArrayLists, deren 1. Parameter, die Matr.Nr 
   * und der 2. Parameter true / false enthaelt - je nachdem, ob derjenige teilgenommen
   * hat oder nicht.
   */
  public void setAttendanceStatus(int termID, ArrayList<ArrayList<Object>> attended) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    
    PreparedStatement psA = this.connection.prepareStatement("INSERT INTO " + pref + "_termToStudent " + 
        "(TermID,MatrikelNo) VALUES (" + termID + ", ? )");
    
    PreparedStatement psD = this.connection.prepareStatement("DELETE FROM " + pref + "_termToStudent " + 
        "WHERE MatrikelNo = ? AND TermID = " + termID);
    
    for(ArrayList<Object> subData : attended)
    {     
      try
      {
        if (subData.get(1).equals(true))
        {
          psA.setInt(1, (Integer)subData.get(0));
          psA.execute();
          psA.clearParameters();
          
        }
        else
        {
          psD.setInt(1, (Integer)subData.get(0));
          psD.execute();
          psD.clearParameters();
        }  
      }
      catch(SQLException sqlEx)
      {
        // d.h. der Student war entweder
        // - schon in der Liste vorhanden und sollte hinzugefuegt werden -> ok
        // - noch nicht in der Liste vorhanden und sollte geloescht werden -> ok        
        psA.clearParameters();
        psD.clearParameters();
      }

    }
    this.sync();
  }
  
  /**
   * Gibt zurueck, ob Student stud am Termin teilgenommen hat.
   * @param int matNr - Matrikelnummer des Studenten
   * @param int termID - ID des besagten Termins
   * @return boolean true, falls anwesend gewesen - sonst false
   */
  public boolean getAttendantsStatus(int matNr, int termID) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	ResultSet rs = stmt.executeQuery("SELECT tTS.MatrikelNo FROM " + pref + "_term T "
  			+ "INNER JOIN " + pref + "_termToStudent tTS ON T.TermID=tTS.TermID "
  			+ "WHERE tTS.MatrikelNo=" + matNr + " AND tTS.TermID=" + termID);
  	
  	if (rs.next())
  		return(true);
  	else
  		return(false);
  }

  /**
   * Fuegt den Studenten dem Projekt hinzu.
   * @param Student stud
   * @param int projectID
   * @return void
   */
  public void addStudentToProject(Student stud, int projectID) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("INSERT INTO " + pref + "_projectToStudent"
        + "(ProjectID,MatrikelNo) VALUES(" + projectID + "," + stud.getMatNr()
        + ")");
    
    this.sync();
  }
  
  /**
   * Praktika zurueckgeben, an denen ein Student teilnimmt.
   * @param		Student stud
   * @return 	ArrayList<Event> - Liste aller Events, an denen per teilnimmt
   */
  public ArrayList<Event> getAttendedEvents(Student stud) throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    ArrayList<Event> events = new ArrayList<Event>();
    ResultSet rs = stmt.executeQuery("SELECT E.*, T.* FROM " + pref + "_eventToStudent eTS "
    		+ "INNER JOIN " + pref + "_event E ON eTS.EventID = E.EventID "
    		+ "INNER JOIN " + pref + "_tutor T ON E.TutorID=T.TutorID "
    		+ "WHERE eTS.MatrikelNo=" + stud.getMatNr());
    while (rs.next())
    {
      events.add(new Event(rs.getString("EventName"), rs.getString("Description"), rs
          .getTimestamp("DateEdit"), new Tutor(rs.getInt("TutorID"), rs
          .getString("LastName"), rs.getString("FirstName"), rs
          .getTimestamp("DateEdit")), rs.getInt("EventID")));
    }
    return (events);
  }
  
  /**
   * Gruppen zurueckgeben, die in einer Veranstaltung vorhanden sind
   * @param	int eventID (betroffene Veranstaltung)
   * @return	ArrayList<Group> Liste der Gruppen
   * @author PR
   */
  public ArrayList<Group> getGroupsInEvent(int eventID) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
  	ArrayList<Group> groups = new ArrayList<Group>();
    Statement stmt = connection.createStatement();
  	ResultSet rs = stmt.executeQuery("SELECT GroupID, Description FROM " + pref + "_group "
  			+ "WHERE EventID = " + eventID);
  	
  	while(rs.next())
  		groups.add(new Group(rs.getInt("GroupID"),rs.getString("Description")));
  	
  	return(groups);
  }

  /**
   * Gruppe zurueckgeben, in der Student stud im Event mit Name "eName" gelistet
   * ist.
   * @param		Student stud, int eventID
   * @return	Group - Gruppe in der Person per im Event eName eingetragen ist
   */
  public Group getEventGroup(Student stud, int eventID) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT G.GroupID, G.Description FROM " + pref
        + "_group G INNER JOIN " + pref + "_groupToStudent gTS ON G.GroupID=gTS.GroupID "
        + "WHERE gTS.MatrikelNo=" + stud.getMatNr() + " AND G.EventID=" + eventID );

    if (rs.next())
    {
      return (new Group(rs.getInt("GroupID"), rs.getString("Description")));
    }
    else
      return (null);
  }

  /**
   * Note fuer ein Projekt / Student zurueckgeben
   * @param String firstColumn Entweder "EventName" oder "ProjectID"
   * @author PR
   * @return ArrayList<ArrayList<String>> 2-Dimensionaler String-Vektor
   */
  public ArrayList<ArrayList<Object>> getResults(Student stud, String firstColumn)
      throws SQLException
  {
	  String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
	  ArrayList<ArrayList<Object>> results = new ArrayList<ArrayList<Object>>();

    Statement stmt = connection.createStatement();
	  ResultSet rs = stmt.executeQuery("SELECT P.ProjectID, P.Description,Result,R.DateEdit,EventName "
	  		  +"FROM "		  + pref + "_results R "
	  		  +"INNER JOIN " + pref + "_project P ON R.ProjectID=P.ProjectID "
	  		  +"INNER JOIN " + pref + "_event E ON P.EventID=E.EventID "
	  		  +"WHERE R.MatrikelNo="+stud.getMatNr()
	  );
    
	  // SimpleDateFormat fuer gute Datumsdarstellung
	  SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	  while(rs.next())
	  {
		  ArrayList<Object> innerResults = new ArrayList<Object>();
		  innerResults.add(rs.getString(firstColumn));
		  innerResults.add(rs.getString("Description"));
		  innerResults.add(rs.getString("Result"));
			innerResults.add(sdf.format(rs.getDate("DateEdit")));
			Date dl = this.getProjectDeadline(rs.getInt("ProjectID"));
			innerResults.add(
					( dl == null ? "Unbegrenzt" : sdf.format(dl) )
							);
			results.add(innerResults);
	  }
	  
	  return results;
  }

  /**
   * Gibt eine Liste von Project-IDs zurueck, die im angegebenen
   * Event enthalten sind.
   * @return int-Array von ProjectIDs, die zum angegebenen Event gehoeren
   */
  public ArrayList<Integer> getProjectsInEvent(int EventID)
  {
    try
    {
      Statement stmt = connection.createStatement();
      String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX,
          DEFAULT_PREFIX);
      /*ResultSet count = stmt.executeQuery("SELECT count(ProjectID) FROM "
          + pref + "_project " + "WHERE EventID = '" + EventID + "'");*/
      ResultSet rs = stmt.executeQuery("SELECT ProjectID FROM " + pref
          + "_project " + "WHERE EventID = " + EventID );
      //count.next();
      //int[] prIDs = new int[count.getInt(1)];
      //int i = 0;
      ArrayList<Integer> prIDs = new ArrayList<Integer>();
      while (rs.next())
      {
        int prID = rs.getInt("ProjectID");
        prIDs.add(prID);
      }
      return prIDs;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      return new ArrayList<Integer>();
    }
  }

  /**
   * Gibt die EventID des zu diesem Projekt gehoerenden Events zurueck.
   * @return int mit dem Event des  Projects
   */
  public int getEventByProject(int projectID)
  {
    int id = 0;
    try
    {
      Statement stmt = connection.createStatement();
      String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX,DEFAULT_PREFIX);
      ResultSet rs = stmt.executeQuery("SELECT EventID FROM " + pref
          + "_project " + "WHERE ProjectID = '" + projectID + "'");
      while (rs.next()) // TODO: Einzelige Selects brauchen keine while-Schleife!
      {
        id = rs.getInt("EventID");
      }
      return id;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      return -1;
    }
  }
  
  /**
   * Gibt die Beschreibung eines Projekts zurueck.
   * @return String mit der Project Beschreibung
   */
  public String getProjectDesc(int ProjectID)
  {
    String desc = new String();
    try
    {
      Statement stmt = connection.createStatement();
      String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX,
          DEFAULT_PREFIX);
      ResultSet rs = stmt.executeQuery("SELECT Description FROM " + pref
          + "_project " + "WHERE ProjectID = '" + ProjectID + "'");
      while (rs.next())
      {
        desc = rs.getString("Description");
      }
      if (desc.equals(null))
        desc = "";
      return desc;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Gibt die Projekt-IDs der Projekte zurueck, an denen der angegebene
   * Student teilnimmt.
   * @return ArrayList mit den Project Beschreibungen der Projekte an denen der Student teilnimmt.
   * */
  public ArrayList<String> getProjectsByStudent(int matrNr)
  {
    //String projects[] = new String[7];
    ArrayList<String> projectDesc = new ArrayList<String>();
    try
    {
      String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT Description FROM "+pref+"_projectToStudent pTS "
                        + "INNER JOIN "+pref+"_project P "
                        + "ON P.ProjectID = pTS.ProjectID "
                        + "WHERE MatrikelNo = " + matrNr +"");

      while(rs.next())
      {
        projectDesc.add(rs.getString("Description"));
      }
      return projectDesc;     
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      return null;
    }  
  }

  /**
   * Gibt das Datum eines Projekts zurueck.
   * @param int ProjectID
   * @return Timestamp DateEdit. Falls nicht vorhanden: null
   */
  public Timestamp getProjectDate(int projectID) throws SQLException
  {
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT DateEdit FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_project WHERE ProjectID=" + projectID);
    if (rs.next())
      return rs.getTimestamp("DateEdit");
    else
      return null;
  }

  /**
   * Holt den Endtermin eines Projekts aus der Datenbank.
   * @param int ProjectID
   * @return DateEdit Deadline. Falls nicht vorhanden: null
   */
  public Timestamp getProjectDeadline(int ProjectID) throws SQLException
  {
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT Deadline FROM "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_project WHERE ProjectID=" + ProjectID);
    if (rs.next())
      return rs.getTimestamp("Deadline");
    else
      return new Timestamp(Long.MAX_VALUE);
  }

  /**
   * Gibt die vom angegebenen Tutor betreuten Gruppen zurueck.
   * @param Tutor
   * @return ArrayList<ArrayList<String>> - wird direkt an Table
   *         weitergeleitet
   */
  public ArrayList<ArrayList<Object>> getOverseenGroups(Tutor tut)
      throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    ArrayList<ArrayList<Object>> groups = new ArrayList<ArrayList<Object>>();
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt
        .executeQuery("SELECT E.EventName, E.Description, gTT.GroupID FROM " + pref
            + "_groupToTutor gTT INNER JOIN " + pref
            + "_group G ON gTT.GroupID=G.GroupID INNER JOIN " + pref
            + "_event E ON G.EventID=E.EventID " + " WHERE gTT.TutorID="
            + tut.getID());
    while (rs.next())
    {
      ArrayList<Object> innerGroups = new ArrayList<Object>();
      innerGroups.add(rs.getString("EventName"));
      innerGroups.add(rs.getString("Description"));
      innerGroups.add(rs.getString("GroupID"));
      groups.add(innerGroups);
    }

    return (groups);
  }

  /**
   * Gibt die vom Tutor betreuten Projekte zurueck.
   * @param Tutor tut
   * @return ArrayList<ArrayList<String>> - wird direkt an Table weitergeleitet
   */
  public ArrayList<ArrayList<Object>> getOverseenProjects(Tutor tut)
      throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    ArrayList<ArrayList<Object>> events = new ArrayList<ArrayList<Object>>();
    ResultSet rs = stmt
        .executeQuery("SELECT EventID, TutorID, EventName, Description FROM " + pref
            + "_event WHERE " + "TutorID=" + tut.getID());
    while (rs.next())
    {
      ArrayList<Object> innerEvents = new ArrayList<Object>();
      innerEvents.add(rs.getInt("EventID"));
      innerEvents.add(rs.getInt("TutorID"));
      innerEvents.add(new Boolean(false));
      innerEvents.add(rs.getString("EventName"));
      innerEvents.add(rs.getString("Description"));
      events.add(innerEvents);
    }
    return events;
  }

  /**
   * Schreibt einen Studenten in die Datenbank. Falls vorhanden, 
   * wird ein Update ausgefuehrt.
   */
  public void saveStudent(Student stud) throws SQLException
  {
    // this.connect();
    // Student existiert noch nicht -> INSERT
    if (this.getStudent(stud.getMatNr()) == null)
    {
      this.addStudent(stud);
    }
    else
    // Student existiert bereits -> UPDATE
    {
      this.updateStudent(stud);
    }
    // this.disconnect();
  }

  /**
   * Hebt eine Event-<->Tutorzuordnung auf
   */
  public void deleteAssignment(int eventID, int tutorID) throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    stmt.executeUpdate("UPDATE " + pref + "_event SET TutorID = 0 where "
        + "EventID = " + eventID + " and TutorID = " + tutorID);
    this.sync();
  }
  
  /**
   * Setzt die Event zu Tutor Zuordnung.
   * @param eventID
   * @param tutorID
   * @throws SQLException
   */
  public void setEventToTutor(int eventID, int tutorID) throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    stmt.executeUpdate("UPDATE "+pref+"_event SET TutorID = "+ tutorID +" where "
        + "EventID = "+ eventID);
    
    this.sync();
  }
  
  /** Gibt die Liste aller Veranstaltungen zurueck */
  public ArrayList<ArrayList<Object>> getUnassignedEvents() throws SQLException
  {
    Statement stmt = connection.createStatement();
    ArrayList<ArrayList<Object>> events = new ArrayList<ArrayList<Object>>();
    String prefix = Config.getInstance().get(CONFIG_TABLE_PREFIX,
        DEFAULT_PREFIX);
    String tutorTable = prefix + "_tutor";
    String eventTable = prefix + "_event";
    ResultSet rs = stmt.executeQuery("Select " + eventTable + ".*,"
        + tutorTable + ".* " + "from " + eventTable + " left join "
        + tutorTable + " on " + eventTable + ".TutorID = " + tutorTable
        + ".TutorID where TutorId = 0");
    while (rs.next())
    {
      ArrayList<Object> innerEvents = new ArrayList<Object>();
      innerEvents.add(rs.getInt("EventID"));
      innerEvents.add(rs.getInt("TutorID"));
      innerEvents.add(new Boolean(false));
      innerEvents.add(rs.getString("EventName"));
      innerEvents.add(rs.getString("Description"));
      events.add(innerEvents);
    }
    return events;
  }

  /**
   * Schreibt einen Tutor in die Datenbank. Falls vorhanden, wird
   * ein Update ausgefuehrt.
   */
  public void saveTutor(Tutor tut) throws SQLException
  {
    // this.connect();
    // Tutor existiert noch nicht -> INSERT
    if (this.getTutor(tut.getID()) == null)
    {
      this.addTutor(tut);
    }
    else
    // Tutor existiert bereits -> UPDATE
    {
      this.updateTutor(tut);
    }
    // this.disconnect();
  }

  /**
   * Speichert das Event in der Datenbank. Falls es schon existiert,
   * wird es aktualisiert.
   * @param e
   * @throws SQLException
   */
  public void saveEvent(Event e) throws SQLException
  {
    // this.connect();
    // Tutor existiert noch nicht -> INSERT
    if (this.getEvent(e.getID()) == null)
    {
      this.addEvent(e);
    }
    else
    // Tutor existiert bereits -> UPDATE
    {
      this.updateEvent(e);
    }
    // this.disconnect();
  }

  /**
   * Fuegt den Studenten stud dem Event ev hinzu.
   * @param Student stud
   * @param Event ev
   */
  public void addStudentToEvent(Student stud, Event ev) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("INSERT INTO " + pref + "_eventToStudent"
        + "(EventID,MatrikelNo) VALUES(" + ev.getID() + "," + stud.getMatNr()
        + ")");
    
    this.sync();
  }
  
  /**
   * Fuegt einen Studenten zu einer Veranstaltung hinzu.
   * @param eventID
   * @param studID
   * @throws SQLException
   */
  public void addStudentToEvent(int eventID, int studID) throws SQLException
  {
    Statement stmt = connection.createStatement();
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    System.out.println(eventID+"----"+studID);
    stmt.executeUpdate("INSERT INTO " + pref + "_eventToStudent"
        + "(EventID,MatrikelNo) VALUES(" + eventID + "," + studID
        + ")");
    
    this.sync();
  }
  
  /**
   * Fuegt den Termin term dem Event ev hinzu.
   * @param Term term, Event ev
   */
  public void addTermToEvent(Term term, Event ev) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    try
    {
      stmt.executeUpdate("INSERT INTO " + pref + "_term"
        + "(TermID,EventID,DateEdit) VALUES(" + term.getTermID() + "," + ev.getID() + ",'" +term.getDate()+"'"
        + ")");
    }
    catch (SQLException sqlE) 
    {
      stmt.executeUpdate("UPDATE " + pref + "_term "         
          + "SET DateEdit = '" + term.getDate()
          + "' WHERE TermID = "+ term.getTermID()); 
    }
    this.sync();
  }
  
  /**
   * Fuegt das Projekt mit der ID projectID dem Event ev hinzu.
   * Als Beschreibung wird desc gesetzt.
   * @param int projectID
   * @param Event ev
   * @param String desc
   */
  public void addProjectToEvent(int projectID, Event ev, String desc) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("INSERT INTO " + pref + "_project"
        + "(EventID,ProjectID,Description) VALUES(" + ev.getID() + "," + projectID + ",'" + desc
        + "')");
    
    this.sync();
  }
  
  /**
   * Fuegt das Projekt p dem Event ev hinzu.
   * @param Project p
   * @param Event ev
   */
  public void addProjectToEvent(Project p, Event ev) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("INSERT INTO " + pref + "_project"
        + "(EventID,ProjectID,Description,DateEdit,Deadline) VALUES(" + 
        ev.getID() + "," + p.getID() + ",'" + p.getDescription() + "','" + 
        p.getStartDate() + "','" + p.getEndDate()
        + "')");
    
    this.sync();
  }
  
  /** 
   * Entfernt einen Termin aus der Datenbank.
   */
  public void removeTerm(int termID) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	stmt.addBatch("DELETE FROM " + pref + "_termToStudent WHERE TermID=" + termID);
  	stmt.addBatch("DELETE FROM " + pref + "_term WHERE TermID=" + termID);
  	stmt.executeBatch();
  	
  	this.sync();
  }
 
  /** 
   * Traegt einen Studenten aus einer Veranstaltung aus.
   * @param Student stu, Event evt
   */
  public void removeStudentFromEvent(int stuMatr, int evtID) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	stmt.addBatch("DELETE FROM " + pref + "_eventToStudent WHERE EventID=" + evtID +" AND MatrikelNo="+stuMatr);
  	stmt.executeBatch();
  	
  	this.sync();
  }

  /** 
   * Entfernt einen Studenten aus einem Projekt.
   * @param Student stu
   * @param int projectID
   */
  public void removeStudentFromProject(int matrikelNo, int projectID) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	stmt.addBatch("DELETE FROM " + pref + "_projectToStudent WHERE ProjectID=" + projectID +" AND MatrikelNo="+matrikelNo);
  	stmt.executeBatch();
  	
  	this.sync();
  }
  
  
  /** 
   * Loescht ein Projekt.
   * @param int Projekt ID
   */
  public void removeProject(int projectID) throws SQLException
  {
  	String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
  	stmt.addBatch("DELETE FROM " + pref + "_projectToStudent WHERE ProjectID=" + projectID);
  	stmt.addBatch("DELETE FROM " + pref + "_project WHERE ProjectID=" + projectID);
  	stmt.executeBatch();
  	
  	this.sync();
  }
  
  /** Updatet ein Projekt */
  public void updateProject(int projectID, String desc, Timestamp begin, Timestamp end) throws SQLException
  {									
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("UPDATE "
        + Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX)
        + "_project set " + "Description   = '" + desc + "', "
        + "DateEdit  = '" + begin.toString() + "', Deadline = '" + end.toString() +
        "' where ProjectID = " + projectID);
    this.sync();
  }
  
  /**
   * Fuegt den Studenten stud der Group g hinzu.
   * @author PR
   * @param Student stud
   * @param Group g
   */
  public void addStudentToGroup(Student stud, Group g) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("INSERT INTO " + pref + "_groupToStudent"
        + "(GroupID,MatrikelNo) VALUES(" + g.getID() + "," + stud.getMatNr()
        + ")");
    
    this.sync();
  }

  /**
   * Entfernt den Studenten stud aus der Group g.
   * @param Student stud
   * @param Group g
   */
  public void removeStudentFromGroup(Student stud, Group g) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("DELETE FROM " + pref + "_groupToStudent WHERE "
        + "GroupID=" + g.getID() + " AND MatrikelNo=" + stud.getMatNr());
    
    this.sync();
  }

  /**
   * Fuegt den Tutor tut der Group g hinzu.
   * @param Tutor tut
   * @param Group g
   */
  public void addTutorToGroup(Tutor tut, Group g) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("INSERT INTO " + pref + "_groupToTutor"
        + "(GroupID,TutorID) VALUES(" + g.getID() + "," + tut.getID()
        + ")");
    
    this.sync();
  }

  /**
   * Entfernt den Tutor tut aus der Group g.
   * @param Tutor tut
   * @param Group g
   */
  public void removeTutorFromGroup(Tutor tut, Group g) throws SQLException
  {
    String pref = Config.getInstance().get(CONFIG_TABLE_PREFIX, DEFAULT_PREFIX);
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("DELETE FROM " + pref + "_groupToTutor WHERE "
        + "GroupID=" + g.getID() + " AND TutorID=" + tut.getID());
    
    this.sync();
  }

  /**
   * Fuehrt ein COMMIT aus. Derzeit nicht notwendig.
   * @throws SQLException
   */
  public void sync() throws SQLException
  {
    this.connection.commit();
  }
}
