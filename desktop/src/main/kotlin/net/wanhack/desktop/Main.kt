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

package net.wanhack.desktop

import net.wanhack.common.Direction
import net.wanhack.common.Version
import net.wanhack.desktop.common.ErrorDialog
import net.wanhack.desktop.console.ConsoleView
import net.wanhack.desktop.extensions.makeAction
import net.wanhack.desktop.extensions.set
import net.wanhack.desktop.game.InventoryView
import net.wanhack.desktop.game.RegionView
import net.wanhack.desktop.game.StartGameDialog
import net.wanhack.desktop.game.StatisticsView
import net.wanhack.desktop.game.action.GameActionSet
import net.wanhack.model.GameFacade
import net.wanhack.utils.SystemAccess
import net.wanhack.utils.logger
import java.awt.BorderLayout
import java.awt.event.KeyEvent.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.logging.Level
import javax.swing.*

class Main(val wizardMode: Boolean) {

    private val frame = JFrame("Wanhack")
    private val consoleView = ConsoleView()
    private val inventoryView = InventoryView()
    private val statisticsView = StatisticsView()
    private var gameFacade: GameFacade? = null
    private val regionView = RegionView()
    private val log = Main::class.java.logger()
    private val gameActions = GameActionSet()

    init {
        JOptionPane.setRootFrame(frame)

        frame.jMenuBar = createMenuBar()
        initializeInputMap()
        initializeActionMap()

        val mainPanel = JPanel().apply {
            layout = BorderLayout()

            add(consoleView, BorderLayout.NORTH)
            add(regionView, BorderLayout.CENTER)
            add(statisticsView, BorderLayout.SOUTH)
        }

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, inventoryView)
        splitPane.dividerSize = 2
        splitPane.isContinuousLayout = true
        splitPane.resizeWeight = 1.0

        frame.contentPane.add(splitPane, BorderLayout.CENTER)

        frame.pack()
        frame.isLocationByPlatform = true

        initUncaughtExceptionHandler()

        frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        frame.addWindowListener(object : WindowAdapter() {

            override fun windowClosing(e: WindowEvent) {
                exit()
            }
        })

