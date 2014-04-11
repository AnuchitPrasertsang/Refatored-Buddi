package org.homeunix.thecave.buddi.model.impl;

import org.homeunix.thecave.buddi.model.BudgetCategoryType;

import java.awt.*;
import java.util.Date;

public class BudgetPeriod {
    private Period period;
    private BudgetCategoryType type;

    @Override
    public boolean equals(Object o) {
        BudgetPeriod another = (BudgetPeriod) o;
        return this.period.equals(another.period);
    }

    public BudgetPeriod(BudgetCategoryType type, Date date) {
        this.period = new Period(type.getStartOfBudgetPeriod(date), type.getEndOfBudgetPeriod(date));
        this.type = type;
    }

    public BudgetPeriod nextBudgetPeriod() {
        return new BudgetPeriod(type, type.getStartOfNextBudgetPeriod(period.getStartDate()));
    }

    public Date getStartDate() {
        return period.getStartDate();
    }

    public Date getEndDate() {
        return period.getEndDate();
    }

    public BudgetPeriod previousBudgetPeriod() {
        return new BudgetPeriod(type, type.getStartOfPreviousBudgetPeriod(period.getStartDate()));
    }

    public long getDayCount() {
        return period.getDayCount();
    }
}
