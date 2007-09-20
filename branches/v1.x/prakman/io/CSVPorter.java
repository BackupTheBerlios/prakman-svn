package prakman.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import prakman.model.Event;
import prakman.model.Group;
import prakman.model.Project;
import prakman.model.Student;
import prakman.model.Term;
import prakman.model.Tutor;
import prakman.model.Workspace;

/**
 * Klasse, die den CSV-Export und -Import von Listen (Student, Tutor, Event)
 * und einzelnen Objekten (Event) uebernimmt.
 * @author CL
 */
public abstract class CSVPorter
{
  /**
   * Importiert CSVPortable Objekte von der angegebenen Datei und Klasse.
   * @param fileName
   * @param type Klasse der tatsaechlichen CSVPortable-Implementierung.
   * @return
   */
  public static CSVPortable[] importFrom(String fileName, Class type)
  {
    try
    {
      CSVProperties csv = new CSVProperties();
      csv.load(fileName);
      
      CSVPortable[] ptbl        = new CSVPortable[csv.size()];
      String[]      columnNames = null;

      for(int n = 0; n < csv.size(); n++)
      {
        ptbl[n] = (CSVPortable)type.newInstance();
        if(columnNames == null)
          columnNames = ptbl[n].getColumnNames();
        
        String[] row = new String[columnNames.length];
        
        for(int m = 0; m < columnNames.length; m++)
        {
          row[m] = csv.get(n, m);
        }

        ptbl[n].setRow(row);
      }      
      return ptbl;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Exportiert CSVPortable Objekte in die angegebene CSV-Datei.
   * @param fileName
   * @param data
   * @return
   */
  public static boolean exportTo(String fileName, CSVPortable[] data)
  {
    try
    {
      String[]      columnNames = data[0].getColumnNames();
      CSVProperties prop        = new CSVProperties(columnNames);

      for(int n = 0; n < data.length; n++)
      {
        for(int m = 0; m < columnNames.length; m++)
          prop.set(n, data[n].getRow());
      }
      
      prop.save(fileName);
      return true;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Da der Export eines einzelnen Events besondere Handlungen erfordert,
   * gibt es diese Methode. Hinweis: zum Exportieren von Event-Listen ist
   * diese Methode nicht geeignet, verwende exportTo() stattdessen.
   * 
   * Es muessen folgende Daten exportiert werden:
   * - Name des Events
   * - Daten des Betreuers
   * - Daten jedes einzelnen Studenten (Teilnehmer)
   * - Projekte
   * - Termine
   * 
   * @param fileName
   * @param data
   * @return
   */
  public static boolean exportEventTo(String fileName, Event event)
  {
    try
    {
      Database db = Workspace.getInstance().getDatabase();
      
      // Oeffne Datei zum Schreiben
      PrintWriter out = new PrintWriter(
          new OutputStreamWriter(new FileOutputStream(fileName)));
      
      // Allgemeine Infos des Events
      out.println("Veranstaltung " + event.getName());
      out.println(event.getDesc());
      out.println(event.getID());
      out.println();
      
      // Daten des Eventbetreuers
      out.println("Betreuer");
      out.print(event.getTutor().getID() + ", ");
      out.print(event.getTutor().getLastName() + ", ");
      out.print(event.getTutor().getFirstName());
      out.println();
      out.println();
      
      // Gruppen
      out.println("Gruppen");
      out.println("Gruppen-Nr., Beschreibung");
      ArrayList<Group> groups = db.getGroupsInEvent(event.getID());
      for(Group g : groups)
      {
      	out.print(g.getID() + ",");
      	out.print(g.getDesc().replace(",", "\\0x2C"));	// Kommas ersetzen um csv zu sichern
      	out.println();
      }
      out.println();
      
      // Teilnehmer
      out.println("Studenten");
      out.println("Matrikelnummer, Name, Vorname, Email, Gruppen-Nummer");
      ArrayList<Student> stds = db.getStudentsInEvent(event.getID());
      for(Student std : stds)
      {
        out.print(std.getMatNr() + ", ");
        out.print(std.getLastName() + ", ");
        out.print(std.getFirstName() + ", ");
        out.print(std.getEmail() + ",");
        Group eventGroup = db.getEventGroup(std, event.getID());
        out.print(eventGroup == null ? 0 : eventGroup.getID());
        out.println();
      }
      out.println();
      
      // Projekte der Veranstaltung
      out.println("Projekte");
      out.println("ID, Beschreibung, Start, Ende");
      ArrayList<Integer> pids = db.getProjectsInEvent(event.getID());
      
      for(int pid : pids)
      {
        String desc = db.getProjectDesc(pid).replace(",", "\\0x2C");
        Timestamp start  = db.getProjectDate(pid);
        Timestamp end    = db.getProjectDeadline(pid);
        if(end == null)
          end = new Timestamp(Long.MAX_VALUE);
        
        out.print(pid + ",");
        out.print(desc + ",");
        out.print(start.getTime() + ",");
        out.print(end.getTime());
        out.println();
      }
      out.println();
      
      // Noten fuer die Projekte
      out.println("Benotung");
      out.println("Projekt-ID, Matrikel-Nr., Note");
      
      for(Student std : stds)
      {
      	ArrayList<ArrayList<Object>> notenInfo = db.getResults(std, "ProjectID");
      	for(ArrayList<Object> o : notenInfo)
      	{
      		out.print(o.get(0) + ",");
        	out.print(std.getMatNr() + ",");
        	out.print(((String)o.get(2)).replace(",", "\\0x2C"));
        	out.println();
      	}
      }
      out.println();
      
      // Termine der Veranstaltung
      out.println("Termine");
      out.println("ID, Datum");
      ArrayList<Term> terms = db.getTermsInEvent(event.getID());
      
      for(Term term : terms)
      {
        out.print(term.getTermID() + ",");
        out.print(term.getDate().getTime());
        out.println();
      }
      out.println();
      
      // Datei abschliessen
      out.flush();
      out.close();
      
      return true;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }
  
  /**
   * Importiert ein Event aus einer CSV-Datei.
   * @param fileName
   * @return
   */
  public static Event importEventFrom(String fileName)
  {
    try
    {
      Database db       = Workspace.getInstance().getDatabase();
      Event event       = new Event();
      BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
      
      String line = in.readLine();
      if(!line.startsWith("Veranstaltung "))
        return null;
      
      // Lese Name der Veranstaltung
      line = line.substring("Veranstaltung ".length());
      event.setName(line);
      
      // Lese Beschreibung
      line = in.readLine();
      event.setDesc(line);
      
      // Lese Event ID
      line = in.readLine();
      event.setID(Integer.parseInt(line.trim()));
      
      in.readLine(); // Ueberspringen
      
      // Lese Betreuer
      line = in.readLine();
      if(!line.equals("Betreuer"))
        return null;
      
      line = in.readLine();
      String[] lines = line.split(",");
      int    id         = Integer.parseInt(lines[0].trim());
      String lastName   = lines[1].trim();
      String firstName  = lines[2].trim();
      Tutor tutor = new Tutor(id, lastName, firstName);
      
      event.setTutor(tutor);
      in.readLine();
      
      // Event in die Datenbank schreiben.
      // Dies ist hier schon notwendig, da Term darauf verweist.
      Event oldEvent = Workspace.getInstance().getDatabase().getEvent(event.getID());
      if(oldEvent != null)
      {
        if(oldEvent.equals(event))
          Workspace.getInstance().getDatabase().updateEvent(event);
        else
        {
          do
            event.setID(event.getID()+1);
          while(Workspace.getInstance().getDatabase().getEvent(event.getID()) != null);
          Workspace.getInstance().getDatabase().addEvent(event);
        }
      }
      else
        Workspace.getInstance().getDatabase().addEvent(event);
      
      // Gruppen
      line = in.readLine();
      if(!line.equals("Gruppen"))
      	return null;
      line = in.readLine();	// Spaltenueberschriften
      ArrayList<Group> groups = new ArrayList<Group>();
      for(;;)
      {
        line = in.readLine();
        if(line == null || line.equals(""))
          break;
        
        lines = line.split(",");	// Gruppen-ID, Beschreibung
        groups.add(
        		new Group(Integer.parseInt(lines[0]),lines[1].replace("\\0x2C", ","))
        		);
      }
      
      // Teilnehmer
      line = in.readLine();
      if(!line.equals("Studenten"))
        return null;
      line = in.readLine(); // Spaltenueberschriften
      ArrayList<Student> students = new ArrayList<Student>();
      ArrayList<ArrayList<Integer>> studToGroup = new ArrayList<ArrayList<Integer>>();
      for(;;)
      {
        line = in.readLine();
        if(line == null || line.equals(""))
          break;
        
        lines = line.split(","); // Matrikelnummer, Name, Vorname, Email
        int     matNr = Integer.parseInt(lines[0].trim());
        String  name  = lines[1].trim();
        String  fname = lines[2].trim();
        String  email = lines[3].trim();
        int			gruppe= Integer.parseInt(lines[4].trim());
        
        students.add(new Student(name, fname, matNr, email));
        // Gruppenzuordnung
        ArrayList<Integer> innerStudToGroup = new ArrayList<Integer>();
        innerStudToGroup.add(matNr);
        innerStudToGroup.add(gruppe);
        
        studToGroup.add(innerStudToGroup);
      }
      
      // Projekte einlesen
      line = in.readLine();
      if(!line.equals("Projekte"))
        return null;
      line = in.readLine(); // ID, Beschreibung, Start, Ende
      
      ArrayList<Project> projects = new ArrayList<Project>();
      
      for(;;)
      {
        line = in.readLine();
        if(line == null || line.equals(""))
          break;
        
        lines = line.split(",");
        int     pid   = Integer.parseInt(lines[0].trim());
        String  desc  = lines[1].trim().replace("\\0x2C", ",");
        Timestamp    start = new Timestamp(Long.parseLong(lines[2].trim()));
        Timestamp    end   = new Timestamp(Long.parseLong(lines[3].trim()));
        
        // Projekte muessen gespeichert werden, da eventuell
        // das Projekt noch nicht angelegt wurde
        projects.add(new Project(pid, desc, start, end));
      }
      
      // Benotung einlesen
      line = in.readLine();
      if(!line.equals("Benotung"))
      	return null;
      line = in.readLine(); // Projekt-ID, Matrikel-Nr., Note
      
      ArrayList<ArrayList<Object>> results = new ArrayList<ArrayList<Object>>();
      
      for(;;)
      {
      	line = in.readLine();
      	if (line == null || line.equals(""))
      		break;
      	
      	lines = line.split(",");
      	
      	ArrayList<Object> innerResults = new ArrayList<Object>();
      	
      	innerResults.add(Integer.parseInt(lines[0].trim()));	// ProjectID
      	innerResults.add(Integer.parseInt(lines[1].trim()));	// Matr-Nr.
      	innerResults.add(lines[2].trim().replace("\\0x2C", ","));	// Result
      	
      	results.add(innerResults);
      }
      
      // Termine einlesen
      line = in.readLine();
      if(!line.equals("Termine"))
        return null;
      line = in.readLine(); // Datum
      
      ArrayList<Term> terms = new ArrayList<Term>();
      
      for(;;)
      {
        line = in.readLine();
        if(line == null || line.equals(""))
          break;
        
        lines = line.split(",");
        terms.add(
            new Term(
                Integer.parseInt(lines[0].trim()), 
                event.getID(), 
                new Timestamp(Long.parseLong(lines[1].trim()))
                    )
            );
      }
      
      // Aenderungen in die Datenbank uebernehmen
      // Tutor
      Tutor oldTutor = Workspace.getInstance().getDatabase().getTutor(event.getTutor().getID());
      if(oldTutor != null)
      {
        if(oldTutor.equals(event.getTutor()))
          Workspace.getInstance().getDatabase().updateTutor(event.getTutor());
        else
        {
          do
            event.getTutor().setID(event.getTutor().getID()+1);
          while(Workspace.getInstance().getDatabase().getTutor(event.getTutor().getID()) != null);
        }
      }
      else
        Workspace.getInstance().getDatabase().addTutor(event.getTutor());

      
      // Teilnehmer
      for(Student std : students)
      {
        try
        {
          db.addStudent(std);
        }
        catch(SQLException e)
        {
          System.out.println("Student " + std + " schon vorhanden.");
        }
        db.addStudentToEvent(std, event);
      }
      
      // Gruppen
      for(Group g : groups)
      {
      	g.insertInEvent(event.getID());
      	
        // Gruppenzuordnung
        for(ArrayList<Integer> innerStudToGroup : studToGroup)
        {
        	int gNr = innerStudToGroup.get(1);
        	if(g.getID() == gNr)
        		g.add(db.getStudent(innerStudToGroup.get(0)));
        }
      }
      
      // Projekte
      for(Project p : projects)
      {
        try
        {
          db.addProjectToEvent(p, event);
        }
        catch(SQLException e)
        {
          //e.printStackTrace();
        	e.printStackTrace();
          System.out.println("Projekt " + p.getDescription() + " existiert schon!");
        }
      }
      
      
      // Benotung
      for(ArrayList<Object> res : results)
      {
      	try
      	{
      		int mNr = (Integer)res.get(1);
      		int pID = (Integer)res.get(0);
      		Student stud = null;
      		
      		for(Student st : students)
      		{
      			if (st.getMatNr() == mNr)
      				stud = st;
      		}
      		if (stud != null)
      		{
      			db.addStudentToProject(stud, pID);
      			db.setMark(mNr, pID, (String)res.get(2));
      		}
      		stud = null;
      	}
      	catch(SQLException e)
      	{
      		// setMark macht ein automatisches Update, falls Insert fehlschlaegt
      		// Jegliche Exception muss also ein echter Datenbankfehler sein!
      		e.printStackTrace();
      	}
      	
      }
      
      // Termine
      for(Term term : terms)
      {
        try
        {
          db.addTermToEvent(term, event);
        }
        catch(SQLException e)
        {
          System.out.println("Termin existiert schon!");
        }
      }
      
      // Workspace-Tree updaten
      Workspace.getInstance().updateTree("");
      
      return event;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
