package io.github.yedaxia.musicnote.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/1/10
 */

public class FileUtils {

    /**
     * 删除单个文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除目录中的文件
     *
     * @param dir
     */
    public static void deleteDir(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] listFiles = dir.listFiles();
                for (File f : listFiles) {
                    deleteDir(f);
                }
            } else {
                dir.delete();
            }
        }
    }

    /**
     * 获取整个目录大小
     *
     * @param dir
     * @return 单位是byte
     */
    public static long getDirSize(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] listFiles = dir.listFiles();
                long size = 0;
                for (File f : listFiles) {
                    size += getDirSize(f);
                }
                return size;
            } else {
                return dir.length();
            }
        } else {
            return 0;
        }
    }

    /**
     * 压缩打包多个文件
     *
     * @param zipFile
     * @param files
     * @return
     */
    public static void compressFiles(File zipFile, File[] files) throws IOException {
        if (zipFile == null || ArrayUtils.isEmpty(files)) {
            return;
        }
        ZipOutputStream osZip = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32()));
        int bufferSize = 8192;
        for (File file : files) {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(file.getName());
            osZip.putNextEntry(entry);
            int count;
            byte data[] = new byte[bufferSize];
            while ((count = bis.read(data, 0, bufferSize)) != -1) {
                osZip.write(data, 0, count);
            }
            IOUtils.closeSilently(bis);
        }
        IOUtils.closeSilently(osZip);
    }

    /**
     * 获取文件的后缀
     *
     * @param f
     * @return
     */
    public static String getFileExt(File f) {
        if (f == null) {
            return "";
        }
        String fileName = f.getName();
        return fileName.substring(fileName.indexOf(".") + 1);
    }

    public static File mkDirs(File path){
        if(!path.exists()){
            path.mkdirs();
        }
        return path;
    }
}
