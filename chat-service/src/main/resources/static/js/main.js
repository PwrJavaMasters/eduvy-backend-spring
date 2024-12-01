'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let nickname = null;
let email = null;
let selectedUserId = null;
let numberOfMessages = 0;

function connect(event) {
    nickname = document.querySelector('#nickname').value.trim();
    email = document.querySelector('#email').value.trim();

    if (nickname && email) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        const headers = {
            Authorization: `Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkxZTDhRbXdRbkN5TFp0eXdsemJIRCJ9.eyJ1c2VyX3JvbGVzIjpbInN0dWRlbnQiLCJ0dXRvciJdLCJuaWNrbmFtZSI6IndvanRlay5rb3J5cyIsIm5hbWUiOiJ3b2p0ZWsua29yeXNAd3AucGwiLCJwaWN0dXJlIjoiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvNGE0Njc1OGU1ZTU5YjMzODE2ZTU1MTc1MzYzZDI3ZGI_cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZ3by5wbmciLCJ1cGRhdGVkX2F0IjoiMjAyNC0xMi0wMVQxMzozMzo0MC4yNTRaIiwiZW1haWwiOiJ3b2p0ZWsua29yeXNAd3AucGwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImlzcyI6Imh0dHBzOi8vZGV2LWdrbmlkazJxMWZvZTR0eGUudXMuYXV0aDAuY29tLyIsImF1ZCI6IkJoNnZ2U2JRbFd2T0d6aUE2Y3VLcXBaZFdJc1UzTEJ0IiwiaWF0IjoxNzMzMDYwMDIxLCJleHAiOjE3MzMwOTYwMjEsInN1YiI6ImF1dGgwfDY3MzI2NTFmZDMxNzNiODliY2JkZGU2YSIsInNpZCI6ImlKdEM5NGlkaHhmOWtCMFU2WVozRlVGWDFXOEk0YnFIIiwibm9uY2UiOiJiVVp2ZUY5eFdUaCtjME15TkhWbmJuVklUV1JSUlRaUlUyRXpiRmt3YjNSbVVGWk9lakpMYmxVMFRRPT0ifQ.Wuu_4wllA_WsOctlBRGsdyGji2v5rXkofr9fYaQwqbvzppGMd5ZwgcUtOe-r-aHgPh_zXPFdEuDn54-8s0UxEfABZcCr1qZ08uTZ60tqpsKTqTQjuxdTDpr_IQl0y1hZXWeaV8b48FeELkC82fGfVImkE3391kf7-tNBaRjkGCTDHWIraAgjMysCubiV4nNx2586ld_oz6FBDZVzLQrraNgbsNEc4mjvqy0XfaGLde6HHzqcbKzdX1VdGygdllqJgI_21brD9SeGnQeIGKuhIpH3FvIOY2dWGfEvMw1ynTweddcD2V1_1ngu-oKOBQ4Z_lt0bEHfr9RsJYkIKNRZGw`
        };

        stompClient.connect(headers, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    // register the connected user
    stompClient.send("/app/user.addUser",
        {},
        JSON.stringify({nickName: nickname, email: email, status: 'ONLINE'})
    );
    document.querySelector('#connected-user-email').textContent = email;
    findAndDisplayConnectedUsers().then();
}

async function findAndDisplayConnectedUsers() {
    const connectedUsersResponse = await fetch('/getUsers');
    console.log(connectedUsersResponse);
    let connectedUsers = await connectedUsersResponse.json();
    connectedUsers = connectedUsers.filter(user => user.nickName !== nickname);
    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML = '';

    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUsersList);
        if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });
}

setInterval(findAndDisplayConnectedUsers, 1000);

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.nickName;

    const userImage = document.createElement('img');
    userImage.src = '../image/user_icon.png';
    userImage.alt = user.email;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.email;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '';
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    listItem.addEventListener('click', userItemClick);

    connectedUsersList.appendChild(listItem);
}

function userItemClick(event) {

    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    if (nbrMsg) {
        nbrMsg.textContent = '';
        nbrMsg.classList.add('hidden');
    }
    numberOfMessages = 0;
}

function displayMessage(senderId, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === nickname) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/chat/${nickname}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.message);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
         const chatMessage = {
            senderId: nickname,
            recipientId: selectedUserId,
            message: messageInput.value.trim(),
            timeStamp: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(nickname, messageInput.value.trim());
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}


async function onMessageReceived(payload) {
    numberOfMessages+=1;
    console.log("numberOfMessages ",  numberOfMessages);
    await findAndDisplayConnectedUsers();
    console.log('Message received', payload);
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.message);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    // Update the notification for the user who sent the message
    const notifiedUser = document.querySelector(`#${message.senderId}`);
    if (notifiedUser) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        if (nbrMsg) {
            nbrMsg.textContent = numberOfMessages; // Show the number of unread messages
            nbrMsg.classList.remove('hidden'); // Ensure the notification is visible
        }
    }

    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }
}

function onLogout() {
    stompClient.send("/app/user.disconnectUser",
        {},
        JSON.stringify({nickName: nickname, email: email, status: 'OFFLINE'})
    );
    window.location.reload();
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();