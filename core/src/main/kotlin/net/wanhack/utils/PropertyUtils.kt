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

package net.wanhack.utils

import java.lang.reflect.Method

object PropertyUtils {

    fun getSettablePropertyType(obj: Any, name: String): Class<*>? =
        findSetter(obj.javaClass, name)?.getParameterTypes()?.get(0)

    fun setProperty(obj: Any, name: String, value: Any?) {
        val setter = findSetter(obj.javaClass, name) ?: throw Exception("unknown property '$name' for ${obj.javaClass}")
        setter.invoke(obj, value)
    }

    private fun findSetter(cl: Class<*>, name: String): Method? {
        val methodName = "set${name.capitalize()}"
        return cl.getMethods().find { m -> m.getName() == methodName && m.getParameterTypes()?.size == 1 }
    }
}
