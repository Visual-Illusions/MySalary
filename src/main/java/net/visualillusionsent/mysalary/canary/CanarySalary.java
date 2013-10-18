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
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPlugin;
import net.visualillusionsent.mysalary.Finance;
import net.visualillusionsent.mysalary.MySalary;
import net.visualillusionsent.mysalary.MySalaryConfiguration;
import net.visualillusionsent.utils.UtilityException;

import java.io.IOException;

/**
 * MySalary main Canary Plugin class
 *
 * @author Jason (darkdiplomat)
 */
public class CanarySalary extends VisualIllusionsCanaryPlugin implements MySalary {
    private MySalaryConfiguration myscfg;
    private Finance finance;

    @Override
    public boolean enable() {
        checkVersion();
        checkStatus();
        try {
            myscfg = new MySalaryConfiguration(this);
        }
        catch (IOException ioex) {
            getLogman().logSevere(ioex.getMessage());
            return false;
        }
        catch (UtilityException uex) {
            getLogman().logStacktrace(uex.getMessage(), uex);
        }
        finance = new Finance(this);
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
        finance.close();
    }

    @Override
    public final MySalaryConfiguration getCfg() {
        return myscfg;
    }

    @Override
    public void broadcastPayDay() {
        if (myscfg.isRequireClaimEnabled()) {
            Canary.getServer().broadcastMessage("[§AMySalary§F]§2 PAYDAY!§6 CHECKS ARE READY FOR PICKUP!");
        }
        else {
            Canary.getServer().broadcastMessage("[§AMySalary§F]§2 PAYDAY!§6 CHECKS ARE BEING DEPOSITED!");
        }
        getLogman().logInfo("Players paid!");
    }

    @Override
    public void messageUser(String user_name, String message) {
        Player player = Canary.getServer().getPlayer(user_name);
        if (player != null) {
            player.message(message);
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
}
