package io.github.yedaxia.musicnote.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * I/O相关的实用方法
 * @author Darcy
 * Email: yeguozhong@yeah.com
 */
public class IOUtils {

    /**
     * 关闭流
     * @param stream
     */
    public static void closeSilently(Closeable stream){
        if(stream != null){
            try{
                stream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
