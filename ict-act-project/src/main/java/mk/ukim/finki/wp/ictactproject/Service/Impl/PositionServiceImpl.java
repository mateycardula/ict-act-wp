package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.Position;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.PositionDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Repository.PositionRepository;
import mk.ukim.finki.wp.ictactproject.Service.PositionService;
import org.springframework.stereotype.Service;

@Service
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;

    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public Position findById(Long id) {
        return this.positionRepository.findById(id).orElseThrow(PositionDoesNotExist::new);
    }
}
