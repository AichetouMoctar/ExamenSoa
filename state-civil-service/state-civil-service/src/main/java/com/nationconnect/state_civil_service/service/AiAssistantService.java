package com.nationconnect.state_civil_service.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiAssistantService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public AiAssistantService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public String genererReponse(String question) {
        // 1. Chercher les informations pertinentes dans le manuel (Vector DB)
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.query(question).withTopK(2));
        
        // 2. Créer le contexte à partir des textes trouvés
        String contexte = documents.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        // 3. Construire le message pour l'IA
        String messageComplet = "Tu es l'assistant du service État Civil. " +
                "Utilise UNIQUEMENT ce contexte pour répondre : \n" + contexte + 
                "\n\nQuestion du citoyen : " + question;

        // 4. Envoyer à l'IA et retourner la réponse
        return chatClient.call(messageComplet);
    }
}