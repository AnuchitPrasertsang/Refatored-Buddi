/*
 * Created on Jul 29, 2007 by wyatt
 */
package org.homeunix.thecave.buddi.model.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.homeunix.thecave.buddi.model.BudgetCategory;
import org.homeunix.thecave.buddi.model.BudgetCategoryType;
import org.homeunix.thecave.buddi.model.ModelObject;
import org.homeunix.thecave.buddi.model.impl.FilteredLists.BudgetCategoryListFilteredByDeleted;
import org.homeunix.thecave.buddi.model.impl.FilteredLists.BudgetCategoryListFilteredByParent;
import org.homeunix.thecave.buddi.plugin.api.exception.DataModelProblemException;
import org.homeunix.thecave.buddi.plugin.api.exception.InvalidValueException;
import org.homeunix.thecave.moss.util.DateFunctions;
import org.homeunix.thecave.moss.util.Log;

/**
 * Default implementation of an BudgetCategory.  You should not create this object directly; 
 * instead, please use the ModelFactory to create it, as this will ensure that all
 * required fields are correctly set.
 * @author wyatt
 *
 */
public class BudgetCategoryImpl extends SourceImpl implements BudgetCategory {
	private boolean income;
	private boolean expanded;
	private BudgetCategoryType periodType;
	private BudgetCategory parent;
	private Map<String, Long> amounts;
	private List<BudgetCategory> children;
	private List<BudgetCategory> allChildren;
	
	public Map<String, Long> getAmounts() {
		if (amounts == null)
			amounts = new HashMap<String, Long>();
		return amounts;
	}
	public void setAmounts(Map<String, Long> amounts) {
		this.amounts = amounts;
	}
	/**
	 * Returns the budgeted amount associated with the given budget category, for 
	 * the date in which the given period date exists.
	 * @param periodDate
	 * @return
	 */
	public long getAmount(Date periodDate){
		Long l = getAmounts().get(getPeriodKey(periodDate));
		if (l == null)
			return 0;
		return l;
	}
	
	@Override
	public void setDeleted(boolean deleted) throws InvalidValueException {
		if (getDocument() != null)
			getDocument().startBatchChange();
		//We need to delete / undelete ancestors / descendents as needed.  The rule to follow is that
		// we cannot have any account which is not deleted which has a parent which is deleted.
		//We also cannot set any children deleted if the document is not set.  This should be fine
		// for almost all operations - the only potential problem would be if you create a budget
		// category, add some children to it, delete the parent, and then add the parent.  
		// TODO Check for this condition
		if (getDocument() != null){
			if (deleted){
				//If we delete this one, we must also delete all children.
				for (BudgetCategory bc : getChildren()) {
					bc.setDeleted(deleted);
				}
			}
			else {
				//If we undelete this one, we must also undelete all ancestors.  
				if (getParent() != null)
					getParent().setDeleted(deleted);
			}
		}
		
		setChanged();
		
		super.setDeleted(deleted);
		
		if (getDocument() != null)
			getDocument().finishBatchChange();
	}
	
	public List<BudgetCategory> getChildren() {
		if (children == null)
			children = new BudgetCategoryListFilteredByDeleted(getDocument(), new BudgetCategoryListFilteredByParent(getDocument(), getDocument().getBudgetCategories(), this));
		return children;
	}
	
	public List<BudgetCategory> getAllChildren() {
		if (allChildren == null)
			allChildren = new BudgetCategoryListFilteredByParent(getDocument(), getDocument().getBudgetCategories(), this);
		return allChildren;
	}	
	
