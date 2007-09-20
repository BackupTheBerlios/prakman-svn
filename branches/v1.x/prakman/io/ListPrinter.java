package prakman.io;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.Paper;
import java.util.ArrayList;

/**
 * Klasse zum Drucken von diversen Listen.
 */
public class ListPrinter implements Printable
{
  private String[]                 allColumns;
  private String[]                 printColumns;
  // Alle Daten  unformatiert
  private String[][]               allValues;
  // Das was zur Voransicht gedruckt wird
  private String[][]               printValues; 
  // Fuer die Print() Methode um hinterher alles richtig zu drucken
  private ArrayList<String[][]>    printAllValues;
  private ArrayList<String[]>	     printAllColumns;

  private String[]				          underText = new String[0];
  private PageFormat                pageFormat;
  private Paper					            paperUsed;
  private ArrayList<BufferedImage>  previewPages;
  private String 				            previewtitle;
  private Font					            textFont;
  
  // Maximale Zahl der Datensaetze pro Seite
  private int 			   		   maxPersonsPerPage = 20;
  // Versatzausgleich bei Pageformataenderungen
  private int 					   heightDiff = 0;
  // Mindestbreite der momentanen Vertikalen Grid-Linie

  //Unteres Seitenende	
  private int 					   pageBottom = 0;
  //Abstand der Vertikalen Grid-Linie
  private int[] 				   GridVerticalSpace;
  //Falls nicht alles auf eine Seite passt wird die Anzahl der Seite verdoppelt
  private int					   pageMultiplyer = 1;
  
  private int[] 				   colSpaces; 
  //Globaler Seitenzaehler wird bei jedem print() erhoeht
  //und bei jedem neuen CreatePreview() resetet
  private int 					   globalPageCounter = 0 ; 
  
  /**
   * Erstellt ein neues ListPrinter Objekt.
   * @param persons Die Liste von Personen, die gedruckt werden soll.
   * @param columns Die Ueberschriften der zusaetzlichen Spalten.
   */
  public ListPrinter(String[][] _values, String[] columns)
  {  
    this.pageFormat = new PageFormat();
    this.paperUsed  = new Paper();
    this.paperUsed.setImageableArea(72,72,451,697);
    this.pageFormat.setPaper(paperUsed);
    this.allValues		= _values;
    this.allColumns   = columns;
  }
  
