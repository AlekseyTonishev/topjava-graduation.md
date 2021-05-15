package com.topjava.graduation.to;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class MenuTo extends BaseTo {

    @NotNull
    private LocalDate date;




    public MenuTo(Integer id, LocalDate date) {
        super(id);
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MenuTo{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
}