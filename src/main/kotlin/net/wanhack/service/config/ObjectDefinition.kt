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

package net.wanhack.service.config

import java.awt.Color
import java.util.HashMap
import net.wanhack.model.item.Item
import net.wanhack.model.item.weapon.Weapon
import net.wanhack.utils.ColorFactory
import net.wanhack.utils.exp.Expression
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.beanutils.PropertyUtils
import org.apache.commons.logging.LogFactory

class ObjectDefinition(val name: String, val abstractDefinition: Boolean, val objectFactory: ObjectFactory) {
    val attributes = HashMap<String, Any>()
    var objectClass: Class<*>? = null
        get() = $objectClass ?: parent?.objectClass
        set(objectClass: Class<*>?) = $objectClass = objectClass

    var parent: ObjectDefinition? = null
    var swarmSize = Expression.constant(1)

    var probability: Int? = null
        get() = $probability ?: parent?.probability
        set(probability: Int?) = $probability = probability

    var level: Int? = null
        get() = $level ?: parent?.level
        set(level: Int?) = $level = level

    var maximumInstances = Integer.MAX_VALUE
    var createdInstances = 0
    private val log = LogFactory.getLog(javaClass)

    fun swarmSize(): Int =
        swarmSize.evaluate()

    fun isInstantiable(cl: Class<*>): Boolean {
        val oc = objectClass
        return oc != null && cl.isAssignableFrom(oc) && !abstractDefinition && createdInstances < maximumInstances
    }

    private fun getAttributes(): Map<String, Any> {
        if (parent == null)
            return attributes
        else {
            val result = HashMap<String, Any>(parent!!.getAttributes())
            result.putAll(attributes)
            return result
        }
    }

    public fun createObject(): Any {
        if (abstractDefinition)
            throw ConfigurationException("Can't instantiate abstract definition <$name>")

        try {
            val oc = objectClass ?: throw ConfigurationException("object class not set for <$name>")
            val ctor = oc.getConstructor(javaClass<String>())

            val obj = ctor.newInstance(name)!!
            for ((key, value) in getAttributes())
                setProperty(obj, key, value)

            createdInstances++
            return obj

        } catch (e: Exception) {
            throw ConfigurationException("Can't construct object <" + name + ">", e)
        }
    }

    private fun setProperty(obj: Any, name: String, value: Any) {
        try {
            val propertyType = PropertyUtils.getPropertyType(obj, name)
            if (propertyType == null) {
                log.error("invalid property <$name> for <$obj>")
                return
            }

            BeanUtils.setProperty(obj, name, evaluateValue(propertyType, value))
        } catch (e: ConfigurationException) {
            throw e
        } catch (e: Exception) {
            throw ConfigurationException("Can't initialize object <$obj>", e)
        }
    }

    private fun evaluateValue(propertyType: Class<*>, value: Any?): Any? =
        if (value is String)
            evaluateStringValue(propertyType, value)
        else
            value

    private fun evaluateStringValue(propertyType: Class<*>, exp: String): Any? =
        when {
            propertyType == javaClass<Int>(),
            propertyType == javaClass<Int?>()       -> Expression.evaluate(exp)
            propertyType == javaClass<Boolean>(),
            propertyType == javaClass<Boolean?>()   -> "true".equalsIgnoreCase(exp)
            propertyType == javaClass<Color>()      -> ColorFactory.getColor(exp)
            propertyType == javaClass<Weapon>()     -> objectFactory.create(javaClass<Weapon>(), exp)
            propertyType == javaClass<Item>()       -> objectFactory.create(javaClass<Item>(), exp)
            propertyType == javaClass<Expression>() -> Expression.parse(exp)
            propertyType.isEnum()                   -> enumValue(propertyType, exp)
            else -> exp
        }

    fun toString() = "ObjectDefinition [name=$name]"

    fun enumValue(cl: Class<*>, name: String): Any =
        (cl.getEnumConstants() as Array<Any>).find { (it as Enum<*>).name() == name } ?: throw ConfigurationException("$cl does not have enum constant <$name>")
}
