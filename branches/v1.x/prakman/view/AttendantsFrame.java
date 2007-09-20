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


package prakman.view;

import java.sql.Timestamp;
import prakman.view.object.EventPanel;

/**
 * Diese Klasse stellt die Anwesenheitsliste der Studenten fuer 
 * ein Event dar. Das zugehoerige Panel, ist die AttendantsPanel
 * Klasse. 
 */
public class AttendantsFrame extends BaseFrame
{
	private static final long serialVersionUID = 0;
	
	private int eventID;
	private int termID;
  private Timestamp date;	
  private AttendantsPanel attendantsPanel;
  private EventPanel eventPanel;
	
	public AttendantsFrame(int eID, int tID, EventPanel evPanel)
	{
		setTitle("Anwesenheitsliste");
    this.eventPanel = evPanel;
		this.eventID	= eID;
		this.termID		= tID;
		this.attendantsPanel = new AttendantsPanel(eventID, termID,this);
    setContentPane(attendantsPanel);
		
		this.setLocation(MainFrame.getInstance().getLocation());
		this.pack();
		this.setVisible(true);
	}
  
  public AttendantsFrame(int eID, int tID, Timestamp dateTime, EventPanel evPanel)
  {
    setTitle("Anwesenheitsliste");
    this.date = dateTime;
    this.eventPanel = evPanel;
    this.eventID  = eID;
    this.termID   = tID;
    this.attendantsPanel = new AttendantsPanel(eventID, termID, date, this);
    setContentPane(attendantsPanel);
    
    this.setLocation(MainFrame.getInstance().getLocation());
    this.pack();
    this.setVisible(true);
  }
	
	/** 
   * @return Gibt die Tabelle fuer die Anwesenheit zurueck.
	 */
	public Table getAttendantsTable()
	{
		return attendantsPanel.getAttendants();
	}
	
	/** 
   * @return Gibt die ID des zugehoerigen Termins zurueck.
	 */
	public int getTermID()
	{
		return termID;
	}
  
  /**
   * @return Das aufrufende EventPanel.
   */
  EventPanel getEventPanel()
  {
    return this.eventPanel;
  }
  
  /**
   * @return Das Datum des Termins.
   */
  public Timestamp getDateTime()
  {
    return this.date;
  }
}
