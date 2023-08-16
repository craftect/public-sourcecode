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

/* DDL
use netincomesimulator;
CREATE TABLE AccountDetail (
  Id varchar(255) NOT NULL,
  category varchar(255) DEFAULT NULL,
  summary varchar(255) DEFAULT NULL,
  unitValue bigint DEFAULT NULL,
  unitAmount bigint DEFAULT NULL,
  keepValue Boolean DEFAULT NULL
) ;

*/
