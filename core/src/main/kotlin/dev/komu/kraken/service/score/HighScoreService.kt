/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.service.score

import dev.komu.kraken.model.Game
import dev.komu.kraken.utils.logger
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.AccessControlException
import java.util.concurrent.Executors
import java.util.logging.Level

class HighScoreService {
    private val log = javaClass.logger()
    private val highScoreExecutor = Executors.newSingleThreadExecutor()

    fun saveGameScore(game: Game, killer: String) {
        val params = ParameterSet()
        val player = game.player
        params.add("name", player.name)
        params.add("score", player.experience)
        params.add("killed_by", killer)
        params.add("level", player.level)
        params.add("max_hitpoints", player.maximumHitPoints)
        params.add("dungeon_level", game.dungeonLevel)
        params.add("max_dungeon_level", game.maxDungeonLevel)
        params.add("client_version", dev.komu.kraken.common.Version.fullVersion)

        highScoreExecutor.execute {
            saveGameScoreImpl(params)
        }
    }

    private fun saveGameScoreImpl(parameters: ParameterSet) {
        log.fine("Saving high-score for game.")
        try {
            val url = URL("http://wanhack.net/submit-score")
            val conn = url.openConnection() as HttpURLConnection
            conn.allowUserInteraction = false
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true
            conn.useCaches = false
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.outputStream!!.use { out ->
                out.writer(Charsets.ISO_8859_1).write(parameters.toString())
            }

            log.fine("response: " + conn.responseMessage)
            conn.disconnect()

        } catch (e: IOException) {
            log.log(Level.SEVERE, "Saving high-scores failed", e)
        } catch (e: AccessControlException) {
            log.log(Level.FINE, "Saving high-scores failed", e)
        }
    }

    private class ParameterSet {
        private val sb = StringBuffer()

        fun add(name: String, value: Any) {
            if (sb.isNotEmpty())
                sb.append('&')

            sb.append(urlEncode(name)).append('=').append(urlEncode(value.toString()))
        }

        override fun toString() = sb.toString()

        companion object {
            fun urlEncode(s: String) = URLEncoder.encode(s, "UTF-8")
        }
    }
}
