package ma.fsm.lab_manager.services;

import ma.fsm.lab_manager.entities.*;
import ma.fsm.lab_manager.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommunicationService {
    @Autowired private MessageRepository messageRepository;
    @Autowired private CommentaireRepository commentaireRepository;
    @Autowired private AuditService auditService;

    public List<Message> getTeamMessages(Long equipeId) {
        return messageRepository.findByEquipeIdOrderByDateEnvoiAsc(equipeId);
    }

    public List<Message> getEventMessages(Long evenementId) {
        return messageRepository.findByEvenementIdOrderByDateEnvoiAsc(evenementId);
    }

    public List<Message> getPublicMessages() {
        return messageRepository.findByEquipeIsNullAndEvenementIsNullOrderByDateEnvoiAsc();
    }

    public Message saveMessage(Message message) {
        message.setDateEnvoi(LocalDateTime.now());
        Message saved = messageRepository.save(message);
        String type = message.getEquipe() != null ? "TEAM_ROOM" : (message.getEvenement() != null ? "EVENT_ROOM" : "PUBLIC_ROOM");
        auditService.log("MESSAGE_SENT", "Type: " + type + ", Content: " + message.getContenu());
        return saved;
    }

    public List<Commentaire> getCommentsByPublication(Long pubId) {
        return commentaireRepository.findByPublicationId(pubId);
    }

    public List<Commentaire> getCommentsByDocument(Long docId) {
        return commentaireRepository.findByDocumentId(docId);
    }

    public Commentaire saveComment(Commentaire comment) {
        comment.setDateCreation(LocalDateTime.now());
        Commentaire saved = commentaireRepository.save(comment);
        String target = comment.getPublication() != null ? "Publication " + comment.getPublication().getId() : "Document " + comment.getDocument().getId();
        auditService.log("COMMENT_ADDED", "Target: " + target);
        return saved;
    }
}