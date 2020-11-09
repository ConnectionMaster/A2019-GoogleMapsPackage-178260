package com.automationanywhere.commands.GoogleMaps;

import Utils.HTTPRequest;
import static com.automationanywhere.commandsdk.model.AttributeType.*;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.simple.parser.*;

import static com.automationanywhere.commandsdk.model.DataType.STRING;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Get Map Image", label = "Get Map Image",
        node_label = "Retrieve image of map for {{center}} in session {{sessionName}}", description = "Returns image of map based on search criteria", icon = "MAPS.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7")

public class GetMapImage {
    @Sessions
    private Map<String, Object> sessions;

    private static final Messages MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.demo.messages");

    @com.automationanywhere.commandsdk.annotations.GlobalSessionContext
    private com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }

    //Identify the entry point for the action.
    @Execute
    public void action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default") @NotEmpty String sessionName,
            @Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Center (location) of the map", description = "e.g. Brooklyn Bridge, New York, NY") @NotEmpty String center,
            @Idx(index = "3", type = TEXT) @Pkg(label = "Zoom level", description="e.g. Zoom factor of 1 (World map) to 20 (streets/buildings)") @NotEmpty String zoom,
            @Idx(index = "4", type = RADIO, options = {
                    @Idx.Option(index ="4.1", pkg = @Pkg(label = "Roadmap", value = "roadmap")),
                    @Idx.Option(index ="4.2", pkg = @Pkg(label = "Satellite", value = "satellite")),
                    @Idx.Option(index ="4.3", pkg = @Pkg(label = "Terrain", value = "terrain")),
                    @Idx.Option(index ="4.4", pkg = @Pkg(label = "Hybrid", value = "hybrid"))
            }) @Pkg(label = "Type") String type,
            @Idx(index = "5", type = TEXT) @Pkg(label = "Horizontal pixel size", description = "e.g. 600") @NotEmpty String horizontal,
            @Idx(index = "6", type = TEXT) @Pkg(label = "Horizontal pixel size", description = "e.g. 400") @NotEmpty String vertical,
            @Idx(index = "7", type = AttributeType.FILE) @Pkg(label = "File Path for .png output", description = "e.g. *.png") @NotEmpty String filePath
    ) throws IOException, ParseException {

        if ("".equals(center.trim())) { throw new BotCommandException(MESSAGES.getString("emptyInputString", "center"));}
        if ("".equals(zoom.trim())) { throw new BotCommandException(MESSAGES.getString("emptyInputString", "zoom"));}
        if ("".equals(horizontal.trim())) { throw new BotCommandException(MESSAGES.getString("emptyInputString", "horizontal"));}
        if ("".equals(vertical.trim())) { throw new BotCommandException(MESSAGES.getString("emptyInputString", "vertical"));}
        if ("".equals(filePath.trim())) { throw new BotCommandException(MESSAGES.getString("emptyInputString", "file path"));}

        //Retrieve APIKey String that is passed as Session Object
        String APIKey = (String) this.sessions.get(sessionName);
        center = URLEncoder.encode(center, StandardCharsets.UTF_8);
        String url = "https://maps.googleapis.com/maps/api/staticmap?center="+center+"&zoom="+zoom+"&size="+horizontal+"x"+vertical+"&maptype="+type+"&key="+APIKey;
        HTTPRequest.GETpng(url, filePath);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
}
