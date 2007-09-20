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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import prakman.Main;
import prakman.io.Resource;
import prakman.model.Workspace;
import prakman.view.print.PrintPreview;

/**
 * Das Hauptfenster der Anwendung.
 */
public class MainFrame extends JFrame implements ActionListener
{
  public static final String CONFIG_MAINFRAME_WIDTH   = "MainFrameWidth";
  public static final String CONFIG_MAINFRAME_HEIGHT  = "MainFrameHeight";
  
  private static final long serialVersionUID = 0;
  private static MainFrame instance;
  private JTextField txtFilter;
 
  /** gibt die Instance des MainFrames zurueck */
  public static MainFrame getInstance()
  {
    return instance;
  }
  // JTree
  private Workspace workspace = null;
  
  // Datei-Menu
  private JMenuItem mnuItemOpen;
  private JMenuItem mnuItemExit;
  private JMenuItem mnuItemInfo;
  private JMenuItem mnuItemPrint;

  // Tabbed Pane zur Anzeige
  private TabbedPane tabbedPane = new TabbedPane();

  public MainFrame(Workspace ws)
  {
    instance = this;
    this.workspace = ws;
    setTitle(Main.VERSION);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setIconImage(Resource.getImage("resource/gfx/icons/applications-other-16.png").getImage());

    // Fuege WindowListener hinzu
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        workspace.getConfig().set(CONFIG_MAINFRAME_WIDTH, getWidth());
        workspace.getConfig().set(CONFIG_MAINFRAME_HEIGHT, getHeight());
      }
    });
    
    int width  = workspace.getConfig().get(CONFIG_MAINFRAME_WIDTH, 800);
    int height = workspace.getConfig().get(CONFIG_MAINFRAME_HEIGHT, 600);
    setSize(width, height);
    setJMenuBar(createJMenuBar());
    setLayout(new BorderLayout());

    JPanel tree = new JPanel();
    JPanel treeTop = new JPanel();
    tree.setLayout(new BorderLayout());
    treeTop.setLayout(new BorderLayout());
    
    // Filter
    treeTop.add(new JLabel("Filter: "), BorderLayout.WEST);
    txtFilter = new JTextField();
    txtFilter.addKeyListener(new KeyAdapter() 
    {
      public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if(Character.isDigit(c) || Character.isLetter(c))
          Workspace.getInstance().updateTree(txtFilter.getText() + c);        
        else
          Workspace.getInstance().updateTree(txtFilter.getText());
      }
    });
    treeTop.add(txtFilter, BorderLayout.CENTER);    
    tree.add(treeTop, BorderLayout.NORTH);
    tree.add(new JScrollPane(workspace), BorderLayout.CENTER);   

    // JSplitpane erstellen 
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, tabbedPane);
    splitPane.setOneTouchExpandable(true);    
    splitPane.setDividerLocation(250);
    
    getContentPane().add(splitPane);
    setVisible(true);
  }

  /** Erstellt die Menubar des MainFrames */
  public JMenuBar createJMenuBar()
  {
    JMenuBar menuBar = new JMenuBar();
    JMenu mnuFile = new JMenu("Datei");
    JMenu mnuInfo = new JMenu("?");

    menuBar.add(mnuFile);
    menuBar.add(mnuInfo);

    // Filemenue
    mnuItemOpen = new JMenuItem("Arbeitsbereich wechseln...");
    mnuItemPrint = new JMenuItem("Drucken...");
    mnuItemExit = new JMenuItem("Beenden");

    mnuFile.add(mnuItemOpen);
    mnuFile.addSeparator();
    mnuFile.add(mnuItemPrint);
    mnuFile.addSeparator();
    mnuFile.add(mnuItemExit);

    // Infomenue
    mnuItemInfo = new JMenuItem("Info...");
    mnuInfo.add(mnuItemInfo);

    // Fuege ActionListener hinzu
    mnuItemOpen.addActionListener(this);
    mnuItemPrint.addActionListener(this);
    mnuItemExit.addActionListener(this);
    mnuItemInfo.addActionListener(this);
    
    // Icons laden
    mnuItemOpen.setIcon(Resource.getImage("resource/gfx/icons/contact-new_16_16.png"));
    mnuItemPrint.setIcon(Resource.getImage("resource/gfx/icons/document-print-preview_16_16.png"));
    mnuItemExit.setIcon(Resource.getImage("resource/gfx/icons/process-stop_16_16.png"));
    
    return menuBar;
  }

  /**
   * Useraktionen
   */
  public void actionPerformed(ActionEvent e)
  {
    if(e.getSource().equals(mnuItemOpen))
    {
      Main.showStartFrame();
      setVisible(false);
    }
    else if (e.getSource().equals(mnuItemPrint))
    {
      PrintPreview printPreviewDialog = new PrintPreview(workspace);
      if (printPreviewDialog.createPreview() == 0 )
      {
        JOptionPane.showMessageDialog(printPreviewDialog, "Keine Studenten vorhanden!", "Hinweis", JOptionPane.INFORMATION_MESSAGE);  
      }
      else
        printPreviewDialog.setVisible(true);
    } 
    else if (e.getSource().equals(mnuItemExit))
    {
      System.exit(0);
    } 
    else if (e.getSource().equals(mnuItemInfo))
    {
      String[] msg = { Main.VERSION, " ", "PrakMan wurde erstellt von:", " ",
          "Andreas Depping \t<andreas.depping@fh-osnabrueck.de>",
          "Christian Lins \t<christian.lins@fh-osnabrueck.de>",
          "Kai Ritterbusch \t<kai.ritterbusch@fh-osnabrueck.de>",
          "Philipp Rollwage \t<philipp.rollwage@fh-osnabrueck.de>" };
      JOptionPane.showMessageDialog(this, msg, "PrakMan",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * @return TabbedPane zurueckgeben
   */
  public TabbedPane getTabbedPane()
  {
    return this.tabbedPane;
  }
}
