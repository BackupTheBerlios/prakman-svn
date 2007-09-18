package prakman.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.*;

/**
 * Hovanes Gambaryan
 * Henry Demirchian
 * CSUN, CS 585
 * Professor Mike Barnes
 * December 06, 2000
 *
 * DateChooser class is a general GUI based date chooser. It allows
 * the user to select an instance of GregorianCalendar defined in
 * java.util package.
 *
 * Programming API is similar to JFC's JColorChooser or JFileChooser. 
 * This class can be used in any application to enable the user to 
 * select a date from a visually displayed calendar.
 *
 * There is a lot of improvements that can be done over this class
 * in areas of functionality, usability, and appearance. But as is,
 * the class can be easily used from within any Java program.
 *
 * Typical usage is like:
 *
 *  // initial date
 *  GregorianCalendar date = new GregorianCalendar()
 *
 *      // The  owner is the JFrame of the application ("AppClass.this")
 *
 *  // show the date chooser
 *  DateChooser dc = new DateChooser(owner, date);
 *
 *  // user can eiter choose a date or cancel by closing
 *  if (dc.showDateChooser() == DateChooser.OK_OPTION) {
 *    date = dc.getDate();
 *  }
 */
 
public class DateChooser 
  extends JDialog 
  implements ActionListener, WindowListener
{
  private static final long serialVersionUID = 0;
  public static final int OK_OPTION = 1;
  public static final int CANCEL_OPTION = 2;

  private static final ArrayList<String> monthNames;
  static {
    monthNames = new ArrayList<String>(12);
    monthNames.add("Januar");
    monthNames.add("Februar");
    monthNames.add("M\u00E4rz");
    monthNames.add("April");
    monthNames.add("Mai");
    monthNames.add("June");
    monthNames.add("Juli");
    monthNames.add("August");
    monthNames.add("September");
    monthNames.add("Oktober ");
    monthNames.add("November");
    monthNames.add("Dezember");
  };

  private GregorianCalendar date;
  private JLabel monthLabel;
  private JLabel yearLabel;
  private JPanel dayGrid;
  private boolean ready;
  // Zeit
  private JTextField txtTime = new JTextField(5);

  /**
   * Constructor for DateChooser
   *
   * @param owner JFrame instance, owner of DateChooser dialog
   * @param d GregorianCalendar instance that will be the initial date
   *          for this dialog
   */
  public DateChooser (JFrame owner, GregorianCalendar d) 
  {    
    super(owner, "Datum und Uhrzeit w\u00E4hlen", true);    
    System.out.println("DateChooser");
    
    date = d;
    addWindowListener(this);
    MainFrame.getInstance().setEnabled(false);
    
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    JPanel yearPane = new JPanel();
    JPanel monthPane = new JPanel();
    yearPane.setLayout(new BoxLayout(yearPane, BoxLayout.X_AXIS));
    monthPane.setLayout(new BoxLayout(monthPane, BoxLayout.X_AXIS));

    JButton[] navButton = new JButton[4];

    // build the panel with month name and navigation buttons
    monthPane.add(navButton[0] = new JButton("<"));
    monthPane.add(monthLabel =
        new JLabel(String.valueOf(monthNames.get(
        date.get(GregorianCalendar.MONTH))), JLabel.CENTER));
    monthLabel.setMinimumSize(new Dimension(80, 17));
    monthLabel.setMaximumSize(new Dimension(80, 17));
    monthLabel.setPreferredSize(new Dimension(80, 17));
    monthPane.add(navButton[1] = new JButton(">"));

    // build the panel with year and navigation buttons
    yearPane.add(navButton[2] = new JButton("<<"));
    yearPane.add(yearLabel =
        new JLabel(String.valueOf(
        date.get(GregorianCalendar.YEAR)),
        JLabel.CENTER), BorderLayout.CENTER);
    yearLabel.setMinimumSize(new Dimension(50, 17));
    yearLabel.setMaximumSize(new Dimension(50, 17));
    yearLabel.setPreferredSize(new Dimension(50, 17));
    yearPane.add(navButton[3] = new JButton(">>"));

    // register a listener on the navigation buttons
    for (int i=0; i<4; i++) {
      navButton[i].addActionListener(this);
    }

    // set the tool tip text on the navigation buttons
    navButton[0].setToolTipText("Vorheriger Monat");
    navButton[1].setToolTipText("N\u00E4chster Monat");
    navButton[2].setToolTipText("Vorheriges Jahr");
    navButton[3].setToolTipText("N\u00E4chstes Jahr");

    // put the panel for months and years together and add some formatting
    JPanel topPane = new JPanel();
    topPane.setLayout(new BoxLayout(topPane, BoxLayout.X_AXIS));
    topPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
    topPane.add(monthPane);
    topPane.add(Box.createRigidArea(new Dimension(20, 0)));
    topPane.add(yearPane);

    // create the panel that will hold the days of the months
    dayGrid = new JPanel(new GridLayout(7, 7));
    updateDayGrid();

    // ZeitPanel
    JPanel timePanel = new JPanel(new FlowLayout());
    timePanel.add(new JLabel("Uhrzeit (HH:MM): "));
    timePanel.add(txtTime);
    txtTime.setText("00:00");
    
    contentPane.add(topPane, BorderLayout.NORTH);
    contentPane.add(dayGrid, BorderLayout.CENTER);
    contentPane.add(timePanel, BorderLayout.SOUTH);
    setResizable(false);
    ready = false;
    pack();
    
    // center this dialog over the owner
    int xPos = (int) (owner.getLocation().getX() + 
      (owner.getWidth() / 2) - (getWidth() / 2));
    int yPos =  (int) (owner.getLocation().getY() +
      (owner.getHeight() / 2) - (getHeight() / 2));
    setLocation(xPos, yPos);
  }


  /**
   * Return the last selected date for this instance of DateChooser
   */
  public GregorianCalendar getDate () 
  {
    return date;
  }


  /**
   * Displays a DateChooser dialog on the screen. If a new date is selected
   * returnsor OK_OPTION. If the action is canceled returns CANCEL_OPTION.
   * Both of the returned values are defined as static constants.
   */
  public int showDateChooser () {
    ready = false;
    setVisible(true);
    
    
    if (ready) {
      return(OK_OPTION);
    }
    else {
      return(CANCEL_OPTION);
    }
  }

  /** Gibt die Zeit zurueck */
  public String[] getTime()
  {    
    String[] hour = null;
    try
    {
      hour = txtTime.getText().split(":");
    }
    catch (Exception e) 
    {            
      JOptionPane.showMessageDialog(null, "Bitte g\u00FCltige Zeit eingeben: Format (hh:mm)");
      hour[0] = "00";
      hour[1] = "00";
    }
    return hour; 
  }

  /**
   * Action handler for this dialog, which handles all the button presses.
   *
   * @param evt ActionEvent
   */
  public void actionPerformed (ActionEvent evt) {
    String label = ((JButton) evt.getSource()).getText();

    if (label.equals("<")) {
      int m = monthNames.indexOf(monthLabel.getText());
      m = prevMonth(m);
      monthLabel.setText((String) monthNames.get(m));
      updateDayGrid();
    }
    else if (label.equals(">")) {
      int m = monthNames.indexOf(monthLabel.getText());
      m = nextMonth(m);
      monthLabel.setText((String) monthNames.get(m));
      updateDayGrid();
    }
    else if (label.equals("<<")) {
      int y = 0;
      try {
        y = Integer.parseInt(yearLabel.getText());
      }
      catch (NumberFormatException e) {
        System.err.println(e.toString());
      }
      yearLabel.setText(String.valueOf(--y));
      updateDayGrid();
    }
    else if (label.equals(">>")) {
      int y = 0;
      try {
        y = Integer.parseInt(yearLabel.getText());
      }
      catch (NumberFormatException e) {
        System.err.println(e.toString());
      }
      yearLabel.setText(String.valueOf(++y));
      updateDayGrid();
    }
    else {
      int m = monthNames.indexOf(monthLabel.getText());
      int y = 0;
      int d = 0;
      try 
      {
        y = Integer.parseInt(yearLabel.getText());
        d = Integer.parseInt(label);
      }
      catch (NumberFormatException e) 
      {
        System.err.println(e.toString());
      }

      try
      {
        date = new GregorianCalendar(y, m, d, Integer.parseInt(getTime()[0]), Integer.parseInt(getTime()[1]));
        ///date.setTimeZone( TimeZone.getTimeZone("CET") );
        date.setLenient(false);
        ready = true;
        dispose();
      }
      catch(NumberFormatException e)
      {
        String[] msg = 
          {"Ung\u00FCltige Zeitangabe!",
            "Bitte w\u00E4hlen Sie eine Zeit im Format HH:MM"};
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.WARNING_MESSAGE);
        txtTime.setText("HH:MM");
      }
    }
  }


  /**
   * This method is used by DateChooser to calculate and display days of
   * the month in correct format for the month currently displayed.
   * Days of the months are displayed as JButtons that the user can select.
   * DateChooser's current day is higlighted in red color.
   */
  private void updateDayGrid () {
    dayGrid.removeAll();

    // get the currently selected month and year
    int m = monthNames.indexOf(monthLabel.getText());
    int y = 0;
    try {
      y = Integer.parseInt(yearLabel.getText());
    }
    catch (NumberFormatException e) {
      System.err.println(e.toString());
    }

    // look at the first day of the month for this month
    GregorianCalendar temp = new GregorianCalendar(y, m, 1);
    temp.setLenient(false);
    int offset = 0;

    // decide what day of the week is the first day of this month
    switch(temp.get(GregorianCalendar.DAY_OF_WEEK)) {
      case GregorianCalendar.MONDAY  : offset = 0; break;
      case GregorianCalendar.TUESDAY   : offset = 1; break;
      case GregorianCalendar.WEDNESDAY : offset = 2; break;
      case GregorianCalendar.THURSDAY  : offset = 3; break;
      case GregorianCalendar.FRIDAY  : offset = 4; break;
      case GregorianCalendar.SATURDAY  : offset = 5; break;
      case GregorianCalendar.SUNDAY  : offset = 6; break;
    }

    // display 7 days of the week across the top
    dayGrid.add(new JLabel("Mon", JLabel.CENTER));
    dayGrid.add(new JLabel("Die", JLabel.CENTER));
    dayGrid.add(new JLabel("Mi", JLabel.CENTER));
    dayGrid.add(new JLabel("Do", JLabel.CENTER));
    dayGrid.add(new JLabel("Fr", JLabel.CENTER));
    dayGrid.add(new JLabel("Sa", JLabel.CENTER));
    dayGrid.add(new JLabel("So", JLabel.CENTER));

    // skip to the correct first day of the week for this month
    for (int i=1; i<=offset; i++) {
      dayGrid.add(new JLabel(""));
    }

    // display days of the month for this month
    JButton day;
    for (int i=1; i<=getLastDay(); i++) {
      dayGrid.add(day = new JButton(String.valueOf(i)));
      day.setToolTipText("Klicken Sie auf einen Tag um ihn zu w\u00E4hlen");
      day.addActionListener(this);

      // show the current day in bright red.
      if (i == date.get(Calendar.DATE) &&
        m == date.get(Calendar.MONTH) &&
        y == date.get(Calendar.YEAR)) {
          day.setForeground(Color.red);
      }
    }

    // display the remaining empty slots to preserve the structure
    for (int i=(offset+getLastDay()+1); i<=42; i++) {
      dayGrid.add(new JLabel(""));
    }

    repaint();
    validate();
  }


  /**
   * Return the month following the one passed in as an argument.
   * If the argument is the las month of the year, return the first month.
   *
   * @param month Current month expressed as an integer (0 to 11).
   */
  private int nextMonth (int month) {
    if (month == 11) {
      return(0);
    }
    return(++month);
  }


  /**
   * Return the month preceding the one passed in as an argument.
   * If the argument is the first month of the year, return the last month.
   *
   * @param month Current month expressed as an integer (0 to 11).
   */
  private int prevMonth (int month) 
  {
    if (month == 0) {
      return(11);
    }
    return(--month);
  }


  /**
   * Return then value of the last day in the currently selected month
   */
  private int getLastDay () {
    int m = (monthNames.indexOf(monthLabel.getText()) + 1);
    int y = 0;
    try {
      y = Integer.parseInt(yearLabel.getText());
    }
    catch (NumberFormatException e) {
      System.err.println(e.toString());
    }

    if ((m==4) || (m==6) || (m==9) || (m==11)) {
      return(30);
    }
    else if (m==2) {
      if (date.isLeapYear(y)) {
        return(29);
      }
      return(28);
    }
    return(31);
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
    
    System.out.print("Schlie\u00DFen");
    MainFrame.getInstance().setEnabled(true);
    setVisible(false);
  }

  public void windowClosed(WindowEvent evt)
  {
    // Fenster WURDE geschlossen (verschwindet vom Bildschirm)
    System.out.print("Schlie\u00DFen");
    MainFrame.getInstance().setEnabled(true);
    setVisible(false);
        
  }
  
  
}
