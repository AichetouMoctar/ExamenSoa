package com.nationconnect.state_civil_service.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
public class IaController {

    // Tes règles d'État Civil
    private final String REGLES = """
            1. Âge minimum : 18 ans.
            2. Documents mariage : Carte d'identité et Acte de naissance.
            3. Célébration : Officier d'état civil ou Imam autorisé.
            4. Certificat de mariage : délivré sous 48 heures.
            5. Naissance : enregistrement sous 15 jours.
            """;

    @PostMapping("/aide")
    public Map<String, String> aide(@RequestParam("question") String question) {
        String q = question.toLowerCase();
        String reponse;

        // Logique de simulation d'IA (Recherche de mots-clés)
        if (q.contains("mariage") || q.contains("document")) {
            reponse = "Pour le mariage, il faut : une copie de la carte d'identité (National ID) et un acte de naissance.";
        } else if (q.contains("âge") || q.contains("age")) {
            reponse = "L'âge minimum pour se marier est de 18 ans pour les hommes et les femmes.";
        } else if (q.contains("naissance")) {
            reponse = "Pour enregistrer une naissance, le parent doit se présenter dans les 15 jours suivant l'accouchement.";
        } else if (q.contains("délai") || q.contains("heure")) {
            reponse = "Le certificat de mariage est délivré sous un délai de 48 heures.";
        } else {
            reponse = "Je peux vous renseigner sur le mariage (âge, documents, célébration) et les naissances. Voici nos règles :\n" + REGLES;
        }

        return Map.of(
            "reponse", reponse
            
        );
    }
}