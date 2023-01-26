package command;

public enum AddressCommand {

    LD("0001"), //Загрузка в аккумулятор => MEM(x) -> ACC
    ADD("0010"), //Сложение => ACC + MEM(X)
    CMP("0011"), //Сравнение => Установить флаги по результату ACC - MEM(X)
    JUMP("0100"), //Прыжок => X -> CC
    DROP("0101"), //Отправка => ACC -> DEVICE(X)

    BEQ("0110"); //Переход, если минус => IF N==1 THEN CC + X + 1 -> CC

    private String code;

    AddressCommand(String code) {
        this.code = code;
    }
    public String getCode(){ return code;}
}
