package com.lifehackaid.netincomesimulator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;

@SpringBootTest
public class PresentationProcessHandleServiceWithSpiedCaseTest {

    @Mock
    private BindingResult bindingResult;

	@Spy
	private RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

	@Autowired
	@InjectMocks
	private PresentationProcessHandleService testedService;

	private AccountDetailForm accountDetailFormForTest = new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L,
			12L, true);

	@Test
	public void addNotificationMessage() {

		testedService.addNotificationMessage(redirectAttributes, "ui.SaveDataCompletion");

		List<String> messages = (List<String>) redirectAttributes.getFlashAttributes().get("notificationMessages");
		assertNotNull(messages, "Messages should not be null"); // 必要に応じてNULLチェックを追加
		assertFalse(messages.isEmpty(), "Messages should not be empty"); // メッセージが空でないことを確認
		String actualMessage = messages.get(0); // 最初のメッセージを取得

		assertEquals(actualMessage, "データを保存しました。URLを保存しておくとデータが復元できます。");

	}

}
