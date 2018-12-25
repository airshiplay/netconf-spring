package com.airlenet.netconf.model;

import com.airlenet.data.jpa.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table
public class DeviceModel extends BaseEntity<Long> {
    @Id
    private Long id;
    private String name;
    private String ip;
    private int port;
    private String user;
    private String pass;
}
