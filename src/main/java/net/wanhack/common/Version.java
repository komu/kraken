/*
 *  Copyright 2005 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {

    private static Version instance = null;
    private final String version;
    private final String revision;
    
    private Version(String version, String revision) {
        this.version = version;
        this.revision = revision;
    }
    
    public static String getFullVersion() {
        Version version = getInstance();
        if (version.revision != null && !"".equals(version.revision)) {
            return version.version + " (r" + version.revision + ")";
        } else {
            return version.version;
        }
    }
    
    public static String getVersion() {
        return getInstance().version;
    }
    
    public static String getRevision() {
        return getInstance().revision;
    }
    
    private synchronized static Version getInstance() {
        try {
            if (instance == null) {
                instance = loadVersion();
            }
            return instance;
        } catch (IOException e) {
            return new Version("unknown", "");
        }
    }

    private static Version loadVersion() throws IOException {
        InputStream in = openResource("/version.properties");
        try {
            Properties properties = new Properties();
            properties.load(in);
            
            String version = properties.getProperty("version", "unknown");
            String revision = properties.getProperty("revision", "");
            
            return new Version(version, revision);
        } finally {
            in.close();
        }
    }
    
    private static InputStream openResource(String name) 
            throws FileNotFoundException {
        InputStream in = Version.class.getResourceAsStream(name);
        if (in != null) {
            return in;
        } else {
            throw new FileNotFoundException("resource:" + name);
        }
    }
}
