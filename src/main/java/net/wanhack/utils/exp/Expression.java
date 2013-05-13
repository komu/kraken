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

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public abstract class Expression implements Serializable {
    
    protected static final long serialVersionUID = 0;
    
    public final int evaluate() {
        return evaluate(Collections.<String,Integer>emptyMap());
    }
    
    public final int evaluate(String name, int value) {
        return evaluate(Collections.singletonMap(name, value));
    }
    
    public abstract int evaluate(Map<String,Integer> env);
    
    // force subclasses to override toString
    @Override
    public abstract String toString();
    
    public static int evaluate(String exp) {
        return parse(exp).evaluate();
    }

    public static int evaluate(String exp, String name, int value) {
        return parse(exp).evaluate(name, value);
    }
    
    public static int evaluate(String exp, Map<String, Integer> env) {
        return parse(exp).evaluate(env);
    }
    
    public static Expression parse(String exp) {
        return new ExpressionParser(exp).parse();
    }

    public static Expression constant(int value) {
        return new ConstantExpression(value);
    }
}