  /** 
   * Erstellt Bilder fuer die Druckvorschau.
   * @param title Die Ueberschrift der Voransicht
   */
  public void createPreview(String title)
  {
    // Wenn das SeitenFormat geaendert wird ruft Das Druckfenster diese methode 
    // auf jedoch ohne Titel und ueberschreib diesen mit ""(nix). Daher:
    if (!title.equals("")) 
      previewtitle = title;

    this.globalPageCounter = 0 ;
	
    this.colSpaces  = new int[allColumns.length];

    printAllValues		    = new ArrayList<String[][]>();
    printAllColumns		    = new ArrayList<String[]>();
    previewPages  			  = new ArrayList<BufferedImage>();
    int width     			  = (int)pageFormat.getWidth();
    int height    			  = (int)pageFormat.getHeight();

    if (pageFormat.getHeight() < pageFormat.getWidth())
    {
    	maxPersonsPerPage 	= 10;//12
    	heightDiff 			= 72;//24
    	pageFormat.setOrientation(PageFormat.LANDSCAPE);
    }
    else // falls Vertikal
    {
    	maxPersonsPerPage 	= 20;
    	heightDiff 			= 79;
    	pageFormat.setOrientation(PageFormat.PORTRAIT);
    }

    // Daten aufteilen, sodass sie auf die Seiten passen!
    
    // Holt die Info welche Spalten auf welche Seite kommen
    int[] pageCols = this.separateColumns(width, height);
    
    int pageColsCount = pageCols.length;
    
    // Speichert immer eine oder gleich mehrere Seiten 
    for(int m = 0 ; m < pageColsCount-1; m++ )
    {
    	// System.out.println("Von:"+pageCols[m]+" Bis:"+(pageCols[m+1]));

    	int from 	= pageCols[m];
    	int to 		= pageCols[m+1];
    	
    	// Listen umformatieren
    	if (pageColsCount > 1) // Es wird mehr als eine Seite geben 
    	{
    	  if (allValues[0].length > maxPersonsPerPage) // Falls zu viele Personen fuer eine Seite
    	  {
    	    // Vertikal Splitting
    	    ArrayList<String[][]> splitted = (splitVertValues(splitValues(allValues, from, to), maxPersonsPerPage));
    	    int size = splitted.size();
    	    for (int k = 0; k < size ; k++)
    	    {
    	      System.out.println("Nummer"+(k+1));
    	      printAllColumns.add(splitColumns(allColumns, from, to));    	    			
    	      printAllValues.add(  splitted.get(k)  );
    	    }
    	  }
    		else // Es passt vertikal auf eine Seite 
    		{
    			printAllValues.add(splitValues(allValues, from, to)); 
    			printAllColumns.add(splitColumns(allColumns, from, to));     			    			    			
    		}		
    	}
    	else // Es gibt nur eine Seite Horizontal
    	{
    	  if (allValues[0].length > maxPersonsPerPage) // Falls zuviele Personen fuer eine Seite
    	  {
    	    // Vertikal Splitting
    	    ArrayList<String[][]> splitted = (splitVertValues(splitValues(allValues, from, to),maxPersonsPerPage));
    	    int size = splitted.size();
    	    for (int k = 0 ; k < size; k++)
    	    {
    	      //System.out.println("Nummer"+(k+1));
    	      printAllColumns.add(splitColumns(allColumns, from, to));    	    			
    	      printAllValues.add(  splitted.get(k)  );
    	    }
    	  }
    	  else //Es gibt wirklich nur eine Seite Vertikal wie Horizontal
    	  {
    	    printAllValues.add(allValues); 
    	    printAllColumns.add(allColumns);     	    	    	    	    	    	    	    	
    	  }
    	}
    	//System.out.println(" ArraySize:"+printAllValues.size());
    }
    
    // Fuer jede erstellte Seite wird Print() aufgerufen 
    for (int i = 0 ;; i++)
    {
    	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    	if(print(img.getGraphics(), pageFormat, i) == Printable.NO_SUCH_PAGE)
    		break;
    	
    	previewPages.add(img);
    }
  }
  
  
  /**
   * Gibt eine ArrayList mit Images zurueck, welche die Seiten
   * der Druckvorschau enthalten.
   * Vorher muss allerdings die Methode createPreview() aufgerufen
   * werden, sonst ist die ArrayList leer.
   */
  public ArrayList<BufferedImage> getPreviewPages()
  {
	  return previewPages;
  }
  
