public class Item {
    private String Id ;
    private final String Cipher ;
    private final String IV ;
    private  String prev;
    private  String next;
    private final String date ;
    private final String author ;
    private final int lec ;
    private final int ecr ;

    public Item(String Id, String Cipher, String IV, int lec,int erc, String prev, String next, String date, String author) {
        this.Id = Id;
        this.Cipher = Cipher;
        this.IV = IV;
        this.lec = lec;
        this.ecr = erc;
        this.prev = prev;
        this.next = next;
        this.date = date;
        this.author = author;
    }
    public Item(String Cipher, String IV, int lec,int erc, String prev, String next, String date, String author) {
        this.Cipher = Cipher;
        this.IV = IV;
        this.lec = lec;
        this.ecr = erc;
        this.prev = prev;
        this.next = next;
        this.date = date;
        this.author = author;
    }
    public Item(String[]data,String Id) {
        //data=Author;cipher;date;lec;erc;iv;next;prev
        this.author = data[0];
        this.Cipher = data[1];
        this.date = data[2];
        this.lec = Integer.parseInt(data[3]);
        this.ecr = Integer.parseInt(data[4]);
        this.IV = data[5];
        this.next = data[6];
        this.prev = data[7];
        this.Id=Id;
    }

    public Item( Item item) {
        this.Id = item.getId();
        this.Cipher = item.getCipher();
        this.IV = item.getIV();
        this.lec=item.getlec();
        this.ecr=item.getecr();
        this.prev = item.getPrev();
        this.next = item.getNext();
        this.date = item.getDate();
        this.author = item.getAuthor();
    }
    public Item (String[] data)
    {
        //{link.author},{link.cipher},{link.date},{link.lec},{link.ecr},{link.iv},{link.id},{next_id},{prev_id}
        this.author = data[0];
        this.Cipher = data[1];
        this.date = data[2];
        this.lec = Integer.parseInt(data[3]);
        this.ecr = Integer.parseInt(data[4]);
        this.IV = data[5];
        this.Id = data[6];
        this.next = data[7];
        this.prev = data[8];

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
    public int getlec() {
        return lec;
    }
    public int getecr() {
        return ecr;
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
    public void print() {
        System.out.println("Id: " + Id + " Cipher: " + Cipher + " IV: " + IV + " lec: " + lec + " ecr: " + ecr + " prev: " + prev + " next: " + next + " date: " + date + " author: " + author);
    }
}

