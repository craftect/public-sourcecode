package com.lifehackaid.netincomesimulator.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AccountCategoriesTest {

    @Test
    public void testGetDescription() {
        assertEquals("売上", AccountCategories.Revenue.getDescription());
        assertEquals("必要経費", AccountCategories.NecessaryExpenses.getDescription());
        assertEquals("所得控除", AccountCategories.IncomeDeduction.getDescription());
        assertEquals("税額控除", AccountCategories.TaxDeduction.getDescription());
    }
}
