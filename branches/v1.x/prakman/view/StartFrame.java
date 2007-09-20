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

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;
import prakman.Config;
import prakman.Main;
import prakman.io.Database;
import prakman.io.Resource;
import prakman.model.*;

/**
 * Der StartDialog der Anwendung. Der Benutzer kann hier den
 * Arbeitsbereich (Workspace) und Datenbankeinstellungen
 * auswaehlen.
 */
public class StartFrame
  extends JFrame
  implements ActionListener, ChangeListener
{
  public static final String[] DB_DRIVERS =
    {
   // "org.hsqldb.jdbcDriver",
   // "com.mysql.jdbc.Driver",
    "org.postgresql.Driver"
   // "oracle.jdbc.driver.OracleDriver"
    };
  
  public static final String[] DB_HOSTS =
    {
   // "jdbc:hsqldb:file:",
   // "jdbc:mysql://localhost/",
    "jdbc:postgresql://localhost/"
   // "jdbc:oracle:thin:@131.173.108.224/"
    };
  
  public static final String[] DB_NAMES =
    {
    //  "Hypersonic DB",
    //  "MySQL (defekt)",
      "PostgreSQL"
    //  "Oracle"
    };
  
  private static final long serialVersionUID = 0;
  
  private JButton       btnCancel;
  private JButton       btnChooseDir;
  private JButton       btnOk;
  private JComboBox     cmbWorkspace;
  private JRadioButton  radioExternalDb = new JRadioButton();
  private JRadioButton  radioInternalDb = new JRadioButton();
  private ArrayList<String> lastWorkspaces = new ArrayList<String>();
  private JLabel lblDbType = new JLabel("Typ:");
  private JLabel lblDbHost = new JLabel("Server:");
  private JLabel lblDbName = new JLabel("Datenbank:");
  private JLabel lblDbUser = new JLabel("Benutzer:");
  private JLabel lblDbPwd  = new JLabel("Passwort:");
  private JComboBox   cmbDbType   = new JComboBox(DB_NAMES);
  private JTextField  txtDbHost   = new JTextField(DB_HOSTS[0]);
  private JTextField  txtDbName   = new JTextField(prakman.io.Database.DEFAULT_DATABASE);
  private JTextField  txtDbUser   = new JTextField(prakman.io.Database.DEFAULT_USER);
  private JPasswordField txtDbPwd = new JPasswordField();

  public StartFrame()
  {
    setSize(450, 500);
    setTitle(Main.VERSION);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setIconImage(Resource.getImage("resource/gfx/icons/applications-other-22.png").getImage());
    
    // Workspace-History setzen
    setWorkspaceHistory();
    
    // Erzeuge Layout
    getContentPane().setLayout(new BorderLayout());
    
    JPanel top = new JPanel();
    JLabel lblTop = new JLabel("PrakMan");
    top.setLayout(new BorderLayout());
    top.add(lblTop, BorderLayout.CENTER);
    top.add(new JLabel("<html><body>Bitte w\u00E4hlen Sie ein Arbeitsverzeichnis, in dem alle n\u00F6tigen Daten gespeichert werden.</body></html>"), BorderLayout.SOUTH);
    lblTop.setFont(new Font("Serif", Font.BOLD, 28));
    top.setBackground(Color.WHITE);
    getContentPane().add(top, BorderLayout.NORTH);

    JPanel centerA = new JPanel();
    centerA.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 0.8;
    gbc.weighty = 0.9;
    gbc.insets  = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 1;
    gbc.gridy = 1;    
    centerA.add(new JLabel("Arbeitsverzeichnis:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 2; 
    cmbWorkspace = new JComboBox();
    cmbWorkspace.setEditable(true);
    centerA.add(cmbWorkspace, gbc);
    cmbWorkspace.setPreferredSize(new Dimension(200, 22));
    cmbWorkspace.addActionListener(this);

    gbc.gridx = 2;
    gbc.gridy = 2; 
    btnChooseDir = new JButton("W\u00E4hlen...");
    centerA.add(btnChooseDir, gbc);

    JPanel centerB = new JPanel();
    centerB.setLayout(new GridBagLayout());
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    centerB.add(radioInternalDb, gbc);

    gbc.gridx = 2;
    gbc.anchor = GridBagConstraints.WEST;
    centerB.add(new JLabel("Interne Datenbank verwenden"), gbc);
    
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    centerB.add(radioExternalDb, gbc);

    gbc.gridx = 2;
    gbc.anchor = GridBagConstraints.WEST;
    centerB.add(new JLabel("Benutzerdefinierte Datenbank verwenden:"), gbc);

    // Components fuer die benutzerdefinierte Datenbank einfuegen
  
    gbc.gridx = 1;
    gbc.gridy = 3;
    centerB.add(lblDbType, gbc);
    gbc.gridx = 2;
    centerB.add(cmbDbType, gbc);

    gbc.gridx = 1;
    gbc.gridy = 4;
    centerB.add(lblDbHost, gbc);
    gbc.gridx = 2;
    centerB.add(txtDbHost, gbc);

    gbc.gridx = 1;
    gbc.gridy = 5;
    centerB.add(lblDbName, gbc);
    gbc.gridx = 2;
    centerB.add(txtDbName, gbc);

    gbc.gridx = 1;
    gbc.gridy = 6;
    centerB.add(lblDbUser, gbc);
    gbc.gridx = 2;
    centerB.add(txtDbUser, gbc);

    gbc.gridx = 1;
    gbc.gridy = 7;
    centerB.add(lblDbPwd, gbc);
    gbc.gridx = 2;
    centerB.add(txtDbPwd, gbc);

    JPanel center = new JPanel();
    center.setLayout(new GridBagLayout());
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.gridx = 1;
    gbc.gridy = 1;
    center.add(centerA, gbc);
    gbc.gridy = 2;
    center.add(centerB, gbc);

    getContentPane().add(center, BorderLayout.CENTER);

    JPanel bottom = new JPanel();
    bottom.setLayout(new FlowLayout());
    btnOk     = new JButton("Ok");
    btnCancel = new JButton("Abbrechen");
    bottom.add(btnOk);
    bottom.add(btnCancel);
    getContentPane().add(bottom, BorderLayout.SOUTH);
    
    btnOk.setDefaultCapable(true);

    // EventLister zu Components hinzufuegen
    btnCancel.addActionListener(this);
    btnChooseDir.addActionListener(this);
    btnOk.addActionListener(this);
    radioExternalDb.addChangeListener(this);

    // RadioButtons gruppieren
    ButtonGroup group = new ButtonGroup();
    group.add(radioExternalDb);
    group.add(radioInternalDb);
    
    radioInternalDb.setSelected(true);
    stateChanged(null);
    
    cmbDbType.addActionListener(this);
    
    // Den zuletzt gewaehlten Workspace auswaehlen
    for(String item : lastWorkspaces)
      cmbWorkspace.addItem(item);
    cmbWorkspace.setSelectedItem(Config.getInstance().get(Config.LAST_WORKSPACE_0, ""));
  }

  public void actionPerformed(ActionEvent e)
  {
    if(e.getSource().equals(btnCancel))
    {
      System.exit(0);
    }
    else if(e.getSource().equals(btnChooseDir))
    {
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fc.showOpenDialog(this);
      if(fc.getSelectedFile() != null)
        cmbWorkspace.setSelectedItem(fc.getSelectedFile().getAbsolutePath());
    }
    else if(e.getSource().equals(btnOk))
    {
      btnOk.setEnabled(false);
      String item = (String)cmbWorkspace.getSelectedItem();
      if(item == null || item.equals(""))
      {
        JOptionPane.showMessageDialog(this, "Kein Arbeitsbereich ausgew\u00E4hlt!", "PrakMan", JOptionPane.WARNING_MESSAGE);
        btnOk.setEnabled(true);
        return;
      }
      
      // Workspacehistory setzen 
      if(lastWorkspaces.size()>0)
      {
        if(lastWorkspaces.contains(item))
        {
          lastWorkspaces.remove(item);
          lastWorkspaces.add("");                    
        }
        for(int i = lastWorkspaces.size()-1; i > 0; i--)
          lastWorkspaces.set(i,lastWorkspaces.get(i-1));
        lastWorkspaces.set(0,item);        
        saveWorkspaceHistory();
      }
      
      Workspace workspace = new Workspace(new java.io.File(item));
      
      // Benutzerdefinierte Einstellungen setzen
      if(radioExternalDb.isSelected())
      {
        Config conf = workspace.getConfig();
        conf.set(Database.CONFIG_DRIVER, DB_DRIVERS[cmbDbType.getSelectedIndex()]);
        conf.set(Database.CONFIG_DATABASE, txtDbName.getText());
        conf.set(Database.CONFIG_HOST, txtDbHost.getText());
        conf.set(Database.CONFIG_PASSWORD, new String(txtDbPwd.getPassword()));
        conf.set(Database.CONFIG_USER, txtDbUser.getText());
        conf.set(Config.USE_EXTERNAL_DB, "true");
      }
      
      try
      {
        workspace.init();
      }
      catch(Exception ex)
      {
        String[] msg = {
            "Beim Initialisieren des Arbeitsbereichs ist ein Fehler aufgetreten:",
            ex.getMessage()};
      
        JOptionPane.showMessageDialog(null, msg, Main.VERSION, JOptionPane.ERROR_MESSAGE);
        btnOk.setEnabled(true);
        return;
      }
      
      new MainFrame(workspace);
      setVisible(false);
    }
    else if(e.getSource().equals(cmbDbType))  
    { // Eine andere benutzerdefinierte Datenbank wurde gewaehlt
      txtDbHost.setText(DB_HOSTS[cmbDbType.getSelectedIndex()]);
    }
    else if(e.getSource().equals(cmbWorkspace))
    { // Falls der Workspace schon existiert muessen wir versuchen
      // die Workspace-Config zu laden und die entsprechenden
      // Datenbank-Einstellungen restaurieren
      try
      {
        Config cfg = new Config();
        cfg.load((String)cmbWorkspace.getSelectedItem() + "/config.conf");
        if(cfg.get(Config.USE_EXTERNAL_DB, "false").equals("true"))
        {
          radioExternalDb.setSelected(true);
          txtDbHost.setText(cfg.get(Database.CONFIG_HOST, Database.DEFAULT_HOST));
          txtDbName.setText(cfg.get(Database.CONFIG_DATABASE, Database.DEFAULT_DATABASE));
          txtDbPwd.setText(cfg.get(Database.CONFIG_PASSWORD, Database.DEFAULT_PASSWORD));
          txtDbUser.setText(cfg.get(Database.CONFIG_USER, Database.DEFAULT_PASSWORD));
        }
      }
      catch(Exception ex)
      {
        System.out.println("Laden der Workspace-Config fehlgeschlagen!");
        radioInternalDb.setSelected(true);
      }
    }
  }

  /**
   * Radiobutton status setzen
   */
  public void stateChanged(ChangeEvent e)
  {
    if(radioExternalDb.isSelected())
    {
      cmbDbType.setEnabled(true);
      txtDbHost.setEnabled(true);
      txtDbName.setEnabled(true);
      txtDbUser.setEnabled(true);
      txtDbPwd.setEnabled(true);
    }
    else
    {
      cmbDbType.setEnabled(false);
      txtDbHost.setEnabled(false);
      txtDbName.setEnabled(false);
      txtDbUser.setEnabled(false);
      txtDbPwd.setEnabled(false);
    }
  }

  /**
   * Sichtbarkeit einstellen
   */
  public void setVisible(boolean state)
  {
    // Zentriere das Fenster
    int x = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - getWidth() / 2);
    int y = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/ 2 - getHeight()/ 2);

    setLocation(x, y);  

    super.setVisible(state);
  }
  
  /**
   * die zuletzt geoeffneten Workspaces setzen
   */
  private void setWorkspaceHistory()
  {    
    lastWorkspaces.add(Config.getInstance().get(Config.LAST_WORKSPACE_0,""));
    lastWorkspaces.add(Config.getInstance().get(Config.LAST_WORKSPACE_1,""));
    lastWorkspaces.add(Config.getInstance().get(Config.LAST_WORKSPACE_2,""));
    lastWorkspaces.add(Config.getInstance().get(Config.LAST_WORKSPACE_3,""));
    lastWorkspaces.add(Config.getInstance().get(Config.LAST_WORKSPACE_4,""));
  }
  
  /**
   * die zuletzt geoeffneten Workspaces speichern
   */
  private void saveWorkspaceHistory()
  {    
    Config.getInstance().set(Config.LAST_WORKSPACE_0,lastWorkspaces.get(0));
    Config.getInstance().set(Config.LAST_WORKSPACE_1,lastWorkspaces.get(1));
    Config.getInstance().set(Config.LAST_WORKSPACE_2,lastWorkspaces.get(2));
    Config.getInstance().set(Config.LAST_WORKSPACE_3,lastWorkspaces.get(3));
    Config.getInstance().set(Config.LAST_WORKSPACE_4,lastWorkspaces.get(4));
  }
}
