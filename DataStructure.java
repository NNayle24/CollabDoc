public interface DataStructure {
    void addItem(Item item);
    void removeItem(String id);
    Item getItem(String id);
    Item[] getItems(String[] ids);
    Item[] getItems(String first);
    String IdGenerator();
    String[] getAdded();
    String[] getRemoved();
    void LoadFileContent(String filename);
    void SaveFileContent(String filename);
    
}
