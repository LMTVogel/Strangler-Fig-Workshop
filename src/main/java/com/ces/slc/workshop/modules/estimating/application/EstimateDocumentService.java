package com.ces.slc.workshop.modules.estimating.application;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ces.slc.workshop.modules.core.application.breakdown.BreakdownStructureService;
import com.ces.slc.workshop.modules.core.application.document.AbstractDocumentService;
import com.ces.slc.workshop.modules.core.web.dto.DocumentDto;
import com.ces.slc.workshop.modules.estimating.domain.EstimateComponent;
import com.ces.slc.workshop.modules.estimating.domain.EstimateDocument;
import com.ces.slc.workshop.modules.estimating.web.dto.EstimateComponentDto;
import com.ces.slc.workshop.modules.estimating.web.dto.EstimateImportComponentDto;
import com.ces.slc.workshop.modules.knowledgebase.domain.KnowledgebaseDocument;
import com.ces.slc.workshop.security.domain.User;

@Service
public class EstimateDocumentService extends AbstractDocumentService<EstimateDocument, EstimateComponent> {

    protected EstimateDocumentService(
            EstimateDocumentMapper documentMapper,
            EstimateDocumentRepository documentRepository,
            EstimateComponentRepository documentComponentRepository,
            BreakdownStructureService breakdownStructureService,
            EstimateComponentService estimateComponentService) {
        super(documentRepository, documentComponentRepository, breakdownStructureService, estimateComponentService);
    }

    @Override
    public EstimateDocumentRepository getDocumentRepository() {
        return (EstimateDocumentRepository) super.getDocumentRepository();
    }

    @Override
    protected EstimateDocument createNewDocument(User author, DocumentDto documentDto) {
        EstimateDocument estimateDocument = new EstimateDocument(documentDto.name(), author, LocalDateTime.now());
        copyDocumentMetadata(estimateDocument, documentDto);
        return estimateDocument;
    }

    @Override
    protected void updateDocument(EstimateDocument existingDocument, DocumentDto documentDto) {
        copyDocumentMetadata(existingDocument, documentDto);
    }

    @Override
    public Optional<Set<EstimateComponent>> getComponents(Long id) {
        return getDocumentById(id).map(document -> getDocumentRepository().getTopLevelComponents(document));
    }

    public Optional<Set<EstimateComponent>> getComponentChildren(Long id, Long componentId) {
        return getDocumentById(id)
                .flatMap(document -> getDocumentComponentService().getComponentChildren(document, componentId));
    }

    @Override
    public EstimateComponentService getDocumentComponentService() {
        return (EstimateComponentService) super.getDocumentComponentService();
    }

    public Optional<EstimateComponent> createComponentChild(Long id, Long componentId, EstimateComponentDto componentDto) {
        return getDocumentById(id)
                .flatMap(document -> getDocumentComponentService().createComponentChild(document, componentId, componentDto));
    }

    public Optional<EstimateComponent> importTopLevelComponent(Long id, EstimateImportComponentDto componentDto) {
        return getDocumentById(id)
                .flatMap(document -> getDocumentComponentService().importTopLevelComponent(document, componentDto));
    }

    public Optional<EstimateComponent> importComponent(Long id, Long parentId, EstimateImportComponentDto componentDto) {
        return getDocumentById(id)
                .flatMap(document -> getDocumentComponentService().importComponent(document, parentId, componentDto));
    }

    public Optional<Set<KnowledgebaseDocument>> getReferencedKnowledgebases(Long id) {
        return getDocumentById(id).map(document -> getDocumentRepository().getKnowledgebaseReferences(document));
    }
}
