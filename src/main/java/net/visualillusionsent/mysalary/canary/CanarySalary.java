/*
 * This file is part of MySalary.
 *
 * Copyright © 2011-2013 Visual Illusions Entertainment
 *
 * MySalary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * MySalary is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MySalary.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.mysalary.canary;

import net.canarymod.Canary;
import net.canarymod.api.OfflinePlayer;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.commandsys.CommandDependencyException;
import net.visualillusionsent.dconomy.dCoBase;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPlugin;
import net.visualillusionsent.mysalary.MySalary;
import net.visualillusionsent.mysalary.MySalaryInitializationException;
import net.visualillusionsent.mysalary.Router;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MySalary main Canary Plugin class
 *
 * @author Jason (darkdiplomat)
 */
public class CanarySalary extends VisualIllusionsCanaryPlugin implements MySalary {

    @Override
    public boolean enable() {
        super.enable();

        try {
            new Router(this);
        }
        catch (MySalaryInitializationException msiex) {
            getLogman().log(Level.SEVERE, msiex.getMessage(), msiex.getCause());
            return false;
        }

        try {
            new CanarySalaryCommandListener(this);
        }
        catch (CommandDependencyException cdex) {
            getLogman().logStacktrace("Failed to register commands...", cdex);
            return false;
        }

        return true;
    }

    @Override
    public void disable() {
        Router.closeConnection();
    }

    @Override
    public void broadcastPayDay() {
        if (Router.getCfg().isRequireClaimEnabled()) {
            Canary.getServer().broadcastMessage("[§AMySalary§F]§2 PAYDAY!§6 CHECKS ARE READY FOR PICKUP!");
        }
        else {
            Canary.getServer().broadcastMessage("[§AMySalary§F]§2 PAYDAY!§6 CHECKS ARE BEING DEPOSITED!");
        }
        getLogman().logInfo("Players paid!");
    }

    @Override
    public void messageUser(String user_name, String message_key, Object... args) {
        Player player = Canary.getServer().getPlayer(user_name);
        if (player != null) {
            player.message(Router.getTranslator().translate(message_key, getUserLocale(), args));
        }
    }

    @Override
    public String getGroupNameForUser(String user_name) {
        OfflinePlayer offplayer = Canary.getServer().getOfflinePlayer(user_name);
        if (offplayer != null) {
            return offplayer.getGroup().getName();
        }
        return null;
    }

    @Override
    public void error(String message) {
        getLogman().warning(message);
    }

    @Override
    public void message(String message) {
        getLogman().info(message);
    }

    @Override
    public final boolean hasPermission(String s) {
        return true;
    }

    @Override
    public String getUserLocale() {
        return dCoBase.getServerLocale();
    }

    @Override
    public Logger getPluginLogger() {
        return getLogman();
    }
}
