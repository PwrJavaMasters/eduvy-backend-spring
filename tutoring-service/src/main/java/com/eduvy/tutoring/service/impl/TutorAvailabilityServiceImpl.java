package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.dto.availibility.AddAvailabilityBlockRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityResponse;
import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.model.TutorAvailability;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.model.utils.HoursBlock;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.repository.TutorAvailabilityRepository;
import com.eduvy.tutoring.repository.TutorProfileRepository;
import com.eduvy.tutoring.service.TutorAvailabilityService;
import com.eduvy.tutoring.service.TutorProfileService;
import com.eduvy.tutoring.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.eduvy.tutoring.utils.SecurityContextHolderUtils.getCurrentUserMailFromContext;

@Service
@AllArgsConstructor
public class TutorAvailabilityServiceImpl implements TutorAvailabilityService {

    UserService userService;
    TutorProfileService tutorProfileService;

    TutorAvailabilityRepository tutorAvailabilityRepository;
    TutorProfileRepository tutorProfileRepository;
    AppointmentRepository appointmentRepository;


    @Override
    @Transactional
    public ResponseEntity<GetAvailabilityResponse> addAvailabilityBlock(AddAvailabilityBlockRequest addAvailabilityBlockRequest) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (addAvailabilityBlockRequest == null )
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByDay(addAvailabilityBlockRequest.getDay());
        if (tutorAvailability == null)
            tutorAvailability = new TutorAvailability(addAvailabilityBlockRequest.getDay(), userMail);

        //todo do validation if block is fine and eventually add block or do some refactoring
        tutorAvailability.getHoursBlockList().add(new HoursBlock(addAvailabilityBlockRequest.getStartTime(), addAvailabilityBlockRequest.getEndTime()));
        tutorAvailabilityRepository.saveAndFlush(tutorAvailability);

        return ResponseEntity.ok(new GetAvailabilityResponse(tutorAvailability.getDay(), tutorAvailability.getHoursBlockList()));
    }

    @Override
    public ResponseEntity<GetAvailabilityResponse> getDayAvailability(GetAvailabilityRequest getAvailabilityRequest) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (getAvailabilityRequest == null )
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByTutorAndDay(userMail , getAvailabilityRequest.getDay());
        if (tutorAvailability == null)
            return ResponseEntity.ok(new GetAvailabilityResponse(getAvailabilityRequest.getDay(), new ArrayList<>()));

        return ResponseEntity.ok(new GetAvailabilityResponse(tutorAvailability.getDay(), tutorAvailability.getHoursBlockList()));
    }

    @Override
    public ResponseEntity<GetAvailabilityResponse> getDayAvailabilityByTutorId(String tutorId, GetAvailabilityRequest getAvailabilityRequest) {
        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorId(tutorId);
        if (tutorProfile == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (getAvailabilityRequest == null )
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByTutorAndDay(tutorProfile.getTutorMail() , getAvailabilityRequest.getDay());
        tutorAvailability = getDailyAvailabilityExcludingMeetings(tutorAvailability,
                appointmentRepository.findAppointmentsByTutorProfileAndDay(tutorProfile, getAvailabilityRequest.getDay()));

        if (tutorAvailability == null)
            return ResponseEntity.ok(new GetAvailabilityResponse(getAvailabilityRequest.getDay(), new ArrayList<>()));


        return ResponseEntity.ok(new GetAvailabilityResponse(tutorAvailability.getDay(), tutorAvailability.getHoursBlockList()));
    }

    public TutorAvailability getDailyAvailabilityExcludingMeetings(TutorAvailability tutorAvailability, List<Appointment> appointments) {
        if (tutorAvailability == null) return null;
        List<HoursBlock> updatedHoursBlocks = new ArrayList<>();

        for (HoursBlock hoursBlock : tutorAvailability.getHoursBlockList()) {
            List<HoursBlock> nonOverlappingBlocks = getNonOverlappingBlocks(hoursBlock, appointments);
            updatedHoursBlocks.addAll(nonOverlappingBlocks);
        }

        tutorAvailability.setHoursBlockList(updatedHoursBlocks);
        return tutorAvailability;
    }

    private List<HoursBlock> getNonOverlappingBlocks(HoursBlock hoursBlock, List<Appointment> appointments) {
        List<HoursBlock> nonOverlappingBlocks = new ArrayList<>();
        nonOverlappingBlocks.add(hoursBlock);

        List<HoursBlock> blocksToAdd = new ArrayList<>();
        List<HoursBlock> blocksToRemove = new ArrayList<>();

        for (Appointment appointment : appointments) {
            for (HoursBlock block : nonOverlappingBlocks) {
                if (appointment.getStartDate().before(block.getEndTime()) && appointment.getEndDate().after(block.getStartTime())) {
                    blocksToRemove.add(block);

                    if (block.getStartTime().before(appointment.getStartDate())) {
                        blocksToAdd.add(new HoursBlock(block.getStartTime(), appointment.getStartDate()));
                    }

                    if (block.getEndTime().after(appointment.getEndDate())) {
                        blocksToAdd.add(new HoursBlock(appointment.getEndDate(), block.getEndTime()));
                    }
                }
            }

            nonOverlappingBlocks.removeAll(blocksToRemove);
            nonOverlappingBlocks.addAll(blocksToAdd);

            blocksToRemove.clear();
            blocksToAdd.clear();
        }

        return nonOverlappingBlocks;
    }
}