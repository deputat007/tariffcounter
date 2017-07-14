package com.thehatefulsix.tariffcounter.models.core;


public interface IModelWithID<T> {

    T getId();

    void setId(T id);
}
