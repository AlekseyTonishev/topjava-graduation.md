package com.topjava.graduation.util;


import com.topjava.graduation.HasId;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import com.topjava.graduation.util.exception.ExpiredDateTimeException;
import com.topjava.graduation.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ValidationUtil {
    public static final LocalTime EXPIRED_TIME = LocalTime.of(11, 0);

    private ValidationUtil() {
    }

    public static void checkExpiredDateWithTime(LocalDate date, int menuId) {
        boolean expired = LocalTime.now().isAfter(EXPIRED_TIME);
        checkExpiredDate(date, menuId);
        if (expired) {
            throw new ExpiredDateTimeException("Expired time voting for menu with id=" + menuId);
        }
    }

    public static void checkExpiredDate(LocalDate date, int menuId) {
        LocalDate today = LocalDate.now();
        if (!date.equals(today)) {
            throw new ExpiredDateTimeException("Expired date voting for menu with id=" + menuId);
        }
    }

    public static <T> T checkNotFoundWithId(T object, int id) {
        return checkNotFound(object, "id=" + id);
    }

    public static void checkNotFoundWithId(boolean found, int id) {
        checkNotFound(found, "id=" + id);
    }

    public static <T> T checkNotFound(T object, String msg) {
        checkNotFound(object != null, msg);
        return object;
    }

    public static void checkNotFound(boolean found, String msg) {
        if (!found) {
            throw new NotFoundException("Not found entity with " + msg);
        }
    }

    public static void checkNew(HasId entity) {
        if (!entity.isNew()) {
            throw new IllegalArgumentException(entity + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(HasId entity, int id) {
        if (entity.isNew()) {
            entity.setId(id);
        } else if (entity.getId() != id) {
            throw new IllegalArgumentException(entity + " must be with id=" + id);
        }
    }

    public static Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

    public static String[] getCauseMessage(Throwable throwable) {
        List<String> fieldErrors = new ArrayList<>();
        if (throwable instanceof BindException) {
            BindException exception = (BindException) throwable;
            List<ObjectError> errors = exception.getBindingResult().getAllErrors();
            errors.forEach(e -> fieldErrors.add(((FieldError) e).getField() + ": " + e.getDefaultMessage()));
        }
        return fieldErrors.toArray(String[]::new);
    }
}