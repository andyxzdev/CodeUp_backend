package com.codeUp.demo.repository;

import com.codeUp.demo.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario,Long> {

    List<Comentario> findByPublicacaoIdOrderByCreatedAtDesc(Long publicacaoId);
}
