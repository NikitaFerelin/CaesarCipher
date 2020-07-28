public interface Algorithms {

    enum ALGORITHM_NAME {
        ALG_UNICODE,
        ALG_SHIFT
    }

    String cipherUnicode(String message, String mode, int key);
    String cipherShift(String message, String mode, int key);
}
