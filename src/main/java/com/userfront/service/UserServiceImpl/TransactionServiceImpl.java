package com.userfront.service.UserServiceImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userfront.config.TestDataGenerator;
import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.PrimaryTransactionDao;
import com.userfront.dao.RecipientDao;
import com.userfront.dao.SavingsAccountDao;
import com.userfront.dao.SavingsTransactionDao;
import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.Recipient;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;
import com.userfront.service.TransactionService;
import com.userfront.service.UserService;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private PrimaryTransactionDao primaryTransactionDao;

    @Autowired
    private SavingsTransactionDao savingsTransactionDao;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private RecipientDao recipientDao;

    public List<PrimaryTransaction> findPrimaryTransactionList(String username) {
        User user = userService.findByUsername(username);
        List<PrimaryTransaction> primaryTransactionList = user.getPrimaryAccount().getPrimaryTransactionList();

        return primaryTransactionList;
    }

    public List<SavingsTransaction> findSavingsTransactionList(String username) {
        User user = userService.findByUsername(username);
        List<SavingsTransaction> savingsTransactionList = user.getSavingsAccount().getSavingsTransactionList();

        return savingsTransactionList;
    }

    public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }

    public void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    public void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }

    public void betweenAccountsTransfer(String transferFrom, String transferTo, String amount,
            PrimaryAccount primaryAccount, SavingsAccount savingsAccount) throws Exception {
        BigDecimal transferAmount = new BigDecimal(amount);

        if (transferFrom.equalsIgnoreCase("Primary") && transferTo.equalsIgnoreCase("Savings")) {
            if (primaryAccount.getAccountBalance().compareTo(transferAmount) < 0) {
                throw new Exception("主账户余额不足");
            }
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(transferAmount));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(transferAmount));
            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date,
                    "账户间转账：转账给" + transferTo, "Transfer", "Finished", Double.parseDouble(amount),
                    primaryAccount.getAccountBalance(), primaryAccount);
            primaryTransactionDao.save(primaryTransaction);
            SavingsTransaction savingsTransaction = new SavingsTransaction(date,
                    "账户间转账：从" + transferFrom + "转入", "Transfer", "Finished", Double.parseDouble(amount),
                    savingsAccount.getAccountBalance(), savingsAccount);
            savingsTransactionDao.save(savingsTransaction);
        } else if (transferFrom.equalsIgnoreCase("Savings") && transferTo.equalsIgnoreCase("Primary")) {
            if (savingsAccount.getAccountBalance().compareTo(transferAmount) < 0) {
                throw new Exception("储蓄账户余额不足");
            }
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(transferAmount));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(transferAmount));
            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date,
                    "账户间转账：转账给" + transferTo, "Transfer", "Finished", Double.parseDouble(amount),
                    savingsAccount.getAccountBalance(), savingsAccount);
            savingsTransactionDao.save(savingsTransaction);
            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date,
                    "账户间转账：从" + transferFrom + "转入", "Transfer", "Finished", Double.parseDouble(amount),
                    primaryAccount.getAccountBalance(), primaryAccount);
            primaryTransactionDao.save(primaryTransaction);
        } else {
            throw new Exception("无效的转账操作");
        }
    }

    public List<Recipient> findRecipientList(Principal principal) {
        String username = principal.getName();
        List<Recipient> recipientList = recipientDao.findAll().stream() // convert list to stream
                .filter(recipient -> username.equals(recipient.getUser().getUsername())) // filters the line, equals to
                                                                                         // username
                .collect(Collectors.toList());

        return recipientList;
    }

    public Recipient saveRecipient(Recipient recipient) {
        return recipientDao.save(recipient);
    }

    public Recipient findRecipientByName(String recipientName) {
        return recipientDao.findByName(recipientName);
    }

    public void deleteRecipientByName(String recipientName) {
        recipientDao.deleteByName(recipientName);
    }

    public void toSomeoneElseTransfer(Recipient recipient, String accountType, String amount,
            PrimaryAccount primaryAccount, SavingsAccount savingsAccount) {
        BigDecimal transferAmount = new BigDecimal(amount);

        User recipientUser = userService.findByUsername(recipient.getAccountNumber());

        if (recipientUser == null) {
            throw new RuntimeException("收款人账户不存在，无法转账");
        }

        PrimaryAccount recipientPrimaryAccount = recipientUser.getPrimaryAccount();
        SavingsAccount recipientSavingsAccount = recipientUser.getSavingsAccount();

        if (accountType.equalsIgnoreCase("Primary")) {
            if (primaryAccount.getAccountBalance().compareTo(transferAmount) < 0) {
                throw new RuntimeException("主账户余额不足");
            }
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(transferAmount));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "转账给收款人：" + recipient.getName(),
                    "Transfer", "Finished", Double.parseDouble(amount), primaryAccount.getAccountBalance(),
                    primaryAccount);
            primaryTransactionDao.save(primaryTransaction);

            recipientPrimaryAccount.setAccountBalance(recipientPrimaryAccount.getAccountBalance().add(transferAmount));
            primaryAccountDao.save(recipientPrimaryAccount);

            PrimaryTransaction recipientTransaction = new PrimaryTransaction(date, "收到来自" + recipient.getName() + "的转账",
                    "Transfer", "Finished", Double.parseDouble(amount), recipientPrimaryAccount.getAccountBalance(),
                    recipientPrimaryAccount);
            primaryTransactionDao.save(recipientTransaction);
        } else if (accountType.equalsIgnoreCase("Savings")) {
            if (savingsAccount.getAccountBalance().compareTo(transferAmount) < 0) {
                throw new RuntimeException("储蓄账户余额不足");
            }
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(transferAmount));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "转账给收款人：" + recipient.getName(),
                    "Transfer", "Finished", Double.parseDouble(amount), savingsAccount.getAccountBalance(),
                    savingsAccount);
            savingsTransactionDao.save(savingsTransaction);

            recipientSavingsAccount.setAccountBalance(recipientSavingsAccount.getAccountBalance().add(transferAmount));
            savingsAccountDao.save(recipientSavingsAccount);

            SavingsTransaction recipientTransaction = new SavingsTransaction(date, "收到来自" + recipient.getName() + "的转账",
                    "Transfer", "Finished", Double.parseDouble(amount), recipientSavingsAccount.getAccountBalance(),
                    recipientSavingsAccount);
            savingsTransactionDao.save(recipientTransaction);
        }
    }
}
