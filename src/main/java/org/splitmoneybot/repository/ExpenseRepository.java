package org.splitmoneybot.repository;

import org.splitmoneybot.entity.AppUser;
import org.splitmoneybot.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByPaidBy(AppUser paidBy);
}
