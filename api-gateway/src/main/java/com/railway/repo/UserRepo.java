package com.railway.repo;

import java.util.UUID;
import com.railway.api_gateway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    User findByUserName(String userName);
}