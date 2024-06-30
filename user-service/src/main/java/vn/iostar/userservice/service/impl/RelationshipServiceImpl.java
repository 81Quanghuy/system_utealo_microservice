package vn.iostar.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import vn.iostar.userservice.entity.Relationship;
import vn.iostar.userservice.repository.RelationshipRepository;
import vn.iostar.userservice.service.RelationshipService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelationshipServiceImpl implements RelationshipService {

    private final RelationshipRepository relationshipRepository;

    @Override
    public List<Relationship> findAll() {
        return relationshipRepository.findAll();
    }

    @Override
    public <S extends Relationship> S save(S entity) {
        return relationshipRepository.save(entity);
    }

    @Override
    public long count() {
        return relationshipRepository.count();
    }

    @Override
    public void deleteById(String s) {
        relationshipRepository.deleteById(s);
    }

    @Override
    public <S extends Relationship> long count(Example<S> example) {
        return relationshipRepository.count(example);
    }

    @Override
    public Optional<Relationship> findByParent(String id) {
        return relationshipRepository.findByParentUserId(id);
    }
}
