package com.raycast.service.base;

import com.raycast.domain.Message;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import java.util.List;

/**
 * Created by Lucas on 08/10/2014.
 */
@Rest(rootUrl = "http://api.raycastapp.com", converters = {MappingJackson2HttpMessageConverter.class })
@Accept(MediaType.APPLICATION_JSON)
public interface RaycastRESTClient {
    @Get("/message?latitude={latitude}&longitude={longitude}&radius={radius}")
    List<Message> getMessages(double latitude, double longitude, double radius);
}
