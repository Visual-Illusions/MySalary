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
import net.canarymod.chat.MessageReceiver;
import net.canarymod.motd.MOTDKey;
import net.canarymod.motd.MessageOfTheDayListener;
import net.visualillusionsent.mysalary.Router;

import java.text.MessageFormat;

/**
 * Canary Message Of The Day Listener
 *
 * @author Jason (darkdiplomat)
 */
public class CanarySalaryMOTD implements MessageOfTheDayListener {
    private final CanarySalary cSalary;

    public CanarySalaryMOTD(CanarySalary cSalary) {
        this.cSalary = cSalary;
        Canary.motd().registerMOTDListener(this, cSalary, false);
    }

    @MOTDKey(key = "{salary}")
    public String salary_numeric(MessageReceiver msgrec) {
        double salary = 0;
        if (Router.getCfg().isGroupSpecificEnabled()) {
            if (msgrec instanceof Player) {
                salary = Router.getCfg().getGroupPay(cSalary.getGroupNameForUser(msgrec.getName()));
            }
            else if (Router.getCfg().payServer()) {
                salary = Router.getCfg().getGroupPay("SERVER");
            }
        }
        else if (msgrec instanceof Player || Router.getCfg().payServer()) {
            salary = Router.getCfg().getDefaultPayAmount();
        }
        return MessageFormat.format("{0,number,0.00}", salary);
    }
}
