package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AccountRepositoryImpl.class)
class AccountRepositoryImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("save()でAccountが永続化されること")
    void shouldSaveAccount() {
        Account account = Account.create("田中太郎");

        Account saved = accountRepository.save(account);

        assertThat(saved.getId()).isEqualTo(account.getId());
    }

    @Test
    @DisplayName("findByAccountNumber()で保存したAccountを取得できること")
    void shouldFindAccountByAccountNumber() {
        Account account = Account.create("田中太郎").deposit(Money.of(1000));
        accountRepository.save(account);

        Optional<Account> found = accountRepository.findByAccountNumber(account.getAccountNumber());

        assertThat(found).isPresent();
        assertThat(found.get().getOwnerName()).isEqualTo("田中太郎");
        assertThat(found.get().getBalance()).isEqualTo(Money.of(1000));
    }

    @Test
    @DisplayName("findByAccountNumber()で存在しない口座番号の場合はOptional.empty()が返ること")
    void shouldReturnEmptyForNonExistentAccount() {
        AccountNumber nonExistent = new AccountNumber("9999999999");

        Optional<Account> found = accountRepository.findByAccountNumber(nonExistent);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("ACTIVEステータスのAccountを保存・取得できること")
    void shouldPersistActiveStatus() {
        Account account = Account.create("田中太郎");
        accountRepository.save(account);

        Optional<Account> found = accountRepository.findByAccountNumber(account.getAccountNumber());

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("CLOSEDステータスのAccountを保存・取得できること")
    void shouldPersistClosedStatus() {
        Account account = Account.create("田中太郎");
        accountRepository.save(account);

        Account closed = account.close();
        accountRepository.save(closed);

        Optional<Account> found = accountRepository.findByAccountNumber(account.getAccountNumber());

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(found.get().getBalance()).isEqualTo(Money.of(0));
    }

    @Test
    @DisplayName("save()で既存Accountの残高が更新されること")
    void shouldUpdateExistingAccountBalance() {
        Account account = Account.create("田中太郎").deposit(Money.of(1000));
        accountRepository.save(account);

        Account updated = account.deposit(Money.of(500));
        accountRepository.save(updated);

        Optional<Account> found = accountRepository.findByAccountNumber(account.getAccountNumber());
        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualTo(Money.of(1500));
    }
}
