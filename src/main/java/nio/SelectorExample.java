package nio;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;

public class SelectorExample {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        // selector.select(); собирает со всех каналов события в очередь

    }

}
