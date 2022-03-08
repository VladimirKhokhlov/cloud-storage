package nio;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BufferExamples {

    public static void main(String[] args) {

        ByteBuffer buf = ByteBuffer.allocate(7);

        //    _ _ _ _ _ _ _
        // pos              limit, capacity
        buf.put("Hello".getBytes(StandardCharsets.UTF_8));

        // h e l l o   _ _
        //           pos  limit, capacity
        buf.flip(); // фиксация сообщения
        // limit = pos, pos = 0
        //    h e l l o       _ _
        // pos         limit      capacity

        while (buf.hasRemaining()) {
            byte b = buf.get();
            System.out.print((char) b);
        }
        System.out.println();

        buf.rewind();
        // pos = 0
        while (buf.hasRemaining()) {
            byte b = buf.get();
            System.out.print((char) b);
        }
        System.out.println();
        // buf.clear();
        // pos = 0, limit = capacity

        buf.rewind();
        buf.get();
        buf.get();
        buf.get();
        buf.mark();
        buf.get();
        buf.get();
        buf.reset();
        // pos = mark
        while (buf.hasRemaining()) {
            byte b = buf.get();
            System.out.print((char) b);
        }
        System.out.println();
    }
}