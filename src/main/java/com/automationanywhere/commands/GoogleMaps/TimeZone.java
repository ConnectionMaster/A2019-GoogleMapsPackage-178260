package com.automationanywhere.commands.GoogleMaps;
import Utils.*;
import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.STRING;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;
import java.io.IOException;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Time Zone", label = "Time Zone",
        node_label = "Return Time Zone based on Lat/Long coordinates {{latitude}} and {{longitude}} in session {{sessionName}}", description = "Returns string variable with time zone results", icon = "MAPS.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7",
        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "Assign output to a string variable", return_type = STRING, return_required = true, return_description = "Returns the name of the time zone")

public class TimeZone {
    @Sessions
    private Map<String, Object> sessions;

    private static final Messages MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.demo.messages");

    @com.automationanywhere.commandsdk.annotations.GlobalSessionContext
    private com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }
    //Identify the entry point for the action.
    @Idx(index = "2", type = GROUP)
    @Pkg(label = "Coordinates")
    String nameGroup;
    @Execute
    public StringValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default") @NotEmpty String sessionName,

            @Idx(index = "2.1", type = AttributeType.TEXT) @Pkg(label = "Latitude", description = "e.g. 39.6034810") @NotEmpty String latitude,
            @Idx(index = "2.2", type = AttributeType.TEXT) @Pkg(label = "Longitude", description = "e.g. -119.6822510") @NotEmpty String longitude
    ) throws IOException, ParseException {

        if ("".equals(latitude.trim())) {
            throw new BotCommandException(MESSAGES.getString("emptyInputString", "latitude"));
        }
        if ("".equals(longitude.trim())) {
            throw new BotCommandException(MESSAGES.getString("emptyInputString", "longitude"));
        }
        //Retrieve APIKey String that is passed as Session Object
        String APIKey = (String) this.sessions.get(sessionName);
        Long secondsSince1970 = GetCurrentTime.secondsSince1970();
        String seconds = secondsSince1970.toString();
        String url = "https://maps.googleapis.com/maps/api/timezone/json?location="+latitude+","+longitude+"&timestamp="+seconds+"&key="+APIKey;
        String response = HTTPRequest.GET(url);
        //Parse JSON to get JSON array
        Object obj = new JSONParser().parse(response);
        JSONObject jsonObj = (JSONObject) obj;
        String timezone = jsonObj.get("timeZoneName").toString();
        return new StringValue(timezone);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
}

