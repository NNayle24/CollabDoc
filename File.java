import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public final class File extends Observer implements DataStructure {
    private static final int n = 16;
    private final ArrayList<Subscriber> subscribers;
    private final HashMap<String, Item> items;
    private final ArrayList<String> added = new ArrayList<>();
    private final ArrayList<String> removed = new ArrayList<>();

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
        item.setId(IdGenerator());
        items.put(item.getId(), item);
        items.get(item.getPrev()).setNext(item.getId());
        items.get(item.getNext()).setPrev(item.getId());
        added.add(item.getId());
        added.add(item.getPrev());
        added.add(item.getNext());
        notifySubscribers();
    }

    @Override
    public void removeItem(String id) {
        added.add(items.get(id).getPrev());
        added.add(items.get(id).getNext());
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
        this.subscribers = new ArrayList<>();
        Item first = new Item(IdGenerator(), "", "", 0, null, null, "", "");
        Item last = new Item(IdGenerator(), "", "", 0, first.getId(), null, "", "");
        items.put(first.getId(), first);
        items.put(last.getId(), last);
    }

    @Override
    public String[] getAdded() {
        String[] addedArray = new String[added.size()];
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
        subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers() {
        for (Subscriber subscriber : subscribers) {
            subscriber.update();
        }
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
                Item item = new Item(data[0], data[1], data[2], Integer.parseInt(data[3]), data[4], data[5], data[6], data[7]);
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
                bw.write(item.getId() + ";" + item.getCipher() + ";" + item.getIV() + ";" + item.getLvl() + ";" + item.getPrev() + ";" + item.getNext() + ";" + item.getDate() + ";" + item.getAuthor());
                bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving file content: " + e.getMessage());
        }
    }
}
