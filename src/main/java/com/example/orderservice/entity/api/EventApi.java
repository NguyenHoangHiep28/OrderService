package com.example.orderservice.entity.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@CrossOrigin("*")
public class EventApi {
//    public static List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    public static Map<String, SseEmitter> emitterMap = new HashMap<>();
    @RequestMapping(method = RequestMethod.GET,path = "/subcribe")
    public SseEmitter subscribe(@RequestParam(name = "clientId") String id) {
        emitterMap.remove(id);
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        sseEmitter.onCompletion(() -> emitterMap.remove(emitterMap));
        emitterMap.put(id, sseEmitter);
        return sseEmitter;
    }
//    @RequestMapping(method = RequestMethod.GET, path = "send")
    public static void dis(String id, String message) {
        System.out.println(emitterMap.size());
        SseEmitter sseEmitter = emitterMap.get(id);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name("orderStatus").data(message));
            } catch (IOException exception) {
                emitterMap.remove(id);
            }
        }
    }
    @RequestMapping(method = RequestMethod.GET, path = "send")
    public static void dis(@RequestParam(name = "id") String id) {
        SseEmitter sseEmitter = emitterMap.get(id);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name("orderStatus").data("test message"));
            } catch (IOException exception) {
                emitterMap.remove(id);
            }
        }
    }
}
