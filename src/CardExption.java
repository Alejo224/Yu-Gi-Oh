public class CardExption extends RuntimeException {
    public CardExption(String message) {
        super(message);
    }

    public CardExption(String message, Throwable causa) {
        super(message, causa);
    }
}
