package command;

public enum UnAddressCommand {

    HLT("0000_0001_0000_0000"), //Остановка тактового генератора
    CLA("0000_0010_0000_0000"), //Отчистка аккумулятора

    INC("0000_0111_0000_0001"), //Инкремент => ACC + 1 -> AC
    DEC("0000_0111_0000_0002"); //Декремент => ACC - 1 -> AC

    private String code;

    UnAddressCommand(String code) {
        this.code = code;
    }
    public String getCode(){ return code;}
}
