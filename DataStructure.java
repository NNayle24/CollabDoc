public interface DataStructure {
    void addItem(Item item);
    void removeItem(String id);
    Item getItem(String id);
    Item[] getItems(String[] ids);
    Item[] getItems(String first);
    String IdGenerator();
    Item[] getAdded();
    String[] getRemoved();
    int getN();
    void LoadFileContent(String filename);
    void SaveFileContent(String filename);
}
