package com.tomoe.medassistant.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DocumentIngestionService {

    private final VectorStore googleVectorStore;

    @Value("classpath:docs/farmacologia-ibuprofeno.pdf")
    private Resource ibuprofenoDoc;

    @Value("classpath:docs/guia-hipertension-medassistant.pdf")
    private Resource hipertensionDoc;

    @Value("classpath:docs/protocolo-atencion-cardiovascular.pdf")
    private Resource cardioVascularDoc;

    public DocumentIngestionService(
            @Qualifier("googleVectorStore") VectorStore googleVectorStore) {
        this.googleVectorStore = googleVectorStore;
    }

    @PostConstruct
    public void ingest(){
        log.info("Iniciando ingestion de documentos medicos...");
        List<Document> allDocuments = new ArrayList<>();
        allDocuments.addAll(readDocument(ibuprofenoDoc));
        allDocuments.addAll(readDocument(hipertensionDoc));
        allDocuments.addAll(readDocument(cardioVascularDoc));

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMaxNumChunks(1000)
                .build();

        List<Document> chunks = splitter.apply(allDocuments);

        log.info("Documentos divididos en {} fragmentos", chunks.size());

        log.info("Ingiriendo en collections Google...");
        googleVectorStore.add(chunks);
    }

    private List<Document> readDocument(Resource resource) {
        log.info("Leyendo documento: {}", resource.getFilename());
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        return reader.get();
    }
}
