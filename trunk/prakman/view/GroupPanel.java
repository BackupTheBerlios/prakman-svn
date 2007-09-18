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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import prakman.io.Database;
import prakman.io.Saveable;
import prakman.model.Group;
import prakman.model.Workspace;
import prakman.view.inset.SaveResetEditPanel;

/**
 *  
 */
public class GroupPanel extends BasePanel implements Saveable
{
	private static final long serialVersionUID = 0;
	
	private int        groupID;	
  private JTextField txtGroupNo;
  private JTextField txtGroupDesc;  
	private JPanel     groupsContentPanel	= new JPanel();
	
	public GroupPanel(int gID)
	{
		this.groupID = gID;
		
    // Felder fuellen
		try
		{
			Group g = Workspace.getInstance().getDatabase().getGroup(gID);
			txtGroupNo   = new JTextField(Integer.toString(((prakman.model.Group)g).getID()));
			txtGroupDesc	 = new JTextField(((prakman.model.Group)g).getDesc());			
			txtGroupNo.setEditable(false);	// die ID kann man nicht aendern
		}
		catch(SQLException sqlEx)
		{
			sqlEx.printStackTrace();
		}
		
		// attendantsContentPanel layouten
	  GridBagLayout      gbl = new GridBagLayout();
	  GridBagConstraints gbc = new GridBagConstraints();

	  groupsContentPanel.setLayout(gbl);    
	  gbc.fill    = GridBagConstraints.HORIZONTAL;
	    
	  gbc.weightx = 0.7; // Wir lassen links und rechts einen kleinen Rand frei
	  gbc.weighty = 0.7; // Wir lassen oben und unten einen kleinen Rand frei
	  gbc.gridy = 1;
	  gbc.gridx = 1;
	  groupsContentPanel.add(new SaveResetEditPanel(this),gbc);
	  gbc.gridy = 2;
	  groupsContentPanel.add(new JLabel("Gruppen-Nr."),gbc);
	  gbc.gridx = 2;
	  groupsContentPanel.add(txtGroupNo,gbc);
	  gbc.gridy = 3;
	  gbc.gridx = 1;
	  groupsContentPanel.add(new JLabel("Beschreibung"),gbc);
	  gbc.gridx = 2;
	  groupsContentPanel.add(txtGroupDesc, gbc);	  
		this.add(groupsContentPanel, BorderLayout.CENTER);
	}

  /**
   * @return GroupID
   */
	public int getID()
  {
    return groupID;
  }
  
  /**
   * @return aktuelles Objekt
   */
	public Saveable getSaveable()
	{
	  return this;
	}

  /**
   * Setzt den Wert zurueck
   */
  public void reset()
  {
  	Database db = Workspace.getInstance().getDatabase();
  	try
  	{
  		Group g = db.getGroup(groupID);
  		this.txtGroupDesc.setText(g.getDesc());
  	}
  	catch(SQLException sqlEx)
  	{
  		sqlEx.printStackTrace();
  	}
  }
	  
  /**
   * Hier wird die Gruppe gespeichert werden.
   * Diese Methode wird automatisch vom SaveResetEditPanel
   * aufgerufen, wenn der Speichern-Button geklickt wurde.
   */
  public boolean save()
  {
  	Database db = Workspace.getInstance().getDatabase();
  	try
  	{
  		Group g = db.getGroup(groupID);
  		g.setDesc(this.txtGroupDesc.getText());
  		g.updateGroup();
  		JOptionPane.showMessageDialog(this, 
  				"Erfolgreich gespeichert.\nBitte das Fenster schlieen\u00DF,\num \u00C4nderungen zu sehen.");
  		return true;
  	}
  	catch(SQLException sqlEx)
  	{
  		sqlEx.printStackTrace();
  		return false;
  	}
  }
}
