package com.example.Annuaire.Repository;

import com.example.Annuaire.Models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByDeleted(int deleted);

    Optional<User> findByIdAndDeleted(Long id, int deleted);
}
