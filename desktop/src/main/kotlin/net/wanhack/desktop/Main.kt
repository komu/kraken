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

import java.awt.BorderLayout
import java.awt.event.*
import java.awt.event.KeyEvent.*
import javax.swing.*
import javax.swing.Action.*
import kotlin.swing.*
import net.wanhack.common.Version
import net.wanhack.model.common.Direction
import net.wanhack.service.config.ObjectFactory
import net.wanhack.service.region.RegionLoader
import net.wanhack.service.ServiceProvider
import net.wanhack.desktop.common.ErrorDialog
import net.wanhack.desktop.console.ConsoleView
import net.wanhack.desktop.debug.LogFrame
import net.wanhack.desktop.game.action.GameActionSet
import net.wanhack.desktop.game.InventoryView
import net.wanhack.desktop.game.RegionView
import net.wanhack.desktop.game.StatisticsView
import net.wanhack.desktop.game.StartGameDialog
import net.wanhack.desktop.extensions.set
import net.wanhack.utils.SystemAccess
import net.wanhack.model.GameRef
import net.wanhack.utils.logger
import java.util.logging.Level
import net.wanhack.definitions.*
import net.wanhack.model.GameFacade

class Main(val wizardMode: Boolean) {

    private val frame = JFrame("Wanhack")
    private val consoleView = ConsoleView()
    private val inventoryView = InventoryView()
    private val statisticsView = StatisticsView()
    private val objectFactory = ObjectFactory()
    private val regionLoader = RegionLoader(objectFactory)
    private var gameRef: GameRef? = null
    private val regionView = RegionView()
    private val log = javaClass<Main>().logger()
    private val logFrame = if (wizardMode) LogFrame() else null
    private val gameActions = GameActionSet();

    {
        JOptionPane.setRootFrame(frame)

        objectFactory.addDefinitions(Weapons)
        objectFactory.addDefinitions(Items)
        objectFactory.addDefinitions(Creatures)

        ServiceProvider.console = consoleView
        ServiceProvider.objectFactory = objectFactory
        ServiceProvider.regionLoader = regionLoader

        frame.jmenuBar = createMenuBar()
        initializeInputMap()
        initializeActionMap()

        val mainPanel = panel {
            setLayout(BorderLayout())

            add(consoleView, BorderLayout.NORTH)
            add(regionView, BorderLayout.CENTER)
            add(statisticsView, BorderLayout.SOUTH)
        }

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, inventoryView)
        splitPane.setDividerSize(1)

        frame.contentPane!!.add(splitPane, BorderLayout.CENTER)

        frame.pack()
        frame.setLocationByPlatform(true)

        initUncaughtExceptionHandler()

        frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        frame.addWindowListener(object : WindowAdapter() {

            override fun windowClosing(e: WindowEvent) {
                exit()
            }
        })

        frame.setVisible(true)
        startNewGame()
    }

    fun exit() {
        val res = JOptionPane.showConfirmDialog(frame, "Really exit Wanhack?", "Are you sure?", JOptionPane.YES_NO_OPTION)
        if (res == JOptionPane.YES_OPTION)
            System.exit(0)
    }

    fun createMenuBar() =
        menuBar {
            menu("Game") {
                mnemonic = VK_G

                add(action("New Game", mnemonic=VK_N) {
                    startNewGame()
                })

                addSeparator()

                add(action("Exit", mnemonic=VK_X) {
                    exit();
                })
            }

            menu("Action") {
                mnemonic = VK_A

                for (action in gameActions.actions)
                    add(action)
            }

            if (wizardMode) {
                menu("Debug") {
                    mnemonic = VK_D

                    add(action("Reveal Current Region", mnemonic=VK_R) {
                        gameRef?.scheduleAction { it.revealCurrentRegion() }
                        regionView.repaint()
                    })

                    add(ShowLogFrameAction(logFrame!!).menuItem)
                }
            }

            menu("Help") {
                mnemonic = VK_H
                add(action("About Wanhack", mnemonic=VK_A) {
                    showAbout()
                })
            }
        }

    fun initializeInputMap() {
        log.fine("Initializing input map")

        val inputMap = regionView.getInputMap()!!

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
        log.fine("Initializing action map");

        val actionMap = regionView.getActionMap()!!

        for (direction in Direction.values()) {
            actionMap[direction]        = gameAction { it.movePlayer(direction) }
            actionMap["run $direction"] = gameAction { it.runTowards(direction) }
        }

        actionMap["down"]           = gameAction { it.movePlayerVertically(false) }
        actionMap["up"]             = gameAction { it.movePlayerVertically(true) }
        actionMap["rest"]           = gameAction { it.skipTurn() }

        actionMap["history up"]     = uiAction { consoleView.scrollUp() }
        actionMap["history down"]   = uiAction { consoleView.scrollDown() }
        actionMap["map"]            = uiAction { regionView.translate = !regionView.translate; regionView.repaint() }
    }

    fun startNewGame() {
        log.fine("starting new game")

        val dialog = StartGameDialog(frame);
        val config = dialog.showDialog();

        if (config != null) {
            try {
                val game = GameFacade(config, wizardMode, consoleView) { tick -> update(tick) }
                consoleView.clear();
                setGame(game);
                gameRef!!.scheduleAction { it.start() }

            } catch (e: Exception) {
                log.log(Level.SEVERE, "Failed to start new game", e);
                JOptionPane.showMessageDialog(frame, e, "Failed to start new game", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private fun setGame(game: GameFacade?) {
        this.gameRef = if (game != null) GameRef(game) else null

        gameActions.gameRef = gameRef
        regionView.gameRef = gameRef
        inventoryView.gameRef = gameRef
    }

    private fun update(tick: Boolean) {
        val ref = gameRef
        if (ref != null) {
            ref.executeQuery { game ->
                if (tick)
                    consoleView.turnEnd()
                else
                    consoleView.repaint()
                regionView.repaint()
                statisticsView.updateStatistics(game)
                inventoryView.update(game)
            }

            ref.yieldWriteLock()
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
        });
    }

    fun setAWTUncaughtExceptionHandler(eh: Thread.UncaughtExceptionHandler) {
        SwingUtilities.invokeLater(Runnable {
            Thread.currentThread().setUncaughtExceptionHandler(eh)
        });
    }

    fun gameAction(callback: (GameFacade) -> Unit) = object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) {
            gameRef?.scheduleAction { callback(it) }
        }
    }

    fun uiAction(callback: () -> Unit) = object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) {
            callback()
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

    class ShowLogFrameAction(val frame: LogFrame) : AbstractAction("Log") {

        val menuItem = JCheckBoxMenuItem(this);

        {
            putValue(MNEMONIC_KEY, VK_L);
            frame.addWindowListener(object : WindowAdapter() {
                public override fun windowClosing(e: WindowEvent) {
                    menuItem.setSelected(false);
                }
            });
        }

        public override fun actionPerformed(e: ActionEvent) {
            val newState = !frame.isVisible()
            frame.setVisible(newState)
            menuItem.setSelected(newState)
        }
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
