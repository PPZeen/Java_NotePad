import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class EnumMenu <T extends Enum <T>> {
    private Menu menu ;
    private MenuItem[] menuItems;
    
    EnumMenu(Class<T> a,String name) {
        menu = new Menu(name);
        menuItems = new MenuItem[a.getEnumConstants().length];
        T[] enumArr= a.getEnumConstants();
        for(int i=0;i<enumArr.length;i++)
            menuItems[i]=new MenuItem(enumArr[i].toString());
        menu.getItems().addAll(menuItems);    
    };
    public Menu getMenu() {
        return menu;
    }
    public MenuItem[] getMenuItems() {
        return menuItems;
    }
}
