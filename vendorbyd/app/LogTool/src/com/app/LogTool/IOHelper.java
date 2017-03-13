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
 *    ==========       ========      ====================      ==========================
 *    2013-10-04         jiwei         Normandy_C000734             create file
 */
package com.app.LogTool;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * IO close
 *
 * @author jw
 */
public class IOHelper {

    private IOHelper() {
    }



    /**
     * close both InputStream and OutputStream, convert the exception into RuntimeException
     * @param is
     * @param os
     */
    public static void close(InputStream is, OutputStream os) {
        close(is);
        close(os);
    }

    /**
     * close InputStream , convert the exception into RuntimeException
     * @param is
     */
    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * close OutputStream , convert the exception into RuntimeException
     * @param os
     */
    public static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


