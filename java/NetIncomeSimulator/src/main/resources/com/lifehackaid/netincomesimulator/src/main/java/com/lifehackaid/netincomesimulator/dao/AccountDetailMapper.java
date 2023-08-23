package com.lifehackaid.netincomesimulator.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lifehackaid.netincomesimulator.entity.AccountDetailEntity;

@Mapper
public interface AccountDetailMapper {

    void insertInBulk(List<AccountDetailEntity> accountDetailEntityList);

    List<AccountDetailEntity> findAllById(String Id);

    void delete(String Id);
}