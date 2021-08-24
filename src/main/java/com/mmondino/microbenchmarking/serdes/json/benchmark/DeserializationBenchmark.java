package com.mmondino.microbenchmarking.serdes.json.benchmark;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
public class DeserializationBenchmark {

    private static final Gson gson = new Gson();
    private static final Moshi moshi = new Moshi.Builder().build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Benchmark
    public List<User> benchmarkDeserializationWithFastjson(DeserializationDataProvider deserializationDataProvider) throws IOException {
        return JSON.parseArray(deserializationDataProvider.usersAsJson, User.class);
    }

    @Benchmark
    public List<User> benchmarkDeserializationWithGSON(DeserializationDataProvider deserializationDataProvider) {
        return this.gson.fromJson(deserializationDataProvider.usersAsJson, new TypeToken<ArrayList<User>>(){}.getType());
    }

    @Benchmark
    public List<User> benchmarkDeserializationWithJackson(DeserializationDataProvider deserializationDataProvider) throws IOException {
        return this.objectMapper.readValue(deserializationDataProvider.usersAsJson, new TypeReference<Object>(){});
    }

    @Benchmark
    public List<User> benchmarkDeserializationWithMoshi(DeserializationDataProvider deserializationDataProvider) throws IOException {

        Type type = Types.newParameterizedType(List.class, User.class);
        JsonAdapter<List<User>> jsonAdapter = this.moshi.adapter(type);

        return jsonAdapter.fromJson(deserializationDataProvider.usersAsJson);
    }

    @State(Scope.Thread)
    public static class DeserializationDataProvider {

        @Param({"10", "100", "1000"})
        private int count;

        private String usersAsJson;

        @Setup(Level.Trial)
        public void setup() {

            List users = new ArrayList();

            for (int i = 0; i < this.count; i++) {
                users.add(new User("Given Name " + i, "Family Name " + i));
            }

            this.usersAsJson = JSON.toJSONString(users);
        }
    }
}
