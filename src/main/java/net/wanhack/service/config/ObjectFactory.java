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
package net.wanhack.service.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.wanhack.model.common.Attack;
import net.wanhack.model.item.weapon.NaturalWeapon;
import net.wanhack.utils.exp.Expression;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ObjectFactory {
    
    private final Map<String, ObjectDefinition> definitions =
        new HashMap<String, ObjectDefinition>();
    
    public void parse(String definitionFile, 
                      Class<?> objectClass, 
                      String element) throws Exception {
        InputStream in = open(definitionFile);
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            SAXParser parser = parserFactory.newSAXParser();
            parser.parse(in, new MySAXHandler(element));
        } finally {
            in.close();
        }
    }
    
    private InputStream open(String file) throws FileNotFoundException {
        InputStream in = getClass().getResourceAsStream(file);
        if (in != null) {
            return in;
        } else {
            throw new FileNotFoundException("classpath:" + file);
        }
    }
    
    public <T> T create(Class<T> objectClass, String name) {
        ObjectDefinition def = getDefinition(name);
        return objectClass.cast(def.createObject());
    }
    
    /**
     * Returns the definitions for given type so that the definitions
     * are still available (i.e. no limits have been exceeded).
     */
    public <T> List<ObjectDefinition> getAvailableDefinitionsForClass(Class<T> cl) {
        List<ObjectDefinition> result = new ArrayList<ObjectDefinition>();
        
        for (ObjectDefinition def : definitions.values()) {
            if (def.isInstantiable(cl)) {
                result.add(def);
            }
        }
        
        return result;
    }
    
    private ObjectDefinition getDefinition(String name) {
        ObjectDefinition definition = definitions.get(name);
        if (definition != null) {
            return definition;
        } else {
            throw new ConfigurationException("No such object <" + name + ">");
        }
    }
    
    @Override
    public String toString() {
        return definitions.toString();
    }
    
    private class MySAXHandler extends DefaultHandler {
        
        private final String element;
        private ObjectDefinition definition;
        
        public MySAXHandler(String element) {
            this.element = element;
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals(element)) {
                startDefinition(attributes);
                
            } else if (localName.equals("attributes")) {
                startAttributes(attributes);
                
            } else if (localName.equals("natural-weapon")) {
                startNaturalWeapon(attributes);
                
            } else if (!localName.equals("definitions")) {
                throw new SAXException("Unknown tag: " + localName);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equals(element)) {
                endDefinition();
            }
        }
        
        private void startDefinition(Attributes attributes) {
            String name = attributes.getValue("name");
            String className = attributes.getValue("class");
            String parent = attributes.getValue("parent");
            boolean isAbstract = "true".equals(attributes.getValue("abstract"));
            String probability = attributes.getValue("probability");
            String level = attributes.getValue("level");
            String maximumInstances = attributes.getValue("maximumInstances");
            String swarmSize = attributes.getValue("swarmSize");
            
            definition = new ObjectDefinition(name, isAbstract, ObjectFactory.this);
            if (className != null) {
                definition.setObjectClass(getClassForName(className));
            }
            
            if (parent != null) {
                definition.setParent(getDefinition(parent));
            }
            
            if (probability != null) {
                definition.setProbability(new Integer(probability));
            }
            
            if (level != null) {
                definition.setLevel(new Integer(level));
            }

            if (maximumInstances != null) {
                definition.setMaximumInstances(Integer.parseInt(maximumInstances));
            }
            
            if (swarmSize != null) {
                definition.setSwarmSize(Expression.parse(swarmSize));
            }
        }
        
        private void endDefinition() {
            assert definition != null : "null creature";
            
            definitions.put(definition.getName(), definition);
            definition = null;
        }
        
        private void startAttributes(Attributes attributes) {
            assert definition != null : "null definition";
            
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getLocalName(i);
                String value = attributes.getValue(i);
                
                definition.setAttribute(name, value);
            }
        }
        
        
        private void startNaturalWeapon(Attributes attributes) {
            assert definition != null : "null definition";

            String hit = getAttribute(attributes, "verb", "hit");
            String toHit = getAttribute(attributes, "toHit", "0");
            String damage = getAttribute(attributes, "damage", "randint(1,3)");
            
            Attack naturalWeapon = new NaturalWeapon(hit, toHit, damage);
            definition.setAttribute("naturalWeapon", naturalWeapon);
        }
        
        private String getAttribute(Attributes attributes, String name, String def) {
            String value = attributes.getValue(name);
            if (value != null) {
                return value;
            } else {
                return def;
            }
        }
        
        private Class<? extends Object> getClassForName(String name) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException("No such class: " + name);
            }
        }
    }
}
