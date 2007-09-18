package prakman.view.print;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JPanel;
import prakman.io.Database;
import prakman.io.ListPrinter;
import prakman.model.Student;
import prakman.model.Event;
import prakman.model.Term;
import prakman.model.Workspace;

/**
 * Leinwand, welche vom Druckvorschau-Dialog benutzt wird.
 */
public class PrintPreviewCanvas extends JPanel
{
  private static final long serialVersionUID = 0;
  
  private int           currentPage = 1;
  private ListPrinter   listPrinter;
  private Database      db;
  
  public PrintPreviewCanvas(Workspace workspace)
    throws SQLException, ClassNotFoundException
  {
    db = workspace.getDatabase();
  }
  
  /**
   * Erstellt eine Standard-Voransicht.
   * */
  public int createPreview()
  {
    int result = 0 ;   
    
    // Mitlaufendes Array fuer die Untertexte
    ArrayList<String> underText   = new ArrayList<String>();
    ArrayList<String> underEvents = new ArrayList<String>();
    
    int runningUnderTextCounter = 1;
    int runningUnderEventCounter = 1;
	    
    String[] test = new String[4];
    test[0] = "Name";
    test[1] = "MatrikelNr.";
    test[2] = "Veranst.";
    test[3] = "Projekte";

    try
    {    	
      result = db.getStudentsInEvent(0).toArray(new Student[0]).length;
      if (result == 0)
        return 0;
      
      // Get all students
      ArrayList<Student> students = db.getStudents();
      String[][] values           = new String[test.length][students.size()];

      int matrNr = 0;
      for (int i = 0; i < students.size(); i++)
      {
        for (int j = 0; j < test.length; j++ )
        {
          if(j == 0)
            values[j][i] = students.get(i).getFirstName() + " " + students.get(i).getLastName(); 
          else if(j == 1)
		    	{
            values[j][i] = Integer.toString(students.get(i).getMatNr()); 
            matrNr = Integer.parseInt(values[j][i]);
		    	}
          else if(j == 2)
          {
            ArrayList<Event> events = db.getAttendedEvents(students.get(i));
            String eventBuffer = "";
            for (int k = 0 ; k < events.size(); k++)
            {
              eventBuffer += (k+1) + ",";	
              
              if (!(underEvents.contains(events.get(k).getDesc())))
              {
                runningUnderEventCounter++;
                underEvents.add(events.get(k).getDesc());
              }	
            }
            values[j][i] = eventBuffer;		    				
          }
          else if(j == 3)
          {
            String             conclusion = "";
            ArrayList<String> results     = db.getProjectsByStudent(matrNr);
            
            for (int k = 0; k < results.size(); k++)
            {
              if(underText.contains(results.get(k)))
              {
		    					//System.out.println("Schon vorhanden...");		    					
              }
              else
              {
                conclusion += runningUnderTextCounter + ",";
                runningUnderTextCounter++;
                underText.add(results.get(k));		    					
              }	  	
            }
            values[j][i] = conclusion;
          }
        }
      }
  
      int size = underText.size()+underEvents.size();
      String[] underBuffer = new String[size];

      int k = 1;
      for(int i = 0; i < underText.size() ; i++)
      {
        underBuffer[i] = "Projekt Nr:" + (i+1) +" = " + underText.get(i);
      }

      for(int i = underText.size(); i < underText.size() + underEvents.size(); i++)
      {
        underBuffer[i] = "Event Nr:" + (k) +" = " + underEvents.get(k-1);
        k++;
      }

      System.out.println("UnderText:"+underText.size());   	
      this.listPrinter    = new ListPrinter(values, test);
      this.listPrinter.setUnderText(underBuffer);
	    this.listPrinter.createPreview("Studentenliste");
    }
	  catch(SQLException e)
    {
      e.printStackTrace();
    }

	  return result;
  }
  
  /**
   * Erstellt eine Voransicht zum Drucken von Gruppen.
   **/
  public int createGroupPreview(int groupID)
  {  	    
    String[] rowNames = new String[3];
    rowNames[0] = "Name";
    rowNames[1] = "MatrikelNr.";
    rowNames[2] = "E-Mail";
	    
    try
    {
      // Gruppen holen 
      ArrayList<Student> students = db.getStudentsInGroup(groupID);
	    	
      // Wenn keine studenten vorhanden sind return
      if (students.size() == 0)
        return 0;

      String[][] values = new String[rowNames.length][students.size()];
      for (int i = 0; i < students.size(); i++)
      {
        values[0][i] = students.get(i).getFirstName()+" "+
	    						   students.get(i).getLastName();
        values[1][i] = Integer.toString(students.get(i).getMatNr());
        values[2][i] = students.get(i).getEmail();				  
      }
	
      this.listPrinter    = new ListPrinter(values, rowNames);
      this.listPrinter.createPreview(db.getGroup(groupID).getDesc());

      return students.size();
    }
    catch(SQLException e)
    {
      e.printStackTrace();
    }

    return 0;
  }
  
