package com.baowen;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
//import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumDeserializationSchema;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Properties;

/**
 * @author mangguodong
 * @create 2021-09-26
 */
public class flinktest {

    public static void main(String[] args) throws Exception {

        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Properties properties = new Properties();
        //properties.setProperty("scan.startup.mode", "schema_only");
       // properties.setProperty("snapshot.mode", "schema_only");
//        properties.setProperty("scan.startup.mode", "initial");
       // properties.setProperty("scan.startup.mode", "latest-offset");
        DebeziumSourceFunction<String> sourceFunction = MySQLSource.<String>builder()
                .hostname("hadoop102")
                .port(3306)
                .username("root")
                .password("123456")
                .databaseList("gmall-flink0821")
                //.tableList("gmall-flink0821.z_user_info")
                .deserializer(new CustomerDeserialization())
                // .deserializer(new StringDebeziumDeserializationSchema())
               // .deserializer(new MySchema())
                .debeziumProperties(properties)
               // .startupOptions(StartupOptions.latest())
                .build();
        //DataStreamSource<String> streamSource = env.addSource(sourceFunction);

        DataStreamSource<String> StringDS = env.addSource(sourceFunction);

        StringDS.print();

        env.execute();
    }


    public static class MySchema implements DebeziumDeserializationSchema<String> {

        //反序列化方法
        @Override
        public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {

            //库名&表名
            String topic = sourceRecord.topic();
            String[] split = topic.split("\\.");
            String db = split[1];
            String table = split[2];

            //获取数据
            Struct value = (Struct) sourceRecord.value();
            Struct after = value.getStruct("after");
            JSONObject data = new JSONObject();
            if (after != null) {
                Schema schema = after.schema();
                for (Field field : schema.fields()) {
                    data.put(field.name(), after.get(field.name()));
                }
            }

            //获取操作类型
            Envelope.Operation operation = Envelope.operationFor(sourceRecord);

            //创建JSON用于存放最终的结果
            JSONObject result = new JSONObject();
            result.put("database", db);
            result.put("table", table);
            result.put("type", operation.toString().toLowerCase());
            result.put("data", data);

            collector.collect(result.toJSONString());
        }


        //定义数据类型
        @Override
        public TypeInformation<String> getProducedType() {
            return TypeInformation.of(String.class);
        }
    }
}
