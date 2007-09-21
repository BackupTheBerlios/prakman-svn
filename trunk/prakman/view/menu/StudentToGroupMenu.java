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

package prakman.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import prakman.model.Group;
import prakman.model.Student;
import prakman.model.Workspace;
import prakman.view.object.EventPanel;

/** 
 * Diese Klasse praesentiert ein Menue, mit welchem Studenten in eine
 * vorher fuer das entsprechende Event erstellte Gruppe geschoben werden
 * koennen.
 * Erwartet werden fuer den Konstruktor eine Referenz auf das Event-Panel
 * und eine ArrayList<Integer> mit Matrikelnummern von Studenten, die verschoben
 * werden sollen.
 * @author PR
 * @version	0.3
 */
public class StudentToGroupMenu extends JPopupMenu
{
	private static final long serialVersionUID = 0;
	
	private EventPanel thisPanel;
	private int eventID;
	
	private ArrayList<Integer> selectedMatrikelNos;	// Matrikelnummern der ausgewaehlten Studenten
	private ArrayList<Group> groupNo;	// Gruppen-Liste
	
	// Hauptmenue-Eintraege
	private JMenuItem	removeGroup;
	private JMenu	    setGroup;
	
	public StudentToGroupMenu(EventPanel ep, ArrayList<Integer> matNrn)
	{
		super("Gruppe setzen:");
		this.thisPanel = ep;
		this.eventID = ep.getID();
		this.selectedMatrikelNos = matNrn;
		
		ArrayList<JMenuItem> mItems = new ArrayList<JMenuItem>();
		setGroup = new JMenu("Gruppe setzen:");
		removeGroup = new JMenuItem("Aus Gruppe(n) entfernen");
		
		// ActionListener fuer RemoveGroup
		removeGroup.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Workspace ws = Workspace.getInstance();
				boolean deleted = false;

				// alle selektierten Studenten aus dieser Gruppe entfernen
				for(int i : selectedMatrikelNos)
				{
					try
					{
						Student stud = ws.findStudentByID(i);
						Group studGroup = ws.getDatabase().getEventGroup(stud, eventID);
						
						if (studGroup != null)
						{
							studGroup.delete(stud);
							deleted = true;
//							System.out.println("Entferne " + i + " aus der Gruppe Nr." + studGroup.getID());
						}
//						else
//							System.out.println(i + " ist in keiner Gruppe.");
					}
					catch(SQLException sqlEx)
					{
						sqlEx.printStackTrace();
					}
				}
				if (deleted)
				{
					JOptionPane.showMessageDialog(thisPanel, "Studenten erfolgreich\naus Gruppen entfernt");
					thisPanel.refreshPraksTable();
				}
			}
		});

		try
		{
			// Gruppennummern aus Veranstaltung holen
			groupNo = Workspace.getInstance().getDatabase().
				getGroupsInEvent(eventID);
			
			for(Group g : groupNo)
			{
				JMenuItem mItem = new JMenuItem(Integer.toString(g.getID()) + " (" + g.getDesc() + ")");
				mItems.add(mItem);
				setGroup.add(mItem);
				// ActionListener
				mItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						Workspace ws = Workspace.getInstance();
						
						for(Group g : groupNo)
						{
							// Gruppe in zur Verfuegung gestellten Gruppen suchen
							if (g.getID() == Integer.parseInt(e.getActionCommand().split(" ")[0])) // etwas gehackt!
							{
								// alle selektierten Studenten erst aus ihren alten Gruppen entfernen,
								// dann in die angegebene Gruppe einfuegen
								boolean added = false;
								
								for(int i : selectedMatrikelNos)
								{
									try
									{
										Student stud = ws.findStudentByID(i);
										Group studGroup = ws.getDatabase().getEventGroup(stud, eventID);
										if (studGroup != null)
										{
											studGroup.delete(stud);
//											System.out.println("Entferne " + i + " aus der Gruppe Nr." + studGroup.getID());
										}
									}
									catch(SQLException sqlEx)
									{
										sqlEx.printStackTrace();
									}
									
									g.add(ws.findStudentByID(i));
									added = true;
//									System.out.println("Fuege " + i + " in Gruppe " + g.getID() + " ein.");
								}
								if (added)
								{
									JOptionPane.showMessageDialog(thisPanel, "Student(en) erfolgreich in\nGruppen eingef\u00FCgt.");
									thisPanel.refreshPraksTable();
								}
							}
						}
					}
				});
			}
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
		
		this.add(setGroup);
		this.add(removeGroup);
	}

}
