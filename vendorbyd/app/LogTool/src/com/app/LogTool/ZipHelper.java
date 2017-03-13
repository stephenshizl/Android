/* * Copyright (C) BYD 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * BYD Corporation and its licensors retain all intellectual property
 * and proprietary rights in and to this software, related documentation
 * and any modifications thereto.  Any use, reproduction, disclosure or
 * distribution of this software and related documentation without an express
 * license agreement from BYD Corporation is strictly prohibited.
 *
 *
 * General Description: auto SMS
 *
 *
 *        Date           Author           CR/PR                          Reference
 *    ==========       ========      ====================         =============================
 *    2013-10-04         jiwei         Normandy_C000734             create file for compress
 */
package com.app.LogTool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;
import android.util.Log;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
* Compression and decompression tools for the specified file or directory
*
* @author jw
*/
public class ZipHelper {

    static final String TAG = "ZipHelper";

    private ZipHelper() {
    }

    /**
     * compress the specified source file into the target file, use zip format
     * @param src
     *           the source file which will be compressing.
     * @param dest
     *           Compressed file.
     */
    public static void zipFile(File src, File dest) throws RuntimeException {
        BufferedInputStream bis = null;
        ZipOutputStream zos = null;
        byte[] b = new byte[8192];

        try {
            bis = new BufferedInputStream(new FileInputStream(src));//create a buffered stream of bytes which used to read data.
            zos = new ZipOutputStream(new FileOutputStream(dest)); // create compressed output stream
            //an entry of a compressed file is a compressed file.
            zos.putNextEntry(new ZipEntry(src.getName())); // begin write a new zip.

            for (int len = 0; (len = bis.read(b)) != -1;) {
                zos.write(b, 0, len);
            }

            zos.flush(); // flush OutputStream.
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOHelper.close(bis, zos);
        }
    }

    /**
     * compress all sub-directories and sub-files under the specified directory into
     * the specified target file, target file name is "srcDir.zip".
     *
     * @param srcDir
     * @param destBasePath
     */
    public static void zipDir(File srcDir, File destBasePath) {
        zipDir(srcDir, destBasePath, null);
    }

    /**
     * compress all sub-directories and sub-files under the specified directory into
     * the specified target file(zip format).
     *
     * @param srcDir
     *            the source directory which will be Compression.
     * @param destBasePath
     *           the directory which store the zip file.
     * @param destFileName
     *           target file name. if not provide, target file name is "srcDir.zip".
     */
    public static void zipDir(File srcDir, File destBasePath,
                              String destFileName) throws RuntimeException {
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return;
        }

        byte[] b = new byte[8192];
        ZipOutputStream zos = null;
        BufferedInputStream bis = null;
        boolean isSucess = false;

        try {
            if (null == destFileName || "".equals(destFileName)) {
                destFileName = srcDir.getName() + ".zip";
            }

            if(!destBasePath.exists())
                destBasePath.mkdirs();

            zos = new ZipOutputStream(new FileOutputStream(new File(
                                          destBasePath, destFileName)));
            List<File> subFiles = new ArrayList<File>();
            getAllSubFiles(srcDir, subFiles);

            if (subFiles.size() > 0) { // has sub-file
                Log.d(TAG,"have subFiles");

                for (File file : subFiles) { // compress sub file
                    if (!file.canRead()) {
                        Log.d(TAG, "can't read file name : " + file.getName());
                        continue;
                    }

                    bis = new BufferedInputStream(new FileInputStream(file));
                    //add an entry into zip
                    String zipEntryName = file.getAbsolutePath();
                    Log.d(TAG,"zipEntryName = " + zipEntryName);
                    // remove the path of parent directory
                    File parent = srcDir.getParentFile(); // get the parent directory of the specified directory

                    if (parent != null) {
                        String parentName = parent.getAbsolutePath();
                        zipEntryName = zipEntryName.substring(zipEntryName
                                                              .indexOf(parentName)
                                                              + parentName.length() + 1);
                    }

                    zos.putNextEntry(new ZipEntry(zipEntryName));

                    for (int len = 0; (len = bis.read(b)) != -1;) {
                        zos.write(b, 0, len);
                    }

                    zos.flush();
                    bis.close();
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Log.d(TAG,"no have subFiles");
            IOHelper.close(zos);
        }
    }

    /**
     *decompression the specified file.
     *
     * @param src
     *            the source file which will be decompressing.
     * @param destBasePath
     *            the directory which store the specified files.
     */
    public static void unZip(File src, File destBasePath)
    throws RuntimeException {
        ZipInputStream zis = null;
        BufferedOutputStream bos = null;
        byte[] b = new byte[8192];

        try {
            zis = new ZipInputStream(new FileInputStream(src));

            // get each entry from the compression file
            for (java.util.zip.ZipEntry ze = null; (ze = zis.getNextEntry()) != null;) {
                File file = new File(destBasePath, ze.getName());
                File parentFile = file.getParentFile();

                if (!parentFile.exists()) { // if the parent directory is not exist, create it.
                    file.getParentFile().mkdirs();
                }

                // creat a output stream.
                bos = new BufferedOutputStream(new FileOutputStream(file));

                for (int len = 0; (len = zis.read(b)) != -1;) {
                    bos.write(b, 0, len);
                }

                bos.flush();
                bos.close();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOHelper.close(zis);
        }
    }

    /**
     * Recursive gets all descendants file under the specified directory
     *
     * @param baseDir
     *            To recursive directory
     * @param allSubFiles
     *            List all files stored in children
     */
    public static void getAllSubFiles(File baseDir, List<File> allSubFiles) {
        if (baseDir.exists()) {
            if (baseDir.isDirectory()) {
                File[] subFiles = baseDir.listFiles(); //get all sub-directory and sub-files
                int len = subFiles == null ? 0 : subFiles.length;

                for (int i = 0; i < len; i++) {
                    getAllSubFiles(subFiles[i], allSubFiles); // recursive invocation
                }
            } else {
                allSubFiles.add(baseDir);
            }
        }
    }
}

