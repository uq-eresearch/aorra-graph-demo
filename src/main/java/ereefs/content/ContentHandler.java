package ereefs.content;



public interface ContentHandler {

    Content getContent(String path) throws Exception;

    String getResolvedPath(String path) throws Exception;
}
