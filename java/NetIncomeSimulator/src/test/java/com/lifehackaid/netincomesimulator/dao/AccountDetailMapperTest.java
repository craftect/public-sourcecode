package com.lifehackaid.netincomesimulator.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.lifehackaid.netincomesimulator.common.DateUUIDGeneratorUtil;
import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.dao.AccountDetailMapper;
import com.lifehackaid.netincomesimulator.entity.AccountDetailEntity;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;
import com.lifehackaid.netincomesimulator.repository.AccountDetailRepository;

@SpringBootTest
public class AccountDetailMapperTest {

	@Autowired
	private AccountDetailMapper accountDetailMapper;

	@BeforeEach
	public void setUp() {

	}

	// Entity関連の一連の操作(CRUD)がテストできているか確認するテスト
	@Test
	public void testAccountDetailEntityBasicOperations() {

		AccountDetailEntity testedEntity1 = new AccountDetailEntity();

		testedEntity1.setId(DateUUIDGeneratorUtil.generateDateUUID());
		testedEntity1.setCategory(AccountCategories.Revenue);
		testedEntity1.setKeepValue(true);
		testedEntity1.setSummary("Test1");
		testedEntity1.setUnitAmount(10L);
		testedEntity1.setUnitValue(1000L);

		AccountDetailEntity testedEntity2 = new AccountDetailEntity();

		testedEntity2.setId(testedEntity1.getId());
		testedEntity2.setCategory(AccountCategories.NecessaryExpenses);
		testedEntity2.setKeepValue(false);
		testedEntity2.setSummary("Test2");
		testedEntity2.setUnitAmount(20L);
		testedEntity2.setUnitValue(2000L);

		AccountDetailEntity testedEntity3 = new AccountDetailEntity();

		testedEntity3.setId(testedEntity1.getId());
		testedEntity3.setCategory(AccountCategories.NecessaryExpenses);
		testedEntity3.setKeepValue(false);
		testedEntity3.setSummary("Test3");
		testedEntity3.setUnitAmount(30L);
		testedEntity3.setUnitValue(3000L);

		List<AccountDetailEntity> testedList = new ArrayList<>();

		// Create:
		testedList.add(testedEntity1);
		testedList.add(testedEntity2);
		testedList.add(testedEntity3);
		accountDetailMapper.insertInBulk(testedList);


		// Read:取得したレコード数がテスト対象データの件数と同等か
		List<AccountDetailEntity> resultList = accountDetailMapper.findAllById(testedEntity1.getId());
		assertEquals(testedList.size(), resultList.size());

		// Delete:
		accountDetailMapper.delete(testedEntity2.getId());
		assertEquals(accountDetailMapper.findAllById(testedEntity2.getId()).size(), 0);

	}

}
