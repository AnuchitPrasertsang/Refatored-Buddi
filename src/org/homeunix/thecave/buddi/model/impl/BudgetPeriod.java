package org.homeunix.thecave.buddi.model.impl;

import org.homeunix.thecave.buddi.model.BudgetCategoryType;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
        return new BudgetPeriod(type, type.getBudgetPeriodOffset(period.getStartDate(), 1));
    }

    public Date getStartDate() {
        return period.getStartDate();
    }

    private Date getEndDate() {
        return period.getEndDate();
    }

    public long getDayCount() {
        return period.getDayCount();
    }

    public Period getPeriod() {
        return period;
    }

    /**
     * Returns a list of BudgetPeriods, covering the entire range of periods
     * occupied by startDate to endDate.
     *
     *
     * @param endBudgetPeriod @return
     */
    public List<BudgetPeriod> getBudgetPeriodsUntil(BudgetPeriod endBudgetPeriod) {
        List<BudgetPeriod> budgetPeriods = new LinkedList<BudgetPeriod>();

        BudgetPeriod current = this;

        while (current.getStartDate().before(endBudgetPeriod.getEndDate())) {
            budgetPeriods.add(current);
            current = current.nextBudgetPeriod();
        }

        return budgetPeriods;
    }
}
