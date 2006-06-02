/*
 * Created on May 6, 2006 by wyatt
 */
package org.homeunix.drummer.view.logic;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.homeunix.drummer.Translate;
import org.homeunix.drummer.controller.DataInstance;
import org.homeunix.drummer.controller.PrefsInstance;
import org.homeunix.drummer.model.Account;
import org.homeunix.drummer.util.Formatter;
import org.homeunix.drummer.util.Log;
import org.homeunix.drummer.view.AbstractBudgetPanel;
import org.homeunix.drummer.view.layout.ListPanelLayout;

public class AccountListPanel extends ListPanelLayout {
	public static final long serialVersionUID = 0;
		
	public AccountListPanel(){
		super();
	}
	
	protected AbstractBudgetPanel initActions(){
		super.initActions();
		
		newButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				tree.clearSelection();
				new AccountModifyDialog().clearContent().openWindow();
			}
		});
		
		editButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new AccountModifyDialog().loadSource(getSelectedAccount()).openWindow();
			}
		});

		deleteButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (getSelectedAccount() != null) {
					
					Account a = getSelectedAccount();
					
					if (deleteButton.getText().equals(Translate.inst().get(Translate.DELETE))){
						
						//If there are no transactions using this source, ask if user wants to permanently delete source
						if (DataInstance.getInstance().getTransactions(a).size() > 0 
								|| JOptionPane.showConfirmDialog(
										AccountListPanel.this,
										Translate.inst().get(Translate.NO_TRANSACTIONS_USING_ACCOUNT),
										Translate.inst().get(Translate.PERMANENT_DELETE_ACCOUNT),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION){
							
							DataInstance.getInstance().deleteSource(a);
						}
						else{
							DataInstance.getInstance().deleteSourcePermanent(a);
						}
					}
					else{
						DataInstance.getInstance().undeleteSource(a);
					}
					
					//We always want to update everything.  It's the cool thing to do.
					AccountListPanel.this.updateContent();
					AccountListPanel.this.updateButtons();
				}
			}
		});

		
		openButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (getSelectedAccount() != null)
					new TransactionsFrame(getSelectedAccount());
			}
		});
		
		tree.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() >= 2)
					openButton.doClick();
				super.mouseClicked(arg0);
			}
		});

		return this;
	}
	
	protected AbstractBudgetPanel initContent(){
		DataInstance.getInstance().calculateAllBalances();
		
		return this;
	}
	
	public AbstractBudgetPanel updateContent(){
		int balance = 0;
		
		root.removeAllChildren(); 
		selectedSource = null;
		
		for (Account a : DataInstance.getInstance().getAccounts()) {
			if (!a.isDeleted() || PrefsInstance.getInstance().getPrefs().isShowDeletedAccounts())
				root.add(new DefaultMutableTreeNode(a));
			
			Log.debug(a);
			
			balance += a.getBalance();
		}
				
		if (balance >= 0)
			balanceLabel.setForeground(Color.BLACK);
		else
			balanceLabel.setForeground(Color.RED);
		
		balanceLabel.setText(Translate.inst().get(Translate.NET_WORTH) + ": " + (balance >= 0 ? "" : "-") + Translate.inst().get(Translate.CURRENCY_SIGN) + Formatter.getInstance().getDecimalFormat().format(Math.abs((double) Math.abs(balance) / 100.0)));
		
		treeModel.reload(root);
		
		return this;
	}
	
	public Account getSelectedAccount(){
		
		Log.debug(selectedSource);
		if (selectedSource instanceof Account)
			return (Account) selectedSource;
		else
			return null;
	}
}
