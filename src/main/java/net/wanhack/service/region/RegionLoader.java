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
package net.wanhack.service.region;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wanhack.model.creature.Creature;
import net.wanhack.model.item.Item;
import net.wanhack.model.region.CellType;
import net.wanhack.model.region.Region;
import net.wanhack.model.region.RegionInfo;
import net.wanhack.model.region.World;
import net.wanhack.service.config.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class RegionLoader {
    
    private static final DirectivePattern START_PATTERN =
        new DirectivePattern(":start [str] [int],[int]");
    
    private static final DirectivePattern CREATURE_PATTERN =
        new DirectivePattern(":creature [int],[int] [str]");
    
    private static final DirectivePattern ITEM_PATTERN =
        new DirectivePattern(":item [int],[int] [str]");
    
    private static final DirectivePattern DOWN_PATTERN =
        new DirectivePattern(":portal down [int],[int] [str] [str]");
    
    private static final DirectivePattern UP_PATTERN =
        new DirectivePattern(":portal up [int],[int] [str] [str]");
    
    private static final DirectivePattern LIGHT_PATTERN =
        new DirectivePattern(":light [int],[int] [int]");
    
    private static final Log log = LogFactory.getLog(RegionLoader.class);
    
    private final ObjectFactory objectFactory;

    public RegionLoader(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }
    
    public Region loadRegion(World world, RegionInfo info) throws RegionLoadingException {
        try {
            return loadRegionImpl(world, info);
        } catch (IOException e) {
            throw new RegionLoadingException(e);
        }
    }
    
    private Region loadRegionImpl(World world, RegionInfo info) throws IOException {
        BufferedReader reader = openReader("/regions/" + info.getId() + ".region");
        try {
            Map<String,String> regionAliases = new HashMap<String,String>();
            if (info.getNext() != null) {
                regionAliases.put("%next", info.getNext().getId());
            }
            if (info.getPrevious() != null) {
                regionAliases.put("%previous", info.getPrevious().getId());
            }
            
            boolean seenDirective = false;
            int y = 0;
            List<String> lines = new ArrayList<String>();
            int rows = 0;
            int cols = 0;
            
            String str;
            while ((str = reader.readLine()) != null) {
                if (!str.startsWith(";") && !(str.equals("") && seenDirective)) {
                    lines.add(str);    
                    if (str.startsWith(":")) {
                        seenDirective = true;
                    } else {
                        rows++;
                        cols = Math.max(cols, str.length());
                    }
                }
            }
            
            Region region = new Region(world, info.getId(), info.getLevel(), cols + 1, rows + 1);
            
            seenDirective = false;
            for (String line : lines) {
                if (line.startsWith(":")) {
                    processDirective(region, line, regionAliases);
                    seenDirective = true;
                } else if (!seenDirective) {
                    for (int x = 0; x < line.length(); x++) {
                        switch (line.charAt(x)) {
                        case '#':
                            region.getCell(x, y).setType(CellType.HALLWAY_FLOOR);
                            break;
                        case '<':
                            region.getCell(x, y).setType(CellType.STAIRS_UP);
                            break;
                        case '>':
                            region.getCell(x, y).setType(CellType.STAIRS_DOWN);
                            break;
                        case ' ':
                            break;
                        default:
                            log.error("unknown tile: " + line.charAt(x));
                        }
                    }
                    y++;
                } else {
                    log.error("invalid line: <" + line + ">");
                }
            }
            
            return region;
            
        } finally {
            reader.close();
        }
    }
    
    private void processDirective(Region region, String line, Map<String,String> regionAliases) {
        String[] tokens = CREATURE_PATTERN.getTokens(line);
        if (tokens != null) {
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);
            String name = tokens[2];
            
            Creature creature = objectFactory.create(Creature.class, name);
            region.addCreature(creature, x, y);
            return;
        }

        tokens = ITEM_PATTERN.getTokens(line);
        if (tokens != null) {
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);
            String name = tokens[2];
            
            Item item = objectFactory.create(Item.class, name);
            region.addItem(x, y, item);
            return;
        }
        
        tokens = DOWN_PATTERN.getTokens(line);
        if (tokens != null) {
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);
            String target = getRegion(tokens[2], regionAliases);
            String location = tokens[3];
            
            region.addPortal(x, y, target, location, false);
            return;
        }

        tokens = UP_PATTERN.getTokens(line);
        if (tokens != null) {
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);
            String target = getRegion(tokens[2], regionAliases);
            String location = tokens[3];
            
            region.addPortal(x, y, target, location, true);
            return;
        }
        
        tokens = START_PATTERN.getTokens(line);
        if (tokens != null) {
            String name = tokens[0];
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            
            region.addStartPoint(name, x, y);
            return;
        }
        
        tokens = LIGHT_PATTERN.getTokens(line);
        if (tokens != null) {
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);
            int lightPower = Integer.parseInt(tokens[2]);
            
            region.getCell(x, y).setLightPower(lightPower);
            return;
        }
        
        log.error("Invalid directive: " + line);
    }
    
    private static String getRegion(String name, Map<String,String> aliases) {
        String id = aliases.get(name);
        if (id != null) {
            return id;
        } else {
            return name;
        }
    }

    private BufferedReader openReader(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        if (in != null) {
            return new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } else {
            throw new FileNotFoundException("classpath:" + path);
        }
    }
}
