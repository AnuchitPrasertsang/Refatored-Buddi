package org.homeunix.thecave.buddi.model.impl;

import ca.digitalcave.moss.common.DateUtil;

import java.util.Date;

public class Period {
    private Date startDate;
    private Date endDate;

    public Period(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        Period another = (Period) o;
        return this.startDate.equals(another.startDate);
    }

    public long getDayCount() {
        return DateUtil.getDaysBetween(startDate, endDate, true);
    }

    long getOverlappingDayCount(Period anotherPeriod) {
        return new Period(startDate, anotherPeriod.endDate).getDayCount();
    }
}
