package com.railway.user_service.repository;

import com.railway.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for User entity,
 * provides methods to perform CRUD operations and custom queries.
 */
public interface IUserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user
     * @return an Optional containing the User if found, else empty
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their username.
     *
     * @param userName the username of the user
     * @return an Optional containing the User if found, else empty
     */
    Optional<User> findByUserName(String userName);
}
