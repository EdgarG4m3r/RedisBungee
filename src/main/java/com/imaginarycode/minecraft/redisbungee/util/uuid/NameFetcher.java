package com.imaginarycode.minecraft.redisbungee.util.uuid;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.util.closer.Closer;
import dev.luckynetwork.alviann.luckyinjector.lib.google.gson.reflect.TypeToken;
import dev.luckynetwork.alviann.luckyinjector.lib.okhttp3.OkHttpClient;
import dev.luckynetwork.alviann.luckyinjector.lib.okhttp3.Request;
import dev.luckynetwork.alviann.luckyinjector.lib.okhttp3.ResponseBody;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NameFetcher {
    @Setter
    private static OkHttpClient httpClient;

    public static List<String> nameHistoryFromUuid(UUID uuid) throws IOException {
        String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";

        try (Closer closer = new Closer()) {
            Request request = new Request.Builder().url(url).get().build();
            ResponseBody body = closer.add(httpClient.newCall(request).execute().body());
            String response = body.string();

            Type listType = new TypeToken<List<Name>>() {
            }.getType();
            List<Name> names = RedisBungee.getGson().fromJson(response, listType);

            List<String> humanNames = new ArrayList<>();
            for (Name name : names)
                humanNames.add(name.name);

            return humanNames;
        }

    }

    public static class Name {
        private String name;
        private long changedToAt;
    }

}