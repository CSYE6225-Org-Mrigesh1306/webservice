package com.example.webapp.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webapp.Model.User;

public interface UserRepository extends JpaRepository<User,Long> {

}
