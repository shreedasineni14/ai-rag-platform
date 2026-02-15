package com.rag.repository;

import com.rag.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {
	@Query(value = """
		    SELECT id, document_id, content
		    FROM document_chunks
		    ORDER BY embedding <-> CAST(:embedding AS vector)
		    LIMIT :limit
		    """, nativeQuery = true)
		List<Object[]> findTopKSimilarRaw(
		        @Param("embedding") String embedding,
		        @Param("limit") int limit);


	
}
