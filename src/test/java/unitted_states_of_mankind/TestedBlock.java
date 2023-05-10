package unitted_states_of_mankind;

import lombok.Data;

@Data
public class TestedBlock {
    public TestedBlock(int index, String data) {
        this.index = index;
        this.data = data;
    }

    public TestedBlock() {
    }

    private int index;
    private String data;
}
