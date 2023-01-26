package control;

import java.util.ArrayList;
import java.util.List;

public class InstructionLogger {
    private List<String> strs;

    public InstructionLogger() {
        strs = new ArrayList<>();
    }

    public void addLog(String str) {
        strs.add(str);
    }

    public String getLogData() {
        return String.join("\n", this.strs);
    }
}