	public long getAmount(Date startDate, Date endDate){
		if (startDate.after(endDate))
			throw new RuntimeException("Start date cannot be before End Date!");
		
		Log.info("Starting to calculate the budgeted amount for " + getFullName() + " between " + startDate + " and " + endDate + ".");
		
		//If Start and End are in the same budget period
		if (getBudgetPeriodType().getStartOfBudgetPeriod(startDate).equals(
				getBudgetPeriodType().getStartOfBudgetPeriod(endDate))){
			Log.info("Start Date and End Date are in the same period.");
			long amount = getAmount(startDate);
			Log.info("Amount = " + amount);
			long daysInPeriod = getBudgetPeriodType().getDaysInPeriod(startDate);
			Log.info("Days in Period = " + daysInPeriod);
			long daysBetween = DateFunctions.getDaysBetween(startDate, endDate, true);
			Log.info("Days Between = " + daysBetween);
		
			Log.info("Returning " + (long) (((double) amount / (double) daysInPeriod) * daysBetween));
			Log.info("Finished calculating the budget amount.\n\n");
			return (long) (((double) amount / (double) daysInPeriod) * daysBetween);
		}
		 
		//If the area between Start and End overlap at least two budget periods. 
		if (getBudgetPeriodType().getBudgetPeriodOffset(startDate, 1).equals(
				getBudgetPeriodType().getStartOfBudgetPeriod(endDate))
				|| getBudgetPeriodType().getBudgetPeriodOffset(startDate, 1).before(
						getBudgetPeriodType().getStartOfBudgetPeriod(endDate))){
			Log.info("Start Date and End Date are in different budget periods.");
			long amountStartPeriod = getAmount(startDate);
			Log.info("Amount Start Period = " + amountStartPeriod);
			long daysInStartPeriod = getBudgetPeriodType().getDaysInPeriod(startDate);
			Log.info("Days in Start Period = " + daysInStartPeriod);
			long daysAfterStartDateInStartPeriod = DateFunctions.getDaysBetween(startDate, getBudgetPeriodType().getEndOfBudgetPeriod(startDate), true);
			Log.info("Days After Start Date in Start Period = " + daysAfterStartDateInStartPeriod);
			double totalStartPeriod = (((double) amountStartPeriod / (double) daysInStartPeriod) * daysAfterStartDateInStartPeriod);
			Log.info("Total in Start Period = " + totalStartPeriod);
			
			double totalInMiddle = 0;
			for (String periodKey : getBudgetPeriods(
					getBudgetPeriodType().getBudgetPeriodOffset(startDate, 1),
					getBudgetPeriodType().getBudgetPeriodOffset(endDate, -1))) {
				totalInMiddle += getAmount(getPeriodDate(periodKey));
				Log.info("Added " + getAmount(getPeriodDate(periodKey)) + " to total for one period in between; current value is " + totalInMiddle);
			}
			Log.info("Total in Middle = " + totalInMiddle);
			
			long amountEndPeriod = getAmount(endDate);
			Log.info("Amount End Period = " + amountEndPeriod);
			long daysInEndPeriod = getBudgetPeriodType().getDaysInPeriod(endDate);
			Log.info("Days in End Period = " + daysInEndPeriod);
			long daysBeforeEndDateInEndPeriod = DateFunctions.getDaysBetween(getBudgetPeriodType().getStartOfBudgetPeriod(endDate), endDate, true);
			Log.info("Days before End Period = " + daysBeforeEndDateInEndPeriod);
			double totalEndPeriod = (long) (((double) amountEndPeriod / (double) daysInEndPeriod) * daysBeforeEndDateInEndPeriod); 
			Log.info("Total in End Period = " + totalEndPeriod);
			
			Log.info("Sum of Start Period, Middle, and End Period = " + (totalStartPeriod + totalInMiddle + totalEndPeriod));
			Log.info("Finished Calculating the Budget Amount\n\n");
			return (long) (totalStartPeriod + totalInMiddle + totalEndPeriod);
		}

		throw new RuntimeException("You should not be here.  We have returned all legitimate numbers from getAmount(Date, Date) in BudgetCategoryImpl.  Please contact Wyatt Olson with details on how you got here (what steps did you perform in Buddi to get this error message).");
	}
	
