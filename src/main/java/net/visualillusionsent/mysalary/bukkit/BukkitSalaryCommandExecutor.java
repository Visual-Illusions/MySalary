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
import net.visualillusionsent.utils.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * Command Executor for Bukkit
 *
 * @author Jason (darkdiplomat)
 */
public class BukkitSalaryCommandExecutor extends VisualIllusionsBukkitPluginInformationCommand {
    private static final String salary_msg = ChatColor.GREEN + "Your salary is " + ChatColor.GOLD + " {0,number,#.##} " + dCoBase.getProperties().getString("money.name"),
            salary_pay = ChatColor.GREEN + "You have received " + ChatColor.GOLD + " {0,number,#.##} " + dCoBase.getProperties().getString("money.name");

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
                        sender.sendMessage(ChatColor.GREEN + "Next PayCheck in: " + ChatColor.GOLD + getBS().getFinance().getTimeUntil());
                        if (getBS().getCfg().isGroupSpecificEnabled()) {
                            if (sender instanceof Player) {
                                double salary = getBS().getCfg().getGroupPay(getBS().getGroupNameForUser(sender.getName()));
                                if (salary > 0) {
                                    sender.sendMessage(MessageFormat.format(salary_msg, salary));
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED.toString().concat("You do not have a salary."));
                                }
                            }
                            else if (getBS().getCfg().payServer()) {
                                sender.sendMessage(MessageFormat.format(salary_msg, getBS().getCfg().getGroupPay("SERVER")));
                            }
                        }
                        else {
                            if (sender instanceof Player || getBS().getCfg().payServer()) {
                                sender.sendMessage(MessageFormat.format(salary_msg, getBS().getCfg().getDefaultPayAmount()));
                            }
                        }
                    }
                    else {
                        sender.sendMessage(msg);
                    }
                }
                return true;
            }
            else if (args[0].equals("claim")) {
                if (getBS().getCfg().isRequireClaimEnabled()) {
                    double result = getBS().getFinance().checkPendingAndPay(sender.getName());
                    if (result > 0) {
                        sender.sendMessage(MessageFormat.format(salary_pay, result));
                    }
                    else {
                        sender.sendMessage(ChatColor.RED.toString().concat("You do not have any pending checks."));
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED.toString().concat("Checks are auto-deposited. Claiming is not required."));
                }
                return true;
            }
            else if (args[0].equals("broadcast") && sender.hasPermission("mysalary.admin")) {
                Bukkit.getServer().broadcastMessage("[§AMySalary§F]§A Next PayCheck in: " + ChatColor.GOLD + getBS().getFinance().getTimeUntil());
                return true;
            }
            else if (args[0].equals("forcepay") && sender.hasPermission("mysalary.admin")) {
                if (args.length == 2 && args[1].toLowerCase().equals("reset")) {
                    getBS().getFinance().reset(false);
                }
                getBS().getFinance().payout();
                return true;
            }
            else if (args[0].equals("setprop") && sender.hasPermission("mysalary.admin") && args.length > 1) {
                getBS().getCfg().setProperty(args[1], args[2]);
                sender.sendMessage(ChatColor.GOLD + args[1] + ChatColor.GREEN + " is now set to " + ChatColor.YELLOW + args[2]);
                return true;
            }
        }
        return false;
    }

    private final BukkitSalary getBS() {
        return (BukkitSalary) plugin;
    }
}