  /**
   * Liefert die anforderte Seite in Graphics zurueck.
   * @param graphics zum zeichnen
   * @param Pagefromat Seitenformat
   * @param Pageindex die angeforderte Seite
   * */ 
  public int print(Graphics graphics, PageFormat _pageFormat, int pageIndex)
  {
	  	System.out.println("Print(index:"+pageIndex+") PagesCount():"+pagesCount());
	    //	ermittelt anhand der Anzahl der Personen wie viele Seiten notwendig
		//  sind.
	    if(pageIndex >= pagesCount()) 
	      return Printable.NO_SUCH_PAGE;
	    
	    // Seite einrichten fuer Hoch oder Quer-Format
	    if (pageFormat.getWidth() > pageFormat.getHeight())
	    	_pageFormat.setOrientation(PageFormat.LANDSCAPE);
	    else
	    	_pageFormat.setOrientation(PageFormat.PORTRAIT);
	    
      
	    // Die Daten muessen aus der ArrayList geholt werden
	    printValues = this.printAllValues.get(pageIndex);
	    printColumns= this.printAllColumns.get(pageIndex);
	    
	    globalPageCounter++;
	    
	    // Fuelle Hintergrund des nicht druckbaren Bereichs
	    graphics.setColor(Color.WHITE);
	    graphics.fillRect(0, 0, (int)pageFormat.getWidth(), (int)pageFormat.getHeight());
	    
	    int offX    = (int)pageFormat.getPaper().getImageableX();
	    int offY    = (int)pageFormat.getPaper().getImageableY();
	    int width   = (int)pageFormat.getImageableWidth();
	    int height  = (int)pageFormat.getImageableHeight();

	    // Zeichne Rand um den nicht druckbaren Bereich
	    //graphics.setColor(Color.RED);
	    //graphics.drawRect(offX, offY, width, height);
	    
	    // Schreibe Listentitel
	    if(pageIndex == 0)
	    {
	      graphics.setColor(Color.BLACK);
	      textFont = graphics.getFont();
	      graphics.setFont(new Font(null, Font.BOLD, 25));
	      graphics.drawString(previewtitle, offX, offY+=25);
	      graphics.setFont(textFont);
	      offY += 50;
	    }
	    else
	    	offY += 75;
	    
	    // Schreibe Seitenende
	    graphics.setColor(Color.BLACK);
	    graphics.drawString("- Seite " + (pageIndex+1) + " -", 
	    					width / 2, (int)pageFormat.getPaper().getImageableY() + height);
	    height -= heightDiff;
	   
	    // Hoehe der Reihen bestimmen
	    int rowHeight = (graphics.getFont().getSize()*2); 
	    
	    // Zeichne Horizontal-Grid
	    for(int y = 1; y <= maxPersonsPerPage; y++)
	    {
	    	graphics.drawLine(  offX       ,  offY+(y*rowHeight)+(rowHeight/6)  ,
	    						width+offX ,  offY+(y*rowHeight)+(rowHeight/6)   );
	    	
	    	// Unteres SeitenEnde bestimmen und Global speichern
	    	pageBottom = offY+(y*rowHeight)+(rowHeight/6);
	    }

	    // Die Horizontalen Spaltenabstaende werden ermittelt   	
	    GridVerticalSpace = null;
	    GridVerticalSpace = measureGridAllVerticalSpaces(printValues, printColumns, measureGridAllVerticalSpaces(allValues, allColumns, 0)[0]);
	 
	    //String[] GridVerticalSpace;
	    // Jede Spalte wird einzeln nacheinander gezeichnet
	    for(int i = 0 ; i<printColumns.length ; i++)
	    {

	    	
	    	if (i>0) // faengt beim ersten x-Abstand (GridVerticalSpace(0)) an zu zeichnen
	    	{
	    		// Zeichne Spaltenueberschrift fuer diese Spalte
	    		graphics.setColor(Color.BLACK);
	    	    graphics.setFont(new Font(null, Font.BOLD, textFont.getSize()));
	    		if (!(printColumns[i] == null))
	    			graphics.drawString(printColumns[i], offX+GridVerticalSpace[i-1]+4, offY);
	    		//Zeichne eine Column (Spalte) Text
	    		graphics.setFont(textFont);
	    		//printAllValues.add(new String[printColumns.length][maxPersonsPerPage]);
	    		printTextColumn(graphics, pageIndex, offX+GridVerticalSpace[i-1]+4, offY,i,printValues);    		
	    	}
	    	else //f�ngt bei x = 0 an zu zeichnen
	    	{
	    		//Zeichne Spalten�berschrift f�r diese Spalte
	    		graphics.setColor(Color.BLACK);
	    		graphics.setFont(new Font(null, Font.BOLD, textFont.getSize()));
	    		graphics.drawString(printColumns[i], offX+4, offY);
	    		//Zeichne eine Column (Spalte) Text
	    		graphics.setFont(textFont);
	    		//printAllValues.add(new String[printColumns.length][maxPersonsPerPage]);
	    		printTextColumn(graphics, pageIndex, offX+4, offY,i,printValues);    		
	    	}
	    	
	    	//Zeichne eine Vertikal-Gridlinie 
	    	//System.out.println("Vertikal Linie bei i:"+i+" Breitenabstand:"+GridVerticalSpace[i] +" text"+printColumns[i]);
	    	if (printColumns[i] != null)
	    	{
	    		if( !(i > printColumns.length-2) )
	    			graphics.drawLine(GridVerticalSpace[i]+offX, offY, offX+GridVerticalSpace[i], height+graphics.getFont().getSize());
	    	}
	    }
	    
	    //hier Tabellen�berschriften unten anh�ngen
	    if( underText.length > 0)
	    {
	    	printUnderText(graphics, offX, pageBottom+10);
	    }
	    
	    return Printable.PAGE_EXISTS;
  }
   
