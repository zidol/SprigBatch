package kr.co.imin.batch.repository;

import kr.co.imin.batch.entity.PayTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PayRepository extends JpaRepository<PayTest, Long> {
    @Query("SELECT p FROM PayTest p WHERE p.successStatus = true")
    List<PayTest> findAllSuccess();
}
