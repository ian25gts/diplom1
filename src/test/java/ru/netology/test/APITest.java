package ru.netology.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DBHelper;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.APIHelper.payFromCard;
import static ru.netology.data.DBHelper.deleteAllDB;
import static ru.netology.data.DataHelper.*;

public class APITest {

    private static final int amount = 45_000_00;
    private final String debitPay = "/pay";
    private final String creditPay = "/credit";

    @AfterEach
    void setDownDB() {
        deleteAllDB();
    }

    // Тесты дебетовая карта

    @Test
    @DisplayName("Тест поле Amount при оплате одобренной дебетовой картой")
    void shouldHaveAmountWithDebitApprovedCard() {
        payFromCard(getApprovedCard(), debitPay, 200);
        assertEquals(amount, DBHelper.getAmountDebitCard());
    }

    @Test
    @DisplayName("Тест статус APPROVED при оплате одобренной дебетовой картой")
    void shouldHaveStatusWithDebitApprovedCard() {
        payFromCard(getApprovedCard(), debitPay, 200);
        assertEquals("APPROVED", DBHelper.getStatusDebitCard());
    }

    @Test
    @DisplayName("Тест поле PaymentId при оплате одобренной дебетовой картой")
    void shouldHavePaymentIdWithDebitApprovedCard() {
        payFromCard(getApprovedCard(), debitPay, 200);
        assertNotNull(DBHelper.getPaymentId());
    }

    @Test
    @DisplayName("Тест поле TransactionId при оплате одобренной дебетовой картой")
    void shouldHaveTransactionIdWithDebitApprovedCard() {
        payFromCard(getApprovedCard(), debitPay, 200);
        assertNotNull(DBHelper.getTransactionIdDebitCard());
    }

    @Test
    @DisplayName("Тест PaymentId и TransactionId равны при оплате одобренной дебетовой картой")
    void shouldEqualPaymentAndTransactionIdWithDebitApprovedCard() {
        payFromCard(getApprovedCard(), debitPay, 200);
        assertEquals(DBHelper.getPaymentId(), DBHelper.getTransactionIdDebitCard());
    }

    @Test
    @DisplayName("Тест отсутствие поля CreditId при оплате одобренной дебетовой картой")
    void shouldNotHaveCreditIdWithDebitApprovedCard() {
        payFromCard(getApprovedCard(), debitPay, 200);
        assertNull(DBHelper.getCreditId());
    }

    @Test
    @DisplayName("Тест статус DECLINED при оплате одобренной дебетовой картой")
    void shouldHaveStatusWithDebitDeclinedCard() {
        payFromCard(getDeclinedCard(), debitPay, 200);
        assertEquals("DECLINED", DBHelper.getStatusDebitCard());
    }

    @Test
    @DisplayName("Тест отсутствие поля Amount при оплате отклоненной дебетовой картой")
    void shouldNotHaveAmountWithDebitDeclinedCard() {
        payFromCard(getDeclinedCard(), debitPay, 200);
        assertNull(DBHelper.getAmountDebitCard());
    }

    @Test
    @DisplayName("Тест отсутствие поля PaymentId при оплате отклоненной дебетовой картой")
    void shouldNotHavePaymentIdWithDebitDeclinedCard() {
        payFromCard(getDeclinedCard(), debitPay, 200);
        assertNull(DBHelper.getPaymentId());
    }

    @Test
    @DisplayName("Тест отсутствие поля TransactionId при оплате отклоненной дебетовой картой")
    void shouldNotHaveTransactionIdWithDebitDeclinedCard() {
        payFromCard(getDeclinedCard(), debitPay, 200);
        assertNull(DBHelper.getTransactionIdDebitCard());
    }

    @Test
    @DisplayName("Тест отсутствие поля CreditId при оплате отклоненной дебетовой картой")
    void shouldNotHaveCreditIdWithDebitDeclinedCard() {
        payFromCard(getDeclinedCard(), debitPay, 200);
        assertNull(DBHelper.getCreditId());
    }

    @Test
    @DisplayName("Тест возврат ошибки при оплате неизвестной дебетовой картой")
    void shouldReturnFailWithDebitUnknownCard() {
        payFromCard(getUnknownCard(), debitPay, 500);
    }

    // Тесты кредитная карта

    @Test
    @DisplayName("Тест статус APPROVED при оплате одобренной кредитной картой")
    void shouldHaveStatusWithCreditApprovedCard() {
        payFromCard(getApprovedCard(), creditPay, 200);
        assertEquals("APPROVED", DBHelper.getStatusCreditCard());
    }

    @Test
    @DisplayName("Тест поле BankId при оплате одобренной кредитной картой")
    void shouldHaveBankIdWithCreditApprovedCard() {
        payFromCard(getApprovedCard(), creditPay, 200);
        assertNotNull(DBHelper.getBankIdCreditCard());
    }

    @Test
    @DisplayName("Тест поле CreditId при оплате одобренной кредитной картой")
    void shouldHaveCreditIdWithCreditApprovedCard() {
        payFromCard(getApprovedCard(), creditPay, 200);
        assertNotNull(DBHelper.getCreditId());
    }

    @Test
    @DisplayName("Тест отсутствие поля PaymentId при оплате одобренной кредитной картой")
    void shouldNotHavePaymentIdWithCreditApprovedCard() {
        payFromCard(getApprovedCard(), creditPay, 200);
        assertNull(DBHelper.getPaymentId());
    }

    @Test
    @DisplayName("Тест статус DECLINED при оплате отклоненной кредитной картой")
    void shouldHaveStatusWithCreditDeclinedCard() {
        payFromCard(getDeclinedCard(), creditPay, 200);
        assertEquals("DECLINED", DBHelper.getStatusCreditCard());
    }

    @Test
    @DisplayName("Тест отсутствие поля BankId при оплате отклоненной кредитной картой")
    void shouldNotHaveBankIdWithCreditDeclinedCard() {
        payFromCard(getDeclinedCard(), creditPay, 200);
        assertNull(DBHelper.getBankIdCreditCard());
    }

    @Test
    @DisplayName("Тест отсутствие поля CreditId при оплате отклоненной кредитной картой")
    void shouldNotHaveCreditIdWithCreditApprovedCard() {
        payFromCard(getDeclinedCard(), creditPay, 200);
        assertNull(DBHelper.getCreditId());
    }

    @Test
    @DisplayName("Тест отсутствие поля PaymentId при оплате отклоненной кредитной картой")
    void shouldNotHavePaymentIdWithCreditDeclinedCard() {
        payFromCard(getDeclinedCard(), creditPay, 200);
        assertNull(DBHelper.getPaymentId());
    }

    @Test
    @DisplayName("Тест возврат ошибки при оплате неизвестной кредитной картой")
    void shouldReturnFailWithCreditUnknownCard() {
        payFromCard(getUnknownCard(), creditPay, 500);
    }
}
