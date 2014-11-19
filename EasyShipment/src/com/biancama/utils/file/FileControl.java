package com.biancama.utils.file;

public class FileControl {
    
    private FileControl(){};
    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    public static String renameFileName(String name){
       return name.replaceAll("[" + new String(ILLEGAL_CHARACTERS) + "]", "_");
    }
    
    public static void main(String[] args) {
        String name = "pippo$$#///gfdgdfgdf.pdf";
        
        System.out.println("nuonva stringa: " + FileControl.renameFileName(name));
    }
}
