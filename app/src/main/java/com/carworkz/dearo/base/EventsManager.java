package com.carworkz.dearo.base;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Farhan on 20/12/2016.
 *
 * Abstraction to manage [{@link EventBus}].
 *
 * Clients should implement [{@link EventSubscriber}] to subscribe/unsubscribe for events.
 *
 *
 * Note: Do Not Forget to unregister to avoid memory leaks.
 */
public class EventsManager {
    private static final EventBus eventBus;

    static {
        eventBus = EventBus.getDefault();
    }

    public interface EventSubscriber {

    }

    public static List<EventSubscriber> getDefaultGlobalSubscribers() {
        List<EventSubscriber> subscribers = new ArrayList<>();
//        subscribers.addAll(Arrays.asList(
//                //Add all Global subscribers here
//
//        ));
        return subscribers;
    }

    public static void register(EventSubscriber subscriber) {
        if (subscriber != null && !isRegistered(subscriber)) {
            eventBus.register(subscriber);
        }
    }

    public static void register(List<EventSubscriber> subscriberList) {
        for (EventSubscriber subscriber : subscriberList) {
            register(subscriber);
        }
    }

    public static void unregister(EventSubscriber subscriber) {
        if (subscriber != null && isRegistered(subscriber)) {
            eventBus.unregister(subscriber);
        }
    }

    public static void unregister(List<EventSubscriber> subscriberList) {
        for (EventSubscriber subscriber : subscriberList) {
            unregister(subscriber);
        }
    }

    public static void post(Object event) {
        if (event != null) {
            eventBus.post(event);
            Timber.d("EVENT", event.getClass().getSimpleName());
        }
    }

    public static void postSticky(Object stickyEvent) {
        if (stickyEvent != null) {
            eventBus.post(stickyEvent);
            Timber.d("STICKY EVENT", stickyEvent.getClass().getSimpleName());
        }
    }

    public static void removeSticky(Object stickyEvent) {
        if (stickyEvent != null) {
            eventBus.removeStickyEvent(stickyEvent);
        }
    }

    private static boolean isRegistered(EventSubscriber subscriber) {
        return eventBus.isRegistered(subscriber);
    }

    public static void cancel(Object event) {
        if (event != null) {
            eventBus.cancelEventDelivery(event);
        }
    }
}

