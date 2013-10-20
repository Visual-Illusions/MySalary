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
/*
 * This file is part of SearchIds.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * SearchIds is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * SearchIds is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with SearchIds.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.mysalary.bukkit;

import net.visualillusionsent.dconomy.dCoBase;
import net.visualillusionsent.minecraft.plugin.bukkit.VisualIllusionsBukkitPluginInformationCommand;
import net.visualillusionsent.mysalary.Router;
import net.visualillusionsent.utils.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command Executor for Bukkit
 *
 * @author Jason (darkdiplomat)
 */
public final class BukkitSalaryCommandExecutor extends VisualIllusionsBukkitPluginInformationCommand {
    BukkitSalaryCommandExecutor(BukkitSalary bsalary) {
        super(bsalary);
        // Initialize Commands
        bsalary.getCommand("mysalary").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.matches("my(s|salary)")) {
            if (args.length == 0) {
                for (String msg : about) {
                    if (msg.equals("$VERSION_CHECK$")) {
                        VersionChecker vc = plugin.getVersionChecker();
                        Boolean isLatest = vc.isLatest();
                        if (isLatest == null) {
                            sender.sendMessage(center(ChatColor.DARK_GRAY.toString().concat("VersionCheckerError: ").concat(vc.getErrorMessage())));
                        }
                        else if (!isLatest) {
                            sender.sendMessage(center(ChatColor.DARK_GRAY.toString().concat(vc.getUpdateAvailibleMessage())));
                        }
                        else {
                            sender.sendMessage(center(ChatColor.GREEN.toString().concat("Latest Version Installed")));
                        }

                        //Inject MySalary messages
                        sender.sendMessage(ChatColor.GREEN + "Next PayCheck in: " + ChatColor.GOLD + Router.getFinance().getTimeUntil());
                        if (Router.getCfg().isGroupSpecificEnabled()) {
                            if (sender instanceof Player) {
                                double salary = Router.getCfg().getGroupPay(getPlugin().getGroupNameForUser(sender.getName()));
                                if (salary > 0) {
                                    sender.sendMessage(Router.getTranslator().translate("user.salary", dCoBase.getServerLocale(), salary));
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED.toString().concat(Router.getTranslator().translate("no.salary", dCoBase.getServerLocale())));
                                }
                            }
                            else if (Router.getCfg().payServer()) {
                                sender.sendMessage(Router.getTranslator().translate("user.salary", dCoBase.getServerLocale(), Router.getCfg().getGroupPay("SERVER")));
                            }
                        }
                        else if (sender instanceof Player || Router.getCfg().payServer()) {
                            sender.sendMessage(Router.getTranslator().translate("user.salary", dCoBase.getServerLocale(), Router.getCfg().getDefaultPayAmount()));
                        }
                        else {
                            sender.sendMessage(ChatColor.RED.toString().concat(Router.getTranslator().translate("no.salary", dCoBase.getServerLocale())));
                        }
                    }
                    else {
                        sender.sendMessage(msg);
                    }
                }
                return true;
            }
            else if (args[0].equals("claim")) {
                if (Router.getCfg().isRequireClaimEnabled()) {
                    double result = Router.getFinance().checkPendingAndPay(sender.getName());
                    if (result > 0) {
                        sender.sendMessage(Router.getTranslator().translate("salary.received", dCoBase.getServerLocale(), result));
                    }
                    else {
                        sender.sendMessage(ChatColor.RED.toString().concat(Router.getTranslator().translate("no.check", dCoBase.getServerLocale())));
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED.toString().concat(Router.getTranslator().translate("auto.checks", dCoBase.getServerLocale())));
                }
                return true;
            }
            else if (args[0].equals("broadcast") && sender.hasPermission("mysalary.admin")) {
                Bukkit.getServer().broadcastMessage("[§AMySalary§F]§A Next PayCheck in: " + ChatColor.GOLD + Router.getFinance().getTimeUntil());
                return true;
            }
            else if (args[0].equals("forcepay") && sender.hasPermission("mysalary.admin")) {
                if (args.length == 2 && args[1].toLowerCase().equals("reset")) {
                    Router.getFinance().reset(false);
                }
                Router.getFinance().payout();
                return true;
            }
            else if (args[0].equals("setprop") && sender.hasPermission("mysalary.admin") && args.length > 1) {
                Router.getCfg().setProperty(args[1], args[2]);
                sender.sendMessage(Router.getTranslator().translate("prop.set", dCoBase.getServerLocale(), args[1], args[2]));
                return true;
            }
        }
        return false;
    }

    protected final BukkitSalary getPlugin() {
        return (BukkitSalary) plugin;
    }
}
