package br.com.nsdecora.repository;

import br.com.nsdecora.expcetion.AccountNotFoundException;
import br.com.nsdecora.expcetion.PixInUseException;
import br.com.nsdecora.model.AccountWallet;
import br.com.nsdecora.model.MoneyAudit;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.nsdecora.repository.CommonsRepository.checkFundsForTransaction;

public class AccountRepository {


    private final List<AccountWallet> accounts = new ArrayList<>();

    public List<AccountWallet> create(final List<String> pix, final long initialFunds){
        if (!accounts.isEmpty()) {
            var pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
            for (var p : pix) {
                if (pixInUse.contains(p)) {
                    throw new PixInUseException("O pix '" + p + "' já está em uso");
                }
            }
        }
        var newAccount = new AccountWallet(initialFunds, pix);
        accounts.add(newAccount);
        return Collections.singletonList(newAccount);
    }

    public void deposit(final String pix, final long fundsAmount){
        var target = findByPix(pix);
        target.addMoney(fundsAmount, "depósito");
    }

    public long withdraw(final String pix, final long amount){
        var source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount){
        var source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        var target = findByPix(targetPix);
        var message = "pix enviado de '" + sourcePix + "' para '" + targetPix + "'";
        target.addMoney(source.reduceMoney(amount), source.getService(), message);
    }

    public AccountWallet findByPix(final String pix){
        return accounts.stream()
                .filter(a -> a.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("A conta com a chave pix '" + pix + "'não existe ou foi encerrada"));
    }

    public List<AccountWallet> list(){
        return this.accounts;
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String pix) {
        // 1. Encontra a conta (AccountWallet) com base na chave PIX.
        var accountWallet = findByPix(pix);

        // 2. Obtém a lista de MoneyAudit da conta.
        List<MoneyAudit> audits = accountWallet.getFinancialTransaction();

        // 3. Usa o Stream API para agrupar as auditorias por data.
        return audits.stream()
                .collect(Collectors.groupingBy(MoneyAudit::createdAt));
    }
}
