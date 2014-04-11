package org.homeunix.thecave.buddi.model.impl;

import org.homeunix.thecave.buddi.model.BudgetCategoryType;

import java.util.Date;

public class BudgetPeriod {
    private Period period;

    @Override
    public boolean equals(Object o) {
        BudgetPeriod another = (BudgetPeriod) o;
        return this.period.equals(another.period);
    }

    public BudgetPeriod(BudgetCategoryType type, Date date) {
        this.period = new Period(type.getStartOfBudgetPeriod(date), type.getEndOfBudgetPeriod(date));
    }
}
