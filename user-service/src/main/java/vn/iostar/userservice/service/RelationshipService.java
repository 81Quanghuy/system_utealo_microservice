package vn.iostar.userservice.service;

import org.springframework.data.domain.Example;
import vn.iostar.userservice.entity.Relationship;

import java.util.List;
import java.util.Optional;

public interface RelationshipService {
    List<Relationship> findAll();

    <S extends Relationship> S save(S entity);

    long count();

    void deleteById(String s);

    <S extends Relationship> long count(Example<S> example);

    Optional<Relationship> findByParent(String id);
}
