package com.automationanywhere.commands.GoogleMaps;

import Utils.HTTPRequest;
import com.automationanywhere.botcommand.data.Value;
import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

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

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Geocode", label = "Geocode",
        node_label = "Return Latitude and Longitude Coordinates for address: {{address}} in session {{sessionName}}", description = "Returns dictionary variable with coordinates results", icon = "MAPS.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7",
        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "Assign output to a dictionary variable", return_type = DataType.DICTIONARY, return_required = true, return_description = "Returns Lat/Long cooordinates at dictionary keys 'Latitude' and 'Longitude'")

public class Geocode {
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
            @Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Address") @NotEmpty String address
    ) throws IOException, ParseException {

        if ("".equals(address.trim())) {
            throw new BotCommandException(MESSAGES.getString("emptyInputString", "address"));
        }
        //Retrieve APIKey String that is passed as Session Object
        String APIKey = (String) this.sessions.get(sessionName);
        //Create Map for Dictionary Output
        Map<String, Value> ResMap = new LinkedHashMap();
        /*address = address.replace("+", "%2B");
        address = address.replace(" ", "+");*/
        address = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+address+"&key="+APIKey;
        String response = HTTPRequest.GET(url);

        //Parse JSON to get JSON array
        Object obj = new JSONParser().parse(response);
        JSONObject jsonObj = (JSONObject) obj;

        JSONArray results = (JSONArray) jsonObj.get("results");
        JSONObject resultsElement = (JSONObject) results.get(0);
        JSONObject geo = (JSONObject) resultsElement.get("geometry");
        JSONObject location = (JSONObject) geo.get("location");
        ResMap.put("Latitude", new StringValue(location.get("lat").toString()));
        ResMap.put("Longitude", new StringValue(location.get("lng").toString()));

        return new DictionaryValue(ResMap);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
}
