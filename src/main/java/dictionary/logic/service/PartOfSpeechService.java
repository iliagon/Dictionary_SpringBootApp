package dictionary.logic.service;

import dictionary.exception.PartOfSpeechNotFoundException;
import dictionary.logic.repository.PartOfSpeechRepository;
import dictionary.model.entity.PartOfSpeech;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartOfSpeechService {
    private final PartOfSpeechRepository repository;

    public PartOfSpeech replace(PartOfSpeech newPartOfSpeech, String code) {
         return repository.save(newPartOfSpeech.setCode(code));
    }

    public PartOfSpeech findByCode(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new PartOfSpeechNotFoundException(code));
    }

    public List<PartOfSpeech> findAll() {
        return repository.findAll();
    }

    public PartOfSpeech add(PartOfSpeech partOfSpeech) {
        return repository.save(partOfSpeech);
    }

    public void delete(String code) {
        repository.deleteById(code);
    }
}
