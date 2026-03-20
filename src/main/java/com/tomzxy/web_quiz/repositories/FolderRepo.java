package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Folder;
import com.tomzxy.web_quiz.models.Question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepo extends JpaRepository<Folder, Long> {
    
    // Get all folders in a QuestionBank
    List<Folder> findByBankId(Long bankId);
    
    // Get root folders (parent_id = null) in a QuestionBank
    @Query("SELECT f FROM Folder f WHERE f.bank.id = :bankId AND f.parent IS NULL")
    List<Folder> findRootFoldersByBankId(@Param("bankId") Long bankId);
    
    // Get subfolders of a specific parent
    List<Folder> findByParentId(Long parentId);
    
    // Find folder by name in bank
    @Query("SELECT f FROM Folder f WHERE f.bank.id = :bankId AND f.name = :name AND f.parent IS NULL")
    Optional<Folder> findByBankIdAndName(@Param("bankId") Long bankId, @Param("name") String name);
    
    // Find folder by name and parent
    @Query("SELECT f FROM Folder f WHERE f.parent.id = :parentId AND f.name = :name")
    Optional<Folder> findByParentIdAndName(@Param("parentId") Long parentId, @Param("name") String name);
    
    // Check if folder exists in bank
    boolean existsByBankIdAndId(Long bankId, Long folderId);
    
    // Get folders with pagination
    Page<Folder> findByBankId(Long bankId, Pageable pageable);
    
    // Get folder with all details
    @Query("SELECT DISTINCT f FROM Folder f " +
           "LEFT JOIN FETCH f.children c " +
           "LEFT JOIN FETCH f.questions q " +
           "WHERE f.id = :folderId")
    Optional<Folder> findByIdWithDetails(@Param("folderId") Long folderId);
    @Query("SELECT f FROM Folder f LEFT JOIN FETCH f.children WHERE f.id = :id")
    
    Optional<Folder> findByIdWithChildren(@Param("id") Long id);
    @Query("SELECT q FROM Question q WHERE q.folder.id = :id")
    Optional<List<Question>> findByIdWithQuestions(@Param("id") Long id);
    
    // Get root folders with children (batch load to avoid N+1)
    @Query("SELECT DISTINCT f FROM Folder f " +
           "LEFT JOIN FETCH f.children " +
           "WHERE f.bank.id = :bankId AND f.parent IS NULL")
    List<Folder> findRootFoldersWithChildren(@Param("bankId") Long bankId);
    
    // Check folder ownership in single query (avoid N+1)
    @Query("SELECT COUNT(f) > 0 FROM Folder f " +
           "WHERE f.id = :folderId AND f.bank.owner.id = :userId")
    boolean existsByIdAndOwnerId(@Param("folderId") Long folderId, 
                                 @Param("userId") Long userId);
    
    // Soft delete folder and children
    @Modifying
    @Transactional
    @Query("UPDATE Folder f SET f.isActive = false " +
           "WHERE f.id = :folderId OR f.parent.id = :folderId")
    void deactivateByIdAndChildren(@Param("folderId") Long folderId);
    
    // Check circular reference using recursive query
    @Query(value = "WITH RECURSIVE folder_path AS (" +
           "  SELECT id, parent_id FROM folder WHERE id = ?2 " +
           "  UNION ALL " +
           "  SELECT f.id, f.parent_id FROM folder f " +
           "  INNER JOIN folder_path fp ON f.parent_id = fp.id" +
           ") SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM folder_path WHERE id = ?1",
           nativeQuery = true)
    boolean isDescendantOf(Long folderId, Long parentId);
}
