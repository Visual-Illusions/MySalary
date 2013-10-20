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

/** @author Jason (darkdiplomat) */
public final class Router {
    private static Router ROUTER;
    private final MessageTranslator translator;
    private final MySalaryConfiguration my_cfg;
    private final Finance finance;

    public Router(MySalary mysalary) {
        ROUTER = this;
        this.my_cfg = new MySalaryConfiguration(mysalary);
        this.translator = new MessageTranslator();
        this.finance = new Finance(mysalary);
    }

    public final static MessageTranslator getTranslator() {
        return ROUTER.translator;
    }

    public final static MySalaryConfiguration getCfg() {
        return ROUTER.my_cfg;
    }

    public final static Finance getFinance() {
        return ROUTER.finance;
    }

    public final static void closeConnection() {
        if (ROUTER.finance != null) {
            ROUTER.finance.close();
        }
    }
}
