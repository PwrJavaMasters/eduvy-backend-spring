package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.dto.availibility.AddAvailabilityBlockRequest;
import com.eduvy.tutoring.dto.availibility.DayRequest;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
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

        if (addAvailabilityBlockRequest == null || isBlockNotValid(addAvailabilityBlockRequest))
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByDay(addAvailabilityBlockRequest.getDay());
        if (tutorAvailability == null)
            tutorAvailability = new TutorAvailability(addAvailabilityBlockRequest.getDay(), userMail);

        Timestamp newStartTime = addAvailabilityBlockRequest.getStartTime();
        Timestamp newEndTime = addAvailabilityBlockRequest.getEndTime();

        List<HoursBlock> hoursBlockList = tutorAvailability.getHoursBlockList();
        List<HoursBlock> newHoursBlockList = new ArrayList<>();

        for (HoursBlock block : hoursBlockList) {
            if (blocksOverlapOrTouch(block, newStartTime, newEndTime)) {
                newStartTime = new Timestamp(Math.min(block.getStartTime().getTime(), newStartTime.getTime()));
                newEndTime = new Timestamp(Math.max(block.getEndTime().getTime(), newEndTime.getTime()));
            } else {
                newHoursBlockList.add(block);
            }
        }

        newHoursBlockList.add(new HoursBlock(newStartTime, newEndTime));
        tutorAvailability.setHoursBlockList(newHoursBlockList);

        tutorAvailabilityRepository.saveAndFlush(tutorAvailability);

        return ResponseEntity.ok(new GetAvailabilityResponse(tutorAvailability.getDay(), tutorAvailability.getHoursBlockList()));
    }

    private boolean isBlockNotValid(AddAvailabilityBlockRequest request) {
        LocalDate startDateDay = request.getStartTime().toLocalDateTime().toLocalDate();
        LocalDate endDateDay = request.getEndTime().toLocalDateTime().toLocalDate();
        return  (!request.getDay().equals(startDateDay) || !request.getDay().equals(endDateDay));
    }

    private boolean blocksOverlapOrTouch(HoursBlock block, Timestamp newStartTime, Timestamp newEndTime) {
        return !newStartTime.after(block.getEndTime()) && !newEndTime.before(block.getStartTime());
    }

    @Override
    public ResponseEntity<Void> addAvailabilityBlockList(List<AddAvailabilityBlockRequest> addAvailabilityBlockRequestList) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (addAvailabilityBlockRequestList == null || addAvailabilityBlockRequestList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        for (AddAvailabilityBlockRequest addAvailabilityBlockRequest : addAvailabilityBlockRequestList) {
            if (isBlockNotValid(addAvailabilityBlockRequest)) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }

            TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByDay(addAvailabilityBlockRequest.getDay());
            if (tutorAvailability == null) {
                tutorAvailability = new TutorAvailability(addAvailabilityBlockRequest.getDay(), userMail);
            }

            Timestamp newStartTime = addAvailabilityBlockRequest.getStartTime();
            Timestamp newEndTime = addAvailabilityBlockRequest.getEndTime();

            List<HoursBlock> hoursBlockList = tutorAvailability.getHoursBlockList();
            List<HoursBlock> newHoursBlockList = new ArrayList<>();

            for (HoursBlock block : hoursBlockList) {
                if (blocksOverlapOrTouch(block, newStartTime, newEndTime)) {
                    newStartTime = new Timestamp(Math.min(block.getStartTime().getTime(), newStartTime.getTime()));
                    newEndTime = new Timestamp(Math.max(block.getEndTime().getTime(), newEndTime.getTime()));
                } else {
                    newHoursBlockList.add(block);
                }
            }

            newHoursBlockList.add(new HoursBlock(newStartTime, newEndTime));
            tutorAvailability.setHoursBlockList(newHoursBlockList);

            tutorAvailabilityRepository.saveAndFlush(tutorAvailability);
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<GetAvailabilityResponse> getDayAvailability(DayRequest getAvailabilityRequest) {
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
    public ResponseEntity<List<GetAvailabilityResponse>> getMonthAvailability(DayRequest getAvailabilityRequest) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (getAvailabilityRequest == null )
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        List<TutorAvailability> tutorAvailability = getTutorAvailabilityByMonth(userMail, getAvailabilityRequest.getDay());
        if (tutorAvailability == null)
            return ResponseEntity.ok(new ArrayList<>());

        List<GetAvailabilityResponse> responseList = new ArrayList<>();
        for (TutorAvailability availability : tutorAvailability) {
            responseList.add(new GetAvailabilityResponse(availability.getDay(), availability.getHoursBlockList()));
        }

        return ResponseEntity.ok(responseList);
    }

    public List<TutorAvailability> getTutorAvailabilityByMonth(String tutor, LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return tutorAvailabilityRepository.findTutorAvailabilityByMonth(tutor, startDate, endDate);
    }

    @Override
    public ResponseEntity<GetAvailabilityResponse> getDayAvailabilityByTutorId(String tutorId, DayRequest getAvailabilityRequest) {
        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorId(tutorId);
        if (tutorProfile == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (getAvailabilityRequest == null )
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByTutorAndDay(tutorProfile.getTutorMail() , getAvailabilityRequest.getDay());
        System.out.println("tutorAvailability: " + tutorAvailability);
        tutorAvailability = getDailyAvailabilityExcludingMeetings(tutorAvailability,
                appointmentRepository.findAppointmentsByTutorProfileAndDay(tutorProfile, getAvailabilityRequest.getDay()));

        if (tutorAvailability == null)
            return ResponseEntity.ok(new GetAvailabilityResponse(getAvailabilityRequest.getDay(), new ArrayList<>()));

        System.out.println("tutorAvailability: " + tutorAvailability.getHoursBlockList());

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