  /**
   * Zeichnet alle Seiten in Graphics und speichert sie als
   * Grafik in Previewpages.
   **/
  public int printPage(Graphics graphics, PageFormat pageFormat, int pageIndex)
  {
		//	ermittelt anhand der Anzahl der Personen wie viele Seiten notwendig
		//  sind.
	    if(pageIndex >= pagesCount()) //pageMultiplyer*
	      return Printable.NO_SUCH_PAGE;
	    
	    globalPageCounter++;
	    
	    // Fuelle Hintergrund des nicht druckbaren Bereichs
	    graphics.setColor(Color.WHITE);
	    graphics.fillRect(0, 0, (int)pageFormat.getWidth(), (int)pageFormat.getHeight());
	    
	    int offX    = (int)pageFormat.getPaper().getImageableX();
	    int offY    = (int)pageFormat.getPaper().getImageableY();
	    int width   = (int)pageFormat.getImageableWidth();
	    int height  = (int)pageFormat.getImageableHeight();
	    //System.out.println("h�he:"+height+" weite:"+width+" offx:"+offX+" offy:"+offY);
	    
	    // Zeichne Rand um den nicht druckbaren Bereich
	    //graphics.setColor(Color.RED);
	    //graphics.drawRect(offX, offY, width, height);
	    
	    // Schreibe Listentitel
	    if(globalPageCounter == 1)
	    {
	      graphics.setColor(Color.BLACK);
	      textFont = graphics.getFont();
	      graphics.setFont(new Font(null, Font.BOLD, 25));
	      graphics.drawString(previewtitle, offX, offY+=25);
	      graphics.setFont(textFont);
	      offY += 50;
	    }
	    else
	    	offY += 75;
	    
	    // Schreibe Seitenende
	    graphics.setColor(Color.BLACK);
	    graphics.drawString("- Seite " + (globalPageCounter) + " -", 
	        width / 2, (int)pageFormat.getPaper().getImageableY() + height);
	    height -= heightDiff;
	    
	    graphics.setColor(Color.GRAY);
	   
	    // H�he der Reihen bestimmen
	    int rowHeight = (graphics.getFont().getSize()*2); 
	    
	    // Zeichne Horizontal-Grid
	    for(int y = 1; y <= maxPersonsPerPage; y++)
	    {
	    	graphics.drawLine(  offX       ,  offY+(y*rowHeight)+(rowHeight/6)  ,
	    						width+offX ,  offY+(y*rowHeight)+(rowHeight/6)   );
	    	
	    	//Unteres SeitenEnde bestimmen und Global speichern
	    	pageBottom = offY+(y*rowHeight)+(rowHeight/6);
	    }

	    
	    //Jede Spalte wird einzeln nacheinander gezeichnet
	    for(int i = 0 ; i<printColumns.length ; i++)
	    {
	    	
	    	if(pageIndex == 0)
	    	{
	    		//GridVerticalSpace = measureGridVerticalSpace(printValues[i],printColumns,i);
	    		GridVerticalSpace = measureGridAllVerticalSpaces(printValues, printColumns,0);
	    	}
	    	
	    	//System.out.println("jo"+GridVerticalSpace[i]);
	    	
	    	if (i>0)
	    	{
	    		//Zeichne Spalten�berschrift f�r diese Spalte
	    		graphics.setColor(Color.BLACK);
	    	    graphics.setFont(new Font(null, Font.BOLD, textFont.getSize()));
	    		if (!(printColumns[i] == null))
	    			graphics.drawString(printColumns[i], offX+GridVerticalSpace[i-1]+4, offY);
	    		//Zeichne eine Column (Spalte) Text
	    		graphics.setFont(textFont);
	    		//printAllValues.add(new String[printColumns.length][maxPersonsPerPage]);
	    		printTextColumn(graphics, pageIndex, offX+GridVerticalSpace[i-1]+4, offY,i,printValues);    		
	    	}
	    	else
	    	{
	    		//Zeichne Spalten�berschrift f�r diese Spalte
	    		graphics.setColor(Color.BLACK);
	    		graphics.setFont(new Font(null, Font.BOLD, textFont.getSize()));
	    		graphics.drawString(printColumns[i], offX+4, offY);
	    		//Zeichne eine Column (Spalte) Text
	    		graphics.setFont(textFont);
	    		//printAllValues.add(new String[printColumns.length][maxPersonsPerPage]);
	    		printTextColumn(graphics, pageIndex, offX+4, offY,i,printValues);    		
	    	}
	    	
	    	//Zeichne eine Vertikal-Gridlinie 
	    	//System.out.println("Vertikal Linie bei i:"+i+" Breitenabstand:"+GridVerticalSpace[i] +" text"+printColumns[i]);
	    	if (printColumns[i] != null)
	    	{
	    		if(printColumns.length-1 != i)
	    			graphics.drawLine(GridVerticalSpace[i]+offX, offY, offX+GridVerticalSpace[i], height+graphics.getFont().getSize());
	    	}
	    }
	    
	    //hier Tabellen�berschriften unten anh�ngen
	    //System.out.println("UnderText:"+underText.length);
	    if( underText.length > 0)
	    {
	    	printUnderText(graphics, offX, pageBottom+10);
	    }
	    
	    return Printable.PAGE_EXISTS;
  }
    
