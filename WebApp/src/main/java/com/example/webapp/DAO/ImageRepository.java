package com.example.webapp.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.webapp.Model.Image;
import com.example.webapp.Model.User;

@Repository
@Transactional
public interface ImageRepository extends JpaRepository<Image,Long> {

}
