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

import net.wanhack.model.item.weapon.NaturalWeapon
import net.wanhack.utils.exp.Expression
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import javax.xml.parsers.SAXParserFactory
import java.io.FileNotFoundException
import java.util.HashMap

class ObjectFactory {
    private val definitions = HashMap<String, ObjectDefinition>()

    fun parse(definitionFile: String, element: String): Unit {
        open(definitionFile).use { inputStream ->
            val parserFactory = SAXParserFactory.newInstance()!!
            parserFactory.setNamespaceAware(true)

            val parser = parserFactory.newSAXParser()!!
            parser.parse(inputStream, MySAXHandler(element))
        }
    }

    private fun open(file: String) =
        javaClass.getResourceAsStream(file) ?: throw FileNotFoundException("classpath:$file")

    fun create<T>(objectClass: Class<T>, name: String): T {
        val def = getDefinition(name)
        return objectClass.cast(def.createObject())!!
    }

    fun getAvailableDefinitionsForClass<T>(cl: Class<T>): List<ObjectDefinition> =
        definitions.values().filter { it.isInstantiable(cl) }

    private fun getDefinition(name: String) =
        definitions[name] ?: throw ConfigurationException("No such object <$name>")

    fun toString() = definitions.toString()

    private inner class MySAXHandler(val element: String): DefaultHandler() {

        private var definition: ObjectDefinition? = null

        override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes?) {
            when (localName) {
                element          -> startDefinition(attributes!!)
                "attributes"     -> startAttributes(attributes!!)
                "natural-weapon" -> startNaturalWeapon(attributes!!)
                "definitions"    -> { }
                else             -> throw SAXException("Unknown tag: $localName")
            }
        }

        override fun endElement(uri: String?, localName: String?, qName: String) {
            if (localName == element)
                endDefinition()
        }

        private fun startDefinition(attributes: Attributes): Unit {
            val name = attributes.getValue("name")!!
            val className = attributes.getValue("class")
            val parent = attributes.getValue("parent")
            val isAbstract = attributes.getValue("abstract") == "true"
            val probability = attributes.getValue("probability")
            val level = attributes.getValue("level")
            val maximumInstances  = attributes.getValue("maximumInstances")
            val swarmSize = attributes.getValue("swarmSize")

            val definition = ObjectDefinition(name, isAbstract, this@ObjectFactory)
            if (className != null)
                definition.objectClass = getClassForName(className)

            if (parent != null)
                definition.parent = getDefinition(parent)

            if (probability != null)
                definition.probability = probability.toInt()

            if (level != null)
                definition.level = level.toInt()

            if (maximumInstances != null)
                definition.maximumInstances = maximumInstances.toInt()

            if (swarmSize != null)
                definition.swarmSize = Expression.parse(swarmSize)

            this.definition = definition
        }

        private fun endDefinition() {
            definitions[definition!!.name] = definition!!
            definition = null
        }

        private fun startAttributes(attributes: Attributes) {
            for (i in 0..attributes.getLength() - 1) {
                val name = attributes.getLocalName(i)!!
                val value = attributes.getValue(i)!!

                definition!!.attributes[name] = value
            }
        }

        private fun startNaturalWeapon(attributes: Attributes) {
            val hit = attributes.getValue("verb") ?: "hit"
            val toHit = attributes.getValue("toHit") ?: "0"
            val damage = getAttribute(attributes, "damage", "randint(1,3)")

            definition!!.attributes["naturalWeapon"] = NaturalWeapon(hit, toHit, damage)
        }

        private fun getAttribute(attributes: Attributes, name: String, def: String) =
            attributes.getValue(name) ?: def

        private fun getClassForName(name: String): Class<out Any?> {
            try {
                return Class.forName(name)
            } catch (e: ClassNotFoundException) {
                throw ConfigurationException("No such class: $name")
            }
        }
    }
}
