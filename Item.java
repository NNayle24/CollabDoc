public class Item {
    private String Id ;
    private final String Cipher ;
    private final String IV ;
    private final int lvl;
    private  String prev;
    private  String next;
    private final String date ;
    private final String author ;

    public Item(String Id, String Cipher, String IV, int lvl, String prev, String next, String date, String author) {
        this.Id = Id;
        this.Cipher = Cipher;
        this.IV = IV;
        this.lvl = lvl;
        this.prev = prev;
        this.next = next;
        this.date = date;
        this.author = author;
    }
    public Item(String Cipher, String IV, int lvl, String prev, String next, String date, String author) {
        this.Cipher = Cipher;
        this.IV = IV;
        this.lvl = lvl;
        this.prev = prev;
        this.next = next;
        this.date = date;
        this.author = author;
    }

    public Item( Item item) {
        this.Id = item.getId();
        this.Cipher = item.getCipher();
        this.IV = item.getIV();
        this.lvl = item.getLvl();
        this.prev = item.getPrev();
        this.next = item.getNext();
        this.date = item.getDate();
        this.author = item.getAuthor();
    }
    public String getId() {
        return Id;
    }
    public String getCipher() {
        return Cipher;
    }
    public String getIV() {
        return IV;
    }
    public int getLvl() {
        return lvl;
    }
    public String getPrev() {
        return prev;
    }
    public String getNext() {
        return next;
    }
    public String getDate() {
        return date;
    }
    public String getAuthor() {
        return author;
    }
    public void setNext(String next) {
        this.next = next;
    }
    public void setPrev(String prev) {
        this.prev = prev;
    }
    public void setId(String id) {
        this.Id = id;
    }
}

