package dictionary.logic.service;

import dictionary.exception.LangNotFoundException;
import dictionary.logic.repository.LangRepository;
import dictionary.model.entity.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LangService {
    private final LangRepository repository;

    public Language replace(Language newLanguage, String code) {
         return repository.findById(code)
                .map(partOfSpeech ->
                        repository.save(
                                partOfSpeech.setInfo(newLanguage.getInfo()))
                )
                .orElseGet(() ->
                        repository.save(newLanguage.setCode(code))
                );
    }

    public Language findByCode(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new LangNotFoundException(code));
    }

    public List<Language> findAll() {
        return repository.findAll();
    }

    public Language add(Language language) {
        return repository.save(language);
    }

    public void delete(String code) {
        repository.deleteById(code);
    }
}
