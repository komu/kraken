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

package net.wanhack.service

import net.wanhack.model.common.Console
import net.wanhack.service.config.ObjectFactory
import net.wanhack.service.region.RegionLoader
import net.wanhack.service.score.HighScoreService

object ServiceProvider {

    private var _console: Console? = null
    private var _objectFactory: ObjectFactory? = null
    private var _regionLoader: RegionLoader? = null

    var console: Console
        get() = _console!!
        set(console: Console) = _console = console

    var objectFactory: ObjectFactory
        get() = _objectFactory!!
        set(objectFactory: ObjectFactory) = _objectFactory = objectFactory

    var regionLoader: RegionLoader
        get() = _regionLoader!!
        set(regionLoader: RegionLoader) = _regionLoader = regionLoader

    val highScoreService = HighScoreService()
}
