package translator;

import command.AddressCommand;
import command.UnAddressCommand;

import java.util.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Translator {

    public static String getBinaryCode(String filePath) {
        Path path = Paths.get(filePath);
        Scanner scanner = null;
        try {
            scanner = new Scanner(path);
        } catch (IOException e) {
            System.out.println("Ошибка доступа к файловой системе");
            return null;
        }

        Map<String, String> variables = new LinkedHashMap<>();
        Map<String, List<String>> executable = new LinkedHashMap<>();

        String currentExecutable = "";
        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();
            if (line.equals(""))
                continue;

            String[] lineArr = line.split(" ");
            if (lineArr[0].getBytes()[lineArr[0].length() - 1] == ':') {
                if (lineArr[1].equals("WORD")) {
                    variables.put(lineArr[0].substring(0, lineArr[0].length() - 1), lineArr[2]);
                } else {
                    currentExecutable = lineArr[0].substring(0, lineArr[0].length() - 1);
                    executable.put(currentExecutable, new LinkedList<>());

                    boolean x = lineArr.length == 2 ? executable.get(currentExecutable).add(lineArr[1]) : executable.get(currentExecutable).add(lineArr[1] + " " + lineArr[2]);
                }
            } else {
                boolean x = lineArr.length == 1 ? executable.get(currentExecutable).add(lineArr[0]) : executable.get(currentExecutable).add(lineArr[0] + " " + lineArr[1]);
            }
        }

        AtomicInteger countInstructionsAtomic = new AtomicInteger();
        executable.forEach((k, v) -> countInstructionsAtomic.addAndGet(v.size()));
        int countInstructions = countInstructionsAtomic.intValue();

        List<String> result = new ArrayList();
        executable.forEach((k, v) -> {
            for (String values : v) {
                if (values.split(" ").length == 1) {
                    for (UnAddressCommand unAddressCommand : UnAddressCommand.values())
                        if (values.split(" ")[0].equals(unAddressCommand.toString()))
                            result.add(unAddressCommand.getCode().replace("_", "") + "\n");
                } else {
                    String addRes = "";

                    for (AddressCommand addressCommand : AddressCommand.values())
                        if (values.split(" ")[0].equals(addressCommand.toString())) {
                            addRes += addressCommand.getCode();

                            if (values.split(" ")[1].matches("[-+]?\\d+")) {
                                addRes += parseIn12bit(values.split(" ")[1]);
                                result.add(addRes + "\n");
                            } else {
                                int numberInMemory = countInstructions;
                                for (Map.Entry<String, String> entry : variables.entrySet()) {
                                    if (entry.getKey().equals(values.split(" ")[1]))
                                        break;
                                    numberInMemory++;
                                }
                                addRes += parseIn12bit(String.valueOf(numberInMemory));
                                result.add(addRes + "\n");
                            }
                        }
                }
            }
        });
        variables.forEach((k, v) -> result.add(parseIn16bit(v) + "\n"));
        return String.join("", result);
    }

    private static String parseIn16bit(String str) {
        String[] strArr = Integer.toBinaryString(Integer.parseInt(str)).split("");

        String[] res = new String[16];
        Arrays.fill(res, "0");

        int difference = 16 - strArr.length;
        for (int i = 0; i < res.length; i++) {
            if (i - difference < 0)
                continue;
            res[i] = strArr[i - difference];
        }
        return Arrays.stream(res).collect(Collectors.joining());
    }

    private static String parseIn12bit(String str) {
        String[] strArr = Integer.toBinaryString(Integer.parseInt(str)).split("");

        String[] res = new String[12];
        Arrays.fill(res, "0");

        int difference = 12 - strArr.length;
        for (int i = 0; i < res.length; i++) {
            if (i - difference < 0)
                continue;
            res[i] = strArr[i - difference];
        }
        return Arrays.stream(res).collect(Collectors.joining());
    }

    private static String parseIn8bit(String str) {
        String[] strArr = Integer.toBinaryString(Integer.parseInt(str)).split("");

        String[] res = new String[8];
        Arrays.fill(res, "0");

        int difference = 8 - strArr.length;
        for (int i = 0; i < res.length; i++) {
            if (i - difference < 0)
                continue;
            res[i] = strArr[i - difference];
        }
        return Arrays.stream(res).collect(Collectors.joining());
    }

    private static String parseIn4bit(String str) {
        String[] strArr = Integer.toBinaryString(Integer.parseInt(str)).split("");

        String[] res = new String[4];
        Arrays.fill(res, "0");

        int difference = 4 - strArr.length;
        for (int i = 0; i < res.length; i++) {
            if (i - difference < 0)
                continue;
            res[i] = strArr[i - difference];
        }
        return Arrays.stream(res).collect(Collectors.joining());
    }
}
