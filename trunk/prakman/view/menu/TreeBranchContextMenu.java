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
import prakman.io.Resource;
import prakman.model.Workspace;

/**
 * ContextMenu fuer die Baumordner, die weitere Unterobjekte enthalten.
 */
public abstract class TreeBranchContextMenu extends TreeContextMenu
{  
  public static TreeBranchContextMenu getInstance(Object obj)
  {
    String str = obj.toString();
    
    if(str.equals(Workspace.EVENT))
      return new TreeBranchEventContextMenu(obj);
    else if(str.equals(Workspace.STUDENT))
      return new TreeBranchStudentContextMenu(obj);
    else if(str.equals(Workspace.TUTOR))
      return new TreeBranchTutorContextMenu(obj);
    
    return null;
  }
  
  protected JMenuItem mnuAdd = new JMenuItem("Hinzuf\u00FCgen", Resource
      .getImage("resource/gfx/icons/list-add_16_16.png"));

  protected JMenuItem mnuImport = new JMenuItem("Liste importieren...", Resource
      .getImage("resource/gfx/icons/list-import_16_16.png"));

  protected JMenuItem mnuExport = new JMenuItem("Liste exportieren...", Resource
      .getImage("resource/gfx/icons/list-export_16_16.png"));

  protected JMenuItem mnuClear = new JMenuItem("Alle l\u00F6schen", Resource
      .getImage("resource/gfx/icons/edit-clear_16_16.png"));
  
  protected TreeBranchContextMenu(Object obj)
  {
    super(obj);
    init();
  }
  
  public abstract void addClicked();
  public abstract void deleteAllClicked();
  public abstract void exportClicked() throws Exception;
  public abstract void importClicked();
  
  /** Kontextmenu fuer die Ueberordner */
  private void init()
  {
    add(mnuAdd);
    add(mnuImport);
    add(mnuExport);
    add(mnuClear);
    mnuAdd.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        addClicked();
        Workspace.getInstance().updateTree("");
      }
    });
    
    mnuImport.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        importClicked();
        Workspace.getInstance().updateTree("");
      }
    });
    
    mnuExport.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          exportClicked();
          Workspace.getInstance().updateTree("");
        }
        catch(Exception ex)
        {
          String[] msg = 
            {"Es ist ein Fehler beim Export aufgetreten!",
              ex.getMessage()};
          JOptionPane.showMessageDialog(null, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    
    mnuClear.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        deleteAllClicked();
        Workspace.getInstance().updateTree("");
      }
    });
  }
}
