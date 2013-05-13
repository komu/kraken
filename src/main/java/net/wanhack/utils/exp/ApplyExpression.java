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

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class ApplyExpression extends Expression {

    private final String function;
    private final List<Expression> args;

    public ApplyExpression(String function, List<Expression> args) {
        this.function = function;
        this.args = args;
    }
    
    @Override
    public int evaluate(Map<String,Integer> env) {
        Method method = findMethod();
        
        try {
            return (Integer) method.invoke(null, evaluateArguments(env));
        } catch (Exception e) {
            throw new EvaluationException(e);
        }
    }

    private Object[] evaluateArguments(Map<String,Integer> env) {
        Object[] result = new Object[args.size()];
        
        int n = 0;
        for (Expression exp : args) {
            result[n++] = exp.evaluate(env);
        }
        
        return result;
    }

    private Method findMethod() {
        int argc = args.size();
        
        for (Method method : Functions.class.getMethods()) {
            if (function.equals(method.getName()) 
                    && method.getParameterTypes().length == argc) {
                return method;
            }
        }
        
        throw new EvaluationException("No such function: " + function + "/" + argc);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(function).append("(");
 
        for (Iterator<Expression> it = args.iterator(); it.hasNext(); ) {
            Expression exp = it.next();
            sb.append(exp);
            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(")");
        return sb.toString();
    }
}
