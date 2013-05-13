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
package net.wanhack.model.region;

import java.io.Serializable;

public final class JumpTarget implements Serializable {

    private final String region;
    private final String location;
    private static final long serialVersionUID = 0;

    public JumpTarget(String region, String location) {
        this.region = region;
        this.location = location;
    }
    
    public boolean isExit() {
        return region.equals("exit");
    }
        
    public String getRegion() {
        return region;
    }
    
    public String getLocation() {
        return location;
    }
    
    @Override
    public String toString() {
        return "JumpTarget [region=" + region + ", location=" + location + "]";
    }
}
