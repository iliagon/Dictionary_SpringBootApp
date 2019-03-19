//package dictionary.logic.controllers.resource.assembler;
//
//import dictionary.logic.controllers.TranslateController;
//import dictionary.model.dto.Translate;
//import lombok.RequiredArgsConstructor;
//import org.springframework.hateoas.mvc.ControllerLinkBuilder;
//import org.springframework.stereotype.Component;
//
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
//
//@Component
//@RequiredArgsConstructor
//public class TranslateResourceAssembler{
//    private final WordResourceAssembler wordResourceAssembler;
//
//    public Translate setLinks(Translate translate) {
//        wordResourceAssembler.setLinks(translate.getWord1());
//        wordResourceAssembler.setLinks(translate.getWord2());
//
//        translate.add(ControllerLinkBuilder.linkTo(
//                methodOn(TranslateController.class).getTranslate(translate.getTranslateRelationUUID())).withSelfRel());
//
//        return translate;
//    }
//}
