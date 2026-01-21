package com.courier.application;

import com.courier.domain.Store;

import java.util.List;

public interface StoreRepository {
    List<Store> findAll();
}
