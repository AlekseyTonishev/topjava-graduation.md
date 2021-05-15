package com.topjava.graduation.to;


import com.topjava.graduation.HasId;

public abstract class BaseTo implements HasId {
    protected Integer id;



    public BaseTo(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}
