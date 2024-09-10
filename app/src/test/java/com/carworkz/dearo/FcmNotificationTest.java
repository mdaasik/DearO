package com.carworkz.dearo;

import com.carworkz.dearo.domain.entities.Notification;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Test;

import java.io.IOException;

import timber.log.Timber;

public class FcmNotificationTest {


    @Test
    public void testAdapter() {
        Moshi moshi = new Moshi.Builder()
                // .add(new JsonStringToMapAdapter())
                .build();

//        String jsonResponse = "{\n" +
//                "   options={\n" +
//                "      \"start_date\":\"2019-03-02T18:30:00.000Z\",\n" +
//                "      \"end_date\":\"2019-03-03T18:30:00.000Z\"\n" +
//                "   },\n" +
//                "   screen=my customers,\n" +
//                "   task=notification_task,\n" +
//                "   category=category_my_customer,\n" +
//                "   title=Service Reminder,\n" +
//                "   version=1.0.0,\n" +
//                "   message=\t\n" +
//                "}";


//
//        String jsonResponse ={
//                "screen":"my customers",
//                "task":"notification_task",
//                "title":"Service Reminder",
//                "version":"1.0.0",
//                " message":"1 upcoming services due on March 3rd 2019"
//};

        String jsonResponse = "{\n" +
                "   options={\n" +
                "      \"start_date\":\"2019-03-02T18:30:00.000Z\",\n" +
                "      \"end_date\":\"2019-03-03T18:30:00.000Z\"\n" +
                "   }\n" +
                "}";

//        ParameterizedType types  = Types.newParameterizedType(Map.class, String.class, String.class);
//        String json = moshi.adapter(types).toJson(jsonResponse);
//        System.out.print( "successful "+ json);

        JsonAdapter<Notification> jsonAdapter = moshi.adapter(Notification.class).lenient();
        try {
            Notification notification = jsonAdapter.fromJson(jsonResponse);
            System.out.print("successful " + notification);
            Timber.d("is parsed" + notification);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
