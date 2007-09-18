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

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * Beschreibung einer Gruppe aendern
 */
public class GroupFrame extends JFrame
{
	private static final long serialVersionUID = 0;
	
	private int groupID;
  private GroupPanel groupPanel;
  
	public GroupFrame(int gID)
	{
		super("Gruppe bearbeiten");
		this.groupID	= gID;		
		setLayout(new BorderLayout());		
		this.groupPanel = new GroupPanel(groupID);
    setContentPane(groupPanel);		
		this.setLocation(MainFrame.getInstance().getLocation());
		this.pack();
		this.setVisible(true);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
