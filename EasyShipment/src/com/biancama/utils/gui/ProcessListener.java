package com.biancama.utils.gui;

public interface ProcessListener {
    /**
     * 
     * @param exec
     *            Source Object
     * @param latestLine
     *            Die zuletzte gelesene zeile. \b chars werden als new line char
     *            angesehen
     * @param totalBuffer
     *            Der complette BUffer (exec.getInputStringBuilder()|
     *            exec.getErrorStringBuilder())
     */
    public void onProcess(Executer exec, String latestLine, DynByteBuffer totalBuffer);

    public void onBufferChanged(Executer exec, DynByteBuffer totalBuffer, int latestReadNum);

}
