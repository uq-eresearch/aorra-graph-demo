package ereefs.content;

import java.io.OutputStream;

public interface Content {

    String getContentType();

    void write(OutputStream out) throws Exception;

}
