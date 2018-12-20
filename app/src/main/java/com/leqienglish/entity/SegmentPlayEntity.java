package com.leqienglish.entity;


import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.english.Segment;

public class SegmentPlayEntity {
    private Integer startTime;
    private Integer endTime;
    private String filePath;
    private String segmentId;


    public static SegmentPlayEntity toSegmentPlayEntity(Segment segment) {
        if (segment == null) {
            return null;
        }
        SegmentPlayEntity segmentPlayEntity = new SegmentPlayEntity();

        segmentPlayEntity.setFilePath(segment.getAudioPath());
        segmentPlayEntity.setSegmentId(segment.getId());

        String[] sentences = segment.getContent().split(Consistent.SLIP_SENTENCE);
        if (sentences == null || sentences.length == 0) {
            return null;
        }
        String firstSentence = sentences[0];
        String lastSentence = sentences[sentences.length - 1];

        String[] startTimeArr = firstSentence.split(Consistent.SLIP_START_AND_END);
        if (startTimeArr == null || startTimeArr.length == 0) {
            return null;
        }

        String startTime = startTimeArr[0];
        segmentPlayEntity.setStartTime(Integer.valueOf(startTime));

        String[] lastTimePartArr = lastSentence.split(Consistent.SLIP_TIME_AND_TEXT);
        if (lastTimePartArr == null || lastTimePartArr.length == 0) {
            return null;
        }

        String lastTimePart = lastTimePartArr[0];

        String[] endTiemStrArr = lastTimePart.split(Consistent.SLIP_START_AND_END);
        if (endTiemStrArr == null || endTiemStrArr.length == 0) {
            return null;
        }
        String endTiemStr = endTiemStrArr[1];
        segmentPlayEntity.setEndTime(Integer.valueOf(endTiemStr));

        return segmentPlayEntity;

    }


    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }
}
