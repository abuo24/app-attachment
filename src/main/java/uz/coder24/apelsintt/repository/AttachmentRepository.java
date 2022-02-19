package uz.coder24.apelsintt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.coder24.apelsintt.entity.Attachment;

import java.util.Date;
import java.util.Optional;


@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findByHashId(String hashId);

    @Query(value = "select a from Attachment a where a.hashId=?1")
    Optional<Attachment> findByHashIds(String hashId);

    @Query(nativeQuery = true,
            value = "select * from public.attachment a where lower(a.name) like %:search% and (a.created_at between cast(:startDate AS timestamp) and cast(:finishDate AS timestamp) ) and a.file_size>=:minFileSize and a.file_size<=:maxFileSize ")
    Page<Attachment> findAllBySort(Pageable pageable, @Param("search") String search,@Param("startDate")  Date startDate,@Param("finishDate") Date finishDate,@Param("minFileSize") Long minFileSize,@Param("maxFileSize") Long maxFileSize);

    @Query(value = "select * from file f where f.hash_id=?1", nativeQuery = true)
    Attachment getFileByHashId(String hashId);

}
