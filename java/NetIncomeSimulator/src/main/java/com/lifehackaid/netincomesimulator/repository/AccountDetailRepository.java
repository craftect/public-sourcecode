package com.lifehackaid.netincomesimulator.repository;

import java.util.List;
import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.lifehackaid.netincomesimulator.dao.AccountDetailMapper;
import com.lifehackaid.netincomesimulator.entity.AccountDetailEntity;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;

@Repository
public class AccountDetailRepository {

	@Mapper
	private AccountDetailMapper accountDetailMapper;

	public AccountDetailRepository(AccountDetailMapper accountDetailMapper) {
		this.accountDetailMapper = accountDetailMapper;
	}

	public List<AccountDetailForm> loadAccountDetailFormList(String recordId) {

		List<AccountDetailEntity> accountDetailEntityList = new ArrayList<AccountDetailEntity>();
		List<AccountDetailForm> accountDetailFormList = new ArrayList<AccountDetailForm>();

		accountDetailEntityList = accountDetailMapper.findAllById(recordId);

		for (AccountDetailEntity accountDetailEntity : accountDetailEntityList) {
			accountDetailFormList.add(populateToAccountDetailForm(accountDetailEntity));
		}

		return accountDetailFormList;

	}

	public void saveAccountDetailFormList(String recordId, List<AccountDetailForm> accountDetailFormList) {

		List<AccountDetailEntity> accountDetailEntityList = new ArrayList<AccountDetailEntity>();

		for (AccountDetailForm accountDetailForm : accountDetailFormList) {
			accountDetailEntityList.add(populateToAccountDetailEntity(recordId, accountDetailForm));
		}

		accountDetailMapper.insertInBulk(accountDetailEntityList);

	}

	public void removeAccountDetailFormList(String recordId) {

		accountDetailMapper.delete(recordId);

	}

	// support method

	private AccountDetailEntity populateToAccountDetailEntity(String recordId, AccountDetailForm accountDetailForm) {

		AccountDetailEntity accountDetailEntity = new AccountDetailEntity();

		accountDetailEntity.setId(recordId);
		accountDetailEntity.setCategory(accountDetailForm.getCategory());
		accountDetailEntity.setUnitValue(accountDetailForm.getUnitValue());
		accountDetailEntity.setUnitAmount(accountDetailForm.getUnitAmount());
		accountDetailEntity.setSummary(accountDetailForm.getSummary());
		accountDetailEntity.setKeepValue(accountDetailForm.getKeepValue());

		return accountDetailEntity;

	}

	private AccountDetailForm populateToAccountDetailForm(AccountDetailEntity accountDetailEntity) {

		// @formatter:off
		return (new AccountDetailForm(
				accountDetailEntity.getCategory(),
				accountDetailEntity.getSummary(),
				accountDetailEntity.getUnitValue(),
				accountDetailEntity.getUnitAmount(),
				accountDetailEntity.getKeepValue()
		));
		// @formatter:on

	}

}