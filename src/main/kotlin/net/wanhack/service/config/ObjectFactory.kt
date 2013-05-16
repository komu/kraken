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

    fun parse(definitionFile: String): Unit {
        open(definitionFile).use { inputStream ->
            val parserFactory = SAXParserFactory.newInstance()!!
            parserFactory.setNamespaceAware(true)

            val parser = parserFactory.newSAXParser()!!
            parser.parse(inputStream, MySAXHandler())
        }
    }

    fun addDefinition(definition: ObjectDefinition) {
        definitions[definition.name] = definition
    }

    private fun open(file: String) =
        javaClass.getResourceAsStream(file) ?: throw FileNotFoundException("classpath:$file")

    fun create<T>(objectClass: Class<T>, name: String): T =
        objectClass.cast(getDefinition(name).createObject())!!

    fun getAvailableDefinitionsForClass<T>(cl: Class<T>): List<ObjectDefinition> =
        definitions.values().filter { it.isInstantiable(cl) }

    private fun getDefinition(name: String) =
        definitions[name] ?: throw ConfigurationException("No such object <$name>")

    fun toString() = definitions.toString()

    private inner class MySAXHandler: DefaultHandler() {

        private var currentDefinition: ObjectDefinition? = null

        override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes?) {
            attributes!!
            when (localName) {
                "definitions"      -> { }
                "creature", "item" -> currentDefinition = startDefinition(attributes)
                "attributes"       -> currentDefinition!!.addAttributes(attributes)
                "natural-weapon"   -> currentDefinition!!.addNaturalWeapon(attributes)
                else               -> throw SAXException("Unknown tag: $localName")
            }
        }

        override fun endElement(uri: String?, localName: String?, qName: String) {
            if (localName == "creature" || localName == "item") {
                addDefinition(currentDefinition!!)
                currentDefinition = null
            }
        }

        private fun startDefinition(attributes: Attributes): ObjectDefinition {
            val name = attributes["name"]!!
            val definition = ObjectDefinition(name, this@ObjectFactory)

            definition.isAbstract  = attributes["abstract"]?.toBoolean() ?: false
            definition.objectClass = attributes["class"]?.toClass()
            definition.probability = attributes["probability"]?.toInt()
            definition.level       = attributes["level"]?.toInt()

            val parent            = attributes["parent"]
            if (parent != null)
                definition.parent = getDefinition(parent)

            val maximumInstances = attributes["maximumInstances"]?.toInt()
            if (maximumInstances != null)
                definition.maximumInstances = maximumInstances

            val swarmSize = attributes["swarmSize"]?.toExpression()
            if (swarmSize != null)
                definition.swarmSize = swarmSize

            return definition
        }

        private fun ObjectDefinition.addAttributes(attributes: Attributes) {
            for ((name, value) in attributes)
                this.attributes[name] = value
        }

        private fun ObjectDefinition.addNaturalWeapon(attributes: Attributes) {
            val hit = attributes["verb"] ?: "hit"
            val toHit = attributes["toHit"] ?: "0"
            val damage = attributes["damage"] ?: "randint(1,3)"

            this.attributes["naturalWeapon"] = NaturalWeapon(hit, toHit, damage)
        }
    }

    private fun Attributes.iterator() =
        indices.iterator().map { i -> Pair(getLocalName(i)!!, getValue(i)!!) }

    private fun Attributes.get(name: String) =
        getValue(name)

    private val Attributes.indices: IntRange
        get() = 0..getLength()-1

    private fun String.toExpression() = Expression.parse(this)

    private fun String.toClass(): Class<out Any?> =
        try {
            Class.forName(this)
        } catch (e: ClassNotFoundException) {
            throw ConfigurationException("No such class: $this")
        }
}
