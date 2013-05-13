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
package net.wanhack.service.score;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.AccessControlException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.wanhack.common.Version;
import net.wanhack.model.GameRef;
import net.wanhack.model.IGame;
import net.wanhack.model.SimpleQueryCallback;
import net.wanhack.model.creature.Player;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HighScoreService {

    private final Log log = LogFactory.getLog(getClass());
    private final Executor highScoreExecutor = 
        Executors.newSingleThreadExecutor();
    
    public void saveGameScore(final GameRef gameRef, final String killer) {
        final ParameterSet parameters = new ParameterSet();
        
        gameRef.executeQuery(new SimpleQueryCallback() {
            public void execute(IGame game) {
                Player player = game.getPlayer();
                parameters.add("name", player.getName());
                parameters.add("score", game.getScore());
                parameters.add("killed_by", killer);
                parameters.add("level", player.getLevel());
                parameters.add("max_hitpoints", player.getMaximumHitpoints());
                parameters.add("dungeon_level", game.getDungeonLevel());
                parameters.add("max_dungeon_level", game.getMaxDungeonLevel());
                parameters.add("client_version", Version.getFullVersion());
            }
        });
        
        highScoreExecutor.execute(new Runnable() {
            public void run() {
                saveGameScoreImpl(parameters);        
            }
        });
    }
    
    private void saveGameScoreImpl(ParameterSet parameters) {
        log.debug("Saving high-score for game.");
        try {
            URL url = new URL("http://wanhack.net/submit-score");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(false);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            OutputStream out = conn.getOutputStream();
            try {
                out.write(parameters.toString().getBytes("ISO-8859-1"));
                out.flush();
            } finally {
                out.close();
            }
            
            log.debug("response: " + conn.getResponseMessage());
            conn.disconnect();
            
        } catch (IOException e) {
            log.error("Saving high-scores failed", e);
        } catch (AccessControlException e) {
            // We probably WebStarted the program from another server than
            // we use for high-scores and therefore can't send the scores.
            // It's nothing to bother the user about.
            log.debug("Saving high-scores failed", e);
        }
    }
    
    private static class ParameterSet {
        private final StringBuffer sb = new StringBuffer();
        
        public void add(String name, long value) {
            add(name, String.valueOf(value));
        }
        
        public void add(String name, String value) {
            if (sb.length() > 0 ) {
                sb.append("&");
            }
            
            sb.append(encode(name)).append('=').append(encode(value));
        }

        @Override
        public String toString() {
            return sb.toString();
        }
        
        private static final String encode(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
