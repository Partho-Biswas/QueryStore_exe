package com.querystore.server.repository;

import com.querystore.server.model.Query;
import com.querystore.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface QueryRepository extends JpaRepository<Query, UUID> {
    List<Query> findByUser(User user);
    List<Query> findByUserAndTitleContainingIgnoreCase(User user, String title);
}
