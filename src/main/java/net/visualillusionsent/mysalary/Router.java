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

import net.visualillusionsent.dconomy.dCoBase;

/** @author Jason (darkdiplomat) */
public final class Router {
    private static Router ROUTER;
    private final SalaryTranslator translator;
    private final MySalaryConfiguration my_cfg;
    private final Finance finance;
    private final float dCoVersion = 3.0F;
    private final long dCoRevision = 0;

    public Router(MySalary mysalary) {
        ROUTER = this;
        if (dCoBase.isNewerThan(dCoVersion, dCoRevision)) {
            mysalary.getPluginLogger().warning("dConomy appears to be a newer version. Incompatibility could result...");
        }

        this.my_cfg = new MySalaryConfiguration(mysalary);
        this.translator = new SalaryTranslator(mysalary, my_cfg.updateLang());
        this.finance = new Finance(mysalary);
    }

    public final static SalaryTranslator getTranslator() {
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
