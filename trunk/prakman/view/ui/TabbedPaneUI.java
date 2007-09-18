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

package prakman.view.ui;

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.*;
import prakman.io.*;

/**
 * Diese Klasse ist die Spezialisierung der BasicTabbedPaneUI-Klasse
 * und ist notwendig fuer die Implementierung des kleinen
 * Schliessen-Buttons fuer die einzelnen Tabs im TabbedPane.
 */
public class TabbedPaneUI 
  extends BasicTabbedPaneUI 
  implements MouseListener 
{  
  private Rectangle[] rects;
  private JTabbedPane parent;
  
  public TabbedPaneUI(JTabbedPane parent)
  {
    this.parent = parent;
    
    // Korrigiere Reihenfolge der MouseListener
    for(MouseListener l : parent.getMouseListeners())
      parent.removeMouseListener(l);

    parent.addMouseListener(this);
  }
  
  /** Extra Platz schaffen */
  protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) 
  {
    return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 40;
  }
  
  
  /** Ueberschreiben der paintTab Methode */
  protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect)
  {    
    super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
    paintCloseIcon(g, rects[tabIndex]);
    this.rects = rects;
  }
  
  /**
   * Zeichnet das Schliessen-Icon.
   * @param g
   * @param tabRect
   */
  protected void paintCloseIcon(Graphics g, Rectangle tabRect)
  {
    g.drawImage(
        Resource.getImage("resource/gfx/icons/process-stop_16_16.png").getImage(), 
        tabRect.x + tabRect.width - 20, tabRect.y + 2, null);
  }
  
  /**
   * Zeichnet den Title-Text des Tabs.
   */
  protected void paintText(Graphics g,
      int tabPlacement,
      Font font,
      FontMetrics metrics,
      int tabIndex,
      String title,
      Rectangle textRect,
      boolean isSelected)
  {
    textRect.setLocation(textRect.x - 20, textRect.y);
    super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
  }
  
  /**
   * Zeichnet das Icon des Tabs.
   */
  protected void paintIcon(Graphics g,
      int tabPlacement,
      int tabIndex,
      Icon icon,
      Rectangle iconRect,
      boolean isSelected)
  {
    iconRect.setLocation(iconRect.x - 20, iconRect.y);
    super.paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);
  }
  
  /** Es wurde auf ein Tab geklickt. */
  public void mousePressed(MouseEvent e) 
  {
    if(rects == null)
      return;
    
    for(int n = 0; n < rects.length; n++)
    {
      Rectangle rec = rects[n];
      if(rec.contains(e.getPoint()))
      {
        System.out.println(rec);
        if(e.getX() > rec.x + rec.width - 20)
          this.parent.remove(n);
      }
    }
  }

  /** 
   * Leere Interface-Methode.
   */
  public void mouseReleased(MouseEvent e) 
  {
  }

  /** 
   * Leere Interface-Methode.
   */
  public void mouseEntered(MouseEvent e)
  {
  }

  /** 
   * Leere Interface-Methode.
   */
  public void mouseExited(MouseEvent e) 
  {
  }

  /** 
   * Leere Interface-Methode.
   */
  public void mouseClicked(MouseEvent e) 
  {
  }
}
