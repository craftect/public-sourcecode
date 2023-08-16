package com.lifehackaid.netincomesimulator.repository;

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

import com.lifehackaid.netincomesimulator.common.AccountDetailBusinessUtil;
import com.lifehackaid.netincomesimulator.common.DateUUIDGeneratorUtil;
import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.dao.AccountDetailMapper;
import com.lifehackaid.netincomesimulator.entity.AccountDetailEntity;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;
import com.lifehackaid.netincomesimulator.repository.AccountDetailRepository;

@SpringBootTest
public class AccountDetailRepositoryTest {

	@Autowired
	private AccountDetailRepository accountDetailRepository;

	@BeforeEach
	public void setUp() {

	}

	// ユースケース（画面内のリストの登録→読込）を意識したRepositoryクラスのテスト。（ユースケース上、deleteは使わないが将来のために実装）
	@Test
	public void testAccountDetailRepository() {

		List<AccountDetailForm> verifyingAccountDetailFormList = AccountDetailBusinessUtil.createDefaultAccountDetailFormList();
		String testRecordId = DateUUIDGeneratorUtil.generateDateUUID();

		// 予めテストデータがないことを確認
		assertEquals(accountDetailRepository.loadAccountDetailFormList(testRecordId).size(), 0);

		// Create:登録処理確認
		accountDetailRepository.saveAccountDetailFormList(testRecordId, verifyingAccountDetailFormList);

		// Read:登録したデータの件数確認
		assertEquals(accountDetailRepository.loadAccountDetailFormList(testRecordId).size(),
				verifyingAccountDetailFormList.size());

		// Delete:削除処理確認
		accountDetailRepository.removeAccountDetailFormList(testRecordId);
		assertEquals(accountDetailRepository.loadAccountDetailFormList(testRecordId).size(),0);

	}


}