  /**
   * Zeichnet eine Textzeile in Graphics.
   **/
  private void printTextColumn(Graphics graphics,int pageIndex,int offX,int offY, int colNr, String[][] values)
  {
	    //String[][] printBuffer = printAllValues.get(printallCount);
	    //  Schreibe Textinhalte
	    if (values[0].length > maxPersonsPerPage) //Falls zuviele Personen f�r eine Seite
	    {
	    	int personBegin = pageIndex*maxPersonsPerPage; //schreibe den Rest auf die n�chste Seite	    	
	    
	    	graphics.setColor(Color.BLACK);    
	    	//Falls es mehr as 2 Seiten sind und eine Mittlere geschrieben werden soll!
	    	int drawEnding=0;
	    	if (values[0].length <= personBegin+maxPersonsPerPage)
	    		drawEnding = values[0].length;
	    	else
	    		drawEnding = personBegin+maxPersonsPerPage;
	    	//for SChleife zum Zeichnen
	    	for (int i = personBegin,j=0 ; i<drawEnding ; i++,j++)
	    	{
	    		//System.out.println(i+",");
	    		offY += (graphics.getFont().getSize()*2);
	    		if ( values[colNr][i] != null)
	    		{
	    			graphics.drawString(values[colNr][i], offX, offY);
	    			//TODO:
	    			//printBuffer[colNr][j] = values[colNr][i];
	    		}
	    	}
	    }
	    else
	    {
	    	graphics.setColor(Color.BLACK);    
	    	for (int i = 0 ; i<values[0].length ; i++)
	    	{
	    		offY += (graphics.getFont().getSize()*2);
	    		if ( values[colNr][i] != null)
	    		{
	    			graphics.drawString(values[colNr][i], offX, offY);	
	    			//TODO:
	    			//printBuffer[colNr][i] = values[colNr][i];	
	    		}
	    	}
	    }
  }
  
