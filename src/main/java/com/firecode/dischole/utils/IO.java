package com.firecode.dischole.utils;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Yasser Ganjisaffar
 * @author JIANG
 */
public class IO {
    private static final Log logger = LogFactory.getLog(IO.class);

    public static boolean deleteFolder(File folder) {
        return deleteFolderContents(folder) && folder.delete();
    }

    public static boolean deleteFolderContents(File folder) {
        logger.debug("Deleting content of: " + folder.getAbsolutePath());
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (!file.delete()) {
                    return false;
                }
            } else {
                if (!deleteFolder(file)) {
                    return false;
                }
            }
        }
        return true;
    }
}