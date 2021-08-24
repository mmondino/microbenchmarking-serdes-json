package com.mmondino.microbenchmarking.serdes.json.benchmark;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.mmondino.microbenchmarking.serdes.json.model.User;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
public class SerializationBenchmark {

    private static final Gson gson = new Gson();
    private static final Moshi moshi = new Moshi.Builder().build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Benchmark
    public String benchmarkSerializationWithFastjson(SerializationDataProvider serializationDataProvider) throws IOException {
        return JSON.toJSONString(serializationDataProvider.users);
    }

    @Benchmark
    public String benchmarkSerializationWithGSON(SerializationDataProvider serializationDataProvider) {
        return this.gson.toJson(serializationDataProvider.users);
    }

    @Benchmark
    public String benchmarkSerializationWithJackson(SerializationDataProvider serializationDataProvider) throws IOException {
        return this.objectMapper.writeValueAsString(serializationDataProvider.users);
    }

    @Benchmark
    public String benchmarkSerializationWithMoshi(SerializationDataProvider serializationDataProvider) throws IOException {

        Type type = Types.newParameterizedType(List.class, User.class);
        JsonAdapter<List<User>> jsonAdapter = this.moshi.adapter(type);

        return jsonAdapter.toJson(serializationDataProvider.users);
    }

    @State(Scope.Benchmark)
    public static class SerializationDataProvider {

        @Param({"10", "100", "1000"})
        private int count;

        private List<User> users;

        @Setup(Level.Trial)
        public void setup() {

            this.users = new ArrayList();

            for (int i = 0; i < this.count; i++) {
                this.users.add(new User("Given Name " + i, "Family Name " + i));
            }
        }
    }
}
