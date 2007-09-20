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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;


import prakman.view.print.PrintPreview;

/** 
 * Diese Klasse praesentiert ein Menue, mit welchem die Gruppen 
 * ausgedruckt werden koennen.
 * Erwartet werden fuer den Konstruktor die ID der Gruppe.
 * @author AD
 * @version	0.1
 */
public class PrintGroupMenu extends JPopupMenu
{
	private static final long serialVersionUID = 0;
	
	private int 		groupID;
	private JMenuItem	printButton;
	
	public PrintGroupMenu(int _groupID)
	{
		super("GruppeDrucken");
		groupID = _groupID;
		printButton = new JMenuItem("Diese Gruppe ausdrucken...");
		
		printButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PrintPreview print = new PrintPreview();
				if (  print.createGroupPreview(groupID) == 0 )
				{
			        JOptionPane.showMessageDialog(print,
			                "Keine Studenten in dieser Gruppe vorhanden!", "Hinweis",
			                JOptionPane.INFORMATION_MESSAGE);
				}
				else
					print.setVisible(true);
			
				//System.out.println("drucke Gruppe "+groupID+" gedr�ckt...");
			}
		});
		
		this.add(printButton);	
	}
}
