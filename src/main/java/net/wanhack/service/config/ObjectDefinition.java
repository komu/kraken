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

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import net.wanhack.model.item.Item;
import net.wanhack.model.item.weapon.Weapon;
import net.wanhack.utils.ColorFactory;
import net.wanhack.utils.exp.Expression;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ObjectDefinition {
    
    private final String name;
    private final boolean abstractDefinition;
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private Class<?> objectClass;
    private ObjectDefinition parent;
    private final ObjectFactory objectFactory;
    private Expression swarmSize = Expression.constant(1);
    private Integer probability = null;
    private Integer level = null;
    private int maximumInstances = Integer.MAX_VALUE;
    private int createdInstances = 0;
    private final Log log = LogFactory.getLog(getClass());
    
    public ObjectDefinition(String name, 
                            boolean abstractDefinition,
                            ObjectFactory objectFactory) {
        this.name = name;
        this.abstractDefinition = abstractDefinition;
        this.objectFactory = objectFactory;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public Integer getLevel() {
        for (ObjectDefinition d = this; d != null; d = d.parent) {
            if (d.level != null) {
                return d.level;
            }
        }
        
        return null;
    }
    
    public int swarmSize() {
        return swarmSize.evaluate();
    }
    
    public void setSwarmSize(Expression swarmSize) {
        this.swarmSize = swarmSize;
    }

    public int getMaximumInstances() {
        return maximumInstances;
    }
    
    public void setMaximumInstances(int maximumInstances) {
        this.maximumInstances = maximumInstances;
    }
    
    public int getProbability() {
        for (ObjectDefinition d = this; d != null; d = d.parent) {
            if (d.probability != null) {
                return d.probability;
            }
        }
        
        return 100;
    }
    
    public void setProbability(Integer probability) {
        this.probability = probability;
    }
    
    public boolean isAbstractDefinition() {
        return abstractDefinition;
    }

    public String getName() {
        return name;
    }
    
    public void setParent(ObjectDefinition parent) {
        this.parent = parent;
    }
    
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    public void setObjectClass(Class objectClass) {
        this.objectClass = objectClass;
    }
    
    public Class getObjectClass() {
        for (ObjectDefinition d = this; d != null; d = d.parent) {
            if (d.objectClass != null) {
                return d.objectClass;
            }
        }
        
        throw new ConfigurationException(
                "Can't construct <" + name + ">, objectClass not set.");
    }
    
    public boolean isInstantiable(Class<?> cl) {
        return cl.isAssignableFrom(getObjectClass()) 
            && !isAbstractDefinition()
            && createdInstances < maximumInstances;
    }
    
    private Map<String, Object> getAttributes() {
        if (parent == null) {
            return attributes;
        } else {
            Map<String, Object> result = 
                new HashMap<String, Object>(parent.getAttributes());
            result.putAll(attributes);
            return result;
        }
    }
    
    public Object createObject() {
        if (abstractDefinition) throw new ConfigurationException(
                "Can't instantiate abstract definition <" + name + ">");
        
        try {
            Constructor<?> ctor = getObjectClass().getConstructor(String.class); 
            Object object = ctor.newInstance(name);
            
            for (Map.Entry<String, Object> entry : getAttributes().entrySet()) {
                setProperty(object, entry.getKey(), entry.getValue());
            }
            
            createdInstances++;
            
            return object;

        } catch (Exception e) {
            throw new ConfigurationException(
                    "Can't construct object <" + name + ">", e);
        }
    }
    
    private void setProperty(Object object, String name, Object value) {
        try {
            Class type = PropertyUtils.getPropertyType(object, name);
            if (type == null) {
                log.error("invalid property <" + name + "> for <" + object + ">");
                return;
            }
            
            BeanUtils.setProperty(object, name, evaluateValue(type, value));
            
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException(
                    "Can't initialize object <" + object + ">", e);
        }
    }
    
    private Object evaluateValue(Class<?> type, Object value) {
        if (value instanceof String) {
            return evaluateStringValue(type, (String) value);
        } else {
            return value;
        }
    }
    
    private Object evaluateStringValue(Class<?> type, String exp) {
        if (type == int.class || type == Integer.class) {
            return Expression.evaluate(exp);
        } else if (type == boolean.class || type == Boolean.class) {
            return "true".equalsIgnoreCase(exp);
        } else if (type == Color.class) {
            return ColorFactory.getColor(exp);
        } else if (type == Weapon.class) {
            return objectFactory.create(Weapon.class, exp);
        } else if (type == Item.class) {
            return objectFactory.create(Item.class, exp);
        } else if (type == Expression.class) {
            return Expression.parse(exp);
        } else if (type.isEnum()) {
            return enumValue(type, exp);
        } else {
            return exp;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static Object enumValue(Class cl, String name) {
        return Enum.valueOf(cl, name);
    }

    @Override
    public String toString() {
        return "ObjectDefinition [name=" + name + "]";
    }
}
