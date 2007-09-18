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

package prakman.view.print;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import prakman.model.Workspace;

/**
 * Stellt eine Druckvorschau zur Verfuegung.
 * Dies ist das Fenster in dem die Vorschau dargestellt wird.
 */
public class PrintPreview extends JFrame
{
  private static final long serialVersionUID = 0;
  
  private PrintPreviewCanvas   canvas;
  private int                  currentPage = 1;
  private JPanel               toolbar;
  private Workspace            workspace;
  
  public PrintPreview(Workspace workspace)
  {
    this.workspace = workspace;
    setSize(700, 500);
    setTitle("Druckvorschau");
    setLayout(new BorderLayout());
    
    toolbar = buildToolbar();
    add(toolbar, BorderLayout.NORTH);
  }

  public PrintPreview()
  {
    workspace = Workspace.getInstance();

    setSize(700, 500);
    setTitle("Druckvorschau");
    setLayout(new BorderLayout());
  
    toolbar = buildToolbar();
    add(toolbar, BorderLayout.NORTH);
  }
  
  /**
   * Erstellt eine Standard-Voransicht.
   **/
  public int createPreview()
  {
	    int studentCount = 0 ; 
	  	// Erzeuge Vorschau
	    try
	    {
	      canvas = new PrintPreviewCanvas(workspace);
	      studentCount = canvas.createPreview();
	      add(new JScrollPane(canvas), BorderLayout.CENTER);
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    return studentCount;
  }
  
  /**
   * Erstellt eine Voransicht zum Drucken von Gruppen.
   **/
  public int createGroupPreview(int groupID)
  {
	    int studentCount = 0 ; 
	  	// Erzeuge Vorschau
	    try
	    {
	      canvas = new PrintPreviewCanvas(workspace);
	      studentCount = canvas.createGroupPreview(groupID);
	      add(new JScrollPane(canvas), BorderLayout.CENTER);
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    return studentCount;
  }
  
  
  /**
   * Erstellt eine Voransicht zum drucken von Notenlisten.
   **/
  public int createPreviewMarkList(int EventID)
  {
	    int projectsCount = 0;
	  	// Erzeuge Vorschau
	    try
	    {
	      canvas = new PrintPreviewCanvas(workspace);
	      projectsCount = canvas.createMarkListPreview(EventID);
	      add(new JScrollPane(canvas), BorderLayout.CENTER);
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    return projectsCount;
  }
 
  /**
   * Erstellt eine Voransicht zum drucken von Anwesenheitslisten.
   **/
  public int createPreviewPresenceList(int EventID)
  {
	    int termsCount = 0;
	    // Erzeuge Vorschau
	    try
	    {
	      canvas = new PrintPreviewCanvas(workspace);
	      termsCount = canvas.createPresenceListPreview(EventID);
	      add(new JScrollPane(canvas), BorderLayout.CENTER);
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    return termsCount;
  }
  
  /**
   * Erstellt die Werkzeugleiste im Voransichtsfenster.
   **/
  private JPanel buildToolbar()
  {
    JPanel toolbar = new JPanel();
    toolbar.setLayout(new FlowLayout());
    
    JButton btnPageFormat = new JButton("Seiteneinstellungen");
    btnPageFormat.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        PrinterJob job = PrinterJob.getPrinterJob();
        canvas.setPageFormat(job.pageDialog(job.defaultPage()));
        canvas.repaint();
      }
    });
    toolbar.add(btnPageFormat);
    
    JButton btnPrint = new JButton("Drucken");
    btnPrint.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        PrinterJob job = PrinterJob.getPrinterJob();
        if(job.printDialog())
        {
          try
          {
            // Drucke die Seiten
            job.setPrintable(canvas.getPrintable());
            job.print();
          }
          catch(PrinterException ex)
          {
            // Zeige Fehlerdialog           
            String[] msg = {"Es ist folgender Fehler aufgetreten:",
                ex.getLocalizedMessage()};
            
            JOptionPane.showMessageDialog(
                getParent(), msg, "Druckfehler",JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });
    toolbar.add(btnPrint);
    
    JButton btnBack = new JButton("<<");
    btnBack.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(currentPage > 1)
          currentPage--;
        setTitle("Druckvorschau - Seite " + currentPage);
        canvas.setCurrentPage(currentPage);
        canvas.repaint();
      }
    });
    toolbar.add(btnBack);
    
    JButton btnForward = new JButton(">>");
    btnForward.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(currentPage >= canvas.getPrintable().getPreviewPages().size())
          return;
        
        currentPage++;
        setTitle("Druckvorschau - Seite " + currentPage);
        canvas.setCurrentPage(currentPage);
        canvas.repaint();
      }
    });
    toolbar.add(btnForward);
    
    return toolbar;
  }
}
