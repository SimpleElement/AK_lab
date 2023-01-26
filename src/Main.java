import control.ControlUnit;
import control.InstructionLogger;
import control.PortBuffer;
import translator.Translator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        test1(args[0]);
    }

    public static void test1(String filePath) {
        Path path = Paths.get(filePath);
        Scanner scanner = null;
        try {
            scanner = new Scanner(path);
        } catch (IOException e) {
            System.out.println("Ошибка доступа к файловой системе");
            return;
        }

        System.out.println("----------CODE----------\n");
        while (scanner.hasNext()) {
            System.out.println(scanner.nextLine());
        }
        System.out.println("\n----------CODE----------\n");

        System.out.println("----------BYTECODE----------\n");
        String byteCode = Translator.getBinaryCode(filePath);

        System.out.println(byteCode);
        System.out.println("----------BYTECODE----------\n");

        System.out.println("----------ProcessorStart----------\n");

        ControlUnit controlUnit = new ControlUnit();
        PortBuffer portBuffer = new PortBuffer();
        InstructionLogger instructionLogger = new InstructionLogger();

        controlUnit.downloadProgramInMemory(byteCode);
        controlUnit.setDevices(new PortBuffer[]{ portBuffer });

        controlUnit.start(instructionLogger);

        System.out.println(instructionLogger.getLogData());
        System.out.println("\n----------ProcessorStart----------\n");

        System.out.println("\n----------PortData----------\n");
        System.out.println("В числовом представлении: ");
        for (String el: portBuffer.getBufferData())
            System.out.print(el);
        System.out.println();
        System.out.println("\nВ виде utf-8:");
        char[] chars = new char[portBuffer.getBufferData().size()];
        for (int i = 0; i < chars.length; i++)
            chars[i] = (char) Integer.parseInt(portBuffer.getBufferData().get(i));
        System.out.println(new String(chars));
        System.out.println("\n----------PortData----------\n");
    }
}
