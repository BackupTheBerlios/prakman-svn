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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import prakman.model.TableModel;

/**
 * Eigene Implementierung der JTable Klasse
 * Basisklasse aller Tabellen
 */
public class Table extends JTable 
{
  public static final long serialVersionUID = 0;
  
  private TableModel tableModel;
  private int        activeCol = 0;   
  
  public Table()
  {
    super();
    
    for(int col = 0; col < getColumnCount(); col++)
      setBestColumnWidth(col);
    
    getTableHeader().addMouseListener(new MouseAdapter() 
    {
      public void mousePressed(MouseEvent evt) 
      {
         activeCol = columnAtPoint(evt.getPoint());
         tableModel.sortByColumn(columnAtPoint(evt.getPoint()));    

         tableModel.fireTableDataChanged();
       }
     });
  }
   
  /** Konstruktor mit TableModel*/
  public Table(TableModel tm)
  {
    super(tm);
    this.tableModel = tm; 
    //getTableHeader().setReorderingAllowed(false);
    //setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);    
    //setModel(tableModel); 
    
    getTableHeader().addMouseListener(new MouseAdapter() 
    {
      public void mousePressed(MouseEvent evt) 
      {
         activeCol = columnAtPoint(evt.getPoint());
         tableModel.sortByColumn(columnAtPoint(evt.getPoint()));    

         tableModel.fireTableDataChanged();
       }
     });
   }
   
   /** Konstruktor mit TableModel, entfernt zusaetzlich nicht erwuenschte Spalten*/
   public Table(TableModel tm, int[] invalidColumns)
   {     
     super(tm);
     tableModel = tm;
     //setModel(tableModel);
     
     // Alle unerwuenschten Spalten entfernen
     for(int i=0; i< invalidColumns.length; i++)
       getColumnModel().removeColumn(getColumnModel().getColumn(invalidColumns[i]));
     
     // Sortieren einfuegen
     getTableHeader().addMouseListener(new MouseAdapter() {
       public void mousePressed(MouseEvent evt) {
         tableModel.sortByColumn(columnAtPoint(evt.getPoint()));
       }
     });     
  }   
   /** Column-Breite setzen */
   public void setBestColumnWidth( int col ) 
   {
     int headerWidth = getHeaderTextWidth( col );
     int columnWidth = getMaxCellTextWidth( col );
     int w = headerWidth > columnWidth ? headerWidth : columnWidth;
     getColumn( getColumnName( col ) ).setPreferredWidth( w );
     System.out.println("Setze Tabellenbreite");
   }
   
   /** Optimale Header-Text-Breite */
   public int getHeaderTextWidth( int col ) 
   {
     TableCellRenderer rend = getColumn( getColumnName( col ) ).getHeaderRenderer();
     if( rend != null )
         return rend.getTableCellRendererComponent( this, getColumnName( col ),
                 false, false, 0, 0 ).getPreferredSize().width ;
     return 0;
   }
   
   /** Optimale Zellenbreite */
   public int getMaxCellTextWidth( int col ) 
   {
     int iMax = 0;
     int w;
     
     for( int r = 0, max = getModel().getRowCount(); r < max; r++ ) 
     {
         Component c = getCellRenderer( r, col ).getTableCellRendererComponent(
                 this, getValueAt( r , col), false,false, r, col );
         w = c.getPreferredSize().width;
         iMax = w > iMax ? w : iMax;
     }
     return iMax;
 }
   
   /** Konstruktor mit TableModel*/
   public void refresh(TableModel tm)
   {
     this.tableModel = tm; 
     getTableHeader().setReorderingAllowed(false);
     //setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);    
     setModel(tableModel); 
     
     getTableHeader().addMouseListener(new MouseAdapter() 
     {
       public void mousePressed(MouseEvent evt) 
       {
          activeCol = columnAtPoint(evt.getPoint());
          tableModel.sortByColumn(columnAtPoint(evt.getPoint()));    

          tableModel.fireTableDataChanged();
        }
      });
    }
}
