package com.jwt.auth.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
    /*siguiendo la  convención de Spring Data JPA Spring analiza este nombre y lo interpreta como una consulta para 
     * buscar usuarios toma el parametro username para filtrar la busqueda
     */
    Optional<User> findByUsername(String username);
}
