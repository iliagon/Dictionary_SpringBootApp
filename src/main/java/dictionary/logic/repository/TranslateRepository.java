package dictionary.logic.repository;

import dictionary.model.entity.Language;
import dictionary.model.entity.TranslateRelation;
import dictionary.model.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TranslateRepository extends JpaRepository<TranslateRelation, Long> {

    @Query("select t from TranslateRelation t " +
            "where t.compositePK.word = :word1 or t.compositePK.word = :word2")
    List<TranslateRelation> findTranslateRelations(Word word1, Word word2);

    @Query("select t from TranslateRelation t " +
            "where t.compositePK.lang = :language and t.compositePK.translateRelationUUID in " +
            "                         (select t1.compositePK.translateRelationUUID from TranslateRelation t1 " +
            "                           where t1.compositePK.word = :word1)")
    List<TranslateRelation> findTranslateRelations(Word word1, Language language);

    @Query("select t from TranslateRelation t " +
            "where t.compositePK.translateRelationUUID = :translateRelationUUID")
    List<TranslateRelation> findTranslateRelations(String translateRelationUUID);

    @Modifying
    @Transactional
    @Query("delete from TranslateRelation t " +
            "where t.compositePK.translateRelationUUID = :translateRelationUUID" +
            "   and t.compositePK.word = :word")
    void deleteByTranslateRelationsUUIDAndWordId(String translateRelationUUID, Word word);

    @Modifying
    @Transactional
    @Query("update TranslateRelation t set t.compositePK.translateRelationUUID = :newTranslateRelationsUUID " +
            "where t.compositePK.translateRelationUUID = :oldTranslateRelationsUUID")
    void updateTranslateRelationsUUID(String oldTranslateRelationsUUID, String newTranslateRelationsUUID);
}
