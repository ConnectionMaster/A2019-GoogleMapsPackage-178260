package com.automationanywhere.commands.GoogleMaps;

import Utils.HTTPRequest;
import com.automationanywhere.botcommand.data.Value;
import static com.automationanywhere.commandsdk.model.AttributeType.*;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
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
import com.automationanywhere.commandsdk.model.DataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.util.ArrayList;
import java.util.List;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Distance", label = "Distance",
        node_label = "Search Distance between {{origin}} and {{destination}} with Google Maps session {{sessionName}}", description = "Returns dictionary variable with distance results", icon = "MAPS.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7",
        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "Assign output to a dictionary variable", return_type = DataType.DICTIONARY, return_required = true, return_description = "Dictionary keys are 'Destination', 'Origin', 'Distance', and 'Duration'")

public class Distance {
    @Sessions
    private Map<String, Object> sessions;

    private static final Messages MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.demo.messages");

    @com.automationanywhere.commandsdk.annotations.GlobalSessionContext
    private com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }

    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public DictionaryValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default") @NotEmpty String sessionName,
            @Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Origin", description = "e.g. Dallas, TX") @NotEmpty String origin,
            @Idx(index = "3", type  = TEXT)@Pkg(label = "Destination", description = "e.g. San Francisco, CA") @NotEmpty String destination,
            @Idx(index = "4", type = RADIO, options = {
                    @Idx.Option(index ="4.1", pkg = @Pkg(label = "Imperial", value = "imperial")),
                    @Idx.Option(index ="4.2", pkg = @Pkg(label = "Metric", value = "metric"))
            }) @Pkg(label = "Distance units") String units,
            @Idx(index = "5", type = SELECT, options = {
                    @Idx.Option(index ="5.1", pkg = @Pkg(label = "Driving", value = "driving")),
                    @Idx.Option(index ="5.2", pkg = @Pkg(label = "Walking", value = "walking")),
                    @Idx.Option(index ="5.3", pkg = @Pkg(label = "Bicycling", value = "bicycling")),
                    @Idx.Option(index ="5.4", pkg = @Pkg(label = "Transit", value = "transit"))
            }) @Pkg(label = "Travel mode", default_value_type = STRING, default_value = "driving") String mode
    ) throws IOException, ParseException {

        if ("".equals(origin.trim())) { throw new BotCommandException(MESSAGES.getString("emptyInputString", "origin"));}
        if ("".equals(destination.trim())) { throw new BotCommandException(MESSAGES.getString("emptyInputString", "destination"));}

        //Retrieve APIKey String that is passed as Session Object
        String APIKey = (String) this.sessions.get(sessionName);
        //Create Map for Dictionary Output
        Map<String,Value> ResMap = new LinkedHashMap();

        origin = URLEncoder.encode(origin, StandardCharsets.UTF_8);
        destination = URLEncoder.encode(destination, StandardCharsets.UTF_8);


        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units="+units+"&origins="+origin+"&destinations="+destination+"&mode="+mode+"&key="+APIKey;

        String response = HTTPRequest.GET(url);
        //Parse JSON to get JSON array
        Object obj = new JSONParser().parse(response);
        JSONObject jsonObj = (JSONObject) obj;
        //Add destination address to Dictionary output
        JSONArray destinationAddresses = (JSONArray) jsonObj.get("destination_addresses");
        ResMap.put("Destination", new StringValue(destinationAddresses.get(0).toString()));
        //Add origin address to Dictionary output
        JSONArray originAddresses = (JSONArray) jsonObj.get("origin_addresses");
        ResMap.put("Origin", new StringValue(originAddresses.get(0).toString()));

        //Get distance and travel time values
        JSONArray rows = (JSONArray) jsonObj.get("rows");
        JSONObject rowElement = (JSONObject) rows.get(0);
        JSONArray elements = (JSONArray) rowElement.get("elements");
        JSONObject travelInfo = (JSONObject) elements.get(0);
        JSONObject distanceInfo = (JSONObject) travelInfo.get("distance");
        JSONObject durationInfo = (JSONObject) travelInfo.get("duration");
        ResMap.put("Distance", new StringValue(distanceInfo.get("text").toString()));
        ResMap.put("Duration", new StringValue(durationInfo.get("text").toString()));

        return new DictionaryValue(ResMap);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
}
