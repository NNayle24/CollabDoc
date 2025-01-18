public class Utilities {
    public static String[] decapsulate(String message) {
        return message.split(",");
    }
    public static String encapsulate(String[] message) {
        String result = message[0] ;
        for (int i = 1; i < message.length-1; i++) {
            result += message[i] + ",";
        }
        result += message[message.length-1];
        return result;
    }
    public static String encapsulate(String[] message, String separator) {
        String result = message[0] ;
        for (int i = 1; i < message.length-1; i++) {
            result += message[i] + separator;
            }
        result += message[message.length-1];
        return result;
    }
    public static String AddLibelle(String[] message, String libelle) {
        String[] result = new String[message.length + 1];
        result[0] = libelle;
        for (int i = 0; i < message.length; i++) {
            result[i + 1] = message[i];
        }
        return Utilities.encapsulate(result);
    }
    public static String[] RmLibelle(String[] message) {
        String[] result = new String[message.length - 1];
        for (int i = 1; i < message.length; i++) {
            result[i - 1] = message[i];
        }
        return result;
    }
    public static String[] ItemtoData(Item item) {
        String[] data = new String[9];
        data[0] = item.getAuthor();
        data[1] = item.getCipher();
        data[2] = item.getDate();
        data[3] = Integer.toString(item.getlec());
        data[4] = Integer.toString(item.getecr());
        data[5] = item.getIV();
        data[6] = item.getId();
        data[7] = item.getNext();
        data[8] = item.getPrev();
        return data;
        }
        public static String ExtractToken(String message[]) {
        return message[0].split(":")[1];
    }
}
