package com.eduvy.tutoring.service.impl;


import com.eduvy.tutoring.dto.availibility.AvailabilityBlockRequest;
import com.eduvy.tutoring.dto.availibility.DayRequest;
import com.eduvy.tutoring.dto.availibility.GetAvailabilityResponse;
import com.eduvy.tutoring.dto.availibility.GetMonthAvailabilityResponse;
import com.eduvy.tutoring.model.Appointment;
import com.eduvy.tutoring.model.TutorAvailability;
import com.eduvy.tutoring.model.TutorProfile;
import com.eduvy.tutoring.model.utils.HoursBlock;
import com.eduvy.tutoring.repository.AppointmentRepository;
import com.eduvy.tutoring.repository.TutorAvailabilityRepository;
import com.eduvy.tutoring.repository.TutorProfileRepository;
import com.eduvy.tutoring.service.AppointmentManagementService;
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
import java.util.*;

import static com.eduvy.tutoring.utils.SecurityContextHolderUtils.getCurrentUserMailFromContext;

@Service
@AllArgsConstructor
public class TutorAvailabilityServiceImpl implements TutorAvailabilityService {

    private final AppointmentManagementServiceImpl appointmentManagementServiceImpl;
    UserService userService;
    TutorProfileService tutorProfileService;
    AppointmentManagementService appointmentManagementService;

    TutorAvailabilityRepository tutorAvailabilityRepository;
    TutorProfileRepository tutorProfileRepository;
    AppointmentRepository appointmentRepository;


    @Override
    @Transactional
    public ResponseEntity<GetAvailabilityResponse> addAvailabilityBlock(AvailabilityBlockRequest availabilityBlockRequest) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (availabilityBlockRequest == null || isBlockNotValid(availabilityBlockRequest))
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByDay(availabilityBlockRequest.getDay());
        if (tutorAvailability == null)
            tutorAvailability = new TutorAvailability(availabilityBlockRequest.getDay(), userMail);

        Timestamp newStartTime = availabilityBlockRequest.getStartTime();
        Timestamp newEndTime = availabilityBlockRequest.getEndTime();

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

    private boolean isBlockNotValid(AvailabilityBlockRequest request) {
        LocalDate startDateDay = request.getStartTime().toLocalDateTime().toLocalDate();
        LocalDate endDateDay = request.getEndTime().toLocalDateTime().toLocalDate();
        return  (!request.getDay().equals(startDateDay) || !request.getDay().equals(endDateDay));
    }

    private boolean blocksOverlapOrTouch(HoursBlock block, Timestamp newStartTime, Timestamp newEndTime) {
        return !newStartTime.after(block.getEndTime()) && !newEndTime.before(block.getStartTime());
    }

