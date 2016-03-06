package com.rest.indexer.common;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Created by vijay on 3/5/16.
 */
@Slf4j
public class HelperUtil {

    public static void convertInputToFile(InputStream inputStream, String fileLocation) {
        OutputStream streamOutput = null;
        try {
            streamOutput = new FileOutputStream(fileLocation);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                streamOutput.write(bytes, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //close the streams
        finally {
            try {
                streamOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        } else log.error("file not found at" + filePath);
    }
}
