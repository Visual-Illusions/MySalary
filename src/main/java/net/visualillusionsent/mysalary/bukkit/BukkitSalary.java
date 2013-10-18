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
import net.visualillusionsent.minecraft.plugin.bukkit.VisualIllusionsBukkitPlugin;
import net.visualillusionsent.mysalary.Finance;
import net.visualillusionsent.mysalary.MySalary;
import net.visualillusionsent.mysalary.MySalaryConfiguration;
import net.visualillusionsent.utils.JarUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;

/** @author Jason (darkdiplomat) */
public class BukkitSalary extends VisualIllusionsBukkitPlugin implements MySalary {
    private MySalaryConfiguration myscfg;
    private Finance finance;
    private Permission permission = null;

    @Override
    public void onEnable() {
        initialize();
        checkVersion();
        checkStatus();
        try {
            myscfg = new MySalaryConfiguration(this);
        }
        catch (IOException ioex) {
            throw new RuntimeException(ioex);
        }

        finance = new Finance(this);
        permission = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
        new BukkitSalaryCommandExecutor(this);
    }

    @Override
    public void onDisable() {
        if (finance != null) {
            finance.close();
        }
    }

    @Override
    public final MySalaryConfiguration getCfg() {
        return myscfg;
    }

    @Override
    public void broadcastPayDay() {
        if (myscfg.isRequireClaimEnabled()) {
            Bukkit.getServer().broadcastMessage("[§AMySalary§F]§2 PAYDAY!§6 CHECKS ARE READY FOR PICKUP!");
        }
        else {
            Bukkit.getServer().broadcastMessage("[§AMySalary§F]§2 PAYDAY!§6 CHECKS ARE BEING DEPOSITED!");
        }
        getLogger().info("Players paid!");
    }

    @Override
    public void messageUser(String user_name, String message) {
        Player player = Bukkit.getServer().getPlayer(user_name);
        if (player != null) {
            player.sendMessage(message);
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
    public final Finance getFinance() {
        return finance;
    }

    @Override
    public final void error(String s, Object... objects) {
    }

    @Override
    public void message(String s, Object... objects) {
    }

    @Override
    public final boolean hasPermission(String s) {
        return true;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public String getUserLocale() {
        return null;
    }

    @Override
    public String getJarPath() {
        return JarUtils.getJarPath(BukkitSalary.class);
    }
}
