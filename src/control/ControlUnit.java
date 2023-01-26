package control;

import command.*;

public class ControlUnit {

    //Аккумулятор
    private String accumulator;

    //Счётчик команд
    private String clockCommand;

    //Регистры
    private String bufferRegister;
    private String dataRegister;
    private String commandRegister;

    //Флаги
    private boolean zeroFlag;
    private boolean overflowFlag;
    private boolean negativeFlag;

    //Память
    private String[] memory;
    private String addressRegister;

    //Порты
    private PortBuffer[] ports;

    public ControlUnit() {
        this.accumulator = "0";

        this.clockCommand = "0";

        this.bufferRegister = "0";
        this.dataRegister = "0";
        this.commandRegister = "0";

        this.zeroFlag = false;
        this.overflowFlag = false;
        this.negativeFlag = false;

        this.memory = new String[4096];
        this.addressRegister = "0";

        this.ports = new PortBuffer[5];
    }

    public void downloadProgramInMemory(String binaryCode) {
        this.memory = binaryCode.split("\n");
    }

    public void setDevices(PortBuffer[] buffers) {
        this.ports = buffers;
    }

    private interface Execute {
        public void execute();
    }

    public void start(InstructionLogger instructionLogger) {
        while (true) {
            instructionLogger.addLog("{\n\taccumulator: " + accumulator + "\n\tClockCommand: " + clockCommand + "\n\tBufferRegister: " + bufferRegister +
                    "\n\tDataRegister: "+ dataRegister + "\n\tCommandRegister: " + commandRegister + "\n\tAddressRegister: " + addressRegister +
                    "\n\tZeroFlag: " + zeroFlag + "\n\tOverflowFlag: " + overflowFlag + "\n\tNegativeFlag: " + negativeFlag + "\n}");
            addressRegister = clockCommand;
            bufferRegister = clockCommand;
            //1

            clockCommand = Integer.toBinaryString(Integer.parseInt(bufferRegister, 2) + 1);
            dataRegister = memory[Integer.parseInt(addressRegister, 2)];
            //2

            commandRegister = dataRegister;
            //3

            Execute exCommand = null;
            if (commandRegister.substring(0, 4).equals("0000")) {

                if (commandRegister.equals(UnAddressCommand.HLT.getCode().replaceAll("_", ""))) {
                    instructionLogger.addLog("{\n\taccumulator: " + accumulator + "\n\tClockCommand: " + clockCommand + "\n\tBufferRegister: " + bufferRegister +
                            "\n\tDataRegister: "+ dataRegister + "\n\tCommandRegister: " + commandRegister + "\n\tAddressRegister: " + addressRegister +
                            "\n\tZeroFlag: " + zeroFlag + "\n\tOverflowFlag: " + overflowFlag + "\n\tNegativeFlag: " + negativeFlag + "\n}");
                    return;
                }
                if (commandRegister.equals(UnAddressCommand.CLA.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            accumulator = "0";
                            //4
                        }
                    };
                }
                if (commandRegister.equals(UnAddressCommand.INC.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            accumulator = Integer.toBinaryString(Integer.parseInt(accumulator, 2) + 1);

                            if (accumulator.length() > 16) {
                                accumulator = accumulator.substring(accumulator.length() - 16, accumulator.length());
                                overflowFlag = true;
                            }
                            if (Integer.parseInt(accumulator, 2) == 0)
                                zeroFlag = true;
                            if (accumulator.getBytes()[0] == '1')
                                negativeFlag = true;
                            //4
                        }
                    };
                }
                if (commandRegister.equals(UnAddressCommand.DEC.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            accumulator = Integer.toBinaryString(Integer.parseInt(accumulator, 2) - 1);

                            if (accumulator.length() > 16) {
                                accumulator = accumulator.substring(accumulator.length() - 16, accumulator.length());
                            }
                            if (Integer.parseInt(accumulator, 2) == 0)
                                zeroFlag = true;
                            if (accumulator.getBytes()[0] == '1')
                                negativeFlag = true;
                            //4
                        }
                    };
                }

            } else if (!commandRegister.substring(0, 4).equals("1111")) {

                if (commandRegister.substring(0, 4).equals(AddressCommand.LD.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            addressRegister = commandRegister.substring(commandRegister.length() - 12, commandRegister.length());
                            dataRegister = memory[Integer.parseInt(addressRegister, 2)];
                            //4

                            accumulator = dataRegister;
                            //5
                        }
                    };
                }

                if (commandRegister.substring(0, 4).equals(AddressCommand.ADD.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            addressRegister = commandRegister.substring(commandRegister.length() - 12, commandRegister.length());
                            dataRegister = memory[Integer.parseInt(addressRegister, 2)];
                            //4

                            accumulator = Integer.toBinaryString(Integer.parseInt(accumulator, 2) + Integer.parseInt(dataRegister, 2));
                            if (accumulator.length() > 16) {
                                accumulator = accumulator.substring(accumulator.length() - 16, accumulator.length());
                                overflowFlag = true;
                            }
                            if (Integer.parseInt(accumulator, 2) == 0)
                                zeroFlag = true;
                            if (accumulator.getBytes()[0] == '1')
                                negativeFlag = true;
                            //5
                        }
                    };
                }

                if (commandRegister.substring(0, 4).equals(AddressCommand.CMP.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            addressRegister = commandRegister.substring(commandRegister.length() - 12, commandRegister.length());
                            dataRegister = memory[Integer.parseInt(addressRegister, 2)];
                            //4

                            bufferRegister = Integer.toBinaryString(Integer.parseInt(accumulator, 2) + Integer.parseInt(dataRegister, 2));
                            if (bufferRegister.length() > 16) {
                                bufferRegister = bufferRegister.substring(bufferRegister.length() - 16, bufferRegister.length());
                                overflowFlag = true;
                            }
                            if (Integer.parseInt(bufferRegister, 2) == 0)
                                zeroFlag = true;
                            if (bufferRegister.getBytes()[0] == '1')
                                negativeFlag = true;
                            //5
                        }
                    };
                }

                if (commandRegister.substring(0, 4).equals(AddressCommand.JUMP.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            bufferRegister = commandRegister.substring(commandRegister.length() - 12, commandRegister.length());
                            //4

                            clockCommand = bufferRegister;
                            //5
                        }
                    };
                }

                if (commandRegister.substring(0, 4).equals(AddressCommand.DROP.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            bufferRegister = commandRegister.substring(commandRegister.length() - 12, commandRegister.length());
                            //4

                            ports[Integer.parseInt(bufferRegister, 2)].addData(accumulator);
                            //5
                        }
                    };
                }

            } else {
                if (commandRegister.substring(0, 4).equals(AddressCommand.BEQ.getCode().replaceAll("_", ""))) {
                    exCommand = new Execute() {
                        @Override
                        public void execute() {
                            if (!zeroFlag) {
                                bufferRegister = commandRegister.substring(commandRegister.length() - 12, commandRegister.length());
                                //4

                                clockCommand = Integer.toBinaryString(Integer.parseInt(bufferRegister, 2) + Integer.parseInt(clockCommand, 2));
                                //5
                            }
                        }
                    };
                }
            }
            exCommand.execute();
        }
    }
}
