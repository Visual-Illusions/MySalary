/*
 * This file is part of MySalary.
 *
 * Copyright © 2011-2014 Visual Illusions Entertainment
 *
 * MySalary is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.mysalary.canary;

import net.canarymod.Canary;
import net.canarymod.api.OfflinePlayer;
import net.canarymod.api.entity.living.humanoid.Player;
import net.visualillusionsent.dconomy.dCoBase;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPlugin;
import net.visualillusionsent.mysalary.MySalary;
import net.visualillusionsent.mysalary.Router;

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
            new CanarySalaryCommandListener(this);
            new CanarySalaryMOTD(this);
            return true;
        }
        catch (Exception ex) {
            String reason = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            if (debug) { // Only stack trace if debugging
                getLogman().error("MySalary failed to start. Reason: ".concat(reason), ex);
            }
            else {
                getLogman().error("MySalary failed to start. Reason: ".concat(reason));
            }
        }
        return false;
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
        getLogman().info("Players paid!");
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
        getLogman().warn(message);
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
}
