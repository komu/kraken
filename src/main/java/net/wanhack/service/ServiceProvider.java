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
package net.wanhack.service;

import net.wanhack.model.common.Console;
import net.wanhack.service.config.ObjectFactory;
import net.wanhack.service.region.RegionLoader;
import net.wanhack.service.score.HighScoreService;

public final class ServiceProvider {

    // Singletons, bah!
    private static Console console;
    private static ObjectFactory objectFactory;
    private static RegionLoader regionLoader;
    private static HighScoreService highScoreService = new HighScoreService();
    
    public static Console getConsole() {
        if (console == null)
            throw new IllegalStateException("console not set");
            
        return console;
    }
    
    public static void setConsole(Console console) {
        ServiceProvider.console = console;
    }

    public static ObjectFactory getObjectFactory() {
        return objectFactory;
    }
    
    public static void setObjectFactory(ObjectFactory objectFactory) {
        ServiceProvider.objectFactory = objectFactory;
    }

    public static RegionLoader getRegionLoader() {
        return regionLoader;
    }
    
    public static void setRegionLoader(RegionLoader regionLoader) {
        ServiceProvider.regionLoader = regionLoader;
    }

    public static HighScoreService getHighScoreService() {
        return highScoreService;
    }
}
