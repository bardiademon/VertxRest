import com.bardiademon.controller.adapter.*;
import com.bardiademon.data.dto.SignInDto;
import com.bardiademon.data.enums.DateTimeFormatPattern;
import com.bardiademon.data.enums.UserRole;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

public class ResponseInfoTest {

    public static void main(String[] args) {

        final var info = new LinkedHashMap<>();

        info.put("1" , 1);
        info.put("2" , 1.5D);
        info.put("role" , UserRole.USER);
        info.put("localDateTime" , LocalDateTime.now());
        info.put("date" , new Date());
        info.put("time" , new Time(System.nanoTime()));
        info.put("jsonObject" , new JsonObject().put("test" , 1));
        info.put("jsonObject2" , new JsonObject().put("name" , "bardiademon").put("info" , new JsonArray().add(12).add(null)).put("address" , new JsonObject().put("1" , "iran")));
        info.put("clazz" , new SignInDto("bardiademon@gmail.com" , "123456"));
        info.put("json_array_clazz" , new JsonArray().add(new SignInDto("bardiademon@gmail.com" , "123456")));
        info.put("jsonArray" , new JsonArray().add(1));
        info.put("json_object_clazz" , new JsonObject().put("clazz" , new SignInDto("bardiademon@gmail.com" , "123456")));
        info.put("set" , Set.of("1" , 1 , "bardiademon"));
        info.put("stream" , Stream.of("1" , 1 , "bardiademon" , null , new SignInDto("bardiademon@gmail.com" , "123456")).toList());
        info.put("list" , List.of("1" , 1 , "bardiademon"));
        info.put("map" , new TestResponse<>(info).info());
        info.put("null" , null);
        info.put("bytes" , new byte[]{1 , 2 , 5 , 6 , 3 , 6});

        System.out.println("LinkedHashMap = " + new TestResponse<>(info).info());
        System.out.println("SignInDto = " + new TestResponse<>(new SignInDto("bardiademon@gmail.com" , "123456")).info());
        System.out.println("JsonObject = " + new TestResponse<>(new JsonObject().put("name" , "bardiademon").put("info" , new JsonArray().add(12).add(null)).put("address" , new JsonObject().put("1" , "iran"))).info());
        System.out.println("JsonArray SignInDto = " + new TestResponse<>(new JsonArray().add(new SignInDto("bardiademon@gmail.com" , "123456"))).info());
        System.out.println("role = " + new TestResponse<>(UserRole.USER).info());
        System.out.println("null = " + new TestResponse<>(null).info());
        System.out.println("Date = " + new TestResponse<>(new Date()).info());
        System.out.println("Time = " + new TestResponse<>(new Time(System.nanoTime())).info());
        System.out.println("LocalDateTime = " + new TestResponse<>(LocalDateTime.now()).info());
        System.out.println("LocalDate = " + new TestResponse<>(LocalDate.now()).info());
        System.out.println("LocalTime = " + new TestResponse<>(LocalTime.now()).info());
        System.out.println("byte = " + new TestResponse<>(new byte[]{1 , 2 , 5 , 6 , 3 , 6}).info());

    }

    private static final class TestResponse<T> {
        private final T info;

        private static final Gson GSON;

        static {
            final GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls()
                    .registerTypeAdapter(LocalDateTime.class , new LocalDateTimeGsonAdapter())
                    .registerTypeAdapter(JsonObject.class , new JsonObjectGsonAdapter())
                    .registerTypeAdapter(JsonArray.class , new JsonArrayGsonAdapter())
                    .registerTypeAdapter(Time.class , new TimeGsonAdapter())
                    .registerTypeAdapter(LocalDate.class , new LocalDateGsonAdapter());
            GSON = gsonBuilder.create();
        }

        public TestResponse(final T info) {
            this.info = info;
        }

        public Object info() {
            try {

                if (info == null) {
                    return null;
                }

                if (info instanceof String || info instanceof Short || info instanceof Integer || info instanceof Long || info instanceof Float || info instanceof Double || info instanceof Byte) {
                    return info;
                }

                if (info instanceof Enum<?>) {
                    return info;
                }

                if (info instanceof final LocalDateTime localDateTime) {
                    return localDateTime.format(DateTimeFormatPattern.DATE_TIME_PATTERN.pattern);
                }
                if (info instanceof final LocalDate localDate) {
                    return DateTimeFormatPattern.DATE_PATTERN.pattern.format(localDate);
                }
                if (info instanceof final LocalTime localTime) {
                    return localTime.format(DateTimeFormatPattern.TIME_PATTERN.pattern);
                }
                if (info instanceof final Time time) {
                    return time.toLocalTime().format(DateTimeFormatPattern.TIME_PATTERN.pattern);
                }

                final String jsonStr = GSON.toJson(info);
                try {
                    return new JsonObject(jsonStr);
                } catch (Exception e1) {
                    try {
                        return new JsonArray(jsonStr);
                    } catch (Exception e2) {
                    }
                }
            } catch (Exception e) {
            }

            return info;
        }
    }

}