	/**
	 * Returns a list of BudgetPeriods, covering the entire range of periods
	 * occupied by startDate to endDate.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<String> getBudgetPeriods(Date startDate, Date endDate){
		List<String> budgetPeriodKeys = new LinkedList<String>();

		Date temp = getBudgetPeriodType().getStartOfBudgetPeriod(startDate);

		while (temp.before(getBudgetPeriodType().getEndOfBudgetPeriod(endDate))){
			budgetPeriodKeys.add(getPeriodKey(temp));
			temp = getBudgetPeriodType().getBudgetPeriodOffset(temp, 1);
		}

		return budgetPeriodKeys;
	}
	
	/**
	 * Sets the budgeted amount for the given time period.
	 * @param periodDate
	 * @param amount
	 */
	public void setAmount(Date periodDate, long amount){
		if (getAmount(periodDate) != amount)
			setChanged();
		getAmounts().put(getPeriodKey(periodDate), amount);
	}
	public BudgetCategoryType getPeriodType() {
		return periodType;
	}
	public void setPeriodType(BudgetCategoryType periodType) {
		this.periodType = periodType;
		setChanged();
	}
	public boolean isIncome() {
		return income;
	}
	public void setIncome(boolean income) {
		this.income = income;
		setChanged();
	}
	@Override
	public void setName(String name) throws InvalidValueException {
//		if (getDocument() != null){
//			for (BudgetCategory bc : ((Document) getDocument()).getBudgetCategories()) {
//				if (bc.getName().equalsIgnoreCase(name)
//						&& !bc.equals(this)
//						&& ((bc.getParent() == null && this.getParent() == null)
//								|| (bc.getParent() != null && this.getParent() != null && bc.getParent().equals(this.getParent()))))
//					throw new InvalidValueException("The budget category name must be unique for nodes which share the same parent");
//			}
//		}
		super.setName(name);
	}
	public BudgetCategory getParent() {
		return parent;
	}
	public void setParent(BudgetCategory parent) {
		this.parent = parent;
		setChanged();
	}
	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	/**
	 * Returns the key which is associated with the date contained within the
	 * current budget period.  The string is constructed as follows:
	 * 
	 * <code>String periodKey = getPeriodType() + ":" + getStartOfBudgetPeriod(periodDate).getTime();</code>
	 * 
	 * @param periodDate
	 * @return
	 */
	private String getPeriodKey(Date periodDate){
		Date d = getBudgetPeriodType().getStartOfBudgetPeriod(periodDate); 
		return getBudgetPeriodType().getName() + ":" + DateFunctions.getYear(d) + ":" + DateFunctions.getMonth(d) + ":" + DateFunctions.getDay(d);
	}
	/**
	 * Parses a periodKey to get the date 
	 * @param periodKey
	 * @return
	 */
	private Date getPeriodDate(String periodKey){
		String[] splitKey = periodKey.split(":");
		if (splitKey.length == 4){
			int year = Integer.parseInt(splitKey[1]);
			int month = Integer.parseInt(splitKey[2]);
			int day = Integer.parseInt(splitKey[3]);
			return getBudgetPeriodType().getStartOfBudgetPeriod(DateFunctions.getDate(year, month, day));
		}

		throw new DataModelProblemException("Cannot parse date from key " + periodKey);
	}
	public String getFullName(){
		if (getDocument() != null && getParent() != null){
			for (BudgetCategory bc : getDocument().getBudgetCategories()) {
				if (bc.getName().equals(this.getName())
						&& !bc.equals(this))
					return this.getName() + " (" + this.getParent().getFullName() + ")";
			}
		}
		return this.getName();
	}
	/**
	 * Returns the Budget Period type.  One of the values in Enum BudgePeriodKeys.
	 * @return
	 */
	public BudgetCategoryType getBudgetPeriodType() {
		if (getPeriodType() == null)
			setPeriodType(new BudgetCategoryTypeMonthly());
		return getPeriodType();
	}
	@Override
	public int compareTo(ModelObject arg0) {
		if (arg0 instanceof BudgetCategoryImpl){
			BudgetCategoryImpl c = (BudgetCategoryImpl) arg0;
			if (this.isIncome() != c.isIncome())
				return -1 * new Boolean(this.isIncome()).compareTo(new Boolean(c.isIncome()));
			return this.getFullName().compareTo(c.getFullName());
		}
		return super.compareTo(arg0);
	}
}
