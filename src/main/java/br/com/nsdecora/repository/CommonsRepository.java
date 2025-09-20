package br.com.nsdecora.repository;

import static br.com.nsdecora.model.BankService.ACCOUNT;
import static lombok.AccessLevel.PRIVATE;

import br.com.nsdecora.expcetion.NoFundsEnoughExpection;
import br.com.nsdecora.model.AccountWallet;
import br.com.nsdecora.model.Money;
import br.com.nsdecora.model.MoneyAudit;
import br.com.nsdecora.model.Wallet;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@NoArgsConstructor(access = PRIVATE)

public final class CommonsRepository {

    public static void checkFundsForTransaction(final Wallet source, final long amount){
        if (source.getFounds() < amount){
            throw new NoFundsEnoughExpection("Sua conta não tem saldo o suficiente para realizar essa transação");
        }

    }

    public static List<Money> generateMoney(final UUID transactionID, final long funds, final String description) {
        var history = new MoneyAudit(transactionID, ACCOUNT, description, OffsetDateTime.now());
        return Stream.generate(() -> new Money(history)).limit(funds).toList();
    }

}
