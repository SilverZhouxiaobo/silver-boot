package cn.silver.framework.file.model;

import lombok.Data;

import java.util.List;

@Data
public class EventExportModel {
    private static final long serialVersionUID = 1L;
    private String eventName;
    private String eventDate;
    private String eventWeek;
    private String address;
    private List<EventAgentModel> agents;
    private List<EventAttendModel> attends;
    private List<EventRecordModel> records;
    private List<EventContentModel> contents;
}

@Data
class EventAgentModel {
    private String orgName;
    private String name;
}

@Data
class EventAttendModel {
    private String companyName;
    private String companyDomicile;
    private String companyType;
    private String organ;
    private String name;
    private String title;
}

@Data
class EventRecordModel {
    private String beginTime;
    private String endTime;
    private String address;
    private String content;
}

@Data
class EventContentModel {
    private String content;
    private String answer;
}