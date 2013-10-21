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
package net.visualillusionsent.mysalary.bukkit;

import net.milkbowl.vault.permission.Permission;
import net.visualillusionsent.dconomy.dCoBase;
import net.visualillusionsent.minecraft.plugin.bukkit.VisualIllusionsBukkitPlugin;
import net.visualillusionsent.mysalary.MySalary;
import net.visualillusionsent.mysalary.Router;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Jason (darkdiplomat) */
public final class BukkitSalary extends VisualIllusionsBukkitPlugin implements MySalary {
    private Permission permission = null;

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            new Router(this);
            permission = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
            new BukkitSalaryCommandExecutor(this);
        }
        catch (Exception ex) {
            String reason = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            if (debug) { // Only stack trace if debugging
                getLogger().log(Level.SEVERE, "MySalary failed to start. Reason: ".concat(reason), ex);
            }
            else {
                getLogger().severe("MySalary failed to start. Reason: ".concat(reason));
            }
            die();
        }
    }

    @Override
    public void onDisable() {
        Router.closeConnection();
    }

    @Override
    public void broadcastPayDay() {
        if (Router.getCfg().isRequireClaimEnabled()) {
            Bukkit.getServer().broadcastMessage("[§AMySalary§F]§2 PAYDAY!§6 CHECKS ARE READY FOR PICKUP!");
        }
        else {
            Bukkit.getServer().broadcastMessage("[§AMySalary§F]§2 PAYDAY!§6 CHECKS ARE BEING DEPOSITED!");
        }
        getLogger().info("Players paid!");
    }

    @Override
    public void messageUser(String user_name, String message_key, Object... args) {
        Player player = Bukkit.getServer().getPlayer(user_name);
        if (player != null) {
            player.sendMessage(Router.getTranslator().translate(message_key, getUserLocale(), args));
        }
    }

    @Override
    public String getGroupNameForUser(String user_name) {
        try {
            return permission.getPrimaryGroup((String) null, user_name);
        }
        catch (UnsupportedOperationException uoex) {
            // If a permission system without groups is used, default to either ops or default grouping
            return Bukkit.getServer().getOfflinePlayer(user_name).isOp() ? "ops" : "default";
        }
    }

    @Override
    public void error(String message) {
    }

    @Override
    public void message(String message) {
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
        return getLogger();
    }
}
