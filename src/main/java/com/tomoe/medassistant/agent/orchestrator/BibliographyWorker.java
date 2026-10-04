package com.tomoe.medassistant.agent.orchestrator;


import com.tomoe.medassistant.config.ClientResolver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class BibliographyWorker {

    private final ClientResolver clientResolver;

    @Value("classpath:prompts/worker-bibliography.st")
    private Resource bibliographyResource;

    private PromptTemplate bibliographyTemplate;

    @PostConstruct
    void init() {
        bibliographyTemplate = new PromptTemplate(bibliographyResource);
    }

    private String interpret(List<Document> documents, String symptoms, String model){
        String documentContent = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = bibliographyTemplate.render(Map.of(
                "documentos", documentContent,
                "sintomas", symptoms
        ));

        String result = ChatClient.create(clientResolver.resolveModel(model))
                .prompt()
                .system("Eres un especialista en bibliografia medica.")
                .user(prompt)
                .call()
                .content();

        log.info("Worker bibliografia - interpretacion Completa");
        return result;
    }

    private List<Document> searchDocument(String sypmtoms, String model){
        List<Document> documents = clientResolver.resolveVectorStore(model)
                .similaritySearch(
                        SearchRequest.builder()
                                .query(sypmtoms)
                                .topK(3)
                                .similarityThreshold(0.7)
                                .build());

        log.info("Worker bibliografia - documentos encontrados: {}", documents.size());
        return documents;
    }

    public String research(String symptoms, String model){
        List<Document> documents = searchDocument(symptoms, model);

        if (documents.isEmpty()){
            return "No se encontraron documentos medicos relevantes para estos sintomas";
        }

        return interpret(documents, symptoms, model);
    }
}