  /**
   * Erstellt eine Voransicht zum Drucken von Notenlisten 
   * */
  public int createMarkListPreview(int eventID)
  {
    ArrayList<Integer> projectsInEvent = db.getProjectsInEvent(eventID);
	  if(projectsInEvent.size() <= 0 )
	    return 0;
	  
	  try
	  {
	    // Ueberschriften
	    System.out.println("NrProjects:" + projectsInEvent.size());
	    String[] projectDesc         = new String[projectsInEvent.size()];
	    String[] projectStringBuffer = new String[projectsInEvent.size()+1];
			  
	    projectStringBuffer[0] = "Name";
	    for(int i = 0; i < projectsInEvent.size() ;i++)
	    {
	      // Ueberschriften 
	      projectStringBuffer[i+1] = "Nr:" + (i+1);
        
	      // UnterText Die PrjBeschreibung wird geholt.
	      projectDesc[i] = "Nr:" + (i+1) + " = " + db.getProjectDesc(projectsInEvent.get(i));
	    }			  

	    // TabellenWerte
      ArrayList<Integer> projectBuffer = db.getProjectsInEvent(eventID);
      ArrayList<Student> students = db.getStudentsInEvent(eventID);
	    String[][] values = new String[projectStringBuffer.length][students.size()];
	    for (int j = 0; j < projectStringBuffer.length; j++)
	    {
	      for (int i = 0; i < students.size(); i++)
	      {
	        if (j == 0)
	          values[j][i] = students.get(i).getFirstName() + " " 
						  + students.get(i).getLastName(); 
	        else
					{
	          int len = db.getResults(students.get(i), "EventName").size();
						if (len > 0)						 
              values[j][i] = db.getMark(students.get(i).getMatNr(), projectBuffer.get(j-1));					 		    						    				
						else
						{
						  values[j][i] = "";	
						}
					}
	      }
	    }
			  
	    this.listPrinter = new ListPrinter(values, projectStringBuffer);
	    this.listPrinter.setUnderText(projectDesc);
	    this.listPrinter.createPreview("Bewertung");
            
    }
	  catch(SQLException e)
	  {
	    e.printStackTrace();
    } 
	
    return projectsInEvent.size();
  }
 
  /**
   * Erstellt eine Voransicht zum Drucken von Anwesenheitslisten.
   **/
  public int createPresenceListPreview(int eventID)
  { 
	  try
	  {
      ArrayList<Term> termsInEvent = db.getTermsInEvent(eventID);
	    if (termsInEvent.size() == 0)
	      return 0;
	    
	    String[] datesInEvent = new String[termsInEvent.size() + 1];
	    datesInEvent[0] = "Name";
      
	    for (int i = 1; i < termsInEvent.size() + 1 ;i++)
	    {
        // What the hell wird hier gemacht?
	      datesInEvent[i] = termsInEvent.get(i-1).getDate().toString().substring(8, 10)+
	    					  "."+termsInEvent.get(i-1).getDate().toString().substring(5, 7)+".";
	    }

      ArrayList<Student> students = db.getStudentsInEvent(eventID);
	    String[][] values = new String[datesInEvent.length][students.size()];
		    
	    for (int j = 0; j < datesInEvent.length; j++)
	    {
	      for (int i = 0 ; i < students.size() ; i++)
	      {
	        if (j == 0)
	          values[j][i] = students.get(i).getFirstName() + " " 
		    				  	      + students.get(i).getLastName(); 
	        else
	        {
	          // Abfrage ob der Student hier anwesend war!
	          if (db.getAttendantsStatus(students.get(i).getMatNr(), termsInEvent.get(j-1).getTermID()))
		    				values[j][i] = "X";
		    		}
		    	}
		    }
	
	    this.listPrinter    = new ListPrinter(values, datesInEvent);
	    this.listPrinter.createPreview("Anwesenheit");

      return termsInEvent.size();
	  }
    catch(SQLException e)
	  {
      e.printStackTrace();
	  }
	     
    return 0;
  }
  
  /**
   * Gibt die bevorzugten Dimensionen dieses Objektes zurueck.
   */
  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(listPrinter.getPreviewPages().get(0).getWidth()+50,
         listPrinter.getPreviewPages().get(0).getHeight()+50);
  }
  
  /**
   * Zeichnet die vom ListPrinter erzeugten Vorschaubilder.
   */
  public void paintComponent(Graphics g)
  {
    g.setColor(Color.GRAY);
    g.fillRect(0, 0, 4000, 4000);
    
    BufferedImage img = listPrinter.getPreviewPages().get(currentPage-1);
    
    // Zeichne Schatten
    g.setColor(Color.BLACK);
    g.fillRect(29, 29, img.getWidth(), img.getHeight());
    
    // Zeichne Bild der Druckvorschau mit schwarzem Rand herum
    g.drawImage(img, 25, 25, this);
    g.drawRect(25, 25, img.getWidth()-1, img.getHeight()-1);
  }
  
  /**
   * Liefert den das Printable-Objekt fuer das Java Drucksystem.
   **/
  public ListPrinter getPrintable()
  {
    return listPrinter;
  }
  
  /**
   * Setzt die momentane Seite.
   **/
  public void setCurrentPage(int page)
  {
    currentPage = page;
  }
  
  /**
   * Setzt das Seitenformat im Drucker.
   **/
  public void setPageFormat(PageFormat pageFormat)
  {
    this.listPrinter.setPageFormat(pageFormat);
    this.listPrinter.createPreview("");
    
    // Setze die Groesse dieses Components
    setPreferredSize(new Dimension(listPrinter.getPreviewPages().get(0).getWidth()+50,
        listPrinter.getPreviewPages().get(0).getHeight()+50));
  }
}
