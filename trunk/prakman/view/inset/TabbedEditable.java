package prakman.view.inset;

/**
 * Jedes BasePanel, welches das TabbedEditPanel verwendet, muss
 * dieses Interface implementieren.
 * Das TabbedEditPanel wird dann die Methoden dieses Interfaces
 * aufrufen.
 */
public interface TabbedEditable
{
  public void addClicked();
  public void removeClicked();
  
  public void selectAllClicked();
  public void deselectAllClicked();
}
