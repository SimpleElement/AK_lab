package control;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PortBuffer {
    private List<String> strs;

    public PortBuffer() {
        this.strs = new ArrayList<>();
    }

    public void addData(String str) {
        this.strs.add(String.valueOf(Integer.parseInt(str, 2)));
    }

    public List<String> getBufferData() {
        return strs;
    }
}
