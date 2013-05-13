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

final class ExpressionLexer {

    private final String str;
    private int pos = 0;
    private Object currentValue;
    private int oldPos = -1;

    public ExpressionLexer(String str) {
        this.str = str;
    }

    public TokenType next() {
        currentValue = null;
        
        if (!hasNext()) {
            return TokenType.EOF;
        }
        
        oldPos = pos;
        
        char first = str.charAt(pos++);
        switch (first) {
        case '(':   return TokenType.LPAR;
        case ')':   return TokenType.RPAR;
        case '+':   return TokenType.PLUS;
        case '-':   return TokenType.MINUS;
        case '*':   return TokenType.MUL;
        case '/':   return TokenType.DIV;
        case '%':   return TokenType.MOD;
        case ',':   return TokenType.COMMA;
        default:
            pos--;
        }

        int start = pos++;
        
        while (pos < str.length() && isIdentifierChar(str.charAt(pos))) {
            pos++;
        }

        String tok = str.substring(start, pos);
        if (allDigits(tok)) {
            currentValue = new Integer(tok);
            return TokenType.NUMBER;
        } else {
            currentValue = tok;
            return TokenType.IDENTIFIER;
        }
    }
    
    public Object getCurrentValue() {
        return currentValue;
    }
    
    public void pushBack() {
        if (oldPos == -1) {
            throw new IllegalStateException("no tokens read: can't pushback");
        }
        
        pos = oldPos;
    }
    
    private static boolean allDigits(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isIdentifierChar(char ch) {
        return Character.isLetter(ch)
            || Character.isDigit(ch);
    }
    
    public boolean hasNext() {
        skipWhitespace();
        return pos < str.length();
    }

    private void skipWhitespace() {
        while (pos < str.length() && Character.isWhitespace(str.charAt(pos))) {
            pos++;
        }
    }
}
