package pl.tomaszdziurko.jvm_bloggers.people.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByNameIgnoreCase(String name);
    Optional<Person> findByJsonId(Long jsonId);

    List<Person> findByDateAddedAfter(LocalDateTime lastNewsletterSent);
}
