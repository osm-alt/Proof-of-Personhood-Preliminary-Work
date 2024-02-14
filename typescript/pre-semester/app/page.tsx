"use client"

import { useState } from "react";

var client:any;


export default function Home() {

  const [number, setNumber] = useState(0);
  const [received, setReceived] = useState(0);

  function connect() {
    var W3CWebSocket = require('websocket').w3cwebsocket;

    client = new W3CWebSocket('ws://localhost:11337/');
    
    client.onerror = function() {
        console.log('Connection Error');
    };
    
    client.onopen = function() {
        console.log('WebSocket Client Connected');
    };
    
    client.onclose = function() {
        console.log('Client Closed');
    };
    
    client.onmessage = function(e:any) {
        if (typeof e.data === 'string') {
            console.log("Received: '" + e.data + "'");
            setReceived(e.data);
        }
    };
  }

  function sendNumber(number:number) {
    if (client) {
        client.send(number.toString());
        console.log("Sent: " + number);
    }
    else {
      console.log("Error sending number");
    }
  }
  
  
  return (
      <div>
        <h1>Websocket Typescript/React Client</h1>
        <button onClick={() => connect()}>Connect to websocket server</button>
        <br></br><br></br>
        <input type="number" value={number}
      onChange={e => setNumber(parseInt(e.target.value))} ></input>
        <button onClick={() => sendNumber(number)}>Send</button>
        <h2>Received answer: {received}</h2>
      </div>
    
      );
}
