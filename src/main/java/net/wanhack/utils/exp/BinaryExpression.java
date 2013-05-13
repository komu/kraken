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

import java.util.Map;

class BinaryExpression extends Expression {

    private final BinOp op;
    private final Expression lhs;
    private final Expression rhs;

    public BinaryExpression(BinOp op, Expression lhs, Expression rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    @Override
    public int evaluate(Map<String,Integer> env) {
        return op.evaluate(lhs.evaluate(env), rhs.evaluate(env));
    }
    
    @Override
    public String toString() {
        return "(" + lhs + " " + op + " " + rhs + ")";
    }
}
