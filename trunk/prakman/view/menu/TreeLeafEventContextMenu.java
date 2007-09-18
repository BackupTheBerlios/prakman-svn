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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import prakman.io.CSVPorter;
import prakman.io.Resource;
import prakman.model.Event;
import prakman.model.Workspace;
import prakman.view.MainFrame;
import prakman.view.PortableFileChooser;
import prakman.view.print.PrintPreview;

/**
 * Kontextmenue fuer die Veranstaltungs-Blaetter des Workspace-Baumes.
 */
public class TreeLeafEventContextMenu extends TreeLeafContextMenu
{
  private static final long serialVersionUID = 0;
  
  private JMenuItem mnuEventExport            = new JMenuItem("Export...");
  private JMenuItem mnuEventImport            = new JMenuItem("Import...");
  private JMenu     mnuEventPrint             = new JMenu("Drucken");
  private JMenuItem mnuEventPrintMarkList     = new JMenuItem("Notenliste");
  private JMenuItem mnuEventPrintPresenceList = new JMenuItem("Anwesenheitsliste");
  
  public TreeLeafEventContextMenu(Object obj)
  {
    super(obj);
    
    addSeparator();
    add(mnuEventExport);
    add(mnuEventImport);
    addSeparator();
    add(mnuEventPrint);
    
    mnuEventExport.setIcon(Resource.getImage("resource/gfx/icons/list-export_16_16.png"));
    mnuEventImport.setIcon(Resource.getImage("resource/gfx/icons/list-import_16_16.png"));
    mnuEventPrint.setIcon(Resource.getImage("resource/gfx/icons/document-print-preview_16_16.png"));
    
    mnuEventPrint.add(mnuEventPrintMarkList);
    mnuEventPrint.add(mnuEventPrintPresenceList);
    
    mnuEventExport.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        exportClicked();
      }
    });
    
    mnuEventImport.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        importClicked();
      }
    });
    
    mnuEventPrintMarkList.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        // System.out.println("PrintMarkList");
        ((prakman.model.Event) selectedObject).getDesc();
        PrintPreview printPreviewDialog = new PrintPreview(Workspace
            .getInstance()); // ohne Workspace keine Daten!
        if (printPreviewDialog
            .createPreviewMarkList(((prakman.model.Event) selectedObject)
                .getID()) == 0)
        {
          JOptionPane.showMessageDialog(printPreviewDialog,
              "Keine Projekte vorhanden!", "Hinweis",
              JOptionPane.INFORMATION_MESSAGE);
        }
        else
          printPreviewDialog.setVisible(true);
      }
    });

    mnuEventPrintPresenceList.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        // System.out.println("PrintPresenceList");
        PrintPreview printPreviewDialog = new PrintPreview(Workspace
            .getInstance()); // ohne Workspace keine Daten!
        if ((printPreviewDialog
            .createPreviewPresenceList(((prakman.model.Event) selectedObject)
                .getID())) == 0)
        {
          JOptionPane.showMessageDialog(printPreviewDialog,
              "Keine Termine vorhanden!", "Hinweis",
              JOptionPane.INFORMATION_MESSAGE);
        }
        else
          printPreviewDialog.setVisible(true);
      }
    });
  }
  
  @Override
  public void deleteClicked()
  {  
    //boolean delete = false;
    int yesNo = JOptionPane
        .showConfirmDialog(
            MainFrame.getInstance(),
            "Wenn Sie die Veranstaltung '"
                + selectedObject.toString()
                + "' l\u00F6schen,\n"
                + "werden s\u00E4mtliche Noten und Gruppen, die mit ihr in Verbindung stehen,\n"
                + "ebenfalls gel\u00F6scht.\n\nWollen Sie '"
                + selectedObject.toString() + "' wirklich l\u00F6schen?");
    if (yesNo == 0) // Ja
    {
      try
      {
        Workspace.getInstance().getDatabase().deleteEvent(
            (prakman.model.Event) selectedObject);
        JOptionPane.showMessageDialog(MainFrame.getInstance(), "'"
            + selectedObject.toString() + "' erfolgreich gel\u00F6scht.",
            "Veranstaltung l\u00F6schen", JOptionPane.INFORMATION_MESSAGE);
        Workspace.getInstance().updateTree("");
        super.deleteClicked();
      }
      catch (SQLException sqlEx)
      {
        System.out.println("L\u00F6schen gescheitert.");
        sqlEx.printStackTrace();
      }
    }
    else
      System.out.println("Abgebrochen");

  }
  
  private void exportClicked()
  {
    PortableFileChooser fc = new PortableFileChooser();
    fc.showSaveDialog(MainFrame.getInstance());
    
    if(fc.getSelectedFile() != null)
    {
      if(CSVPorter.exportEventTo(fc.getSelectedFile().getAbsolutePath(), (Event)selectedObject))
      {
        String[] msg = {"Erfolgreich exportiert!"};
        JOptionPane.showMessageDialog(MainFrame.getInstance(), msg);
      }
      else 
      {
        String[] msg = {"Beim Export ist ein Fehler aufgetreten!"};
        JOptionPane.showMessageDialog(MainFrame.getInstance(), msg);
      }
    }
  }
  
  static void importClicked()
  {
    try
    {
      PortableFileChooser fc = new PortableFileChooser();
      fc.showOpenDialog(null);
      
      if(fc.getSelectedFile() != null)
      {
        if(null == CSVPorter.importEventFrom(fc.getSelectedFile().getAbsolutePath()))
          throw new Exception();
        else
        {
          JOptionPane.showMessageDialog(MainFrame.getInstance(), "Import erfolgreich!");
        }
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      String[] msg = {"Beim Importieren ist ein Fehler aufgetreten!","", e.getMessage()};
      JOptionPane.showMessageDialog(MainFrame.getInstance(), msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
  }
}
