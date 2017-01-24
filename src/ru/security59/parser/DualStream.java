package ru.security59.parser;

import java.io.PrintStream;

class DualStream extends PrintStream {

    private PrintStream out;

    DualStream(PrintStream out1, PrintStream out2) {
        super(out1);
        this.out = out2;
    }

    public void write(byte buf[], int off, int len) {
        try {
            super.write(buf, off, len);
            out.write(buf, off, len);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        super.flush();
        out.flush();
    }
}
