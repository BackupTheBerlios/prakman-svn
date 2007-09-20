package prakman.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;

/**
 * Das TableModel fuer die in PrakMan verwendeten Tabellen.
 */
public class TableModel extends AbstractTableModel 
{
  private static final long serialVersionUID = 0;
  
  private String[]		columnNames;      // Spaltennamen  
  private boolean[]		sortColumnDesc;   // Sortierungsstatus
  private ArrayList< ArrayList<Object> > data = new ArrayList< ArrayList<Object> >(); 
  
  /** Konstruktor alle Werte des Objektes anzeigen */
  public TableModel(String[] columnNames, ArrayList< ArrayList<Object> > data)
  {
    this.columnNames    = columnNames;
    this.data           = data;
    this.sortColumnDesc = new boolean[columnNames.length];
  }
  
  /** Anzahl der Zeilen zurueckgeben */
  public int getRowCount() 
  {
    return data.size();
  }    
  
  /** Gibt das Objekt in der angegebenen Zeile und Spalte zurueck */
  public Object getValueAt(int row, int col) 
  {     
    return data.get(row).get(col);
  }
  
  /**
   * Gibt die durch den Index spezifizierte Zeile zurueck.
   * @param row
   * @return
   */
  public Object getRow(int row) 
  {
    return data.get(row);
  }
  
  /** Name einer Spalte zurueckgeben */
  public String getColumnName(int col) 
  {  
    return columnNames[col];
  }
  
  /** Datentyp einer Spalte zurueckgeben */
  public Class<?> getColumnClass(int c) 
  {
    return getValueAt(0, c).getClass();
  }
  
  /** Anzahl der Spalten */
  public int getColumnCount() 
  { 
    return columnNames.length;
  }
  
  /** Setzen eines speziellen Wertes */
  public void setValueAt(Object value, int row, int col)
  {
    data.get(row).set(col, value);    
    fireTableCellUpdated(row, col);    
  }
  
  /** Legt fest welche Spalten editierbar isnd*/
  public boolean isCellEditable(int row, int col) 
  {
    if (getValueAt(row, col).equals(true) || getValueAt(row, col).equals(false) || (getValueAt(row, col) instanceof JComboBox))    
      return true;    
    else    
      return false;    
  }
  
  /**
   * Gibt alle Zeilen der Tabelle zurueck.
   * @return
   */
  public ArrayList getRows()
  {   
   return data; 
  }
  
  /**
   * Gibt an ob die angegebene Spalte sortiert ist oder nicht.
   * @param col
   * @return
   */
  public boolean getSortState(int col)
  {
    return sortColumnDesc[col];
  }
  
  /** Sortiericon erstellen */
  private Icon createAscendingIcon(int col)
  {
    sortColumnDesc[col] = false;
    return new Icon()
    {
      public int getIconHeight() 
      {
        return 3;
      }

      public int getIconWidth() 
      {
        return 5;
      }

      public void paintIcon(Component c, Graphics g, int x, int y) 
      {
        g.setColor( Color.BLACK );
        g.drawLine( x, y, x+4, y );
        g.drawLine( x+1, y+1, x+3, y+1 );
        g.drawLine( x+2, y+2, x+2, y+2 );
      }
    };
  }
  
  /**
   * Sortiericon erstellen.
   * @param col
   * @return
   */
  private Icon createDescendingIcon(int col)
  {
    sortColumnDesc[col] = true;
    return new Icon(){
      public int getIconHeight() {
        return 3;
      }

      public int getIconWidth() {
        return 5;
      }

      public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor( Color.BLACK );
        g.drawLine( x, y+2, x+4, y+2 );
        g.drawLine( x+1, y+1, x+3, y+1 );
        g.drawLine( x+2, y, x+2, y );
      }
    };
  } 
  
  /** Sortieren */
  public void sortByColumn(final int col)
  {
    if(data.size() == 0)
      return;
    Collections.sort(data, new Comparator<ArrayList>()
    {
      public int compare(ArrayList v1, ArrayList v2)
      {        
        int size1 = v1.size();
        if(col >= size1)
          throw new IllegalArgumentException("Out of Bounds");
        
        Comparable s1 = (Comparable<?>) v1.get(col);
        Comparable s2 = (Comparable<?>) v2.get(col);
        
        SimpleDateFormat df = new SimpleDateFormat( "dd.MM.yyyy - HH:mm" );
        try
        {     
            Date dates1 = df.parse(s1.toString());
            s1 = dates1;
            Date dates2 = df.parse(s2.toString());
            s2 = dates2;      
        }
        catch (Exception e) {
           System.out.println("Kein Datum");  
        }
        
        int cmp = s1.compareTo(s2);
        if (sortColumnDesc[col])
        {
          cmp *= -1;
        }
        return cmp;
      }     
     });
     sortColumnDesc[col] ^= true; 
     
  }
}
