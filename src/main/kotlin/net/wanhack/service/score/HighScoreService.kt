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

package net.wanhack.service.score

import net.wanhack.common.Version
import net.wanhack.model.GameRef
import org.apache.commons.logging.LogFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.AccessControlException
import java.util.concurrent.Executors

class HighScoreService {
    private val log = LogFactory.getLog(javaClass)
    private val highScoreExecutor = Executors.newSingleThreadExecutor()

    fun saveGameScore(gameRef: GameRef, killer: String) {
        val parameters = gameRef.executeQuery { game ->
            val params = ParameterSet()
            val player = game.player
            params.add("name", player.name)
            params.add("score", game.score)
            params.add("killed_by", killer)
            params.add("level", player.level)
            params.add("max_hitpoints", player.maximumHitPoints)
            params.add("dungeon_level", game.dungeonLevel)
            params.add("max_dungeon_level", game.maxDungeonLevel)
            params.add("client_version", Version.fullVersion)
            params
        }

        highScoreExecutor.execute(Runnable() {
            saveGameScoreImpl(parameters)
        })
    }

    private fun saveGameScoreImpl(parameters: ParameterSet) {
        log.debug("Saving high-score for game.")
        try {
            val url = URL("http://wanhack.net/submit-score")
            val conn = url.openConnection() as HttpURLConnection
            conn.setAllowUserInteraction(false)
            conn.setRequestMethod("POST")
            conn.setDoInput(true)
            conn.setDoOutput(true)
            conn.setUseCaches(false)
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.getOutputStream()!!.use { out ->
                out.writer("ISO-8859-1").write(parameters.toString())
            }

            log.debug("response: " + conn.getResponseMessage())
            conn.disconnect()

        } catch (e: IOException) {
            log.error("Saving high-scores failed", e)
        } catch (e: AccessControlException) {
            log.debug("Saving high-scores failed", e)
        }
    }

    private class ParameterSet {
        private val sb = StringBuffer()

        fun add(name: String, value: Long) {
            add(name, value.toString())
        }

        fun add(name: String, value: Int) {
            add(name, value.toString())
        }

        fun add(name: String, value: String) {
            if (sb.length > 0)
                sb.append('&')

            sb.append(encode(name)).append('=').append(encode(value))
        }

        fun toString() = sb.toString()

        class object {
            fun encode(s: String) = URLEncoder.encode(s, "UTF-8")
        }
    }
}
