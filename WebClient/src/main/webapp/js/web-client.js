'use strict';

var CHAT_CONTAINER_ID = "web-client-chat-container";
var MESSAGES_LIST_ID = "web-client-messages-area";
var MESSAGE_INPUT_ID = "web-client-message-input";
var SEND_MESSAGE_BUTTON_ID = "web-client-send-message";
var PROMPT_ID = "web-client-prompt";
var USERNAME_INPUT_ID = "web-client-username-input";
var USER_ROLE_SELECTOR = "web-client-role-selector";
var PROMPT_CONFIRM_BUTTON_ID = "web-client-prompt-submit";
var DISPLAY_HIDDEN_CLASS = "web-client-chat-container-hidden";
var DISPLAY_FLEX_CLASS = "web-client-chat-container-visible";
var REGISTER_KEY = "/register ";
var EXIT_KEY = "/exit";
var LEAVE_KEY = "/leave";

var APP_PATH = "ws://localhost:8080/web-client/chat";

var webSocket;
var userName = "Wenya";
var userRole = "client";

function openConnection() {
    webSocket.send(REGISTER_KEY + userRole + " " + userName);
    console.log("Connection established");
}

function disableInput() {
    document.getElementById(MESSAGE_INPUT_ID).disabled = true;
    document.getElementById(SEND_MESSAGE_BUTTON_ID).disabled = true;
}

function closeConnection(event) {
    if (event.wasClean) {
        console.log("Connection closed");
    } else {
        console.log("Connection interrupted");
    }
    console.log("Code: " + event.code + " caused by: " + event.reason);
    disableInput();
    webSocket.send(EXIT_KEY);
}

function showMessage(message) {
    document.getElementById(MESSAGES_LIST_ID).appendChild(document.createTextNode(message + "\n"));
}

function receiveMessage(event) {
    if (event.data === "") {
        return;
    }
    showMessage("-> " + event.data);
}

function handleError(error) {
    console.log("Error " + error.message);
    showMessage("Sorry, something went wrong... Please reload the page");
    disableInput();
    webSocket.send(EXIT_KEY);
}

function checkIfKeyMessage(message) {
    var systemMessage;

    if (!message.includes("/")) {
        return;
    }

    switch (message) {
        case LEAVE_KEY:
            systemMessage = "You've leaved the conversation";
            break;
        case EXIT_KEY:
            systemMessage = "You've finished conversation";
            disableInput();
            break;
        default:
            systemMessage = "Unsupported chat command";
    }
    showMessage(systemMessage);
}

function sendMessage() {
    var inputBox = document.getElementById(MESSAGE_INPUT_ID);
    if (inputBox.value !== "") {
        showMessage(userName + ": " + inputBox.value);
        checkIfKeyMessage(inputBox.value);
    }

    webSocket.send(inputBox.value);
    inputBox.value = "";
    inputBox.focus();
}

function initWS() {
    webSocket = new WebSocket(APP_PATH);
    webSocket.addEventListener("open", openConnection);
    webSocket.addEventListener("close", closeConnection);
    webSocket.addEventListener("message", receiveMessage);
    webSocket.addEventListener("error", handleError);
}

function saveUserInfo() {
    userName = document.getElementById(USERNAME_INPUT_ID).value || userName;
    userRole = Array.from(document.getElementById(USER_ROLE_SELECTOR).getElementsByTagName("option")).find(
        function findSelectedRole(option) {
            return option.selected;
        }
    ).innerHTML || userRole;
    document.body.removeChild(document.getElementById(PROMPT_ID));
    document.getElementById(CHAT_CONTAINER_ID).classList.remove(DISPLAY_HIDDEN_CLASS);
    document.getElementById(CHAT_CONTAINER_ID).classList.add(DISPLAY_FLEX_CLASS);
    initWS();
}

window.addEventListener("load", function initPage() {
    document.getElementById(SEND_MESSAGE_BUTTON_ID).addEventListener("click", sendMessage);
    document.getElementById(PROMPT_CONFIRM_BUTTON_ID).addEventListener("click", saveUserInfo);
});