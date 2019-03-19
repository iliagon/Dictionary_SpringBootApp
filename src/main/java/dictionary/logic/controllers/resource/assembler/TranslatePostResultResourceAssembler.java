//package dictionary.logic.controllers.resource.assembler;
//
//import dictionary.logic.controllers.TranslateController;
//import dictionary.model.dto.TranslatePostResultDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.hateoas.mvc.ControllerLinkBuilder;
//import org.springframework.stereotype.Component;
//
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
//
//@Component
//@RequiredArgsConstructor
//public class TranslatePostResultResourceAssembler {
//    private final WordResourceAssembler wordResourceAssembler;
//
//    public TranslatePostResultDto setLinks(TranslatePostResultDto translatePostResult) {
//        translatePostResult.add(ControllerLinkBuilder.linkTo(
//                methodOn(TranslateController.class).getTranslate(translatePostResult.getTranslateRelationUUID())).withSelfRel());
//
//        return translatePostResult;
//    }
//}
