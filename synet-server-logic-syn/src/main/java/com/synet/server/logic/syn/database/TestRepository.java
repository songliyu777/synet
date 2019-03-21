package com.synet.server.logic.syn.database;

import com.synet.server.logic.syn.database.bean.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
}
