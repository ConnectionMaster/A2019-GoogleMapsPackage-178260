package com.automationanywhere.commands.GoogleMaps;
import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import Utils.HTTPRequest;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import com.automationanywhere.core.security.SecureString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

@BotCommand

@CommandPkg(
        name = "Start Session", label = "Start Session",
        node_label = "Start Session {{sessionName}}", description = "Enter API Key to authenticate with Google API", icon = "MAPS.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7"
        )

public class StartSession {

    @Sessions
    private Map<String, Object> sessions;
    @com.automationanywhere.commandsdk.annotations.GlobalSessionContext
    private com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }


    private static final Messages MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.demo.messages");

    @Execute
    public void action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name",  default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName,
            @Idx(index = "2", type = CREDENTIAL) @Pkg(label = "API Key") @NotEmpty SecureString APIKeyInput

    ) throws IOException, ParseException {

        String apiKey="";

        if (this.sessions.containsKey(sessionName)){
            throw new BotCommandException(MESSAGES.getString("Session name in use"));
        }

        apiKey = APIKeyInput.getInsecureString();

        this.sessions.put(sessionName, apiKey);
    }
    public void setSessions(Map<String, Object> sessions) {this.sessions = sessions;}
}
