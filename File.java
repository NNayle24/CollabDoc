import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public final class File extends Observer implements DataStructure {
    private static final int n = 16;
    private Subscriber subscribers;
    private final HashMap<String, Item> items;
    private final List<Item> added = Collections.synchronizedList(new ArrayList<>());
    private final List<String> removed = Collections.synchronizedList(new ArrayList<>());

    @Override
    public String IdGenerator() {
        String id;

        do {
            id = "";
            for (int i = 0; i < n; i++) {
                id += (char) (Math.random() * 26 + 97);
            }
        } while (items.containsKey(id));
        return id;
    }

    @Override
    public void addItem(Item item) {
        items.put(item.getId(), item);
        items.get(item.getPrev()).setNext(item.getId());
        items.get(item.getNext()).setPrev(item.getId());
        added.add(item);
        added.add(items.get(item.getPrev()));
        added.add(items.get(item.getNext()));
        System.out.println("Number of items: " + items.size());
        notifySubscribers();
    }

    @Override
    public void removeItem(String id) {
        added.add(items.get(items.get(id).getPrev()));
        added.add(items.get(items.get(id).getNext()));
        items.get(items.get(id).getPrev()).setNext(items.get(id).getNext());
        items.get(items.get(id).getNext()).setPrev(items.get(id).getPrev());
        items.remove(id);
        removed.add(id);
        notifySubscribers();
    }

    @Override
    public Item getItem(String id) {
        return items.get(id);
    }

    @Override
    public Item[] getItems(String[] ids) {
        Item[] items = new Item[ids.length];
        for (int i = 0; i < ids.length; i++) {
            items[i] = this.items.get(ids[i]);
        }
        return items;
    }

    @Override
    public Item[] getItems(String first) {
        Item[] items = new Item[this.items.size()];
        Item item = this.items.get(first);
        int i = 0;
        while (item != null) {
            items[i++] = item;
            item = this.items.get(item.getNext());
        }
        items = Arrays.copyOf(items, i);
        return items;
    }

    public File() {
        this.items = new HashMap<>();
        this.subscribers = new Sender(this);
        Item first = new Item("first", "cipher1", "iv1", 0, 0, null, "last", "date1", "author1");
        Item last = new Item("last", "cipher2", "iv2", 0, 0, "first", null, "date2", "author2");
        items.put(first.getId(), first);
        items.put(last.getId(), last);
    }

    @Override
    public Item[] getAdded() {
        Item[] addedArray = new Item[added.size()];
        added.toArray(addedArray);
        added.clear();
        return addedArray;
    }

    @Override
    public String[] getRemoved() {
        String[] removedArray = new String[removed.size()];
        removed.toArray(removedArray);
        removed.clear();
        return removedArray;
    }

    public void subscribe(Subscriber subscriber) {
        subscribers= subscriber;
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers = null;
    }

    @Override
    public void notifySubscribers() {
        subscribers.update();
    }

    @Override
    public void LoadFileContent(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isEmpty = true;
            while ((line = br.readLine()) != null) {
                System.out.println("line: " + line);
                isEmpty = false;
                String[] data = line.split(";");
                Item item = new Item(data[0], data[1], data[2], Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[5], data[6], data[7], data[8]);
                addItem(item);
            }
            if (isEmpty) {
                System.out.println("The file is empty.");
            }
        } catch (IOException e) {
            System.err.println("Error loading file content: " + e.getMessage());
        }
    }

    @Override
    public void SaveFileContent(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Item item : items.values()) {
                if (item.getPrev() == null || item.getNext() == null) {
                    continue;
                }
                else {
                bw.write(item.getId() + ";" + item.getCipher() + ";" + item.getIV() + ";" +item.getlec() +";" +item.getecr() + ";" + item.getPrev() + ";" + item.getNext() + ";" + item.getDate() + ";" + item.getAuthor());
                bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving file content: " + e.getMessage());
        }
    }

    @Override
    public int getN() {
        return items.size();
    }

    public void printSubscribers() {
        System.out.println("Subscribers: " + subscribers);
    }
}
