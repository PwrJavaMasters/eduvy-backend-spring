package com.eduvy.tutoring.model.utils;

public enum Subject {

    MATHS, ENGLISH, POLISH, PHYSICS, BIOLOGY, CHEMISTRY;

    public static Subject fromString(String subjectStr) {
        if (subjectStr == null || subjectStr.isEmpty()) {
            return null;
        }
        try {
            return Subject.valueOf(subjectStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}