        frame.isVisible = true
        startNewGame()
    }

    fun exit() {
        val res = JOptionPane.showConfirmDialog(frame, "Really exit Wanhack?", "Are you sure?", JOptionPane.YES_NO_OPTION)
        if (res == JOptionPane.YES_OPTION)
            System.exit(0)
    }

    fun createMenuBar() =
        JMenuBar().apply {
            add(JMenu("Game").apply {
                mnemonic = VK_G

                add(makeAction("New Game", mnemonic=VK_N) {
                    startNewGame()
                })

                addSeparator()

                add(makeAction("Exit", mnemonic=VK_X) {
                    exit()
                })
            })

            add(JMenu("Action").apply {
                mnemonic = VK_A

                for (action in gameActions.actions)
                    add(action)
            })

            if (wizardMode) {
                add(JMenu("Debug").apply {
                    mnemonic = VK_D

                    add(makeAction("Reveal Current Region", mnemonic=VK_R) {
                        gameFacade?.revealCurrentRegion()
                        regionView.repaint()
                    })
                })
            }

            add(JMenu("Help").apply {
                mnemonic = VK_H
                add(makeAction("About Wanhack", mnemonic=VK_A) {
                    showAbout()
                })
            })
        }

    fun initializeInputMap() {
        log.fine("Initializing input map")

        val inputMap = regionView.inputMap

        // Movement with numlock off

        inputMap["UP"]              = Direction.NORTH
        inputMap["DOWN"]            = Direction.SOUTH
        inputMap["LEFT"]            = Direction.WEST
        inputMap["RIGHT"]           = Direction.EAST
        inputMap["control UP"]      = "run " + Direction.NORTH
        inputMap["control DOWN"]    = "run " + Direction.SOUTH
        inputMap["control LEFT"]    = "run " + Direction.WEST
        inputMap["control RIGHT"]   = "run " + Direction.EAST

        // Movement with numlock on
        inputMap["NUMPAD1"] = Direction.SW
        inputMap["NUMPAD2"] = Direction.SOUTH
        inputMap["NUMPAD3"] = Direction.SE
        inputMap["NUMPAD4"] = Direction.WEST
        inputMap["NUMPAD6"] = Direction.EAST
        inputMap["NUMPAD7"] = Direction.NW
        inputMap["NUMPAD8"] = Direction.NORTH
        inputMap["NUMPAD9"] = Direction.NE
        inputMap["control NUMPAD1"] = "run " + Direction.SW
        inputMap["control NUMPAD2"] = "run " + Direction.SOUTH
        inputMap["control NUMPAD3"] = "run " + Direction.SE
        inputMap["control NUMPAD4"] = "run " + Direction.WEST
        inputMap["control NUMPAD6"] = "run " + Direction.EAST
        inputMap["control NUMPAD7"] = "run " + Direction.NW
        inputMap["control NUMPAD8"] = "run " + Direction.NORTH
        inputMap["control NUMPAD9"] = "run " + Direction.NE

        // Up/down
        inputMap["typed >"]     = "down"
        inputMap["typed <"]     = "up"

        // Other controls
        inputMap["NUMPAD5"]     = "rest"
        inputMap["SPACE"]       = "rest"
        inputMap["typed ."]     = "rest"
        inputMap["M"]           = "map"

        // Scrolling history
        inputMap["PAGE_UP"]     = "history up"
        inputMap["PAGE_DOWN"]   = "history down"
    }

    fun initializeActionMap() {
        log.fine("Initializing action map")

        val actionMap = regionView.actionMap

        for (direction in Direction.values()) {
            actionMap[direction]        = makeAction { gameFacade?.movePlayer(direction) }
            actionMap["run $direction"] = makeAction { gameFacade?.runTowards(direction) }
        }

        actionMap["down"]           = makeAction { gameFacade?.movePlayerVertically(false) }
        actionMap["up"]             = makeAction { gameFacade?.movePlayerVertically(true) }
        actionMap["rest"]           = makeAction { gameFacade?.skipTurn() }

        actionMap["history up"]     = makeAction { consoleView.scrollUp() }
        actionMap["history down"]   = makeAction { consoleView.scrollDown() }
        actionMap["map"]            = makeAction { regionView.translate = !regionView.translate; regionView.repaint() }
    }

    fun startNewGame() {
        log.fine("starting new game")

        val dialog = StartGameDialog(frame)
        val config = dialog.showDialog()

        if (config != null) {
            config.wizardMode = wizardMode
            val game = GameFacade(config, consoleView) { tick -> update(tick) }
            consoleView.clear()
            gameActions.gameFacade = game
            regionView.gameFacade = game

            gameFacade = game
            game.start()
        }
    }

    private fun update(tick: Boolean) {
        gameFacade?.query { game ->
            if (tick)
                consoleView.turnEnd()
            else
                consoleView.repaint()
            regionView.repaint()
            statisticsView.updateStatistics(game.statistics)
            inventoryView.update(game.inventoryItems)
        }
    }

    fun initUncaughtExceptionHandler() {
        setAWTUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(t: Thread, e: Throwable) {
                log.log(Level.SEVERE, "Uncaught exception", e)

                ErrorDialog.show(frame, "Unexpected Error", e)

                // The thread is about to die, set this same handler
                // for the newly created AWT event handler thread.
                setAWTUncaughtExceptionHandler(this)
            }
        })
    }

    fun setAWTUncaughtExceptionHandler(eh: Thread.UncaughtExceptionHandler) {
        SwingUtilities.invokeLater {
            Thread.currentThread().uncaughtExceptionHandler = eh
        }
    }

    fun showAbout() {
        val message =
                "Wanhack ${Version.fullVersion}\n" +
                "\n" +
                "Copyright 2005-2013 The Wanhack Team\n" +
                "\n" +
                "This product includes software developed by the\n" +
                "Apache Software Foundation http://www.apache.org/"

        JOptionPane.showMessageDialog(
                frame, message,
                "About Wanhack", JOptionPane.INFORMATION_MESSAGE)
    }
}

fun main(args: Array<String>) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        val wizardMode = SystemAccess.getSystemProperty("wizard") != null

        Main(wizardMode)

    } catch (e: Exception) {
        e.printStackTrace()
        ErrorDialog.show(null, "Unexpected Error", e)
        System.exit(1)
    }
}