  /**
   * Bestimmt die Abstaende zwischen den Spalten anhand der
   * Inhalte und Ueberschriften. 
   * TODO: Sinn, Zweck und Notwendigkeit dieser Methode?
   **/
  private int[] measureGridVerticalSpace(String[] text, String[] colText, int col)
  {
	  
	  //System.out.println("col:"+col+" len:"+colSpaces.length);
	  //maximale Spaltenbreite bestimmen
	  for(int i = 0 ; i < text.length ; i++)
	  {
		  if (measureText( text[i] )+8 > colSpaces[col] )
			  colSpaces[col] = measureText(text[i])+8;
	  }		  

	  
	  //�berschriften mit einbeziehen 
		  if (measureText( colText[col] )+8 > colSpaces[col] )
			  colSpaces[col] = measureText(colText[col])+8;

	  
	  //Spaltenbreite aufaddieren 
	  if (col>0)
	  {
		  colSpaces[col] += colSpaces[col-1];
	  }
	  
	  return colSpaces;
  }
 
  /**
   * Bestimmt die Abstaende zwischen den Spalten anhand der
   * Inhalte und Ueberschriften. 
   **/
  private int[] measureGridAllVerticalSpaces(String[][] text, String[] colText, int firstColMin)
  {
	  int[] colVertSpaces = new int[colText.length];
	  
	  //maximale Spaltenbreite bestimmen
	  for ( int j = 0 ; j < colText.length ; j++)//Spalten ,�berschriften 
	  {
		  for(int i = 0 ; i < text[j].length ; i++)//Zeilen ,Namen
		  {
			  if (measureText( text[j][i] )+8 > colVertSpaces[j] )
				  colVertSpaces[j] = measureText(text[j][i])+8;
		  }		  		  
	  }

	  //�berschriften mit einbeziehen 
	  for (int i = 0 ; i < colText.length ; i++)
	  {
		  if (measureText( colText[i] )+8 > colVertSpaces[i] )
			  colVertSpaces[i] = measureText(colText[i])+8;		  
	  }

	  if(firstColMin > colVertSpaces[0])
	  {
		  colVertSpaces[0] = firstColMin;
	  }
	  
	  //Spaltenbreite aufaddieren 
	  for (int i = 1 ; i < colVertSpaces.length ; i++)
	  {
		  colVertSpaces[i] += colVertSpaces[i-1];		  
	  }
	  
	  return colVertSpaces;
  }

  /**
   * Bestimmt die Abstaende zwischen den Spalten anhand der
   * Inhalte und Ueberschriften. 
   * Beruecksichtigt das die erste Spalte auf jeder Seite 
   * wieder gedruckt wird.
   **/
  private int[] measureGridAllVerticalSpacesWithFirstColumn(String[][] text, String[] colText)
  {
	  /*
	   * Wenn die letzten Spalten nicht mehr auf eine Seite passen 
	   * muss die SeitenAnzahl erh�ht werden.
	   * Die Spalten m�ssen �berpr�ft werden ob sie wiederum auf
	   * eine Seite passen.
	   * */
	  
	  int[] colVertSpaces = new int[colText.length];
	  
	  
	  
	  //maximale Spaltenbreite bestimmen
	  for ( int j = 0 ; j < colText.length ; j++)//Spalten ,�berschriften 
	  {
		  for(int i = 0 ; i < text[j].length ; i++)//Zeilen ,Namen
		  {
			  if (measureText( text[j][i] )+8 > colVertSpaces[j] )
				  colVertSpaces[j] = measureText(text[j][i])+8;
		  }		  		  
	  }

	  
	  //�berschriften mit einbeziehen 
	  for (int i = 0 ; i < colText.length ; i++)
	  {
		  if (measureText( colText[i] )+8 > colVertSpaces[i] )
			  colVertSpaces[i] = measureText(colText[i])+8;		  
	  }

	  
	  //Spaltenbreite aufaddieren 
	  for (int i = 1 ; i < colVertSpaces.length ; i++)
	  {
		  colVertSpaces[i] += colVertSpaces[i-1];	
		  //System.out.println("i:"+i+" VertikalAbst�nde:"+colVertSpaces[i]);
	  }
	  
	  return colVertSpaces;
  }
  
