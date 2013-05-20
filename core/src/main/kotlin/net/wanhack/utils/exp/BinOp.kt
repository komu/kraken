/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.utils.exp

enum class BinOp(val op: String) {
    ADD : BinOp("+") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs + rhs
    }
    SUB : BinOp("-") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs - rhs
    }
    MUL : BinOp("*") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs * rhs
    }
    DIV : BinOp("/") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs / rhs
    }
    MOD : BinOp("%") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs % rhs
    }

    abstract fun evaluate(lhs: Int, rhs: Int): Int

    fun toString() = op
}
