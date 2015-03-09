package com.liujiang.todolist;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/1/11.
 */
public class Agenda implements Serializable {
    private int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParticipator() {
        return participator;
    }

    public void setParticipator(String participator) {
        this.participator = participator;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public int getStart_alarm() {
        return start_alarm;
    }

    public void setStart_alarm(int start_alarm) {
        this.start_alarm = start_alarm;
    }

    public int getEnd_alarm() {
        return end_alarm;
    }

    public void setEnd_alarm(int end_alarm) {
        this.end_alarm = end_alarm;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getSubtask() {
        return subtask;
    }

    public void setSubtask(String subtask) {
        this.subtask = subtask;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getPS() {
        return PS;
    }

    public void setPS(String PS) {
        this.PS = PS;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private String topic;
    private String address;
    private String participator;
    private long start_time;
    private long end_time;
    private int start_alarm;
    private int end_alarm;
    private int repeat;
    private String subtask;
    private String project;
    private String label;
    private int importance;
    private String PS;
    private int status;

    public Agenda(int ID, String topic, String address, String participator, long start_time,
                  long end_time, int start_alarm, int end_alarm, int repeat, String subtask,
                  String project, String label, int importance, String PS, int status) {
        this.ID = ID;
        this.topic = topic;
        this.address = address;
        this.participator = participator;
        this.start_time = start_time;
        this.end_time = end_time;
        this.start_alarm = start_alarm;
        this.end_alarm = end_alarm;
        this.repeat = repeat;
        this.subtask = subtask;
        this.project = project;
        this.label = label;
        this.importance = importance;
        this.PS = PS;
        this.status = status;
    }

    public Agenda() {
    }
}
