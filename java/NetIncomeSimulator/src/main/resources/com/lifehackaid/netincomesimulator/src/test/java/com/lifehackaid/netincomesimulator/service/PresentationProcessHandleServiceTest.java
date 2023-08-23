package com.lifehackaid.netincomesimulator.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.lifehackaid.netincomesimulator.common.DateUUIDGeneratorUtil;
import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;
import com.lifehackaid.netincomesimulator.repository.AccountDetailRepository;

@SpringBootTest
public class PresentationProcessHandleServiceTest {

	@Mock
	private MessageSource messageSource;

	@Mock
	private NetIncomeCalcService netIncomeCalcService;

	@InjectMocks
	private PresentationProcessHandleService testedService;

	@Mock
	private BindingResult bindingResult;

	@Mock
	private AccountDetailRepository accountDetailRepository;

	@Spy
	private RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

	private AccountDetailForm accountDetailFormForTest = new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L,
			12L, true);

	@Test
    public void testHasBindingErrorWithMsgProcess_NoErrors() {

    	when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());
        
        boolean result = testedService.hasBindingErrorWithMsgProcess(redirectAttributes, bindingResult);

        assertFalse(result);
    }

	@Test
	public void testHasBindingErrorWithMsgProcess_WithErrors() {
		List<FieldError> fieldErrors = new ArrayList<FieldError>();
		fieldErrors.add(new FieldError("objectName", "field", "defaultMessage"));

		when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
		when(messageSource.getMessage(any(), any())).thenReturn("testMessage");

		// 前画面にエラーMSGが残っている場合を考慮して予めエラーメッセージをFlashスコープにセットしておく
		List<String> inheritedErrorMessages = new ArrayList<String>();
		inheritedErrorMessages.add("inheritedTestMessage");
		redirectAttributes.addFlashAttribute("errorMessages", inheritedErrorMessages);

		System.out.println(redirectAttributes.getFlashAttributes().get("errorMessages"));

		boolean result = testedService.hasBindingErrorWithMsgProcess(redirectAttributes, bindingResult);

		assertTrue(result);

	}

	// Create（Add）Case

	@Test
    public void testHasErrorToAddAccountDetailWithMsgProcess_ErrorMessagesExist() {
    	when(netIncomeCalcService.calcTaxableIncomeAmount(anyList())).thenReturn(-1L);

        assertTrue(testedService.hasErrorToAddAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<>(), accountDetailFormForTest));
    }

	@Test
    public void testHasErrorToAddAccountDetailWithMsgProcess_NoErrors() {
        when(netIncomeCalcService.calcTaxableIncomeAmount(anyList())).thenReturn(1L);

        assertFalse(testedService.hasErrorToAddAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<>(), accountDetailFormForTest));
    }

	// Read(Show)Case

	@Test
	public void testHasErrorToShowAccountDetailWithMsgProcess_WrongLengthRecordId() {
		
		// RecordID長が誤っているケース。
		assertTrue(testedService.hasErrorToShowAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<>(),
				"dummyId"));
	}

	@Test
	public void testHasErrorToShowAccountDetailWithMsgProcess_WrongNumberRecordId() {

		// 前画面にエラーMSGが残っている場合を考慮して予めエラーメッセージをFlashスコープにセットしておく
		List<String> inheritedErrorMessages = new ArrayList<String>();
		inheritedErrorMessages.add("inheritedTestMessage");
		redirectAttributes.addFlashAttribute("errorMessages", inheritedErrorMessages);
		
		// RecordID長は正しいが、該当RecordIDに紐づくデータが存在しないケース。
		String testRecordId = DateUUIDGeneratorUtil.generateDateUUID();

		List<AccountDetailForm> spyList = Mockito.spy(new ArrayList<>());
		when(spyList.size()).thenReturn(0);
		when(accountDetailRepository.loadAccountDetailFormList(testRecordId)).thenReturn(spyList);
		
		assertTrue(testedService.hasErrorToShowAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<>(),
				testRecordId));
	}
	
	
	@Test
	public void testHasErrorToShowAccountDetailWithMsgProcess_ValidRecordId() {

		//RecordIDが正しい場合
		String testRecordId = DateUUIDGeneratorUtil.generateDateUUID();

		List<AccountDetailForm> spyList = Mockito.spy(new ArrayList<>());
		when(spyList.size()).thenReturn(10);
		when(accountDetailRepository.loadAccountDetailFormList(testRecordId)).thenReturn(spyList);

		assertFalse(testedService.hasErrorToShowAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<>(),
				testRecordId));
	}

	@Test
	public void testHasErrorToShowAccountDetailWithMsgProcess_NoRecordId() {

		//RecordIDが付与されていない場合
		assertFalse(testedService.hasErrorToShowAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<>(), ""));

	}

	// UpdateCase

	@Test
    public void testHasErrorToUpdateAccountDetailWithMsgProcess_ErrorMessagesExist() {
    	when(netIncomeCalcService.calcTaxableIncomeAmount(anyList())).thenReturn(-1L);

        assertTrue(testedService.hasErrorToUpdateAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<AccountDetailForm>(Arrays.asList(accountDetailFormForTest)),accountDetailFormForTest, "0"));
    }

	@Test
    public void testHasErrorToUpdateAccountDetailWithMsgProcess_NoErrors() {
        when(netIncomeCalcService.calcTaxableIncomeAmount(anyList())).thenReturn(1L);

        assertFalse(testedService.hasErrorToUpdateAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<AccountDetailForm>(Arrays.asList(accountDetailFormForTest)),accountDetailFormForTest, "0"));
    }

	// Delete(Remove)Case

	@Test
    public void testHasErrorToRemoveAccountDetailWithMsgProcess_ErrorMessagesExist() {
    	when(netIncomeCalcService.calcTaxableIncomeAmount(anyList())).thenReturn(-1L);

        assertTrue(testedService.hasErrorToRemoveAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<AccountDetailForm>(Arrays.asList(accountDetailFormForTest)), "0"));
    }

	@Test
    public void testHasErrorToRemoveAccountDetailWithMsgProcess_NoErrors() {
        when(netIncomeCalcService.calcTaxableIncomeAmount(anyList())).thenReturn(1L);

        assertFalse(testedService.hasErrorToRemoveAccountDetailWithMsgProcess(redirectAttributes, new ArrayList<AccountDetailForm>(Arrays.asList(accountDetailFormForTest)), "0"));
    }

}
