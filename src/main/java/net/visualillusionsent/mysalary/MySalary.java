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

import net.visualillusionsent.dconomy.api.dConomyAddOn;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * MySalary interface
 *
 * @author Jason (darkdiplomat)
 */
public interface MySalary extends dConomyAddOn {

    void broadcastPayDay();

    void messageUser(UUID userUUID, String message_key, Object... args);

    String getGroupNameForUser(UUID userUUID);

    Logger getPluginLogger();
}
