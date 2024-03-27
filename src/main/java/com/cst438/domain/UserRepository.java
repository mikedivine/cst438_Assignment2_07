package com.cst438.domain;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer>{

	List<User> findAllByOrderByIdAsc();

	User findByEmail(String email);
}
