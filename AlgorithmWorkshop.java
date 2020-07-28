import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AlgorithmWorkshop implements Algorithms {

    private ALGORITHM_NAME algorithmType;

    private final CommandLineArguments arguments;

    private final int LOW_CASE_UP_LIMIT = 122;
    private final int LOW_CASE_DOWN_LIMIT = 97;
    private final int UP_CASE_UP_LIMIT = 90;
    private final int UP_CASE_DOWN_LIMIT = 65;

    private static INPUT_TYPE inputType;
    private static OUTPUT_TYPE outputType;

    enum INPUT_TYPE {
        FROM_FILE,
        FROM_ARG;
    }

    enum OUTPUT_TYPE {
        IN_FILE,
        IN_CMD;
    }

    private AlgorithmWorkshop(String[] args) {
        arguments = CommandLineArguments.getCommandState(args);

        prepareToCipher();

        switch (arguments.getAlgorithm()) {
            case "shift":
                algorithmType = ALGORITHM_NAME.ALG_SHIFT;
                break;
            case "unicode":
                algorithmType = ALGORITHM_NAME.ALG_UNICODE;
                break;
            default:
                System.out.println("Unknown Algorithm(Constructor): " + arguments.getAlgorithm());
        }
    }

    public static String startCipher(String[] cmdArgs) {
        AlgorithmWorkshop algorithmWorkshop = new AlgorithmWorkshop(cmdArgs);

        String resultCipher = "error";

        switch (algorithmWorkshop.algorithmType) {
            case ALG_UNICODE:
                resultCipher = algorithmWorkshop.cipherUnicode(algorithmWorkshop.arguments.getData(),
                        algorithmWorkshop.arguments.getMode(),
                        algorithmWorkshop.arguments.getKey());
                break;

            case ALG_SHIFT:
                resultCipher = algorithmWorkshop.cipherShift(algorithmWorkshop.arguments.getData(),
                        algorithmWorkshop.arguments.getMode(),
                        algorithmWorkshop.arguments.getKey());
                break;

            default:
                System.out.println("Unknown Algorithm(startCipher)" + algorithmWorkshop.algorithmType);

        }

        if (outputType.equals(OUTPUT_TYPE.IN_FILE)) {

            File file = new File(algorithmWorkshop.arguments.getOutputFileName());
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(resultCipher);
            } catch (IOException e) {
                System.out.println("Cannot open file to write: " + file.getName());
            }

        } else {
            System.out.println(resultCipher);
        }

        return resultCipher;
    }

    @Override
    public String cipherUnicode(String message, String mode, int key) {
        boolean isEncryption = mode.equals("enc"); // else decryption
        StringBuilder mutableStr = new StringBuilder(message);

        if (isEncryption) {

            for (int i = 0; i < mutableStr.length(); i++) {
                mutableStr.setCharAt(i, (char) (mutableStr.charAt(i) + key));
            }

        } else {

            for (int i = 0; i < mutableStr.length(); i++) {
                mutableStr.setCharAt(i, (char) (mutableStr.charAt(i) - key));
            }

        }

        return String.valueOf(mutableStr);
    }

    @Override
    public String cipherShift(String message, String mode, int key) {
        boolean isEncryption = mode.equals("enc"); // else decryption
        StringBuilder mutableStr = new StringBuilder(message);

        if (isEncryption) {

            for (int i = 0; i < mutableStr.length(); i++) {

                char currentSym = mutableStr.charAt(i);

                if (!isLetter(currentSym)) {
                    continue;
                }

                if (isUpCase(currentSym)) {

                    if (currentSym + key > UP_CASE_UP_LIMIT) {
                        mutableStr.setCharAt(i, specialSymEncTransform(currentSym, key, true));
                    } else {
                        mutableStr.setCharAt(i, (char) (currentSym + key));
                    }

                } else if (isLowCase(currentSym)) {

                    if (currentSym + key > LOW_CASE_UP_LIMIT) {
                        mutableStr.setCharAt(i, specialSymEncTransform(currentSym, key, false));
                    } else {
                        mutableStr.setCharAt(i, (char) (currentSym + key));
                    }
                }
            }

        } else {

            for (int i = 0; i < mutableStr.length(); i++) {

                char currentSym = mutableStr.charAt(i);

                if (!isLetter(currentSym)) {
                    continue;
                }

                if (isUpCase(currentSym)) {

                    if (currentSym - key < UP_CASE_DOWN_LIMIT) {
                        mutableStr.setCharAt(i, specialSymDecTransform(currentSym, key, true));
                    } else {
                        mutableStr.setCharAt(i, (char) (currentSym - key));
                    }

                } else if (isLowCase(currentSym)) {

                    if (currentSym - key < LOW_CASE_DOWN_LIMIT) {
                        mutableStr.setCharAt(i, specialSymDecTransform(currentSym, key, false));
                    } else {
                        mutableStr.setCharAt(i, (char) (currentSym - key));
                    }
                }
            }
        }

        return String.valueOf(mutableStr);
    }

    private char specialSymEncTransform(char sym, int key, boolean isUpCase) {

        int up_limit = isUpCase ? UP_CASE_UP_LIMIT : LOW_CASE_UP_LIMIT;
        int down_limit = isUpCase ? UP_CASE_DOWN_LIMIT : LOW_CASE_DOWN_LIMIT;

        int newPos = sym;

        for (int i = key; i > 0; i--) {
            if (newPos == up_limit) newPos = down_limit;
            newPos++;
        }

        return (char) --newPos;
    }

    private char specialSymDecTransform(char sym, int key, boolean isUpCase) {
        int up_limit = isUpCase ? UP_CASE_UP_LIMIT : LOW_CASE_UP_LIMIT;
        int down_limit = isUpCase ? UP_CASE_DOWN_LIMIT : LOW_CASE_DOWN_LIMIT;

        int newPos = sym;

        for (int i = key; i > 0; i--) {
            if (newPos == down_limit) newPos = up_limit;
            newPos--;
        }

        return (char) ++newPos;
    }

    private void prepareToCipher() {
        // If data is not empty
        if (!arguments.getData().equals("")) {
            inputType = INPUT_TYPE.FROM_ARG;

            if (arguments.getOutputFileName().equals("")) {
                outputType = OUTPUT_TYPE.IN_CMD;
            } else {
                outputType = OUTPUT_TYPE.IN_FILE;
            }

        } else if (arguments.getData().equals("") && !arguments.getInputFileName().equals("")) {
            inputType = INPUT_TYPE.FROM_FILE;

            // Data from file
            File file = new File(arguments.getInputFileName());
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) arguments.setData(scanner.nextLine());
            } catch (IOException e) {
                System.out.println("Cannot open file to read: " + arguments.getInputFileName());
            }

            if (arguments.getOutputFileName().equals("")) {
                outputType = OUTPUT_TYPE.IN_CMD;
            } else {
                outputType = OUTPUT_TYPE.IN_FILE;
            }
        }

    }

    private boolean isLetter(char sym) {
        return (sym >= UP_CASE_DOWN_LIMIT && sym <= UP_CASE_UP_LIMIT) ||
                (sym >= LOW_CASE_DOWN_LIMIT && sym <= LOW_CASE_UP_LIMIT);
    }

    private boolean isUpCase(char sym) {
        return (sym >= UP_CASE_DOWN_LIMIT && sym <= UP_CASE_UP_LIMIT);
    }

    private boolean isLowCase(char sym) {
        return (sym >= LOW_CASE_DOWN_LIMIT && sym <= LOW_CASE_UP_LIMIT);
    }
}
