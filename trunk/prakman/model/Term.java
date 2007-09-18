package prakman.model;

import java.sql.Timestamp;

/**
 * Repraesentiert den Termin einer Veranstaltung.
 */
public class Term
{
	private	int	       termID;
	private	int	       eventID;
	private	Timestamp	 date;
	
	/** Konstruktor: Termin
	 * @param	int termID
   * @param int eventID
   * @param Timestamp date
	 */
	public Term(int termID, int eventID, Timestamp date)
	{
		this.termID   = termID;
		this.eventID  = eventID;
		this.date	    = date;
	}
	
	/** Getter: TermID
	 * @return int TermID
	 */
  public int getTermID()
  {
	  return termID;
  }
  
   /**
	 * @return int Zugehoerige EventID dieses Termins.
	 */
	 public int getEventID()
	 {
		 return eventID;
	 }
	 
		/** 
		 * @return Date Datum dieses Termins.
		 */
	public Timestamp getDate()
	{
		return date;
	}
  
}
