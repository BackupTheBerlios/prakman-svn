package prakman;

import java.util.Random;
import prakman.model.Student;
import prakman.model.Workspace;
import prakman.view.StartFrame;

/**
 * Startklasse des Programmes.
 */
public class Main 
{
  /**
   * Der Titel- und Versionstext des Programmes.
   */
  public static final String VERSION = "PrakMan Version 1.0";

  /**
   * Diese Testfunktion fuegt die gewuenschte Anzahl an
   * Dummy-Studenten in die Datenbank ein.
   * @param numStudents
   */
  public static void crashTest(Workspace ws, int numStudents)
  {
    System.out.println("CrashTest erwuenscht...");
    Random rnd = new Random();
    for(int n = 0; n < numStudents; n++)
    {
      try
      {
        Student std = new Student("Name" + n, "Vorname" + n, rnd.nextInt(), "test@test.com");
        ws.getDatabase().addStudent(std);
      }
      catch(Exception e)
      {
        System.out.println("crashTest: " + e.getMessage());
      }
    }
  }
  
  /**
   * Der Programm-Einsprung-Punkt.
   * @param args
   */
  public static void main(String[] args) 
  {
    Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    showStartFrame();
  }

  /**
   * Zeigt den StartFrame der Anwendung an.
   */
  public static void showStartFrame()
  {
    new StartFrame().setVisible(true);
  }
}
