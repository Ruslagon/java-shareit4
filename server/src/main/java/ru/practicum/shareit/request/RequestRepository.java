package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findAllByUserIdOrderByCreatedDesc(Long userId, PageRequest pageRequest);

    Page<Request> findAllByUserIdNotOrderByCreatedDesc(Long userId, PageRequest pageRequest);
}
