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
package net.visualillusionsent.mysalary;

import net.visualillusionsent.dconomy.api.dConomyUser;

/**
 * MySalary interface
 *
 * @author Jason (darkdiplomat)
 */
public interface MySalary extends dConomyUser {

    String getJarPath();

    MySalaryConfiguration getCfg();

    void broadcastPayDay();

    void messageUser(String user_name, String message);

    String getGroupNameForUser(String user_name);

    Finance getFinance();
}
