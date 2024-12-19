package com.k0rwin.repository;

import com.k0rwin.entity.Item;
import com.k0rwin.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
}
