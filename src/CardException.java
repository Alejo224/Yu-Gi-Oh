public class CardException extends Exception {
    public CardException(String message) {
        super(message);
    }

    public CardException(String message, Throwable causa) {
        super(message, causa);
    }
}
