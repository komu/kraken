/*
 *  Copyright 2005-2006 The Wanhack Team
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
package net.wanhack.model;

import java.io.Serializable;

import net.wanhack.model.creature.Sex;
import net.wanhack.utils.RandomUtils;

public final class GameConfiguration implements Serializable {

    private String name = "";
    private Sex sex = RandomUtils.randomEnum(Sex.class);
    private PetType pet = PetType.DORIS;
    private static final long serialVersionUID = 0;
    
    public enum PetType {
        DORIS("Doris"), LASSIE("Lassie");
        
        private final String name;

        private PetType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public String getName() {
        if (name.equals("")) {
            return "Anonymous Coward";
        } else {
            return name;
        }
    }
    
    public void setName(String name) {
        assert name != null : "null name";
        
        this.name = name;
    }
    
    public Sex getSex() {
        return sex;
    }
    
    public void setSex(Sex sex) {
        assert sex != null : "null sex";
        
        this.sex = sex;
    }
 
    public PetType getPet() {
        return pet;
    }
    
    public void setPet(PetType pet) {
        this.pet = pet;
    }
}
