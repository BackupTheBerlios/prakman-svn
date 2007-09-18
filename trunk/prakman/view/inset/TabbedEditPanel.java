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
import javax.swing.JPanel;
import prakman.io.*;

/**
 * Stellt ein Panel bereit, das Add, Remove, Select, Deselect Buttons 
 * bereit stellt.
 * Panels, welche dieses Panel verwenden wollen, muessen das Interface
 * TabbedEditable implementieren.
 */
public class TabbedEditPanel extends JPanel 
{
	private static final long serialVersionUID = 0;
	
  private JButton btnAdd			    = new JButton(Resource.getImage("resource/gfx/icons/list-add_16_16.png") );
  private JButton btnRemove			  = new JButton(Resource.getImage("resource/gfx/icons/list-remove_16_16.png") );
  private JButton btnSelectAll		= new JButton(Resource.getImage("resource/gfx/icons/edit-select-all_16_16.png"));
  private JButton btnDeselectAll	= new JButton(Resource.getImage("resource/gfx/icons/edit-deselect-all_16_16.png"));
 
  private TabbedEditable panelRef;
  
  /** Konstruktor */
  public TabbedEditPanel(TabbedEditable panel)
  {
    setLayout(new FlowLayout(FlowLayout.LEADING,4,4));
    this.panelRef = panel;
   
    // Buttons  
    add(btnAdd);
    add(btnRemove);
    add(btnSelectAll);
    add(btnDeselectAll);
    
    btnAdd.setToolTipText("Hinzuf\u00FCgen");
    btnRemove.setToolTipText("Entfernen");
    btnSelectAll.setToolTipText("Alles selektieren");
    btnDeselectAll.setToolTipText("Alles deselektieren");
    
    btnAdd.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        panelRef.addClicked();
      }
    });
    
    btnRemove.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        panelRef.removeClicked();
      }
    });
    
    btnSelectAll.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        panelRef.selectAllClicked();
      }
    });
    
    btnDeselectAll.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        panelRef.deselectAllClicked();
      }
    });
    
  }
  
  /**
   * @return Eine Referenz auf den Hinzufuegen-Button.
   */
  public JButton getAddButton()
  {
    return this.btnAdd;
  }
  
  /**
   * @return Eine Referenz auf den Entfernen-Button.
   */
  public JButton getRemoveButton()
  {
    return this.btnRemove;
  }

}
