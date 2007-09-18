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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import prakman.model.TableModel;
import prakman.model.Workspace;
import prakman.model.Student;
import prakman.view.object.EventPanel;
import prakman.io.Database;
import prakman.io.Resource;

/**
 * 
 * Stellt die Projektzurordnung von Studenten dar
 *
 */
public class ProjectFrame extends BaseFrame 
  implements ActionListener, WindowListener
{
  private static final long serialVersionUID = 0;
  
  // StudentenTabele
  private JLabel      tableOverDesc = new JLabel("Studenten");
  private Table       table;
  private JScrollPane sPane;
  
  // StundentenButtons
  private JButton     add;
  private JButton     rem;
  
  // Beschreibung
  private JLabel      lblDesc   = new JLabel("Beschreibung");
  private JTextField  prjDesc;
  
  // Projekt Beginn Datum
  private JLabel      lblBeginTerm  = new JLabel("Start-Termin");
  private JTextField  prjBegin;
  private JButton     prjBeginButton;
  
  // Projekt Ende Datum
  private JLabel      lblEndTerm    = new JLabel("Abgabe-Termin");
  private JTextField  prjEnd;
  private JButton     prjEndButton;
  private JPanel ref;  
  private TableModel prakTm;
  // Save Undo Buttons
  private JButton     btnSave;
  private JButton     btnUndo;
  private int         eventID;
  private int         projectID;
  // OriginalWerte
  private String      orgDesc;
  private Timestamp   orgBegin;
  private Timestamp   orgEnd;
  // Neue Werte
  private Timestamp   newBegin;
  private Timestamp   newEnd;

  public ProjectFrame(int _projectID, int _eventID, JPanel reference)
  {
    super("Projekt Editor");
    this.ref = reference;
    addWindowListener(this);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    MainFrame.getInstance().setEnabled(false);
    
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridBagLayout());
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new FlowLayout());
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new FlowLayout());
    
    eventID   = _eventID;
    projectID = _projectID;
    
    GridBagConstraints gbc = new GridBagConstraints();
    this.setLayout(new BorderLayout());
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.insets = new Insets(4, 4, 4, 4);
    
    // -------------------Buttons Save und Undo------------------
    btnSave = new JButton(Resource.getImage("resource/gfx/icons/document-save_16_16.png"));
    btnSave.addActionListener(this);
    gbc.gridx = 0;
    gbc.gridy = 0;
    topPanel.add(btnSave, gbc);
    
    btnUndo = new JButton(Resource.getImage("resource/gfx/icons/edit-undo_16_16.png"));
    btnUndo.addActionListener(this);
    gbc.gridx = 1;
    gbc.gridy = 0;
    topPanel.add(btnUndo, gbc);
    
    // -------------------Beschreibung------------------
    gbc.gridx = 0;
    gbc.gridy = 1;
    centerPanel.add(lblDesc, gbc);
    prjDesc = new JTextField();
    prjDesc.setColumns(41);
    gbc.gridx = 1;
    gbc.gridy = 1;
    centerPanel.add(prjDesc, gbc);
    
    // -------------------StartTermin------------------
    gbc.gridx = 0;
    gbc.gridy = 2;
    centerPanel.add(lblBeginTerm, gbc);
    prjBegin = new JTextField();
    prjBegin.setColumns(41);
    prjBegin.setEditable(false);
    gbc.gridx = 1;
    gbc.gridy = 2;
    centerPanel.add(prjBegin, gbc);
    prjBeginButton = new JButton(Resource.getImage("resource/gfx/icons/appointment-new_16_16.png"));
    prjBeginButton.addActionListener(this);
    gbc.gridx = 2;
    gbc.gridy = 2;
    centerPanel.add(prjBeginButton, gbc);
    
    // -------------------EndTermin------------------
    gbc.gridx = 0;
    gbc.gridy = 3;
    centerPanel.add(lblEndTerm, gbc);
    prjEnd = new JTextField();
    prjEnd.setColumns(41);
    prjEnd.setEditable(false);
    gbc.gridx = 1;
    gbc.gridy = 3;
    centerPanel.add(prjEnd, gbc);
    prjEndButton = new JButton(Resource.getImage("resource/gfx/icons/appointment-new_16_16.png"));
    prjEndButton.addActionListener(this);
    gbc.gridx = 2;
    gbc.gridy = 3;
    centerPanel.add(prjEndButton, gbc);
    
    // -------------------StudentenTabelle------------------
    gbc.gridx = 0;
    gbc.gridy = 4;
    centerPanel.add(tableOverDesc, gbc);
    table = new Table();
    // table.setMaximumSize(new Dimension(200,50));
    sPane = new JScrollPane(table);
    gbc.gridx = 1;
    gbc.gridy = 4;
    centerPanel.add(sPane, gbc);
    
    // -------------------StudentenButtons------------------
    add = new JButton(Resource
        .getImage("resource/gfx/icons/list-add_16_16.png"));
    add.addActionListener(this);
    gbc.gridx = 0;
    gbc.gridy = 5;
    bottomPanel.add(add, gbc);
    rem = new JButton(Resource
        .getImage("resource/gfx/icons/list-remove_16_16.png"));
    rem.addActionListener(this);
    gbc.gridx = 1;
    gbc.gridy = 5;
    
    bottomPanel.add(rem, gbc);
    
    // Panels zusammenfuegen
    add(topPanel, BorderLayout.NORTH);
    add(centerPanel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
    
    getProject();
    pack();
    setVisible(true);
  }

  /**
   * Aktualsiert die Tableansicht
   */
  private void getProject()
  {
    try
    {
      Database db = Workspace.getInstance().getDatabase();
      if (!db.getProjectDesc(projectID).equals(""))
      {
        orgDesc = db.getProjectDesc(projectID);
        prjDesc.setText(orgDesc);
      }
      if ((db.getProjectDate(projectID)) != null)
      {
        orgBegin = (db.getProjectDate(projectID));
        prjBegin.setText(orgBegin.toString());
      }
      if ((db.getProjectDeadline(projectID)) != null)
      {
        orgEnd = db.getProjectDeadline(projectID);
        prjEnd.setText(orgEnd.toString());
      }
      ArrayList<ArrayList<Object>> memberData = new ArrayList<ArrayList<Object>>();
      // Teilnehmer aus der Datenbank holen
      ArrayList<Student> studentData = new ArrayList<Student>();
      Integer[] stu = db.getStudentsInProject(projectID);
      for (int i = 0; i < stu.length; i++)
      {
        studentData.add(db.getStudent(stu[i]));
      }
      // Daten in ausgabefaehiges Format umkopieren
      for (Student std : studentData)
      {
        ArrayList<Object> subData = new ArrayList<Object>();
        subData.add(false);
        subData.add(std.getMatNr());
        subData.add(std.getLastName());
        subData.add(std.getFirstName());
        String str = Workspace.getInstance().getDatabase().getMark(std.getMatNr(), projectID );        
        subData.add(str);
        memberData.add(subData);
      }
      String[] colNames = { "#", "Matr-Nr.", "Nachname", "Vorname", "Note" };
      prakTm = new TableModel(colNames, memberData);
      table.refresh(prakTm);
      table.addMouseListener(new MouseAdapter() 
      {
        // Wichtig um Panel mit Studenten zu oeffnen
        public void mouseClicked(MouseEvent e)
          {
            if(e.getClickCount() > 1)
            {
              try
              {
                String mark = JOptionPane.showInputDialog(table, "Bitte eine Note eingeben");                
                if(mark != null)
                {
                  Workspace.getInstance().getDatabase().setMark(
                    Integer.parseInt(table.getValueAt(table.rowAtPoint(e.getPoint()),1 ).toString()),
                    projectID,
                    mark
                    );                
                  refreshStudents();
                }
              }
              catch (Exception err) 
              {
                err.printStackTrace();
              }
            }
          }
        });
      
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
  }
  
 /** 
 * Studentenobjekte aktualisieren
 */
  private void refreshStudents()
  {
    try
    {
      Database db = Workspace.getInstance().getDatabase();
      ArrayList<ArrayList<Object>> memberData = new ArrayList<ArrayList<Object>>();
      // Teilnehmer aus der Datenbank holen
      ArrayList<Student> studentData = new ArrayList<Student>();
      Integer[] stu = db.getStudentsInProject(projectID);
      for (int i = 0; i < stu.length; i++)
      {
        studentData.add(db.getStudent(stu[i]));
      }
      // Daten in ausgabefaehiges Format umkopieren
      for (Student std : studentData)
      {
        ArrayList<Object> subData = new ArrayList<Object>();
        subData.add(false);
        subData.add(std.getMatNr());
        subData.add(std.getLastName());
        subData.add(std.getFirstName());
        String str = Workspace.getInstance().getDatabase().getMark(std.getMatNr(), projectID );        
        subData.add(str);
        memberData.add(subData);
      }
      String[] colNames = { "#", "Matr-Nr.", "Nachname", "Vorname", "Note" };
      TableModel prakTm = new TableModel(colNames, memberData);
      table.refresh(prakTm);
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource().equals(add))
    {
      try
      {
        String[] columnNames = {"#", "Name", "Matrikelnummer"};
        
        ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
        ArrayList<Student> stds = Workspace.getInstance().getDatabase().getUnassignedStudentsInEvent(eventID, projectID);
                
        for(Student std : stds)
        {
          ArrayList<Object> row = new ArrayList<Object>();
          row.add(new Boolean(false));
          row.add(std.toString());
          row.add(std.getMatNr());
          
          data.add(row);
        } 
        
        TableModel model = new TableModel(columnNames, data);
        ListFrame stdListFrame = new ListFrame(model, "Studenten der Veranstaltung", this);
        stdListFrame.setVisible(true);
        refreshStudents();
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
    }
    else if (e.getSource().equals(rem)) // Student entfernen
    {
      System.out.println("rem");
      // Studenten auswaehlen----------------------------------
      Table students = table;
      boolean del = false;
      try
      {
        for (int i = 0; i < students.getRowCount(); i++)
        {
          if ((Boolean) (students.getValueAt(i, 0)) == true) 
          {            
            del = true;            
            Workspace.getInstance().getDatabase().removeStudentFromProject(
                (Integer) students.getValueAt(i, 1), projectID);
          }
        }
        if (del)
        {
          // Table refreshen
          this.refreshStudents();
          JOptionPane.showMessageDialog(this,
              "Student erfolgreich gel\u00F6scht");
        }
      }
      catch (SQLException sqlEx)
      {
        sqlEx.printStackTrace();
      }
    }
    else if (e.getSource().equals(prjBeginButton)) // Datum Beginn aendern
    {
      DateChooser date = new DateChooser(this, new GregorianCalendar());
      if (date.showDateChooser() == DateChooser.OK_OPTION)
      {
        Timestamp stamp = new Timestamp(date.getDate().getTimeInMillis());
        newBegin = stamp;
        prjBegin.setText(newBegin.toString());
      }
    }
    else if (e.getSource().equals(prjEndButton))// Datum Ende
    // Aendern------------------
    {
      DateChooser date = new DateChooser(this, new GregorianCalendar());
      if (date.showDateChooser() == DateChooser.OK_OPTION)
      {
        Timestamp stamp = new Timestamp(date.getDate().getTimeInMillis());
        newEnd = stamp;
        prjEnd.setText(newEnd.toString());
      }
    }
    else if (e.getSource().equals(btnSave))// Speichern
    {
      try
      {
        Database db = Workspace.getInstance().getDatabase();
        if (prjDesc.getText().equals(""))
          prjDesc.setText(" ");
        if (newBegin == null)
          newBegin = new Timestamp(orgBegin.getTime());
        if (newEnd == null)
        {
          if(orgEnd == null)
            orgEnd = new Timestamp(Long.MAX_VALUE);
          newEnd = new Timestamp(orgEnd.getTime()); 
        }
        db.updateProject(projectID, prjDesc.getText(), newBegin, newEnd);
        // Tabelle auf EventPanel aktualisieren
        ((EventPanel)ref).refreshProjectsTable();
      }
      catch (SQLException ex)
      {
        ex.printStackTrace();
      }
      JOptionPane.showMessageDialog(this, "\u00C4nderungen gespeichert");
    }
    else if (e.getSource().equals(btnUndo)) // Aenderungen verwerfen
    {
      JOptionPane.showMessageDialog(this, "\u00C4nderungen gel\u00F6scht");
      this.getProject();
    }
  }

  public void windowActivated(WindowEvent evt)
  {
    // Fenster ist aktiviert worden
    // (d.h. Eingaben/Mousebewegungen betreffen dieses Fenster)
  }

  public void windowDeactivated(WindowEvent evt)
  {
    // Fenster ist deaktiviert worden
    // (d.h. Eingaben/Mousebewegungen betreffen anderes Fenster)
  }

  public void windowIconified(WindowEvent evt)
  {
    // Fenster wurde zum Icon
  }

  public void windowDeiconified(WindowEvent evt)
  {
    // Fenster wurde vom Icon in die Normalgroesse geaendert
  }

  public void windowOpened(WindowEvent evt)
  {
    // Fenster wurde geoeffnet (erschien am Bildschirm)
  }

  public void windowClosing(WindowEvent evt)
  {
    // Fenster SOLL geschlossen werden (soll vom Bildschirm verschwinden)
  }

  public void windowClosed(WindowEvent evt)
  {
    // Fenster WURDE geschlossen (verschwindet vom Bildschirm)
    setVisible(false);
    MainFrame.getInstance().setEnabled(true);
  }
  
  public void addStudent(Student std)
  {
    try
    {
      Workspace.getInstance().getDatabase().addStudentToProject(std, projectID);
      refreshStudents();
    }
    catch(SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public ArrayList<ArrayList<Object> > getMemberData()
  {
    ArrayList<ArrayList<Object>> memberData = null;
    try
    {
      Database db = Workspace.getInstance().getDatabase();
      memberData = new ArrayList<ArrayList<Object>>();
      // Teilnehmer aus der Datenbank holen
      ArrayList<Student> studentData = new ArrayList<Student>();
      Integer[] stu = db.getStudentsInProject(projectID);
      for (int i = 0; i < stu.length; i++)
      {
        studentData.add(db.getStudent(stu[i]));
      }
      // Daten in ausgabefaehiges Format umkopieren
      for (Student std : studentData)
      {
        ArrayList<Object> subData = new ArrayList<Object>();
        subData.add(false);
        subData.add(std.getMatNr());
        subData.add(std.getLastName());
        subData.add(std.getFirstName());
        memberData.add(subData);
      }
    }
    catch (Exception e) 
    {
      e.printStackTrace();
    }
    return memberData;
  }
}
