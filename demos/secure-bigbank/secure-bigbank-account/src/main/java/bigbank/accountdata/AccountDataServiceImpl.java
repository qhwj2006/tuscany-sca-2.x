/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package bigbank.accountdata;

import org.osoa.sca.annotations.Service;

/**
 * @version $$Rev: 540764 $$ $$Date: 2007-05-23 03:17:57 +0530 (Wed, 23 May 2007) $$
 */

@Service(AccountDataService.class)
public class AccountDataServiceImpl implements AccountDataService {

	public CheckingAccount getCheckingAccount(String customerID) {

		CheckingAccount checkingAccount = new CheckingAccount();
		checkingAccount.setAccountNumber("CHA_" + customerID);
		checkingAccount.setBalance(500.0f);

		return checkingAccount;
	}

	public SavingsAccount getSavingsAccount(String customerID) {

		SavingsAccount savingsAccount = new SavingsAccount();
		savingsAccount.setAccountNumber("SAA_" + customerID);
		savingsAccount.setBalance(1500.0f);

		return savingsAccount;
	}

	public StockAccount getStockAccount(String customerID) {

		StockAccount stockAccount = new StockAccount();
		stockAccount.setAccountNumber("STA_" + customerID);
		stockAccount.setSymbol("IBM");
		stockAccount.setQuantity(100);

		return stockAccount;
	}
}
