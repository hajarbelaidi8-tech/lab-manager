package ma.fsm.lab_manager.web;

import ma.fsm.lab_manager.entities.Commentaire;
import ma.fsm.lab_manager.entities.Message;
import ma.fsm.lab_manager.services.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comm")
public class CommRestController {
    @Autowired private CommunicationService communicationService;

    @GetMapping("/messages/team/{equipeId}")
    public List<Message> getTeamMessages(@PathVariable Long equipeId) {
        return communicationService.getTeamMessages(equipeId);
    }

    @GetMapping("/messages/event/{evenementId}")
    public List<Message> getEventMessages(@PathVariable Long evenementId) {
        return communicationService.getEventMessages(evenementId);
    }

    @GetMapping("/messages/public")
    public List<Message> getPublicMessages() {
        return communicationService.getPublicMessages();
    }

    @PostMapping("/messages")
    public Message sendMessage(@RequestBody Message message) {
        return communicationService.saveMessage(message);
    }

    @GetMapping("/comments/publication/{pubId}")
    public List<Commentaire> getPubComments(@PathVariable Long pubId) {
        return communicationService.getCommentsByPublication(pubId);
    }

    @GetMapping("/comments/document/{docId}")
    public List<Commentaire> getDocComments(@PathVariable Long docId) {
        return communicationService.getCommentsByDocument(docId);
    }

    @PostMapping("/comments")
    public Commentaire addComment(@RequestBody Commentaire comment) {
        return communicationService.saveComment(comment);
    }
}