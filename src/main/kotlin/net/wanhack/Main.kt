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

package net.wanhack

import java.awt.BorderLayout
import java.awt.event.*
import java.awt.event.KeyEvent.*
import javax.swing.*
import javax.swing.Action.*
import kotlin.swing.*
import org.apache.commons.logging.LogFactory
import net.wanhack.common.Version
import net.wanhack.model.common.Direction
import net.wanhack.model.Game
import net.wanhack.service.config.ObjectFactory
import net.wanhack.service.region.RegionLoader
import net.wanhack.service.ServiceProvider
import net.wanhack.ui.common.ErrorDialog
import net.wanhack.ui.console.ConsoleView
import net.wanhack.ui.debug.LogFrame
import net.wanhack.ui.game.action.GameActionSet
import net.wanhack.ui.game.InventoryView
import net.wanhack.ui.game.RegionView
import net.wanhack.ui.game.StatisticsView
import net.wanhack.ui.game.StartGameDialog
import net.wanhack.utils.SystemAccess
import net.wanhack.model.GameRef

class Main(val wizardMode: Boolean) {

    private val frame = JFrame("Wanhack")
    private val consoleView = ConsoleView()
    private val inventoryView = InventoryView()
    private val statisticsView = StatisticsView()
    private val objectFactory = ObjectFactory()
    private val regionLoader = RegionLoader(objectFactory)
    private var gameRef: GameRef? = null
    private val regionView = RegionView()
    private val log = LogFactory.getLog(javaClass<Main>())
    private val logFrame = if (wizardMode) LogFrame() else null
    private val gameActions = GameActionSet();

    {
        JOptionPane.setRootFrame(frame)

        objectFactory.parse("/items/items.xml", "item")
        objectFactory.parse("/items/weapons.xml", "item")
        objectFactory.parse("/creatures/creatures.xml", "creature")

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
        log.debug("Initializing input map")

        // Movement with numlock off

        addInput("UP",          Direction.NORTH);
        addInput("DOWN",        Direction.SOUTH);
        addInput("LEFT",        Direction.WEST);
        addInput("RIGHT",       Direction.EAST);
        addInput("control UP",    "run " + Direction.NORTH);
        addInput("control DOWN",  "run " + Direction.SOUTH);
        addInput("control LEFT",  "run " + Direction.WEST);
        addInput("control RIGHT", "run " + Direction.EAST);

        // Movement with numlock on
        addInput("NUMPAD1",   Direction.SW);
        addInput("NUMPAD2",   Direction.SOUTH);
        addInput("NUMPAD3",   Direction.SE);
        addInput("NUMPAD4",   Direction.WEST);
        addInput("NUMPAD6",   Direction.EAST);
        addInput("NUMPAD7",   Direction.NW);
        addInput("NUMPAD8",   Direction.NORTH);
        addInput("NUMPAD9",   Direction.NE);
        addInput("control NUMPAD1",   "run " + Direction.SW);
        addInput("control NUMPAD2",   "run " + Direction.SOUTH);
        addInput("control NUMPAD3",   "run " + Direction.SE);
        addInput("control NUMPAD4",   "run " + Direction.WEST);
        addInput("control NUMPAD6",   "run " + Direction.EAST);
        addInput("control NUMPAD7",   "run " + Direction.NW);
        addInput("control NUMPAD8",   "run " + Direction.NORTH);
        addInput("control NUMPAD9",   "run " + Direction.NE);

        // Up/down
        addInput("typed >",   "down");
        addInput("typed <",   "up");

        // Other controls
        addInput("NUMPAD5",   "rest");
        addInput("SPACE",     "rest");
        addInput("typed .",   "rest");
        addInput("M",   "map");

        // Scrolling history
        addInput("PAGE_UP", "history up");
        addInput("PAGE_DOWN", "history down");
    }

    fun initializeActionMap() {
        log.debug("Initializing action map");

        val actionMap = regionView.getActionMap()!!

        for (direction in Direction.values())
            actionMap.put(direction, MovePlayerAction(direction, false))

        for (direction in Direction.values())
            actionMap.put("run " + direction, MovePlayerAction(direction, true))

        actionMap.put("down", VerticalMoveAction(false))
        actionMap.put("up",   VerticalMoveAction(true))

        actionMap.put("rest", SkipTurnAction())
        actionMap.put("history up", HistoryScrollAction(true))
        actionMap.put("history down", HistoryScrollAction(false))
        actionMap.put("map",  MapAction())
    }

    fun startNewGame() {
        log.debug("starting new game")

        val dialog = StartGameDialog(frame);
        val config = dialog.showDialog();

        if (config != null) {
            try {
                val game = Game(config, wizardMode)
                consoleView.clear();
                setGame(game);
                gameRef!!.scheduleAction { it.start() }

            } catch (e: Exception) {
                log.error("Failed to start new game", e);
                JOptionPane.showMessageDialog(
                        frame, e, "Failed to start new game", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private fun setGame(game: Game?) {
        if (game != null) {
            game.listener = { update() }
            this.gameRef = game.selfRef
        } else {
            this.gameRef = null
        }

        gameActions.gameRef = gameRef
        regionView.gameRef = gameRef
        inventoryView.gameRef = gameRef
    }

    fun addInput(keyStroke: String, actionMapKey: Any) {
        val inputMap = regionView.getInputMap()!!
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionMapKey)
    }

    private fun update() {
        val ref = gameRef
        if (ref != null) {
            ref.executeQuery { game ->
                consoleView.turnEnd()
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
                log.error("Uncaught exception", e);

                ErrorDialog.show(frame, "Unexpected Error", e);

                // The thread is about to die, set this same handler
                // for the newly created AWT event handler thread.
                setAWTUncaughtExceptionHandler(this);
            }
        });
    }

    fun setAWTUncaughtExceptionHandler(eh: Thread.UncaughtExceptionHandler) {
        SwingUtilities.invokeLater(Runnable {
            Thread.currentThread().setUncaughtExceptionHandler(eh)
        });
    }

    inner class MovePlayerAction(val direction: Direction, val run: Boolean) : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            gameRef?.scheduleAction { game ->
                if (run)
                    game.runTowards(direction);
                else
                    game.movePlayer(direction);
            }
        }
    }

    inner class VerticalMoveAction(val up: Boolean) : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            gameRef?.scheduleAction { it.movePlayerVertically(up) }
        }
    }

    inner class SkipTurnAction : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            gameRef?.scheduleAction { it.skipTurn() }
        }
    }

    inner class HistoryScrollAction(val up: Boolean) : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            if (up)
                consoleView.scrollUp()
            else
                consoleView.scrollDown()
        }
    }

    inner class MapAction : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            regionView.translate = !regionView.translate
            regionView.repaint()
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
