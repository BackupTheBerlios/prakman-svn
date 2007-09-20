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

package prakman.view.inset;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import prakman.io.Resource;
import prakman.io.Saveable;
import prakman.model.Workspace;
import prakman.view.BasePanel;
import prakman.view.MainFrame;

/**
 * Panel, dass einen Speichern- und einen Zuruecksetzen-Button bereit
 * stellt.
 * Panels, welche dieses Panel verwenden, muessen das Interface
 * prakman.io.Saveable implementieren.
 */
public class SaveResetEditPanel extends JPanel
  implements ActionListener
{
  private static final long serialVersionUID = 0;
  // Referenz auf das Panel, aus welchem die Werte zum Speichern gelesen werden
  private BasePanel	panelRef;
  
  // Buttons
  private JButton	btnSave	= new JButton(Resource.getImage("resource/gfx/icons/document-save_16_16.png") );
  private JButton btnReset	= new JButton(Resource.getImage("resource/gfx/icons/edit-undo_16_16.png")  );
  
  /** Konstruktor */
  public SaveResetEditPanel(BasePanel panelRef)
  {
	  this.panelRef = panelRef;    
	  setLayout(new FlowLayout(FlowLayout.LEADING,4,4));
	  btnSave.setToolTipText("Speichern");
	  btnReset.setToolTipText("R\u00FCckg\u00E4ngig");
	  add(btnSave);
	  add(btnReset);	  
	  btnSave.addActionListener(this);
	  btnReset.addActionListener(this);
  }
  
  /**
   * Ein Event ist aufgetreten.
   */
  public void actionPerformed(ActionEvent e)
  {
	  // Save-Button gedrueckt
	  if (e.getSource().equals(btnSave))
	  {
      Saveable saveObj = panelRef.getSaveable();
      if(saveObj == null || !saveObj.save())
      {
        // Zeige Fehlerdialog
        String[] msg = {"Es ist ein Fehler beim Speichern aufgetreten!"};
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
      }
      else
      {
        int tabIndex = MainFrame.getInstance().getTabbedPane().getTabIndex(saveObj);
        if(tabIndex != -1)        
          MainFrame.getInstance().getTabbedPane().setTitleAt(tabIndex, saveObj.toString());
        Workspace.getInstance().updateTree("");
      }
	  }
	  // Reset-Button gedrueckt
	  else if (e.getSource().equals(btnReset))
	  {
      panelRef.reset();
	  }
  }

}
