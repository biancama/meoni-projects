package com.biancama.config;

public interface ConfigurationListener {
    public void onPreSave(SubConfiguration subConfiguration);

    public void onPostSave(SubConfiguration subConfiguration);
}
