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
package net.wanhack.utils.exp;

final class Token {

    private final TokenType type;
    private final Object value;

    public Token(TokenType type) {
        this.type = type;
        this.value = null;
    }
    
    public Token(TokenType type, Object value) {
        this.type = type;
        this.value = value;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public Object getValue() {
        return value;
    }
}
