package com.example.vmi.util;


import java.io.*;
import java.util.Properties;

/**
 * Created by razamd on 1/31/2017.
 */
public class PropUtils {

    private static PropUtils INSTANCE;

    private PropUtils() {

    }

    public static PropUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PropUtils();
        }
        return INSTANCE;
    }

    public Properties getProps(File file) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            props.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is == null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }

    public Properties getProps(InputStream is) {
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    public String getProperty(File file, String key, String defaultValue) {
        Properties props = getProps(file);
        return props.getProperty(key, defaultValue);
    }

    public String getProperty(InputStream is, String key, String defaultValue) {
        Properties props = getProps(is);
        return props.getProperty(key, defaultValue);
    }

    public void setProperty(File file, String key, String value) {
        Properties prop = getProps(file);
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            prop.setProperty(key, value);
            prop.store(output, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
