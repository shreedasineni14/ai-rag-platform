package com.rag.service;

import com.rag.entity.Document;
import com.rag.entity.DocumentChunk;
import com.rag.repository.DocumentChunkRepository;
import com.rag.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

	private final DocumentRepository documentRepository;
	private final DocumentChunkRepository documentChunkRepository;
	private final EmbeddingService embeddingService;
	private final GenerationService generationService;

	private static final int CHUNK_SIZE = 500;
	private static final String DEFAULT_TENANT = "tenant_1";

	@Transactional
	public void processDocument(MultipartFile file) {

		String content = extractText(file);
		UUID documentId = saveDocumentMetadata(file);
		List<String> chunks = chunkText(content, CHUNK_SIZE);

		saveChunksWithEmbeddings(documentId, chunks);

		System.out.println("Document ID: " + documentId);
		System.out.println("Chunks saved: " + chunks.size());
	}

	private String extractText(MultipartFile file) {
		try {
			Tika tika = new Tika();
			return tika.parseToString(file.getInputStream());
		} catch (Exception e) {
			throw new RuntimeException("Error extracting text from file", e);
		}
	}

	private UUID saveDocumentMetadata(MultipartFile file) {

		UUID documentId = UUID.randomUUID();

		Document document = Document.builder().id(documentId).name(file.getOriginalFilename()).tenantId(DEFAULT_TENANT)
				.version(1).createdAt(LocalDateTime.now()).build();

		documentRepository.save(document);

		return documentId;
	}

	private void saveChunksWithEmbeddings(UUID documentId, List<String> chunks) {

		for (String chunk : chunks) {

			float[] vector = embeddingService.generateEmbedding(chunk);

			DocumentChunk documentChunk = DocumentChunk.builder().id(UUID.randomUUID()).documentId(documentId)
					.content(chunk).embedding(vector).build();

			documentChunkRepository.save(documentChunk);
		}
	}

	private List<String> chunkText(String text, int chunkSize) {

		List<String> chunks = new ArrayList<>();

		int start = 0;
		while (start < text.length()) {
			int end = Math.min(text.length(), start + chunkSize);
			chunks.add(text.substring(start, end));
			start = end;
		}

		return chunks;
	}

	public String askQuestion(String question) {

	    float[] queryVector = embeddingService.generateEmbedding(question);
	    String vectorString = toVectorString(queryVector);

	    List<Object[]> results =
	            documentChunkRepository.findTopKSimilarRaw(vectorString, 3);

	    StringBuilder contextBuilder = new StringBuilder();

	    for (Object[] row : results) {
	        contextBuilder.append(row[2]).append("\n");
	    }

	    return generationService.generateAnswer(
	            question,
	            contextBuilder.toString()
	    );
	}


	private String toVectorString(float[] vector) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < vector.length; i++) {
			sb.append(vector[i]);
			if (i < vector.length - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();

	}
}
