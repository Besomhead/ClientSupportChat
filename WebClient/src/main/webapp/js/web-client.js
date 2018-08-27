'use strict';

var MESSAGES_LIST_ID = "web-client-messages-area";
var MESSAGE_INPUT_ID = "web-client-message-input";
var SEND_MESSAGE_BUTTON = "web-client-send-message";
var REGISTER_KEY = "/register client ";

var APP_PATH = "ws://localhost:8080/web-client/chat";

var webSocket;
var userName = "Wenya";

function openConnection() {
    webSocket.send(REGISTER_KEY + userName);
    console.log("Connection established");
}

function closeConnection(event) {
    if (event.wasClean) {
        console.log("Connection closed");
    } else {
        console.log("Connection interrupted");
    }
    console.log("Code: " + event.code + " caused by: " + event.reason);
}

function showMessage(message) {
    document.getElementById(MESSAGES_LIST_ID).appendChild(document.createTextNode(message + "\n"));
}

function receiveMsg(event) {
    if (event.data === "") {
        return;
    }
    showMessage("Agent: " + event.data);
}

function handleError(error) {
    console.log("Error " + error.message);
}

function sendMessage() {
    var inputBox = document.getElementById(MESSAGE_INPUT_ID);
    showMessage(userName + ": " + inputBox.value);
    webSocket.send(inputBox.value);
    inputBox.value = "";
    inputBox.focus();
}

function initWS() {
    webSocket = new WebSocket(APP_PATH);
    webSocket.addEventListener("open", openConnection);
    webSocket.addEventListener("close", closeConnection);
    webSocket.addEventListener("message", receiveMsg);
    webSocket.addEventListener("error", handleError);
}

function askName() {
    userName = prompt("Please introduce yourself", "Wenya") || userName;
    initWS();
}

window.addEventListener("load", function initPage() {
    document.getElementById(SEND_MESSAGE_BUTTON).addEventListener("click", sendMessage);
    askName();
});