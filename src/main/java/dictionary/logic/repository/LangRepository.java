package dictionary.logic.repository;

import dictionary.model.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LangRepository extends JpaRepository<Language, String> {
}
