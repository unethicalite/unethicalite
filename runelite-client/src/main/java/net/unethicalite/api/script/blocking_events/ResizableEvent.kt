package net.unethicalite.api.script.blocking_events

import net.unethicalite.api.game.Game
import net.runelite.api.GameState
import net.unethicalite.client.Static

class ResizableEvent : BlockingEvent() {
    override fun validate(): Boolean {
        if (Game.getState() != GameState.LOGGED_IN) {
            return false
        }

        return Static.getClient().windowedMode != 1 || Static.getClient().preferences.windowMode != 1
    }

    override fun loop(): Int {
        if (Static.getClient().windowedMode != 1) {
            Static.getClient().windowedMode = 1
            return 1000
        }

        if (Static.getClient().preferences.windowMode != 1) {
            Static.getClient().preferences.windowMode = 1
        }

        return 1000
    }
}
