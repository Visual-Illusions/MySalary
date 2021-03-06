/*
 * This file is part of MySalary.
 *
 * Copyright © 2011-2015 Visual Illusions Entertainment
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

import net.visualillusionsent.dconomy.accounting.AccountNotFoundException;
import net.visualillusionsent.dconomy.accounting.AccountingException;
import net.visualillusionsent.dconomy.api.account.wallet.WalletAPIListener;
import net.visualillusionsent.dconomy.api.dConomyUser;
import net.visualillusionsent.dconomy.dCoBase;
import net.visualillusionsent.utils.DateUtils;
import net.visualillusionsent.utils.PropertiesFile;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Finance Department
 *
 * @author Jason (darkdiplomat)
 */
public final class Finance {
    private final PropertiesFile _pending, _reset;
    private final MySalary mys;
    private Timer timer;

    public Finance(MySalary mys) {
        this.mys = mys;
        _reset = new PropertiesFile("config/MySalary/.reset.mys");
        _reset.getLong("timer.reset", -1);
        _pending = new PropertiesFile("config/MySalary/.pending.mys");
        reset(true);
    }

    public final void payout() {
        mys.broadcastPayDay();
        for (UUID owner : WalletAPIListener.getWalletOwners()) {
            // check wallet lock status
            try {
                if (WalletAPIListener.isLocked(owner) && !Router.getCfg().payIfLocked())
                    continue;
            }
            catch (AccountNotFoundException anfex) {
                // Impossible
            }
            double pay = Router.getCfg().getDefaultPayAmount();
            // check if group amount is needed
            if (Router.getCfg().isGroupSpecificEnabled()) {
                String group = mys.getGroupNameForUser(owner);
                // if no group, ignore wallet and continue
                if (group == null)
                    continue;
                pay = Router.getCfg().getGroupPay(group);
                // if group doesn't have an amount, ignore it and continue
                if (pay <= 0)
                    continue;
            }

            // Check if player's are required to claim their pay
            if (Router.getCfg().isRequireClaimEnabled()) {
                // Check if checks can be accumulated
                if (Router.getCfg().isAccumulateChecksEnabled()) {
                    // if an owner has a pending check, add to it
                    if (_pending.containsKey(owner.toString())) {
                        _pending.setDouble(owner.toString(), _pending.getDouble(owner.toString()) + pay);
                    }
                    else { // just add them to the list
                        _pending.setDouble(owner.toString(), pay);
                    }
                }
                else { // accumulating is disabled, so the current pay is set and the old check is burned
                    _pending.setDouble(owner.toString(), pay);
                }
            }
            else {
                try {
                    dConomyUser user = dCoBase.getServer().getUserFromUUID(owner);
                    if (user == null) {
                        mys.getPluginLogger().severe("Was unable to locate User: " + owner + ".");
                        continue;
                    }
                    WalletAPIListener.walletDeposit(mys, user, pay, false);
                    mys.messageUser(owner, "salary.received", pay, dCoBase.getProperties().getString("money.name"));
                }
                catch (AccountingException aex) {
                    mys.getPluginLogger().severe("Accounting Exception occurred while trying to pay User: " + owner + ". Reason: " + aex.getMessage());
                }
                catch (AccountNotFoundException iamyourfather) {
                    // THAT'S IMPOSSIBLE
                }
                catch (Exception ex) {
                    mys.getPluginLogger().severe("Exception occurred while trying to pay User: " + owner + ". Reason: " + ex.getCause());
                }
            }
        }
        if (Router.getCfg().payServer()) {
            double serv_pay = Router.getCfg().getDefaultPayAmount();
            if (Router.getCfg().isGroupSpecificEnabled()) {
                serv_pay = Router.getCfg().getGroupPay("SERVER");
                if (serv_pay <= 0)
                    return;
            }
            try {
                WalletAPIListener.walletDeposit(mys, dCoBase.getServer(), serv_pay, false);
            }
            catch (AccountingException aex) {
                mys.getPluginLogger().severe("Accounting Exception occurred while trying to pay SERVER. Reason: " + aex.getMessage());
            }
            catch (AccountNotFoundException anfex) {
                // still impossible
            }
        }
    }

    public final void reset(boolean booting) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        long period = Router.getCfg().getDelay();
        if (_reset.getLong("timer.reset") > -1 && booting) {
            period = _reset.getLong("timer.reset") - System.currentTimeMillis();
            if (period <= 0) {
                period = 60000; //reset to at least a minute so the server has time to get going
                _reset.setLong("timer.reset", System.currentTimeMillis() + 60000);
            }
        }
        else {
            _reset.setLong("timer.reset", System.currentTimeMillis() + Router.getCfg().getDelay());
        }
        timer.schedule(new SalaryTask(), period, Router.getCfg().getDelay());
    }

    public final double checkPendingAndPay(String user_name) {
        if (_pending.containsKey(user_name)) {
            try {
                double pay = _pending.getDouble(user_name);
                WalletAPIListener.walletDeposit(mys, user_name, pay, false);
                _pending.removeKey(user_name);
                return pay;
            }
            catch (AccountingException aex) {
                dConomyUser user = dCoBase.getServer().getUser(user_name);
                user.error(aex.getLocalizedMessage(user.getUserLocale()));
                return -1;
            }
            catch (AccountNotFoundException anfex) {
                return -1;
            }
        }
        return 0;
    }

    public final String getTimeUntil() {
        return DateUtils.getTimeUntil((_reset.getLong("timer.reset") - System.currentTimeMillis()) / 1000);
    }

    public final void close() {
        _pending.save();
        _reset.save();
        timer.cancel();
    }

    private final class SalaryTask extends TimerTask {

        @Override
        public void run() {
            payout();
            _reset.setLong("timer.reset", System.currentTimeMillis() + Router.getCfg().getDelay());
        }
    }
}
