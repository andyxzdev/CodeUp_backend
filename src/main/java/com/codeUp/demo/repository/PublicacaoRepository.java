package com.codeUp.demo.repository;

import com.codeUp.demo.model.Publicacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacaoRepository extends JpaRepository<Publicacao, Long>{
    List<Publicacao> findAllByOrderByCreatedAtDesc();
    List<Publicacao> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    Page<Publicacao> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
