public class CommandLineArguments {

    private static CommandLineArguments obj = null;

    private String mode = "enc";
    private String data = "";
    private String inputFileName = "";
    private String outputFileName = "";
    private String algorithm = "shift";
    private int key = 0;

    private CommandLineArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode" -> mode = args[i + 1];
                case "-key" -> key = Integer.parseInt(args[i + 1]);
                case "-data" -> data = args[i + 1];
                case "-in" -> inputFileName = args[i + 1];
                case "-out" -> outputFileName = args[i + 1];
                case "-alg" -> algorithm = args[i + 1];
            }
        }
    }

    public static CommandLineArguments getCommandState(String[] args) {
        if (obj == null) {
            obj = new CommandLineArguments(args);
        }
        return obj;
    }

    public String getMode() {
        return mode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public int getKey() {
        return key;
    }
}