  /**
   * Misst die Laenge des uebergebenen Strings anhand des Fonts 
   **/
  private int measureText(String str)
  {
	  int size;
	  //System.out.println("Stringnull?;"+str);
	  if(str == null)
		  return 0;
	  
	  size = (int)textFont.getStringBounds(str, new FontRenderContext(null,false,false)).getWidth();
	  return size;
  }
  
  /**
   * Setzt die UnterTexte.
   **/
  public void setUnderText(String[] _underText)
  {
	  //System.out.println("ut:"+_underText.length);
	  for (int i = 0 ; i < _underText.length ; i++)
	  {
		  if (_underText[i] == null)
		  {
			  _underText[i] = "Nr.1 = noch unbekannt....   ;)";
		  }
	  }
	  underText = _underText;
  }
  
  /**
   * Druckt die UnterTexte in Graphics.
   **/
  public void printUnderText(Graphics graphics, int offX, int offY)
  {
      textFont = graphics.getFont();
      graphics.setColor(Color.BLACK);
      graphics.setFont(new Font(null, Font.PLAIN, 9));
      for (int i = 0 ; i < underText.length ; i++ )
      {
    		  graphics.drawString(underText[i], offX, offY+=11);
      }
      graphics.setFont(textFont);
  }
  
  /**
   * Anzahl der Seiten. 
   **/
  private int pagesCount()
  {
    return this.printAllValues.size();
  }
  
  /**
   * Liefert die Anzahl der Spalten.
   **/
  private int columnsCount()
  {
    if(printColumns == null)
      return 2;
    else
      return printColumns.length + 2;
  }
  
  /**
   * Setzt das Seitenformat.
   **/
  public void setPageFormat(PageFormat pageFormat)
  {
    this.pageFormat = pageFormat;
  }

  /**
   * Liefert die Ueberschriften in den Grenzen von und bis. 
   * */
  private String[] splitColumns(String[] col, int from, int to)
  {
    String[] print = new String[to-from+1];
    
    print[0] = col[0]; // Ueberschrift Namen muss bleiben

    if (from == 0)
      from = 1;

    for(int i = from, j = 1; i < to ; i++, j++)
  		print[j] = col[i];    	

    return print; 
  }
  
  /**
   * Liefert die Inhalte in den Grenzen von und bis.
   * */
  private String[][] splitValues(String[][] val, int from,int to)
  {
	  String[][] buffer = new String[to-from+1][val[from].length];
	  //System.out.println("Splitting"+from+" "+to);
	  
	  buffer[0] = val[0];
	  
	  if ( from == 0 )
	  {
	  	  from = 1;
	  	  //to++;
	  }

	  
	  
	  for (int i = from, k = 1; i < to ; i++,k++)
	  {
		  for ( int j = 0 ; j < val[from].length ; j++ )
		  {
			  //System.out.println(buffer[0][j]+"@"+val[i][j]);
			  buffer[k][j] = val[i][j];
		  }
	  }

	  return buffer;
  }
  
  /**
   * Prueft ob die Inhalte und Ueberschriften auf die Seite passen.
   **/
  private boolean testPageFit(String[][] values,String[] columns)
  {
	  int[] result;
	  
	  result = this.measureGridAllVerticalSpaces(values, columns,0);
	  
	  for (int i = 0 ; i < result.length ; i++ )
	  {
		  //System.out.println("Column:"+columns[i]+"Result:"+result[i]);
		  if (result[i] > pageFormat.getImageableWidth()+72)
			  return false;
	  }
	  
	  return true;
  }
  
