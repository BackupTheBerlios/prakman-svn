package prakman.io;

import java.io.*;
import java.util.*;

/**
 * Dient zum Laden und Speichern von Properties aus und in eine
 * CSV-Datei.
 * @author CL
 */
public class CSVProperties
  implements Iterable<String[]>
{
  public static final String SEPARATOR        = ",";
  public static final String SEPARATOR_STRING = "\"";

  private ArrayList<String>       columnNames = new ArrayList<String>();
  private ArrayList<String[]>     rows        = new ArrayList<String[]>();

  /**
   * Standardkonstruktor.
   */
  public CSVProperties()
  {
  }
  
  /**
   * Erzeugt ein CSVProperties-Objekt.
   * @param columnNames Spaltennamen
   */
  public CSVProperties(String[] columnNames)
  {
    for(String s : columnNames)
    {
      this.columnNames.add(s);
    }
  }
  
  /**
   * Fuegt eine Zeile zu diesen Properties hinzu.
   * @param row
   * @return
   */
  public boolean add(String[] row)
  {
    if(columnNames.size() != row.length)
      return false;

    return rows.add(row);
  }

  /**
   * Gibt den Wert der angegebenen Zeile und Spalte zurueck.
   * @param n Zeile
   * @param column Spalte
   * @return
   */
  public String get(int n, String column)
  {
    for(int m = 0; m < columnNames.size(); m++)
      if(columnNames.get(n).equals(column))
        return get(n, m);

    return null;
  }

  /**
   * Gibt den Wert der angegebenen Zeile und Spalte zurueck.
   * @param n Zeile
   * @param column Spalte
   * @return
   */
  public String get(int n, int column)
  {
    try
    {
      return get(n)[column];
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Gibt die angegebene Zeile zurueck.
   * @param n
   * @return
   */
  public String[] get(int n)
  {
    return rows.get(n);
  }

  /**
   * Gibt die Spaltenueberschriften zurueck.
   * @return
   */
  public ArrayList<String> getColumnNames()
  {
    return this.columnNames;
  }

  /**
   * Gibt den numerischen Index fuer den angegebenen Spaltennamen zurueck.
   * @param columnName
   * @return
   */
  private int getColumnIndex(String columnName)
  {
    for(int n = 0; n < columnNames.size(); n++)
      if(columnName.equals(columnNames.get(n)))
        return n;
    return -1;
  }

  /**
   * Gibt einen Zeileniterator zurueck. Noetig fuer das Interface
   * Iterable.
   */
  public Iterator<String[]> iterator()
  {
    return this.rows.iterator();
  }

  /**
   * Laedt Daten aus dem angegebenen Dateinamen in diese Properties.
   * @param fileName
   * @throws IOException
   */
  public void load(String fileName)
    throws IOException
  {
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

    String   line    = in.readLine();
    String[] columns = split(line);
    for(String s : columns)
      this.columnNames.add(s);

    for(line = in.readLine();line != null; line = in.readLine())
      this.rows.add(split(line));
  }

  /**
   * Speichert diese Properties unter dem angegebenen Dateinamen.
   * Die Properties selber werden nicht veraendert.
   * @param fileName
   * @throws IOException
   */
  public void save(String fileName)
    throws IOException
  {
    PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
    
    // Schreibe Spaltennamen
    for(String c : columnNames)
    {
      out.print(c);
      out.print(SEPARATOR);
    }
    out.println();

    // Schreibe Zeilen
    for(String[] row : rows)
    {        
      for(String r : row)
      {
        out.print(r);
        out.print(SEPARATOR);
      }
      out.println();
    }

    out.flush();
    out.close();
  }

  /**
   * Setzt einen Wert in der angegebenen Zeile und Spalte.
   * @param column
   * @param row
   * @param value
   * @return
   */
  public boolean set(String column, int row, Object value)
  {
    int col = getColumnIndex(column);    
    if(col == -1)
      return false;

    while(this.rows.size() <= row)
      this.rows.add(new String[columnNames.size()]);
    
    this.rows.get(row)[col] = value.toString();

    return true;
  }
  
  /**
   * Setzt eine Zeile mit Werten.
   * @param row
   * @param values
   * @return
   */
  public boolean set(int row, String[] values)
  {
    if(values.length != columnNames.size())
      return false;
    
    while(this.rows.size() <= row)
      this.rows.add(new String[columnNames.size()]);
    
    this.rows.set(row, values);
    
    return true;
  }

  /**
   * Gibt die Anzahl der gespeicherten Zeilen zurueck.
   * @return
   */
  public int size()
  {
    return this.rows.size();
  }

  /**
   * Zerlegt die uebergebene Zeile in ein Array von Strings.
   * @param line
   * @return
   */
  private String[] split(String line)
  {
    String[] splitted = line.split(SEPARATOR);
    
    for(int n = 0; n < splitted.length; n++)
    {
      splitted[n] = splitted[n].trim();
      if(splitted[n].endsWith(SEPARATOR_STRING) && splitted[n].startsWith(SEPARATOR_STRING))
        splitted[n] = splitted[n].substring(1, splitted[n].length() - 1);
    }

    return splitted;
  }
}
