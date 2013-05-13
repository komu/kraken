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
package net.wanhack.service.region;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses lines of form 'a b c d ... f' to components so that each 
 * component is either a single token or a quoted string.
 * <p>
 * For example: <code>token_one "token two" "token \" three"</code> would
 * contain three tokens.
 */
final class DirectivePattern {
    
    private final Pattern pattern;
    
    public DirectivePattern(String pattern) {
        String regex = pattern.replace("[int]", "(\\d+)");
        regex = regex.replace("[str]", "\"([^\"]+)\"");
        regex = regex.replaceAll("\\ +", "\\\\s+");
        this.pattern = Pattern.compile(regex);
    }

    public String[] getTokens(String str) {
        Matcher m = pattern.matcher(str);
        if (m.matches()) {
            String[] result = new String[m.groupCount()];
            for (int i = 0; i < result.length; i++) {
                result[i] = m.group(i + 1);
            }
            return result;
            
        } else {
            return null;
        }
    }
}
