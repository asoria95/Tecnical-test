package com.challenge.account.application;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    List<AccountResponse> findAll();

    AccountResponse findById(Long id);

    AccountResponse updateAccount(Long id, UpdateAccountRequest request);

    void deleteAccount(Long id);
}
