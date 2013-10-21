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
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.Colors;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.visualillusionsent.dconomy.api.dConomyUser;
import net.visualillusionsent.dconomy.canary.api.Canary_User;
import net.visualillusionsent.dconomy.dCoBase;
import net.visualillusionsent.minecraft.plugin.ModMessageReceiver;
import net.visualillusionsent.minecraft.plugin.canary.CanaryMessageReceiver;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPluginInformationCommand;
import net.visualillusionsent.mysalary.Router;

/**
 * Canary MySalary Command Listener
 *
 * @author Jason (darkdiplomat)
 */
public final class CanarySalaryCommandListener extends VisualIllusionsCanaryPluginInformationCommand {

    public CanarySalaryCommandListener(CanarySalary plugin) throws CommandDependencyException {
        super(plugin);
        Canary.commands().registerCommands(this, plugin, false);
    }

    @Command(aliases = { "mysalary", "mys" },
            description = "MySalary information/main Command",
            permissions = { "mysalary.getpaid" },
            toolTip = "/mysalary [subcommand]")
    public final void information(MessageReceiver msgrec, String[] args) {
        super.sendInformation(msgrec);
    }

    @Override
    protected final void messageInject(ModMessageReceiver receiver) {
        MessageReceiver msgrec = ((CanaryMessageReceiver) receiver).unwrap();
        receiver.message(Colors.LIGHT_GREEN + "Next PayCheck in: " + Colors.ORANGE + Router.getFinance().getTimeUntil());
        if (Router.getCfg().isGroupSpecificEnabled()) {
            if (msgrec instanceof Player) {
                double salary = Router.getCfg().getGroupPay(getPlugin().getGroupNameForUser(msgrec.getName()));
                if (salary > 0) {
                    msgrec.message(Router.getTranslator().translate("user.salary", asUser(msgrec).getUserLocale(), salary));
                }
                else {
                    msgrec.notice(Router.getTranslator().translate("no.salary", asUser(msgrec).getUserLocale()));
                }
            }
            else if (Router.getCfg().payServer()) {
                msgrec.message(Router.getTranslator().translate("user.salary", asUser(msgrec).getUserLocale(), Router.getCfg().getGroupPay("SERVER")));
            }
            else {
                msgrec.notice(Router.getTranslator().translate("no.salary", asUser(msgrec).getUserLocale()));
            }
        }
        else if (msgrec instanceof Player || Router.getCfg().payServer()) {
            msgrec.message(Router.getTranslator().translate("user.salary", asUser(msgrec).getUserLocale(), Router.getCfg().getDefaultPayAmount()));
        }
        else {
            msgrec.notice(Router.getTranslator().translate("no.salary", asUser(msgrec).getUserLocale()));
        }
    }

    @Command(aliases = { "claim" },
            description = "Used to claim pending checks",
            permissions = { "mysalary.getpaid" },
            parent = "mysalary",
            toolTip = "/mysalary claim")
    public final void claimcheck(MessageReceiver msgrec, String[] args) {
        if (Router.getCfg().isRequireClaimEnabled()) {
            double result = Router.getFinance().checkPendingAndPay(msgrec.getName());
            if (result > 0) {
                msgrec.message(Router.getTranslator().translate("salary.received", asUser(msgrec).getUserLocale(), result));
            }
            else if (result == 0) {
                msgrec.notice(Router.getTranslator().translate("no.check", asUser(msgrec).getUserLocale()));
            }
        }
        else {
            msgrec.notice(Router.getTranslator().translate("auto.checks", asUser(msgrec).getUserLocale()));
        }
    }

    @Command(aliases = { "broadcast" },
            description = "Broadcasts time until next paycheck",
            permissions = { "mysalary.admin" },
            parent = "mysalary",
            toolTip = "/mysalary broadcast")
    public final void broadcast(MessageReceiver msgrec, String[] args) {
        Canary.getServer().broadcastMessage("[§AMySalary§F]§A Next PayCheck in: " + Colors.ORANGE + Router.getFinance().getTimeUntil());
    }

    @Command(aliases = { "forcepay" },
            description = "Forces a pay out of checks",
            permissions = { "mysalary.admin" },
            parent = "mysalary",
            toolTip = "/mysalary forcepay [reset]")
    public final void forcepay(MessageReceiver msgrec, String[] args) {
        if (args.length == 2 && args[1].toLowerCase().equals("reset")) {
            Router.getFinance().reset(false);
        }
        Router.getFinance().payout();
    }

    @Command(aliases = { "setprop" },
            description = "Sets/changes a property value",
            permissions = { "mysalary.admin" },
            parent = "mysalary",
            toolTip = "/mysalary setprop <key> <value>",
            min = 2)
    public final void setProp(MessageReceiver msgrec, String[] args) {
        try {
            Router.getCfg().setProperty(args[1], args[2]);
            msgrec.message(Router.getTranslator().translate("prop.set", asUser(msgrec).getUserLocale(), args[1], args[2]));
        }
        catch (IllegalArgumentException iaex) {
            msgrec.notice(iaex.getMessage());
        }
    }

    protected final CanarySalary getPlugin() {
        return (CanarySalary) plugin;
    }

    private dConomyUser asUser(MessageReceiver msgrec) {
        return msgrec instanceof Player ? new Canary_User((Player) msgrec) : dCoBase.getServer();
    }
}
