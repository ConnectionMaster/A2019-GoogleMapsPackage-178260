package com.automationanywhere.commands.GoogleMaps;
import Utils.HTTPRequest;
import com.automationanywhere.botcommand.data.Value;
import static com.automationanywhere.commandsdk.model.AttributeType.*;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
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
        name = "Search Places", label = "Search Places",
        node_label = "Search Places from Google Maps session {{sessionName}}", description = "Returns table variable with list of places", icon = "MAPS.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7",
        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "Assign output to a table variable", return_type = DataType.TABLE, return_required = true)

public class SearchPlaces {

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
    public TableValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default") @NotEmpty String sessionName,
            @Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Search Terms", description = "e.g. CN Tower, Toronto, ON") @NotEmpty String search
            ) throws IOException, ParseException {

        //Internal Checks on inputs
        if ("".equals(search.trim())) { throw new BotCommandException(MESSAGES.getString("emptyInputString", "search"));}


        //Retrieve APIKey String that is passed as Session Object
        String APIKey = (String) this.sessions.get(sessionName);
        //modify input string for URL encoded
        search = URLEncoder.encode(search, StandardCharsets.UTF_8);

        String URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="+search+"&inputtype=textquery&fields=formatted_address,name,rating,opening_hours&key="+APIKey;
        //Make API Call
        String response = HTTPRequest.GET(URL);
        //Create Table for output
        Table table = new Table();
        //Create List of type "Row" objects - this will be used to store the list of Row objects
        List<Row> tableRows = new ArrayList<Row>();
        //Parse JSON to get JSON array
        Object obj = new JSONParser().parse(response);
        JSONObject jsonObj = (JSONObject) obj;
        //Get JSON Array that occurs at key "candidates"
        JSONArray candidatesArray = (JSONArray)jsonObj.get("candidates");
        List<Value> headerValues = new ArrayList<>();
        headerValues.add(new StringValue("Name"));
        headerValues.add(new StringValue("Address"));
        headerValues.add(new StringValue("Open Now"));
        headerValues.add(new StringValue("Rating"));
        //Add headers to Row object, then Row object to List of Row objects
        Row headerRow = new Row();
        //Set header row values with the List of StringValues in rowValues
        headerRow.setValues(headerValues);
        //add Row object headerRow to the List of type Row
        tableRows.add(0,headerRow);

        //Create List of Lists of type Value

        for (int i=0; i<candidatesArray.size(); i++){
            List<Value> rowValues = new ArrayList<>();
            JSONObject place = (JSONObject) candidatesArray.get(i);
            rowValues.add(new StringValue(place.get("name").toString()));
            rowValues.add(new StringValue(place.get("formatted_address").toString()));

            JSONObject openingHours = (JSONObject) place.get("opening_hours");
            rowValues.add(new StringValue(openingHours.get("open_now").toString()));

            rowValues.add(new StringValue(place.get("rating").toString()));

            Row currentRow = new Row(rowValues);
            tableRows.add(i+1, currentRow);
            table.setRows(tableRows);
        }

        return new TableValue(table);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
}
