package entities;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import models.SquareEmail;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GmailService {
    private static final String APPLICATION_NAME = "Fancy Picnics";
    private static final String USER = "me";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String[] SCOPES_LIST = {GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY};
    private static final List<String> SCOPES = new ArrayList<>(Arrays.asList(SCOPES_LIST));
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Gmail service;

    public GmailService() {
        final NetHttpTransport HTTP_TRANSPORT;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<SquareEmail> getEmails(LocalDate startDate, LocalDate endDate, long max_results){
        Gmail.Users.Messages.List request;
        List<Message> messageList = new LinkedList<>();
        String epochStartDateInSeconds = Long.toString(java.sql.Date.valueOf(startDate).getTime() / 1000);
        String epochEndDateInSeconds = Long.toString(java.sql.Date.valueOf(endDate).getTime() / 1000);
        try {
            do {
                request = service.users().messages().list(USER)
                        .setQ("from:squarespace subject:Form after:" + epochStartDateInSeconds + " before:" + epochEndDateInSeconds)
                        .setMaxResults(max_results);

                ListMessagesResponse response = request.execute();
                try {
                    messageList.addAll(response.getMessages());
                } catch (NullPointerException e){
                    return new ArrayList<>();
                }
                request.setPageToken(response.getNextPageToken());
            } while (request.getPageToken() != null && request.getPageToken().length() > 0 && max_results > 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String startDateString = startDate.format(formatter);
        String endDateString = endDate.format(formatter);

        List<String> squareEmailIdList = SquareEmail.getAllIdWithinDate(startDateString, endDateString);
        List<SquareEmail> squareEmailList = new LinkedList<>();
        for(Message message : messageList){
            if(!squareEmailIdList.contains(message.getId())) {
                SquareEmail tempSquareEmail = new SquareEmail();
                tempSquareEmail.setId(message.getId());
                    squareEmailList.add(tempSquareEmail);
            }
        }
        return getFullMessages(squareEmailList);
    }

    public List<SquareEmail> getFullMessages(List<SquareEmail> squareEmails){
        try {
            for (SquareEmail squareEmail : squareEmails) {
                if (squareEmail != null) {
                    Message tmp = service.users().messages().get(USER, squareEmail.getId())
                            .setFormat("FULL")
                            .execute();
                    MessagePart msgPart = tmp.getPayload();
                    String body = getContent(msgPart);
                    body = body.replaceAll("[\\n\\r]", " ").split("\\(Sent via Fancy Picnics <https://www.fancypicnicshouston.com> \\)")[0];
                    String[] bodyArray = body.split("Name: | Email: | Phone number: | How did you hear about us: | Picnic Date: | Time of picnic: | Estimated guest count: | Location of your picnic : | Address: | Type of event: | Picnic style: | Add on preferences: ");
                    List<String> bodyList = new ArrayList<>(Arrays.asList(bodyArray));
                    List<String> trimmedList = new ArrayList<>();
                    for (String item : bodyList) {
                        if (!item.isEmpty())
                            trimmedList.add(item.trim());
                    }

                    squareEmail.setEmailDate(new Date(tmp.getInternalDate()));
                    squareEmail.setEventName(trimmedList.get(0));
                    squareEmail.setEventEmail(trimmedList.get(1));
                    squareEmail.setEventPhoneNumber(trimmedList.get(2));
                    squareEmail.setEventSource(trimmedList.get(3));
                    squareEmail.setEventDate(trimmedList.get(4));
                    squareEmail.setEventTime(trimmedList.get(5));
                    squareEmail.setEventGuestCount(trimmedList.get(6));
                    squareEmail.setEventLocationArray((trimmedList.get(7)).split(","));
                    squareEmail.setEventAddress(trimmedList.get(8));
                    squareEmail.setEventTypeArray((trimmedList.get(9)).split(","));
                    squareEmail.setEventStyleArray((trimmedList.get(10)).split(","));
                    squareEmail.setEventAddonsArray((trimmedList.get(11)).split(","));

                    String[] eventLocationArray = squareEmail.getEventLocationArray();
                    for (int i = 0; i < eventLocationArray.length; i++) {
                        if (eventLocationArray[i].contains("Home delivery"))
                            eventLocationArray[i] = "Home delivery";
                    }
                    squareEmail.setEventLocationArray(eventLocationArray);

                    String[] eventStyleArray = squareEmail.getEventStyleArray();
                    for (int i = 0; i < eventStyleArray.length; i++) {
                        if (eventStyleArray[i].contains("Custom color pallette"))
                            eventStyleArray[i] = "Custom color pallette";
                    }
                    squareEmail.setEventStyleArray(eventStyleArray);

                    String[] eventAddonsArray = squareEmail.getEventAddonsArray();
                    for (int i = 0; i < eventAddonsArray.length; i++) {
                        if (eventAddonsArray[i].contains("Cinema experience"))
                            eventAddonsArray[i] = "Cinema experience";
                    }
                    squareEmail.setEventAddonsArray(eventAddonsArray);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return squareEmails;
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GmailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));


        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private String getContent(MessagePart msgPart) {
        StringBuilder stringBuilder = new StringBuilder();
        getPlainTextFromMessageParts(msgPart, stringBuilder);
        byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private void getPlainTextFromMessageParts(MessagePart msgPart, StringBuilder stringBuilder) {
        switch (msgPart.getMimeType()) {
            case "text/plain":
            case "text/html":
                stringBuilder.append(msgPart.getBody().getData());
                break;
            case "multipart/alternative":
            case "multipart/mixed":
                List<MessagePart> newParts = msgPart.getParts();
                for (MessagePart newPart : newParts) {
                    getPlainTextFromMessageParts(newPart, stringBuilder);
                }
                break;
        }
    }
}
