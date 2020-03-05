package com.hibernatus.hibmobtech.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by tgo on 25/10/15.
 * Allow to de-serialize only the data field in the Json response.
 */
public class ItemTypeAdapterFactory implements TypeAdapterFactory {
    public static final String TAG = ItemTypeAdapterFactory.class.getSimpleName();

    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        //Log.e(TAG, "TypeAdapter: create");

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {

            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            public T read(JsonReader in) throws IOException {

                JsonElement jsonElement = elementAdapter.read(in);
                if (jsonElement.isJsonObject()) {
                    //Log.e(TAG, "TypeAdapter");

                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("data") && jsonObject.get("data").isJsonObject())
                    {
                        //Log.e(TAG, "TypeAdapter: jsonObject=" + jsonObject);

                        jsonElement = jsonObject.get("data");
                    }
                }

                return delegate.fromJsonTree(jsonElement);
            }
        }.nullSafe();
    }
}