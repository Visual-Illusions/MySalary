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
package net.visualillusionsent.mysalary;

import net.visualillusionsent.dconomy.api.dConomyUser;
import net.visualillusionsent.minecraft.plugin.CommandTabCompleteUtil;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jason (darkdiplomat)
 */
public final class SalaryTabComplete extends CommandTabCompleteUtil {
    private static final String[] salaryCMD = new String[]{ "claim", "broadcast", "forcepay", "setprop" };
    static final Matcher matchA = Pattern.compile("(broadcast|forcepay|setprop)").matcher(""),
            propBool = Pattern.compile("(update\\.lang|require\\.claim|group\\.specific\\.pay|accumulate\\.checks|pay\\.locked|pay\\.server)").matcher("");

    public static List<String> match(dConomyUser user, String[] args) {
        if (args.length == 1) {
            List<String> preRet = matchTo(args, salaryCMD);
            Iterator<String> preRetItr = preRet.iterator();
            while (preRetItr.hasNext()) {
                String ret = preRetItr.next();
                if (matchA.reset(ret).matches() && !user.hasPermission("mysalary.admin")) {
                    preRetItr.remove();
                }
                else if (ret.equals("claim") && !Router.getCfg().isRequireClaimEnabled()) {
                    preRetItr.remove();
                }
            }
            return preRet;
        }
        else if (args.length == 2 && user.hasPermission("mysalary.admin")) {
            if (args[0].equals("forcepay")) {
                return matchTo(args, new String[]{ "reset" });
            }
            else if (args[0].equals("setprop")) {
                return matchTo(args, Router.getCfg().getPropKeys());
            }
        }
        else if (args.length == 3 && user.hasPermission("mysalary.admin")) {
            if (propBool.reset(args[1]).matches()) {
                return matchTo(args, new String[]{ "yes", "no", "true", "false" });
            }
        }
        return null;
    }
}
