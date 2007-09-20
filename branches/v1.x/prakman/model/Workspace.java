package prakman.model;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import prakman.Config;
import prakman.io.Database;
import prakman.io.Resource;
import prakman.view.MainFrame;
import prakman.view.menu.TreeContextMenu;

/**
 * Repraesentiert den Arbeitsbereich und ist gleichzeitig
 * der sichtbare Baum (JTree).
 */
public class Workspace extends JTree implements TreeModelListener
{
  // Ueberschriften
  public static final String STUDENT          = "Studenten";
  public static final String TUTOR            = "Lehrende";
  public static final String EVENT            = "Veranstaltungen";
  private static final long  serialVersionUID = 0;
  public static Workspace    instance;

  public static Workspace getInstance()
  {
    return instance;
  }

  private String                 strRoot = "FH-Osnabr\u00FCck";
  private DefaultMutableTreeNode root;
  private DefaultMutableTreeNode event;
  private DefaultMutableTreeNode tutor;
  private DefaultMutableTreeNode student;
  private Config                 config  = new Config();
  private File                   path;
  private Database               db;

  public Workspace(File path)
  {
    // Instanz erstellen
    instance = this;
    // Verzeichnis ueberpruefen
    this.path = path;
   
    if (!path.exists())
      path.mkdirs(); // Erstelle das Verzeichnis
    strRoot = this.path.getName();
    // Baum erstellen
    createTree();
    // TreeModelListener hinzufuegen, sonst funktioniert die Aktualisierung
    // nicht
    getModel().addTreeModelListener(this);

    // Selection-Listener
    this.addTreeSelectionListener(new TreeSelectionListener()
    {
      public void valueChanged(TreeSelectionEvent event)
      {
        TreePath tp = event.getNewLeadSelectionPath();
        if (tp != null)
        {
          System.out.println("  Selektiert: " + tp.toString());
        }
      }
    });
    // MouseListener
    this.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        // MouseModifiers = 4 => rechte Maustaste
        if ((e.getModifiers() == 4)
            && (getRowForLocation(e.getX(), e.getY()) != -1))
        {
          setSelectionRow(getRowForLocation(e.getX(), e.getY())); 
          // Ruft das Kontextmenue auf
          TreeContextMenu menu = TreeContextMenu.getInstance(getLastSelectedPathComponent());
          menu.show(e.getComponent(), e.getX(), e.getY()); 
        }
        if ((e.getClickCount() > 1)
            && (getLastSelectedPathComponent() instanceof Student))
        {
          // Bearbeiten bei Doppelklick
          MainFrame.getInstance().getTabbedPane().addTab(
              (getLastSelectedPathComponent()));
        }
        if ((e.getClickCount() > 1)
            && (getLastSelectedPathComponent() instanceof prakman.model.Event))
        {
          // Bearbeiten bei Doppelklick
          MainFrame.getInstance().getTabbedPane().addTab(
              getLastSelectedPathComponent());
        }
        if ((e.getClickCount() > 1)
            && (getLastSelectedPathComponent() instanceof Tutor))
        {
          // Bearbeiten bei Doppelklick
          MainFrame.getInstance().getTabbedPane().addTab(
              getLastSelectedPathComponent());
        }
      }
    });
  }

  /**
   * Diesen Workspace initialisieren.
   * @throws Exception
   */
  public void init()
    throws Exception
  {
    try
    {
      // Config laden
      config.load(path.getAbsolutePath() + "/config.conf");
    }
    catch(Exception e)
    {
      System.out.println("Keine oder korrupte Config! Wird neu angelegt...");
    }
    
    // Datenbank erstellen
    db = new Database(this);
    db.connect();
    db.checkTables();
    
    // Testen ob CrashTest erwuenscht
    int ct = config.get(Config.CRASHTEST, 0);
    if(ct > 0)
      prakman.Main.crashTest(this, ct);
    
    updateTree("");
    //expandPath(new TreePath());
  }
  
  /**
   * @return Die Config dieses Workspaces.
   */
  public Config getConfig()
  {
    return this.config;
  }

  /**
   * @return Die Datenbank des Workspaces.
   */
  public Database getDatabase()
  {
    return this.db;
  }

  /** Getter Root */
  public String getRoot()
  {
    return strRoot;
  }

  /** Eintrag hinzufuegen */
  public void addNode(Object node)
  {
    if (node instanceof Student)
      student.add(((Student) node));
    else if (node instanceof Tutor)
      tutor.add((Tutor) node);
    else if (node instanceof prakman.model.Event)
      event.add(new DefaultMutableTreeNode((prakman.model.Event) node));
  }

  /** Ganze Liste hinzufuegen */
  public void addList(Object[] arr)
  {
    if (arr instanceof Student[])
      for (Object std : arr)
        student.add((Student) std);
    else if (arr instanceof Tutor[])
      for (Object tut : arr)
        tutor.add((Tutor) tut);
    else if (arr instanceof prakman.model.Event[])
      for (Object evt : arr)
        event.add((prakman.model.Event) evt);
    else
      System.err.println("Falscher Typ");
  }

  /** Tree loeschen */
  public void clearTree()
  {
    student.removeAllChildren();
    tutor.removeAllChildren();
    event.removeAllChildren();
  }

  /** Einen Standardbaum erstellen */
  public void createTree()
  {
    root    = new DefaultMutableTreeNode(getRoot());
    event   = new DefaultMutableTreeNode(EVENT);
    tutor   = new DefaultMutableTreeNode(TUTOR);
    student = new DefaultMutableTreeNode(STUDENT);
    root.add(event);
    root.add(tutor);
    root.add(student);
    DefaultTreeModel tree = new DefaultTreeModel(root);
    ImageIcon iconStudent = Resource
        .getImage("resource/gfx/icons/tab-new_16_16.png");
    ImageIcon iconOpen = Resource
        .getImage("resource/gfx/icons/list-remove_16_16.png");
    ImageIcon iconClosed = Resource
        .getImage("resource/gfx/icons/list-add_16_16.png");
    DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
    rend.setOpenIcon(iconOpen);
    rend.setClosedIcon(iconClosed);
    rend.setLeafIcon(iconStudent);
    setCellRenderer(rend);
    this.setModel(tree);
  }

  /** Aktualisiert die Baumansicht */
  public void updateTree(String strFilter)
  {
    clearTree();
    try
    {
      addList(db.getFilteredEvents(strFilter).toArray(new Event[0]));
      addList(db.getFilteredStudents(strFilter).toArray(new Student[0]));
      addList(db.getFilteredTutors(strFilter).toArray(new Tutor[0]));      
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    // ((DefaultTreeModel)
    // getModel()).reload((DefaultMutableTreeNode)getModel().getRoot());
    ((DefaultTreeModel) getModel()).reload(student);
    ((DefaultTreeModel) getModel()).reload(event);
    ((DefaultTreeModel) getModel()).reload(tutor);
  }

  /** Events */
  public void treeNodesChanged(TreeModelEvent e)
  {
    // ((DefaultTreeModel)
    // getModel()).reload((DefaultMutableTreeNode)e.getSource());
    System.out.println("geaendert");
  }

  
  public void treeNodesInserted(TreeModelEvent e)
  {
  }

  public void treeNodesRemoved(TreeModelEvent e)
  {
  }

  public void treeStructureChanged(TreeModelEvent e)
  {
  }

  /**
   * Sucht einen Studenten ueber seine ID (Matrikelnummer).
   * Diese Methode wird noch nicht konsequent ueberall verwendet.
   * @param ID
   * @return
   */
  public Student findStudentByID(int ID)
  {
    for (Enumeration e = student.children(); e.hasMoreElements();)
    {
      Student nextStud = (Student) e.nextElement();
      if (nextStud.getMatNr() == ID)
      {
        System.out.println("Student gefunden!");
        return nextStud;
      }
    }
    return null; // Objekt nicht gefunden
  }

  /**
   * Findet einen Studenten ueber seine ID.
   * @param ID
   * @return
   */
  public Tutor findTutorByID(int ID)
  {
    for (Enumeration e = tutor.children(); e.hasMoreElements();)
    {
      Tutor nextTut = (Tutor) e.nextElement();
      if (nextTut.getID() == ID)
      {
        System.out.println("Tutor gefunden!");
        return nextTut;
      }
    }
    return null; // Objekt nicht gefunden
  }

  /**
   * Findet ein Event ueber die ID.
   * @param ID
   * @return
   */
  public Event findEventByID(int ID)
  {
    for (Enumeration e = event.children(); e.hasMoreElements();)
    {
      Event nextEvent = (Event) e.nextElement();
      if (nextEvent.getID() == ID)
      {
        System.out.println("Event gefunden!");
        return (nextEvent);
      }
    }
    return null; // Objekt nicht gefunden
  }
  
  /**
   * Speichert die Config des Workspaces in eine Datei.
   * @throws Exception
   */
  public void saveConfig()
    throws Exception
  {
    File file = new File(
        this.path.getAbsolutePath() + File.separatorChar + "config.conf"); 
    this.config.save(file);
  }
  
  /**
   * Gibt den Pfad des Workspaces zurueck.
   * @return
   */
  public String getPath()
  {
    return this.path.getAbsolutePath();
  }
}
