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
package net.wanhack.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Constructs colors from their string representations.
 */
public class ColorFactory {
    
    private static final Map<String, Color> colorMap = initializeColorMap();
    private static final Log log = LogFactory.getLog(ColorFactory.class);
    
    private ColorFactory() {
    }
    
    private static Map<String, Color> initializeColorMap() {
        Map<String, Color> colorMap = new HashMap<String, Color>();
        
        colorMap.put("black", Color.BLACK);
        colorMap.put("blackish", new Color(10, 10, 10));
        colorMap.put("blue", Color.BLUE);
        colorMap.put("light blue", new Color(100, 100, 255));
        colorMap.put("brown", new Color(100, 100, 0));
        colorMap.put("brownish", new Color(120, 100, 10));
        colorMap.put("light brown", new Color(200, 200, 0));
        colorMap.put("cyan", Color.CYAN);
        colorMap.put("dark gray", Color.DARK_GRAY);
        colorMap.put("dark green", new Color(0, 130, 0));
        colorMap.put("dark grey", Color.DARK_GRAY);
        colorMap.put("gray", Color.GRAY);
        colorMap.put("grey", Color.GRAY);
        colorMap.put("green", new Color(0, 150, 0));
        colorMap.put("light gray", Color.LIGHT_GRAY);
        colorMap.put("light grey", Color.LIGHT_GRAY);
        colorMap.put("magenta", Color.MAGENTA);
        colorMap.put("orange", Color.ORANGE);
        colorMap.put("pink", Color.PINK);
        colorMap.put("red", Color.RED);
        colorMap.put("aluminium", new Color(220, 230, 250));
        colorMap.put("white", Color.WHITE);
        colorMap.put("whiteish", new Color(240, 240, 230));
        colorMap.put("yellow", Color.YELLOW);
        colorMap.put("yellowish", new Color(250, 240, 140));
        
        return colorMap;
    }

    public static Color getColor(String exp) {
        Color color = colorMap.get(exp);
        if (color != null) {
            return color;
        } else {
            log.error("unknown color: <" + exp + ">");
            return Color.BLACK;
        }
    }
}
