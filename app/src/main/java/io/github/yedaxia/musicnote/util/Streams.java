package io.github.yedaxia.musicnote.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author yeguozhong yedaxia.github.com
 */
public class Streams {

    /**
     * simple read stream to String
     * @param in
     * @return
     * @throws IOException
     */
    public static String streamToString(InputStream in) throws IOException{
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, "utf-8");
        char[] buffer = new char[4096];
        int bytesRead = -1;
        while ((bytesRead = reader.read(buffer)) != -1) {
            stringBuilder.append(buffer, 0, bytesRead);
        }
        reader.close();
        return stringBuilder.toString();
    }

}
