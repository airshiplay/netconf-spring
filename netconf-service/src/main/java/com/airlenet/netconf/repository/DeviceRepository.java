package com.airlenet.netconf.repository;

import com.airlenet.data.jpa.EntityRepository;
import com.airlenet.netconf.model.DeviceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.List;

@Repository
public interface DeviceRepository extends EntityRepository<DeviceModel,Long> {

}