    @Override
    public ResponseEntity<Void> addAvailabilityBlockList(List<AvailabilityBlockRequest> availabilityBlockRequestList) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (availabilityBlockRequestList == null || availabilityBlockRequestList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        for (AvailabilityBlockRequest availabilityBlockRequest : availabilityBlockRequestList) {
            if (isBlockNotValid(availabilityBlockRequest)) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }

            TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByDay(availabilityBlockRequest.getDay());
            if (tutorAvailability == null) {
                tutorAvailability = new TutorAvailability(availabilityBlockRequest.getDay(), userMail);
            }

            Timestamp newStartTime = availabilityBlockRequest.getStartTime();
            Timestamp newEndTime = availabilityBlockRequest.getEndTime();

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
    @Transactional
    public ResponseEntity<Void> deleteAvailabilityBlockList(List<AvailabilityBlockRequest> deleteAvailabilityBlockRequestList) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (deleteAvailabilityBlockRequestList == null || deleteAvailabilityBlockRequestList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        for (AvailabilityBlockRequest availabilityBlockRequest : deleteAvailabilityBlockRequestList) {
            if (isBlockNotValid(availabilityBlockRequest)) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }

            TutorAvailability tutorAvailability = tutorAvailabilityRepository.getTutorAvailabilityByDay(availabilityBlockRequest.getDay());

            if (tutorAvailability == null) {
                // No availability exists for this day; nothing to delete
                continue;
            }

            if (!tutorAvailability.getTutor().equals(userMail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Timestamp deleteStartTime = availabilityBlockRequest.getStartTime();
            Timestamp deleteEndTime = availabilityBlockRequest.getEndTime();

            deleteAvailabilityBlock(tutorAvailability, new HoursBlock(deleteStartTime, deleteEndTime));

            if (tutorAvailability.getHoursBlockList().isEmpty()) {
                // If no availability left for the day, delete the TutorAvailability record
                tutorAvailabilityRepository.delete(tutorAvailability);
            } else {
                tutorAvailabilityRepository.saveAndFlush(tutorAvailability);
            }
        }

        return ResponseEntity.ok().build();
    }

    private void deleteAvailabilityBlock(TutorAvailability tutorAvailability, HoursBlock blockToDelete) {
        List<HoursBlock> hoursBlockList = tutorAvailability.getHoursBlockList();
        List<HoursBlock> newHoursBlockList = new ArrayList<>();

        for (HoursBlock block : hoursBlockList) {
            List<HoursBlock> adjustedBlocks = subtractBlock(block, blockToDelete);
            newHoursBlockList.addAll(adjustedBlocks);
        }

        tutorAvailability.setHoursBlockList(newHoursBlockList);
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
    public ResponseEntity<GetMonthAvailabilityResponse> getMonthAvailability(DayRequest getAvailabilityRequest) {
        String userMail = getCurrentUserMailFromContext();
        if (userMail == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (getAvailabilityRequest == null )
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

        // GET TUTOR AVAILABILITY
        List<TutorAvailability> tutorAvailability = getTutorAvailabilityByMonth(userMail, getAvailabilityRequest.getDay());
        if (tutorAvailability == null)
            return ResponseEntity.ok(new GetMonthAvailabilityResponse());

        // GET TUTOR APPOINTMENTS
        TutorProfile tutorProfile = tutorProfileService.getTutorProfileByTutorMail(userMail);
        List<Appointment> appointments = appointmentManagementService.getTutorAppointmentsByTutorProfileAndMonth(getAvailabilityRequest.getDay(), tutorProfile);

        // Analyze availability and appointments to create non-overlapping blocks
        GetMonthAvailabilityResponse response = processAvailabilityAndAppointments(tutorAvailability, appointments);

        return ResponseEntity.ok(response);
    }

    private GetMonthAvailabilityResponse processAvailabilityAndAppointments(List<TutorAvailability> tutorAvailability, List<Appointment> appointments) {

        // Map to hold appointments grouped by day
        Map<LocalDate, List<HoursBlock>> appointmentBlocksByDay = new HashMap<>();

        for (Appointment appointment : appointments) {
            LocalDate day = appointment.getDay();
            HoursBlock block = new HoursBlock(appointment.getStartDate(), appointment.getEndDate());
            appointmentBlocksByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(block);
        }

        // Map to hold availability grouped by day
        Map<LocalDate, List<HoursBlock>> availabilityBlocksByDay = new HashMap<>();

        for (TutorAvailability availability : tutorAvailability) {
            LocalDate day = availability.getDay();
            List<HoursBlock> blocks = availability.getHoursBlockList();
            availabilityBlocksByDay.computeIfAbsent(day, k -> new ArrayList<>()).addAll(blocks);
        }

        // Set of all days in the month with either availability or appointments
        Set<LocalDate> days = new HashSet<>();
        days.addAll(availabilityBlocksByDay.keySet());
        days.addAll(appointmentBlocksByDay.keySet());

        List<GetAvailabilityResponse> appointmentsList = new ArrayList<>();
        List<GetAvailabilityResponse> availabilityList = new ArrayList<>();

        for (LocalDate day : days) {
            // Get appointment blocks for the day
            List<HoursBlock> appointmentBlocks = appointmentBlocksByDay.getOrDefault(day, new ArrayList<>());

            // Create appointments list
            if (!appointmentBlocks.isEmpty()) {
                GetAvailabilityResponse appointmentResponse = new GetAvailabilityResponse();
                appointmentResponse.setDay(day);
                appointmentResponse.setHoursBlockList(appointmentBlocks);
                appointmentsList.add(appointmentResponse);
            }

            // Get availability blocks for the day
            List<HoursBlock> availabilityBlocks = availabilityBlocksByDay.getOrDefault(day, new ArrayList<>());

            // Subtract appointments from availability
            List<HoursBlock> adjustedAvailabilityBlocks = subtractHoursBlocks(availabilityBlocks, appointmentBlocks);

            if (!adjustedAvailabilityBlocks.isEmpty()) {
                GetAvailabilityResponse availabilityResponse = new GetAvailabilityResponse();
                availabilityResponse.setDay(day);
                availabilityResponse.setHoursBlockList(adjustedAvailabilityBlocks);
                availabilityList.add(availabilityResponse);
            }
        }

        // Create the response object
        GetMonthAvailabilityResponse response = new GetMonthAvailabilityResponse();
        response.setAppointmentsList(appointmentsList);
        response.setAvailabilityList(availabilityList);

        return response;
    }

    private List<HoursBlock> subtractHoursBlocks(List<HoursBlock> availabilityBlocks, List<HoursBlock> appointmentBlocks) {
        List<HoursBlock> result = new ArrayList<>(availabilityBlocks);

        for (HoursBlock appointmentBlock : appointmentBlocks) {
            List<HoursBlock> tempResult = new ArrayList<>();
            for (HoursBlock availabilityBlock : result) {
                tempResult.addAll(subtractBlock(availabilityBlock, appointmentBlock));
            }
            result = tempResult;
        }

        return result;
    }

    private List<HoursBlock> subtractBlock(HoursBlock existingBlock, HoursBlock blockToSubtract) {
        List<HoursBlock> result = new ArrayList<>();

        Timestamp existingStart = existingBlock.getStartTime();
        Timestamp existingEnd = existingBlock.getEndTime();
        Timestamp deleteStart = blockToSubtract.getStartTime();
        Timestamp deleteEnd = blockToSubtract.getEndTime();

        // No overlap
        if (existingEnd.compareTo(deleteStart) <= 0 || existingStart.compareTo(deleteEnd) >= 0) {
            result.add(existingBlock);
            return result;
        }

        // Overlap exists
        if (existingStart.compareTo(deleteStart) < 0) {
            result.add(new HoursBlock(existingStart, deleteStart));
        }
        if (existingEnd.compareTo(deleteEnd) > 0) {
            result.add(new HoursBlock(deleteEnd, existingEnd));
        }

        return result;
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
        tutorAvailability = getDailyAvailabilityExcludingMeetings(tutorAvailability,
                appointmentRepository.findAppointmentsByTutorProfileAndDay(tutorProfile, getAvailabilityRequest.getDay()));

        if (tutorAvailability == null)
            return ResponseEntity.ok(new GetAvailabilityResponse(getAvailabilityRequest.getDay(), new ArrayList<>()));

        tutorAvailability.getHoursBlockList().sort(Comparator.comparing(HoursBlock::getStartTime));

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