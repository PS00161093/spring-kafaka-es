package com.ps.app.kafkatoes.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;
import java.util.List;

public interface KafkaConsumer<K extends Serializable, V extends SpecificRecordBase> {

    void received(List<V> messages, List<Integer> key, List<Integer> partitions, List<Long> offSet);
}
