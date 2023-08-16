package com.lifehackaid.netincomesimulator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lifehackaid.netincomesimulator.common.DateUUIDGeneratorUtil;
import com.lifehackaid.netincomesimulator.constant.AccountCategories;
import com.lifehackaid.netincomesimulator.constant.NavigationConstants;
import com.lifehackaid.netincomesimulator.form.AccountDetailForm;
import com.lifehackaid.netincomesimulator.repository.AccountDetailRepository;
import com.lifehackaid.netincomesimulator.service.NetIncomeCalcService;
import com.lifehackaid.netincomesimulator.service.PresentationProcessHandleService;

@SpringBootTest
public class AccountDetailControllerTest {

	private MockMvc mockMvc;

	@Mock
	private NetIncomeCalcService netIncomeCalcService;

	@Mock
	private PresentationProcessHandleService presentationProcessHandleService;

	@Mock
	private AccountDetailRepository accountDetailRepository;

	@InjectMocks
	private AccountDetailController accountDetailController;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(accountDetailController).build();
	}

	//// Test-Cases


	// CreateDetailCase

	@Test
	public void testCreateAccountDetailForm_WithoutError() throws Exception {
		List<AccountDetailForm> accountDetailFormList = new ArrayList<>();
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L, 12L, true));

		when(presentationProcessHandleService.hasBindingErrorWithMsgProcess(any(), any())).thenReturn(false);
		when(presentationProcessHandleService.hasErrorToAddAccountDetailWithMsgProcess(any(), any(), any()))
				.thenReturn(false);

		mockMvc.perform(post("/createAccountDetailForm")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}

	@Test
	public void testCreateAccountDetailForm_WithBindingError() throws Exception {
		List<AccountDetailForm> accountDetailFormList = new ArrayList<>();
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L, 12L, true));

		when(presentationProcessHandleService.hasBindingErrorWithMsgProcess(any(), any())).thenReturn(true);
		when(presentationProcessHandleService.hasErrorToAddAccountDetailWithMsgProcess(any(), any(), any()))
				.thenReturn(false);

		mockMvc.perform(post("/createAccountDetailForm")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}

	@Test
	public void testCreateAccountDetailForm_WithErrorToAdd() throws Exception {
		List<AccountDetailForm> accountDetailFormList = new ArrayList<>();
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L, 12L, true));

		when(presentationProcessHandleService.hasBindingErrorWithMsgProcess(any(), any())).thenReturn(false);
		when(presentationProcessHandleService.hasErrorToAddAccountDetailWithMsgProcess(any(), any(), any()))
				.thenReturn(true);

		mockMvc.perform(post("/createAccountDetailForm")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}
	
	
	
	// RootPathCase

	@Test
	public void testHandleRootPath() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));
	}

	

	// ShowDetailsCase

	@Test
	public void testShowAccountDetailFormList_WithRecordId() throws Exception {
		String recordId = DateUUIDGeneratorUtil.generateDateUUID();
		mockMvc.perform(get("/showAccountDetailFormList").param("recordId", recordId)).andExpect(status().isOk())
				.andExpect(view().name(NavigationConstants.ACCOUNT_DETAIL_FORM_LIST_TEMPLATENAME));
	
		verify(accountDetailRepository, times(1)).loadAccountDetailFormList(any());

	}

	@Test
	public void testShowAccountDetailFormList_WithoutRecordId() throws Exception {
	
		//更新処理がある場合で実施
		mockMvc.perform(get("/showAccountDetailFormList").param("editIndex","0")).andExpect(status().isOk())
				.andExpect(view().name(NavigationConstants.ACCOUNT_DETAIL_FORM_LIST_TEMPLATENAME));
		
		verify(accountDetailRepository, never()).loadAccountDetailFormList(any());

	}

	// SaveDetailsCase

	@Test
	public void testSaveAccountDetailFormList() throws Exception {

		mockMvc.perform(post("/saveAccountDetailFormList")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_WITH_RECORDID_PATH));

		verify(accountDetailRepository, times(1)).saveAccountDetailFormList(any(), any());

	}
	
	// AddRecordId

	@Test
	public void testAddRecordId() throws Exception {
		mockMvc.perform(get("/addRecordId")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("/showAccountDetailFormList*"));
	}

	// edit-case

	@Test
	public void testEditAccountDetailForm() throws Exception {
		String index = "0";
		
		mockMvc.perform(post("/editAccountDetailForm").param("index", index)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}

	// update-case
	
	@Test
	public void testUpdateAccountDetailForm_WithBindingError() throws Exception {

		when(presentationProcessHandleService.hasBindingErrorWithMsgProcess(any(), any())).thenReturn(true);
		when(presentationProcessHandleService.hasErrorToAddAccountDetailWithMsgProcess(any(), any(),any())).thenReturn(false);

		
		mockMvc.perform(post("/updateAccountDetailForm").param("index", "0")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}

	@Test
	public void testUpdateAccountDetailForm_WithErrorToAddAccountDetail() throws Exception {
		AccountDetailForm updateAccountDetailForm = new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L, 12L,
				true);

		when(presentationProcessHandleService.hasBindingErrorWithMsgProcess(any(), any())).thenReturn(false);
		when(presentationProcessHandleService.hasErrorToAddAccountDetailWithMsgProcess(any(), any(),any())).thenReturn(true);

		mockMvc.perform(post("/updateAccountDetailForm").param("index", "0")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}

	@Test
	public void testUpdateAccountDetailForm_SuccessfulUpdate() throws Exception {
		AccountDetailForm updateAccountDetailForm = new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L, 12L,
				true);

		when(presentationProcessHandleService.hasBindingErrorWithMsgProcess(any(), any())).thenReturn(false);
		when(presentationProcessHandleService.hasErrorToAddAccountDetailWithMsgProcess(any(), any(),any())).thenReturn(false);

		mockMvc.perform(post("/updateAccountDetailForm").param("index", "0")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}
	// RemoveDetailCase

	@Test
	public void testRemoveAccountDetailForm_WithoutError() throws Exception {
		String index = "0";
		List<AccountDetailForm> accountDetailFormList = new ArrayList<>();
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L, 12L, true));

		when(presentationProcessHandleService.hasErrorToRemoveAccountDetailWithMsgProcess(any(), any(), any()))
		.thenReturn(false);
		
		mockMvc.perform(post("/removeAccountDetailForm").param("index", index).flashAttr("accountDetailFormList",
				accountDetailFormList)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}

	@Test
	public void testRemoveAccountDetailForm_WithError() throws Exception {
		String index = "0";
		List<AccountDetailForm> accountDetailFormList = new ArrayList<>();
		accountDetailFormList.add(new AccountDetailForm(AccountCategories.Revenue, "売上", 600000L, 12L, true));

		when(presentationProcessHandleService.hasErrorToRemoveAccountDetailWithMsgProcess(any(), any(), any()))
				.thenReturn(true);

		mockMvc.perform(post("/removeAccountDetailForm").param("index", index).flashAttr("accountDetailFormList",
				accountDetailFormList)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(NavigationConstants.SHOW_ACCOUNT_DETAIL_FORM_LIST_PATH));

	}



}