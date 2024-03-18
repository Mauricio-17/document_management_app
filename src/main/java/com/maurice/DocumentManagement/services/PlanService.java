package com.maurice.DocumentManagement.services;

import com.maurice.DocumentManagement.dto.PlanDto;
import com.maurice.DocumentManagement.exceptions.BadRequestException;
import com.maurice.DocumentManagement.exceptions.NotFoundException;
import com.maurice.DocumentManagement.repository.PlanRepository;
import com.maurice.DocumentManagement.entities.Plan;
import com.maurice.DocumentManagement.utils.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public Plan registerPlan(PlanDto plan){
        Optional<Plan> found = planRepository.findPlanByName(plan.name());
        if(found.isPresent()){
                throw new BadRequestException("The name "+plan.name()+" already exists.");
        }
        Plan saved = Mappers.dtoToPlan.apply(plan);
        saved.setCreatedAt(LocalDateTime.now());
        saved.setLastModifiedAt(LocalDateTime.now());
        return planRepository.save(saved);
    }

    public List<PlanDto> getAllPlans(){
        return planRepository.findAll().stream().map(Mappers.planToDto).toList();
    }

    public PlanDto getPlanById(Long id){
        return planRepository.findById(id).map(Mappers.planToDto).orElseThrow(
                () -> new NotFoundException("Plan not found.")
        );
    }

}
