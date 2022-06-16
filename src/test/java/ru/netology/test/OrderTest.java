package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DBHelper;
import ru.netology.page.CreditPage;
import ru.netology.page.DebitPage;
import ru.netology.page.HomePage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.DBHelper.deleteAllDB;
import static ru.netology.data.DataHelper.*;

public class OrderTest {

    private static final int amount = 45_000_00;

    @BeforeAll
    static void addListenerAndHeadless() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(false));
    }

    @AfterAll
    static void removeListener() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080/");
        new HomePage().openPage();
    }

    @AfterEach
    void setDown() {
        deleteAllDB();
    }

    // Тесты дебетовая карта

    @Test
    @DisplayName("Тест успешная оплата с одобренной дебетовой картой в форме оплаты")
    void shouldSuccessPayWithApprovedDebitCard() {
        successDebitPage().enterValidUserWithApprovedCard();
        assertEquals(amount, DBHelper.getAmountDebitCard());
        assertEquals("APPROVED", DBHelper.getStatusDebitCard());
        assertNotNull(DBHelper.getPaymentId());
        assertNotNull(DBHelper.getTransactionIdDebitCard());
        assertEquals(DBHelper.getPaymentId(), DBHelper.getTransactionIdDebitCard());
        assertNull(DBHelper.getCreditId());
    }

    @Test
    @DisplayName("Тест возврат ошибки с отклоненной дебетовой картой в форме оплаты")
    void shouldReturnFailWithDeclinedDebitCard() {
        successDebitPage()
                .enterValidUserWithIncorrectCard(validUser(getDeclinedCard()));
        assertEquals("DECLINED", DBHelper.getStatusDebitCard());
        assertNull(DBHelper.getPaymentId());
        assertNull(DBHelper.getTransactionIdDebitCard());
    }

    @Test
    @DisplayName("Тест возврат ошибки с неизвестной дебетовой картой в форме оплаты")
    void shouldReturnFailWithUnknownDebitCard() {
        successDebitPage()
                .enterValidUserWithIncorrectCard(validUser(getUnknownCard()));
        assertNull(DBHelper.getPaymentId());
        assertNull(DBHelper.getTransactionIdDebitCard());
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустой дебетовой картой в форме оплаты")
    void shouldReturnErrorWithEmptyDebitCard() {
        successDebitPage()
                .enterIncorrectCardInput(emptyCardUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустым полем месяца в форме оплаты")
    void shouldReturnErrorWithEmptyMonthDebit() {
        successDebitPage()
                .enterIncorrectMonthInput(emptyMonthUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустым полем года в форме оплаты")
    void shouldReturnErrorWithEmptyYearDebit() {
        successDebitPage()
                .enterIncorrectYearInput(emptyYearUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустым полем имени в форме оплаты")
    void shouldReturnErrorWithEmptyNameDebit() {
        successDebitPage()
                .enterIncorrectNameInput(emptyNameUser(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустым полем CVC в форме оплаты")
    void shouldReturnErrorWithEmptyCodeDebit() {
        successDebitPage()
                .enterIncorrectCodeInput(emptyCodeUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со всеми пустыми полями в форме оплаты")
    void shouldReturnErrorsWithEmptyAllDebit() {
        successDebitPage()
                .enterInputs(emptyUser());
        errorsDisplayDebit(
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Поле обязательно для заполнения",
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем карты в форме оплаты")
    void shouldReturnErrorWithInvalidDebitCard() {
        successDebitPage()
                .enterIncorrectCardInput(invalidCardUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем месяца в форме оплаты")
    void shouldReturnErrorWithInvalidMonthDebit() {
        successDebitPage()
                .enterIncorrectMonthInput(invalidMonthUser(), "Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем года в форме оплаты")
    void shouldReturnErrorWithInvalidYearDebit() {
        successDebitPage()
                .enterIncorrectYearInput(invalidYearUser(), "Истёк срок действия карты");
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем имени в форме оплаты")
    void shouldReturnErrorWithInvalidNameDebit() {
        successDebitPage()
                .enterIncorrectNameInput(invalidNameUser(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем CVC в форме оплаты")
    void shouldReturnErrorWithInvalidCodeDebit() {
        successDebitPage()
                .enterIncorrectCodeInput(invalidCodeUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со всеми некорректными полями в форме оплаты")
    void shouldReturnErrorsWithInvalidAllDebit() {
        successDebitPage()
                .enterInputs(invalidUser());
        errorsDisplayDebit(
                "Неверный формат",
                "Неверно указан срок действия карты",
                "Истёк срок действия карты",
                "Неверный формат",
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля Карты в форме оплаты")
    void shouldReturnErrorsWithCardZeroInputsDebit() {
        successDebitPage()
                .enterIncorrectCardInput(userWithCardZero(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля Года в форме оплаты")
    void shouldReturnErrorsWithYearZeroInputsDebit() {
        successDebitPage()
                .enterIncorrectYearInput(userWithYearZero(), "Истёк срок действия карты");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля Месяца в форме оплаты")
    void shouldReturnErrorsWithMonthZeroInputsDebit() {
        successDebitPage()
                .enterIncorrectMonthInput(userWithMonthZero(), "Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля Имени в форме оплаты")
    void shouldReturnErrorsWithNameZeroInputsDebit() {
        successDebitPage()
                .enterIncorrectNameInput(userWithNameZero(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля CVC в форме оплаты")
    void shouldReturnErrorsWithCodeZeroInputsDebit() {
        successDebitPage()
                .enterIncorrectCodeInput(userWithCodeZero(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со всеми полями со значением '0' в форме оплаты")
    void shouldReturnErrorsWithAllZeroInputsDebit() {
        successDebitPage()
                .enterInputs(userWithAllZero());
        errorsDisplayDebit(
                "Неверный формат",
                "Неверно указан срок действия карты",
                "Истёк срок действия карты",
                "Неверный формат",
                "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Карты до лимита в форме оплаты")
    void shouldReturnErrorWithCardUnderLimitDebit() {
        successDebitPage()
                .enterIncorrectCardInput(userWithCardUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Года до лимита в форме оплаты")
    void shouldReturnErrorWithYearUnderLimitDebit() {
        successDebitPage()
                .enterIncorrectYearInput(userWithYearUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Месяца до лимита в форме оплаты")
    void shouldReturnErrorWithMonthUnderLimitDebit() {
        successDebitPage()
                .enterIncorrectMonthInput(userWithMonthUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Имени до лимита в форме оплаты")
    void shouldReturnErrorWithNameUnderLimitDebit() {
        successDebitPage()
                .enterIncorrectNameInput(userWithNameUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля CVC до лимита в форме оплаты")
    void shouldReturnErrorWithCodeUnderLimitDebit() {
        successDebitPage()
                .enterIncorrectCodeInput(userWithCodeUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со всеми полями до лимита в форме оплаты")
    void shouldReturnErrorsWithAllUnderLimitsDebit() {
        successDebitPage()
                .enterInputs(userWithAllUnderLimits());
        errorsDisplayDebit(
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Неверный формат");
    }

    @Test
    @DisplayName("Тест успешно внести валидное значение поля Карты сверх лимита в форме оплаты")
    void shouldReturnErrorWithCardAfterLimitDebit() {
        successDebitPage()
                .enterInputs(userWithCardAfterLimit());
    }

    @Test
    @DisplayName("Тест успешно внести валидное значение поля Года сверх лимита в форме оплаты")
    void shouldReturnErrorWithYearAfterLimitDebit() {
        successDebitPage()
                .enterInputs(userWithYearAfterLimit());
    }

    @Test
    @DisplayName("Тест успешно внести валидное значение поля Месяца сверх лимита в форме оплаты")
    void shouldReturnErrorWithMonthAfterLimitDebit() {
        successDebitPage()
                .enterInputs(userWithMonthAfterLimit());
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Имени сверх лимита в форме оплаты")
    void shouldReturnErrorWithNameAfterLimitDebit() {
        successDebitPage()
                .enterIncorrectNameInput(userWithNameAfterLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест успешно внести валидное значение поля CVC сверх лимита в форме оплаты")
    void shouldReturnErrorWithCodeAfterLimitDebit() {
        successDebitPage()
                .enterInputs(userWithCodeAfterLimit());
    }

    @Test
    @DisplayName("Тест возврат ошибки у поля Имени при вводе всех полей сверх лимита в форме оплаты")
    void shouldReturnErrorsWithAllAfterLimitsDebit() {
        successDebitPage()
                .enterInputs(userWithAfterLimits());
        errorNameDisplayDebit(
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Карты с некорректными символами в форме оплаты")
    void shouldReturnFailWithCardIncorrectSymbolsDebit() {
        successDebitPage()
                .enterIncorrectCardInput(userWithCardIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Года с некорректными символами в форме оплаты")
    void shouldReturnFailWithYearIncorrectSymbolsDebit() {
        successDebitPage()
                .enterIncorrectYearInput(userWithYearIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Месяца с некорректными символами в форме оплаты")
    void shouldReturnFailWithMonthIncorrectSymbolsDebit() {
        successDebitPage()
                .enterIncorrectMonthInput(userWithMonthIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Имени с некорректными символами в форме оплаты")
    void shouldReturnFailWithNameIncorrectSymbolsDebit() {
        successDebitPage()
                .enterIncorrectNameInput(userWithNameIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле CVC с некорректными символами в форме оплаты")
    void shouldReturnFailWithCodeIncorrectSymbolsDebit() {
        successDebitPage()
                .enterIncorrectCodeInput(userWithCodeIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в полях с некорректными символами в форме оплаты")
    void shouldReturnFailWithAllIncorrectSymbolsDebit() {
        successDebitPage()
                .enterInputs(userWithAllIncorrectSymbols());
        errorsDisplayDebit(
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Карты с символьным значением в форме оплаты")
    void shouldReturnFailWithCardSymbolicValueDebit() {
        successDebitPage()
                .enterIncorrectCardInput(userWithCardSymbolicValue(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Года с символьным значением в форме оплаты")
    void shouldReturnFailWithYearSymbolicValueDebit() {
        successDebitPage()
                .enterIncorrectYearInput(userWithYearSymbolicValue(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Месяца с символьным значением в форме оплаты")
    void shouldReturnFailWithMonthSymbolicValueDebit() {
        successDebitPage()
                .enterIncorrectMonthInput(userWithMonthSymbolicValue(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Имени с символьным значением в форме оплаты")
    void shouldReturnFailWithNameSymbolicValueDebit() {
        successDebitPage()
                .enterIncorrectNameInput(userWithNameSymbolicValue(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле CVC с символьным значением в форме оплаты")
    void shouldReturnFailWithCodeSymbolicValueDebit() {
        successDebitPage()
                .enterIncorrectCodeInput(userWithCodeSymbolicValue(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в полях с символьными значениями в форме оплаты")
    void shouldReturnFailWithSymbolicValuesDebit() {
        successDebitPage()
                .enterInputs(userWithSymbolicValues());
        errorsDisplayDebit(
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Поле обязательно для заполнения",
                "Неверный формат"
        );
    }

    // Тесты - кредитная карта

    @Test
    @DisplayName("Тест успешная оплата с одобренной кредитной картой в форме кредита")
    void shouldSuccessPayWithApprovedCreditCard() {
        successCreditPage()
                .enterValidUserWithApprovedCard();
        assertEquals("APPROVED", DBHelper.getStatusCreditCard());
        assertNotNull(DBHelper.getBankIdCreditCard());
        assertNotNull(DBHelper.getCreditId());
        assertNull(DBHelper.getPaymentId());
    }

    @Test
    @DisplayName("Тест возврат ошибки с отклоненной кредитной картой в форме кредита")
    void shouldReturnFailWithDeclinedCreditCard() {
        successCreditPage()
                .enterValidUserWithIncorrectCard(validUser(getDeclinedCard()));
        assertEquals("DECLINED", DBHelper.getStatusCreditCard());
        assertNull(DBHelper.getBankIdCreditCard());
        assertNull(DBHelper.getCreditId());
    }

    @Test
    @DisplayName("Тест возврат ошибки с неизвестной кредитной картой в форме кредита")
    void shouldReturnFailWithUnknownCreditCard() {
        successCreditPage()
                .enterValidUserWithIncorrectCard(validUser(getUnknownCard()));
        assertNull(DBHelper.getBankIdCreditCard());
        assertNull(DBHelper.getCreditId());
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустой кредитной картой в форме кредита")
    void shouldReturnErrorWithEmptyCreditCard() {
        successCreditPage()
                .enterIncorrectCardInput(emptyCardUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустым полем месяца в форме кредита")
    void shouldReturnErrorWithEmptyMonthCredit() {
        successCreditPage()
                .enterIncorrectMonthInput(emptyMonthUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустым полем года в форме кредита")
    void shouldReturnErrorWithEmptyYearCredit() {
        successCreditPage()
                .enterIncorrectYearInput(emptyYearUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустым полем имени в форме кредита")
    void shouldReturnErrorWithEmptyNameCredit() {
        successCreditPage()
                .enterIncorrectNameInput(emptyNameUser(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Тест возврат ошибки с пустым полем CVC в форме кредита")
    void shouldReturnErrorWithEmptyCodeCredit() {
        successCreditPage()
                .enterIncorrectCodeInput(emptyCodeUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со всеми пустыми полями в форме кредита")
    void shouldReturnErrorsWithEmptyAllCredit() {
        successCreditPage()
                .enterInputs(emptyUser());
        errorsDisplayCredit(
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Поле обязательно для заполнения",
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест - возврат ошибки с некорректным полем карты в форме кредита")
    void shouldReturnErrorWithInvalidCreditCard() {
        successCreditPage()
                .enterIncorrectCardInput(invalidCardUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем месяца в форме кредита")
    void shouldReturnErrorWithInvalidMonthCredit() {
        successCreditPage()
                .enterIncorrectMonthInput(invalidMonthUser(), "Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем года в форме кредита")
    void shouldReturnErrorWithInvalidYearCredit() {
        successCreditPage()
                .enterIncorrectYearInput(invalidYearUser(), "Истёк срок действия карты");
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем имени в форме кредита")
    void shouldReturnErrorWithInvalidNameCredit() {
        successCreditPage()
                .enterIncorrectNameInput(invalidNameUser(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Тест возврат ошибки с некорректным полем CVC в форме кредита")
    void shouldReturnErrorWithInvalidCodeCredit() {
        successCreditPage()
                .enterIncorrectCodeInput(invalidCodeUser(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со всеми некорректными полями в форме кредита")
    void shouldReturnErrorsWithInvalidAllCredit() {
        successCreditPage()
                .enterInputs(invalidUser());
        errorsDisplayCredit(
                "Неверный формат",
                "Неверно указан срок действия карты",
                "Истёк срок действия карты",
                "Неверный формат",
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля Карты в форме кредита")
    void shouldReturnErrorsWithCardZeroInputsCredit() {
        successCreditPage()
                .enterIncorrectCardInput(userWithCardZero(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля Года в форме кредита")
    void shouldReturnErrorsWithYearZeroInputsCredit() {
        successCreditPage()
                .enterIncorrectYearInput(userWithYearZero(), "Истёк срок действия карты");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля Месяца в форме кредита")
    void shouldReturnErrorsWithMonthZeroInputsCredit() {
        successCreditPage()
                .enterIncorrectMonthInput(userWithMonthZero(), "Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля Имени в форме кредита")
    void shouldReturnErrorsWithNameZeroInputsCredit() {
        successCreditPage()
                .enterIncorrectNameInput(userWithNameZero(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением '0' поля CVC в форме кредита")
    void shouldReturnErrorsWithCodeZeroInputsCredit() {
        successCreditPage()
                .enterIncorrectCodeInput(userWithCodeZero(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со всеми полями со значением '0' в форме кредита")
    void shouldReturnErrorsWithAllZeroInputsCredit() {
        successCreditPage()
                .enterInputs(userWithAllZero());
        errorsDisplayCredit(
                "Неверный формат",
                "Неверно указан срок действия карты",
                "Истёк срок действия карты",
                "Неверный формат",
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Карты до лимита в форме кредита")
    void shouldReturnErrorWithCardUnderLimitCredit() {
        successCreditPage()
                .enterIncorrectCardInput(userWithCardUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Года до лимита в форме кредита")
    void shouldReturnErrorWithYearUnderLimitCredit() {
        successCreditPage()
                .enterIncorrectYearInput(userWithYearUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Месяца до лимита в форме кредита")
    void shouldReturnErrorWithMonthUnderLimitCredit() {
        successCreditPage()
                .enterIncorrectMonthInput(userWithMonthUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Имени до лимита в форме кредита")
    void shouldReturnErrorWithNameUnderLimitCredit() {
        successCreditPage()
                .enterIncorrectNameInput(userWithNameUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля CVC до лимита в форме кредита")
    void shouldReturnErrorWithCodeUnderLimitCredit() {
        successCreditPage()
                .enterIncorrectCodeInput(userWithCodeUnderLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки со всеми полями до лимита в форме кредита")
    void shouldReturnErrorsWithAllUnderLimitsCredit() {
        successCreditPage()
                .enterInputs(userWithAllUnderLimits());
        errorsDisplayCredit(
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат успешное внесение валидного значения поля Карты сверх лимита в форме кредита")
    void shouldReturnErrorWithCardAfterLimitCredit() {
        successCreditPage()
                .enterInputs(userWithCardAfterLimit());
    }

    @Test
    @DisplayName("Тест возврат успешное внесение валидного значения поля Года сверх лимита в форме кредита")
    void shouldReturnErrorWithYearAfterLimitCredit() {
        successCreditPage()
                .enterInputs(userWithYearAfterLimit());
    }

    @Test
    @DisplayName("Тест возврат успешное внесение валидного значения поля Месяца сверх лимита в форме кредита")
    void shouldReturnErrorWithMonthAfterLimitCredit() {
        successCreditPage()
                .enterInputs(userWithMonthAfterLimit());
    }

    @Test
    @DisplayName("Тест возврат ошибки со значением поля Имени сверх лимита в форме кредита")
    void shouldReturnErrorWithNameAfterLimitCredit() {
        successCreditPage()
                .enterIncorrectNameInput(userWithNameAfterLimit(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат успешное внесение валидного значения CVC сверх лимита в форме кредита")
    void shouldReturnErrorWithCodeAfterLimitCredit() {
        successCreditPage()
                .enterInputs(userWithCodeAfterLimit());
    }

    @Test
    @DisplayName("Тест возврат ошибки у поля Имени при вводе всех полей сверх лимита в форме кредита")
    void shouldReturnErrorsWithAllAfterLimitsCredit() {
        successCreditPage()
                .enterInputs(userWithAfterLimits());
        errorNameDisplayCredit(
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Карты с некорректными символами в форме кредита")
    void shouldReturnFailWithCardIncorrectSymbolsCredit() {
        successCreditPage()
                .enterIncorrectCardInput(userWithCardIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Года с некорректными символами в форме кредита")
    void shouldReturnFailWithYearIncorrectSymbolsCredit() {
        successCreditPage()
                .enterIncorrectYearInput(userWithYearIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Месяца с некорректными символами в форме кредита")
    void shouldReturnFailWithMonthIncorrectSymbolsCredit() {
        successCreditPage()
                .enterIncorrectMonthInput(userWithMonthIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест - возврат ошибки в поле Имени с некорректными символами в форме кредита")
    void shouldReturnFailWithNameIncorrectSymbolsCredit() {
        successCreditPage()
                .enterIncorrectNameInput(userWithNameIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле CVC с некорректными символами в форме кредита")
    void shouldReturnFailWithCodeIncorrectSymbolsCredit() {
        successCreditPage()
                .enterIncorrectCodeInput(userWithCodeIncorrectSymbols(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в полях с некорректными символами в форме кредита")
    void shouldReturnFailWithIncorrectSymbolsCredit() {
        successCreditPage().enterInputs(userWithAllIncorrectSymbols());
        errorsDisplayCredit(
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Неверный формат"
        );
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Карты с символьным значением в форме кредита")
    void shouldReturnFailWithCardSymbolicValueCredit() {
        successCreditPage()
                .enterIncorrectCardInput(userWithCardSymbolicValue(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Года с символьным значением в форме кредита")
    void shouldReturnFailWithYearSymbolicValueCredit() {
        successCreditPage()
                .enterIncorrectYearInput(userWithYearSymbolicValue(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Месяца с символьным значением в форме кредита")
    void shouldReturnFailWithMonthSymbolicValueCredit() {
        successCreditPage()
                .enterIncorrectMonthInput(userWithMonthSymbolicValue(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле Имени с символьным значением в форме кредита")
    void shouldReturnFailWithNameSymbolicValueCredit() {
        successCreditPage()
                .enterIncorrectNameInput(userWithNameSymbolicValue(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Тест возврат ошибки в поле CVC с символьным значением в форме кредита")
    void shouldReturnFailWithCodeSymbolicValueCredit() {
        successCreditPage()
                .enterIncorrectCodeInput(userWithCodeSymbolicValue(), "Неверный формат");
    }

    @Test
    @DisplayName("Тест возврат ошибки в полях с символьными значениями в форме кредита")
    void shouldReturnFailWithSymbolicValuesCredit() {
        successCreditPage().enterInputs(userWithSymbolicValues());
        errorsDisplayCredit(
                "Неверный формат",
                "Неверный формат",
                "Неверный формат",
                "Поле обязательно для заполнения",
                "Неверный формат"
        );
    }

    private DebitPage successDebitPage() {
        new HomePage().openDebitForm().successOpenPage();
        return new DebitPage();
    }

    private CreditPage successCreditPage() {
        new HomePage().openCreditForm().successOpenPage();
        return new CreditPage();
    }

    private void errorsDisplayDebit(String errorCard, String errorMonth, String errorYear, String errorName, String errorCode) {
        new DebitPage()
                .errorsDisplay(errorCard, errorMonth, errorYear, errorName, errorCode);
    }

    private void errorsDisplayCredit(String errorCard, String errorMonth, String errorYear, String errorName, String errorCode) {
        new CreditPage()
                .errorsDisplay(errorCard, errorMonth, errorYear, errorName, errorCode);
    }

    private void errorNameDisplayDebit(String errorName) {
        new DebitPage()
                .errorNameDisplay(errorName);
    }

    private void errorNameDisplayCredit(String errorName) {
        new CreditPage()
                .errorNameDisplay(errorName);
    }
}