  /**
   * Liefert die Spaltennummern zum Separieren der Inhalte 
   * und Ueberschriften .
   * */
  private int[] separateColumns(int width, int height)
  {
	    //printValues umh�ngen und aufteilen
	    BufferedImage imag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // geht leider nicht anders...
	    textFont = imag.getGraphics().getFont(); //measureText braucht umbedingt dieses Font 
	    
	    //Speicher f�r die UmbruchSpalten mit initilisierung auf 0
	    int   pageColsCount = 1; //1 weil die Liste bei 0 beginnt ;)
	    int[] pageCols 	= new int[allColumns.length]; //mehr als eine Col pro Seite wird es hoffentlich nicht geben...
	    for (int i = 0 ; i < pageCols.length ; i++)
	    	pageCols[i] = 0 ; 
	    
	    
	    
	    int[] buffer 	= this.measureGridAllVerticalSpacesWithFirstColumn(allValues, allColumns);
	    int pages = buffer.length;
	    int von = 0;
	    //SpaltenNummern zum Seitenwechsel bestimmen
	    for (int i = 1 ; i < buffer.length ; i++) //jede Spalte 
	    {
	    	for (int x = 1 ; x < pages ; x++) //Multiplikator f�r die Seite
	    	{
	    		//System.out.println(i+"Buffer:"+ buffer[i] + " Breite:"+ ((pageFormat.getImageableWidth()*x)+72)  );
	    		if (buffer[i] >= (int)( (pageFormat.getImageableWidth())*(double)x)+0 && buffer[i-1] <= (int)(  (pageFormat.getImageableWidth())  *(double)x)+0)
	    		{
	    			//System.out.println("Breite:"+(( (pageFormat.getImageableWidth())*(double)x)+0)+" normal:"+pageFormat.getImageableWidth()+" buffer:"+buffer[i]);
	    			//System.out.println("Neue Seite von:"+von+" bis:"+(i)+" oder etwa X:"+x+"   "+buffer[i]+"l�nge:"+buffer.length);
	    			von = (i);
	    			//Da die Namen immer vorn drangeh�ngt werden muss deren L�nge aufaddiert werden
	    			for( int k = i ; k < buffer.length ; k++)
	    				buffer[k] += buffer[0];
	    			
	    
	    			//hier werden die QuerSeitenSpalten bestimmt 
	    			pageCols[pageColsCount++] = (i);
	    			//SeitenMultiplikator muss um 1 erh�ht werden 
	    			pageMultiplyer++;
	    			
	    			//Wenn der Buffer noch nicht leer ist obwohl wir am ende
	    			//sind muss der Rest auf die n�chste Seite.	
	    		} 
	    	}
	    }
	  
	    pageCols[pageColsCount++]=allColumns.length;
	    
	    int[] result = new int[pageColsCount];
	    for (int i = 0 ; i < pageColsCount ; i++ )
	    	result[i] = pageCols[i];
	    
	    
	    return result;
  }
  
  /**
   * Liefert die Inhalte Vertikal an das Seitenformat angepasst zurueck.
   **/
  private ArrayList<String[][]> splitVertValues(String[][] values,int maxHeight)
  {
	  ArrayList<String[][]> results= new ArrayList<String[][]>();
	  String[][] buffer = new String[values.length][maxHeight];
	 
	  int i=0,j=0,m=0;
	  for (int k = 0 ; k < ((int)(values[0].length/maxHeight)+1) ; k++)
	  {
		  for (i = 0 ; i < values.length ; i++)//�berschriften, Spalten
		  {
			  for (j = k*maxHeight ,m=0 ; j < (k+1)*maxHeight ; j++,m++ )//Reihen,Zeilen 
			  {
				  if(j >= values[0].length)
					  break;
				  
				  //System.out.println("i="+i+"    j="+j+"   k="+k+"   Value="+values[i][j]); 
				  buffer[i][m] = values[i][j];
			  }
		  }	  
		  results.add(buffer);
		  buffer = new String[values.length][maxHeight];
	  }
	  return results;
  }
  
}
